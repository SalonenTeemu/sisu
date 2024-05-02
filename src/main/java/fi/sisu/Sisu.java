package fi.sisu;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The Sisu class is the main class of the JavaFX Sisu application. It extends
 * the Application class from the JavaFX library, and handles the initialization
 * and display of the login and main scenes. It also provides static methods for
 * retrieving the necessary instances and switching between scenes.
 */
public class Sisu extends Application {

    /**
     * The BackgroundHandler instance used by the application.
     */
    private static BackgroundHandler backgroundHandler;
    /**
     * The Main stage used by the application.
     */
    private Stage stage;
    /**
     * The Sisu instance used by the application.
     */
    private static Sisu instance;
    /**
     * The User instance used by the application.
     */
    private static User userFromFile;
    /**
     * The Authentication instance used by the application.
     */
    private static Authentication authentication;

    /**
     * Initializes the application by setting up the main stage and switching to
     * the login scene. Initializes also the BackgroundHandler and
     * Authentication instances.
     *
     * @param stage The main stage of the application.
     * @throws IOException If there is an error loading the login scene.
     */
    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        this.instance = this;
        switchToLoginScene();
        backgroundHandler = new BackgroundHandler();
        authentication = new Authentication();
    }

    /**
     * A getter for BackgroundHandler instance.
     *
     * @return The BackgroundHandler instance used by the application.
     */
    public static BackgroundHandler getBackgroundHandler() {
        return backgroundHandler;
    }

    /**
     * A getter for Authentication instance.
     *
     * @return The Authentication instance used by the application.
     */
    public static Authentication getAuthentication() {
        return authentication;
    }

    /**
     * A getter for Sisu instance.
     *
     * @return The Sisu instance used by the application.
     */
    public static Sisu getInstance() {
        return instance;
    }

    /**
     * Switches the main stage to the login scene by loading its FXML file.
     *
     * @throws IOException If there is an error loading the login scene.
     */
    public static void switchToLoginScene() throws IOException {
        Parent root = FXMLLoader.load(LoginSceneController.class
                .getResource("loginScene.fxml"));
        var scene = new Scene(root);
        getInstance().stage.setScene(scene);
        getInstance().stage.show();
    }

    /**
     * Switches the main stage to the main scene by loading its FXML file.
     *
     * @param studentNumber The student number of the user.
     * @throws IOException If there is an error loading the login scene.
     */
    public static void switchToMainScene(String studentNumber) throws IOException {
        FXMLLoader loader = new FXMLLoader(MainSceneController.class
                .getResource("mainScene.fxml"));
        Parent root = loader.load();
        var scene = new Scene(root);
        getInstance().stage.setScene(scene);
        getInstance().stage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args The command line arguments passed to the application.
     */
    public static void main(String[] args) {
        launch();
    }
}
