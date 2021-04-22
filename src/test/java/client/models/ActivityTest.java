package client.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ActivityTest {
    Activity act = new Activity("1");

    @Test
    public void constructorTest() {
        assertEquals(act.getUserId(), "1");
    }

    @Test
    public void getCO2savedTest() {
        assertEquals((int) act.getSaved(), 0);
    }

    @Test
    public void setCO2savedTest() {
        act.setSaved(10);
        assertEquals((int) act.getSaved(), 10);
    }

    @Test
    public void setUserIDTest() {
        act.setUserId("4");
        assertEquals(act.getUserId(), "4");
    }


}