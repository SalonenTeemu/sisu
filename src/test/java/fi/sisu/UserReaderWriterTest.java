package fi.sisu;

import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * A JUnit test class for the UserReaderWriter class.
 */
public class UserReaderWriterTest {

    private UserReaderWriter readerWriter;
    private User user;
    private File tempFolder;

    @BeforeEach
    public void setUp() throws IOException {
        user = new User("John Doe", "1234");
        readerWriter = new UserReaderWriter(user);
        tempFolder = File.createTempFile("temp", "dir");
        tempFolder.delete();
        tempFolder.mkdir();
    }

    /**
     * Test of readFromFile method, of class UserReaderWriter.
     */
    @Test
    public void testReadFromFile() throws Exception {
        File file = new File(tempFolder, "testRead.json");
        User testUser = new User("Jane Doe", "5678");
        testUser.setName("Jane Doe");
        testUser.setDegreeModule("Computer Science");
        CourseUnit course1 = new CourseUnit("Math", "MATH101", "Group A", 5, "MATH", "Calculus", "Calculus topics");
        CourseUnit course2 = new CourseUnit("Physics", "PHYS101", "Group B", 5, "PHYS", "Mechanics", "Mechanics topics");
        course1.setCompleted(true);
        testUser.addCourse(course1);
        testUser.addCourse(course2);
        UserReaderWriter readerWriter1 = new UserReaderWriter(testUser);
        assertTrue(readerWriter1.writeToFile(file.getAbsolutePath()));
        assertTrue(readerWriter.readFromFile(file.getAbsolutePath()));
        assertEquals("Jane Doe", testUser.getName());
        assertEquals("5678", testUser.getStudentNumber());
        assertEquals("Computer Science", testUser.getDegreeModule());
        assertEquals(2, testUser.getCourses().size());
    }

    /**
     * Test of writeToFile method, of class UserReaderWriter.
     */
    @Test
    public void testWriteToFile() throws Exception {
        File file = new File(tempFolder, "testWrite.json");
        CourseUnit course1 = new CourseUnit("Math", "MATH101", "Group A", 5, "MATH", "Calculus", "Calculus topics");
        CourseUnit course2 = new CourseUnit("Physics", "PHYS101", "Group B", 5, "PHYS", "Mechanics", "Mechanics topics");
        course1.setCompleted(true);
        user.addCourse(course1);
        user.addCourse(course2);
        assertTrue(readerWriter.writeToFile(file.getAbsolutePath()));
        assertTrue(file.exists());
        assertTrue(file.length() > 0);
    }

    /**
     * Test of getUser method, of class UserReaderWriter.
     */
    @Test
    public void testGetUser() throws IOException, Exception {
        File file = new File(tempFolder, "testGet.json");
        User user1 = new User("Jane Doe", "5678");
        user1.setDegreeModule("Computer Science");
        CourseUnit course1 = new CourseUnit("Math", "MATH101", "Group A", 5, "MATH", "Calculus", "Calculus topics");
        CourseUnit course2 = new CourseUnit("Physics", "PHYS101", "Group B", 5, "PHYS", "Mechanics", "Mechanics topics");
        course1.setCompleted(true);
        user1.addCourse(course1);
        user1.addCourse(course2);
        UserReaderWriter readerWriter1 = new UserReaderWriter(user1);
        assertTrue(readerWriter1.writeToFile(file.getAbsolutePath()));
        User user2 = readerWriter.getUser(file.getAbsolutePath());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getStudentNumber(), user2.getStudentNumber());
        assertEquals(user1.getDegreeModule(), user2.getDegreeModule());
        assertEquals(user1.getCourses().size(), user2.getCourses().size());
    }

    /**
     * Test of getUserData method, of class UserReaderWriter.
     */
    @Test
    public void testGetUserData() throws Exception {
        String studentNumber = "12345";
        User expectedUser = new User("John Doe", studentNumber);
        expectedUser.setDegreeModule("Computer Science");
        CourseUnit course1 = new CourseUnit("Math", "MATH101", "Group A", 5, "MATH", "Calculus", "Calculus topics");
        CourseUnit course2 = new CourseUnit("Physics", "PHYS101", "Group B", 5, "PHYS", "Mechanics", "Mechanics topics");
        course1.setCompleted(true);
        expectedUser.addCourse(course1);
        expectedUser.addCourse(course2);

        // create a file for the user
        File file = new File(tempFolder, "testGetUserData.json");
        UserReaderWriter writer = new UserReaderWriter(expectedUser);
        assertTrue(writer.writeToFile(file.getAbsolutePath()));
        assertTrue(readerWriter.readFromFile(file.getAbsolutePath()));

        // call the method and compare with expected user
        UserReaderWriter readerWriter = new UserReaderWriter(expectedUser);
        User actualUser = readerWriter.getUser(file.getAbsolutePath());
        assertEquals(expectedUser.getName(), actualUser.getName());
        assertEquals(expectedUser.getStudentNumber(), actualUser.getStudentNumber());
        assertEquals(expectedUser.getDegreeModule(), actualUser.getDegreeModule());
        assertEquals(expectedUser.getCourses().size(), actualUser.getCourses().size());
    }
    
    /**
     * Test of setDegreeModuleToAUser method, of class UserReaderWriter.
     */
    @Test
    public void testSetDegreeModuleToAUser() {
        DegreeProgramme selectedProgramme = new DegreeProgramme("Computer Science", "CS", "Group A", 180, "Cs", "Compture Sciences", "CS topics");
        User user = new User("John Doe", "12345");
        UserReaderWriter readerWriter = new UserReaderWriter(user);
        boolean result = readerWriter.setDegreeModuleToAUser(selectedProgramme, user);
        assertTrue(result);
        assertEquals("CS", user.getDegreeModule());
        User actualUser = readerWriter.getUser("12345.json");
        assertEquals("CS", actualUser.getDegreeModule());
    }

    /**
     * Test of addCourseToUser method, of class UserReaderWriter.
     */
    @Test
    public void testAddCourseToUser() {
        User user = new User("John Doe", "12345");
        CourseUnit course = new CourseUnit("Math", "MATH101", "Group A", 5, "MATH", "Calculus", "Calculus topics");
        UserReaderWriter readerWriter = new UserReaderWriter(user);
        boolean result = readerWriter.addCourseToUser(course, user);
        assertTrue(result);
        // check if the course is added in the file
        User actualUser = readerWriter.getUser("12345.json");
        assertEquals(1, actualUser.getCourses().size());
        assertEquals("MATH Math (5 op)", actualUser.getCourses().get(0).toString());
    }

}
