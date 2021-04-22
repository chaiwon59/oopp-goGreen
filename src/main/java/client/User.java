package client;

import org.json.simple.JSONObject;
import org.springframework.web.client.RestTemplate;


public class User {


    //Attributes
    private String username;
    private String password;
    private String name;

    /**
     * Constructor method for User.
     *
     * @param username the username
     * @param password the password
     * @param name     the user's name
     */
    public User(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * Getter for name attribute.
     *
     * @return String name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for username attribute.
     *
     * @return String username
     */
    public String getUsername() {
        return this.username;
    }


    /**
     * Getter for the password of the user.
     *
     * @return returns the password in a String
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * The createUser() method is called on a User object, the username, password & name
     * are sent to the server in a JSON object.
     *
     * @return boolean, true if the user was added successfully, false if not.
     */
    public boolean createUser(Client client, RestTemplate restTemplate) {
        JSONObject input = new JSONObject();
        input.put("username", this.username);
        input.put("password", this.password);
        input.put("name", this.name);
        return client.createUser(input, restTemplate);
    }

    /**
     * The deleteUser() method is called on a User object, first the authenticate method is called.
     * If has authorization the username is sent to the server and then the corresponding row
     * in the database is deleted.
     *
     * @return boolean, true if the row was deleted successfully, false if not.
     */
    public boolean deleteUser(Client client, RestTemplate restTemplate) {
        JSONObject input = new JSONObject();
        input.put("username", this.username);
        return client.deleteUser(input, restTemplate);
    }

    /**
     * The getName() method is called on a User object, if the name is not known it's retrieved from
     * the database.
     *
     * @return String, the name attribute.
     */
    public String getNameDb(Client client, RestTemplate restTemplate) {
        JSONObject input = new JSONObject();
        input.put("username", this.username);
        return client.getName(input, restTemplate);
    }

    /**
     * The getPassword() method is called on a User object,
     * the username is sent to the server in JSON format
     * and the server retrieves the corresponding (hashed) password from the database.
     *
     * @return the password.
     */
    public String getPasswordDb(Client client, RestTemplate restTemplate) {
        JSONObject input = new JSONObject();
        input.put("username", this.username);
        return client.getPassword(input, restTemplate);
    }


    /**
     * The changePassword() method is called on a User object
     * when the user wishes to change his password.
     * The old password should be entered again.
     *
     * @param password    The old password.
     * @param newPassword The new password.
     */
    public boolean changePassword(
            String password, String newPassword, Client client, RestTemplate resttemplate) {
        JSONObject input = new JSONObject();
        input.put("username", this.username);
        input.put("password", password);
        input.put("newPassword", newPassword);
        return client.changePassword(input, resttemplate);
    }

    /**
     * The authenticate() method is called on a User object
     * to check whether the username and the corresponding
     * password exist in the database. This method acts as security.
     *
     * @return boolean, true if the user was authenticated successfully,
     *         false if the username and/or password were incorrect.
     */
    public boolean authenticate(Client client, RestTemplate restTemplate) {
        JSONObject input = new JSONObject();
        input.put("username", this.username);
        input.put("password", this.password);
        return client.authenticate(input, restTemplate);
    }



    //Unique user can be identified with only username and password combination
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User user = (User) obj;
        return
                username.equals(user.username)
                        && password.equals(user.password);
    }

}