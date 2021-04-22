package client;

import static client.ClientServerCommunication.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;

import client.models.Car;
import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@RunWith(MockitoJUnitRunner.class)
public class ClientServerCommunicationTest {

//    @Test
//    public void addFoodActivityWhenServerIsOffline() {
//        JSONObject format = new JSONObject();
//        format.put("mealtype", "Vegan");
//        format.put("username", "rick123");
//        format.put("localproduce", "No");
//        format.put("password", "hoi");
//
//        Client mockedclient = mock(Client.class);
//        RestTemplate mockRestTemplate = mock(RestTemplate.class);
//        when(mockedclient.addFoodActivity(format, mockRestTemplate)).thenThrow(ResourceAccessException.class);
//        assertFalse(addFoodActivity("Vegan", new User("rick123", "hoi"),
//                "No", mockedclient, mockRestTemplate));
//    }

    @Test
    public void addFoodActivityTest() {
        JSONObject format = new JSONObject();
        format.put("mealtype", "Vegan");
        format.put("username", "rick123");
        format.put("localproduce", "No");
        format.put("password", "hoi");

        Client mockedclient = mock(Client.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockedclient.addFoodActivity(format, mockRestTemplate)).thenReturn(true);
        assertTrue("Should return true when server is running",
                addFoodActivity("Vegan", new User("rick123", "hoi"),
                        "No", mockedclient, mockRestTemplate));
    }

    @Test
    public void addFoodActivityTestLocalProduceYes() {
        JSONObject format = new JSONObject();
        format.put("mealtype", "Vegan");
        format.put("username", "rick123");
        format.put("localproduce", "Yes");
        format.put("password", "hoi");

        Client mockedclient = mock(Client.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockedclient.addFoodActivity(format, mockRestTemplate)).thenReturn(true);

        assertTrue("should be successful ",
                addFoodActivity("Vegan", new User("rick123", "hoi"), "Yes", mockedclient, mockRestTemplate));
    }

    @Test
    public void testGetUserActivities() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "rick123");
        format.put("password", "hoi");

        Client mockedclient = mock(Client.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        List<String>[] resultarray = new ArrayList[3];
        resultarray[0] = new ArrayList();
        resultarray[0].add("Transport");
        resultarray[1] = new ArrayList();
        resultarray[1].add("100 km Car trip instead of Plane trip");
        resultarray[2] = new ArrayList();
        resultarray[2].add("312");

        when(mockedclient.getUserActivity(format, mockRestTemplate)).thenReturn(resultarray);


        assertEquals(resultarray[0].get(0), getUserActivities(new User("rick123", "hoi"), mockedclient, mockRestTemplate)[0].get(0));
        assertEquals(resultarray[1].get(0), getUserActivities(new User("rick123", "hoi"), mockedclient, mockRestTemplate)[1].get(0));
        assertEquals(resultarray[2].get(0), getUserActivities(new User("rick123", "hoi"), mockedclient, mockRestTemplate)[2].get(0));
    }


    @Test
    public void addTransportActivityTest() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "rick123");
        format.put("password", "hoi");
        format.put("modelused", "Car");
        format.put("modelrejected", "Plane");
        format.put("distance", 100);

        Client mockedclient = mock(Client.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockedclient.addTransportActivity(format, mockRestTemplate, mockedclient)).thenReturn(true);
        assertEquals(true, addTransportActivity(new User("rick123", "hoi"),
                100, "Car", "Plane", mockedclient, mockRestTemplate));
    }

    @Test
    public void addFriendTest() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "rick123");
        format.put("password", "hoi");
        format.put("friend", "test");

        Client mockedclient = mock(Client.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockedclient.addFriend(format, mockRestTemplate)).thenReturn(true);
        assertEquals(true, addFriend(new User("rick123", "hoi"),
                "test", mockedclient, mockRestTemplate));
    }

    @Test
    public void retrieveFriendsTest() {
        JSONObject format = new JSONObject();
        format.put("username", "rick123");
        format.put("password", "hoi");

        Client mockedclient = mock(Client.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        List<String>[] resultarray = new ArrayList[2];
        resultarray[0] = new ArrayList();
        resultarray[0].add("Rickie");
        resultarray[1] = new ArrayList();
        resultarray[1].add("100");
        when(mockedclient.getFriends(format, mockRestTemplate)).thenReturn(resultarray);

        assertEquals(resultarray[0].get(0), retrieveFriends(new User("rick123", "hoi"), mockedclient, mockRestTemplate)[0].get(0));
        assertEquals(resultarray[1].get(0), retrieveFriends(new User("rick123", "hoi"), mockedclient, mockRestTemplate)[1].get(0));
    }

    @Test
    public void testGetScore() {
        JSONObject json = new JSONObject();
        json.put("username", "test");

        JSONObject json1 = new JSONObject();
        json1.put("score", 100);

        Client mockclient = mock(Client.class);
        // ClientServerCommunication csc= new ClientServerCommunication();

        RestTemplate restTemplate = new RestTemplate();

        when(mockclient.getScore(json, restTemplate)).thenReturn(Integer.parseInt(json1.get("score").toString()));
        assertEquals(100, getScore(json.get("username").toString(), mockclient, restTemplate));


    }

    @Test
    public void isUniqueTest() {
        JSONObject json = new JSONObject();
        json.put("username", "test");

        JSONObject json1 = new JSONObject();
        json1.put("unique", true);

        Client mockclient = mock(Client.class);
        // ClientServerCommunication csc= new ClientServerCommunication();

        RestTemplate restTemplate = new RestTemplate();

        when(mockclient.isUnique(json, restTemplate)).thenReturn(Boolean.parseBoolean(json1.get("unique").toString()));
        assertEquals(true, isUnique(json.get("username").toString(), mockclient, restTemplate));
    }

    @Test
    public void updateCarTest() {
        User user = new User("Test", "Test");
        JSONObject format = new JSONObject();
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");
        format.put("username", user.getUsername());
        format.put("password", user.getPassword());
        RestTemplate mockresttemplate = mock(RestTemplate.class);
        Client mockclient = mock(Client.class);
        when(mockclient.updateCar(format, mockresttemplate)).thenReturn(true);

        assertEquals(true, updateCar("R8", user, "2010", "Audi", mockclient, mockresttemplate));
    }

    @Test
    public void addCarTest() {
        User user = new User("Test", "Test");
        JSONObject format = new JSONObject();
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");
        format.put("username", user.getUsername());
        format.put("password", user.getPassword());
        RestTemplate mockresttemplate = mock(RestTemplate.class);
        Client mockclient = mock(Client.class);
        when(mockclient.addCar(format, mockresttemplate)).thenReturn(true);

        assertEquals(true, addCar("R8", user, "2010", "Audi", mockclient, mockresttemplate));
    }

    @Test
    public void getCarTest() {
        User user = new User("Test", "Test");
        Car car = new Car("2010", "Audi", "R8");
        JSONObject format = new JSONObject();
        format.put("username", user.getUsername());
        format.put("password", user.getPassword());


        RestTemplate mockresttemplate = mock(RestTemplate.class);
        Client mockclient = mock(Client.class);
        when(mockclient.getCar(format, mockresttemplate)).thenReturn(car);

        assertEquals(car, getCar(user, mockclient, mockresttemplate));
    }

    @Test
    public void AddSolarPanelTest() {
        User user = new User("Rick","hello");
        JSONObject format = new JSONObject();
        format.put("username", user.getUsername());
        format.put("panels", "2");
        format.put("password", user.getPassword());

        Client mockclient = mock(Client.class);
        RestTemplate mockrestemplate = mock(RestTemplate.class);
        when(mockclient.addSolarPanelActivity(format, mockrestemplate)).thenReturn(true);

        assertEquals(true, addSolarPanelActivity(user, "2", mockclient, mockrestemplate));
    }

    @Test
    public void addTemperatureActivityTest() {
        JSONObject format = new JSONObject();

        format.put("username", "rick123");
        format.put("password", "hoi");
        format.put("type", "Lowering Temperature");

        format.put("time", "5");
        format.put("temperature", "5");


        Client mockedclient = mock(Client.class);
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        when(mockedclient.addTemperatureActivity(format, mockRestTemplate)).thenReturn(true);
        assertTrue("Should return true when server is running",
                addTemperatureActivity("Lowering Temperature", new User("rick123", "hoi"),
                        "5", "5", mockedclient, mockRestTemplate));
    }

//    @Test
//    public void addTemperatureActivityWhenServerIsOffline() {
//        JSONObject format = new JSONObject();
//
//        format.put("username", "rick123");
//        format.put("password", "hoi");
//        format.put("type", "Lowering Temperature");
//
//        format.put("time", "5");
//        format.put("temperature", "5");
//
//        Client mockedclient = mock(Client.class);
//        RestTemplate mockRestTemplate = mock(RestTemplate.class);
//        when(mockedclient.addTemperatureActivity(format, mockRestTemplate)).thenThrow(ResourceAccessException.class);
//        assertFalse(addTemperatureActivity("Lowering Temperature", new User("rick123", "hoi"),
//                "5", "5", mockedclient, mockRestTemplate));
//    }

//    @Test
//    public void addSolarPanelActivityTest() {
//        JSONObject format = new JSONObject();
//
//        format.put("username", "rick123");
//        format.put("password", "hoi");
//        format.put("type", "SolarPanel");
//
//        format.put("panels", "5");
//
//
//        Client mockedclient = mock(Client.class);
//        RestTemplate mockRestTemplate = mock(RestTemplate.class);
//        when(mockedclient.addSolarPanelActivity(format, mockRestTemplate)).thenReturn(true);
//        assertTrue("Should return true when server is running",
//                addSolarPanelActivity("SolarPanel", new User("rick123", "hoi"),
//                        "5", mockedclient, mockRestTemplate));
//    }

//    @Test
//    public void addSolarPanelActivityWhenServerIsOffline() {
//        JSONObject format = new JSONObject();
//
//        format.put("username", "rick123");
//        format.put("password", "hoi");
//        format.put("type", "SolarPanel");
//
//        format.put("panels", "5");
//
//        Client mockedclient = mock(Client.class);
//        RestTemplate mockRestTemplate = mock(RestTemplate.class);
//        when(mockedclient.addSolarPanelActivity(format, mockRestTemplate)).thenThrow(ResourceAccessException.class);
//        assertFalse(addSolarPanelActivity("SolarPanel", new User("rick123", "hoi"),
//                "5", mockedclient, mockRestTemplate));
//    }

    @Test
    public void addUserTest(){
        JSONObject json= new JSONObject();
        json.put("username", "test");

//        JSONObject json1= new JSONObject();
//        json1.put("result", "success");

        Client mockclient= mock(Client.class);
        // ClientServerCommunication csc= new ClientServerCommunication();

        RestTemplate restTemplate= new RestTemplate();

        when(mockclient.addUserAsFriend(json, restTemplate)).thenReturn(true);
        assertEquals(true, addUserAsFriend(json.get("username").toString(), mockclient, restTemplate) );
    }

    @Test
    public void testGetRank() {
        JSONObject json = new JSONObject();
        json.put("username", "test");

        JSONObject json1 = new JSONObject();
        json1.put("rank", 1);

        Client mockclient = mock(Client.class);
        // ClientServerCommunication csc= new ClientServerCommunication();

        RestTemplate restTemplate = new RestTemplate();

        when(mockclient.getUserRank(json, restTemplate)).thenReturn(Integer.parseInt(json1.get("rank").toString()));
        assertEquals(1, getUserRank(json.get("username").toString(), mockclient, restTemplate));
    }
}