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

public class PublicTranspController implements Initializable {

    @FXML
    private StackPane parentContainer;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private Button userButtonTransp;

    @FXML
    private Button transportationBackButton;

    @FXML
    private Button submitButtonTransp;

    @FXML
    private Button backToTransportation;

    @FXML
    private Button logout;

    @FXML
    private TextField distanceField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loadProfileScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/profileScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = userButtonTransp.getScene();

        userButtonTransp.setDisable(true);

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
    private void loadTransportationScreen(ActionEvent event2) throws IOException {

        URL url = new File("src/Main/java/gui/transportationScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = backToTransportation.getScene();

        backToTransportation.setDisable(true);

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
        Scene scene = transportationBackButton.getScene();

        transportationBackButton.setDisable(true);

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
    private void publicTransportInsteadOfCar(ActionEvent event) {
        if (ClientServerCommunication.addTransportActivity(Main.user,
                Integer.parseInt(distanceField.getText()), "Bus",
                "Car", new Client(), new RestTemplate())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Activity added successfully!");
            alert.setContentText(
                    "The activity is now added to your account "
                            + "and your score is updated accordingly.");

            alert.showAndWait();

        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Error adding activity!");
            alert.setContentText(
                    "Something went wrong when trying to add your activity, "
                            + "maybe you have to add a car first?");
            alert.showAndWait();
        }
    }

    @FXML
    private void loadbacktoLogIn(ActionEvent event) throws IOException {
        Main.user.deleteUser(new Client(), new RestTemplate());

        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
        alert2.setTitle("Information Dialog");
        alert2.setHeaderText("Your account has been deleted");
        alert2.showAndWait();

        URL url = new File("src/Main/java/gui/loginScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = logout.getScene();

        logout.setDisable(true);

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
