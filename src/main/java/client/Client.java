package client;

import client.models.Car;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class Client {

    private static final Logger log = LoggerFactory.getLogger(Client.class);

    @Autowired
    private RestTemplate restTemplate;

    /**
     * This method gets the amount of co2 associated with a certain activity.
     *
     * @param format this parameter is the JSON format request for a certain activity
     * @param model  this specifies the model type (flights, car trips etc)
     * @return returns an String containing the right value of co2 that it produces/saves
     */
    public String getCO2(JSONObject format, String model) {

        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();

        //execute the POST request to the given url
        JSONObject response = restTemplate.postForObject("http://localhost:8080/getco2/"
                + model, entity, JSONObject.class);


        //extract wanted date from the received response
        //when invoking the get method a linkedhashmap is returned for some reason,
        //thats why we are using it here
        LinkedHashMap temp = (LinkedHashMap) response.get("decisions");
        LinkedHashMap temp2 = (LinkedHashMap) temp.get("carbon");
        Collection resultcollection = temp2.values();
        Iterator resultstring = resultcollection.iterator();
        String result = (String) resultstring.next();
        return result.substring(0, result.length() - 3);
    }

    /**
     * This method takes the username as an input and then sends a request to the server to
     * retrieve all the activities belonging to that user, along with the corresponding
     * categories and score.
     *
     * @param username The username of that user Stored in JSON format
     * @return returns an array of lists, with the first index containing all the categories of
     *         the activites, the second index containing all the activity descriptions and
     *         the third index containing all the scores associated with the activities
     */
    public List<String>[] getUserActivity(JSONObject username, RestTemplate restTemplate) {

        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(username);

        //send POST request
        //execute the POST request to the given url
        String[] jsonresponse = restTemplate.postForObject("http://localhost:8080/"
                + "getUserActivities", entity, String[].class);

        List<String> activitylist;
        List<String> categorylist;
        List<String> co2savedlist;

        List<String>[] resultarray = new List[3];
        if (jsonresponse != null && jsonresponse[0] != null
                && jsonresponse.length != 0
                && jsonresponse[0].length() != 0) {
            activitylist = Arrays.asList(jsonresponse[1].split("\",\""));
            categorylist = Arrays.asList(jsonresponse[0].split("\",\""));
            co2savedlist = Arrays.asList(jsonresponse[2].split("\",\""));

            //remove parentheses or " at the start
            activitylist.set(0, activitylist.get(0).substring(1));
            categorylist.set(0, categorylist.get(0).substring(1));
            co2savedlist.set(0, co2savedlist.get(0).substring(1));

            //remove " at the end

            activitylist.set(activitylist.size() - 1, activitylist.get(activitylist.size() - 1)
                    .substring(0, activitylist.get(activitylist.size() - 1).length() - 1));
            categorylist.set(categorylist.size() - 1, categorylist.get(categorylist.size() - 1)
                    .substring(0, categorylist.get(categorylist.size() - 1).length() - 1));
            co2savedlist.set(co2savedlist.size() - 1, co2savedlist.get(co2savedlist.size() - 1)
                    .substring(0, co2savedlist.get(co2savedlist.size() - 1).length() - 1));

            //insert into array
            resultarray[0] = categorylist;
            resultarray[1] = activitylist;
            resultarray[2] = co2savedlist;

        }
        return resultarray;

    }

    /**
     * This method adds a new food activity to the database by sending a JSONObject
     * containing all the data to the server.
     *
     * @param format JSONObject with mealtype, localproduce, username and password
     * @return returns true or false whetehr the activity was added succesfully
     */
    public boolean addFoodActivity(JSONObject format, RestTemplate resttemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        //send POST request
        //initialize the client that will sent the HTTP request
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/addFoodActivity/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method checks whether the user has filled in a valid username password combination.
     *
     * @param userLogin the username and password in JSON object format
     * @return returns false if failed, else returns success.
     */
    public boolean authenticate(JSONObject userLogin, RestTemplate restTemplate) {
        //send request to the server url /authenticate and handle response
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(userLogin);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = restTemplate.postForObject("http://localhost:8080/authenticate/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("access granted");
    }

    /**
     * This method is used to retrieve the name corresponding to a certain username. The
     * username is sent to the server, the server is connected with the database.
     *
     * @param username is the "username" key with value, in JSON format so that the database
     *                 can return the corresponding name for that username.
     * @return the JSON object containing the corresponding name to the username.
     */
    public String getName(JSONObject username, RestTemplate restTemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(username);

        //send POST request
        //initialize the client that will sent the HTTP request
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = restTemplate.postForObject("http://localhost:8080/getName/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return "";
        }

        return (String) response.get("name");
    }

    /**
     * This method is used to retrieve the password corresponding to a certain username. The
     * username is sent to the server, the server is connected with the database.
     *
     * @param username is the "username" key with value, in JSON format so that the database
     *                 can return the corresponding name for that username.
     * @return the JSON object containing the corresponding name to the username.
     */
    public String getPassword(JSONObject username, RestTemplate restTemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(username);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = restTemplate.postForObject("http://localhost:8080/getPassword/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return "";
        }
        String result = (String) response.get("result");

        return (String) response.get("password");
    }

    /**
     * This method takes a JSONObject as input and sends it to the server so the
     * server can store it in the database.
     *
     * @param user JSONObject with username, password, name & salt.
     * @return returns true or false based on if the user was created successfully.
     */
    public boolean createUser(JSONObject user, RestTemplate restTemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(user);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = restTemplate.postForObject("http://localhost:8080/createUser/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method sends the username as JSONObject to the server so that the server
     * can delete the corresponding row in the database.
     *
     * @param username the username of the account you want to delete.
     * @return returns false if failed, else returns success.
     */
    public boolean deleteUser(JSONObject username, RestTemplate restTemplate) {
        //send request to the server url /authenticate and handle response
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(username);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = restTemplate.postForObject("http://localhost:8080/deleteUser/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method adds a transport activity to the database by sending a request
     * to the server containing all the necessary information.
     *
     * @param format       the data that needs to be sent to the server, which includes:
     *                     - The model that is being used (like car, plane, etc)
     *                     - The model that is being rejected (like car, plane etc)
     *                     - The username of the user
     *                     - The password of the user
     *                     - The distance travelled
     * @param resttemplate the resttemplate that will send the POST request to the server
     * @return returns true or false based on whether the activity was added successfully
     */
    public boolean addTransportActivity(JSONObject format, RestTemplate resttemplate,
                                        Client client) {

        String activity;
        switch ((String) format.get("modelused")) {
            case "Plane":
                activity = "flights";
                break;
            case "Car":
                activity = "automobile_trips";
                break;
            case "Bus":
                activity = "bus_trips";
                break;
            default:
                activity = "bike trip";
        }

        String co2used = "0";
        format.replace("modelused", activity);
        if (!(activity.equals("bike trip"))) {
            co2used = Co2Request.sendRequest((String) format.get("modelused"),
                    (int) format.get("distance"), client);
        }
        if (co2used == null) {
            return false;
        }
        switch ((String) format.get("modelrejected")) {
            case "Plane":
                activity = "flights";
                break;
            case "Car":
                activity = "automobile_trips";
                break;
            case "Bus":
                activity = "bus_trips";
                break;
            default:
                activity = "bike trip";
        }

        format.replace("modelrejected", activity);
        String co2rejected = "0";
        if (!(activity.equals("bike trip"))) {
            co2rejected = Co2Request.sendRequest((String) format.get("modelrejected"),
                    (int) format.get("distance"), client);
        }
        if (co2rejected == null) {
            return false;
        }
        int co2saved = (int) (Double.valueOf(co2rejected) - Double.valueOf(co2used));
        format.put("co2_saved", co2saved);
        System.out.println(co2saved);
        JSONObject response;
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/addTransportActivity/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");

    }

    /**
     * This method adds a new friend to the database by sending the request to the server.
     *
     * @param format       a JSONObject containing the following:
     *                     - the username of the user
     *                     - the password of the user
     *                     - the username of the friend you want to add
     * @param resttemplate the resttemplate that will send the POST request
     * @return returns true or false based on whether the friend was added succesfully
     */
    public boolean addFriend(JSONObject format, RestTemplate resttemplate) {
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/addFriend/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        } catch (HttpServerErrorException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method gets all the friends and their scores, and returns
     * a list array with the first index being the usernames and the second
     * index being the scores.
     *
     * @param format       the input json containing the username and password
     * @param resttemplate the restemplate that will send the request
     * @return an array of lists with the first list containing all the usernames
     *         and the second list containing all the scores
     */
    public List<String>[] getFriends(JSONObject format, RestTemplate resttemplate) {

        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] jsonresponse;
        try {
            //execute the POST request to the given url
            jsonresponse = resttemplate.postForObject("http://localhost:8080/getFriends/",
                    entity, String[].class);
        } catch (ResourceAccessException e) {
            return new ArrayList[0];
        }

        List<String> usernamelist;
        List<String> scorelist;

        //process result
        List<String>[] resultarray = new List[2];
        if (jsonresponse != null && jsonresponse.length != 0 && jsonresponse[0].length() != 0) {
            usernamelist = Arrays.asList(jsonresponse[0].split("\",\""));
            scorelist = Arrays.asList(jsonresponse[1].split("\",\""));

            //remove parentheses or " at the start
            usernamelist.set(0, usernamelist.get(0).substring(1));
            scorelist.set(0, scorelist.get(0).substring(1));

            //remove " at the end
            usernamelist.set(usernamelist.size() - 1, usernamelist.get(usernamelist.size() - 1)
                    .substring(0, usernamelist.get(usernamelist.size() - 1).length() - 1));
            scorelist.set(scorelist.size() - 1, scorelist.get(scorelist.size() - 1)
                    .substring(0, scorelist.get(scorelist.size() - 1).length() - 1));

            //insert into array
            resultarray[0] = usernamelist;
            resultarray[1] = scorelist;

        }
        return resultarray;

    }

    /**
     * This method takes a JSON object with keys username and password as input and outputs
     * a boolean response true if the user was able to change his password, else false.
     *
     * @param user JSONObject with username, password.
     * @return returns true or false based on if the user was created successfully.
     */
    public boolean changePassword(JSONObject user, RestTemplate resttemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(user);

        //send POST request
        //initialize the client that will sent the HTTP request

        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/changePassword/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method adds a new solar panel activity to the database by sending a JSONObject
     * containing all the data to the server.
     *
     * @param format JSONObject with number of panels, username and password
     * @return returns true or false whether the activity was added succesfully
     */

    public boolean addSolarPanelActivity(JSONObject format, RestTemplate resttemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/addSolarPanelActivity/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }


    /**
     * This method sends the username as JSONObject to the server so that the server
     * can retrieve the user's score from the database.
     *
     * @param input JSONObject with the username
     * @return the user's score
     */
    public int getScore(JSONObject input, RestTemplate resttemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(input);

        //send POST request
        //initialize the client that will sent the HTTP request
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/getScore",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return -1;
        }

        String score = response.get("score").toString();
        int result = Integer.parseInt(score);

        return result;
    }

    /**
     * This method takes a JSON object with the username as input and returns a boolean.
     * This boolean indicates whether the username is unique or not.
     *
     * @param input JSON object with the username
     * @return boolean that indicates uniqueness of username.
     */
    public boolean isUnique(JSONObject input, RestTemplate restTemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(input);

        //send POST request
        //initialize the client that will sent the HTTP request
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = restTemplate.postForObject("http://localhost:8080/isUnique",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }

        String unique = response.get("unique").toString();
        boolean result = Boolean.parseBoolean(unique);

        return result;
    }

    /**
     * This method takes the JSONobject and returns an integer with the user's rank.
     *
     * @param input JSONobject input with the username in it.
     * @return an integer that indicates the user's rank
     */
    public int getUserRank(JSONObject input, RestTemplate restTemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(input);

        //send POST request
        //initialize the client that will sent the HTTP request
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = restTemplate.postForObject("http://localhost:8080/getUserRank",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return -1;
        }

        String rank = response.get("rank").toString();
        int result = Integer.parseInt(rank);

        return result;
    }

    /**
     * This method takes the JSONobject and returns a boolean
     * that indicates whether is was added succesfully as a friend or not.
     *
     * @param format JSONobject that contains username
     * @return returns a boolean that indicates whether the user was added succesfully.
     */
    public boolean addUserAsFriend(JSONObject format, RestTemplate resttemplate) {
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/addUserAsFriend",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        } catch (HttpServerErrorException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method adds a new solar panel activity to the database by sending a JSONObject
     * containing all the data to the server.
     *
     * @param format JSONObject with number of panels, username and password
     * @return returns true or false whether the activity was added succesfully
     */

    public boolean addTemperatureActivity(JSONObject format, RestTemplate resttemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/addTemperatureActivity/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method takes the input JSON containing all the necessary data that needs to be
     * sent to the server.
     *
     * @param input        the input JSON containing:
     *                     - The car model
     *                     - The car brand
     *                     - The car build year
     *                     - The username
     *                     - The password
     * @param resttemplate the resttemplate that will send the request (required for mocking)
     * @return returns true or false based on whether the car was updated successfully.
     */

    public boolean updateCar(JSONObject input, RestTemplate resttemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(input);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/updateCar/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method takes the input JSON containing all the necessary data that needs to be
     * sent to the server.
     *
     * @param input        the input JSON containing:
     *                     - The car model
     *                     - The car brand
     *                     - The car build year
     *                     - The username
     *                     - The password
     * @param resttemplate the resttemplate that will send the request (required for mocking)
     * @return returns true or false based on whether the car was added successfully.
     */

    public boolean addCar(JSONObject input, RestTemplate resttemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(input);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/addCar/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return false;
        }
        String result = (String) response.get("result");

        return result.equals("success");
    }

    /**
     * This method takes the input JSON containing all the necessary data that needs to be
     * sent to the server.
     *
     * @param input        the input JSON containing:
     *                     - The username
     *                     - The password
     * @param resttemplate the resttemplate that will send the request (required for mocking)
     * @return returns the car of the user in the database.
     */

    public Car getCar(JSONObject input, RestTemplate resttemplate) {
        //set json object as data to be sent along the http request
        HttpEntity<JSONObject> entity = new HttpEntity(input);

        //send POST request
        //initialize the client that will sent the HTTP request
        this.restTemplate = new RestTemplate();
        JSONObject response;
        try {
            //execute the POST request to the given url
            response = resttemplate.postForObject("http://localhost:8080/getCar/",
                    entity, JSONObject.class);
        } catch (ResourceAccessException e) {
            return null;
        }

        if (response.get("result").equals("success")) {
            String model = (String) response.get("model");
            String brand = (String) response.get("brand");
            String year = (String) response.get("year");
            Car result = new Car(year, brand, model);
            return result;
        } else {
            return null;
        }

    }
}
