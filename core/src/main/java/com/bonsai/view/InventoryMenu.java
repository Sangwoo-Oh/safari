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
import com.bonsai.model.*;
import com.bonsai.model.Tree;

public class InventoryMenu extends Table {
    GameController controller;
    GameView gameView;
    private Skin skin;
    private final int buttonSize = 64;
    private CheckBox treeButton, pondButton, roadButton,bushButton, grassButton;
    private Inventory inventory;

    private int TreeCount, PondCount, RoadCount, BushCount;
    private Label treeLabel, pondLabel, roadLabel, bushLabel, grassLabel;


    private ButtonGroup<CheckBox> group = new ButtonGroup<>();
    public InventoryMenu(GameController controller, GameView gameView){
        super();
        this.controller = controller;
        this.gameView = gameView;
        this.inventory = controller.getGameModel().getInventory();
        group.setMinCheckCount(0);
        group.setMaxCheckCount(1);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        treeButton          = makeButton("tree.png");
        pondButton          = makeButton("pond.png");
        roadButton          = makeButton("road.png");
        bushButton          = makeButton("bush_test.png");
        grassButton         = makeButton("Grass_top_view_test.png");

        this.TreeCount = inventory.getCount(Tree.class);
        this.PondCount = inventory.getCount(Pond.class);
        this.RoadCount = inventory.getCount(Road.class);
        this.BushCount = inventory.getCount(Bush.class);



        // それぞれのボタンを1行ずつ追加 (例)
        // 必要に応じてrow()の呼び出し位置を調整
        this.add(makeButtonWithLabel(treeButton, TreeCount, "tree")).pad(5);
        this.add(makeButtonWithLabel(pondButton, PondCount, "pond")).pad(5);
        this.row();

        this.add(makeButtonWithLabel(roadButton, RoadCount, "road")).pad(5);
        this.add(makeButtonWithLabel(bushButton, BushCount, "bush")).pad(5);
        this.row();

        this.add(makeButtonWithLabel(grassButton, 0, "Grass")).pad(5);

    }

    private void onCheckBoxChanged(){
        CheckBox checkedBox = group.getChecked();

        if(checkedBox == null){
            gameView.setEditMode(false);

            return;
        }else{
            gameView.setEditMode(true);
            controller.setInventoryCheckBox(checkedBox);
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
//        Texture onTex  = new Texture(Gdx.files.internal("tree.png"));

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
            public void changed(ChangeEvent event, Actor actor) {

                onCheckBoxChanged();

            }
        });
        return checkBox;
    }

    private Table makeButtonWithLabel(CheckBox checkBox, int count, String type) {

        Label label = new Label(Integer.toString(count), skin);

        if(type== "Grass"){
            label = new Label("infite", skin);
        }
        label.setFontScale(0.8f);
        label.setColor(Color.BLACK);

        switch (type) {
            case "tree": treeLabel = label; break;
            case "pond": pondLabel = label; break;
            case "road": roadLabel = label; break;
            case "bush": bushLabel = label; break;
            case "Grass" : grassLabel = new Label("infite", skin); break;
        }

        Table table = new Table();
        table.add(checkBox).size(buttonSize, buttonSize).row();
        table.add(label).padTop(2f);
        return table;
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

    public CheckBox getGrassButton(){
        return grassButton;
    }


    public boolean HasTerrian(CheckBox checkBox){
        if(checkBox == treeButton ){
            return this.inventory.getCount(Tree.class) > 0;
        }else if(checkBox == pondButton){
            return this.inventory.getCount(Pond.class) > 0;
        }else if(checkBox == roadButton){
            return  this.inventory.getCount(Road.class) > 0;
        }else if(checkBox == bushButton){
            return  this.inventory.getCount(Bush.class) > 0;
        }else if(checkBox == grassButton){
            return true;
        }

        return  false;
    }


    public void updateInventoryCount(){
        this.TreeCount = inventory.getCount(Tree.class);
        this.PondCount = inventory.getCount(Pond.class);
        this.RoadCount = inventory.getCount(Road.class);
        this.BushCount = inventory.getCount(Bush.class);

        if (treeLabel != null) treeLabel.setText(Integer.toString(TreeCount));
        if (pondLabel != null) pondLabel.setText(Integer.toString(PondCount));
        if (roadLabel != null) roadLabel.setText(Integer.toString(RoadCount));
        if (bushLabel != null) bushLabel.setText(Integer.toString(BushCount));


    }

    public void resetSelection() {
        for (CheckBox checkBox : group.getButtons()) {
            checkBox.setChecked(false);
        }
    }
}
