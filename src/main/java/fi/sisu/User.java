package fi.sisu;

import java.util.ArrayList;
import java.util.List;

/**
 * User class represents a user of the system, with a name and a student number.
 * It also contains information about the degree module user is enrolled in and
 * the courses user have completed.
 */
public class User {

    private String name;
    private String studentNumber;
    private String degreeModule;
    private List<CourseUnit> courses = new ArrayList<>();

    /**
     * Constructs a new User object with the given name and student number.
     *
     * @param name The name of the user.
     * @param studentNumber The student number of the user.
     */
    public User(String name, String studentNumber) {
        this.name = name;
        this.studentNumber = studentNumber;
    }

    /**
     * Constructs a new empty User object.
     */
    public User() {

    }

    /**
     * Returns the name of the user.
     *
     * @return The name of the user.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the student number of the user.
     *
     * @return The student number of the user.
     */
    public String getStudentNumber() {
        return this.studentNumber;
    }

    /**
     * Sets the name of the user to the given name.
     *
     * @param name The new name of the user.
     */
    public void setName(String name) {
        this.name = name;

    }

    /**
     * Sets the student number of the user to the given student number.
     *
     * @param studentNumber The new student number of the user.
     */
    public void setStudentNumber(String studentNumber) {
        this.name = studentNumber;

    }

    /**
     * Returns the degree module of the user.
     *
     * @return The degree module of the user.
     */
    public String getDegreeModule() {
        return this.degreeModule;
    }

    /**
     * Sets the degree module of the user to the given degree module.
     *
     * @param degreeModule The new degree module of the user.
     */
    public void setDegreeModule(String degreeModule) {
        this.degreeModule = degreeModule;
    }

    /**
     * Returns a map of the courses the user has completed, with the course IDs
     * as keys and a boolean indicating completion status as values.
     *
     * @return A map of the courses the user has completed.
     */
    public List<CourseUnit> getCourses() {
        return this.courses;

    }

    /**
     * Sets the courses the user has chose to the given map.
     *
     * @param courses A map of the courses the user has chosen.
     */
    public void setCourses(List<CourseUnit> courses) {
        this.courses = courses;
    }

    /**
     * Adds a course to the list of chosen courses.
     */
    public void addCourse(CourseUnit course) {
        this.courses.add(course);

    }

    /**
     * Returns a string representation of the user object, including the user's
     * name, student number, degree module, and courses.
     *
     * @return a string representation of the user object.
     */
    public String toString() {
        return "Name: " + name + ", Student number: " + studentNumber + " " + degreeModule + " " + courses;
    }

}
