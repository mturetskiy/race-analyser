package ru.mt.miks.model;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static ru.mt.miks.Analyzer.MIN_WARMUP_LAPS;
import static ru.mt.miks.model.LapRecord.LapMarker.*;

public class DriverSession {
    final Logger log = LoggerFactory.getLogger(DriverSession.class);

    private Integer sessionNumber;
    private Car sessionCar;
    private Driver driver;

    private List<Lap> laps = new ArrayList<>();

    public DriverSession(Integer sessionNumber) {
        this.sessionNumber = sessionNumber;
    }

    public void setSessionCar(Car sessionCar) {
        this.sessionCar = sessionCar;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public void addLap(Integer lapNumber, Integer lapTime, LapRecord.LapMarker marker) {
        this.laps.add(new Lap(lapNumber, lapTime, marker));
    }

    public int getTotalLaps() {
        return laps.size();
    }

    public SessionAnalysis analyzeSession() {
        DescriptiveStatistics normalStat = new DescriptiveStatistics();
        DescriptiveStatistics cleanStat = new DescriptiveStatistics();
        DescriptiveStatistics warmupStat = new DescriptiveStatistics();
        DescriptiveStatistics blueStat = new DescriptiveStatistics();
        DescriptiveStatistics fightStat = new DescriptiveStatistics();
        DescriptiveStatistics stopStat = new DescriptiveStatistics();

        laps.stream().filter(l -> l.getLapMarker() == normal)
                .forEach(lap -> normalStat.addValue(lap.getLapTime()));

        double warmupCutTime = normalStat.getMean();

        boolean warmupDone = false;
        int lapNum = 1;
        for (Lap lap : laps) {
            double lapTime = lap.getLapTime();
            LapRecord.LapMarker lapMarker = lap.getLapMarker();

            if (lapMarker == blueFlag) {
                blueStat.addValue(lapTime);
            } else if (lapMarker == LapRecord.LapMarker.fight) {
                fightStat.addValue(lapTime);
            } else if (lapMarker == normal) {
                if ((lapNum <= MIN_WARMUP_LAPS) || (!warmupDone && lapTime > warmupCutTime)) {
                    warmupStat.addValue(lapTime);
                } else {
                    warmupDone = true;
                    cleanStat.addValue(lapTime);
                }
            } else if (lapMarker == stop) {
                stopStat.addValue(lapTime);
            } else {
                log.warn("Untracked lap: {}#{}  -> lap #{} : {} [{}]", driver.getName(), sessionNumber, lap.getLapNumber(), lapTime, lapMarker);
            }

            lapNum++;
        }

        return new SessionAnalysis(driver.getName(), sessionNumber)
                .setTeam(driver.getTeam())
                .setCar(sessionCar)
                .setTotalLaps(laps.size())
                .setBlueStats(blueStat)
                .setFightStats(fightStat)
                .setCleanStats(cleanStat)
                .setWarmupStats(warmupStat, warmupCutTime)
                .setStopStats(stopStat)
                .calculateWarmupLostTime()
                .calculateInconsistencyLostTime()
                .calculateBlueLostTime()
                .calculateFightLostTime()
                ;
    }

}
