package com.bonsai.controller.factory;

import com.bonsai.view.terrain.GrassView;
import com.bonsai.model.Terrain;
import com.bonsai.view.terrain.TerrainView;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class TerrainViewFactory {
    private final Map<Class<? extends Terrain>, Supplier<TerrainView>> registry = new HashMap<>();

    public void registerTerrain(Class<? extends Terrain> terrainClass, Supplier<TerrainView> supplier) {
        registry.put(terrainClass, supplier);
    }

    public TerrainView createTerrain(Terrain terrain) {
        Supplier<TerrainView> supplier = registry.get(terrain.getClass());
        if (supplier != null) {
            return supplier.get();
        }
        throw new IllegalArgumentException("Unknown terrain type: " + terrain.getClass());
    }
}
