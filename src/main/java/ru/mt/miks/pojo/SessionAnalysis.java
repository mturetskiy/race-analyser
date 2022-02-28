package ru.mt.miks.pojo;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import ru.mt.miks.model.Team;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static ru.mt.miks.Analyzer.TARGET_NORMAL_LAPTIME_INSTABILITY;

public class SessionAnalysis {
    public static final String FIELD_SEPARATOR = ";";
    private int teamNum;
    private String teamName;
    private final String driverName;
    private final int sessionNum;
    private int carNum;
    private double bestLapTime;
    private double targetLapTime;

    // For clean (normal) laps:
    private int cleanLaps;
    private double cleanAvg;
    private double avgInstability;

    // For all laps:
    private int totalLaps;
    private double totalAvg;

    // For Blue flag laps:
    private int blueLaps;
    private double blueMean;
    private double blueMin;

    // For Fight laps:
    private int fightLaps;
    private double fightsMean;

    // For warmup laps:
    private int warmupLaps;
    private double warmupAvg;
    private double warmupTimeCut; // warmup until time is higher

    private int stopLaps;
    private double stopTotal;

    // Lost:
    private double lostOnWarmup;
    private double lostOnFight;
    private double lostOnBlue;
    private double lostOnAvgInstability;

    public SessionAnalysis(String driverName, int sessionNum) {
        this.driverName = driverName;
        this.sessionNum = sessionNum;
    }

    public SessionAnalysis setCar(Car car) {
        if (car != null) {
            this.carNum = car.getCarNumber();
        }
        return this;
    }

    public SessionAnalysis setTeam(Team team) {
        this.teamNum = team.getTeamNumber();
        this.teamName = team.getTeamName();
        return this;
    }

    public SessionAnalysis calculateWarmupLostTime() {
        lostOnWarmup = warmupLaps * (warmupAvg - targetLapTime);
        return this;
    }

    public SessionAnalysis calculateInconsistencyLostTime() {
        lostOnAvgInstability = cleanLaps * (cleanAvg - targetLapTime);
        return this;
    }

    public SessionAnalysis calculateFightLostTime() {
        lostOnFight = fightLaps * (fightsMean - targetLapTime);
        return this;
    }

    public SessionAnalysis calculateBlueLostTime() {
        lostOnBlue = blueLaps * (blueMean - targetLapTime);
        return this;
    }

    SessionAnalysis setBestLapTime(double bestLapTime) {
        this.bestLapTime = bestLapTime;
        this.targetLapTime = bestLapTime + TARGET_NORMAL_LAPTIME_INSTABILITY;
        return this;
    }

    public double getTotalLost() {
        return (lostOnWarmup > 0 ? lostOnWarmup : 0)
                + (lostOnBlue > 0 ? lostOnBlue : 0)
                + (lostOnFight > 0 ? lostOnFight : 0)
                + (stopTotal > 0 ? stopTotal : 0)
                + (lostOnAvgInstability > 0 ? lostOnAvgInstability : 0);
    }

    @Override
    public String toString() {
        return driverName + ", team # " + teamNum + "(" + teamName + "), Session# " + sessionNum + ", car: #" + carNum + " ; laps: " + totalLaps
                + "\n\tClean Laps: " + cleanLaps + ", best: " + ft(bestLapTime) + ", target: " + ft(targetLapTime)
                + ", avg: " + ft(cleanAvg) + ", avg instability: " + ft(avgInstability) + ", lost: " + ft(lostOnAvgInstability)
                + "\n\tWarmup Laps: " + warmupLaps + ", avg: " + ft(warmupAvg) + ", warmupTimeCut: " + ft(warmupTimeCut)
                    + ", lost: " + ft(lostOnWarmup)
                + "\n\tBlue Laps: " + blueLaps + ", avg: " + ft(blueMean) + ", best: " + ft(blueMin)
                    + ", lost: " + ft(lostOnBlue)
                + "\n\tFight Laps: " + fightLaps + ", avg: " + ft(fightsMean) + ", lost: " + ft(lostOnFight)
                + "\n\tStop Laps: " + stopLaps + ", lost: " + ft(stopTotal)
                + "\n\tTotal lost: " + ft(getTotalLost())

                ;
    }

    public static String toCsvHeader() {
        return "DriverName" + FIELD_SEPARATOR   // 1
                + "Team" + FIELD_SEPARATOR   // 2
                + "Session" + FIELD_SEPARATOR   // 2
                + "Car" + FIELD_SEPARATOR       // 3
                + "Total laps" + FIELD_SEPARATOR    // 4
                + "Clean laps" + FIELD_SEPARATOR    // 5
                + "Best lap time" + FIELD_SEPARATOR // 6
                + "Target lap time" + FIELD_SEPARATOR // 7
                + "Avg lap time" + FIELD_SEPARATOR // 8
                + "Avg inconsistency" + FIELD_SEPARATOR // 9
                + "Lost on avg inconsistency" + FIELD_SEPARATOR // 10
                + "Warmup laps" + FIELD_SEPARATOR // 14
                + "Warmup avg lap time" + FIELD_SEPARATOR // 15
                + "Warmup cut lap time" + FIELD_SEPARATOR // 16
                + "Lost on warmup" + FIELD_SEPARATOR // 17
                + "Blue laps" + FIELD_SEPARATOR // 18
                + "Blue avg lap time" + FIELD_SEPARATOR // 19
                + "Blue best lap time" + FIELD_SEPARATOR // 20
                + "Lost on blue" + FIELD_SEPARATOR // 22
                + "Fight laps" + FIELD_SEPARATOR // 23
                + "Fight avg lap time" + FIELD_SEPARATOR // 24
                + "Lost on fight" + FIELD_SEPARATOR // 25
                + "Stop laps" + FIELD_SEPARATOR // 23
                + "Lost on stop" + FIELD_SEPARATOR // 25
                + "Total lost" + FIELD_SEPARATOR // 26
                ;
    }

    public String toCsvString() {
        return driverName + FIELD_SEPARATOR // 1
                + teamName + FIELD_SEPARATOR // 2
                + sessionNum + FIELD_SEPARATOR // 2
                + carNum + FIELD_SEPARATOR // 3
                + totalLaps + FIELD_SEPARATOR // 4
                + cleanLaps + FIELD_SEPARATOR // 5
                + ft(bestLapTime) + FIELD_SEPARATOR // 6
                + ft(targetLapTime) + FIELD_SEPARATOR // 7
                + ft(cleanAvg) + FIELD_SEPARATOR // 8
                + ft(avgInstability) + FIELD_SEPARATOR // 9
                + ft(lostOnAvgInstability) + FIELD_SEPARATOR // 10
                + warmupLaps + FIELD_SEPARATOR // 14
                + ft(warmupAvg) + FIELD_SEPARATOR // 15
                + ft(warmupTimeCut) + FIELD_SEPARATOR // 16
                + ft(lostOnWarmup) + FIELD_SEPARATOR // 17
                + blueLaps + FIELD_SEPARATOR // 18
                + ft(blueMean) + FIELD_SEPARATOR // 19
                + ft(blueMin) + FIELD_SEPARATOR // 20
                + ft(lostOnBlue) + FIELD_SEPARATOR // 22
                + fightLaps + FIELD_SEPARATOR //23
                + ft(fightsMean) + FIELD_SEPARATOR // 24
                + ft(lostOnFight) + FIELD_SEPARATOR // 25
                + stopLaps + FIELD_SEPARATOR //23
                + ft(stopTotal) + FIELD_SEPARATOR // 25
                + ft(getTotalLost()) + FIELD_SEPARATOR // 26
                ;
    }

    private String ft(double val) {
        if (val < 0) return "0";
        BigDecimal bigDecimal = new BigDecimal(val);
        float floatValue = bigDecimal.setScale(3, RoundingMode.HALF_UP).floatValue();
        return String.valueOf(floatValue).replace('.', ',');
    }

    public SessionAnalysis setCleanStats(DescriptiveStatistics cleanStats) {
        if (cleanStats.getN() > 0) {
            cleanLaps = (int) cleanStats.getN();
            setBestLapTime(cleanStats.getMin());
            cleanAvg = cleanStats.getMean();
            avgInstability = cleanAvg - bestLapTime;
        }
        return this;
    }

    public SessionAnalysis setWarmupStats(DescriptiveStatistics warmupStats, double warmupTimeCut) {
        if (warmupStats.getN() > 0) {
            warmupLaps = (int) warmupStats.getN();
            warmupAvg = warmupStats.getMean();
            this.warmupTimeCut = warmupTimeCut;
        }
        return this;
    }

    public SessionAnalysis setStopStats(DescriptiveStatistics stopStats) {
        if (stopStats.getN() > 0) {
            stopLaps = (int) stopStats.getN();
            stopTotal = stopStats.getSum() - stopLaps * bestLapTime;
        }
        return this;
    }

    public SessionAnalysis setTotalLaps(int totalLaps) {
        this.totalLaps = totalLaps;
        return this;
    }

    public SessionAnalysis setBlueStats(DescriptiveStatistics blueStats) {
        if (blueStats.getN() > 0) {
            this.blueLaps = (int) blueStats.getN();
            this.blueMean = blueStats.getMean();
            this.blueMin = blueStats.getMin();
        }
        return this;
    }


    public SessionAnalysis setFightStats(DescriptiveStatistics fightStats) {
        if (fightStats.getN() > 0) {
            this.fightLaps = (int) fightStats.getN();
            this.fightsMean = fightStats.getMean();
        }
        return this;
    }

}
