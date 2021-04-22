package client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import client.models.Car;
import org.json.simple.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Co2Request.class)
public class Co2RequestTest {


    @Test
    public void converToJsonTestPlane() {
        JSONObject json = new JSONObject();
        json.put("distance_estimate", 1000);
        assertEquals(json.toJSONString(), Co2Request.convertToJson(1000, "flights").toJSONString());
    }

    @Test
    public void converToJsonTestCar() {
        JSONObject json = new JSONObject();
        json.put("distance", 1000);
        assertEquals(json.toJSONString(), Co2Request.convertToJson(1000, "automobile_trips").toJSONString());
    }

    @Test
    public void sendRequestTestwithCar() {
gui.Main.car = new Car("2010", "Audi", "R8");
        JSONObject formatjson = new JSONObject();
        formatjson.put("distance", 100);
        formatjson.put("model", "R8");
        formatjson.put("year", "2010");

        mockStatic(Co2Request.class);

        JSONObject formatjson1 = new JSONObject();
        formatjson1.put("distance", 100);
        Client client = mock(Client.class);
        when(Co2Request.convertToJson(100, "automobile_trips")).thenReturn(formatjson1);
        when(Co2Request.sendRequest("automobile_trips", 100, client)).thenCallRealMethod();

        when(client.getCO2(formatjson, "automobile_trips")).thenReturn("50");

        assertEquals("50", Co2Request.sendRequest("automobile_trips", 100, client));
    }

    @Test
    public void co2RequestTest() {
        Co2Request request = new Co2Request();
        assertTrue(request instanceof Co2Request);
    }

    @Test
    public void sendRequestTestWithPlane() {

        JSONObject formatjson = new JSONObject();
        formatjson.put("distance_estimate", 100);

        mockStatic(Co2Request.class);

        Client client = mock(Client.class);
        when(Co2Request.convertToJson(100, "flights")).thenReturn(formatjson);
        when(Co2Request.sendRequest("flights", 100, client)).thenCallRealMethod();


        when(client.getCO2(formatjson, "flights")).thenReturn("50");

        assertEquals("50", Co2Request.sendRequest("flights", 100, client));
    }
}