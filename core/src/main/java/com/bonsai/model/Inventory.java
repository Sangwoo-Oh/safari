package com.bonsai.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Inventory {
    public List<Terrain> entityList;

    private Map<Class<? extends  Terrain>, Integer> entityCount;

    public Inventory(){
        this.entityCount = new HashMap<>();
        addEntity(Tree.class);
    }

    public void addEntity(Class<? extends Terrain> clazz) {
        entityCount.put(clazz, entityCount.getOrDefault(clazz, 0) + 1);
    }

    public void removeEntity(Class<? extends Terrain> clazz) {
        int current = entityCount.getOrDefault(clazz, 0);
        if (current > 0) {
            entityCount.put(clazz, current - 1);
        }
    }

    public int getCount(Class<? extends Terrain> clazz) {
        return entityCount.getOrDefault(clazz, 0);
    }

    @Override
    public String toString(){
        for (Map.Entry<Class<? extends Terrain>, Integer> entry : entityCount.entrySet()) {
            System.out.println(entry.getKey().getSimpleName() + ": " + entry.getValue());
        }
        return null;
    }

}
