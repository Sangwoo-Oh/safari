package com.bonsai.view;



import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.bonsai.controller.GameController;

public class ShopMenu extends Table {
    GameController controller;
    GameView gameView;
    private Skin skin;
    private final int buttonSize = 64;
    private CheckBox treeButton, pondButton, roadButton,bushButton,
                        jeepButton, rengerButton,
        lionButton, sheepButton,
        horseButton, cheetahButton;

    public final int TreePrice = 200;
    public final int PondPrice = 200;
    public final int RoadPrice = 200;
    public final int BushPrice = 200;
    public final int JeepPrice = 200;
    public final int RengerPrice = 200;
    public final int LionPrice = 800;
    public final int CheetahPrice = 800;
    public final int SheepPrice = 800;
    public final int HoursePrice = 800;

    private ButtonGroup<CheckBox> group = new ButtonGroup<>();
    public ShopMenu(GameController controller, GameView gameView){
        super();
        this.controller = controller;
        this.gameView = gameView;

        group.setMinCheckCount(0);
        group.setMaxCheckCount(1);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        treeButton          = makeButton("tree.png");
        pondButton          = makeButton("pond.png");
        roadButton          = makeButton("road.png");
        bushButton          = makeButton("bush_test.png");
        jeepButton          = makeButton("jeep.png");
        rengerButton        = makeButton("ranger.png");
        lionButton = makeButton("Lion.png");
        sheepButton = makeButton("sheep.png");
        horseButton = makeButton("horse.png");
        cheetahButton = makeButton("cheetah.png");

        this.add(makeButtonWithLabel(treeButton, TreePrice)).pad(5);
        this.add(makeButtonWithLabel(pondButton, PondPrice)).pad(5);
        this.row();

        this.add(makeButtonWithLabel(roadButton, RoadPrice)).pad(5);
        this.add(makeButtonWithLabel(bushButton, BushPrice)).pad(5);
        this.row();

        this.add(makeButtonWithLabel(jeepButton, JeepPrice)).pad(5);
        this.add(makeButtonWithLabel(rengerButton, RengerPrice)).pad(5);
        this.row();

        this.add(makeButtonWithLabel(lionButton, LionPrice)).pad(5);
        this.add(makeButtonWithLabel(sheepButton, CheetahPrice)).pad(5);
        this.row();

        this.add(makeButtonWithLabel(horseButton, SheepPrice)).pad(5);
        this.add(makeButtonWithLabel(cheetahButton, HoursePrice)).pad(5);
        this.row();


    }

    private void onCheckBoxChanged(){
        CheckBox checkedBox = group.getChecked();
        System.out.println("checkbox changed");

        if(checkedBox == null){
            gameView.setEditMode(false);
            controller.setSlectedTiles(null);
            return;
        }else{
            gameView.setEditMode(true);
            controller.setSlectedTiles(checkedBox);
        }

    }

    private CheckBox makeButton(String fileName){
        // 2枚の画像 (オフ/オン)
        Texture offTex = new Texture(Gdx.files.internal(fileName));

        Pixmap pixmap = new Pixmap(Gdx.files.internal(fileName));
        pixmap.setBlending(Pixmap.Blending.SourceOver);
        pixmap.setColor(new Color(0.5f,0.5f,0.5f,0.5f));
        pixmap.fillRectangle(0, 0, pixmap.getWidth(), pixmap.getHeight());
        Texture onTex = new Texture(pixmap);
        pixmap.dispose();

        TextureRegionDrawable offDrawable = new TextureRegionDrawable(offTex);
        TextureRegionDrawable onDrawable  = new TextureRegionDrawable(onTex);

        offDrawable.setMinWidth(buttonSize);
        offDrawable.setMinHeight(buttonSize);
        onDrawable.setMinWidth(buttonSize);
        onDrawable.setMinHeight(buttonSize);


        // 1) CheckBoxStyleを先に作り、checkboxOff/On等を設定
        CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
        style.checkboxOff = offDrawable;
        style.checkboxOn  = onDrawable;

        style.up = offDrawable;
        style.down = onDrawable;
        style.checked = onDrawable;
        style.checkedDown = offDrawable;
        // フォント指定（テキスト不要でも必須。適当にskinから取得）
        style.font = skin.getFont("default-font");
        style.fontColor = Color.WHITE;


        // 2) そのStyleを使ってCheckBoxを作成
        CheckBox checkBox = new CheckBox("", style);
        checkBox.setChecked(false); // 初期状態でチェックを外す
        group.add(checkBox);
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {

                onCheckBoxChanged();

            }
        });
        return checkBox;
    }

    private Table makeButtonWithLabel(CheckBox checkBox, int price) {

        Label label = new Label(price + "$", skin);
        label.setFontScale(0.8f);
        label.setColor(Color.BLACK);


        Table table = new Table();
        table.add(checkBox).size(buttonSize, buttonSize).row();
        table.add(label).padTop(2f);

        return table;
    }

    public void setNull(){
        this.controller.setSlectedTiles(null);
    }

    public CheckBox getTreeButton() {
        return treeButton;
    }

    public CheckBox getPondButton() {
        return pondButton;
    }

    public CheckBox getRoadButton() {
        return roadButton;
    }

    public CheckBox getBushButton() {
        return bushButton;
    }

    public CheckBox getJeepButton() {
        return jeepButton;
    }

    public CheckBox getRengerButton() {
        return rengerButton;
    }

    public CheckBox getLionButton() {
        return lionButton;
    }

    public CheckBox getSheepButton() {
        return sheepButton;
    }

    public CheckBox getHorseButton() {
        return horseButton;
    }

    public CheckBox getCheetahButton() {
        return cheetahButton;
    }

    public void resetSelection() {
        for (CheckBox checkBox : group.getButtons()) {
            checkBox.setChecked(false);
        }
    }

}
