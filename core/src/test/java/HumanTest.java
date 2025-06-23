import com.badlogic.gdx.math.Vector3;
import com.bonsai.controller.GameController;
import com.bonsai.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HumanTest {
    private Ranger ranger;
    private Poacher poacher;
    private GameModel mockModel;
    private GameController mockController;
    private Time time;
    private Animal mockAnimal;
    @BeforeEach
    void setup() {

        mockModel = mock(GameModel.class);
        mockController = mock(GameController.class);
        mockAnimal = mock(Animal.class);
        time = mock(Time.class);
        when(mockModel.getMAP_SIZE()).thenReturn(3);
        Terrain[][] dummyMap = new Terrain[3][3];
        dummyMap[1][1] = new Entrance(1,1); // S
        dummyMap[2][2] = new Exit(2,2);     // G
        dummyMap[1][2] = new Road(1,2);     // R
        when(mockModel.getMap()).thenReturn(dummyMap);

        when(mockModel.getEntrancePosX()).thenReturn(1);
        when(mockModel.getEntrancePosY()).thenReturn(1);
        when(mockController.getGameModel()).thenReturn(mockModel);
        when(mockAnimal.getPosition()).thenReturn(new Vector3(0, 0, 0));
        ranger = new Ranger(0,0,0, mockController);
        poacher = new Poacher(0, 0, 0, mockController, mockAnimal);
    }

    @Test
    public void testInitialStateIsMovingRanger() {

        assertEquals(Ranger.RangerState.MOVING, ranger.getState());
    }

    @Test
    public void testSetMissionUpdatesState() {
        Animal dummyAnimal = mock(Animal.class);
        ranger.setMission(dummyAnimal);
        assertEquals(Ranger.RangerState.MISSION , ranger.getState());
    }

    @Test
    public void testFightStateTransition() {
        ranger.setFightState();
        assertEquals(Ranger.RangerState.FIGHTING, ranger.getState());
    }


    @Test
    void testInitialStateIsMovingPoacher() {
        assertEquals(Poacher.PoacherState.MOVING, poacher.getState());
    }

    @Test
    void testFightStateWhenNearRanger() {
        Ranger mockRanger = mock(Ranger.class);
        when(mockRanger.getPosition()).thenReturn(new Vector3(0, 0, 0));

        when(mockController.getRangerFromModel()).thenReturn(Collections.singletonList(mockRanger));
        poacher.update(1.0f, mock(Time.class));

        assertEquals(Poacher.PoacherState.FIGHTING, poacher.getState());
    }

    @Test
    void testBackStateAfterHunting() {

        poacher.update(1.0f, mock(Time.class)); // ← 1回目のupdateでHUNTINGに
        poacher.update(6.0f, mock(Time.class)); // ← 十分なdeltaでBACKINGへ

        assertEquals(Poacher.PoacherState.BACKING, poacher.getState());
    }

    @Test
    void testLoseCallsRemove() {
        poacher.lose();
        verify(mockModel).addHumanToRemove(poacher);
    }

}
