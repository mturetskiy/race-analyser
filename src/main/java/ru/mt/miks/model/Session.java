package ru.mt.miks.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Session {
    @JsonProperty("bl")
    private Integer bestLap;

    @JsonProperty("n")
    private Integer sessionNumber;

    @JsonProperty("rn")
    private Integer teamNumber;

    @JsonProperty("drv")
    private String driverName;

    @JsonProperty("Car")
    private Integer carNumber;

    public Integer getBestLap() {
        return bestLap;
    }

    public void setBestLap(Integer bestLap) {
        this.bestLap = bestLap;
    }

    public Integer getSessionNumber() {
        return sessionNumber;
    }

    public void setSessionNumber(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public Integer getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(Integer teamNumber) {
        this.teamNumber = teamNumber;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public Integer getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(Integer carNumber) {
        this.carNumber = carNumber;
    }
}
