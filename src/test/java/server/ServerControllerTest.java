package server;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc

public class ServerControllerTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    @Mock
    DatabaseConnection databaseMock;
    @Mock
    Connection connectionMock;
    @Mock
    PreparedStatement preparedStatementMock;
    @Mock
    PreparedStatement preparedStatementMock2;
    @Mock
    PreparedStatement preparedStatementMock3;
    @Mock
    PreparedStatement preparedStatementMock4;
    @Mock
    ResultSet resultSetMock;
    @Mock
    ResultSet resultSetMock2;
    @Mock
    ResultSet resultSetMock3;
    @Mock
    ResultSet resultSetMock4;
    @Autowired
    private MockMvc mockMvc;

    @Before
    public void initialize() {
        //set up authentication mocking
        try {
            String getPasswordQuery = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
            String getSaltQuery = "SELECT \"salt\" FROM \"UserTable\" WHERE \"username\" = ?";
            when(connectionMock.prepareStatement(getPasswordQuery)).thenReturn(preparedStatementMock2);
            when(connectionMock.prepareStatement(getSaltQuery)).thenReturn(preparedStatementMock3);

            when(preparedStatementMock2.executeQuery()).thenReturn(resultSetMock2);
            when(preparedStatementMock3.executeQuery()).thenReturn(resultSetMock3);

            when(resultSetMock2.next()).thenReturn(true);
            when(resultSetMock3.next()).thenReturn(true);

            when(resultSetMock2.getString(1)).thenReturn("$2a$10$TV2urFjhamNfLLJ8EGvY.OxYfBizFIFo6W0MitCq6IkZdREwDvmfS");
            when(resultSetMock3.getString(1)).thenReturn("$2a$10$TV2urFjhamNfLLJ8EGvY.O");
        } catch (SQLException e) {
            System.out.println("Something went wrong when setting up the test");
        }
    }

    @Test
    //sends an http request to the server and checks whether the response is the expected response, this test doesnt need the server to be online it simulates all the steps
    //a basic request would do
    public void testgetCo2() throws Exception {

        JSONObject json = new JSONObject();
        json.put("origin_airport", "AMS");
        json.put("destination_airport", "LAX");
        this.mockMvc.perform(post("/getco2/flights")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json.toJSONString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("3975.2 kg")));
    }

    @Test
    public void testAddFoodActivity() throws Exception {

        JSONObject format = new JSONObject();
        format.put("mealtype", "Vegan");
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("localproduce", "No");

        //set up food specific mocking
        String getFoodCO2query = "SELECT co2_saved FROM \"Food\" WHERE diet = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock getting the co2 from the diet type
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getFoodCO2query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(4);

        //mock adding new activity
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock updating total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);

        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "success");
        ServerController testcontroller = new ServerController();
        assertEquals(testcontroller.addFoodActivity(format, databaseMock).toJSONString(), jsonresponse.toJSONString());
    }

    @Test
    public void testAddFoodActivityEmptyResultSet() throws Exception {

        JSONObject format = new JSONObject();
        format.put("mealtype", "Vegan");
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("localproduce", "No");

        //set up food specific mocking
        String getFoodCO2query = "SELECT co2_saved FROM \"Food\" WHERE diet = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock getting the co2 from the diet type
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getFoodCO2query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);
        when(resultSetMock.getInt(1)).thenReturn(4);

        //mock adding new activity
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock updating total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);

        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "success");
        ServerController testcontroller = new ServerController();
        assertEquals(testcontroller.addFoodActivity(format, databaseMock).toJSONString(), jsonresponse.toJSONString());
    }

    @Test
    public void testAddFoodActivitywithLocalProduce() throws Exception {
        JSONObject format = new JSONObject();
        format.put("mealtype", "Vegan");
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("localproduce", "Yes");

        //set up food specific mocking
        String getFoodCO2query = "SELECT co2_saved FROM \"Food\" WHERE diet = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock getting the co2 from the diet type
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getFoodCO2query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(4);

        //mock adding new activity
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock updating total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);

        //verify that the method returns the expected value
        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "success");
        ServerController testcontroller = new ServerController();
        assertEquals(testcontroller.addFoodActivity(format, databaseMock).toJSONString(), jsonresponse.toJSONString());
    }

    @Test
    public void testAddFoodActivityWithWrongCredentials() throws Exception {
        JSONObject format = new JSONObject();
        format.put("mealtype", "Vegan");
        format.put("username", "TestFoodActivity");
        format.put("password", "helloo");
        format.put("localproduce", "Yes");

        //set up food specific mocking
        String getFoodCO2query = "SELECT co2_saved FROM \"Food\" WHERE diet = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock getting the co2 from the diet type
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getFoodCO2query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(4);

        //mock adding new activity
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock updating total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);

        //verify that the method returns the expected value
        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "fail");
        ServerController testcontroller = new ServerController();
        assertEquals(testcontroller.addFoodActivity(format, databaseMock).toJSONString(), jsonresponse.toJSONString());
    }

    @Test
    public void testEmptyResultSet() throws Exception {
        JSONObject format = new JSONObject();
        format.put("mealtype", "Indian");
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("localproduce", "No");

        //set up food specific mocking
        String getFoodCO2query = "SELECT co2_saved FROM \"Food\" WHERE diet = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock getting the co2 from the diet type
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getFoodCO2query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getInt(1)).thenReturn(4);

        //mock adding new activity
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock updating total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);

        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "success");
        ServerController testcontroller = new ServerController();
        assertEquals(testcontroller.addFoodActivity(format, databaseMock).toJSONString(), jsonresponse.toJSONString());
    }

    @Test
    public void testgetUserActivities() throws Exception {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");

        //set up retrieving activities mocks
        String getActivitiesQuery = "SELECT * FROM  \"UserActivities\" WHERE username = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getActivitiesQuery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        //first call returns true, next next() call returns false
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        //set up expected returns from the database
        when(resultSetMock.getString("activity")).thenReturn("Vegetarian meal")
                .thenReturn("Vegetarian meal")
                .thenReturn("100 km bike trip instead of Car trip")
                .thenReturn("100 km bike trip instead of Car trip")
                .thenReturn("100 km bike trip instead of Car trip")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by lowering Temperature by 2 degrees");

        when(resultSetMock.getString("co2_saved")).thenReturn("2").thenReturn("100")
                .thenReturn("25").thenReturn("2");
//        when(resultSetMock.getString("activity").contains("meal")).thenReturn(true);

        //generate expected result
        String[] expectedresult = new String[3];
        expectedresult[0] = "\"Food\",\"Transport\",\"Energy\",\"Energy\"";
        expectedresult[1] = "\"Vegetarian meal\",\"100 km bike trip instead of Car trip\"," +
                "\"Save energy by adding 2 Solar panels\",\"Save energy by lowering Temperature by 2 degrees\"";
        expectedresult[2] = "\"2\",\"100\",\"25\",\"2\"";

        ServerController testcontroller = new ServerController();
        String[] actualresult = testcontroller.getUserActivities(format, databaseMock);

        assertEquals(expectedresult[0], actualresult[0]);
        assertEquals(expectedresult[1], actualresult[1]);
        assertEquals(expectedresult[2], actualresult[2]);
    }

    @Test
    public void testgetUserActivitiesWithoutEnergy() throws Exception {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");

        //set up retrieving activities mocks
        String getActivitiesQuery = "SELECT * FROM  \"UserActivities\" WHERE username = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getActivitiesQuery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        //first call returns true, next next() call returns false
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        //set up expected returns from the database
        when(resultSetMock.getString("activity")).thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by adding 2 Solar panels")
                .thenReturn("Save energy by lowering Temperature 2 degrees");

        when(resultSetMock.getString("co2_saved")).thenReturn("2").thenReturn("100");
        //        when(resultSetMock.getString("activity").contains("meal")).thenReturn(true);

        //generate expected result
        String[] expectedresult = new String[3];
        expectedresult[0] = "\"Energy\",\"Energy\"";
        expectedresult[1] = "\"Save energy by adding 2 Solar panels\",\"Save energy by lowering Temperature 2 degrees\"";
        expectedresult[2] = "\"2\",\"100\"";

        ServerController testcontroller = new ServerController();
        String[] actualresult = testcontroller.getUserActivities(format, databaseMock);

        assertEquals(expectedresult[0], actualresult[0]);
        assertEquals(expectedresult[1], actualresult[1]);
        assertEquals(expectedresult[2], actualresult[2]);
    }

    @Test
    public void testgetUserActivitiesTemperature() throws Exception {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");

        //set up retrieving activities mocks
        String getActivitiesQuery = "SELECT * FROM  \"UserActivities\" WHERE username = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getActivitiesQuery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        //first call returns true, next next() call returns false
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

        //set up expected returns from the database
        when(resultSetMock.getString("activity")).thenReturn("Vegetarian meal")
                .thenReturn("Vegetarian meal")
                .thenReturn("100 km bike trip instead of Car trip")
                .thenReturn("100 km bike trip instead of Car trip")
                .thenReturn("100 km bike trip instead of Car trip")
                .thenReturn("Save energy by lowering Temperature by 2 degrees");
        when(resultSetMock.getString("co2_saved")).thenReturn("2").thenReturn("100")
                .thenReturn("25");
//        when(resultSetMock.getString("activity").contains("meal")).thenReturn(true);

        //generate expected result
        String[] expectedresult = new String[3];
        expectedresult[0] = "\"Food\",\"Transport\",\"Energy\"";
        expectedresult[1] = "\"Vegetarian meal\",\"100 km bike trip instead of Car trip\"," +
                "\"Save energy by lowering Temperature by 2 degrees\"";
        expectedresult[2] = "\"2\",\"100\",\"25\"";

        ServerController testcontroller = new ServerController();
        String[] actualresult = testcontroller.getUserActivities(format, databaseMock);

        assertEquals(expectedresult[0], actualresult[0]);
        assertEquals(expectedresult[1], actualresult[1]);
        assertEquals(expectedresult[2], actualresult[2]);
    }

    @Test
    public void testgetUserActivitiesWrongCredentials() throws Exception {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hellooo");


        //set up retrieving activities mocks
        String getActivitiesQuery = "SELECT * FROM  \"UserActivities\" WHERE username = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getActivitiesQuery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        //first call returns true, next next() call returns false
        when(resultSetMock.next()).thenReturn(true).thenReturn(false);

        //set up expected returns from the database
        when(resultSetMock.getString("activity")).thenReturn("Vegetarian meal");
        when(resultSetMock.getString("co2_saved")).thenReturn("2");


        ServerController testcontroller = new ServerController();
        String[] actualresult = testcontroller.getUserActivities(format, databaseMock);
        assertNull(actualresult);
    }

    @Test
    public void testgetUserActivitiesEmpty() throws Exception {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");

        //set up retrieving activities mocks
        String getActivitiesQuery = "SELECT * FROM  \"UserActivities\" WHERE username = ?";

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\","
                + " \"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getActivitiesQuery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        //first call returns false, meaning there is an empty result set
        when(resultSetMock.next()).thenReturn(false);

        //generate expected result
        String[] expectedresult = new String[3];
        expectedresult[0] = "";
        expectedresult[1] = "";
        expectedresult[2] = "";

        ServerController testcontroller = new ServerController();
        String[] actualresult = testcontroller.getUserActivities(format, databaseMock);

        assertEquals(expectedresult[0], actualresult[0]);
        assertEquals(expectedresult[1], actualresult[1]);
        assertEquals(expectedresult[2], actualresult[2]);
    }

    @Test
    public void testTransportActivityCarPlane() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("modelused", "automobile_trips");
        format.put("modelrejected", "flights");
        format.put("distance", "123");
        format.put("co2_saved", 211);

        //set up adding activities mocks

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", "
                + "\"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock all calls t oupdate total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock2);
        when(preparedStatementMock2.execute()).thenReturn(true);
        //first call returns false, meaning there is an empty result set

        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "success");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addTransportActivity(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }

    @Test
    public void AddTransportActivityBikeCar() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("modelused", "bike_trips");
        format.put("modelrejected", "automobile_trips");
        format.put("distance", "123");
        format.put("co2_saved", 211);

        //set up adding activities mocks

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", "
                + "\"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock all calls t oupdate total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock2);
        when(preparedStatementMock2.execute()).thenReturn(true);
        //first call returns false, meaning there is an empty result set

        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "success");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addTransportActivity(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }

    @Test
    public void AddTransportActivityPublicTransportCar() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("modelused", "bus_trips");
        format.put("modelrejected", "automobile_trips");
        format.put("distance", "123");
        format.put("co2_saved", 211);

        //set up adding activities mocks

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", "
                + "\"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock all calls t oupdate total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock2);
        when(preparedStatementMock2.execute()).thenReturn(true);
        //first call returns false, meaning there is an empty result set

        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "success");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addTransportActivity(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }

    @Test
    public void AddTransportActivityWrongCredentials() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "helloo");
        format.put("modelused", "bike_trips");
        format.put("modelrejected", "automobile_trips");
        format.put("distance", "123");
        format.put("co2_saved", 211);

        //set up adding activities mocks

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", "
                + "\"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock all calls t oupdate total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock2);
        when(preparedStatementMock2.execute()).thenReturn(true);
        //first call returns false, meaning there is an empty result set

        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "fail");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addTransportActivity(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }

    @Test
    public void AddTransportActivityPlanePublic() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("modelused", "flights");
        format.put("modelrejected", "bus_trips");
        format.put("distance", "123");
        format.put("co2_saved", 211);

        //set up adding activities mocks

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", "
                + "\"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock all calls t oupdate total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock2);
        when(preparedStatementMock2.execute()).thenReturn(true);
        //first call returns false, meaning there is an empty result set

        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "success");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addTransportActivity(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }

    @Test
    public void AddTransportActivityBusBike() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("modelused", "bus_trips");
        format.put("modelrejected", "bike_trip");
        format.put("distance", "123");
        format.put("co2_saved", 211);

        //set up adding activities mocks

        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", "
                + "\"co2_saved\")" + "VALUES ( ?, ?, ?);";


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ?"
                + " WHERE username = ?";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        //mock all calls t oupdate total score
        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock2);
        when(preparedStatementMock2.execute()).thenReturn(true);
        //first call returns false, meaning there is an empty result set

        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "success");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addTransportActivity(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }

    @Test
    public void AddFriendTest() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("friend", "rik2");

        //set up adding friend mocks
        String testFriendExists = "SELECT \"username\" FROM \"UserTable\" "
                + "WHERE \"username\" = ?";


        String query = "INSERT INTO \"Friends\"(username, friend) VALUES (?, ?)";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(testFriendExists)).thenReturn(preparedStatementMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);


        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "success");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addFriend(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }


    @Test
    public void AddFriendTestNonExistingUsername() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("friend", "rikddd2");

        //set up adding friend mocks
        String testFriendExists = "SELECT \"username\" FROM \"UserTable\" "
                + "WHERE \"username\" = ?";


        String query = "INSERT INTO \"Friends\"(username, friend) VALUES (?, ?)";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(testFriendExists)).thenReturn(preparedStatementMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);


        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "fail");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addFriend(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }

    @Test
    public void AddFriendTestWrongCredentials() throws SQLException {
        //setup input data
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "helloo");
        format.put("friend", "rikddd2");

        //set up adding friend mocks
        String testFriendExists = "SELECT \"username\" FROM \"UserTable\" "
                + "WHERE \"username\" = ?";


        String query = "INSERT INTO \"Friends\"(username, friend) VALUES (?, ?)";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(testFriendExists)).thenReturn(preparedStatementMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);


        //generate expected result
        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "fail");


        ServerController testcontroller = new ServerController();
        JSONObject actualresult = testcontroller.addFriend(format, databaseMock);
        assertEquals(expectedresult.toJSONString(), actualresult.toJSONString());
    }

    @Test
    public void getFriendsTestSorted() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");

        //set up retrieving activities mocks
        String getfriendsQuery = "SELECT friend, score FROM \"Friends\", \"UserTable\""
                + " WHERE \"Friends\".friend = \"UserTable\".username"
                + " AND \"Friends\".username = ?" +
                "ORDER BY score DESC";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getfriendsQuery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        //first call returns true, next next() call returns false
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        //set up expected returns from the database
        when(resultSetMock.getString("friend")).thenReturn("Rik2")
                .thenReturn("Rik1");

        when(resultSetMock.getString("score")).thenReturn("100").thenReturn("2");

        //generate expected result
        String[] expectedresult = new String[2];
        expectedresult[0] = "\"Rik2\",\"Rik1\"";
        expectedresult[1] = "\"100\",\"2\"";

        ServerController testcontroller = new ServerController();
        String[] actualresult = testcontroller.getFriends(format, databaseMock);

        assertEquals(expectedresult[0], actualresult[0]);
        assertEquals(expectedresult[1], actualresult[1]);
    }

    @Test
    public void getFriendsTestWrongCredentials() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "helllo");

        //set up retrieving activities mocks
        String getfriendsQuery = "SELECT friend, score FROM \"Friends\", \"UserTable\""
                + " WHERE \"Friends\".friend = \"UserTable\".username"
                + " AND \"Friends\".username = ?" +
                "ORDER BY score DESC";

        //mock all the calls that involve database functions
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getfriendsQuery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);

        //first call returns true, next next() call returns false
        when(resultSetMock.next()).thenReturn(true).thenReturn(true).thenReturn(false);

        //set up expected returns from the database
        when(resultSetMock.getString("friend")).thenReturn("Rik1")
                .thenReturn("Rik2");

        when(resultSetMock.getString("score")).thenReturn("2").thenReturn("100");

        //generate expected result


        ServerController testcontroller = new ServerController();
        String[] actualresult = testcontroller.getFriends(format, databaseMock);
        assertNull(actualresult);
    }

    @Test
    public void createUserTest() throws SQLException {
        JSONObject newUser = new JSONObject();
        newUser.put("username", "testUsername");
        newUser.put("password", "testPassword");
        newUser.put("name", "testName");

        String query = "INSERT INTO \"UserTable\"(\"username\", \"password\",\"score\", "
                + "\"name\", \"salt\")"
                + "VALUES ( ?, ?, 0, ?, ?);";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);

        JSONObject testResponse = new JSONObject();
        testResponse.put("result", "success");

        ServerController testServer = new ServerController();
        JSONObject actualResponse = testServer.createUser(newUser, databaseMock);
        assertEquals(testResponse.get("result"), actualResponse.get("result"));
    }

    @Test
    public void deleteUserTest() throws SQLException {
        JSONObject user = new JSONObject();
        user.put("username", "testUsername");

        String query = "BEGIN;" +
                "DELETE FROM \"UserTable\" WHERE \"username\" = " + "?" + ";" +
                "DELETE FROM \"UserActivities\" WHERE \"username\" = ?; " +
                "DELETE FROM \"Friends\" WHERE \"username\" = ? or friend = ? ;" +
                "COMMIT;";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject testResponse = new JSONObject();
        testResponse.put("result", "success");

        ServerController testServer = new ServerController();
        JSONObject actualResponse = testServer.deleteUser(user, databaseMock);
        assertEquals(testResponse.get("result"), actualResponse.get("result"));
    }

    @Test
    public void authenticateTest() throws SQLException {
        JSONObject userLogin = new JSONObject();
        userLogin.put("username", "testUsername");
        userLogin.put("password", "hello");

        String getPasswordQuery = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
        String getSaltQuery = "SELECT \"salt\" FROM \"UserTable\" WHERE \"username\" = ?";
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getPasswordQuery)).thenReturn(preparedStatementMock2);
        when(connectionMock.prepareStatement(getSaltQuery)).thenReturn(preparedStatementMock3);
        when(preparedStatementMock2.executeQuery()).thenReturn(resultSetMock2);
        when(preparedStatementMock3.executeQuery()).thenReturn(resultSetMock3);
        when(resultSetMock2.next()).thenReturn(true);
        when(resultSetMock3.next()).thenReturn(true);
        when(resultSetMock2.getString(1)).thenReturn("$2a$10$TV2urFjhamNfLLJ8EGvY.OxYfBizFIFo6W0MitCq6IkZdREwDvmfS");
        when(resultSetMock3.getString(1)).thenReturn("$2a$10$TV2urFjhamNfLLJ8EGvY.O");
        ServerController serverController = new ServerController();
        assertEquals("access granted", serverController.authenticate(userLogin, databaseMock).get("result"));
    }

    @Test
    public void authenticateTestNoSalt() throws SQLException {
        JSONObject userLogin = new JSONObject();
        userLogin.put("username", "testUsername");
        userLogin.put("password", "hello");

        String getPasswordQuery = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
        String getSaltQuery = "SELECT \"salt\" FROM \"UserTable\" WHERE \"username\" = ?";
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getPasswordQuery)).thenReturn(preparedStatementMock2);
        when(connectionMock.prepareStatement(getSaltQuery)).thenReturn(preparedStatementMock3);
        when(preparedStatementMock2.executeQuery()).thenReturn(resultSetMock2);
        when(preparedStatementMock3.executeQuery()).thenReturn(resultSetMock3);
        when(resultSetMock2.next()).thenReturn(false);
        when(resultSetMock3.next()).thenReturn(false);
        when(resultSetMock2.getString(1)).thenReturn("$2a$10$TV2urFjhamNfLLJ8EGvY.OxYfBizFIFo6W0MitCq6IkZdREwDvmfS");
        when(resultSetMock3.getString(1)).thenReturn("$2a$10$TV2urFjhamNfLLJ8EGvY.O");
        ServerController serverController = new ServerController();
        assertEquals("access denied", serverController.authenticate(userLogin, databaseMock).get("result"));
    }

    @Test
    public void authenticatewrongCredentialsTest() throws SQLException {
        JSONObject userLogin = new JSONObject();
        userLogin.put("username", "testUsername");
        userLogin.put("password", "helloo");

        String getPasswordQuery = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
        String getSaltQuery = "SELECT \"salt\" FROM \"UserTable\" WHERE \"username\" = ?";
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getPasswordQuery)).thenReturn(preparedStatementMock2);
        when(connectionMock.prepareStatement(getSaltQuery)).thenReturn(preparedStatementMock3);
        when(preparedStatementMock2.executeQuery()).thenReturn(resultSetMock2);
        when(preparedStatementMock3.executeQuery()).thenReturn(resultSetMock3);
        when(resultSetMock2.next()).thenReturn(true);
        when(resultSetMock3.next()).thenReturn(true);
        when(resultSetMock2.getString(1)).thenReturn("$2a$10$TV2urFjhamNfLLJ8EGvY.OxYfBizFIFo6W0MitCq6IkZdREwDvmfS");
        when(resultSetMock3.getString(1)).thenReturn("$2a$10$TV2urFjhamNfLLJ8EGvY.O");
        ServerController serverController = new ServerController();
        assertEquals("access denied", serverController.authenticate(userLogin, databaseMock).get("result"));
    }

    @Test
    public void authenticateEmtpyResultTest() throws SQLException {
        JSONObject userLogin = new JSONObject();
        userLogin.put("username", "testUsername");
        userLogin.put("password", "helloo");

        String getPasswordQuery = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
        String getSaltQuery = "SELECT \"salt\" FROM \"UserTable\" WHERE \"username\" = ?";
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(getPasswordQuery)).thenReturn(preparedStatementMock2);
        when(connectionMock.prepareStatement(getSaltQuery)).thenReturn(preparedStatementMock3);
        when(preparedStatementMock2.executeQuery()).thenReturn(resultSetMock2);
        when(preparedStatementMock3.executeQuery()).thenReturn(resultSetMock3);
        when(resultSetMock2.next()).thenReturn(false);
        when(resultSetMock3.next()).thenReturn(false);
        ServerController serverController = new ServerController();
        assertEquals("access denied", serverController.authenticate(userLogin, databaseMock).get("result"));
    }

    @Test
    public void getPasswordTest() throws SQLException {
        JSONObject user = new JSONObject();
        user.put("username", "testUsername");
        user.put("password", "123");

        String query = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString(1)).thenReturn("hackerman");

        //generate expected result
        JSONObject expectedResult = new JSONObject();
        expectedResult.put("password", "hackerman");

        ServerController testController = new ServerController();
        JSONObject actualResult = testController.getPassword(user, databaseMock);
        assertEquals(expectedResult.toJSONString(), actualResult.toJSONString());

    }

    @Test
    public void getPasswordTestNoResult() throws SQLException {
        JSONObject user = new JSONObject();
        user.put("username", "testUsername");
        user.put("password", "123");

        String query = "SELECT \"password\" FROM \"UserTable\" WHERE \"username\" = ?";
        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);
        when(resultSetMock.getString(1)).thenReturn("hackerman");

        //generate expected result
        JSONObject expectedResult = new JSONObject();
        expectedResult.put("password", "");

        ServerController testController = new ServerController();
        JSONObject actualResult = testController.getPassword(user, databaseMock);
        assertEquals(expectedResult.toJSONString(), actualResult.toJSONString());

    }

    @Test
    public void changePasswordTestWrongAuthentication() throws SQLException {
        JSONObject user = new JSONObject();
        user.put("username", "testUsername");
        user.put("password", "helllo");

        String query1 = "UPDATE \"UserTable\" "
                + "SET \"salt\" = ?"
                + "WHERE \"username\" = ?";

        String query2 = "UPDATE \"UserTable\" "
                + "SET \"password\" = ?"
                + "WHERE \"username\" = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query1)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query2)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject testResponse = new JSONObject();
        testResponse.put("result", "access denied");

        ServerController testServer = new ServerController();
        JSONObject actualResponse = testServer.changePassword(user, databaseMock);
        assertEquals(testResponse.get("result"), actualResponse.get("result"));
    }

    @Test
    public void changePasswordTest() throws SQLException {
        JSONObject user = new JSONObject();
        user.put("username", "testUsername");
        user.put("password", "hello");

        String query1 = "UPDATE \"UserTable\" "
                + "SET \"salt\" = ?"
                + "WHERE \"username\" = ?";

        String query2 = "UPDATE \"UserTable\" "
                + "SET \"password\" = ?"
                + "WHERE \"username\" = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query1)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query2)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject testResponse = new JSONObject();
        testResponse.put("result", "success");

        ServerController testServer = new ServerController();
        JSONObject actualResponse = testServer.changePassword(user, databaseMock);
        assertEquals(testResponse.get("result"), actualResponse.get("result"));
    }

    @Test
    public void getNameTest() throws SQLException {
        JSONObject user = new JSONObject();
        user.put("username", "testUsername");
        user.put("password", "testPassword");
        user.put("name", "testName");

        String query = "SELECT \"name\" FROM \"UserTable\" WHERE \"username\" = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString(1)).thenReturn("testName");

        //generate expected result
        JSONObject expectedResult = new JSONObject();
        expectedResult.put("name", "testName");

        ServerController testController = new ServerController();
        JSONObject actualResult = testController.getName(user, databaseMock);
        assertEquals(expectedResult.toJSONString(), actualResult.toJSONString());
    }

    @Test
    public void getNameTestEmpty() throws SQLException {
        JSONObject user = new JSONObject();
        user.put("username", "testUsername");
        user.put("password", "testPassword");
        user.put("name", "testName");

        String query = "SELECT \"name\" FROM \"UserTable\" WHERE \"username\" = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);
        when(resultSetMock.getString(1)).thenReturn("testName");

        //generate expected result
        JSONObject expectedResult = new JSONObject();
        expectedResult.put("name", "");

        ServerController testController = new ServerController();
        JSONObject actualResult = testController.getName(user, databaseMock);
        assertEquals(expectedResult.toJSONString(), actualResult.toJSONString());
    }

    @Test
    public void AddSolarPanelTest() throws Exception {
        JSONObject input = new JSONObject();
        input.put("username", "rick");
        input.put("password", "hello");

        input.put("panels", "2");


        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", \"co2_saved\")"
                + "VALUES ( ?, ?, ?);";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ? "
                + "WHERE username = ?";

        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "success");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.addSolarPanelActivity(input, databaseMock).toJSONString());
    }

    @Test
    public void AddSolarPanelTestWrongPassword() throws Exception {
        JSONObject input = new JSONObject();
        input.put("username", "rick");
        input.put("password", "helllo");

        input.put("panels", "2");


        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", \"co2_saved\")"
                + "VALUES ( ?, ?, ?);";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ? "
                + "WHERE username = ?";

        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "fail");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.addSolarPanelActivity(input, databaseMock).toJSONString());
    }

    @Test
    public void updateCarTest() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");

        String checkifexists = "SELECT username FROM \"UserCar\" WHERE username = ?";

        String query = "UPDATE \"UserCar\" SET \"carBrand\" = ?, \"carBuildYear\" = ?, \"carModel\" = ? WHERE \"username\" = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(connectionMock.prepareStatement(checkifexists)).thenReturn(preparedStatementMock4);
        when(preparedStatementMock4.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "success");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.updateCar(format, databaseMock).toJSONString());
    }

    @Test
    public void updateCarTestWrongCredentials() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "helloo");
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");

        String checkifexists = "SELECT username FROM \"UserCar\" WHERE username = ?";

        String query = "UPDATE \"UserCar\" SET \"carBrand\" = ?, \"carBuildYear\" = ?, \"carModel\" = ? WHERE \"username\" = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(connectionMock.prepareStatement(checkifexists)).thenReturn(preparedStatementMock4);
        when(preparedStatementMock4.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "fail");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.updateCar(format, databaseMock).toJSONString());
    }

    @Test
    public void updateCarTestEmptyResult() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");

        String checkifexists = "SELECT username FROM \"UserCar\" WHERE username = ?";

        String query = "UPDATE \"UserCar\" SET \"carBrand\" = ?, \"carBuildYear\" = ?, \"carModel\" = ? WHERE \"username\" = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(connectionMock.prepareStatement(checkifexists)).thenReturn(preparedStatementMock4);
        when(preparedStatementMock4.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "fail");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.updateCar(format, databaseMock).toJSONString());
    }

    @Test
    public void addCarTest() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");



        String query = "INSERT INTO \"UserCar\"(\"username\", \"carBrand\", \"carBuildYear\", \"carModel\") VALUES (?,?,?,?)";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "success");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.addCar(format, databaseMock).toJSONString());
    }

    @Test
    public void addCarTestWrongCredentials() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "helloo");
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");



        String query = "INSERT INTO \"UserCar\"(\"username\", \"carBrand\", \"carBuildYear\", \"carModel\") VALUES (?,?,?,?)";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "fail");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.addCar(format, databaseMock).toJSONString());
    }

    @Test
    public void addCarTestDuplicateCar() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");
        format.put("model", "R8");
        format.put("buildyear", "2010");
        format.put("brand", "Audi");



        String query = "INSERT INTO \"UserCar\"(\"username\", \"carBrand\", \"carBuildYear\", \"carModel\") VALUES (?,?,?,?)";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenThrow(PSQLException.class);

        JSONObject result = new JSONObject();
        result.put("result", "fail");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.addCar(format, databaseMock).toJSONString());
    }

    @Test
    public void getCarTest() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");


        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "success");
        jsonresponse.put("model", "R8");
        jsonresponse.put("year", "2010");
        jsonresponse.put("brand", "Audi");

        String query = "SELECT \"carBuildYear\", \"carBrand\", \"carModel\" FROM \"UserCar\" WHERE username = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString(3)).thenReturn("R8");
        when(resultSetMock.getString(1)).thenReturn("2010");
        when(resultSetMock.getString(2)).thenReturn("Audi");

        ServerController serverController = new ServerController();
        assertEquals(jsonresponse.toJSONString(), serverController.getCar(format, databaseMock).toJSONString());
    }

    @Test
    public void getCarTestWrongCredentials() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "helloo");


        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "fail");

        String query = "SELECT \"carBuildYear\", \"carBrand\", \"carModel\" FROM \"UserCar\" WHERE username = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(true);
        when(resultSetMock.getString(3)).thenReturn("R8");
        when(resultSetMock.getString(1)).thenReturn("2010");
        when(resultSetMock.getString(2)).thenReturn("Audi");

        ServerController serverController = new ServerController();
        assertEquals(jsonresponse.toJSONString(), serverController.getCar(format, databaseMock).toJSONString());
    }

    @Test
    public void getCarTestNoCar() throws SQLException {
        JSONObject format = new JSONObject();
        format.put("username", "TestFoodActivity");
        format.put("password", "hello");


        JSONObject jsonresponse = new JSONObject();
        jsonresponse.put("result", "fail");

        String query = "SELECT \"carBuildYear\", \"carBrand\", \"carModel\" FROM \"UserCar\" WHERE username = ?";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
        when(resultSetMock.next()).thenReturn(false);
        when(resultSetMock.getString(3)).thenReturn("R8");
        when(resultSetMock.getString(1)).thenReturn("2010");
        when(resultSetMock.getString(2)).thenReturn("Audi");

        ServerController serverController = new ServerController();
        assertEquals(jsonresponse.toJSONString(), serverController.getCar(format, databaseMock).toJSONString());
    }


    @Test
    public void addTemperatureActivityTest() throws Exception {
        JSONObject input = new JSONObject();
        input.put("username", "rick");
        input.put("password", "hello");

        input.put("time", "5");
        input.put("temperature", "5");


        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", \"co2_saved\")"
                + "VALUES ( ?, ?, ?);";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ? "
                + "WHERE username = ?";

        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "success");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.addTemperatureActivity(input, databaseMock).toJSONString());
    }

    @Test
    public void addTemperatureActivityTestWrongPassword() throws Exception {
        JSONObject input = new JSONObject();
        input.put("username", "rick");
        input.put("password", "helllo");

        input.put("time", "5");
        input.put("temperature", "5");


        String query = "INSERT INTO \"UserActivities\"(\"username\", \"activity\", \"co2_saved\")"
                + "VALUES ( ?, ?, ?);";

        when(databaseMock.connect()).thenReturn(connectionMock);
        when(connectionMock.prepareStatement(query)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);


        String totalscorequery = "UPDATE \"UserTable\" SET \"score\" = \"score\" + ? "
                + "WHERE username = ?";

        when(connectionMock.prepareStatement(totalscorequery)).thenReturn(preparedStatementMock);
        when(preparedStatementMock.execute()).thenReturn(true);

        JSONObject result = new JSONObject();
        result.put("result", "fail");
        ServerController serverController = new ServerController();
        assertEquals(result.toJSONString(), serverController.addTemperatureActivity(input, databaseMock).toJSONString());
    }

}