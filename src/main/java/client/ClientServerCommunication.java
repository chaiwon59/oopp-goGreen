package client;

import client.models.Car;
import org.json.simple.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class ClientServerCommunication {

    /**
     * This method sends the right data to the Client class and
     * prepares to store a nwe food activity.
     *
     * @param type         the type of meal that you did
     * @param user         the user object that is currently logged in
     * @param localproduce whether you have bought local produce or not
     * @param client       a client object(allows for mockito tests)
     * @return returns true or false based on whether it was successful
     */
    public static boolean addFoodActivity(String type, User user, String localproduce,
                                          Client client, RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("mealtype", type);
        format.put("username", user.getUsername());
        format.put("localproduce", localproduce);
        format.put("password", user.getPassword());
        return client.addFoodActivity(format, restTemplate);

    }

    /**
     * This method sends a JSONObject to the Client class to eventually
     * retrieve all the activities belonging to that user and
     * returns a list containing all the activities.
     *
     * @param user   the current user that is logged in
     * @param client the client(allows for mockito tests)
     * @return returns a list containing all the activities of that user
     */
    public static List<String>[] getUserActivities(User user,
                                                   Client client, RestTemplate resttemplate) {
        JSONObject format = new JSONObject();
        format.put("username", user.getUsername());
        format.put("password", user.getPassword());

        List<String>[] result = client.getUserActivity(format, resttemplate);
        return result;
    }

    /**
     * This method adds a new transport activity to the database
     * and returns a boolean based on whether the activity was added successfully.
     *
     * @param user          the user object that wants to add the activity
     * @param distance      the distance traveled in km
     * @param modelused     the actual model that is being used instead of the rejected model
     *                      (like car, plane, bike etc)
     * @param modelrejected the model that the user decided to not use today
     * @param client        the client that will send the http request (used for mocking tests)
     * @return returns true or false based on whether the activity was added succesfully
     */
    public static boolean addTransportActivity(User user, int distance,
                                               String modelused, String modelrejected,
                                               Client client, RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("username", user.getUsername());
        format.put("modelused", modelused);
        format.put("modelrejected", modelrejected);
        format.put("distance", distance);
        format.put("password", user.getPassword());
        return client.addTransportActivity(format, restTemplate, client);

    }

    /**
     * This method adds a new friend to the database and returns true or false
     * based on whether the friend was added successfully.
     *
     * @param user   the user that wants to add the friend activity
     * @param friend the username of the friend you want to add
     * @param client the cilent that will send the http request (used for mockito)
     * @return returns true or false based on whether the friend was added successfully
     */
    public static boolean addFriend(User user, String friend,
                                    Client client, RestTemplate resttemplate) {
        JSONObject format = new JSONObject();
        format.put("username", user.getUsername());
        format.put("friend", friend);
        format.put("password", user.getPassword());
        return client.addFriend(format, resttemplate);
    }

    /**
     * This method retrieves all the friends along with their scores
     * of a certain user.
     *
     * @param user   the user that wants to retrieve his friends
     * @param client the client that will send the http request(used for mockito)
     * @return returns an array of lists with the first index being all the friend usernames
     *         and the second index being all the scores of the friends
     */
    public static List<String>[] retrieveFriends(User user, Client client,
                                                 RestTemplate resttemplate) {
        JSONObject format = new JSONObject();
        format.put("username", user.getUsername());
        format.put("password", user.getPassword());
        return client.getFriends(format, resttemplate);
    }

    /**
     * This method sends the right data to the Client class and
     * prepares to store a new solar panel activity.
     *
     * @param user   the user object that is currently logged in
     * @param panels how many panels you have installed
     * @param client a client object(allows for mockito tests)
     * @return returns true or false based on whether it was successful
     */

    public static boolean addSolarPanelActivity(User user, String panels,
                                                Client client, RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("username", user.getUsername());
        format.put("panels", panels);
        format.put("password", user.getPassword());

        return client.addSolarPanelActivity(format, restTemplate);

    }


    /**
     * This method creates the JSON object with the username
     * and calls the method that returns user's score.
     *
     * @param username is put in a JSON object to retrieve the score
     * @param client   the client that will send the http request(used for mockito)
     * @return an integer that indicates the user's score
     */
    public static int getScore(String username, Client client, RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("username", username);


        return client.getScore(format, restTemplate);
    }

    /**
     * This method takes the username as input and makes a JSON object of it.
     * The method isUnique is called,
     * this returns a boolean to indicate whether its is unique or not.
     *
     * @param username is the username that should be checked
     * @param client   a client object(allows for mockito tests)
     * @return a boolean that indicates whether the username is unique or not
     */
    public static boolean isUnique(
            String username, Client client, RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("username", username);

        return client.isUnique(format, restTemplate);
    }

    /**
     * This method takes a username as input and returns an integer with the rank.
     *
     * @param username is the input username you want to get the rank of.
     * @param client   a client object(allows for mockito tests)
     * @return returns an integer with the rank
     */
    public static int getUserRank(String username,
                                  Client client, RestTemplate resttemplate) {
        JSONObject format = new JSONObject();
        format.put("username", username);
        return client.getUserRank(format, resttemplate);
    }

    /**
     * This method makes sure that the user is added to the friends
     * so that it could be compared to them.
     *
     * @param username takes a username as input and returns a boolean that
     *                 indicates whether the user has been added as a friend or not
     * @param client   a client object(allows for mockito tests)
     * @return returns a boolean that indicates
     *         whether it has been added succesfully or not.
     */
    public static boolean addUserAsFriend(String username,
                                          Client client, RestTemplate resttemplate) {
        JSONObject format = new JSONObject();
        format.put("username", username);
        return client.addUserAsFriend(format, resttemplate);
    }

    /**
     * This method sends the right data to the Client class and
     * prepares to store a new solar panel activity.
     *
     * @param type        the type of meal that you did
     * @param user        the user object that is currently logged in
     * @param temperature how many panels you have installed
     * @param time        how many panels you have installed
     * @param client      a client object(allows for mockito tests)
     * @return returns true or false based on whether it was successful
     */

    public static boolean addTemperatureActivity(String type, User user, String temperature,
                                                 String time, Client client,
                                                 RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("type", type);
        format.put("username", user.getUsername());
        format.put("temperature", temperature);
        format.put("time", time);
        format.put("password", user.getPassword());


        return client.addTemperatureActivity(format, restTemplate);

    }

    /**
     * This method updates the existing car of the user.
     *
     * @param model     the type model of the car
     * @param user      the user object that is currently logged in
     * @param buildyear the year that the car was built
     * @param brand     the brand of the car
     * @param client    a client object(allows for mockito tests)
     * @return returns true or false based on whether it was successfully updated
     */

    public static boolean updateCar(String model, User user, String buildyear,
                                    String brand, Client client, RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("model", model);
        format.put("buildyear", buildyear);
        format.put("brand", brand);
        format.put("username", user.getUsername());
        format.put("password", user.getPassword());

        return client.updateCar(format, restTemplate);
    }


    /**
     * This method adds a new car to the user.
     *
     * @param model     the type model of the car
     * @param user      the user object that is currently logged in
     * @param buildyear the year that the car was built
     * @param brand     the brand of the car
     * @param client    a client object(allows for mockito tests)
     * @return returns true or false based on whether it was successfully updated
     */
    public static boolean addCar(String model, User user, String buildyear,
                                 String brand, Client client, RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("model", model);
        format.put("buildyear", buildyear);
        format.put("brand", brand);
        format.put("username", user.getUsername());
        format.put("password", user.getPassword());

        return client.addCar(format, restTemplate);
    }

    /**
     * This method gets the car of the user.
     *
     * @param user   the user object that is currently logged i
     * @param client a client object(allows for mockito tests)
     * @return returns the car of the user
     */
    public static Car getCar(User user, Client client, RestTemplate restTemplate) {
        JSONObject format = new JSONObject();
        format.put("username", user.getUsername());
        format.put("password", user.getPassword());

        return client.getCar(format, restTemplate);
    }
}
