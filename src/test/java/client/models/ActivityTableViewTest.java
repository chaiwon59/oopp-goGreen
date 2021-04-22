package client.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ActivityTableViewTest {

    @Test
    public void getCategoryTest() {
        ActivityTableView test = new ActivityTableView("Food", "Vegetarian meal", "2");
        assertEquals("Food", test.getCategory());
    }

    @Test
    public void getActivityTest() {
        ActivityTableView test = new ActivityTableView("Food", "Vegetarian meal", "2");
        assertEquals("Vegetarian meal", test.getActivity());
    }

    @Test
    public void getScoreTest() {
        ActivityTableView test = new ActivityTableView("Food", "Vegetarian meal", "2");
        assertEquals("2", test.getScore());
    }
    @Test
    public void constructorTest() {
        ActivityTableView test = new ActivityTableView("Food", "Vegetarian meal", "2");
        assertEquals("2", test.getScore());
        assertEquals("Vegetarian meal", test.getActivity());
        assertEquals("Food", test.getCategory());
    }
}
