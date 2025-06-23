package com.bonsai.controller.factory;

import com.bonsai.model.Human;
import com.bonsai.view.human.HumanView;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class HumanViewFactory {
    private final Map<Class<? extends Human>, Supplier<HumanView>> registry = new HashMap<>();
    public void registerHuman(Class<? extends  Human> humanClass, Supplier<HumanView> supplier){
        registry.put(humanClass, supplier);
    }
    public HumanView createHuman(Human human){
        Supplier<HumanView> supplier = registry.get(human.getClass());
        if(supplier != null){
            return supplier.get();
        }

        throw  new IllegalArgumentException("Unknown human type: " + human.getClass());
    }
}
