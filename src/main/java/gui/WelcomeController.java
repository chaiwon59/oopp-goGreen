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
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.json.simple.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    @FXML
    private Button userButtonMain;

    @FXML
    private StackPane parentContainer;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private Button goToFriends;

    @FXML
    private Button logoutButton;

    @FXML
    private Button goToMainScreen;

    @FXML
    private Button showActivitiesScreenButton;

    @FXML
    private XYChart<String, Double> scoreGraph;

    @FXML
    private CategoryAxis name;

    @FXML
    private NumberAxis score;

    @FXML
    private Label welcomeUser;

    @FXML
    private Label scoreLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String name = Main.user.getNameDb(new Client(), new RestTemplate());
        String name1 = name.substring(0, 1).toUpperCase();
        name1 = name1 + name.substring(1);
        welcomeUser.setText(name1);

        int score = ClientServerCommunication.getScore(
                Main.user.getUsername(), new Client(), new RestTemplate());
        scoreLabel.setText(Integer.toString(score));

        XYChart.Series set1 = new XYChart.Series<>();


        Client c1 = new Client();
        JSONObject input = new JSONObject();
        input.put("username", Main.user.getUsername());

        int scoress = c1.getScore(input, new RestTemplate());

        c1.getFriends(input, new RestTemplate());

        User u1 = new User(Main.user.getUsername(), Main.user.getPassword());

        List<String>[] friends = ClientServerCommunication.retrieveFriends(
                Main.user, new Client(), new RestTemplate());

        set1.getData().add(new XYChart.Data<>(name, scoress));
        int i = 0;

        while (i < 6 && i < friends[0].size()) {
            if (!(friends[0].get(i).equals(Main.user.getUsername()))) {
                if (set1.getData().size() < 6) {
                    set1.getData().add(
                            new XYChart.Data<>(
                                    friends[0].get(i), Integer.parseInt(friends[1].get(i))));
                }
            }
            i++;
        }
        scoreGraph.getData().addAll(set1);


    }


    @FXML
    private void loadProfileScreen(ActionEvent event4) throws IOException {

        URL url = new File("src/Main/java/gui/profileScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = userButtonMain.getScene();

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
    private void loadGoToFriends(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/friends.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = goToFriends.getScene();
        getClass().getResource("TableView.css");

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
    private void loadMainScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/mainScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = goToMainScreen.getScene();

        //trying to get the css to work
        //scene.getStylesheets().add(getClass().getResource("CheckBox.css").toString());

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
    private void showActivitiesScreen(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/showActivitiesScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = showActivitiesScreenButton.getScene();
        getClass().getResource("TableView.css");

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
