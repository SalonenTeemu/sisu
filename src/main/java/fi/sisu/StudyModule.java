package fi.sisu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class for modeling a study module. Extends the DegreeModule class.
 */
public class StudyModule extends DegreeModule {

    private HashMap<String, CourseUnit> courseUnits = new HashMap<>();
    private HashMap<String, StudyModule> childStudyModules = new HashMap<>();

    /**
     *
     * @param name name of the study module.
     * @param id id of the study module.
     * @param groupId groupId of the study module.
     * @param minCredits credits to pass the study module.
     * @param code code of the study module.
     * @param description description of the study module.
     * @param outcomes learning outcomes of the study module.
     */
    public StudyModule(String name, String id, String groupId, int minCredits, String code, String description, String outcomes) {
        super(name, id, groupId, minCredits, code, description, outcomes);
    }

    /**
     * Adds a course to the courseUnits map.
     *
     * @param courseUnit the course to be added.
     */
    public void addCourseUnit(CourseUnit courseUnit) {
        courseUnits.put(courseUnit.getId(), courseUnit);
    }

    /**
     * Returns all courses as a list, sorted by the names.
     *
     * @return list of all courses.
     */
    public List<CourseUnit> getCourseUnitsAsList() {
        ArrayList<CourseUnit> list = new ArrayList<>(courseUnits.values());
        list.sort(DegreeModule::compareTo);
        return list;
    }

    /**
     * Adds a child study module to the childStudyModules map.
     *
     * @param childModule the child study module to be added.
     */
    public void addChildStudyModule(StudyModule childModule) {
        childStudyModules.put(childModule.getId(), childModule);
    }

    /**
     * Returns all child study modules as a list, sorted by the names.
     *
     * @return list of all child study modules.
     */
    public List<StudyModule> getChildStudyModulesAsList() {
        ArrayList<StudyModule> list = new ArrayList<>(childStudyModules.values());
        list.sort(DegreeModule::compareTo);
        return list;
    }

    /**
     * Returns a specific course from the map searched by its id key.
     *
     * @param id the id of the course used as the key.
     * @return the course, which has the given id.
     */
    public CourseUnit getCourseUnitById(String id) {
        return courseUnits.get(id);
    }

    /**
     * Returns all courses of a study module including the courses of the modules children study modules.
     *
     * @return a list of all courses under this study module.
     */
    public List<CourseUnit> getCourseUnitsAndNestedChildrenCourseUnits() {
        List<CourseUnit> allCourseUnits = new ArrayList<>();
        allCourseUnits.addAll(courseUnits.values());
        childStudyModules.values().forEach((child) -> {
            allCourseUnits.addAll(child.getCourseUnitsAndNestedChildrenCourseUnits());
        });
        allCourseUnits.sort(DegreeModule::compareTo);
        return allCourseUnits;
    }
}
