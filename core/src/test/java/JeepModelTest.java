package com.bonsai.model;

import com.badlogic.gdx.math.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Unit tests for {@link Jeep}. */
class JeepModelTest {
    private GameModel mockModel;
    private Capital mockCapital;
    private JeepManager spyManager;
    private Jeep jeep;

    @BeforeEach
    void setUp() {
        mockModel = mock(GameModel.class);
        when(mockModel.getMAP_SIZE()).thenReturn(3);

        Terrain[][] map = new Terrain[3][3];
        map[1][1] = new Entrance(1, 1);
        map[1][2] = new Road(1, 2);
        map[2][2] = new Exit(2, 2);
        when(mockModel.getMap()).thenReturn(map);
        when(mockModel.getEntrancePosX()).thenReturn(1);
        when(mockModel.getEntrancePosY()).thenReturn(1);

        mockCapital = mock(Capital.class);
        when(mockModel.getCapital()).thenReturn(mockCapital);

        spyManager = Mockito.spy(new JeepManager(mockModel));
        jeep = new Jeep(spyManager);
    }

    // reflection helper
    private <T> T getField(Object target, String name, Class<T> type) {
        try {
            Field f = target.getClass().getDeclaredField(name);
            f.setAccessible(true);
            return type.cast(f.get(target));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void refreshPath_reachesExit() {
        jeep.refreshPath();
        List<Vector3> path = getField(jeep, "path", List.class);
        Vector3 last = path.get(path.size() - 1);
        assertEquals(20f, last.x, 0.001f);
        assertEquals(20f, last.z, 0.001f);
    }

    @Test
    void refreshPathWithDelay_setsWaiting() {
        jeep.refreshPathWithDelay(0.5f);
        assertTrue(getField(jeep, "isWaiting", Boolean.class));
        assertEquals(-0.5f, getField(jeep, "waitTimer", Float.class), 0.0001f);
    }

    @Test
    void goalArrival_rewardsAndResets() {
        List<Tourist> tourists = new ArrayList<>(Arrays.asList(
            mock(Tourist.class), mock(Tourist.class), mock(Tourist.class)));
        jeep.setPassengers(tourists);
        assertEquals(3, jeep.getPassengers().size());

        Time time = mock(Time.class);
        when(time.getHoursToAdvance()).thenReturn(Time.Speed.HOUR);

        for (int i = 0; i < 20 && !jeep.getPassengers().isEmpty(); i++) {
            jeep.update(1f, time);
        }

        assertTrue(jeep.getPassengers().isEmpty());
        verify(mockModel).incrementVisitorCount(3);
        verify(mockCapital).addMoney(300);
        verify(spyManager).reassignTouristsToJeep(jeep);

        assertTrue(getField(jeep, "isWaiting", Boolean.class));
        assertEquals(-1.5f, getField(jeep, "waitTimer", Float.class), 0.0001f);
    }
}
