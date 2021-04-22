package gui;

import client.Client;
import client.ClientServerCommunication;
import client.models.Friend;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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

public class FriendsController implements Initializable {

    ObservableList data = FXCollections.observableArrayList();

    @FXML
    private Button userButtonMain;

    @FXML
    private Button logoutButton;

    @FXML
    private StackPane parentContainer;

    @FXML
    private AnchorPane anchorRoot;

    @FXML
    private Button goToAddFriend;

    @FXML
    private Button backToWelcome;

    @FXML
    private TableView<Friend> friendTable;

    @FXML
    private TableColumn<Friend, String> usernameColumn;

    @FXML
    private TableColumn<Friend, String> scoreColumn;


    private TableRow<String> user;

    @FXML
    private Label rank;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loadFriends();

        int rank1 = ClientServerCommunication.getUserRank(Main.user.getUsername(),
                new Client(), new RestTemplate());
        String rankk = Integer.toString(rank1);
        char rankk1 = rankk.charAt(rankk.length() - 1);
        System.out.println(rankk);
        if (rank1 == 11 || rank1 == 12 || rank1 == 13) {
            rankk = rankk + "th";
        } else {
            switch (rankk1) {
                case '1':
                    rankk = Integer.toString(rank1) + "st";
                    break;
                case '2':
                    rankk = Integer.toString(rank1) + "nd";
                    break;
                case '3':
                    rankk = Integer.toString(rank1) + "rd";
                    break;
                default:
                    rankk = Integer.toString(rank1) + "th";
                    break;
            }
        }
        rank.setText(rankk);

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
    private void loadGoToAddFriend(ActionEvent event) throws IOException {

        URL url = new File("src/Main/java/gui/usernameSearch.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = goToAddFriend.getScene();

        goToAddFriend.setDisable(true);

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
        Scene scene = backToWelcome.getScene();

        backToWelcome.setDisable(true);

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


    private void loadFriends() {
        List<String>[] friends = ClientServerCommunication.retrieveFriends(
                Main.user, new Client(), new RestTemplate());

        //check whether the user authenticated successfully
        if (friends.length == 0 || friends[0] == null || friends[1] == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("No friends added yet!");
            alert.setContentText(
                    "It looks like you have not added any friends yet, "
                            + "make sure to add some and start competing!");
            alert.showAndWait();

        } else if (friends[0].size() == 0 || friends[1].size() == 0) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Error getting friends!");
            alert.setContentText(
                    "Something went wrong when retrieving your friends, "
                            + "please try again later");
            alert.showAndWait();
        } else {
            //make iterators that iterate over the result lists
            Iterator<String> scoreiter = friends[1].iterator();
            Iterator<String> usernameiter = friends[0].iterator();


            //add a new activity every row
            while (scoreiter.hasNext()) {
                data.add(new Friend(usernameiter.next(), scoreiter.next()));

            }

            //specify which column corresponds to which attribute

            usernameColumn.setCellValueFactory(new PropertyValueFactory<Friend, String>(
                    "username"));
            scoreColumn.setCellValueFactory(new PropertyValueFactory<Friend, String>(
                    "score"));
            //set the rows
            friendTable.setItems(data);


        }
    }
}


