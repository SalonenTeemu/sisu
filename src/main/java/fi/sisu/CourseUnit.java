package fi.sisu;

/**
 * Class for modeling a Course. Extends the DegreeModule class.
 */
public class CourseUnit extends DegreeModule {
    public boolean completed = false;

    /**
     *
     * @param name name of the course.
     * @param id id of the course.
     * @param groupId groupId of the course.
     * @param minCredits credits to pass the course.
     * @param code the code of the course.
     * @param description the description of the course.
     * @param outcomes the learning outcomes of the course.
     */
    public CourseUnit(String name, String id, String groupId, int minCredits, String code, String description, String outcomes) {
        super(name, id, groupId, minCredits, code, description, outcomes);
    }

    /**
     * Returns whether the user has completed the course.
     *
     * @return true is the course has been completed by the user, otherwise false.
     */
    public boolean isCompleted() {
      return this.completed;
    }

    /**
     * Sets the completed value of the course.
     *
     * @param selected the boolean value to set the completed value to.
     */
    public void setCompleted(boolean selected) {
        this.completed = selected;
    }
}
