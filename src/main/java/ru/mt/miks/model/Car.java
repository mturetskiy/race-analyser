package ru.mt.miks.model;

import java.util.*;

public class Car {
    private int carNumber;
    private TreeSet<Integer> bestLaps;

    public Car(int carNumber) {
        this.carNumber = carNumber;
        this.bestLaps = new TreeSet<>(Comparator.naturalOrder());
    }

    public void addBestLap(Integer bestLap) {
        bestLaps.add(bestLap);
    }

    public int getCarNumber() {
        return carNumber;
    }

    public Integer getTopBestLap() {
        if (bestLaps.isEmpty()) return 0;
        return bestLaps.first();
    }

    public Integer getWorstBestLap() {
        if (bestLaps.isEmpty()) return 0;
        return bestLaps.last();
    }

    @Override
    public String toString() {
        return "Car #" + carNumber +
                " [topBest=" + getTopBestLap() +
                ", lowBest=" + getWorstBestLap() +
                ", bestLaps=" + bestLaps +
                ']';
    }
}
