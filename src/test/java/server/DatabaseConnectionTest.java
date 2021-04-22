package server;

import static java.sql.DriverManager.getConnection;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.validation.constraints.Null;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DriverManager.class)
public class DatabaseConnectionTest {

    @Test
    public void testMockDBConnection() throws Exception {

        mockStatic(DriverManager.class);
        Connection mockConnection = mock(Connection.class);
        String jdbUrl = "jdbc:postgresql://localhost/OOPP";
        String username = "postgres";
        String password = "postgres";
        Mockito.when(getConnection(jdbUrl, username, password)).thenReturn(mockConnection);
        Mockito.when(mockConnection.getClientInfo()).thenReturn(new Properties());
        DatabaseConnection db = new DatabaseConnection();
        assertEquals(mockConnection.getClientInfo(), db.connect().getClientInfo());
    }

    @Test
    public void testMockDBConnectionThrowsException() throws Exception {

        mockStatic(DriverManager.class);
        Connection mockConnection = mock(Connection.class);
        String jdbUrl = "jdbc:postgresql://localhost/OOPP";
        String username = "postgres";
        String password = "postgres";
        PowerMockito.when(DriverManager.getConnection(jdbUrl, username, password)).thenThrow(SQLException.class);
        try {
            getConnection(jdbUrl, username, password);
        } catch (SQLException e) {
            System.out.println("WOK");
        }
        DatabaseConnection db = mock(DatabaseConnection.class);
        when(db.connect()).thenCallRealMethod();
        assertEquals(new Properties(), db.connect().getClientInfo());
    }
}