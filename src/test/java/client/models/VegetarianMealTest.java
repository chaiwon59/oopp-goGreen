package client.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class VegetarianMealTest {
    private VegetarianMeal meal = new VegetarianMeal("1");

    private double vegCO2 = 1700 / 365;
    private double avgCO2 = 2500 / 365;

    private double meatCO2 = 3300 / 365;
    private double noBeefCO2 = 1900 / 365;
    private double veganCO2 = 1500 / 365;

    private double savedWithNoBeef = avgCO2 - noBeefCO2;
    private double savedWithVegan = avgCO2 - veganCO2;
    private double savedWithMeat = avgCO2 - meatCO2;

    private double savedWithVeg = avgCO2 - vegCO2;
    private double savedWithLocalAvg = avgCO2 / 10;
    private double SavedWithVeganLocal = savedWithVegan + Math.abs(savedWithVegan / 10);
    private double SavedWithMeatLocal = savedWithMeat + Math.abs(savedWithMeat / 10);


    @Test
    public void constructorTest() {
        assertEquals((int) meal.saved, 0);
    }

    @Test
    public void constructorDietTest() {
        assertEquals(meal.getDiet(), "Regular");
    }

    @Test
    public void constructorLocalProduceTest() {
        assertEquals(meal.isLocalProduce(), "No");
    }


    @Test
    public void noLocalProduceTest() {
        meal.setLocalProduce("No");
        assertEquals((int) meal.saved, 0);
    }

    @Test
    public void localProduceTest() {
        meal.setLocalProduce("Yes");
        assertEquals((int) meal.saved, (int) savedWithLocalAvg);
    }

    @Test
    public void localProduceTestNotRegular() {
        meal.setDiet("Vegan");
        meal.setLocalProduce("Yes");
        assertEquals(meal.getDiet(), "Vegan");
        assertEquals((int) meal.saved, (int) SavedWithVeganLocal);
    }

    @Test
    public void localProduceTestLotOfMeat() {
        meal.setDiet("Meal with a lot of beef");
        meal.setLocalProduce("Yes");
        assertNotSame(meal.getDiet(), "Vegan");
        assertEquals((int) meal.saved, (int) SavedWithMeatLocal);
    }


    @Test
    public void setDietVegetarianTest() {
        meal.setDiet("Vegetarian");
        assertEquals((int) meal.saved, (int) savedWithVeg);
        assertEquals(meal.getDiet(), "Vegetarian");
    }

    @Test
    public void setDietDefaultTest() {
        meal.setDiet("randomString");
        assertEquals(meal.getDiet(), "Regular");
        assertEquals((int) meal.saved, 0);
    }

    @Test
    public void negativeCO2savedTest() {
        meal.setDiet("Meal with a lot of beef");
        assertTrue(meal.saved < 0);
    }

    @Test
    public void setDietMeatFailsTest() {
        String diet = "Meal with a lot of beef";
        meal.setDiet(diet);
        assertNotSame(meal.getDiet(), "Regular");
        assertNotSame((int) meal.saved, (int) meatCO2);
    }

    @Test
    public void setDietNoBeefFailsTest() {
        String diet = "No beef";
        meal.setDiet(diet);
        assertEquals(meal.getDiet(), diet);
        assertEquals((int) meal.saved, (int) savedWithNoBeef);
    }

    @Test
    public void setDietVeganTest() {
        String diet = "Vegan";
        meal.setDiet(diet);
        assertEquals(meal.getDiet(), diet);
        assertEquals((int) meal.saved, (int) savedWithVegan);
    }


}