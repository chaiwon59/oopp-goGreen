package gui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private Button foodButtonMain;

    @FXML
    private Button transportationButtonMain;

    @FXML
    private Button energyButtonMain;

    @FXML
    private Button userButtonMain;

    @FXML
    private Button mainBackButton;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;

    @FXML
    private Button logoutButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    @FXML

    private void loadFoodScreen(ActionEvent event) throws IOException {


        URL url = new File("src/Main/java/gui/foodScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = foodButtonMain.getScene();
        getClass().getResource("CheckBox.css");

        foodButtonMain.setDisable(true);

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
        Scene scene = transportationButtonMain.getScene();

        transportationButtonMain.setDisable(true);

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
    private void loadEnergyScreen(ActionEvent event3) throws IOException {

        URL url = new File("src/Main/java/gui/energyScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = energyButtonMain.getScene();

        energyButtonMain.setDisable(true);

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
    private void loadProfileScreen(ActionEvent event4) throws IOException {

        URL url = new File("src/Main/java/gui/profileScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = userButtonMain.getScene();

        userButtonMain.setDisable(true);

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
    private void loadWelcomeScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/welcomeScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = mainBackButton.getScene();

        mainBackButton.setDisable(true);

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
    private void logout(ActionEvent event) throws IOException {

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
    private void serCursor(MouseEvent event) throws IOException {

        foodButtonMain.setOnMouseEntered(mouseEvent -> foodButtonMain.setCursor(Cursor.HAND));

    }

    @FXML
    private void serCursor2(MouseEvent event) throws IOException {

        energyButtonMain.setOnMouseEntered(mouseEvent -> energyButtonMain.setCursor(Cursor.HAND));

    }

    @FXML
    private void serCursor3(MouseEvent event) throws IOException {

        transportationButtonMain.setOnMouseEntered(mouseEvent ->
                transportationButtonMain.setCursor(Cursor.HAND));

    }


}
