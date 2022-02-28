package ru.mt.miks.pojo;

import ru.mt.miks.pojo.LapRecord;

public class Lap {
    private Integer lapNumber;
    private double lapTime;
    private LapRecord.LapMarker lapMarker;

    private Integer raceTime;

    public Lap(Integer lapNumber, Integer lapTime, LapRecord.LapMarker lapMarker) {
        this.lapNumber = lapNumber;
        this.lapTime = lapTime / 1000.0;
        this.lapMarker = lapMarker;
    }

    public void setRaceTime(Integer raceTime) {
        this.raceTime = raceTime;
    }

    public Integer getLapNumber() {
        return lapNumber;
    }

    public double getLapTime() {
        return lapTime;
    }

    public LapRecord.LapMarker getLapMarker() {
        return lapMarker;
    }

    public Integer getRaceTime() {
        return raceTime;
    }

    @Override
    public String toString() {
        return "Lap #" + lapNumber +
                ", lapTime=" + lapTime +
                ", lapMarker=" + lapMarker +
                ", raceTime=" + raceTime +
                '}';
    }
}
