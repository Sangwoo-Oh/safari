package com.bonsai.controller.factory;

import com.bonsai.model.Animal;
import com.bonsai.view.animal.AnimalView;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class AnimalViewFactory {
    private final Map<Class<? extends Animal>, Supplier<AnimalView>> registry = new HashMap<>();
    public void registerAnimal(Class<? extends  Animal> animalClass, Supplier<AnimalView> supplier){
        registry.put(animalClass, supplier);
    }
    public AnimalView createAnimal(Animal animal){
        Supplier<AnimalView> supplier = registry.get(animal.getClass());
        if(supplier != null){
            return supplier.get();
        }

        throw  new IllegalArgumentException("Unknown animal type: " + animal.getClass());
    }
}
