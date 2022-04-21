package com.example.weatherapplication;

public class Weather {
    public String day;
    public String status;
    public String image;
    public String max;
    public String min;

    public Weather(String day, String status, String image, String max, String min) {
        this.day = day;
        this.status = status;
        this.image = image;
        this.max = max;
        this.min = min;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }
}
