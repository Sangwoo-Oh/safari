package com.bonsai.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bonsai.controller.GameController;
import com.bonsai.model.*;

public class ActionsMenu extends Table {
    private final GameController controller;
    private final Skin skin;
    private final Label animalInfoLabel;
    private final Label actionInfoLabel;
    private final Image animalImage;
    private final TextButton removeButton;
    private final TextButton locationChipButton;
    private Animal selectedAnimal;

    public ActionsMenu(GameController controller) {
        this.controller = controller;
        this.skin = new Skin(Gdx.files.internal("uiskin.json"));
        this.pad(10);

        animalImage = new Image();
        this.add(animalImage).size(100, 100).padBottom(10).row();


        animalInfoLabel = new Label("No animal selected", skin);
        this.add(animalInfoLabel).colspan(2).padBottom(10).row();

        actionInfoLabel = new Label("", skin);
        this.add(actionInfoLabel).colspan(2).padBottom(10).row();


        removeButton = new TextButton("Remove via Ranger", skin);
        removeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedAnimal != null) {
                    Ranger closest = controller.getClosestRangerTo(selectedAnimal);
                    if (closest != null && closest.getState() != Ranger.RangerState.FIGHTING) {
                        closest.setMission(selectedAnimal);
                        actionInfoLabel.setText("Removal mission set.");
                    } else {
                        actionInfoLabel.setText("No available ranger.");
                    }
                }
            }
        });

        // Location chip ボタン
        locationChipButton = new TextButton("Set Location Chip", skin);
        locationChipButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedAnimal != null && !selectedAnimal.hasLocationChip()) {
                    int chipCost = 50;
                    if (controller.getCapital().subtractMoney(chipCost)) {
                        selectedAnimal.setLocationChip();
                        actionInfoLabel.setText("Chip installed.");
                    } else {
                        actionInfoLabel.setText("Not enough money.");
                    }
                } else {
                    actionInfoLabel.setText("Already has chip.");
                }
            }
        });

        this.add(removeButton).pad(5).row();
        this.add(locationChipButton).pad(5).row();
    }

    public void setSelectedAnimal(Animal animal) {
        this.selectedAnimal = animal;
        if (animal != null) {
            String texturePath = getTexturePathForAnimal(animal);
            animalImage.setDrawable(new TextureRegionDrawable(new Texture(Gdx.files.internal(texturePath))));
            animalInfoLabel.setText("Selected: " + animal.getClass().getSimpleName());
        } else {
            animalInfoLabel.setText("No animal selected");
        }
    }

    private String getTexturePathForAnimal(Animal animal) {
        if (animal instanceof Lion) {
            return "Lion.png";
        } else if (animal instanceof Sheep) {
            return "sheep.png";
        } else if (animal instanceof Horse) {
            return "horse.png";
        } else if (animal instanceof Cheetah) {
            return "cheetah.png";
        }
        return "sheep.png"; // デフォルト
    }
}
