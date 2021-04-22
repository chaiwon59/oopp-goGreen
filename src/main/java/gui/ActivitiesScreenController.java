package gui;

import client.Client;
import client.ClientServerCommunication;
import client.models.ActivityTableView;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;


public class ActivitiesScreenController implements Initializable {

    ObservableList data = FXCollections.observableArrayList();

    @FXML
    private Button backButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button userButtonMain;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private StackPane parentContainer;

    @FXML
    private TableView<String> activityList;

    @FXML
    private TableColumn<String, String> categoryColumn;

    @FXML
    private TableColumn<String, String> activityColumn;

    @FXML
    private TableColumn<String, String> scoreColumn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadData();

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
    private void loadWelcomeScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/welcomeScreen.fxml").toURL();
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

    private void loadData() {
        List<String>[] activities = ClientServerCommunication.getUserActivities(
                Main.user, new Client(), new RestTemplate());

        //check whether the user authenticated successfully
        if (activities.length == 0 || activities == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Error getting activities!");
            alert.setContentText(
                    "Something went wrong when retrieving your activities, "
                            + "please try again later");
            alert.showAndWait();
        } else if (activities[0] == null || activities[1] == null || activities[2] == null) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("You dont have any activities yet!");
            alert.setContentText(
                    "If you add some new activities, they will be displayed on this screen.");
            alert.showAndWait();
        } else {
            //make iterators that iterate over the result lists
            System.out.println(activities[0].get(0));
            Iterator<String> scoreiter = activities[2].iterator();
            Iterator<String> categoryiter = activities[0].iterator();
            Iterator<String> activityiter = activities[1].iterator();

            //add a new activity every row
            while (scoreiter.hasNext()) {
                data.add(new ActivityTableView(categoryiter.next(),
                        activityiter.next(), scoreiter.next()));

            }

            //specify which column corresponds to which attribute
            categoryColumn.setCellValueFactory(new PropertyValueFactory<String, String>(
                    "category"));
            activityColumn.setCellValueFactory(new PropertyValueFactory<String, String>(
                    "activity"));
            scoreColumn.setCellValueFactory(new PropertyValueFactory<String, String>(
                    "score"));
            //set the rows
            activityList.setItems(data);


        }
    }

}
