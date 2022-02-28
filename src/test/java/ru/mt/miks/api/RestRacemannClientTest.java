package ru.mt.miks.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mt.miks.model.DriverData;
import ru.mt.miks.model.LapMarker;
import ru.mt.miks.model.RaceData;
import ru.mt.miks.model.SessionLap;
import ru.mt.miks.pojo.LapRecord;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestRacemannClientTest {
    private static final Logger log = LoggerFactory.getLogger(RestRacemannClientTest.class);

    private RacemannClient client;
    private String RACE_ID = "17fc1f6d-2a4b-4dbe-8cf4-0c49901a70f3";
    private String DRIVER_NAME = "БРЯНЦЕВ НИКОЛАЙ";
    private int TEAM_NUM = 3;
    private int SESSION_NUM = 2;

    @BeforeEach
    void setUp() {
        client = new RestRacemannClient(true);
    }

    @Test
    void testLoadRaceData() {
        RaceData raceData = client.getRaceData(RACE_ID);

        assertNotNull(raceData);
    }

    @Test
    void testLoadDriverData() {
        DriverData driverData = client.getDriverData(DRIVER_NAME);

        assertNotNull(driverData);
        assertEquals(DRIVER_NAME.toLowerCase(), driverData.getName().toLowerCase());
        assertEquals(84, driverData.getWeight());
    }

    @Test
    void testLoadRaceLaps() {
        List<LapRecord> raceLaps = client.getRaceLaps(RACE_ID);

        assertNotNull(raceLaps);
        assertEquals(4821, raceLaps.size());
    }

    @Test
    void testLoadSessionLaps() {
        List<SessionLap> sessionLaps = client.getSessionLaps(RACE_ID, TEAM_NUM, SESSION_NUM);

        assertNotNull(sessionLaps);
        assertEquals(LapMarker.PIT, sessionLaps.get(0).getLapMarker());
        assertEquals(60, sessionLaps.size());
    }
}