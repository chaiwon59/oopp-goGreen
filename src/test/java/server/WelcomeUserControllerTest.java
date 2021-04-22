package server;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gui.RegisterController;
import javafx.event.ActionEvent;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.jdbc.Sql;

import java.sql.*;

public class WelcomeUserControllerTest {

    @Mock
    private DatabaseConnection mockdbconnection;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;

    @Mock
    private ResultSet mockResultSet;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void testGetScore() throws SQLException {
        when(mockdbconnection.connect()).thenReturn(mockConnection);
        String score = "SELECT \"score\" FROM \"UserTable\" where \"username\" = ? ";

        when(mockConnection.prepareStatement(score)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("100");
        JSONObject input = new JSONObject();
        input.put("username", "Test");

        JSONObject expectedresult = new JSONObject();
        expectedresult.put("score", "100");

        WelcomeUserController wec = new WelcomeUserController();
        assertEquals(expectedresult.toJSONString(), wec.getScore(input, mockdbconnection).toJSONString());

    }


    @Test
    public void noUserTest() throws SQLException {
        when(mockdbconnection.connect()).thenReturn(mockConnection);
        String score = "SELECT \"score\" FROM \"UserTable\" where \"username\" = ? ";

        when(mockConnection.prepareStatement(score)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(!mockResultSet.next()).thenReturn(false);
        when(mockResultSet.getString(1)).thenReturn("-1");

        JSONObject input = new JSONObject();
        input.put("username", "Test");

        JSONObject expectedresult = new JSONObject();
        expectedresult.put("score", "-1");

        WelcomeUserController wec = new WelcomeUserController();
        assertEquals(expectedresult.toJSONString(), wec.getScore(input, mockdbconnection).toJSONString());
    }

    @Test
    public void testException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);
        when(mockdbconnection.connect()).thenReturn(mockConnection);
        String score = "SELECT \"score\" FROM \"UserTable\" where \"username\" = ? ";

        when(mockConnection.prepareStatement(score)).thenReturn(mockPreparedStatement);

        JSONObject input = new JSONObject();
        input.put("username", "Test");

        WelcomeUserController wec = new WelcomeUserController();

        JSONObject expectedresult = new JSONObject();
        expectedresult.put("score", -1);

        assertEquals(expectedresult.toJSONString(), wec.getScore(input, mockdbconnection).toJSONString());
    }
}
