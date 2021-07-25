package ru.mt.miks.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Warn {
    @JsonProperty("RegNumber")
    private Integer teamNumber;

    @JsonProperty("Category")
    private WarnCategory category;

    @JsonProperty("Text")
    private String description;

    public Integer getTeamNumber() {
        return teamNumber;
    }

    public void setTeamNumber(Integer teamNumber) {
        this.teamNumber = teamNumber;
    }

    public WarnCategory getCategory() {
        return category;
    }

    public void setCategory(WarnCategory category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public enum WarnCategory {
        warn,
        stop,

        @JsonProperty("return")
        Return // compensation
    }
}
