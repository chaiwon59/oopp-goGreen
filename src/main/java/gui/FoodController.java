package gui;

import client.Client;
import client.ClientServerCommunication;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class FoodController implements Initializable {

    ObservableList list = FXCollections.observableArrayList();

    @FXML
    private Button foodBackButton;

    @FXML
    private Button userButtonFood;

    @FXML
    private Button addFoodActivity;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;

    @FXML
    private ChoiceBox<String> mealChoice;

    @FXML
    private CheckBox localChoiceYes;

    @FXML
    private Button logoutButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        mealChoice.getItems()
                .addAll(
                        "Regular",
                        "Meal with a lot of beef", "No beef", "Vegetarian", "Vegan");

    }

    @FXML
    private void loadMainScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/mainScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = foodBackButton.getScene();

        foodBackButton.setDisable(true);

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
    private void loadProfileScreen(ActionEvent event2) throws IOException {

        URL url = new File("src/Main/java/gui/profileScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = userButtonFood.getScene();

        userButtonFood.setDisable(true);

        root.translateYProperty().set(scene.getHeight());
        parentContainer.getChildren().add(root);

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(root.translateYProperty(),
                0, Interpolator.EASE_IN);
        KeyFrame kf = new KeyFrame(Duration.seconds(1), kv);
        timeline.getKeyFrames().add(kf);
        timeline.setOnFinished(t -> {
            parentContainer.getChildren().remove(anchorRoot);
        });
        timeline.play();

    }

    @FXML
    private void addFoodActivity(ActionEvent event) throws IOException {
        boolean result;
        if (localChoiceYes.isSelected()) {
            result = ClientServerCommunication
                    .addFoodActivity(mealChoice.getValue(),
                            Main.user, "Yes", new Client(), new RestTemplate());
        } else {
            result = ClientServerCommunication
                    .addFoodActivity(mealChoice.getValue(),
                            Main.user, "No", new Client(), new RestTemplate());

        }

        if (result) {
            URL url = new File("src/Main/java/gui/mainScreen.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = foodBackButton.getScene();

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
            alert.showAndWait();
        }

    }

}
