package fi.sisu;

import com.google.gson.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for acquiring and saving the module data. Uses the API class to fetch
 * the data.
 */
public class BackgroundHandler {

    // Strings used for the urls to get data from the Sisu API
    private final String DP_API_URL = "https://sis-tuni.funidata.fi/kori/api/module-search?curriculumPeriodId=uta-lvv-"
            + "2021&universityId=tuni-university-root-id&moduleType=DegreeProgramme&limit=1000";
    private final String MODULE_API_URL_START = "https://sis-tuni.funidata.fi/kori/api/modules/by-group-id?groupId=";
    private final String MODULE_API_URL_END = "&universityId=tuni-university-root-id";
    private final String COURSE_API_URL_START = "https://sis-tuni.funidata.fi/kori/api/course-units/by-group-id?groupId=";
    private final String COURSE_API_URL_END = "&universityId=tuni-university-root-id";

    private HashMap<String, DegreeProgramme> degreeProgrammes = new HashMap<>();

    /**
     * On construction, set all degree programmes from the API to the
     * degreeProgrammes map using the setDegreeProgrammes method.
     */
    public BackgroundHandler() {
        // Try and get moduleRuleGroupsResponse from the Sisu API and if successful,
        // set all degree programmes to the map
        try {
            JsonElement jsonElement = API.getJsonFromApi(DP_API_URL);
            if (jsonElement != null && !jsonElement.isJsonNull() && jsonElement.isJsonObject()) {
                JsonObject degreeProgrammesResponse = jsonElement.getAsJsonObject();
                setDegreeProgrammes(degreeProgrammesResponse);
            } else {
                throw new IOException("API returned null object");
            }
        } catch (IOException e) {
            System.err.println("Error getting data from API");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all degree programmes and saves them to the degreeProgrammes map.
     *
     * @param jsonObject the moduleRuleGroupsResponse from the API containing
     * data of all degree programmes.
     * @throws IOException if the programmes can not be searched or the array is
     * null.
     */
    private void setDegreeProgrammes(JsonObject jsonObject) throws Exception {
        Gson gson = new Gson();
        JsonArray jsonArray = jsonObject.get("searchResults").getAsJsonArray();
        // Check that the array is not null
        if (jsonArray != null && !jsonArray.isJsonNull()) {
            for (JsonElement jsonElement : jsonArray) {
                // Check that the element is not null and if it is not, get its data
                // with addCoreDataToModule and add a new degree programme
                if (!jsonElement.isJsonNull()) {
                    JsonObject jsonDegreeProgramme = gson.fromJson(jsonElement, JsonObject.class);
                    DegreeProgramme newDP = new DegreeProgramme("", "", "", 0, "", "NULL", "NULL");
                    addCoreDataToModule(jsonDegreeProgramme, newDP);
                    degreeProgrammes.put(newDP.getId(), newDP);
                }
            }
        } else {
            throw new IOException("Could not get degree programmes as an array");
        }
    }

    /**
     * Searches a specific degree programme from the API and saves its data with
     * the help of method searchStudyModulesRecursively.
     *
     * @param degreeProgramme the degree programme to search data about.
     */
    public void getDataOfDegreeProgramme(DegreeProgramme degreeProgramme) {
        try {
            // Get the moduleRuleGroupsResponse from the API
            JsonElement jsonElement = API.getJsonFromApi(MODULE_API_URL_START + degreeProgramme.getGroupId() + MODULE_API_URL_END);
            JsonObject degreeProgrammeData = jsonElement.getAsJsonArray().get(0).getAsJsonObject();
            addAdditionalDataToModule(degreeProgrammeData, degreeProgramme);

            JsonObject rule = degreeProgrammeData.get("rule").getAsJsonObject();
            while (!rule.has("rules")) {
                rule = rule.get("rule").getAsJsonObject();
            }
            JsonArray rules = rule.get("rules").getAsJsonArray();
            searchStudyModulesRecursively(rules, degreeProgramme);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds all modules found from the degree programme to its map with the help
     * of method addStudyModulesRecursively.
     *
     * @param rules the JsonArray of the modules to be added.
     * @param degreeProgramme the degree programme to add the modules to.
     */
    private void searchStudyModulesRecursively(JsonArray rules, DegreeProgramme degreeProgramme) {
        // Categorize rules by type so they can be queried as a batch call from the API
        ArrayList<JsonObject> moduleRules = new ArrayList<>();
        ArrayList<JsonObject> compositeRules = new ArrayList<>();
        rules.forEach(rule -> {
            JsonObject ruleObject = rule.getAsJsonObject();
            String ruleType = ruleObject.get("type").getAsString();
            if (ruleType.equals(("ModuleRule"))) {
                moduleRules.add(ruleObject);
            }
            if (ruleType.equals("CompositeRule")) {
                compositeRules.add(ruleObject);
            }
        });

        // Fetch data of modules as a single API request using multiple group ids as a query parameter
        if (!moduleRules.isEmpty()) {
            String moduleRuleGroups = moduleRules
                    .stream()
                    .map(mr -> mr.get("moduleGroupId").getAsString())
                    .collect(Collectors.joining(","));
            JsonArray moduleRuleGroupsResponse = API.getJsonFromApi(MODULE_API_URL_START
                    + moduleRuleGroups + MODULE_API_URL_END).getAsJsonArray();

            moduleRuleGroupsResponse.forEach(groupData -> {
                JsonObject groupDataObject = groupData.getAsJsonObject();
                addStudyModulesRecursively(groupDataObject, degreeProgramme, null);
            });
        }

        // Recursively handle rules inside composite rules
        for (JsonObject compositeRule : compositeRules) {
            JsonArray newRules = compositeRule.get("rules").getAsJsonArray();
            searchStudyModulesRecursively(newRules, degreeProgramme);
        }
    }

    /**
     * Creates studyModules. Adds all modules of the degree programme to its
     * studyModules map. Additionally, adds courses and childModules to these
     * study modules recursively. Uses the helper method searchRules.
     *
     * @param groupData the api response for the StudyModule we want to add.
     * @param degreeProgramme the degree programme to add the study module to.
     * @param previousModule reference to the previous studyModule. If the
     * current module is a childModule of a studyModule, it will be added to the
     * previous study module's childStudyModules map instead of the degree
     * programme's studyModules map.
     */
    private void addStudyModulesRecursively(JsonObject groupData, DegreeProgramme degreeProgramme, StudyModule previousModule) {
        try {
            // Check that the moduleRuleGroupsResponse is not null and then get data about the module
            // with addCoreDataToModule and addAdditionalDataToModule
            if (groupData != null && !groupData.isJsonNull()) {
                StudyModule newStudyModule = new StudyModule("", "", "", 0, "", "", "");
                addCoreDataToModule(groupData, newStudyModule);
                addAdditionalDataToModule(groupData, newStudyModule);

                JsonObject rule = groupData.get("rule").getAsJsonObject();
                while (!rule.has("rules")) {
                    rule = rule.get("rule").getAsJsonObject();
                }

                JsonArray moduleRules = rule.get("rules").getAsJsonArray();
                // Search for courses, modules and more rules with searchRules
                searchRules(degreeProgramme, newStudyModule, moduleRules);

                // If this is the first level, add module to the degree programme, else add it as a childModule of the previousModule
                if (previousModule == null) {
                    degreeProgramme.addStudyModule(newStudyModule);
                } else {
                    previousModule.addChildStudyModule(newStudyModule);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting data of study module.");
            e.printStackTrace();
        }
    }

    /**
     * Helper method, which searches for modules and courses from the rules
     * array and calls other methods according to what is found.
     *
     * @param degreeProgramme the degree programme, whose data is searched.
     * @param studyModule the current studyModule being searched.
     * @param rules JsonArray to search modules from.
     */
    private void searchRules(DegreeProgramme degreeProgramme, StudyModule studyModule, JsonArray rules) {
        // Categorize rules by type so they can be queried as a batch call from the API
        ArrayList<JsonObject> moduleRules = new ArrayList<>();
        ArrayList<JsonObject> compositeRules = new ArrayList<>();
        ArrayList<JsonObject> courseUnitRules = new ArrayList<>();
        rules.forEach(rule -> {
            JsonObject ruleObject = rule.getAsJsonObject();
            String ruleType = ruleObject.get("type").getAsString();
            if (ruleType.equals(("ModuleRule"))) {
                moduleRules.add(ruleObject);
            }
            if (ruleType.equals("CompositeRule")) {
                compositeRules.add(ruleObject);
            }
            if (ruleType.equals("CourseUnitRule")) {
                courseUnitRules.add(ruleObject);
            }
        });

        // Fetch data of modules as a single API request using multiple group ids as a query parameter
        if (!moduleRules.isEmpty()) {
            String moduleRuleGroups = moduleRules
                    .stream()
                    .map(mr -> mr.get("moduleGroupId").getAsString())
                    .collect(Collectors.joining(","));
            JsonArray moduleRuleGroupsResponse = API.getJsonFromApi(MODULE_API_URL_START
                    + moduleRuleGroups + MODULE_API_URL_END).getAsJsonArray();
            moduleRuleGroupsResponse.forEach(groupData -> {
                JsonObject groupDataObject = groupData.getAsJsonObject();
                addStudyModulesRecursively(groupDataObject, degreeProgramme, studyModule);
            });

        }
        // Fetch data of course units as a single API request using multiple group ids as a query parameter
        if (!courseUnitRules.isEmpty()) {
            String courseUnitRuleGroups = courseUnitRules
                    .stream()
                    .map(cur -> cur.get("courseUnitGroupId").getAsString())
                    .collect(Collectors.joining(","));
            JsonArray courseUnitRuleGroupsResponse = API.getJsonFromApi(COURSE_API_URL_START
                    + courseUnitRuleGroups + COURSE_API_URL_END).getAsJsonArray();
            courseUnitRuleGroupsResponse.forEach(courseUnitData -> {
                JsonObject courseUnitDataObject = courseUnitData.getAsJsonObject();
                addCourseUnit(courseUnitDataObject, studyModule);
            });
        }

        // Recursively handle rules inside composite rules
        for (JsonObject compositeRule : compositeRules) {
            JsonArray newRules = compositeRule.get("rules").getAsJsonArray();
            searchRules(degreeProgramme, studyModule, newRules);
        }
    }

    /**
     * Creates and adds a CourseUnit to the provided module with the help of
     * methods addCoreDataToModule and addAdditionalDataToModule.
     *
     * @param courseUnitDataObject the API response of the data for this course.
     * @param studyModule the studyModule to add to course to.
     */
    private void addCourseUnit(JsonObject courseUnitDataObject, StudyModule studyModule) {
        // Check that the courseUnitDataObject is not null and then get data about the course with addCoreDataToModule and
        // addAdditionalDataToModule and finally add the course to the modules courseUnits map
        if (courseUnitDataObject != null && !courseUnitDataObject.isJsonNull()) {
            CourseUnit newCourseUnit = new CourseUnit("", "", "", 0, "", "", "");
            addCoreDataToModule(courseUnitDataObject, newCourseUnit);
            addAdditionalDataToModule(courseUnitDataObject, newCourseUnit);
            studyModule.addCourseUnit(newCourseUnit);
        }
    }

    /**
     * Helper method for getting core data about a degree programme, study
     * module or course unit. Gets the data from the provided jsonObject and
     * adds the data to the provided module using its setter methods.
     *
     * @param jsonObject the moduleRuleGroupsResponse to search the core data
     * from.
     * @param module the degree programme, study module or course unit to add
     * the data to.
     */
    private void addCoreDataToModule(JsonObject jsonObject, DegreeModule module) {
        String id = jsonObject.get("id").getAsString();
        String groupId = jsonObject.get("groupId").getAsString();

        String code = "NULL";
        if (jsonObject.has("code") && !jsonObject.get("code").isJsonNull()) {
            code = jsonObject.get("code").getAsString();
        }

        String name;
        int minCredits = 0;

        // Search the data based on the instance of the provided module
        if (module instanceof DegreeProgramme) {
            name = jsonObject.get("name").getAsString();
            JsonObject jsonCredits = jsonObject.get("credits").getAsJsonObject();
            minCredits = jsonCredits.get("min").getAsInt();
        } else {
            JsonObject jsonName = jsonObject.get("name").getAsJsonObject();
            if (jsonName.has("fi")) {
                name = jsonName.get("fi").getAsString();
            } else {
                name = jsonName.get("en").getAsString();
            }
            if (module instanceof StudyModule) {
                if (jsonObject.has("targetCredits")) {
                    JsonObject jsonCredits = jsonObject.get("targetCredits").getAsJsonObject();
                    minCredits = jsonCredits.get("min").getAsInt();
                }
            } else {
                JsonObject jsonCredits = jsonObject.get("credits").getAsJsonObject();
                minCredits = jsonCredits.get("min").getAsInt();
            }
        }
        // Set the found data to the provided module
        module.setName(name);
        module.setId(id);
        module.setGroupId(groupId);
        module.setCode(code);
        module.setMinCredits(minCredits);
    }

    /**
     * Helper method for getting additional data (description and outcomes)
     * about a degree programme, study module or course unit. Gets the data from
     * the provided jsonObject and adds the data to the provided module using
     * its setter methods.
     *
     * @param jsonObject the moduleRuleGroupsResponse to search the additional
     * data from.
     * @param module the degree programme, study module or course unit to add
     * the data to.
     */
    private void addAdditionalDataToModule(JsonObject jsonObject, DegreeModule module) {
        String outcomes = "NULL";
        String description = "NULL";

        // Search the data based on the instance of the provided module
        if (module instanceof DegreeProgramme) {
            if (jsonObject.has("learningOutcomes") && !jsonObject.get("learningOutcomes").isJsonNull()) {
                JsonObject jsonOutcomes = jsonObject.get("learningOutcomes").getAsJsonObject();
                if (jsonOutcomes.has("fi")) {
                    outcomes = "Oppimistavoitteet: " + jsonOutcomes.get("fi").getAsString();
                } else {
                    outcomes = "Oppimistavoitteet: " + jsonOutcomes.get("en").getAsString();
                }
            }
            if (jsonObject.has("contentDescription") && !jsonObject.get("contentDescription").isJsonNull()) {
                JsonObject jsonDescription = jsonObject.get("contentDescription").getAsJsonObject();
                if (jsonDescription.has("fi")) {
                    description = "Kuvaus: " + jsonDescription.get("fi").getAsString();
                } else {
                    description = "Kuvaus: " + jsonDescription.get("en").getAsString();
                }
            }
        } else {
            if (jsonObject.has("outcomes") && !jsonObject.get("outcomes").isJsonNull()) {
                JsonObject jsonOutcomes = jsonObject.get("outcomes").getAsJsonObject();
                if (jsonOutcomes.has("fi")) {
                    outcomes = "Oppimistavoitteet: " + jsonOutcomes.get("fi").getAsString();
                } else {
                    outcomes = "Oppimistavoitteet: " + jsonOutcomes.get("en").getAsString();
                }
            }
            if (module instanceof StudyModule) {
                String type = jsonObject.get("type").getAsString();
                if (type.equals("StudyModule")) {
                    if (jsonObject.has("contentDescription") && !jsonObject.get("contentDescription").isJsonNull()) {
                        JsonObject jsonDescription = jsonObject.get("contentDescription").getAsJsonObject();
                        if (jsonDescription.has("fi")) {
                            description = "Kuvaus: " + jsonDescription.get("fi").getAsString();
                        } else {
                            description = "Kuvaus: " + jsonDescription.get("en").getAsString();
                        }
                    }
                } else {
                    if (jsonObject.has("description") && !jsonObject.get("description").isJsonNull()) {
                        JsonObject jsonDescription = jsonObject.get("description").getAsJsonObject();
                        if (jsonDescription.has("fi")) {
                            description = "Kuvaus: " + jsonDescription.get("fi").getAsString();
                        } else {
                            description = "Kuvaus: " + jsonDescription.get("en").getAsString();
                        }
                    }
                }
            } else {
                if (jsonObject.has("content") && !jsonObject.get("content").isJsonNull()) {
                    JsonObject jsonDescription = jsonObject.get("content").getAsJsonObject();
                    if (jsonDescription.has("fi")) {
                        description = "Kuvaus: " + jsonDescription.get("fi").getAsString();
                    } else {
                        description = "Kuvaus: " + jsonDescription.get("en").getAsString();
                    }
                }
            }
        }
        // Set the found data to the provided module
        module.setDescription(description);
        module.setOutcomes(outcomes);
    }

    /**
     * Returns all degree programmes as a list, sorted by the names.
     *
     * @return a sorted list of all degree programmes.
     */
    public List<DegreeProgramme> getDegreeProgrammesAsList() {
        ArrayList<DegreeProgramme> list = new ArrayList<>(degreeProgrammes.values());
        list.sort(DegreeModule::compareTo);
        return list;
    }

    /**
     * Returns a specific degree programme from the degreeProgrammes map
     * searched by its id key.
     *
     * @param id the id of the degree programme used as the key.
     * @return the degree programme, which has the given id.
     */
    public DegreeProgramme getDegreeProgrammeById(String id) {
        return degreeProgrammes.get(id);
    }
}
