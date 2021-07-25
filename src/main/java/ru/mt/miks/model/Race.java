package ru.mt.miks.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Race {
    private List<Session> sessions;

    @JsonProperty("warns")
    private List<Warn> warnings;

    @JsonProperty("raceSettings")
    private RaceSettings raceSettings;

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public List<Warn> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<Warn> warnings) {
        this.warnings = warnings;
    }

    public RaceSettings getRaceSettings() {
        return raceSettings;
    }

    public void setRaceSettings(RaceSettings raceSettings) {
        this.raceSettings = raceSettings;
    }
}
