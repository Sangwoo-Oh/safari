package com.bonsai.model;

import java.time.Duration;
import java.time.Instant;

public class Time {

    // 1 second = 1 game hour
    private static final long GAME_HOUR_DURATION = 1 * 1000; // 1 second = 1 game hour

    private Instant startTime; // Real-world start time
    private Instant currentTime; // Real-world current time
    private Speed hoursToAdvance; // Current game speed
    private long elapsedTimeInGame; // In-game elapsed time in hours
    private Instant lastUpdateTime; // last changed log
    private int lastMonth = -1;

    // Speed types
    public enum Speed {
        HOUR(1),
        DAY(24),
        WEEK(24 * 7);

        private final int hours;

        Speed(int hours) {
            this.hours = hours;
        }

        public int getHours() {
            return hours;
        }
    }

    // Constructor: Initialize with current time and default speed
    public Time() {
        this.startTime = Instant.now();
        this.currentTime = startTime;
        this.lastUpdateTime = startTime;
        this.hoursToAdvance = Speed.HOUR;
        this.elapsedTimeInGame = 0;
    }

    // Update real-world time and calculate game world time based on speed
    public void updateCurrentTime() {
        Instant now = Instant.now();
        long realTimeMillis = Duration.between(lastUpdateTime, now).toMillis();
        long gameHoursPassed = realTimeMillis / GAME_HOUR_DURATION;

        if (gameHoursPassed > 0) {
            this.elapsedTimeInGame += gameHoursPassed * hoursToAdvance.getHours();
            this.lastUpdateTime = now;
        }
    }

    public Speed getHoursToAdvance(){
        return this.hoursToAdvance;
    }


    // Advance game time based on selected speed
    public void advanceTime(Speed speed) {
        updateCurrentTime(); // Ensure consistent time update before advancing
    }

    // Get total elapsed game hours
    public long getTotalElapsedGameHours() {
        return elapsedTimeInGame;
    }

    // Get elapsed game years
    public int getElapsedYears() {
        return (int) (getTotalElapsedGameHours() / (12 * 30 * 24));
    }

    // Get elapsed game months
    public int getElapsedMonths() {
        return (int) ((getTotalElapsedGameHours() % (12 * 30 * 24)) / (30 * 24));
    }

    // Get elapsed game days
    public int getElapsedDays() {
        return (int) ((getTotalElapsedGameHours() % (30 * 24)) / 24);
    }

    // Get elapsed game hours (less than a day)
    public int getElapsedHours() {
        return (int) (getTotalElapsedGameHours() % 24);
    }

    public void setHour(){
        updateCurrentTime();
        this.hoursToAdvance = Speed.HOUR;
    }
    public void setDay(){
        updateCurrentTime();
        this.hoursToAdvance = Speed.DAY;
    }
    public void setWeek(){
        updateCurrentTime();
        this.hoursToAdvance = Speed.WEEK;
    }

    public enum TimeOfDay {
        DAY, SUNSET, NIGHT
    }

    public TimeOfDay getTimeOfDay() {
        int hour = getElapsedHours();

        if (hour >= 4 && hour < 14) {
            return TimeOfDay.DAY;
        } else if (hour >= 14 && hour < 18) {
            return TimeOfDay.SUNSET;
        } else {
            return TimeOfDay.NIGHT;
        }
    }


    public boolean hasMonthChanged() {
        int currentMonth = getElapsedMonths();
        if (currentMonth != lastMonth) {
          System.out.println("month was changed");
            lastMonth = currentMonth;
            return true;
        }
        return false;
    }


    // Display game time in a readable format
    @Override
    public String toString() {
        return String.format(
            "%dy %dm %dd %dh",
            getElapsedYears(),
            getElapsedMonths(),
            getElapsedDays(),
            getElapsedHours()
        );
    }
}
