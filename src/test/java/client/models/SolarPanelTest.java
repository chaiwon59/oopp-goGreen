package client.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SolarPanelTest {
    private SolarPanel solarPanel = new SolarPanel("1", "4");

    @Test
    public void constructorTest() {
        assertEquals(solarPanel.getNumberOfPanels(), "4");
        assertEquals(solarPanel.getUserId(), "1");
    }

    @Test
    public void getSolarPanelsTest() {
        assertEquals(solarPanel.getNumberOfPanels(), "4");
    }

    @Test
    public void setCO2savedTest() {
        solarPanel.setNumberOfPanels("9");
        assertEquals(solarPanel.getNumberOfPanels(), "9");
    }

    @Test
    public void calculateNewCO2Test() {
        double CO2 = 2.7;
        solarPanel.setNumberOfPanels("9");
        assertEquals((int) solarPanel.saved, (int) CO2);
    }

}
