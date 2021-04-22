package client;

import gui.Main;
import org.json.simple.JSONObject;

public class Co2Request {

    /**
     * This method converts string input into a correct JSON format that can be
     * understood by the server.
     *
     * @param distance the estimated distance of the flight
     * @return returns the input in json format
     */
    public static JSONObject convertToJson(int distance, String model) {
        JSONObject json = new JSONObject();
        if (model.equals("flights")) {
            json.put("distance_estimate", distance);
        } else {
            json.put("distance", distance);
        }
        return json;
    }


    /**
     * This method is for all the models, it takes the model (model
     * means what type of transport) and distance that is going to be travelled and
     * calculates the amount of co2 is used.
     *
     * @param model    the type of transport used
     * @param distance the distance in km
     * @return returns the amount of co2 saved in kg
     */
    public static String sendRequest(String model, int distance, Client client) {
        JSONObject formatjson;
        formatjson = convertToJson(distance, model);
        if (model.equals("automobile_trips")) {
            try {
                formatjson.put("model", Main.car.getModel());
                formatjson.put("year", Main.car.getYear());
            } catch (NullPointerException e) {
                return null;
            }
        }
        return client.getCO2(formatjson, model);
    }
}