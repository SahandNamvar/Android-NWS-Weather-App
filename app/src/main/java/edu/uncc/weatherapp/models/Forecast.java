package edu.uncc.weatherapp.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Forecast {
    String startTime, temperature, humidity, windSpeed, shortForecast, iconURL;

    public Forecast() {}

    // The Class Constructor receives a JSON object. From this object, we can access the key-value pairs.
    // Note: The methods ran on 'jsonObject' such as 'getString' or 'getJSONObject' refer to the TYPE of VALUE returned from that KEY
    public Forecast(JSONObject jsonObject) throws JSONException {
        this.startTime = jsonObject.getString("startTime");
        this.temperature = jsonObject.getString("temperature");
        /*
         * The 'jsonObject' passed to the Class Constructor has a field 'relativeHumidity' which is an Object (it's value is an object).
         * So, extract that object by creating a JSONObject for it, then extract the humidity value from the object.
         */
        JSONObject relativeHumidity = jsonObject.getJSONObject("relativeHumidity");
        int relativeHumidityValue = relativeHumidity.getInt("value");
        this.humidity = String.valueOf(relativeHumidityValue);
        this.windSpeed = jsonObject.getString("windSpeed");
        this.shortForecast = jsonObject.getString("shortForecast");
        this.iconURL = jsonObject.getString("icon");
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getShortForecast() {
        return shortForecast;
    }

    public void setShortForecast(String shortForecast) {
        this.shortForecast = shortForecast;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "startTime='" + startTime + '\'' +
                ", temperature='" + temperature + '\'' +
                ", humidity='" + humidity + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", shortForecast='" + shortForecast + '\'' +
                '}';
    }
}
