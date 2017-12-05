package com.yitouwushui.miweather;

/**
 * Created by ding on 2017/11/30.
 */

public class WeatherBean {

    public static final String SUN = "晴";
    public static final String CLOUDY ="阴";
    public static final String SNOW = "雪";
    public static final String RAIN = "雨";
    public static final String SUN_CLOUD = "多云";
    public static final String THUNDER = "雷";

    /**天气，取值为上面6种*/
    private String weather;

    /**温度*/
    private int temperature;

    /**温度的描述值*/
    private String temperatureStr;

    /**温度的描述值*/
    private String time;

    public WeatherBean(String weather, int temperature, String temperatureStr, String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.temperatureStr = temperatureStr;
        this.time = time;
    }

    public WeatherBean(String weather, int temperature,String time) {
        this.weather = weather;
        this.temperature = temperature;
        this.time = time;
        this.temperatureStr = temperature + "°";
    }

    public static String[] getAllWeathers(){
        String[] str = {SUN,RAIN,CLOUDY,SUN_CLOUD,SNOW,THUNDER};
        return str;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemperatureStr() {
        return temperatureStr;
    }

    public void setTemperatureStr(String temperatureStr) {
        this.temperatureStr = temperatureStr;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
