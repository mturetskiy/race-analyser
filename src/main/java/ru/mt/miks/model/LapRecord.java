package ru.mt.miks.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//RegNumber;Comp;Pos;Lap;LapTime;RaceTime;LapStat;SessNumber;Driver
public class LapRecord implements Comparable<LapRecord> {
    final Logger log = LoggerFactory.getLogger(LapRecord.class);

    public static final String SEPARATOR = ";";
    public static final int FIELDS_COUNT = 9;

    private int teamNumber;
    private String teamName;
    private int pos; // no need;
    private int lap;
    private int lapTime;
    private int raceTime;
    private LapMarker lapMarker;
    private int sessionNumber;
    private String driver;

    private LapRecord() {
    }

    public static LapRecord parseFromString(String line) {
        String[] parts = line.split(SEPARATOR);
        if (parts.length != FIELDS_COUNT) {
            throw new IllegalArgumentException("Wrong line format. Expected " + FIELDS_COUNT + " elements, but found " + parts.length);
        }

        LapRecord record = new LapRecord();
        record.setTeamNumber(Integer.parseInt(parts[0]));
        record.setTeamName(parts[1]);
        record.setPos(Integer.parseInt(parts[2]));
        record.setLap(Integer.parseInt(parts[3]));
        record.setLapTime(Integer.parseInt(parts[4]));
        record.setRaceTime(Integer.parseInt(parts[5]));
        record.setLapMarker(parts[6]);
        record.setSessionNumber(Integer.parseInt(parts[7]));
        record.setDriver(parts[8]);
        return record;
    }

    public int getTeamNumber() {
        return teamNumber;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getPos() {
        return pos;
    }

    public int getLap() {
        return lap;
    }

    public int getLapTime() {
        return lapTime;
    }

    public int getRaceTime() {
        return raceTime;
    }

    public LapMarker getLapMarker() {
        return lapMarker;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public String getDriver() {
        return driver;
    }

    private void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    private void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    private void setPos(int pos) {
        this.pos = pos;
    }

    private void setLap(int lap) {
        this.lap = lap;
    }

    private void setLapTime(int lapTime) {
        this.lapTime = lapTime;
    }

    private void setRaceTime(int raceTime) {
        this.raceTime = raceTime;
    }

    private void setLapMarker(String code) {
        this.lapMarker = LapMarker.fromString(code);
    }

    private void setSessionNumber(int sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    private void setDriver(String driver) {
        this.driver = driver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LapRecord record = (LapRecord) o;
        return teamNumber == record.teamNumber &&
                pos == record.pos &&
                lap == record.lap &&
                lapTime == record.lapTime &&
                raceTime == record.raceTime &&
                sessionNumber == record.sessionNumber &&
                lapMarker == record.lapMarker &&
                Objects.equals(teamName, record.teamName) &&
                Objects.equals(driver, record.driver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamNumber, teamName, pos, lap, lapTime, raceTime, lapMarker, sessionNumber, driver);
    }

    @Override
    public String toString() {
        return "DataRecord{" +
                "teamNumber=" + teamNumber +
                ", teamName='" + teamName + '\'' +
                ", pos=" + pos +
                ", lap=" + lap +
                ", lapTime=" + lapTime +
                ", raceTime=" + raceTime +
                ", lapStat='" + lapMarker + '\'' +
                ", sessionNumber=" + sessionNumber +
                ", driver='" + driver + '\'' +
                '}';
    }

    @Override
    public int compareTo(LapRecord o) {
        return Integer.compare(this.raceTime, o.raceTime);
    }

    public enum LapMarker {
        normal(""),
        pit("PitUser"),
        fight("Fight"),
        blueFlag("LongLight");

        private String code;
        private static Map<String, LapMarker> markerCodes = new HashMap<>();
        static {
            for (LapMarker marker : LapMarker.values()) {
                markerCodes.put(marker.code, marker);
            }
        }

        LapMarker(String code) {
            this.code = code;
        }

        public static LapMarker fromString(String code) {
            return markerCodes.getOrDefault(code, normal);
        }
    }
}
