package com.bonsai;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g3d.ModelBatch;

import com.bonsai.controller.GameController;
import com.bonsai.model.*;
import com.bonsai.view.GameView;
import com.bonsai.view.MainMenuView;
//import com.bonsai.view.MainMenuView;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends Game {

    private ModelBatch batch;
    private GameView gView;
    private GameModel model;
    private GameController controller;


    @Override
    public void create() {

        batch = new ModelBatch();

        // main menu
        MainMenuView mainMenu = new MainMenuView();

        // setting menu
        // SettingMenuView settingMenu = new SettingMenuView();


        // model = new GameModel(null, null, null);

        // controller = GameController.getInstance(model);
        int initialMoney = 1000000;
        Player player = new Player("ken");
        Capital capital = new Capital(initialMoney);
        Time time = new Time();
        Inventory inventory = new Inventory();
        GameModel gameModel = new GameModel(player, capital, time, inventory, 3);
        GameController gController = GameController.getInstance(gameModel);

        ModelBatch batch = new ModelBatch();
        GameView gView = new GameView(batch, gController);

        setScreen(gView);
//        setScreen(mainMenu);
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
        super.dispose();
    }
}
