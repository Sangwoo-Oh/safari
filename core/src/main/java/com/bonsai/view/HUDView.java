package com.bonsai.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bonsai.controller.GameController;
import com.bonsai.model.Time;
import com.sun.tools.javac.comp.Check;

public class HUDView {
    private Stage stage;
    private Skin skin;
    private Label playerNameLabel;
    private Label capitalLabel;
    private Label timeLabel;
    private Label animalCountLabel;
    private Label fpsLabel;
    private Label attractivenessLabel;
    private Label waitingTouristCountLabel;
    private Label herbivoreCountLabel;
    private Label carnivoreCountLabel;
    private Label jeepCountLabel;
    private Label monthlyVisitorCountLabel;

    private CheckBox speed1;
    private CheckBox speed2;
    private CheckBox speed3;
    private ButtonGroup<CheckBox> group = new ButtonGroup<>();

    private GameController controller;

    public HUDView(GameController controller) {
        this.controller = controller;

        stage = new Stage(new ScreenViewport());
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Initialize Label
        playerNameLabel = new Label("Player: " + this.controller.getSafariName(), skin);
        capitalLabel = new Label("Capital: " + this.controller.getCapital().getMoney(), skin);
        timeLabel = new Label(controller.getTime().toString(), skin);
        animalCountLabel = new Label("Animals: " + this.controller.getAnimalsFromModel().size(), skin);
        attractivenessLabel = new Label("Attractiveness: " + this.controller.getAttractiveness(), skin);
        waitingTouristCountLabel = new Label("Waiting tourists: " + this.controller.getWaitingTourist().size(), skin);
        herbivoreCountLabel = new Label("Herbivorous: " + controller.getHerbivorous().size(), skin);
        carnivoreCountLabel = new Label("Carnivores: " + controller.getCarnivores().size(), skin);
        jeepCountLabel = new Label("Jeeps: " + controller.getJeeps().size(), skin);
        monthlyVisitorCountLabel = new Label("Monthly Visitors: " + controller.getMonthlyVisitorCount(), skin);

        initButton();
        //FPS label
        fpsLabel = new Label("FPS: " + Gdx.graphics.getFramesPerSecond(), skin);

        // Table layout
        Table table = new Table();
        table.top().left();
        table.setFillParent(true);

        table.add(playerNameLabel).pad(5).left().colspan(2);
        table.row();
        table.add(capitalLabel).pad(5).left();
        table.row();
        table.add(timeLabel).pad(5).left();
        table.row();
        table.add(animalCountLabel).pad(5).left();
        table.row();
        table.add(herbivoreCountLabel).pad(5).left();
        table.row();
        table.add(carnivoreCountLabel).pad(5).left();
        table.row();
        table.add(jeepCountLabel).pad(5).left();
        table.row();
        table.add(attractivenessLabel).pad(5).left();
        table.row();
        table.add(waitingTouristCountLabel).pad(5).left();
        table.row();
        table.add(monthlyVisitorCountLabel).pad(5).left();
        table.row();
        table.add(fpsLabel).pad(5).left();


        stage.addActor(table);


    }

    public void initButton() {

        Time.Speed speed = controller.getGameModel().getTime().getHoursToAdvance();

        speed1 = new CheckBox("hour", skin);
        speed2 = new CheckBox("day", skin);
        speed3 = new CheckBox("week", skin);

        switch (speed) {
            case HOUR:
                speed1.setChecked(true);
                break;
            case DAY:
                speed2.setChecked(true);
                break;
            case WEEK:
                speed3.setChecked(true);
                break;
        }


        group.setMaxCheckCount(1);
        group.setMinCheckCount(1);
        group.add(speed1, speed2, speed3);

        speed1.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (speed1.isChecked()) {
                    System.out.println("Normal speed selected");
                    controller.getTime().setHour();
                }
            }
        });

        speed2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (speed2.isChecked()) {
                    System.out.println("2x speed selected");
                    controller.getTime().setDay();
                }
            }
        });

        speed3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (speed3.isChecked()) {
                    System.out.println("3x speed selected");
                    controller.getTime().setWeek();
                }
            }
        });


        Table speedTable = new Table();
        // speedTable を画面左下に配置。setFillParent(true)にすると全体に広がるので、代わりに setBounds() または setPosition() を利用。
        // ここではシンプルに、左下のパディングをつけた位置に配置します:
        speedTable.setPosition(100, 100);  // 左下から10pxずつ内側
        // または speedTable.setBounds(10, 10, desiredWidth, desiredHeight);
        speedTable.add(speed1).pad(5);
        speedTable.row();
        speedTable.add(speed2).pad(5);
        speedTable.row();
        speedTable.add(speed3).pad(5);


        // speedTableをステージに追加
        stage.addActor(speedTable);

    }

    public void updateTimeLabel() {
        timeLabel.setText(controller.getTime().toString());
    }

    public void updateCapitalLabel() {
        capitalLabel.setText("Capital: " + controller.getCapital().getMoney());
    }

    public void updateFPSLabel() {
        fpsLabel.setText("FPS: " + Gdx.graphics.getFramesPerSecond());
    }

    public void updateAnimalCountLabel() {
        animalCountLabel.setText("Animals: " + this.controller.getAnimalsFromModel().size());
    }

    public void updateAttractivenessLabel() {
        attractivenessLabel.setText("Attractiveness: " + this.controller.getAttractiveness());
    }

    public void updateWaitingTouristCountLabel() {
        waitingTouristCountLabel.setText("Waiting tourists: " + this.controller.getWaitingTourist().size());
    }

    public void updateHerbivoreCountLabel() {
        herbivoreCountLabel.setText("Herbivores: " + controller.getHerbivorous().size());
    }

    public void updateCarnivoreCountLabel() {
        carnivoreCountLabel.setText("Carnivores: " + controller.getCarnivores().size());
    }

    public void updateJeepCountLabel() {
        jeepCountLabel.setText("Jeeps: " + controller.getJeeps().size());
    }

    public void updateMonthlyVisitorCountLabel() {
        monthlyVisitorCountLabel.setText("Monthly Visitors: " + controller.getMonthlyVisitorCount());
    }

    public void render(float delta) {
        controller.update(delta);
        updateTimeLabel();
        updateFPSLabel();
        updateAnimalCountLabel();
        updateAttractivenessLabel();
        updateWaitingTouristCountLabel();
        updateHerbivoreCountLabel();
        updateCarnivoreCountLabel();
        updateJeepCountLabel();
        updateMonthlyVisitorCountLabel();
        updateCapitalLabel();
        stage.act(delta);
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
