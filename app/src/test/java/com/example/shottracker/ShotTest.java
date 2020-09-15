package com.example.shottracker;

import org.junit.Test;
import static org.junit.Assert.*;


public class ShotTest {
    Shot shot = new Shot("Driver", 250.0, "Straight", "Super windy");

    @Test
    public void getClub() {
        assertEquals("Driver", shot.getClub());
    }

    @Test
    public void getDistance() {
        assertEquals(250.0, shot.getDistance(), 1);
    }

    @Test
    public void getBallFlight() {
        assertEquals("Straight", shot.getBallFlight());
    }

    @Test
    public void getNotes() {
        assertEquals("Super windy", shot.getNotes());
    }
}
