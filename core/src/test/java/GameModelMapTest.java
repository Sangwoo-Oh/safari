package com.bonsai.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GameModelMapTest {
    @Test
    void template_hasRequiredInvariants() {
        GameModel mockModel = mock(GameModel.class);

        when(mockModel.getEntrancePosX()).thenReturn(0);
        when(mockModel.getEntrancePosY()).thenReturn(5);
        when(mockModel.getExitPosX()).thenReturn(19);
        when(mockModel.getExitPosY()).thenReturn(5);

        MapGenerator gen = new MapGenerator(mockModel);



        Terrain[][] map = gen.getTemplate();
        int size = 20;

        assertEquals(size, map.length);
        assertEquals(size, map[0].length);

        int entrance = 0, exit = 0, ponds = 0;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                Terrain t = map[y][x];
                if (t instanceof Entrance) {
                    entrance++;
                    boolean okPosition =
                        (x == 0  && y == 5) ||
                            (x == 5  && y == 0);
                    assertTrue(okPosition,
                        String.format("Entrance must be at (0,5) or (5,0) but was (%d,%d)", x, y));
                } else if (t instanceof Exit) {
                    exit++;
                    boolean okExit =
                        (x == 19 && y == 5) ||
                            (x == 14 && y == 19);
                    assertTrue(okExit,
                        String.format("Exit must be at (19,5) or (14,19) but was (%d,%d)", x, y));
//                    assertEquals(19, x);
//                    assertEquals(5, y);
                }
                if (t instanceof Pond) ponds++;
            }
        }

        assertEquals(1, entrance);
        assertEquals(1, exit);
        assertTrue(ponds >= 4); // at least one pond square present
    }
}
