package server;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class FriendUserControllerTest {

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
    public void testGetRank() throws SQLException {
        when(mockdbconnection.connect()).thenReturn(mockConnection);

        JSONObject input= new JSONObject();
        input.put("username", "test1");
        String getfriendsQuery = "SELECT friend FROM \"Friends\", \"UserTable\""
                + " WHERE \"Friends\".friend = \"UserTable\".username"
                + " AND \"Friends\".username = ?"
                + "ORDER BY score DESC";

        when(mockConnection.prepareStatement(getfriendsQuery)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString(1)).thenReturn("test").thenReturn("test1");

        FriendsUserController fuc = new FriendsUserController();
        assertEquals(2, fuc.getUserRank(input, mockdbconnection).get("rank") );


    }

    @Test
    public void testGetRankNoUser() throws SQLException {
        when(mockdbconnection.connect()).thenReturn(mockConnection);

        JSONObject input= new JSONObject();
        input.put("username", "test2");
        String getfriendsQuery = "SELECT friend FROM \"Friends\", \"UserTable\""
                + " WHERE \"Friends\".friend = \"UserTable\".username"
                + " AND \"Friends\".username = ?"
                + "ORDER BY score DESC";

        when(mockConnection.prepareStatement(getfriendsQuery)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
        when(mockResultSet.getString(1)).thenReturn("test").thenReturn("test1");

        FriendsUserController fuc = new FriendsUserController();
        assertEquals(-1, fuc.getUserRank(input, mockdbconnection).get("rank") );


    }
}
