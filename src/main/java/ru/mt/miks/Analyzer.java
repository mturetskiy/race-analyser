package ru.mt.miks;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.mt.miks.model.*;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/*
wget --post-data 'raceId=e9758a18-d03d-4be5-8fb7-810b6e983df7' -O race-stats.json https://forza-race.racemann.com/race/GetRaceStartData
wget -O race-data.csv https://forza-race.racemann.com/race/csv/e9758a18-d03d-4be5-8fb7-810b6e983df7

win 10:
wget --post-data="raceId=e88f5fe6-4da9-43e2-aeed-055613442a66" -O race-stats.json http://forza-race.racemann.com/race/GetRaceStartData
wget -O race-data.csv http://forza-race.racemann.com/race/csv/e88f5fe6-4da9-43e2-aeed-055613442a66

 */

public class Analyzer {
    final Logger log = LoggerFactory.getLogger(Analyzer.class);

    public static final double TARGET_NORMAL_LAPTIME_INSTABILITY = 0.2;
    public static final double TARGET_BLUE_LAPTIME_INSTABILITY = 0.5;
    public static final int MIN_WARMUP_LAPS = 1;
//    public static final double TARGET_WARMUP_LAPTIME_INSTABILITY = 0.5;

    private File dataFile; // lap records
    private File statsFile; // common race info, including cars
    private List<LapRecord> records;
    private Map<Integer, Car> cars = new HashMap<>();
    private Map<Pair<String, Integer>, Car> sessionCars = new HashMap<>();    // Driver <> SessionNum => CarNum mapping

    private Map<String, Driver> drivers = new HashMap<>();
    private Race raceStats;

    public Analyzer(String dataFilePath, String statsFilePath) {
        this.dataFile = new File(dataFilePath);
        if (!dataFile.exists()) {
            throw new IllegalArgumentException("Data file does not exist: " + dataFilePath);
        }

        this.statsFile = new File(statsFilePath);
        if (!statsFile.exists()) {
            throw new IllegalArgumentException("Stats file does not exist: " + statsFilePath);
        }

        log.info("Data file is set to: {}", dataFile.getAbsoluteFile());
        log.info("Stats file is set to: {}", statsFile.getAbsoluteFile());

        records = readDataFile();
        raceStats = readRaceStats();
    }

    private List<LapRecord> readDataFile() {
        try {
            List<String> lines = Files.readAllLines(dataFile.toPath(), Charset.forName("Windows-1251"));
            List<LapRecord> records = lines.stream()
                    .skip(2)
                    .map(LapRecord::parseFromString)
                    .sorted()
                    .collect(Collectors.toList());
            log.info("Loaded {} lap records", records.size());
            return records;
        } catch (IOException e) {
            log.error("Unable to read data from given data file", e);
            throw new IllegalArgumentException(e);
        }
    }

    private Race readRaceStats() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Race race = objectMapper.readValue(statsFile, Race.class);

            log.info("Loaded race: {} stats", race.getRaceSettings().getRaceID());

            // filter null sessions:
            List<Session> sessions = race.getSessions();
            // some tech session
            sessions.removeIf(next -> next.getCarNumber() == null || next.getDriverName() == null);
            log.info("Loaded {} race sessions and {} warnings.", race.getSessions().size(), race.getWarnings().size());
            return race;
        }catch (Exception e) {
            throw new IllegalArgumentException("Unable to load race stats from given file.", e);
        }
    }

    public void analyzeCars() {
        List<Session> sessions = raceStats.getSessions();
        sessions.forEach(s -> {
                    Integer carNumber = s.getCarNumber();
                    Integer bestLap = s.getBestLap();

                    Car car = cars.computeIfAbsent(carNumber, Car::new);
                    car.addBestLap(bestLap);

                    sessionCars.computeIfAbsent(new ImmutablePair<>(s.getDriverName(), s.getSessionNumber()), p -> car);
                });
        log.info("Found {} cars used the race in {} sessions", cars.size(), sessions.size());

        // Range cars:
        // TODO: slice for time intervals and add weights to lap times. 10 mins intervals?
        // TODO: slice for drivers based on team pos, and add weights based on them
        log.info("==================================================================================================");
        log.info("Raw time ranged cars:");
        cars.values().stream()
                .sorted(Comparator.comparingInt(Car::getTopBestLap))
                .forEach(c -> log.info("Car #{}: {}", c.getCarNumber(), (float)(c.getTopBestLap() / 1000.0)));
    }

    public void analyzeDriversSessions() {
        for (LapRecord record : records) {
            LapRecord.LapMarker lapMarker = record.getLapMarker();
            if (lapMarker == LapRecord.LapMarker.pit) {
                // don't analyze pit times for now
                continue;
            }

            String driverName = record.getDriver();
            int sessionNumber = record.getSessionNumber();
            Driver driver = drivers.computeIfAbsent(driverName, n -> new Driver(n, record.getTeamNumber()));
            Car sessionCar = sessionCars.get(new ImmutablePair<>(driverName, sessionNumber));
            DriverSession session = driver.getSession(sessionNumber, sessionCar);

            int lapNumber = record.getLap();
            int lapTime = record.getLapTime();
            session.addLap(lapNumber, lapTime, lapMarker);
        }

        int laps = drivers.values().stream().mapToInt(Driver::getTotalLaps).sum();
        int sessions = drivers.values().stream().mapToInt(Driver::getSessions).sum();

        log.info("Loaded {} laps from {} sessions of {} drivers.", laps, sessions, drivers.size());

        File exportFile = new File(statsFile.getParent(), raceStats.getRaceSettings().getRaceID() + ".csv");
        log.info("Exporting to: {}", exportFile.getAbsolutePath());
        try (FileWriter fw = new FileWriter(exportFile, false);
             final BufferedWriter bw = new BufferedWriter(fw)) {

            bw.write(SessionAnalysis.toCsvHeader());

            drivers.values().stream()
//                .filter(d -> d.getName().equals("Турецкий Михаил"))
//                .filter(d -> d.getName().equals("Обросов Игорь"))
//                .filter(d -> d.getName().equals("Грибанов Алексей"))
//                .filter(d -> d.getName().equals("Шамаев Дмитрий"))
//                .filter(d -> d.getName().equals("Бурмистров Станислав"))
//                .filter(d -> d.getName().equals("Вяльсов Михаил"))
//                .filter(d -> d.getName().equals("Смирнов Валерий"))
//                .filter(d -> d.getName().equals("Михаил Лобода"))
//                .filter(d -> d.getName().equals("Кузнецов Антон"))
//                .filter(d -> d.getTeamNumber() == 1)
                    .flatMap(d -> d.getSessionsAnalysis().stream())
                    .forEach(sa -> {
                        log.info("-----------------------------------------------------------------\n{}", sa);
                        try {
                            bw.write("\n");
                            bw.write(sa.toCsvString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Exception e) {
            log.error("Unable to export to: {}", exportFile.getAbsolutePath(), e);
        }

    }

    public static void main(String[] args) {
        if (args.length >= 2) {
            String dataFilePath = args[0];
            String statsFilePath = args[1];
            Analyzer analyzer = new Analyzer(dataFilePath, statsFilePath);
            analyzer.analyzeCars();
            analyzer.analyzeDriversSessions();


        } else {
            throw new IllegalArgumentException("No proper data file specified in args.");
        }
    }
}
