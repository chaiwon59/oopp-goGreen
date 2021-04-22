package client;


import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import client.models.Car;
import org.json.simple.JSONObject;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.List;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Co2Request.class)
public class ClientTest {


    @Test
    @Ignore("server needs to be running")
    public void testGetCo2()
            throws Exception {

        String model = "flights";
        JSONObject json = new JSONObject();
        json.put("distance_estimate", 12000);
        String value = "5861.1";
        Client client = new Client();

        assertEquals(client.getCO2(json, "flights"), value);

    }

    @Test
    public void testaddFoodActivity() {

        JSONObject format = new JSONObject();
        format.put("mealtype", "Vegan");
        format.put("username", "rick123");
        format.put("localproduce", "No");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity<>(format);

        JSONObject mockresponse = new JSONObject();
        mockresponse.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addFoodActivity/"
                , entity, JSONObject.class)).thenReturn(mockresponse);


        Client client = new Client();
        boolean result = client.addFoodActivity(format, mocktemplate);
        assertTrue(result);

    }

    @Test
    public void testaddFoodActivitywhenServerIsOffline() {

        JSONObject format = new JSONObject();
        format.put("mealtype", "Vegan");
        format.put("username", "rick123");
        format.put("localproduce", "No");
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        RestTemplate mocktemplate = mock(RestTemplate.class);
        when(mocktemplate.postForObject("http://localhost:8080/addFoodActivity/", entity, JSONObject.class))
                .thenThrow(ResourceAccessException.class);

        Client client = new Client();
        boolean result = client.addFoodActivity(format, mocktemplate);
        assertFalse(result);

    }

    @Test
    public void testGetUserActivities() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");


        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[3];
        mockreturn[0] = "\"Food\"";
        mockreturn[1] = "\"Vegetarian meal\"";
        mockreturn[2] = "\"3\"";

        when(mocktemplate.postForObject("http://localhost:8080/getUserActivities", entity, String[].class))
                .thenReturn(mockreturn);
        assertEquals("Food", client.getUserActivity(format, mocktemplate)[0].get(0));
        assertEquals("Vegetarian meal", client.getUserActivity(format, mocktemplate)[1].get(0));
        assertEquals("3", client.getUserActivity(format, mocktemplate)[2].get(0));

    }

    @Test
    public void testGetUserActivitiesEmptyResult() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");


        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[3];
        mockreturn[0] = "";
        mockreturn[1] = "";
        mockreturn[2] = "";

        when(mocktemplate.postForObject("http://localhost:8080/getUserActivities", entity, String[].class))
                .thenReturn(mockreturn);
        assertNull(client.getUserActivity(format, mocktemplate)[0]);
        assertNull(client.getUserActivity(format, mocktemplate)[1]);
        assertNull(client.getUserActivity(format, mocktemplate)[2]);
    }

    @Test
    public void testGetUserActivitiesReturnsNull() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");


        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[3];
        mockreturn[0] = "";
        mockreturn[1] = "";
        mockreturn[2] = "";

        when(mocktemplate.postForObject("http://localhost:8080/getUserActivities", entity, String[].class))
                .thenReturn(null);
        assertNull(client.getUserActivity(format, mocktemplate)[0]);
        assertNull(client.getUserActivity(format, mocktemplate)[1]);
        assertNull(client.getUserActivity(format, mocktemplate)[2]);
    }

    @Test
    public void addTransportActivityCarBikeTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");
        format.put("modelrejected", "Bike");
        format.put("modelused", "Car");
        format.put("distance", 100);

        JSONObject intermediateresult = new JSONObject();
        intermediateresult.put("username", "TestUser");
        intermediateresult.put("password", "test");
        intermediateresult.put("modelrejected", "bike trip");
        intermediateresult.put("modelused", "automobile_trips");
        intermediateresult.put("distance", 100);
        intermediateresult.put("co2_saved", -100);

        Client client = new Client();
        PowerMockito.mockStatic(Co2Request.class);

        PowerMockito.when(Co2Request.sendRequest("bike_trip", 100, client)).thenReturn("0");
        PowerMockito.when(Co2Request.sendRequest("automobile_trips", 100, client)).thenReturn("100");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(intermediateresult);

        JSONObject mockreturn = new JSONObject();
        mockreturn.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addTransportActivity/", entity, JSONObject.class))
                .thenReturn(mockreturn);
        assertTrue(client.addTransportActivity(format, mocktemplate, client));
    }

    @Test
    public void addTransportActivityPlaneBusTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");
        format.put("modelrejected", "Bus");
        format.put("modelused", "Plane");
        format.put("distance", 100);

        JSONObject intermediateresult = new JSONObject();
        intermediateresult.put("username", "TestUser");
        intermediateresult.put("password", "test");
        intermediateresult.put("modelrejected", "bus_trips");
        intermediateresult.put("modelused", "flights");
        intermediateresult.put("distance", 100);
        intermediateresult.put("co2_saved", -100);

        Client client = new Client();
        PowerMockito.mockStatic(Co2Request.class);

        PowerMockito.when(Co2Request.sendRequest("bus_trips", 100, client)).thenReturn("0");
        PowerMockito.when(Co2Request.sendRequest("flights", 100, client)).thenReturn("100");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(intermediateresult);

        JSONObject mockreturn = new JSONObject();
        mockreturn.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addTransportActivity/", entity, JSONObject.class))
                .thenReturn(mockreturn);
        assertTrue(client.addTransportActivity(format, mocktemplate, client));
    }


    @Test
    public void addTransportActivityBikePlaneTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");
        format.put("modelrejected", "Plane");
        format.put("modelused", "Bike");
        format.put("distance", 100);

        JSONObject intermediateresult = new JSONObject();
        intermediateresult.put("username", "TestUser");
        intermediateresult.put("password", "test");
        intermediateresult.put("modelrejected", "flights");
        intermediateresult.put("modelused", "bike trip");
        intermediateresult.put("distance", 100);
        intermediateresult.put("co2_saved", 100);

        Client client = new Client();
        PowerMockito.mockStatic(Co2Request.class);

        PowerMockito.when(Co2Request.sendRequest("automobile_trips", 100, client)).thenReturn("0");
        PowerMockito.when(Co2Request.sendRequest("flights", 100, client)).thenReturn("100");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(intermediateresult);

        JSONObject mockreturn = new JSONObject();
        mockreturn.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addTransportActivity/", entity, JSONObject.class))
                .thenReturn(mockreturn);
        assertTrue(client.addTransportActivity(format, mocktemplate, client));
    }

    @Test
    public void testGetScore() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        JSONObject json = new JSONObject();
        json.put("score", 100);

        when(mocktemplate.postForObject("http://localhost:8080/getScore", entity, JSONObject.class)).thenReturn(json);
        assertEquals(100, client.getScore(format, mocktemplate));

    }


    @Test
    public void testGetScoreThrowsException() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        JSONObject json = new JSONObject();
        json.put("score", 100);

        when(mocktemplate.postForObject("http://localhost:8080/getScore", entity, JSONObject.class)).thenThrow(ResourceAccessException.class);
        assertEquals(-1, client.getScore(format, mocktemplate));

    }


    @Test
    public void addTransportActivityBusCarTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");
        format.put("modelrejected", "Car");
        format.put("modelused", "Bus");
        format.put("distance", 100);

        JSONObject intermediateresult = new JSONObject();
        intermediateresult.put("username", "TestUser");
        intermediateresult.put("password", "test");
        intermediateresult.put("modelrejected", "automobile_trips");
        intermediateresult.put("modelused", "bus_trips");
        intermediateresult.put("distance", 100);
        intermediateresult.put("co2_saved", 100);

        Client client = new Client();
        PowerMockito.mockStatic(Co2Request.class);

        PowerMockito.when(Co2Request.sendRequest("bus_trips", 100, client)).thenReturn("0");
        PowerMockito.when(Co2Request.sendRequest("automobile_trips", 100, client)).thenReturn("100");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(intermediateresult);

        JSONObject mockreturn = new JSONObject();
        mockreturn.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addTransportActivity/", entity, JSONObject.class))
                .thenReturn(mockreturn);
        assertTrue(client.addTransportActivity(format, mocktemplate, client));
    }


    @Test
    public void addTransportActivityPlaneBusWhenServerIsOfflineTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");
        format.put("modelrejected", "Bus");
        format.put("modelused", "Plane");
        format.put("distance", 100);

        JSONObject intermediateresult = new JSONObject();
        intermediateresult.put("username", "TestUser");
        intermediateresult.put("password", "test");
        intermediateresult.put("modelrejected", "bus_trips");
        intermediateresult.put("modelused", "flights");
        intermediateresult.put("distance", 100);
        intermediateresult.put("co2_saved", -100);

        Client client = new Client();
        PowerMockito.mockStatic(Co2Request.class);

        PowerMockito.when(Co2Request.sendRequest("bus_trips", 100, client)).thenReturn("0");
        PowerMockito.when(Co2Request.sendRequest("flights", 100, client)).thenReturn("100");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(intermediateresult);

        JSONObject mockreturn = new JSONObject();
        mockreturn.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addTransportActivity/", entity, JSONObject.class))
                .thenThrow(ResourceAccessException.class);
        assertFalse(client.addTransportActivity(format, mocktemplate, client));
    }

    @Test
    public void AddFriendTest() {
        JSONObject format = new JSONObject();
        format.put("password", "test");
        format.put("username", "rick123");
        format.put("friend", "testuser");

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity<>(format);

        JSONObject mockresponse = new JSONObject();
        mockresponse.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addFriend/"
                , entity, JSONObject.class)).thenReturn(mockresponse);


        Client client = new Client();
        boolean result = client.addFriend(format, mocktemplate);
        assertTrue(result);
    }

    @Test
    public void AddFriendWhenServerIsOfflineTest() {
        JSONObject format = new JSONObject();
        format.put("password", "test");
        format.put("username", "rick123");
        format.put("friend", "testuser");

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity<>(format);

        JSONObject mockresponse = new JSONObject();
        mockresponse.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addFriend/"
                , entity, JSONObject.class)).thenThrow(ResourceAccessException.class);


        Client client = new Client();
        boolean result = client.addFriend(format, mocktemplate);
        assertFalse(result);
    }


    @Test
    public void AddFriendWhenFriendDoesNotExistTest() {
        JSONObject format = new JSONObject();
        format.put("password", "test");
        format.put("username", "rick123");
        format.put("friend", "testuser");

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity<>(format);

        JSONObject mockresponse = new JSONObject();
        mockresponse.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addFriend/"
                , entity, JSONObject.class)).thenThrow(HttpServerErrorException.class);


        Client client = new Client();
        boolean result = client.addFriend(format, mocktemplate);
        assertFalse(result);
    }

    @Test
    public void getFriendsTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[2];
        mockreturn[0] = "\"Rickie\",\"Rick\"";
        mockreturn[1] = "\"12\",\"21\"";

        when(mocktemplate.postForObject("http://localhost:8080/getFriends/", entity, String[].class))
                .thenReturn(mockreturn);
        assertEquals("Rickie", client.getFriends(format, mocktemplate)[0].get(0));
        assertEquals("Rick", client.getFriends(format, mocktemplate)[0].get(1));
        assertEquals("12", client.getFriends(format, mocktemplate)[1].get(0));
        assertEquals("21", client.getFriends(format, mocktemplate)[1].get(1));

    }

    @Test
    public void getFriendsServerReturnsNullTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[2];

        List<String>[] result = new List[2];
        when(mocktemplate.postForObject("http://localhost:8080/getFriends/", entity, String[].class))
                .thenReturn(null);
        assertNull(client.getFriends(format, mocktemplate)[0]);

    }

    @Test
    public void getFriendsServerReturnsEmptyTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[0];

        List<String>[] result = new List[2];
        when(mocktemplate.postForObject("http://localhost:8080/getFriends/", entity, String[].class))
                .thenReturn(mockreturn);
        assertNull(client.getFriends(format, mocktemplate)[0]);

    }

    @Test
    public void getFriendsWhenServerOfflineTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[2];
        mockreturn[0] = "\"Rickie\",\"Rick\"";
        mockreturn[1] = "\"12\",\"21\"";

        when(mocktemplate.postForObject("http://localhost:8080/getFriends/", entity, String[].class))
                .thenThrow(ResourceAccessException.class);
        assertEquals(0, client.getFriends(format, mocktemplate).length);
    }

    @Test
    public void getFriendsEmptyResultTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[2];
        mockreturn[0] = "";
        mockreturn[1] = "";

        when(mocktemplate.postForObject("http://localhost:8080/getFriends/", entity, String[].class))
                .thenReturn(mockreturn);
        assertNull(client.getFriends(format, mocktemplate)[0]);
    }

    @Test
    public void getFriendsWrongCredentialsTest() {
        JSONObject format = new JSONObject();
        format.put("username", "TestUser");
        format.put("password", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);
        String[] mockreturn = new String[2];
        mockreturn[0] = "\"Rickie\",\"Rick\"";
        mockreturn[1] = "\"12\",\"21\"";

        when(mocktemplate.postForObject("http://localhost:8080/getFriends/", entity, String[].class))
                .thenReturn(null);
        assertNull(client.getFriends(format, mocktemplate)[0]);
    }

    @Test
    public void changePasswordTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard");
        input.put("password", "hoi");
        input.put("newPassword", "doei");

        HttpEntity<JSONObject> entity = new HttpEntity<>(input);
        RestTemplate mocktemplate = mock(RestTemplate.class);
        JSONObject jsonresult = new JSONObject();
        jsonresult.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/changePassword/",
                entity, JSONObject.class)).thenReturn(jsonresult);

        Client client = new Client();
        assertTrue(client.changePassword(input, mocktemplate));
    }

    @Test
    public void changePasswordExceptionTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard");
        input.put("password", "hoi");
        input.put("newPassword", "doei");

        HttpEntity<JSONObject> entity = new HttpEntity<>(input);
        RestTemplate mocktemplate = mock(RestTemplate.class);
        JSONObject jsonresult = new JSONObject();
        jsonresult.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/changePassword/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);

        Client client = new Client();
        assertFalse(client.changePassword(input, mocktemplate));
    }

    @Test
    public void createUserTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard"); //
        input.put("password", "pw"); //
        input.put("newPassword", "eduard"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(input);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", "success");
        when(mockTemplate.postForObject("http://localhost:8080/createUser/",
                entity, JSONObject.class)).thenReturn(jsonResult);
        Client client = new Client();
        assertTrue(client.createUser(input, mockTemplate));
    }

    @Test
    public void createUserExceptionTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard"); //
        input.put("password", "pw"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(input);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", "success");
        when(mockTemplate.postForObject("http://localhost:8080/createUser/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);
        Client client = new Client();
        assertFalse(client.createUser(input, mockTemplate));
    }

    @Test
    public void deleteUserTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard"); //
        input.put("password", "pw"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(input);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", "success");
        when(mockTemplate.postForObject("http://localhost:8080/deleteUser/",
                entity, JSONObject.class)).thenReturn(jsonResult);
        Client client = new Client();
        assertTrue(client.deleteUser(input, mockTemplate));
    }

    @Test
    public void deleteUserExceptionTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard"); //
        input.put("password", "pw"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(input);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", "success");
        when(mockTemplate.postForObject("http://localhost:8080/deleteUser/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);
        Client client = new Client();
        assertFalse(client.deleteUser(input, mockTemplate));
    }

    @Test
    public void getNameTest() {
        JSONObject username = new JSONObject();
        username.put("username", "eduard"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(username);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject response = new JSONObject();
        response.put("name", "henk");
        when(mockTemplate.postForObject("http://localhost:8080/getName/",
                entity, JSONObject.class)).thenReturn(response);
        Client client = new Client();
        assertEquals(client.getName(username, mockTemplate), "henk");
    }

    @Test
    public void getNameExceptionTest() {
        JSONObject username = new JSONObject();
        username.put("username", "eduard"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(username);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject response = new JSONObject();
        response.put("name", "henk");
        when(mockTemplate.postForObject("http://localhost:8080/getName/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);
        Client client = new Client();
        assertEquals(client.getName(username, mockTemplate), "");
    }

    @Test
    public void getPasswordTest() {
        JSONObject username = new JSONObject();
        username.put("username", "eduard"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(username);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject response = new JSONObject();
        response.put("password", "henk");
        when(mockTemplate.postForObject("http://localhost:8080/getPassword/",
                entity, JSONObject.class)).thenReturn(response);
        Client client = new Client();
        assertEquals(client.getPassword(username, mockTemplate), "henk");
    }

    @Test
    public void getPasswordExceptionTest() {
        JSONObject username = new JSONObject();
        username.put("username", "eduard"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(username);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject response = new JSONObject();
        response.put("password", "henk");
        when(mockTemplate.postForObject("http://localhost:8080/getPassword/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);
        Client client = new Client();
        assertEquals(client.getPassword(username, mockTemplate), "");
    }

    @Test
    public void authenticateTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard"); //
        input.put("password", "pw"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(input);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", "access granted");
        when(mockTemplate.postForObject("http://localhost:8080/authenticate/",
                entity, JSONObject.class)).thenReturn(jsonResult);
        Client client = new Client();
        assertTrue(client.authenticate(input, mockTemplate));
    }

    @Test
    public void authenticateExceptionTest() {
        JSONObject input = new JSONObject();
        input.put("username", "eduard"); //
        input.put("password", "pw"); //

        HttpEntity<JSONObject> entity = new HttpEntity<>(input);
        RestTemplate mockTemplate = mock(RestTemplate.class);
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("result", "access granted");
        when(mockTemplate.postForObject("http://localhost:8080/authenticate/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);
        Client client = new Client();
        assertFalse(client.authenticate(input, mockTemplate));
    }


    @Test
    public void testIsUnique() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        JSONObject json = new JSONObject();
        json.put("unique", true);

        when(mocktemplate.postForObject("http://localhost:8080/isUnique", entity, JSONObject.class)).thenReturn(json);
        assertEquals(true, client.isUnique(format, mocktemplate));

    }

    @Test
    public void testIsUniqueThrowsException() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "test");

        Client client = new Client();

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        JSONObject json = new JSONObject();
        json.put("unique", true);

        when(mocktemplate.postForObject("http://localhost:8080/isUnique", entity, JSONObject.class))
                .thenThrow(ResourceAccessException.class);
        assertEquals(false, client.isUnique(format, mocktemplate));

    }

    @Test
    public void AddSolarActivityTest() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("panels", "2");
        format.put("username", "rick123");

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity<>(format);

        JSONObject mockresponse = new JSONObject();
        mockresponse.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addSolarPanelActivity/"
                , entity, JSONObject.class)).thenReturn(mockresponse);


        Client client = new Client();
        boolean result = client.addSolarPanelActivity(format, mocktemplate);
        assertTrue(result);

    }


    @Test
    public void AddSolarActivityWithException() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("panels", "2");
        format.put("username", "rick123");

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity<>(format);

        JSONObject mockresponse = new JSONObject();
        mockresponse.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addSolarPanelActivity/"
                , entity, JSONObject.class)).thenThrow(ResourceAccessException.class);


        Client client = new Client();
        boolean result = client.addSolarPanelActivity(format, mocktemplate);
        assertFalse(result);
    }

    @Test
    public void updateCarTest() {
        JSONObject format = new JSONObject();
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");
        format.put("username", "rick");
        format.put("password", "hey");

        HttpEntity<JSONObject> entity = new HttpEntity(format);
        JSONObject jsonresult = new JSONObject();
        jsonresult.put("result", "success");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        when(mocktemplate.postForObject("http://localhost:8080/updateCar/",
                entity, JSONObject.class)).thenReturn(jsonresult);

        Client client = new Client();
        assertEquals(true, client.updateCar(format, mocktemplate));
    }

    @Test
    public void updateCarTestWithException() {
        JSONObject format = new JSONObject();
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");
        format.put("username", "rick");
        format.put("password", "hey");

        HttpEntity<JSONObject> entity = new HttpEntity(format);
        JSONObject jsonresult = new JSONObject();
        jsonresult.put("result", "success");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        when(mocktemplate.postForObject("http://localhost:8080/updateCar/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);

        Client client = new Client();
        assertEquals(false, client.updateCar(format, mocktemplate));
    }

    @Test
    public void AddCarTest() {
        JSONObject format = new JSONObject();
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");
        format.put("username", "rick");
        format.put("password", "hey");

        HttpEntity<JSONObject> entity = new HttpEntity(format);
        JSONObject jsonresult = new JSONObject();
        jsonresult.put("result", "success");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        when(mocktemplate.postForObject("http://localhost:8080/addCar/",
                entity, JSONObject.class)).thenReturn(jsonresult);

        Client client = new Client();
        assertEquals(true, client.addCar(format, mocktemplate));
    }

    @Test
    public void AddCarTestWithException() {
        JSONObject format = new JSONObject();
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");
        format.put("username", "rick");
        format.put("password", "hey");

        HttpEntity<JSONObject> entity = new HttpEntity(format);
        JSONObject jsonresult = new JSONObject();
        jsonresult.put("result", "success");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        when(mocktemplate.postForObject("http://localhost:8080/addCar/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);

        Client client = new Client();
        assertEquals(false, client.addCar(format, mocktemplate));
    }

    @Test
    public void getCarTest() {
        JSONObject format = new JSONObject();
        format.put("username", "rick");
        format.put("password", "hey");


        HttpEntity<JSONObject> entity = new HttpEntity(format);


        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("model", "R8");
        result.put("year", "2010");
        result.put("brand", "Audi");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        when(mocktemplate.postForObject("http://localhost:8080/getCar/",
                entity, JSONObject.class)).thenReturn(result);
        Car resultcar = new Car("2010", "Audi", "R8");

        Client client = new Client();
        assertEquals(resultcar.getYear(), client.getCar(format, mocktemplate).getYear());
        assertEquals(resultcar.getBrand(), client.getCar(format, mocktemplate).getBrand());
        assertEquals(resultcar.getModel(), client.getCar(format, mocktemplate).getModel());

    }

    @Test
    public void AddHouseTemperatureTest() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("time", "5");
        format.put("temperature", "5");


        format.put("username", "rick123");

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity<>(format);

        JSONObject mockresponse = new JSONObject();
        mockresponse.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addTemperatureActivity/"
                , entity, JSONObject.class)).thenReturn(mockresponse);


        Client client = new Client();
        boolean result = client.addTemperatureActivity(format, mocktemplate);
        assertTrue(result);

    }


    @Test
    public void AddHouseTemperatureActivityWithException() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("time", "5");
        format.put("temperature", "5");


        format.put("username", "rick123");

        RestTemplate mocktemplate = mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity<>(format);

        JSONObject mockresponse = new JSONObject();
        mockresponse.put("result", "success");
        when(mocktemplate.postForObject("http://localhost:8080/addTemperatureActivity/"
                , entity, JSONObject.class)).thenThrow(ResourceAccessException.class);


        Client client = new Client();
        boolean result = client.addTemperatureActivity(format, mocktemplate);
        assertFalse(result);

    }

    @Test
    public void getCarTestWithException() {
        JSONObject format = new JSONObject();
        format.put("username", "rick");
        format.put("password", "hey");

        HttpEntity<JSONObject> entity = new HttpEntity(format);


        JSONObject result = new JSONObject();
        result.put("result", "success");
        result.put("model", "R8");
        result.put("year", "2010");
        result.put("brand", "Audi");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        when(mocktemplate.postForObject("http://localhost:8080/getCar/",
                entity, JSONObject.class)).thenThrow(ResourceAccessException.class);

        Client client = new Client();
        assertNull(client.getCar(format, mocktemplate));
    }

    @Test
    public void getCarTestWithServerError() {
        JSONObject format = new JSONObject();
        format.put("username", "rick");
        format.put("password", "hey");

        HttpEntity<JSONObject> entity = new HttpEntity(format);


        JSONObject result = new JSONObject();
        result.put("result", "fail");
        result.put("model", "R8");
        result.put("year", "2010");
        result.put("brand", "Audi");
        RestTemplate mocktemplate = mock(RestTemplate.class);
        when(mocktemplate.postForObject("http://localhost:8080/getCar/",
                entity, JSONObject.class)).thenReturn(result);

        Client client = new Client();
        assertNull(client.getCar(format, mocktemplate));
    }


    @Test
    public void testAddUser()throws SQLException{
        JSONObject format= new JSONObject();
        format.put("username", "test");

        Client client= new Client();

        RestTemplate mocktemplate= mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        JSONObject json= new JSONObject();
        json.put("result", "success");

        when(mocktemplate.postForObject("http://localhost:8080/addUserAsFriend", entity, JSONObject.class)).thenReturn(json);
        //assertEquals(true , client.addUserAsFriend(format, mocktemplate));

        boolean result = client.addUserAsFriend(format, mocktemplate);
        assertTrue(result);
    }

    @Test
    public void testRank()throws SQLException{
        JSONObject format= new JSONObject();
        format.put("username", "test");

        Client client= new Client();

        RestTemplate mocktemplate= mock(RestTemplate.class);
        HttpEntity<JSONObject> entity = new HttpEntity(format);

        JSONObject json= new JSONObject();
        json.put("rank", 1);

        when(mocktemplate.postForObject("http://localhost:8080/getUserRank", entity, JSONObject.class)).thenReturn(json);
        assertEquals(1 , client.getUserRank(format, mocktemplate));

    }


}