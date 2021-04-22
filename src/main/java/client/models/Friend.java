package client.models;

public class Friend {

    private String username;
    private String score;

    /**
     * The constructor of the friend class.
     *
     * @param username the username in String of the friend
     * @param score    the score of the friend in String format
     */
    public Friend(String username, String score) {
        this.username = username;
        this.score = score;
    }

    /**
     * Getter for the username.
     *
     * @return returns the username of the friend
     */
    public String getUsername() {
        return username;
    }

    /**
     * The getter for the score of a friend.
     *
     * @return returns the score of the friend.
     */
    public String getScore() {
        return score;
    }
}
