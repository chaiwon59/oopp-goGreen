package gui;

import client.Client;
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
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML
    private Button profileBackButton;

    @FXML
    private Button settingsScreenButton;

    @FXML
    private Label nameLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String name = Main.user.getNameDb(new Client(), new RestTemplate());
        String name1 = name.substring(0, 1).toUpperCase();
        name1 = name1 + name.substring(1);
        nameLabel.setText(name1);
        usernameLabel.setText(Main.user.getUsername());
    }

    @FXML
    private void loadWelcomeScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/welcomeScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = profileBackButton.getScene();

        profileBackButton.setDisable(true);

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

    private void loadSettingsScreen(ActionEvent event2) throws IOException {

        URL url = new File("src/Main/java/gui/settingsScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = settingsScreenButton.getScene();

        settingsScreenButton.setDisable(true);

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
