package maf.mobile.weatherapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather {

    public String date;
    public String condition;
    public int icon;
    public int weatherCode;

    public Weather(String d,  int w){
        this.date = formatDate(d);
        this.condition = getConditionFromWeatherCode(w);
        this.icon = getIconFromWeatherCode(w);
        this.weatherCode = w;
    }

    private int getIconFromWeatherCode(int code) {
        if (code >= 95) {
            return R.drawable.thunderstorm;
        } else if (code >= 85) {
            return R.drawable.snow;
        } else if (code >= 80) {
            return R.drawable.storm;
        } else if (code >= 61) {
            return R.drawable.heavyrain;
        } else if (code >= 45) {
            return R.drawable.cloudyday;
        } else {
            return R.drawable.sunny;
        }
    }

    private String formatDate(String dateString){
        try{
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
            Date date = input.parse(dateString);

            SimpleDateFormat output = new SimpleDateFormat("d MMM yyy");
            return  output.format(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private String getConditionFromWeatherCode(int code) {
        if (code >= 95) {
            return "Thunderstorm";
        } else if (code >= 85) {
            return "Snow Showers";
        } else if (code >= 80) {
            return "Rain showers : violent";
        } else if (code >= 61) {
            return "Rain: heavy intensity";
        } else if (code >= 45) {
            return "Cloudy";
        } else {
            return "Clear Sky";
        }
    }
}
