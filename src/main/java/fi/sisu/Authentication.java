package fi.sisu;

import java.io.IOException;

/**
 * The Authentication class handles user authentication and registration.
 */
public class Authentication {

    /**
     * The currently logged in student number.
     */
    private String currentlyLoggedInStudentNumber = null;
    /**
     * The cause of any errors that occur during login or registration.
     */
    private String errorCause;

    /**
     * Attempts to log in the user with the given student number.
     *
     * @param studentNumber the student number to log in with
     * @return true if the login was successful, false otherwise
     */
    public boolean login(String studentNumber) {
        User userFromFile = readUserFromFile(studentNumber);
        if (userFromFile != null) {
            currentlyLoggedInStudentNumber = studentNumber;
        }
        errorCause = "Opiskelijanumero on väärin tai sitä ei löytynyt!";
        return userFromFile != null;
    }

    /**
     * Gets the currently logged in student number.
     *
     * @return the currently logged in student number, or null if no user is
     * logged in
     */
    public String getCurrentlyLoggedInStudentNumber() {
        return currentlyLoggedInStudentNumber;
    }

    /**
     * Gets the currently logged in user.
     *
     * @return the currently logged in user, or null if no user is logged in
     */
    public User getCurrentlyLoggedInUser() {
        if (getCurrentlyLoggedInStudentNumber() == null) {
            return null;
        }
        return readUserFromFile(getCurrentlyLoggedInStudentNumber());
    }

    /**
     * Gets the cause of any errors that occur during login or registration.
     *
     * @return the error cause
     */
    public String getErrorCause() {
        return errorCause;
    }

    /**
     * Checks whether the given student number and name are valid for
     * registration.
     *
     * @param studentNumber the student number to check
     * @param studentName the student name to check
     * @return true if the information is valid, false otherwise
     * @throws IOException if an error occurs while checking the information
     */
    public boolean isRegisterInfoValid(String studentNumber, String studentName) throws IOException {
        // Perform validation on name and student number fields
        if (readUserFromFile(studentNumber) != null) {
            errorCause = "Opiskelijanumero on jo rekisteröity!";
            return false;
        } else if (studentNumber.isEmpty()) {
            errorCause = "Opiskelijanumero puuttuu!";
            return false;
        } else if (studentName.isEmpty()) {
            errorCause = "Nimi puuttuu!";
            return false;
        }

        // Create a new user with the entered name and student number
        User regUser = new User(studentName, studentNumber);
        iReadAndWriteToFile registerWriter = new UserReaderWriter(regUser);

        try {
            registerWriter.writeToFile(studentNumber + ".json");
            regUser.toString();
        } catch (Exception e) {
            System.err.println("Error writing to file: " + e.getMessage());
            System.out.println();
            return false;
        }
        return true;
    }

    /**
     * Reads a user from file with the given student number.
     *
     * @param studentNumber the student number to read the user for
     * @return the User object read from file, or null if no user was found
     */
    private User readUserFromFile(String studentNumber) {
        User newUser = new User();
        UserReaderWriter userReaderWriter = new UserReaderWriter(newUser);

        try {
            boolean readSuccess = userReaderWriter.readFromFile(studentNumber + ".json");
            if (readSuccess) {
                return userReaderWriter.getUser(studentNumber + ".json");
            }
        } catch (Exception e) {
            System.err.println("Error reading user from file: " + e.getMessage());
        }

        return null;
    }
}
