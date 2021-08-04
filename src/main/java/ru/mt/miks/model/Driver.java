package ru.mt.miks.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Driver {
    final Logger log = LoggerFactory.getLogger(Driver.class);

    public static final AtomicInteger idGen = new AtomicInteger(1);
    private int driverID;
    private Team team;
    private String name;

    private Map<Integer, DriverSession> driverSessions;

    public Driver(String name, Team team) {
        this.name = name;
        this.team = team;
        this.driverID = idGen.getAndIncrement();
        this.driverSessions = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public DriverSession getSession(Integer sessionNum, Car sessionCar) {
        return driverSessions.computeIfAbsent(sessionNum, sn -> {
            DriverSession driverSession = new DriverSession(sn);
            driverSession.setDriver(this);
            driverSession.setSessionCar(sessionCar);

//            log.info("Created session: Driver: {}, sNum: {}, car: {}", name, sessionNum, sessionCar);
            return driverSession;
        });
    }

    public int getSessions() {
        return driverSessions.size();
    }

    public int getTotalLaps() {
        return driverSessions.values().stream().mapToInt(DriverSession::getTotalLaps).sum();
    }

    public List<SessionAnalysis> getSessionsAnalysis() {
        return driverSessions.values().stream().map(DriverSession::analyzeSession).collect(Collectors.toList());
    }
}
