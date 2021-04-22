package client.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

import org.junit.Test;

public class CarTest {
    Car car = new Car("2006", "Peugeot", "206");

    @Test
    public void constructorTest() {
        assertEquals(car.getYear(), "2006");
        assertEquals(car.getBrand(), "Peugeot");
        assertEquals(car.getModel(), "206");
    }

    @Test
    public void getYearTest() {
        assertEquals(car.getYear(), "2006");
    }

    @Test
    public void getYearTestNotEqual() {
        assertNotSame(car.getYear(), "2002");
    }

    @Test
    public void getBrandTest() {
        assertEquals(car.getBrand(), "Peugeot");
    }

    @Test
    public void getModelTest() {
        assertEquals(car.getModel(), "206");
    }

    @Test
    public void setYearTest() {
        car.setYear("2003");
        assertEquals(car.getYear(), "2003");
    }

    @Test
    public void setBrandTest() {
        car.setBrand("Tesla");
        assertEquals(car.getBrand(), "Tesla");
    }

    @Test
    public void setBrandTestNotEqual() {
        car.setBrand("Some car");
        assertNotSame(car.getBrand(), "Tesla");
    }

    @Test
    public void setModelTest() {
        car.setModel("Model X");
        assertEquals(car.getModel(), "Model X");
    }
}
