package fi.sisu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UserTest class for unit tests for the User class.
 */
public class UserTest {

    /**
     * Test the getName() method of the User class.
     */
    @Test
    public void testGetName() {
        User user = new User("John Smith", "123456");
        assertEquals("John Smith", user.getName());
    }

    /**
     * Test the getStudentNumber() method of the User class.
     */
    @Test
    public void testGetStudentNumber() {
        User user = new User("John Smith", "123456");
        assertEquals("123456", user.getStudentNumber());
    }

    /**
     * Test the getSetName() method of the User class.
     */
    @Test
    public void testSetName() {
        User user = new User("John Smith", "123456");
        user.setName("Jane Doe");
        assertEquals("Jane Doe", user.getName());
    }

    /**
     * Test the setStudentNumber() method of the User class.
     */
    @Test
    public void testSetStudentNumber() {
        User user = new User("John Smith", "654321");
        user.setStudentNumber("654321");
        assertEquals("654321", user.getStudentNumber());
    }

    /**
     * Test the getDegreeModule() method of the User class.
     */
    @Test
    public void testGetDegreeModule() {
        User user = new User("John Smith", "123456");
        user.setDegreeModule("Computer Science");
        assertEquals("Computer Science", user.getDegreeModule());
    }

    /**
     * Test the setDegreeModule() method of the User class.
     */
    @Test
    public void testSetDegreeModule() {
        User user = new User("John Smith", "123456");
        user.setDegreeModule("Computer Science");
        assertEquals("Computer Science", user.getDegreeModule());
    }

    /**
     * Test the addCourse() method of the User class.
     */
    @Test
    public void testAddCourse() {
        User user = new User("John Smith", "123456");
        CourseUnit course1 = new CourseUnit("Math", "MATH101", "Group A", 5, "MATH", "Calculus", "Calculus topics");
        user.addCourse(course1);
        assertTrue(user.getCourses().contains(course1));
    }

    /**
     * Test the toString() method of the User class.
     */
    @Test
    public void testToString() {
        User user = new User("John Doe", "123456");
        user.setDegreeModule("Computer Science");

        CourseUnit course1 = new CourseUnit("Math", "MATH101", "Group A", 5, "MATH", "Calculus", "Calculus topics");
        CourseUnit course2 = new CourseUnit("Physics", "PHYS101", "Group B", 5, "PHYS", "Mechanics", "Mechanics topics");

        user.addCourse(course1);
        user.addCourse(course2);

        String expected = "Name: John Doe, Student number: 123456 Computer Science [" + course1.toString() + ", " + course2.toString() + "]";
        String actual = user.toString();

        assertEquals(expected, actual);
    }

}
