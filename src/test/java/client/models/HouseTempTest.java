package client.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class HouseTempTest {

    HouseTemp house = new HouseTemp("1");

    private double avgCO2 = 27.33;
    private int duration = 6;
    private int lowered = 4;
    private double saved = avgCO2 * 0.03 * duration  * lowered;



    @Test
    public void constructorTest() {
        assertEquals(house.getLower(), 0);
        assertEquals((int) house.getSaved(), 0);
    }

    @Test
    public void getLowerTest() {
        assertEquals( house.getLower(), 0);
    }

    @Test
    public void getDurationTest() {
        assertEquals(house.getDuration(), 0);
    }

    @Test
    public void setLowerTest() {
        house.setDuration(0);
        house.setLower(4);
        assertEquals((int) house.getSaved(),  0);
        assertEquals( house.getLower(), 4);
    }

    @Test
    public void setDuration() {
        house.setLower(0);
        house.setDuration(6);
        assertEquals((int) house.getSaved(), 0);
        assertEquals(house.getDuration(), 6);
    }

    @Test
    public void DurationLowerNotZero() {
        house.setLower(4);
        house.setDuration(6);
        assertEquals(house.getDuration(), 6);
        assertEquals(house.getLower(), 4);
        assertEquals((int) house.getSaved(), (int) saved);
    }


}