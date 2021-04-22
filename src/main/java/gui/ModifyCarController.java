package gui;

import client.Client;
import client.ClientServerCommunication;
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

public class ModifyCarController implements Initializable {

    @FXML
    private Button backButton;

    @FXML
    private Button addCar;

    @FXML
    private Button updateCar;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;

    @FXML
    private TextField brand;

    @FXML
    private TextField buildyear;

    @FXML
    private TextField model;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loadSettingsScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/settingsScreen.fxml").toURL();
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

    @FXML
    private void updateCar(ActionEvent event) {
        if (ClientServerCommunication.updateCar(model.getText(), Main.user, buildyear.getText(),
                brand.getText(), new Client(), new RestTemplate())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Car updated successfully!");
            alert.setContentText("Your car has been updated accordingly.");
            Main.car.setBrand(brand.getText());
            Main.car.setModel(model.getText());
            Main.car.setYear(buildyear.getText());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Something went wrong updating your car!");
            alert.setContentText("Something went wrong updating your car, perhaps "
                    + "you don't have an existing car added or entered wrong values?");
            brand.setText(null);
            model.setText(null);
            buildyear.setText(null);
            alert.showAndWait();
        }
    }

    @FXML
    private void addCar(ActionEvent event) {
        if (ClientServerCommunication.addCar(model.getText(), Main.user, buildyear.getText(),
                brand.getText(), new Client(), new RestTemplate())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Car added successfully!");
            alert.setContentText("Your car has been added to your account");
            Main.car = new Car(buildyear.getText(), brand.getText(), model.getText());
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Something went wrong when adding your car!");
            alert.setContentText("Something went wrong adding your car, perhaps "
                    + "you already added a car?");
            alert.showAndWait();
            brand.setText(null);
            model.setText(null);
            buildyear.setText(null);
        }
    }
}

