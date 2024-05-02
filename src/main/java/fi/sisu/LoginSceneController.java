package fi.sisu;

import java.io.IOException;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Controller class for the login scene in JavaFX Sisu application.
 */
public class LoginSceneController {

    /**
     * The text field for reading user input (student number) from login form.
     */
    @FXML
    private TextField studentNumberField;
    /**
     * The label for displaying an error message in login form.
     */
    @FXML
    private Label loginErrorMessageLabel;

    /**
     * The text field for reading user input (name) from registration form.
     */
    @FXML
    private TextField nameTextField;
    /**
     * The text field for reading user input (student number) from registration
     * form.
     */
    @FXML
    private TextField studentNumberTextField;
    /**
     * The label for displaying an error message in registration form.
     */
    @FXML
    private Label registrationErrorMessageLabel;

    /**
     * The student number that is read from the FXML element.
     */
    private String studentNumber;
    /**
     * The name that is read from the FXML element.
     */
    private String studentName;

    /**
     * The user that is currently logged in.
     */
    User userFromFile;

    /**
     * Initializes the controller. Sets login and registration error messages to
     * null when the scene is loaded.
     */
    public void initialize() {
        setLoginErrorMessage(null);
        setRegistrationErrorMessage(null);
    }

    /**
     * Sets the error message for login form.
     *
     * @param message The error message to be displayed.
     */
    public void setLoginErrorMessage(String message) {
        if (message == null || message.isEmpty()) {
            loginErrorMessageLabel.setText("");
            loginErrorMessageLabel.setVisible(false);
            loginErrorMessageLabel.setManaged(false);
            return;
        }

        loginErrorMessageLabel.setText(message);
        loginErrorMessageLabel.setVisible(true);
        loginErrorMessageLabel.setManaged(true);
    }

    /**
     * Sets the error message for registration form.
     *
     * @param message The error message to be displayed.
     */
    public void setRegistrationErrorMessage(String message) {
        if (message == null || message.isEmpty()) {
            registrationErrorMessageLabel.setText("");
            registrationErrorMessageLabel.setVisible(false);
            registrationErrorMessageLabel.setManaged(false);
            return;
        }

        registrationErrorMessageLabel.setText(message);
        registrationErrorMessageLabel.setVisible(true);
        registrationErrorMessageLabel.setManaged(true);
    }

    /**
     * Handles the button click event for login form. Tries to login with given
     * student number, switches to main scene if successful, and sets error
     * message if not.
     *
     * @param event The ActionEvent object that triggered the event.
     * @throws IOException If there was error in login.
     */
    @FXML
    private void handleLoginButtonClick(ActionEvent event) throws IOException {
        setRegistrationErrorMessage(null);
        studentNumber = studentNumberField.getText();
        if (Sisu.getAuthentication().login(studentNumber)) {
            Sisu.switchToMainScene(studentNumber);
        } else {
            String errorCause = Sisu.getAuthentication().getErrorCause();
            setLoginErrorMessage(errorCause);
        }
    }

    /**
     * Handles the button click event for registration form. Tries to register a
     * new user with given student number and name, switches to main scene if
     * successful, and sets error message if not.
     *
     * @param event The ActionEvent object that triggered the event.
     * @throws IOException If there was error in registration.
     */
    @FXML
    private void handleRegisterButtonClick(ActionEvent event) throws IOException {
        setLoginErrorMessage(null);
        studentNumber = studentNumberTextField.getText();
        studentName = nameTextField.getText();

        if (Sisu.getAuthentication().isRegisterInfoValid(studentNumber, studentName)) {
            Sisu.getAuthentication().login(studentNumber);
            Sisu.switchToMainScene(studentNumber);
        } else {
            String errorCause = Sisu.getAuthentication().getErrorCause();
            setRegistrationErrorMessage(errorCause);
        }
    }

    /**
     * Handles the button click event for quitting the application.
     *
     * @param event The ActionEvent object that triggered the event.
     * @throws IOException If the was error in quitting the application.
     */
    @FXML
    private void handleQuitButtonClick(ActionEvent event) throws IOException {
        Platform.exit();
    }
}
