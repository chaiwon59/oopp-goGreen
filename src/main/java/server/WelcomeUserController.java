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
public class WelcomeUserController {


    /**
     * This method takes a JSON object with the username as input
     * and retrieves the user's score from the database.
     * It returns the username and the score in a JSON object.
     *
     * @param input a JSON object with the username you want the score to be retrieved of
     * @param db    the database connection to retrieve the score from
     * @return the user's score from the database is returned as a JSON object
     * @throws SQLException is thrown whenever a database error occurs
     */
    @PostMapping("/getScore")
    public JSONObject getScore(@RequestBody JSONObject input,
                               @RequestParam(required = false)
                                       DatabaseConnection db) throws SQLException {

        String username1 = (String) input.get("username");
        if (db == null) {
            db = new DatabaseConnection();
        }
        Connection connect = db.connect();

        try {
            String score = "SELECT \"score\" FROM \"UserTable\" where \"username\" = ? ";
            PreparedStatement pst = connect.prepareStatement(score);
            pst.setString(1, username1);
            ResultSet score1 = pst.executeQuery();

            JSONObject jsonobject = new JSONObject();
            String result = "";
            if (score1.next()) {
                result = score1.getString(1);
                jsonobject.put("score", result);
                pst.close();
                return jsonobject;
            } else {
                jsonobject.put("score", "-1");
                pst.close();
                return jsonobject;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JSONObject jsonobject = new JSONObject();
            jsonobject.put("score", -1);
            return jsonobject;
        }
    }

}

