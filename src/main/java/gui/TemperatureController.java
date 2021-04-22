package gui;

import client.Client;
import client.ClientServerCommunication;
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

public class TemperatureController implements Initializable {

    @FXML
    private StackPane parentContainer;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private Button energyBackButton;

    @FXML
    private Button userButtonEnergy;

    @FXML
    private Button submitButtonEnergy;

    @FXML
    private Button backToEnergy;

    @FXML
    private Button logoutButton;

    @FXML
    private TextField temperature;

    @FXML
    private TextField time;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loadProfileScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/profileScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = userButtonEnergy.getScene();

        userButtonEnergy.setDisable(true);

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
    private void loadEnergyScreen(ActionEvent event2) throws IOException {

        URL url = new File("src/Main/java/gui/energyScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = backToEnergy.getScene();

        backToEnergy.setDisable(true);

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
    private void loadMainScreen(ActionEvent event3) throws IOException {

        URL url = new File("src/Main/java/gui/mainScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = energyBackButton.getScene();

        energyBackButton.setDisable(true);

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
    private void loadLoginScreen(ActionEvent event3) throws IOException {

        URL url = new File("src/Main/java/gui/loginScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = logoutButton.getScene();

        logoutButton.setDisable(true);

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
    private void addTemperatureActivity(ActionEvent event) throws IOException {
        boolean result;
        result = ClientServerCommunication
                .addTemperatureActivity("HouseTemperature",
                        Main.user, temperature.getText(),
                        time.getText(), new Client(), new RestTemplate());


        if (result) {
            URL url = new File("src/Main/java/gui/mainScreen.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = energyBackButton.getScene();

            energyBackButton.setDisable(true);

            root.translateYProperty().set(scene.getHeight());
            parentContainer.getChildren().add(root);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Activity added successfully!");
            alert.setContentText(
                    "The activity is now added to your account "
                            + "and your score is updated accordingly.");

            alert.showAndWait();
            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(root.translateYProperty(), 0, Interpolator.EASE_IN);
            KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
            timeline.getKeyFrames().add(kf);
            timeline.setOnFinished(t -> {
                parentContainer.getChildren().remove(anchorRoot);
            });
            timeline.play();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Error adding activity!");
            alert.setContentText(
                    "Something went wrong when trying to add your activity, "
                            + "please try again later.");
        }

    }

}
