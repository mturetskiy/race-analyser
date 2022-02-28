package ru.mt.miks.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SessionLap {
    @JsonProperty("n")
    private int lapNumber;

    @JsonProperty("rn")
    private String teamNumber;

    @JsonProperty("lt")
    private int lapTime;

    @JsonProperty("rt")
    private int raceTime;

    @JsonProperty("p")
    private int position;

    @JsonProperty("ls")
    private int lapMarkerCode;

    public int getLapNumber() {
        return lapNumber;
    }

    public void setLapNumber(int lapNumber) {
        this.lapNumber = lapNumber;
    }

    public String getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(String teamNumber) {
        this.teamNumber = teamNumber;
    }

    public int getLapTime() {
        return lapTime;
    }

    public void setLapTime(int lapTime) {
        this.lapTime = lapTime;
    }

    public int getRaceTime() {
        return raceTime;
    }

    public void setRaceTime(int raceTime) {
        this.raceTime = raceTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLapMarkerCode() {
        return lapMarkerCode;
    }

    public void setLapMarkerCode(int lapMarkerCode) {
        this.lapMarkerCode = lapMarkerCode;
    }

    public LapMarker getLapMarker() {
        return LapMarker.fromCode(lapMarkerCode);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SessionLapHolder {
        private List<SessionLap> laps;

        public List<SessionLap> getLaps() {
            return laps;
        }

        public void setLaps(List<SessionLap> laps) {
            this.laps = laps;
        }
    }
}
