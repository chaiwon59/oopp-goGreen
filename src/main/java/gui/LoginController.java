package gui;

import client.Client;
import client.ClientServerCommunication;
import client.User;
import client.models.Car;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    @FXML
    public TextField usernameField;

    @FXML
    public TextField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButtonLogin;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }


    @FXML
    private void loadRegisterScreen(ActionEvent event) throws IOException {


        URL url = new File("src/Main/java/gui/registerScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = registerButtonLogin.getScene();

        registerButtonLogin.setDisable(true);

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

    @FXML
    public void loadWelcomeScreen(ActionEvent event2) throws NullPointerException {
        String username = usernameField.getText();
        String password = passwordField.getText();


        Main.user = new User(username, password);
        if (Main.user.authenticate(new Client(), new RestTemplate())) {
            try {


                URL url = new File("src/main/java/gui/welcomeScreen.fxml").toURL();
                Parent root = FXMLLoader.load(url);
                Scene scene = loginButton.getScene();

                loginButton.setDisable(true);

                Car car = ClientServerCommunication.getCar(Main.user, new Client(),
                        new RestTemplate());
                if (car == null) {
                    Main.car = null;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Information Dialog");
                    alert.setHeaderText("No car detected!");
                    alert.setContentText("You can add a car by going to the User screen and "
                            + "then to the settings!");
                    alert.showAndWait();
                } else {
                    Main.car = car;
                }

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
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Username or password is incorrect");
            alert.setContentText("Please try again");

            alert.showAndWait();
            usernameField.setText(null);
            passwordField.setText(null);

        }

    }

}
