package client.models;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FriendTest {

    @Test
    public void getUsernameTest() {
        Friend friend = new Friend("Rick", "23");
        assertEquals("Rick", friend.getUsername());
    }

    @Test
    public void getScoreTest() {
        Friend friend = new Friend("Rick", "23");
        assertEquals("23", friend.getScore());
    }

    @Test
    public void constructorTest() {
        Friend friend = new Friend("Rick", "23");
        assertEquals("23", friend.getScore());
        assertEquals("Rick", friend.getUsername());
    }
}
