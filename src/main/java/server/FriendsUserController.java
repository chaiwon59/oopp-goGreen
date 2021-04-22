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
import java.util.ArrayList;

@RestController
public class FriendsUserController {
    /**
     *This method takes a JSONobject with the username and returns a JSONobject with the rank.
     * @param input the JSONobject with the username you want to get the rank of
     * @param db the database connection
     * @return returns a JSONobject with the rank and the
     * @throws SQLException when an error with the database occurs
     */
    @PostMapping("/getUserRank")
    public JSONObject getUserRank(@RequestBody JSONObject input,
                                  @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {

        String username = (String) input.get("username");


        Connection connect;

        if (db == null) {
            DatabaseConnection db2 = new DatabaseConnection();
            connect = db2.connect();

        } else {
            connect = db.connect();

        }

        String getfriendsQuery = "SELECT friend FROM \"Friends\", \"UserTable\""
                + " WHERE \"Friends\".friend = \"UserTable\".username"
                + " AND \"Friends\".username = ?"
                + "ORDER BY score DESC";

        PreparedStatement pst = connect.prepareStatement(getfriendsQuery);
        pst.setString(1, username);

        ResultSet result = pst.executeQuery();
        ArrayList<String> resultlist = new ArrayList<>();


        JSONObject userRank = new JSONObject();
        int count = 0;
        boolean found = false;
        while (result.next()) {
            resultlist.add(result.getString(1));
        }


        while (count < resultlist.size() && found == false) {
            if (resultlist.get(count).equals(username)) {
                found = true;
            }
            count++;

        }


        connect.close();
        if (found == true) {
            userRank.put("rank", count);
            return userRank;
        } else {
            userRank.put("rank", -1);
            return userRank;
        }


    }
}
