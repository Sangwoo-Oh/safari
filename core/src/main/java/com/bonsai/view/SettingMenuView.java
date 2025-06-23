package com.bonsai.view;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Color;
import com.bonsai.model.*;
import com.bonsai.controller.GameController;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;






public class SettingMenuView extends ScreenAdapter {
    private Stage stage;
    private Skin skin;
    private SpriteBatch batch;

    @Override
    public void show() {
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
        Label label = new Label("Safari Name", skin);
        table.add(label).colspan(10).center(); // 2列分を使って中央揃え
        table.row(); // 新しい行を開始

        TextField nameField = new TextField("", skin);
        // ラベルとテキストフィールドをテーブルに追加
        table.row(); // 行を変える
        table.add(nameField).colspan(10).center().pad(20);  // テキストフィールドを中央に配置
        table.row(); // 行を変える

        // difficulty label
        Label difficultyLabel = new Label("Difficulty", skin);
        table.add(difficultyLabel).colspan(10).center(); // 2列分を使って中央揃え
        table.row(); // 新しい行を開始


// ボタンを横一列に配置する部分の修正
        TextButton easyButton = new TextButton("Easy", skin);
        TextButton mediumButton = new TextButton("Medium", skin);
        TextButton hardButton = new TextButton("Hard", skin);

// ボタングループにボタンを追加
        ButtonGroup<TextButton> buttonGroup = new ButtonGroup<>(easyButton, mediumButton, hardButton);
        buttonGroup.setMaxCheckCount(1);  // 一度に選べるのは1つだけ
        buttonGroup.setMinCheckCount(1);  // 少なくとも1つ選ばなければならない

        ClickListener buttonClickListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Color defaultColor = Color.WHITE;
                easyButton.setColor(defaultColor);
                mediumButton.setColor(defaultColor);
                hardButton.setColor(defaultColor);

                // 押されたボタンを赤に変更
                ((TextButton) event.getListenerActor()).setColor(Color.RED);
            }
        };

// 各ボタンにリスナーを追加
        easyButton.addListener(buttonClickListener);
        mediumButton.addListener(buttonClickListener);
        hardButton.addListener(buttonClickListener);
// ボタンを横並びに配置
        table.add(easyButton).pad(10).fillX().uniformX();  // ボタンを横並びにするための設定
        table.add(mediumButton).pad(10).fillX().uniformX();
        table.add(hardButton).pad(10).fillX().uniformX();
        table.row();  // 新しい行を開始


        // ボタンの作成
        TextButton startButton = new TextButton("Start", skin);
        TextButton backButton = new TextButton("back to main menu", skin);

        // ボタンをテーブルに追加
        table.add(startButton).colspan(10).center().pad(20); // 横幅を均等にする
        table.row(); // 行を変える
        table.add(backButton).colspan(10).center(); // 横幅を均等にする


        // startButton, backButton addListener
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String safariName = nameField.getText().trim(); // 名前を取得し、前後の空白を除去
                TextButton selectedDifficulty = buttonGroup.getChecked(); // 選択された難易度ボタン

                if (safariName.isEmpty() && selectedDifficulty == null) {
                    // 必須項目が未入力なら警告ダイアログを表示
                    Dialog dialog = new Dialog("Warning", skin);
                    dialog.text("Please enter a Safari Name and select a Difficulty.");
                    dialog.button("OK", true); // OKボタン
                    dialog.show(stage);
                    return; // 処理を中断してゲームを開始しない
                } else if (safariName.isEmpty()) {
                    // 必須項目が未入力なら警告ダイアログを表示
                    Dialog dialog = new Dialog("Warning", skin);
                    dialog.text("Please enter a Safari Name.");
                    dialog.button("OK", true); // OKボタン
                    dialog.show(stage);
                    return; // 処理を中断してゲームを開始しない
                } else if (selectedDifficulty == null) {
                    // 必須項目が未入力なら警告ダイアログを表示
                    Dialog dialog = new Dialog("Warning", skin);
                    dialog.text("Please select a Difficulty.");
                    dialog.button("OK", true); // OKボタン
                    dialog.show(stage);
                    return; // 処理を中断してゲームを開始しない
                }



                int initialMoney = 10000000;
                Player player = new Player(safariName);
                Capital capital = new Capital(initialMoney);
                Time time = new Time();
                Inventory inventory = new Inventory();

                int requiredMonths;
                if (selectedDifficulty.getText().toString().equals("Easy")) {
                    requiredMonths = 3;
                }
                else if (selectedDifficulty.getText().toString().equals("Medium")) {
                    requiredMonths = 6;
                } else {
                    requiredMonths = 12;
                }

                GameModel gameModel = new GameModel(player, capital, time, inventory, requiredMonths);
                GameController gController = GameController.getInstance(gameModel);

                if (gController.getGameModel() != null) {
                    gController.reset(gameModel);
                } else {
                    gController.init(gameModel);
                }
                ModelBatch batch = new ModelBatch();
                GameView gView = new GameView(batch, gController);

                ((Game) Gdx.app.getApplicationListener()).setScreen(gView);
            }
        });



        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // SettingMenuViewのインスタンスを作成
                MainMenuView ainMenu = new MainMenuView();

                // 現在のGameインスタンスからsetScreenを呼び出す
                ((Game) Gdx.app.getApplicationListener()).setScreen(ainMenu);
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
