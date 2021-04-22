package gui;

import client.Client;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChangePasswordController implements Initializable {


    @FXML
    private PasswordField currentPassword;

    @FXML
    private PasswordField newPassword;

    @FXML
    private PasswordField confirmNewPassword;

    @FXML
    private Button submitButtonChangePw;

    @FXML
    private Button changePasswordBackButton;

    @FXML
    private StackPane parentContainer;

    @FXML
    private AnchorPane anchorRoot;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private boolean passwordmatched(String newpassword, String confirmnewpassword) {
        boolean passwordMatch = true;
        if (!newpassword.equals(confirmnewpassword)) {
            passwordMatch = false;

            newPassword.setText(null);
            confirmNewPassword.setText(null);

        }
        return passwordMatch;

    }

    @FXML
    private void loadProfileScreen(ActionEvent event) throws IOException {
        String currentpassword = currentPassword.getText();
        String newpassword = newPassword.getText();
        String confirmnewpassword = confirmNewPassword.getText();


        Main.user = new User(Main.user.getUsername(), Main.user.getPassword());


        if (passwordmatched(newpassword, confirmnewpassword) && Main.user.changePassword(
                currentpassword, newpassword, new Client(), new RestTemplate())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Password has been changed succesfully");
            alert.showAndWait();

            Main.user = new User(Main.user.getUsername(), newpassword);
            URL url = new File("src/Main/java/gui/profileScreen.fxml").toURL();
            Parent root = FXMLLoader.load(url);
            Scene scene = submitButtonChangePw.getScene();

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
        } else {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Password is incorrect or passwords do not match");
            alert.setContentText("Please try again");

            alert.showAndWait();
            currentPassword.setText(null);
            newPassword.setText(null);
            confirmNewPassword.setText(null);

        }

    }


    @FXML

    private void loadProfileScreenback(ActionEvent event2) throws IOException {

        URL url = new File("src/Main/java/gui/settingsScreen.fxml").toURL();
        Parent root = FXMLLoader.load(url);
        Scene scene = changePasswordBackButton.getScene();

        changePasswordBackButton.setDisable(true);

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
