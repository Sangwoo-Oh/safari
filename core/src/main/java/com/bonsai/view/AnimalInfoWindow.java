package com.bonsai.view;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bonsai.controller.GameController;
import com.bonsai.model.Animal;
import com.bonsai.model.Ranger;

public class AnimalInfoWindow extends Window {
    private TextButton removeButton;
    private TextButton locationChipButton;
    private Animal targetAnimal;

    public AnimalInfoWindow(Skin skin, GameController controller, Runnable onClose) {
        super("Animal Info", skin);
        setMovable(true);
        setResizable(false);
        setVisible(false); // 最初は非表示

        removeButton = new TextButton("Remove", skin);
        removeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (targetAnimal != null) {

                    System.out.println("removed");

                    Ranger closest = controller.getClosestRangerTo(targetAnimal);
                    if (closest != null && closest.getState() != Ranger.RangerState.FIGHTING) {
                        closest.setMission(targetAnimal);
                    }
//                    controller.removeAnimal(targetAnimal);
                    onClose.run(); // 呼び出し元に通知（UI更新など）
                    setVisible(false);
                }
            }
        });

        locationChipButton = new TextButton("Set location chip", skin);
        locationChipButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (targetAnimal != null && !targetAnimal.hasLocationChip()) {
                    int chipCost = 10;
                    if (controller.getCapital().subtractMoney(chipCost)) {
                        targetAnimal.setLocationChip();
                        System.out.println("Location chip installed.");
                    } else {
                        System.out.println("Not enough money to buy a location chip.");
                    }
                }
            }
        });
        add(removeButton).pad(10);
        pack();
        add(locationChipButton).pad(10);
        pack();
    }
}
