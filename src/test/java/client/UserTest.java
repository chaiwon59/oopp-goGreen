package client;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserTest {

    String username;
    String password;
    String name1;

    User user1;

    String username2;
    String password2;
    String name2;

    User user2;
    User userX;

    @Before
    public void init() {
        username = "username1";
        password = "password1";
        name1 = "name1";
        user1 = new User(username, password, name1);

        username2 = "username2";
        password2 = "password1";
        name2 = "name2";
        user2 = new User(username2, password2, name2);

        userX = new User("testUN", "testPW", "testN");
    }

    @Test
    public void constructorTest() {
        assertNotEquals(user1, user2);  //different reference
        assertEquals(user1,user1);  //same reference
    }

    @Test
    public void constructor2Test() {
        User user3 = new User(username, password);
        User user4 = new User(username2, password2);
        assertNotEquals(user3, user4);  //different reference
        assertEquals(user3,user3);  //same reference
    }

    @Test
    public void getNameTest() {
        assertEquals(user1.getName(), "name1");
    }

    @Test
    public void getUsernameTest() {
        assertEquals(user1.getUsername(), "username1");
    }

    @Test
    public void getPasswordTest() {
        assertEquals(user1.getPassword(), "password1");
    }

    @Test
    public void createUserTest() {
        JSONObject input = new JSONObject();
        input.put("username", "testUN");
        input.put("password", "testPW");
        input.put("name", "testN");
        Client client = mock(Client.class);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        when(client.createUser(input, mockTemplate)).thenReturn(true);
        assertTrue(userX.createUser(client, mockTemplate));
    }

    @Test
    public void deleteUserTest() {
        JSONObject input = new JSONObject();
        input.put("username", "testUN");
        Client client = mock(Client.class);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        when(client.deleteUser(input, mockTemplate)).thenReturn(true);
        assertTrue(userX.deleteUser(client, mockTemplate));
    }

    @Test
    public void getNameDbTest() {
        JSONObject input = new JSONObject();
        input.put("username", "testUN");
        Client client = mock(Client.class);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        when(client.getName(input, mockTemplate)).thenReturn("eduard");
        assertEquals("eduard", userX.getNameDb(client, mockTemplate));
    }

    @Test
    public void getPasswordDbTest() {
        JSONObject input = new JSONObject();
        input.put("username", "testUN");
        Client client = mock(Client.class);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        when(client.getPassword(input, mockTemplate)).thenReturn("testPW");
        assertEquals("testPW", userX.getPasswordDb(client, mockTemplate));
    }

    @Test
    public void changePasswordTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard");
        input.put("password", "hoidoei");
        input.put("newPassword", "hoi");
        Client client = mock(Client.class);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        when(client.changePassword(input, mockTemplate)).thenReturn(true);
        User user = new User("eduard", "hoidoei");
        assertTrue(user.changePassword("hoidoei", "hoi", client, mockTemplate));

    }

    @Test
    public void authenticateTest() {
        JSONObject input = new JSONObject();
        input.put("username", "testUN");
        input.put("password", "testPW");
        Client client = mock(Client.class);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        when(client.authenticate(input, mockTemplate)).thenReturn(true);
        assertTrue(userX.authenticate(client, mockTemplate));
    }

    @Test
    public void equalsTest() {
        User test1 = new User("1", "2", "3");
        User test2 = new User("1", "2", "3");
        User test3 = new User("2", "2", "3");
        assertEquals(test1, test2);
        assertNotEquals(test1, test3);
        assertEquals(test1, test1);
    }

    @Test
    public void NullequalsTest() {
        User test1 = null;
        User test2 = new User("1", "2", "3");
        User test3 = new User("2", "2", "3");
        String test4 = "test";
        assertNotEquals(test1, test2);
        assertNotEquals(test1, test3);
        assertEquals(test1, test1);
        assertFalse(test2.equals(test1));
        assertFalse(test2.equals(test4));
    }

    @Test
    public void DifferentPasswordequalsTest() {
        User test1 = null;
        User test2 = new User("1", "2", "3");
        User test3 = new User("1", "3", "3");
        assertNotEquals(test1, test2);
        assertNotEquals(test2, test3);
        assertEquals(test1, test1);
    }

    @Test
    public void NonUserequalsTest() {
        Client test1 = new Client();
      User test2 = new User("1", "2", "3");
        assertNotEquals(test1, test2);

    }
}
