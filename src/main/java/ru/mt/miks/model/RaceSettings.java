package ru.mt.miks.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.text.WordUtils;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RaceSettings {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    @JsonProperty("RaceName")
    private String raceName;

    @JsonProperty("Stage")
    private int stage;

    @JsonProperty("Start")
    private Date raceDateTime;

    public String getRaceName() {
        return raceName;
    }

    public void setRaceName(String raceName) {
        this.raceName = raceName;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public Date getRaceDateTime() {
        return raceDateTime;
    }

    public void setRaceDateTime(Date raceDateTime) {
        this.raceDateTime = raceDateTime;
    }

    public String getRaceID() {
        LocalDate ld = raceDateTime != null ? LocalDate.ofInstant(raceDateTime.toInstant(), ZoneOffset.UTC) : null ;
        String name = WordUtils.capitalizeFully(raceName, ' ').replaceAll(" ", "");
        return name + "#" + stage + (ld != null ? ("-" + ld.format(formatter)) : "");
    }
}
