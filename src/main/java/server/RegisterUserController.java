package server;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@RestController
public class RegisterUserController {

    private DatabaseConnection db = new DatabaseConnection();

    private Connection connect = null;


    /**
     * This method takes a username as input and puts it in a JSON object.
     * It returns a JSON object with the value true
     * or false that indicates the uniqueness of the username.
     *
     * @param input this is a JSON object with the username
     * @param db    the database connection to check the username
     * @return a JSON object that contains the value true or false, this indicates uniqueness.
     * @throws SQLException is thrown when a database error occurs
     */
    @PostMapping("/isUnique")
    public JSONObject isUnique(@RequestBody JSONObject input,
                               @RequestParam(required = false)
                                       DatabaseConnection db) throws SQLException {
        String username = (String) input.get("username");
        if (db == null) {
            db = new DatabaseConnection();
        }
        Connection connect = db.connect();


        try {
            String userUnique = "Select \"username\" from \"UserTable\" where \"username\"= ?";
            PreparedStatement pstUserUnique = connect.prepareStatement(userUnique);
            pstUserUnique.setString(1, username);
            ResultSet rsUserUnique = pstUserUnique.executeQuery();

            JSONObject jsonobject = new JSONObject();
            //boolean unique;
            if (rsUserUnique.next()) {
                jsonobject.put("unique", false);
                pstUserUnique.close();
                return jsonobject;
            } else {
                jsonobject.put("unique", true);
                pstUserUnique.close();
                return jsonobject;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("unique", false);
            return jsonobject;
        }
    }

    /**
     * In this method a user is added to the friends with the
     * username and returns a JSONobject with a rank.
     *
     * @param input JSONobject with a username
     * @param db    the database connection
     * @return returns a JSONobject with that indicates a succes or a fail
     * @throws SQLException is thrown when the db gives an error
     */
    @PostMapping("/addUserAsFriend")
    public JSONObject addUserAsFriend(@RequestBody JSONObject input,
                                      @RequestParam(required = false)
                                              DatabaseConnection db) throws SQLException {
        String username = (String) input.get("username");
        if (db == null) {
            db = new DatabaseConnection();
        }
        Connection connect = db.connect();
        JSONObject result = new JSONObject();
        try {
            String addUser = "Insert into \"Friends\" (username, friend) values (?, ?)";
            PreparedStatement pstaddUser = connect.prepareStatement(addUser);
            pstaddUser.setString(1, username);
            pstaddUser.setString(2, username);
            pstaddUser.execute();
            pstaddUser.close();
            connect.close();
            result.put("result", "success");

            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            JSONObject response = new JSONObject();
            response.put("result", "fail");
            return response;
        }

    }

}


