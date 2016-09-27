package entity;

/**
 * Entity of five days forecast item
 */
public class FiveForecast {
    private double dayOne;
    private double dayTwo;
    private double dayThree;
    private double dayFour;
    private double dayFive;
    private String one;
    private String two;
    private String three;
    private String four;
    private String five;

    public FiveForecast() {
    }

    public FiveForecast(double dayOne, double dayTwo, double dayThree, double dayFour, double dayFive, String one, String two, String three, String four, String five) {
        this.dayOne = dayOne;
        this.dayTwo = dayTwo;
        this.dayThree = dayThree;
        this.dayFour = dayFour;
        this.dayFive = dayFive;
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
    }

    public double getDayOne() {
        return dayOne;
    }

    public void setDayOne(double dayOne) {
        this.dayOne = dayOne;
    }

    public double getDayTwo() {
        return dayTwo;
    }

    public void setDayTwo(double dayTwo) {
        this.dayTwo = dayTwo;
    }

    public double getDayThree() {
        return dayThree;
    }

    public void setDayThree(double dayThree) {
        this.dayThree = dayThree;
    }

    public double getDayFour() {
        return dayFour;
    }

    public void setDayFour(double dayFour) {
        this.dayFour = dayFour;
    }

    public double getDayFive() {
        return dayFive;
    }

    public void setDayFive(double dayFive) {
        this.dayFive = dayFive;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

    public String getThree() {
        return three;
    }

    public void setThree(String three) {
        this.three = three;
    }

    public String getFour() {
        return four;
    }

    public void setFour(String four) {
        this.four = four;
    }

    public String getFive() {
        return five;
    }

    public void setFive(String five) {
        this.five = five;
    }
}

