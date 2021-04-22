package server;

import static org.springframework.security.crypto.bcrypt.BCrypt.gensalt;
import static org.springframework.security.crypto.bcrypt.BCrypt.hashpw;

import client.models.HouseTemp;
import client.models.SolarPanel;
import client.models.VegetarianMeal;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.postgresql.util.PSQLException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


@RestController
public class ServerController {

    /**
     * This method takes an json object and model as input and returns the json object
     * received by the third party api.
     *
     * @param input the json input object
     * @param model the model type e.g. flights, cartrips etc
     * @return returns the json response gotten by the server
     */
    @PostMapping("/getco2/{model}")
    public JSONObject getCo2(@RequestBody JSONObject input, @PathVariable String model) {
        //initialize the client that will sent the HTTP request
        RestTemplate restTemplate = new RestTemplate();

        //execute the POST request to the given url
        JSONObject jsonresponse = restTemplate.postForObject("http://impact.brighterplanet.com/"
                + model
                + ".json", input, JSONObject.class);

        return jsonresponse;
    }

    /**
     * This method is supposed to handle post requests that come in with the username and
     * password and check whether the user entered the right password.
     *
     * @param input a JSONObject with the username and password
     * @return returns a JSONObject boolean
     */
    @PostMapping("/authenticate")
    public JSONObject authenticate(@RequestBody JSONObject input,
                                   @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        String username = (String) input.get("username");
        String inputPassword = (String) input.get("password");

        Connection connect;
        if (db == null) {
            DatabaseConnection db2 = new DatabaseConnection();
            connect = db2.connect();
        } else {
            connect = db.connect();
        }

        //Get password and salt to the corresponding username
        String getPasswordQuery = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
        PreparedStatement pwQuery = connect.prepareStatement(getPasswordQuery);
        pwQuery.setString(1, username);
        ResultSet retPassword = pwQuery.executeQuery();

        String getSaltQuery = "SELECT \"salt\" FROM \"UserTable\" WHERE \"username\" = ?";
        PreparedStatement saltQuery = connect.prepareStatement(getSaltQuery);
        saltQuery.setString(1, username);
        ResultSet retSalt = saltQuery.executeQuery();
        String password;
        String salt;

        JSONObject jsonResponse = new JSONObject();
        if (retPassword.next() && retSalt.next()) {
            password = retPassword.getString(1);
            salt = retSalt.getString(1);
            if (hashpw(inputPassword, salt).equals(password)) {
                jsonResponse.put("result", "access granted");
                connect.close();
                return jsonResponse;
            }
        }
        connect.close();
        jsonResponse.put("result", "access denied");
        return jsonResponse;
    }

    /**
     * This method retrieves all the activities when receiving a POST request,
     * with the a user id and then sends them back in a string.
     *
     * @param input a JSON object containing the user id
     * @return returns an array of strings, with the first index containing all the categories,
     *         second index containing all the activities and third index
     *         containing all the co2 saved
     */
    @PostMapping("/getUserActivities")
    public String[] getUserActivities(@RequestBody JSONObject input,
                                      @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        String password = (String) input.get("password");
        String username = (String) input.get("username");

        JSONObject authentication = new JSONObject();
        authentication.put("username", username);
        authentication.put("password", password);
        String authenticateresult;
        Connection connect;
        if (db == null) {
            db = new DatabaseConnection();
            connect = db.connect();
            authenticateresult = (String) authenticate(authentication, db).get("result");
        } else {
            connect = db.connect();
            authenticateresult = (String) authenticate(authentication, db).get("result");
        }
        if (authenticateresult.equals("access granted")) {

            String getActivitiesQuery = "SELECT * FROM  \"UserActivities\" WHERE username = ?";
            PreparedStatement foodquery = connect.prepareStatement(getActivitiesQuery);
            foodquery.setString(1, username);

            ArrayList<String> resultlist = new ArrayList<>();

            JSONArray jsonactivitiesArray = new JSONArray();
            JSONArray jsoncategoriesArray = new JSONArray();
            JSONArray jsonco2savedArray = new JSONArray();
            ResultSet activities = foodquery.executeQuery();
            while (activities.next()) {

                jsonactivitiesArray.put(activities.getString("activity"));
                if (activities.getString("activity").contains("meal")) {
                    jsoncategoriesArray.put("Food");
                } else if (activities.getString("activity").contains("trip")) {
                    jsoncategoriesArray.put("Transport");
                } else if (activities.getString("activity").contains("Temperature")
                        || (activities.getString("activity").contains("Solar"))) {
                    jsoncategoriesArray.put("Energy");
                }
                jsonco2savedArray.put(activities.getString("co2_saved"));
            }
            connect.close();


            JSONObject jsonresultactivities = new JSONObject();
            jsonresultactivities.put("activities", jsonactivitiesArray);
            JSONObject jsonresultcategories = new JSONObject();
            jsonresultcategories.put("activities", jsoncategoriesArray);
            JSONObject jsonresultco2saved = new JSONObject();
            jsonresultco2saved.put("activities", jsonco2savedArray);

            String[] result = new String[3];
            result[0] = jsonresultcategories.toJSONString()
                    .substring(15, jsonresultcategories.toJSONString().length() - 2);
            result[1] = jsonresultactivities.toJSONString()
                    .substring(15, jsonresultactivities.toJSONString().length() - 2);
            result[2] = jsonresultco2saved.toJSONString()
                    .substring(15, jsonresultco2saved.toJSONString().length() - 2);
            return result;
        }
        return null;
    }

    /**
     * This method will add the received transport activity to the database,
     * and updates the total score of the user.
     *
     * @param input the input data containing:
     *              - The model that is being used (like car, plane, etc)
     *              - The model that is being rejected (like car, plane etc)
     *              - The username of the user
     *              - The password of the user
     *              - The distance travelled
     *              - The amount of co2 that was saved
     * @return returns a JSONObject with a 'result' key that will either be 'fail' or 'success'
     * @throws SQLException is thrown whenever a database error occurs
     */
    @PostMapping("/addTransportActivity")
    public JSONObject addTransportActivity(@RequestBody JSONObject input,
                                           @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        String password = (String) input.get("password");
        String username = (String) input.get("username");
        JSONObject authentication = new JSONObject();
        authentication.put("username", username);
        authentication.put("password", password);

        Connection connect;
        String authenticateresult;
        if (db == null) {
            DatabaseConnection db2 = new DatabaseConnection();
            connect = db2.connect();
            authenticateresult = (String) authenticate(authentication, db2).get("result");
        } else {
            connect = db.connect();
            authenticateresult = (String) authenticate(authentication, db).get("result");
        }
        if (authenticateresult.equals("access granted")) {
            String activity;
            switch ((String) input.get("modelused")) {
                case "flights":
                    activity = "Plane trip";
                    break;
                case "automobile_trips":
                    activity = "Car trip";
                    break;
                case "bus_trips":
                    activity = "Bus trip";
                    break;
                default:
                    activity = "bike trip";
            }


            switch ((String) input.get("modelrejected")) {
                case "flights":
                    activity += " instead of Plane trip";
                    break;
                case "automobile_trips":
                    activity += " instead of Car trip";
                    break;
                case "bus_trips":
                    activity += " instead of Bus trip";
                    break;
                default:
                    activity = " instead of bike trip";
            }

            //this part has to do with when testing you cannot use (int) and when
            //you run the application you cannot use valueOf(), so just do a try catch
            int distance;
            try {
                distance = (int) input.get("distance");
            } catch (ClassCastException e) {
                distance = Integer.valueOf((String) input.get("distance"));
            }

            String distancestring = Integer.toString(distance);
            activity = distancestring + " km " + activity;

            //store in database
            int co2saved = (int) input.get("co2_saved");
            String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", "
                    + "\"co2_saved\")" + "VALUES ( ?, ?, ?);";


            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, activity);
            pst.setInt(3, co2saved);
            pst.execute();
            pst.close();

            //update total score
            String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ? "
                    + "WHERE username = ?";


            PreparedStatement pst2 = connect.prepareStatement(totalscorequery);
            pst2.setString(2, username);
            pst2.setInt(1, co2saved);
            pst2.execute();
            pst2.close();
            connect.close();

            JSONObject jsonresponse = new JSONObject();
            jsonresponse.put("result", "success");
            return jsonresponse;
        }
        JSONObject response = new JSONObject();
        response.put("result", "fail");
        return response;
    }

    /**
     * This method adds a food activity to the database and returns
     * true or false based on whether it was added successfully or not.
     *
     * @param input The input JSON format containing some specification
     *              variables like the type of meal
     * @return returns true or false based on whether it was succesfull
     */
    @PostMapping("/addFoodActivity")
    public JSONObject addFoodActivity(@RequestBody JSONObject input,
                                      @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        String password = (String) input.get("password");
        String username = (String) input.get("username");
        JSONObject authentication = new JSONObject();
        authentication.put("username", username);
        authentication.put("password", password);

        String authenticateresult;
        Connection connect;
        if (db == null) {
            DatabaseConnection db2 = new DatabaseConnection();
            connect = db2.connect();
            authenticateresult = (String) authenticate(authentication, db2).get("result");
        } else {
            connect = db.connect();
            authenticateresult = (String) authenticate(authentication, db).get("result");
        }

        if (authenticateresult.equals("access granted")) {

            VegetarianMeal meal = new VegetarianMeal((String) input.get("username"));
            meal.setLocalProduce((String) input.get("localproduce"));
            meal.setDiet((String) input.get("mealtype"));

            String getFoodCO2query = "SELECT co2_saved FROM \"Food\" WHERE diet = ?";

            String mealtype = (String) input.get("mealtype");

            PreparedStatement foodquery = connect.prepareStatement(getFoodCO2query);
            foodquery.setString(1, mealtype);

            ResultSet foodco2 = foodquery.executeQuery();

            int co2saved = 0;
            if (foodco2.next()) {
                co2saved = foodco2.getInt(1);
            }

            String localproduce = (String) input.get("localproduce");
            if (localproduce.equals("Yes")) {
                double temp = co2saved / 1.1;
                co2saved = (int) temp;
            }

            String activity = meal.getDiet();
            String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                    + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";

            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(1, meal.getUserId());
            pst.setString(2, activity + " meal");
            pst.setDouble(3, (int) meal.getSaved());
            pst.execute();
            pst.close();

            //update total score
            String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                    + " WHERE username = ?";

            PreparedStatement pst2 = connect.prepareStatement(totalscorequery);
            pst2.setString(2, username);
            pst2.setInt(1, (int) meal.getSaved());
            pst2.execute();
            pst2.close();
            connect.close();

            JSONObject jsonresponse = new JSONObject();
            jsonresponse.put("result", "success");
            return jsonresponse;
        }
        JSONObject response = new JSONObject();
        response.put("result", "fail");
        return response;
    }


    /**
     * This method can be used to retrieve the name of a user.
     *
     * @param input is the "username" key with value, in JSON format so that the database
     *              can return the corresponding name for that username.
     * @return the JSON object containing the corresponding name to the username.
     */
    @PostMapping("/getName")
    public JSONObject getName(@RequestBody JSONObject input, @RequestParam(required = false)
            DatabaseConnection db) throws SQLException {
        if (db == null) {
            db = new DatabaseConnection();
        }
        Connection connect = db.connect();

        String username = (String) input.get("username");

        String query = "SELECT \"name\" FROM \"UserTable\" WHERE \"username\" = ?";
        PreparedStatement pst = connect.prepareStatement(query);
        pst.setString(1, username);
        ResultSet name = pst.executeQuery();

        String result = "";
        if (name.next()) {
            result = name.getString(1);
        }
        pst.close();
        connect.close();
        JSONObject jsonresult = new JSONObject();
        jsonresult.put("name", result);
        return jsonresult;
    }

    /**
     * This method can be used to retrieve all the users except for
     * the friends of the user that requested this.
     *
     * @param input is the "username" key with value, in JSON format so that the
     *              database can return the corresponding name for that username.
     * @return the JSON object containing the corresponding name to the username.
     */
    @PostMapping("/getPassword")
    public JSONObject getPassword(@RequestBody JSONObject input, @RequestParam(required = false)
            DatabaseConnection db) throws SQLException {
        if (db == null) {
            db = new DatabaseConnection();
        }
        Connection connect = db.connect();

        String username = (String) input.get("username");

        String query = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
        PreparedStatement pst = connect.prepareStatement(query);
        pst.setString(1, username);
        ResultSet password = pst.executeQuery();

        String result = "";
        if (password.next()) {
            result = password.getString(1);
        }
        pst.close();
        connect.close();
        JSONObject jsonresult = new JSONObject();
        jsonresult.put("password", result);
        return jsonresult;
    }

    /**
     * This method can be used to change the users password. Inside the method the
     * authenticate method is used as well.
     * The new password is hashed with a new salt so this method also updates the old salt.
     *
     * @param input is the "username" key with value, in JSON format so that the database
     *              can return the corresponding name for that username.
     * @return the JSON object containing the corresponding name to the username.
     */
    @PostMapping("/changePassword")
    public JSONObject changePassword(@RequestBody JSONObject input, @RequestParam(required = false)
            DatabaseConnection db) throws SQLException {
        JSONObject jsonresult = new JSONObject();
        //authenticate user
        if (db == null) {
            db = new DatabaseConnection();
        }
        if (authenticate(input, db).get("result").equals("access denied")) {
            jsonresult.put("result", "access denied");
            return jsonresult;
        }


        Connection connect = db.connect();

        String username = (String) input.get("username");
        String newSalt = gensalt();

        String query1 = "UPDATE \"UserTable\" "
                + "SET \"salt\" = ?"
                + "WHERE \"username\" = ?";
        PreparedStatement pst = connect.prepareStatement(query1);
        pst.setString(1, newSalt);
        pst.setString(2, username);
        pst.execute();
        pst.close();

        String query2 = "UPDATE \"UserTable\" "
                + "SET \"password\" = ?"
                + "WHERE \"username\" = ?";

        String newPassword = (String) input.get("newPassword");

        PreparedStatement pst2 = connect.prepareStatement(query2);
        pst2.setString(1, hashpw(newPassword, newSalt));
        pst2.setString(2, username);
        pst2.execute();
        pst2.close();
        connect.close();
        jsonresult.put("result", "success");
        return jsonresult;
    }

    /**
     * This method adds a user to the database and returns true or
     * false based on whether it was added successfully or not.
     * In this method the password is also hashed and a salt is assigned to the new user.
     *
     * @param input user object in JSON format (username, password, name)
     * @return returns true or false based on whether it was successful
     */
    @PostMapping("/createUser")
    public JSONObject createUser(@RequestBody JSONObject input,
                                 @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        if (db == null) {
            db = new DatabaseConnection();
        }
        Connection connect = db.connect();

        String username = (String) input.get("username");
        String salt = gensalt(); //method from BCrypt
        String password = hashpw((String) input.get("password"), salt); //method from BCrypt
        String name = (String) input.get("name");

        String query = "INSERT INTO \"UserTable\"(\"username\", \"password\",\"score\", "
                + "\"name\", \"salt\")"
                + "VALUES ( ?, ?, 0, ?, ?);";

        PreparedStatement pst = connect.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, password);
        pst.setString(3, name);
        pst.setString(4, salt);
        pst.execute();
        pst.close();
        connect.close();

        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "success");
        return jsonresponse;
    }

    /**
     * This method deletes a user to the database and returns true or
     * false based on whether it was deleted successfully or not.
     *
     * @param input username in JSON format
     * @return JSONObject returns ("result", "success") if succeeded
     */
    @PostMapping("/deleteUser")
    public JSONObject deleteUser(@RequestBody JSONObject input,
                                 @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        if (db == null) {
            db = new DatabaseConnection();
        }
        Connection connect = db.connect();

        String username = (String) input.get("username");

        String query = "BEGIN;"
                + "DELETE FROM \"UserTable\" WHERE \"username\" = " + "?" + ";"
                + "DELETE FROM \"UserActivities\" WHERE \"username\" = ?; "
                + "DELETE FROM \"Friends\" WHERE \"username\" = ? or friend = ? ;"
                + "COMMIT;";

        PreparedStatement pst = connect.prepareStatement(query);
        pst.setString(1, username);
        pst.setString(2, username);
        pst.setString(3, username);
        pst.setString(4, username);
        pst.execute();
        pst.close();
        connect.close();

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("result", "success");
        return jsonResponse;
    }

    /**
     * This method adds a new friend to the database.
     *
     * @param input a JSONObject containing the following:
     *              - the username of the user
     *              - the password of the user
     *              - the username of the friend you want to add
     * @return returns a JSONObject containing 'success' or 'fail' based on whether the
     *         friend was added successfully
     * @throws SQLException is thrown whenever something with the database went wrong
     */
    @PostMapping("/addFriend")
    public JSONObject addFriend(@RequestBody JSONObject input,
                                @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        String password = (String) input.get("password");
        String username = (String) input.get("username");
        JSONObject authentication = new JSONObject();
        authentication.put("username", username);
        authentication.put("password", password);


        String authenticateresult;
        Connection connect;
        if (db == null) {
            DatabaseConnection db2 = new DatabaseConnection();
            connect = db2.connect();
            authenticateresult = (String) authenticate(authentication, db2).get("result");
        } else {
            connect = db.connect();
            authenticateresult = (String) authenticate(authentication, db).get("result");
        }

        if (authenticateresult.equals("access granted")) {
            String friendusername = (String) input.get("friend");

            String testFriendExists = "SELECT \"username\" FROM \"UserTable\" "
                    + "WHERE \"username\" = ?";

            //check whether the entered username exists
            PreparedStatement pst = connect.prepareStatement(testFriendExists);
            pst.setString(1, friendusername);
            ResultSet friendExist = pst.executeQuery();

            JSONObject result = new JSONObject();
            if (!(friendExist.next()) || username.equals(friendusername)) {
                result.put("result", "fail");
                return result;
            }


            //if (!(friendExist.toString().equals(username))) {
            String query = "INSERT INTO \"Friends\"(username, friend) VALUES (?, ?)";

            PreparedStatement pst2 = connect.prepareStatement(query);
            pst2.setString(1, username);
            pst2.setString(2, friendusername);
            pst2.execute();
            connect.close();
            result.put("result", "success");

            return result;
        }
        //  }
        JSONObject response = new JSONObject();
        response.put("result", "fail");
        return response;
    }

    /**
     * This method retrieves all the friends of a certain user and returns an
     * array of string containing their usernames and score.
     *
     * @param input the input JSONObject containing the username and password of the user
     * @return returns an array of strings, with the first index being the usernames
     *         in one big string separated by comma's, the second index being the
     *         scores separated by comma's aswell
     * @throws SQLException is thrown whenever something with the database goes wrong
     */
    @PostMapping("/getFriends")
    public String[] getFriends(@RequestBody JSONObject input,
                               @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        String password = (String) input.get("password");
        String username = (String) input.get("username");
        JSONObject authentication = new JSONObject();
        authentication.put("username", username);
        authentication.put("password", password);

        String authenticateresult;
        Connection connect;
        if (db == null) {
            DatabaseConnection db2 = new DatabaseConnection();
            connect = db2.connect();
            authenticateresult = (String) authenticate(authentication, db2).get("result");
        } else {
            connect = db.connect();
            authenticateresult = (String) authenticate(authentication, db).get("result");
        }
        if (authenticateresult.equals("access granted")) {

            String getfriendsQuery = "SELECT friend, score FROM \"Friends\", \"UserTable\""
                    + " WHERE \"Friends\".friend = \"UserTable\".username"
                    + " AND \"Friends\".username = ?"
                    + "ORDER BY score DESC";

            PreparedStatement pst = connect.prepareStatement(getfriendsQuery);
            pst.setString(1, username);

            ResultSet result = pst.executeQuery();
            ArrayList<String> resultlist = new ArrayList<>();

            JSONArray jsonfriendsusernameArray = new JSONArray();
            JSONArray jsonfriendsscoreArray = new JSONArray();

            while (result.next()) {
                jsonfriendsusernameArray.put(result.getString("friend"));
                jsonfriendsscoreArray.put(result.getString("score"));
            }
            connect.close();


            JSONObject jsonresultFriendUsernames = new JSONObject();
            jsonresultFriendUsernames.put("usernames", jsonfriendsusernameArray);
            JSONObject jsonresultFriendscore = new JSONObject();
            jsonresultFriendscore.put("scores", jsonfriendsscoreArray);

            System.out.println(jsonresultFriendscore.toJSONString());
            System.out.println(jsonresultFriendUsernames.toJSONString());

            String[] resultarray = new String[2];
            resultarray[0] = jsonresultFriendUsernames.toJSONString()
                    .substring(14, jsonresultFriendUsernames.toJSONString().length() - 2);
            resultarray[1] = jsonresultFriendscore.toJSONString()
                    .substring(11, jsonresultFriendscore.toJSONString().length() - 2);

            return resultarray;
        }
        return null;
    }

    /**
     * This method takes a JSON object as input with the username
     * and the number of solar panels, calculates the amount of co2 saved
     * and returns a JSON object that indicates that the activity has been added.
     *
     * @param input a JSON object with the username and the number of solarpanels
     * @return a JSON object that indicates that the activity has been added
     * @throws SQLException is thrown whenever a database error occurs
     */
    @PostMapping("/addSolarPanelActivity")
    public JSONObject addSolarPanelActivity(@RequestBody JSONObject input,
                                            @RequestParam(required = false)
                                                    DatabaseConnection db) throws SQLException {

        String password = (String) input.get("password");
        String username = (String) input.get("username");
        JSONObject authentication = new JSONObject();
        authentication.put("username", username);
        authentication.put("password", password);

        String authenticateresult;
        Connection connect;
        if (db == null) {
            DatabaseConnection db2 = new DatabaseConnection();
            connect = db2.connect();
            authenticateresult = (String) authenticate(authentication, db2).get("result");
        } else {
            connect = db.connect();
            authenticateresult = (String) authenticate(authentication, db).get("result");
        }

        if (authenticateresult.equals("access granted")) {

            SolarPanel panel = new SolarPanel((String) input.get("username"),
                    (String) input.get("panels"));

            String query = "INSERT INTO \"UserActivities\"(\"username\","
                    + " \"activity\", \"co2_saved\")"
                    + "VALUES ( ?, ?, ?);";

            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(1, panel.getUserId());
            pst.setString(2, panel.getNumberOfPanels() + " Solar panels");
            pst.setDouble(3, panel.getSaved());
            pst.execute();
            pst.close();

            //update total score
            String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ? "
                    + "WHERE username = ?";


            PreparedStatement pst2 = connect.prepareStatement(totalscorequery);
            pst2.setString(2, username);
            pst2.setDouble(1, panel.getSaved());
            pst2.execute();
            pst2.close();
            connect.close();

            JSONObject jsonresponse = new JSONObject();
            jsonresponse.put("result", "success");
            return jsonresponse;

        }
        JSONObject response = new JSONObject();
        response.put("result", "fail");
        return response;
    }

    /**
     * This method takes a JSON object as input with the username
     * and the number of solar panels, calculates the amount of co2 saved
     * and returns a JSON object that indicates that the activity has been added.
     *
     * @param input a JSON object with the username and the number of solarpanels
     * @return a JSON object that indicates that the activity has been added
     * @throws SQLException is thrown whenever a database error occurs
     */
    @PostMapping("/addTemperatureActivity")
    public JSONObject addTemperatureActivity(@RequestBody JSONObject input,
                                             @RequestParam(required = false)
                                                     DatabaseConnection db) throws SQLException {
        String password = (String) input.get("password");
        String username = (String) input.get("username");
        JSONObject authentication = new JSONObject();
        authentication.put("username", username);
        authentication.put("password", password);

        String authenticateresult;
        Connection connect;
        if (db == null) {
            DatabaseConnection db2 = new DatabaseConnection();
            connect = db2.connect();
            authenticateresult = (String) authenticate(authentication, db2).get("result");
        } else {
            connect = db.connect();
            authenticateresult = (String) authenticate(authentication, db).get("result");
        }

        if (authenticateresult.equals("access granted")) {

            HouseTemp temp = new HouseTemp((String) input.get("username"));
            temp.setDuration(Integer.parseInt((String) input.get("temperature")));
            temp.setLower(Integer.parseInt((String) input.get("time")));


            String query = "INSERT INTO \"UserActivities\"(\"username\","
                    + " \"activity\", \"co2_saved\")"
                    + "VALUES ( ?, ?, ?);";

            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(1, temp.getUserId());
            pst.setString(2, "Lowering Temperature");
            pst.setDouble(3, temp.getSaved());
            pst.execute();
            pst.close();

            //update total score
            String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ? "
                    + "WHERE username = ?";


            PreparedStatement pst2 = connect.prepareStatement(totalscorequery);
            pst2.setString(2, username);
            pst2.setDouble(1, temp.getSaved());
            pst2.execute();
            pst2.close();
            connect.close();

            JSONObject jsonresponse = new JSONObject();
            jsonresponse.put("result", "success");
            return jsonresponse;
        }
        JSONObject response = new JSONObject();
        response.put("result", "fail");
        return response;
    }

    /**
     * This method updates the existing car of a user with the new values recieved in the
     * JSONObject.
     *
     * @param input The data needed to store a car:
     *              - The brand
     *              - The model
     *              - The build year
     *              - The username
     * @param db    a database connection (used for mocking)
     * @return returns a jsonobject containing success or fail, based on whether it was updated
     *         successfully
     */
    @PostMapping("/updateCar")
    public JSONObject updateCar(@RequestBody JSONObject input,
                                @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        if (db == null) {
            db = new DatabaseConnection();
        }


        JSONObject authentication = new JSONObject();
        String username = (String) input.get("username");
        String password = (String) input.get("password");

        authentication.put("username", username);
        authentication.put("password", password);

        String authenticateresult;
        authenticateresult = (String) authenticate(authentication, db).get("result");
        if (authenticateresult.equals("access granted")) {

            Connection connect = db.connect();
            String checkifexists = "SELECT username FROM \"UserCar\" WHERE username = ?";
            PreparedStatement checkpst = connect.prepareStatement(checkifexists);
            checkpst.setString(1, username);
            ResultSet exists = checkpst.executeQuery();
            if (!exists.next()) {
                JSONObject response = new JSONObject();
                response.put("result", "fail");
                return response;
            }

            String query = "UPDATE \"UserCar\" SET \"carBrand\" = ?, \"carBuildYear\" = ?,"
                    + " \"carModel\" = ? WHERE \"username\" = ?";

            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(1, (String) input.get("brand"));
            pst.setString(2, (String) input.get("buildyear"));
            pst.setString(3, (String) input.get("model"));
            pst.setString(4, (String) input.get("username"));

            pst.execute();
            pst.close();
            connect.close();

            JSONObject jsonresponse = new JSONObject();
            jsonresponse.put("result", "success");
            return jsonresponse;

        }
        JSONObject response = new JSONObject();
        response.put("result", "fail");
        return response;
    }

    /**
     * This method updates the existing car of a user with the new values recieved in the
     * JSONObject.
     *
     * @param input The data needed to store a car:
     *              - The brand
     *              - The model
     *              - The build year
     *              - The username
     * @param db    a database connection (used for mocking)
     * @return returns a jsonobject containing success or fail, based on whether it was updated
     *         successfully
     */
    @PostMapping("/addCar")
    public JSONObject addCar(@RequestBody JSONObject input,
                             @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        if (db == null) {
            db = new DatabaseConnection();
        }

        Connection connect = db.connect();
        JSONObject authentication = new JSONObject();
        String username = (String) input.get("username");
        String password = (String) input.get("password");

        authentication.put("username", username);
        authentication.put("password", password);
        String authenticateresult;
        authenticateresult = (String) authenticate(authentication, db).get("result");

        if (authenticateresult.equals("access granted")) {

            String query = "INSERT INTO \"UserCar\"(\"username\", \"carBrand\", "
                    + "\"carBuildYear\", \"carModel\")"
                    + " VALUES (?,?,?,?)";

            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(2, (String) input.get("brand"));
            pst.setString(3, (String) input.get("buildyear"));
            pst.setString(4, (String) input.get("model"));
            pst.setString(1, (String) input.get("username"));
            try {
                pst.execute();
            } catch (PSQLException e) {
                JSONObject response = new JSONObject();
                response.put("result", "fail");
                return response;
            }

            pst.close();
            connect.close();

            JSONObject jsonresponse = new JSONObject();
            jsonresponse.put("result", "success");
            return jsonresponse;

        }
        JSONObject response = new JSONObject();
        response.put("result", "fail");
        return response;
    }

    /**
     * This method gets the existing car of a user.
     *
     * @param input The data needed to authenticate:
     *              - The username
     *              - The password
     * @param db    a database connection (used for mocking)
     * @return returns a jsonobject containing the data of the car
     */
    @PostMapping("/getCar")
    public JSONObject getCar(@RequestBody JSONObject input,
                             @RequestParam(required = false) DatabaseConnection db)
            throws SQLException {
        if (db == null) {
            db = new DatabaseConnection();
        }

        Connection connect = db.connect();
        JSONObject authentication = new JSONObject();
        String username = (String) input.get("username");
        String password = (String) input.get("password");

        authentication.put("username", username);
        authentication.put("password", password);
        String authenticateresult;
        authenticateresult = (String) authenticate(authentication, db).get("result");

        if (authenticateresult.equals("access granted")) {

            String query = "SELECT \"carBuildYear\", \"carBrand\", \"carModel\""
                    + " FROM \"UserCar\" WHERE username = ?";

            PreparedStatement pst = connect.prepareStatement(query);
            pst.setString(1, (String) input.get("username"));

            ResultSet result = pst.executeQuery();

            if (!result.next()) {
                JSONObject response = new JSONObject();
                response.put("result", "fail");
                return response;
            }




            String model = result.getString(3);
            String year = result.getString(1);
            String brand = result.getString(2);

            JSONObject jsonresponse = new JSONObject();
            jsonresponse.put("result", "success");
            jsonresponse.put("model", model);
            jsonresponse.put("year", year);
            jsonresponse.put("brand", brand);

            pst.close();
            connect.close();
            return jsonresponse;

        }
        JSONObject response = new JSONObject();
        response.put("result", "fail");
        return response;
    }
}
