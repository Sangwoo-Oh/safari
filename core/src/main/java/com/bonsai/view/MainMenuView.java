package com.bonsai.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.Game;
import com.bonsai.view.SettingMenuView; // SettingMenuViewがこのパッケージにあると仮定
import com.badlogic.gdx.scenes.scene2d.InputEvent;





public class MainMenuView extends ScreenAdapter {
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;


    @Override
    public void show() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        // ステージとバッチの作成
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport(), batch);

        // スキンの作成 (デフォルトのSkinを使用)
        skin = new Skin(Gdx.files.internal("uiskin.json")); // 事前に作成しておいたskinファイルを指定

        // ステージにボタンを配置
        Table table = new Table();
        table.center(); // テーブルを画面の中央に配置
        table.setFillParent(true); // ステージ全体に合わせる

        // safari label
        Label label = new Label("Safari", skin);
        table.add(label).colspan(2).center().pad(20); // 2列分を使って中央揃え
        table.row(); // 新しい行を開始

        // ボタンの作成
        TextButton newGameButton = new TextButton("Create new game", skin);
        TextButton loadGameButton = new TextButton("Continue game", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // ボタンをテーブルに追加
        table.add(newGameButton).fillX().uniformX().pad(10); // 横幅を均等にする
        table.row().pad(10, 0, 10, 0); // 行の間隔を設定
        table.add(loadGameButton).fillX().uniformX().pad(10); // 横幅を均等にする
        table.row().pad(10, 0, 10, 0); // 行の間隔を設定
        table.add(exitButton).fillX().uniformX().pad(10); // 横幅を均等にする

        newGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // SettingMenuViewのインスタンスを作成
                SettingMenuView settingMenu = new SettingMenuView();

                // 現在のGameインスタンスからsetScreenを呼び出す
                ((Game) Gdx.app.getApplicationListener()).setScreen(settingMenu);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });



        // ステージにテーブルを追加
        stage.addActor(table);

        // 入力処理をステージに設定
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.dispose();
    }
}
