package client.models;

public class ActivityTableView {

    private String category;
    private String activity;
    private String score;

    /**
     * The construcor for an Activity in a tableview.
     *
     * @param category The category of the activity (like food, transport etc)
     * @param activity The activity description
     * @param score    the score associated with this activity
     */
    public ActivityTableView(String category, String activity, String score) {
        this.category = category;
        this.activity = activity;
        this.score = score;
    }

    /**
     * The getter for the gategory of an activity.
     *
     * @return the category of the activity as a String
     */
    public String getCategory() {
        return category;
    }

    /**
     * The getter for the activity description of an activity.
     *
     * @return the description of an activity in String format
     */
    public String getActivity() {
        return activity;
    }

    /**
     * The getter for the score of an activity.
     *
     * @return the score of an activity in String format
     */
    public String getScore() {
        return score;
    }
}
