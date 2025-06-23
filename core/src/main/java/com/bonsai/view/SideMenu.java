package com.bonsai.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bonsai.controller.GameController;
import com.bonsai.controller.SideMenuController;

public class SideMenu extends Table {

    private Table content;
    private TextButton tabButton1,tabButton2,tabButton3;
    private Skin skin;
    private final SideMenuController sideMenuController;
    private GameController controller;
    private GameView gameView;
    private ShopMenu shopMenu;
    private InventoryMenu inventoryMenu;
    private ActionsMenu actionsMenu;

    public SideMenu(Skin skin, GameController controller, GameView gameView) {
        super(skin);
        this.skin = skin;
        this.controller = controller;
        this.gameView = gameView;
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();


        skin.add("white", new Texture(pixmap));
        pixmap.dispose();
        setBackground(skin.newDrawable("white", Color.WHITE));



        tabButton1 = new TextButton("SHOP", skin);
        tabButton2 = new TextButton("INVENTORY", skin);
        tabButton3 = new TextButton("ACTIONS", skin);

        this.add(tabButton1).pad(5);
        this.add(tabButton2).pad(5);
        this.add(tabButton3).pad(5);

        this.sideMenuController = new SideMenuController(this);

        row();

        shopMenu = new ShopMenu(controller, gameView);
        inventoryMenu = new InventoryMenu(controller, gameView);
        actionsMenu = new ActionsMenu(controller);

        // content パネルの作成と追加
        content = new Table();
        content.setBackground(skin.newDrawable("white", Color.LIGHT_GRAY));
        this.add(content).colspan(3).expand().fill().padTop(10);

        showContent1();


        tabButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showContent1();
                inventoryMenu.resetSelection();
            }
        });

        tabButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showContent2();
                shopMenu.resetSelection();
            }
        });

        tabButton3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showContent3();
                inventoryMenu.resetSelection();
                shopMenu.resetSelection();
            }
        });
    }
    public ShopMenu getShopMenu(){
//        CheckBox check = content.getSelectedCheckBox();
        return shopMenu;
//        return controller.setSelectedCheckBox(content.getSelectedCheckBox());
    }

    public ActionsMenu getActionsMenu() {
        return actionsMenu;
    }

    public InventoryMenu getInventoryMenu(){
        return  this.inventoryMenu;
    }
    public void showContent1() {
        content.clearChildren();

        Label title = new Label("SHOP MENU", skin, "default");
        content.add(title).padBottom(10).colspan(1).center().row();

        content.add(shopMenu).expand().fill();
    }

    public void showContent2() {
        content.clearChildren();

        Label title = new Label("INVENTORY MENU", skin, "default");
        content.add(title).padBottom(10).colspan(1).center().row();

        content.add(inventoryMenu).expand().fill();
    }

    public void showContent3() {

        content.clearChildren();

        Label title = new Label("ACTIONS MENU", skin, "default");
        content.add(title).padBottom(10).colspan(1).center().row();

        content.add(actionsMenu).expand().fill();
    }

    public TextButton getTabButton1() { return tabButton1; }
    public TextButton getTabButton2() { return tabButton2; }
    public TextButton getTabButton3() { return tabButton3; }


}
