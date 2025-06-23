package com.bonsai.controller;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bonsai.view.GameView;
import com.bonsai.view.SideMenu;

public class SideMenuController {
    private final SideMenu sideMenu;

    public SideMenuController(SideMenu sideMenu) {
        this.sideMenu = sideMenu;
        initListeners();
    }

    public void initListeners() {


        sideMenu.getTabButton1().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //sideMenu.showContent1();
                System.out.println("tabButton1 clicked");
            }
        });

        sideMenu.getTabButton2().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //sideMenu.showContent1();
                System.out.println("tabButton2 clicked");
            }
        });

        sideMenu.getTabButton3().addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //sideMenu.showContent1();
                System.out.println("tabButton3 clicked");
            }
        });


    }
}
