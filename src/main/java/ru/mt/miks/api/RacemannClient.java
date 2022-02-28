package ru.mt.miks.api;

import ru.mt.miks.model.DriverData;
import ru.mt.miks.model.RaceData;
import ru.mt.miks.model.SessionLap;
import ru.mt.miks.pojo.LapRecord;

import java.util.List;

public interface RacemannClient {
    DriverData getDriverData(String name);
    RaceData getRaceData(String raceId);
    List<LapRecord> getRaceLaps(String raceId);
    List<SessionLap> getSessionLaps(String raceId, int teamNum, int sessionNum);
}
