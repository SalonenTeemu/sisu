package fi.sisu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for modeling a degree programme. Extends the DegreeModule class.
 */
public class DegreeProgramme extends DegreeModule {
    private HashMap<String, StudyModule> studyModules = new HashMap<>();

    /**
     *
     * @param name name of the degree programme.
     * @param id id of the degree programme.
     * @param groupId groupId of the degree programme.
     * @param minCredits credits to pass the degree programme.
     * @param code coe of the degree programme.
     * @param description description of the degree programme.
     * @param outcomes learning outcomes of the degree programme.
     */
    public DegreeProgramme(String name, String id, String groupId, int minCredits, String code, String description, String outcomes) {
        super(name, id, groupId, minCredits, code, description, outcomes);
    }

    /**
     * Adds a studyModule to the studyModules map.
     *
     * @param studyModule the studyModule to be added.
     */
    public void addStudyModule(StudyModule studyModule) {
        studyModules.put(studyModule.getId(), studyModule);
    }

    /**
     * Returns all study modules as a list, sorted by the names.
     *
     * @return list of all study modules.
     */
    public List<StudyModule> getStudyModulesAsList() {
        ArrayList<StudyModule> list = new ArrayList<>(studyModules.values());
        list.sort(DegreeModule::compareTo);
        return list;
    }

    /**
     * Returns a specific study module from the map searched by its id key.
     *
     * @param id the id of the study module used as the key.
     * @return the study module, which has the given id.
     */
    public StudyModule getStudyModuleById(String id) {
        return studyModules.get(id);
    }
}
