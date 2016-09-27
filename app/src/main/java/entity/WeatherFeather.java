package entity;

import android.graphics.Bitmap;

/**
 * Weather feather entity for listView item
 */
public class WeatherFeather {
    private String suburbName;
    private String weatherCondition;
    private String temps;
    private Bitmap weaImage;

    public WeatherFeather(){}

    public WeatherFeather(String suburbName) {
        this.suburbName = suburbName;
    }

    public WeatherFeather(String suburbName, String weatherCondition, String temps, Bitmap weaImage) {
        this.suburbName = suburbName;
        this.weatherCondition = weatherCondition;
        this.temps = temps;
        this.weaImage = weaImage;
    }

    public String getSuburbName() {
        return suburbName;
    }

    public void setSuburbName(String suburbName) {
        this.suburbName = suburbName;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public String getTemps() {
        return temps;
    }

    public void setTemps(String temps) {
        this.temps = temps;
    }

    public Bitmap getWeaImage() {
        return weaImage;
    }

    public void setWeaImage(Bitmap weaImage) {
        this.weaImage = weaImage;
    }
}
