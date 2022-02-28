package ru.mt.miks.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.mt.miks.model.RaceSettings;
import ru.mt.miks.model.Session;
import ru.mt.miks.model.Team;
import ru.mt.miks.model.Warn;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RaceData {
    private List<Session> sessions;

    @JsonProperty("warns")
    private List<Warn> warnings;

    @JsonProperty("raceSettings")
    private RaceSettings raceSettings;

    @JsonProperty("comps")
    private List<Team> teams;

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

    public List<Team> getTeams() {
        return teams;
    }

    public void setTeams(List<Team> teams) {
        this.teams = teams;
    }
}
