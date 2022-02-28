package ru.mt.miks.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mt.miks.model.DriverData;
import ru.mt.miks.model.DriverData.DriverDataHolder;
import ru.mt.miks.model.RaceData;
import ru.mt.miks.model.RaceSettings;
import ru.mt.miks.model.SessionLap;
import ru.mt.miks.model.SessionLap.SessionLapHolder;
import ru.mt.miks.pojo.LapRecord;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RestRacemannClient implements RacemannClient {
    private static final Logger log = LoggerFactory.getLogger(RestRacemannClient.class);

    private final String RACE_DATA_REQUEST_URL = "https://forza-race.racemann.com/race/GetRaceStartData";
    private final String RACE_LAPS_REQUEST_URL = "https://forza-race.racemann.com/race/csv/";
    private final String SESSION_LAPS_REQUEST_URL = "https://forza-race.racemann.com/race/GetSessionLaps";
    private final String DRIVER_DATA_REQUEST_URL = "https://forza-race.racemann.com/race/GetDriverData";

    private final String RACE_ID_PARAM = "raceId";
    private final String DRIVER_NAME_PARAM = "name";
    private final String TEAM_NUM_PARAM = "compRegNum";
    private final String SESSION_NUM_PARAM = "sessionNum";

    private final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(30);
    private final String POST_REQ_CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    private final String RACE_DATA_FILENAME = "RaceData.json";
    private final String RACE_LAPS_FILENAME = "RaceLaps.csv";

    private HttpClient client;
    private ObjectMapper objectMapper;
    private boolean persistLoadedData;

    public RestRacemannClient(boolean persistLoadedData) {
        this.persistLoadedData = persistLoadedData;

        client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(DEFAULT_TIMEOUT)
                .build();

        objectMapper = new ObjectMapper();

        log.info("RestRacemannClient has been created.");
    }

    private String getRequestResponse(HttpRequest request) {
        try {
            log.info("Invoking request: {}", request);
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            String body = response.body();

            if (statusCode == HttpURLConnection.HTTP_OK) {
                return body;
            } else {
                throw new IllegalStateException("Unexpected response: " + statusCode + ", body: \n" + body);
            }
        } catch (Exception e) {
            log.error("Unable to get response for request: {}", request, e);
            return null;
        }
    }

    @Override
    public DriverData getDriverData(String name) {
        HttpRequest driverDataReq = HttpRequest.newBuilder()
                .uri(URI.create(DRIVER_DATA_REQUEST_URL))
                .timeout(DEFAULT_TIMEOUT)
                .header("Content-Type", POST_REQ_CONTENT_TYPE)
                .POST(preparePostParams(Map.of(DRIVER_NAME_PARAM, name)))
                .build();

        log.info("Getting driver's data for: {}", name);
        String responseBody = getRequestResponse(driverDataReq);

        if (responseBody == null) {
            log.error("Unable to load driver's data for: {}", name);
            return null;
        }

        try {
            DriverDataHolder driverDataHolder = objectMapper.readValue(responseBody, DriverDataHolder.class);
            DriverData data = driverDataHolder.getData();
            log.info("Loaded driver's data for: {} : {}", name, data);

            return data;
        } catch (Exception e) {
            log.error("Unable to parse loaded driver's data for: {}", name, e);
            return null;
        }
    }

    @Override
    public RaceData getRaceData(String raceId) {
        HttpRequest dataReq = HttpRequest.newBuilder()
                .uri(URI.create(RACE_DATA_REQUEST_URL))
                .timeout(DEFAULT_TIMEOUT)
                .header("Content-Type", POST_REQ_CONTENT_TYPE)
                .POST(preparePostParams(Map.of(RACE_ID_PARAM, raceId)))
                .build();


        log.info("Getting race data for raceId: {}", raceId);
        String responseBody = getRequestResponse(dataReq);

        if (responseBody == null) {
            log.error("Unable to load race data for raceId: {}", raceId);
            return null;
        }

        try {
            persistIfNeeded(responseBody, raceId + "_" + RACE_DATA_FILENAME);

            RaceData raceData = objectMapper.readValue(responseBody, RaceData.class);
            RaceSettings raceSettings = raceData.getRaceSettings();
            log.info("Loaded race data for raceId: {}. {}:{}", raceId, raceSettings.getRaceID(), raceSettings.getRaceName());

            return raceData;
        } catch (Exception e) {
            log.error("Unable to parse loaded race data response for raceId: {}", raceId, e);
            return null;
        }
    }

    @Override
    public List<LapRecord> getRaceLaps(String raceId) {
        HttpRequest csvReq = HttpRequest.newBuilder()
                .uri(URI.create(RACE_LAPS_REQUEST_URL + raceId))
                .timeout(DEFAULT_TIMEOUT)
                .GET()
                .build();

        log.info("Getting race laps for raceId: {}", raceId);
        String responseBody = getRequestResponse(csvReq);

        if (responseBody == null) {
            log.error("Unable to load race laps for raceId: {}", raceId);
            return null;
        }

        try {
            persistIfNeeded(responseBody, raceId + "_" + RACE_LAPS_FILENAME);

            List<LapRecord> records = responseBody.lines()
                    .skip(2)
                    .map(LapRecord::parseFromString)
                    .sorted()
                    .collect(Collectors.toList());

            log.info("Loaded {} laps for raceId: {}", records.size(), raceId);

            return records;
        } catch (Exception e) {
            log.error("Unable to parse loaded race data response for raceId: {}", raceId, e);
            return null;
        }
    }

    @Override
    public List<SessionLap> getSessionLaps(String raceId, int teamNum, int sessionNum) {
        HttpRequest sessionLapsReq = HttpRequest.newBuilder()
                .uri(URI.create(SESSION_LAPS_REQUEST_URL))
                .timeout(DEFAULT_TIMEOUT)
                .header("Content-Type", POST_REQ_CONTENT_TYPE)
                .POST(preparePostParams(
                        Map.of(RACE_ID_PARAM, raceId,
                                TEAM_NUM_PARAM, teamNum,
                                SESSION_NUM_PARAM, sessionNum
                        )
                ))
                .build();

        log.info("Getting session laps for raceId: {}, teamNum: {}, sessionNum: {}", raceId, teamNum, sessionNum);
        String responseBody = getRequestResponse(sessionLapsReq);

        if (responseBody == null) {
            log.error("Unable to load session laps for raceId: {}", raceId);
            return null;
        }

        try {
            SessionLapHolder sessionLapHolder = objectMapper.readValue(responseBody, SessionLapHolder.class);
            List<SessionLap> laps = sessionLapHolder.getLaps();
            log.info("Loaded {} session laps for raceId: {}, teamNum: {}, sessionNum: {}", laps.size(), raceId, teamNum, sessionNum);

            return laps;
        } catch (Exception e) {
            log.error("Unable to parse loaded session laps response for raceId: {}", raceId, e);
            return null;
        }
    }

    private HttpRequest.BodyPublisher preparePostParams(Map<String, Object> params) {
        StringBuilder postData = new StringBuilder();
        for (Map.Entry<String,Object> param : params.entrySet()) {
            if (postData.length() != 0) postData.append('&');
            postData.append(URLEncoder.encode(param.getKey(), UTF_8))
                    .append('=')
                    .append(URLEncoder.encode(String.valueOf(param.getValue()), UTF_8));
        }

        return HttpRequest.BodyPublishers.ofString(postData.toString());
    }

    private void persistIfNeeded(String data, String fileName) {
        if (!persistLoadedData) return;

        Path path = Paths.get(fileName);
        try {
            Files.writeString(path, data);
            log.info("Persisted {} bytes of response to: {}", data.getBytes(UTF_8).length, path);
        } catch (IOException e) {
            log.error("Unable to persist response data to : {}", fileName);
        }
    }
}
