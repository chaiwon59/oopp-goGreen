package gui;

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
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EnergyController implements Initializable {

    @FXML
    private Button energyBackButton;

    @FXML
    private Button userButtonEnergy;

    @FXML
    private Button solarButton;

    @FXML
    private Button temperatureButton;

    @FXML
    private Button logoutButton;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loadMainScreen(ActionEvent event) throws IOException {


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
    private void loadProfileScreen(ActionEvent event2) throws IOException {

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
    private void loadSolarScreen(ActionEvent event2) throws IOException {

        URL url = new File("src/Main/java/gui/solarScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = solarButton.getScene();

        solarButton.setDisable(true);

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
    private void loadTemperatureScreen(ActionEvent event2) throws IOException {

        URL url = new File("src/Main/java/gui/temperatureScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = temperatureButton.getScene();

        temperatureButton.setDisable(true);

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
    private void loadLoginScreen(ActionEvent event2) throws IOException {

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


}
