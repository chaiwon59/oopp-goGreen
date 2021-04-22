package client.models;

public class HouseTemp extends Activity {
    private int lower;
    private int duration;

    private double avgCO2 = 27.33;

    /**
     * Creating a house temperature activity.
     *
     * @param userId The user to which it applies to.
     */

    public HouseTemp(String userId) {
        super(userId);
        this.lower = 0;
        this.duration = 0;
    }


    public int getLower() {
        return lower;
    }

    /**
     * Setting if the temperature is lowered during the day.
     *
     * @param lower The degrees by which you lower your temperature.
     */

    public void setLower(int lower) {
        this.lower = lower;
        savedLower();
    }

    public int getDuration() {
        return duration;
    }

    /**
     * Setting if the duration by which the temperature is lowered during the day.
     *
     * @param duration The duration with which the temperature is lowered.
     */

    public void setDuration(int duration) {
        this.duration = duration;
        savedLower();
    }


    public void savedLower() {
        this.saved = 0.03 * avgCO2 * this.duration * this.lower;
    }
}