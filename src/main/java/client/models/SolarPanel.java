package client.models;

import static java.lang.Integer.parseInt;

public class SolarPanel extends Activity {
    private String numberOfPanels;


    /**
     * Creating a solar panel.
     *
     * @param userId         The user for which it applies.
     * @param numberOfPanels number of panels that the user has.
     */

    public SolarPanel(String userId, String numberOfPanels) {
        super(userId);
        this.numberOfPanels = numberOfPanels;
        calculateNewCO2();
    }

    public String getNumberOfPanels() {
        return numberOfPanels;
    }

    public void setNumberOfPanels(String numberOfPanels) {
        this.numberOfPanels = numberOfPanels;
        calculateNewCO2();
    }


    public void calculateNewCO2() {
        this.saved = parseInt(this.numberOfPanels) * 0.3;
    }

}

