package ru.mt.miks.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DriverData {
    @JsonProperty("Id")
    private String id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Comp")
    private String teamName;

    @JsonProperty("Weight")
    private int weight;

    @JsonProperty("Girl")
    private boolean isGirl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public boolean isGirl() {
        return isGirl;
    }

    public void setGirl(boolean girl) {
        isGirl = girl;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", teamName='" + teamName + '\'' +
                ", weight='" + weight + '\'' +
                '}';
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DriverDataHolder {
        @JsonProperty("drv")
        private DriverData data;

        private int age;

        public DriverData getData() {
            return data;
        }

        public void setData(DriverData data) {
            this.data = data;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}
