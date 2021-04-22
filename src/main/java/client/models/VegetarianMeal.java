package client.models;

public class VegetarianMeal extends Activity {
    private String localProduce;
    private String diet;

    /*
    meat consumption in kg CO2/person day (http://www.greeneatz.com/wp-content/uploads/2013/01/foods-carbon-footprint-7.gif)
    CO2localProduce in percent less CO2 emission (https://www.terrapass.com/eat-your-way-to-a-smaller-carbon-footprint)
    This data could be inserted in the database, for now  I hardcoded it
     */

    private double vegCO2 = 1700 / 365; //5 vegetarian
    private double avgCO2 = 2500 / 365; //7 regular

    private double meatCO2 = 3300 / 365; //9 meat
    private double noBeefCO2 = 1900 / 365; //5 noBeef
    private double veganCO2 = 1500 / 365; //4 vegan

    /**
     * Creating a (vegetarian) meal.
     *
     * @param userId The user for which this applies to.
     */

    public VegetarianMeal(String userId) {
        super(userId);
        this.localProduce = "No";
        this.saved = 0;
        this.diet = "Regular";

    }

    /**
     * Getting the type of diet of the user.
     *
     * @return diet.
     */
    public String getDiet() {
        return diet;
    }


    /**
     * Setting the type of diet of the user.
     *
     * @param diet The type of diet the user inserted.
     */
    public void setDiet(String diet) {
        this.diet = diet;
        changeDietCO2();
        usesLocalProduce();
    }

    /**
     * Retrieving if the user used local produce.
     *
     * @return localproduce.
     */
    public String isLocalProduce() {
        return localProduce;
    }

    /**
     * Setting if the user used local produce.
     *
     * @param localProduce If the user used local produce or not.
     */
    public void setLocalProduce(String localProduce) {
        this.localProduce = localProduce;
        changeDietCO2();
        usesLocalProduce();
    }

    private void usesLocalProduce() {
        if (this.localProduce.equals("Yes")) {
            if (this.diet.equals("Regular")) {
                this.saved = this.saved + (avgCO2 / 10);
            } else {
                this.saved = this.saved + Math.abs(this.saved / 10);
            }
        }
    }

    private void changeDietCO2() {
        switch (this.diet) {
            case "Vegetarian":
                this.saved = avgCO2 - vegCO2;
                break;
            case "Meal with a lot of beef":
                this.saved = avgCO2 - meatCO2;
                break;
            case "No beef":
                this.saved = avgCO2 - noBeefCO2;
                break;
            case "Vegan":
                this.saved = avgCO2 - veganCO2;
                break;
            default:
                this.diet = "Regular";
                break;
        }
    }

}
