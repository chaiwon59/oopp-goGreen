package server;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//import org.mockito.*;
//import org.mockito.runners.MockitoJUnitRunner;
//import org.junit.runner.RunWith;
//import static org.junit.Assert.assertTrue;
//import static org.junit.Assert.assertFalse;

//@RunWith(MockitoJUnitRunner.class)
public class RegisterUserControllerTest {


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
    public void testIsUniqueTrue() throws SQLException {
        when(mockdbconnection.connect()).thenReturn(mockConnection);
        String userUnique = "Select \"username\" from \"UserTable\" where \"username\"= ?";

        when(mockConnection.prepareStatement(userUnique)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);
        when(mockResultSet.getString(1)).thenReturn("test101");
        JSONObject input = new JSONObject();
        input.put("username", "test101");

        JSONObject expectedresult = new JSONObject();
        expectedresult.put("unique", true);

        RegisterUserController ruc= new RegisterUserController();
        assertEquals(expectedresult.toJSONString(), ruc.isUnique(input, mockdbconnection).toJSONString());

    }

    @Test
    public void testException() throws SQLException {
        when(mockPreparedStatement.executeQuery()).thenThrow(SQLException.class);
        when(mockdbconnection.connect()).thenReturn(mockConnection);
        String userUnique = "Select \"username\" from \"UserTable\" where \"username\"= ?";

        when(mockConnection.prepareStatement(userUnique)).thenReturn(mockPreparedStatement);

        JSONObject input = new JSONObject();
        input.put("username", "Test");

        RegisterUserController ruc= new RegisterUserController();

        JSONObject expectedresult = new JSONObject();
        expectedresult.put("unique", false);

        assertEquals(expectedresult.toJSONString(), ruc.isUnique(input, mockdbconnection).toJSONString());
    }

    @Test
    public void testIsUniqueFalse() throws SQLException {
        when(mockdbconnection.connect()).thenReturn(mockConnection);
        String userUnique = "Select \"username\" from \"UserTable\" where \"username\"= ?";

        when(mockConnection.prepareStatement(userUnique)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString(1)).thenReturn("test101");
        JSONObject input = new JSONObject();
        input.put("username", "test101");

        JSONObject expectedresult = new JSONObject();
        expectedresult.put("unique", false);

        RegisterUserController ruc= new RegisterUserController();
        assertEquals(expectedresult.toJSONString(), ruc.isUnique(input, mockdbconnection).toJSONString());

    }

    @Test
    public void testAddUserAsFriend() throws SQLException {
        JSONObject input= new JSONObject();
        input.put("username", "test");

        when(mockdbconnection.connect()).thenReturn(mockConnection);
        String addUser = "Insert into \"Friends\" (username, friend) values (?, ?)";

        when(mockConnection.prepareStatement(addUser)).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.execute()).thenReturn(true);



        JSONObject expectedresult = new JSONObject();
        expectedresult.put("result", "success");

        RegisterUserController ruc= new RegisterUserController();
        assertEquals(expectedresult.toJSONString(), ruc.addUserAsFriend(input, mockdbconnection).toJSONString() );
    }

//    @Before
//    public void setUp(){
//        MockitoAnnotations.initMocks(this);
//        rc1 = mock(RegisterUserController.class);
//        db = mock(DatabaseConnection.class);
//        connect = db.connect();
//        rc1.setDb(db);
//
//    }


//    @Test
//    public void testingmocker() throws SQLException{
//        ArgumentCaptor<String> stringcaptor = ArgumentCaptor.forClass(String.class);
//        String name = "hello12345";
//        String username = "hello12345";
//        String password = "hello123456";
//
//        rc1.register("hello", "hello", "hello", db);
//        verify(mockpst, times(4)).setString(anyInt(), stringcaptor.capture());
//        assertTrue(stringcaptor.getAllValues().get(0).equals(name));
//        assertTrue(stringcaptor.getAllValues().get(1).equals(username));
//        assertTrue(stringcaptor.getAllValues().get(2).equals(password));
//        assertTrue(stringcaptor.getAllValues().get(3).equals(db));
//    }


//    @Test
//    public void test_register(){
//        rc1.register("hello", "hello", "hello", db);
//        verify(rc1).register("hello","hello", "hello", db);
//    }
//    @Test
//    public void test_uniqueness(){
//        rc1.isunique("lalalalal", db);
//        verify(rc1).isunique("lalalalal", db);
//    }

//    @Test
//    public void testing()throws SQLException{
//      when(mockrst.getString(1).contentEquals("hellooo")).thenReturn(true);
//      when(mockrst.getString(2).contentEquals("hellooooo")).thenReturn(true);
//    }


//    @Before
//    public void setUp1() {
//        db = new DatabaseConnection();
//        connect = null;
//        rcu = new RegisterUserController();
//    }

//    @Test
//    public void registerTest() {
//        rcu.register(name1, username1, pass1, db);
//        boolean found = false;
//        String register = "Select \"name\", \"username\", \"password\" from \"UserTable\"";
//        String deleteRegister = "Delete from \"UserTable\" where \"username\"= '" + username1 + "'";
//        try {
//            connect = db.connect();
//            PreparedStatement pstregister = connect.prepareStatement(register);
//            ResultSet rsRegister = pstregister.executeQuery();
//
//            while (rsRegister.next()) {
//                if ((rsRegister.getString(1).equals(name1)) && (rsRegister.getString(2).equals(username1))) {
//                    found = true;
//                }
//            }
//
//            pstregister.close();
//            rsRegister.close();
//
//        } catch (SQLException e1) {
//            e1.printStackTrace();
//        }
//
//        org.junit.Assert.assertTrue(found);
//
//        try {
//            PreparedStatement pstDelete = connect.prepareStatement(deleteRegister);
//            pstDelete.execute();
//            pstDelete.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


//    @Test
//    public void isUniqueTest() {
//
//        rcu.register("-00001", username3, "00000", db);
//        rcu.register("-00001", username2, "00000", db);
//        Assert.assertTrue(rcu.isunique(username1, db));
//
//        String delete1 = "delete from \"UserTable\" where \"username\" = '" + username3 + "' ";
//        String delete2 = "delete from \"UserTable\" where \"username\" = '" + username2 + "'";
//        try {
//            connect = db.connect();
//            PreparedStatement pstDelete = connect.prepareStatement(delete1);
//            pstDelete.execute();
//            pstDelete.close();
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            connect = db.connect();
//
//            PreparedStatement pstDelete2 = connect.prepareStatement(delete2);
//            pstDelete2.execute();
//            pstDelete2.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//
//    }
}





