package gui;

import client.Client;
import client.ClientServerCommunication;
import client.User;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.web.client.RestTemplate;
import server.DatabaseConnection;
import server.RegisterUserController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    public Label errorUsername;

    @FXML
    public Label errorPassword;

    @FXML
    public TextField nameRegister;

    @FXML
    public TextField usernameRegister;

    @FXML
    public PasswordField passwordRegister;

    @FXML
    public PasswordField confirmPasswordRegister;

    @FXML
    private Button registerButtonRegister;

    @FXML
    private Button backButton;

    @FXML
    private StackPane parentContainer;

    @FXML
    private AnchorPane anchorRoot;

    private DatabaseConnection db = new DatabaseConnection();
    private Connection connect = null;
    private RegisterUserController ru1 = new RegisterUserController();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //connect = db.connect();

    }

    @FXML
    private void loadLoginScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/loginScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = backButton.getScene();

        backButton.setDisable(true);

        root.translateYProperty().set(scene.getHeight());
        parentContainer.getChildren().add(root);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(t -> {
            parentContainer.getChildren().remove(anchorRoot);
        });
        timeline.play();
    }


    private boolean passwordmatched(String password, String passwordConf) {
        boolean passwordMatch = true;
        if (!password.equals(passwordConf)) {
            errorPassword.setVisible(true);
            passwordMatch = false;

            passwordRegister.setText(null);
            confirmPasswordRegister.setText(null);

        }
        return passwordMatch;

    }

    /**
     * This method registers a new user into the database.
     *
     * @param event Eventhandling
     */
    @FXML
    public void register(ActionEvent event) {

        String username = usernameRegister.getText();
        String password = passwordRegister.getText();
        String passConf = confirmPasswordRegister.getText();
        String name = nameRegister.getText();


        if (!(ClientServerCommunication.isUnique(
                username, new Client(), new RestTemplate()))) {
            errorUsername.setVisible(true);
            usernameRegister.setText(null);
        }

        if (ClientServerCommunication.isUnique(
                username, new Client(), new RestTemplate())) {
            errorUsername.setVisible(false);
        }

        if (passwordmatched(password, passConf)) {
            errorPassword.setVisible(false);
        }


        if (ClientServerCommunication.isUnique(
                username, new Client(), new RestTemplate())
                && passwordmatched(password, passConf)) {
            errorPassword.setVisible(false);
            errorUsername.setVisible(false);
            User u1 = new User(username, password, name);
            System.out.println("0");
            u1.createUser(new Client(), new RestTemplate());
            System.out.println("1");
            ClientServerCommunication.addUserAsFriend(username, new Client(), new RestTemplate());
            System.out.println("2");

            try {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText("You've been successfully registered");
                alert.setContentText("You may now log in");

                alert.showAndWait();

                usernameRegister.setText(null);
                passwordRegister.setText(null);
                confirmPasswordRegister.setText(null);
                nameRegister.setText(null);

                URL url = new File("src/Main/java/gui/loginScreen.fxml").toURL();
                Parent root = FXMLLoader.load(url);
                Scene scene = registerButtonRegister.getScene();

                root.translateYProperty().set(scene.getHeight());
                parentContainer.getChildren().add(root);

                Timeline timeline = new Timeline();
                KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
                KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
                timeline.getKeyFrames().add(kf);
                timeline.setOnFinished(t -> {
                    parentContainer.getChildren().remove(anchorRoot);
                });
                timeline.play();


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
