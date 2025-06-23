package com.bonsai.controller.factory;

import com.bonsai.model.Jeep;
import com.bonsai.view.jeep.JeepView;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class JeepViewFactory {
    private final Map<Class<? extends Jeep>, Supplier<JeepView>> registry = new HashMap<>();
    public void registerJeep(Class<? extends  Jeep> jeepClass, Supplier<JeepView> supplier){
        registry.put(jeepClass, supplier);
    }
    public JeepView createJeep(Jeep jeep){
        Supplier<JeepView> supplier = registry.get(jeep.getClass());
        if(supplier != null){
            return supplier.get();
        }

        throw  new IllegalArgumentException("Unknown jeep type: " + jeep.getClass());
    }
}
