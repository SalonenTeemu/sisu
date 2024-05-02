package fi.sisu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for reading, writing, and manipulating user data stored in JSON format.
 */
public class UserReaderWriter implements iReadAndWriteToFile {

    private final User user;

    /**
     * Constructs a new instance of the UserReaderWriter class with the given user object.
     * @param user the user object to manipulate
     */
    public UserReaderWriter(User user) {
        this.user = user;
    }

    /**
     * Reads user data from a file and updates the user object with the data.
     * @param fileName the name of the file to read from
     * @return true if the file was read successfully, false otherwise
     * @throws Exception if there was an error reading the file
     */
    @Override
    public boolean readFromFile(String fileName) throws Exception {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(fileName)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            this.user.setName(jsonObject.get("name").getAsString());
            this.user.setStudentNumber(jsonObject.get("studentNumber").getAsString());

            JsonElement degreeModuleElement = jsonObject.get("degreeModule");
            if (degreeModuleElement == null || degreeModuleElement instanceof JsonNull) {
            } else {
                this.user.setDegreeModule(jsonObject.get("degreeModule").getAsString());
            }

            // Read the courses
            JsonArray coursesArray = jsonObject.getAsJsonArray("courses");
            if (coursesArray != null) {
                for (JsonElement courseElement : coursesArray) {
                    JsonObject courseObject = courseElement.getAsJsonObject();
                    String courseName = courseObject.get("courseName").getAsString();
                    String courseId = courseObject.get("id").getAsString();
                    String groupId = courseObject.get("groupId").getAsString();
                    int minCredits = courseObject.get("minCredits").getAsInt();
                    String code = courseObject.get("code").getAsString();
                    String description = courseObject.get("description").getAsString();
                    String outcomes = courseObject.get("outcomes").getAsString();
                    boolean completed = courseObject.get("completed").getAsBoolean();

                    CourseUnit course = new CourseUnit(courseName, courseId, groupId, minCredits, code, description, outcomes);
                    course.setCompleted(completed);
                    this.user.addCourse(course);
                }

            }
            return true;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
            return false;
        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Writes user data to a file in JSON format.
     * @param fileName the name of the file to write to
     * @return true if the file was written successfully, false otherwise
     * @throws Exception if there was an error writing to the file
     */
    @Override
    public boolean writeToFile(String fileName) throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonObject jsonObject = new JsonObject();

        // Make the 'name' field accessible
        Field nameField = User.class.getDeclaredField("name");
        nameField.setAccessible(true);
        jsonObject.addProperty("name", (String) nameField.get(this.user));

        // Make the 'studentNumber' field accessible
        Field studentNumberField = User.class.getDeclaredField("studentNumber");
        studentNumberField.setAccessible(true);
        jsonObject.addProperty("studentNumber", (String) studentNumberField.get(this.user));

        Field degreeModuleField = User.class.getDeclaredField("degreeModule");
        degreeModuleField.setAccessible(true);
        // Check if the value of 'degreeModule' is null
        jsonObject.addProperty("degreeModule", (String) degreeModuleField.get(this.user));

        //List<CourseUnit> coursesList = new ArrayList<>();
        List<CourseUnit> userCourses = this.user.getCourses();
        JsonArray coursesArray = new JsonArray();
        for (CourseUnit course : userCourses) {
            JsonObject courseObject = new JsonObject();
            courseObject.addProperty("courseName", course.getName());
            courseObject.addProperty("id", course.getId());
            courseObject.addProperty("groupId", course.getGroupId());
            courseObject.addProperty("minCredits", course.getMinCredits());
            courseObject.addProperty("code", course.getCode());
            courseObject.addProperty("description", course.getDescription());
            courseObject.addProperty("outcomes", course.getOutcomes());
            courseObject.addProperty("completed", course.isCompleted());
            coursesArray.add(courseObject);
        }
        jsonObject.add("courses", coursesArray);

        String json = gson.toJson(jsonObject);
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + fileName);
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Returns a user object containing data read from a file.
     * @param fileName the name of the file to read from
     * @return a user object containing data read from the file
     */
    public User getUser(String fileName) {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(fileName)) {
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            String name = jsonObject.get("name").getAsString();
            String studentNumber = jsonObject.get("studentNumber").getAsString();
            User user = new User(name, studentNumber);

            if (jsonObject.has("degreeModule") && !jsonObject.get("degreeModule").isJsonNull()) {
                String degreeModule = jsonObject.get("degreeModule").getAsString();
                user.setDegreeModule(degreeModule);
            }

            if (jsonObject.has("courses") && !jsonObject.get("courses").isJsonNull()) {
                List<CourseUnit> coursesList = new ArrayList<>();
                JsonArray coursesArray = jsonObject.getAsJsonArray("courses");
                for (JsonElement courseElement : coursesArray) {
                    JsonObject courseObject = courseElement.getAsJsonObject();
                    String courseName = courseObject.get("courseName").getAsString();
                    String courseId = courseObject.get("id").getAsString();
                    String groupId = courseObject.get("groupId").getAsString();
                    int minCredits = courseObject.get("minCredits").getAsInt();
                    String code = courseObject.get("code").getAsString();
                    String description = courseObject.get("description").getAsString();
                    String outcomes = courseObject.get("outcomes").getAsString();
                    boolean completed = courseObject.get("completed").getAsBoolean();
                    

                    CourseUnit c = new CourseUnit(courseName, courseId, groupId, minCredits, code, description, outcomes);
                    c.setCompleted(completed);
                    coursesList.add(c);
                }
                user.setCourses(coursesList);
            }
            return user;
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + fileName);
            return null;
        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName);
            e.printStackTrace();

        }
        return null;
    }

    /**    
     * Retrieves user data from a JSON file based on the provided student number.
     * @param studentNumber the student number of the user to retrieve data for
     * @return a User object containing the retrieved data
     */
    public static User getUserData(String studentNumber) {
        UserReaderWriter userReaderWriter = new UserReaderWriter(new User());
        User user = userReaderWriter.getUser(studentNumber + ".json");
        return user;
    }

    /**
     * Sets the degree module of a User object to the specified DegreeProgramme object and writes the updated
     * user information to a JSON file.
     * @param selectedProgramme the DegreeProgramme object representing the selected degree module
     * @param user the User object to update
     * @return true if the degree module was successfully set and the user information was written to the file; false otherwise
     */
    public boolean setDegreeModuleToAUser(DegreeProgramme selectedProgramme, User user) {
        user.setDegreeModule(selectedProgramme.getId());
        // Write new user information to  a file:
        iReadAndWriteToFile registerWriter = new UserReaderWriter(user);

        try {
            registerWriter.writeToFile(user.getStudentNumber() + ".json");
            System.out.println("Writing degreemodule was succesfully!");

        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
            System.out.println();
            return false;
        }
        return true;
    }

    /**
     * Adds a CourseUnit object to a User object's list of courses and writes the updated user information to a JSON file.
     * @param course the CourseUnit object to add
     * @param user the User object to update
     * @return true if the course was successfully added and the user information was written to the file; false otherwise
     */
    public boolean addCourseToUser(CourseUnit course, User user) {
        user.addCourse(course);
        // Write updated user information to a file:
        iReadAndWriteToFile registerWriter = new UserReaderWriter(user);

        try {
            registerWriter.writeToFile(user.getStudentNumber() + ".json");
            System.out.println("Adding course was successful!");

        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
            return false;
        }
        return true;
    }

}
