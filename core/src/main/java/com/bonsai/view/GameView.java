package com.bonsai.view;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.bonsai.controller.GameController;
import com.bonsai.model.*;
import com.bonsai.model.Tree;

import java.util.Random;


public class GameView extends ScreenAdapter implements InputProcessor {
    private PerspectiveCamera camera;
    private ModelBatch modelBatch;    // 3D描画用
    private Environment environment;
    private MapView mapView;         // タイルや地面を管理するクラス
    private float lastTouchX, lastTouchY;
    private boolean isDragging = false;
    private GameController controller;
    private Texture entityTexture;
    private SideMenu sideMenu;
    private Table root;
    private Stage stage;
    private InputMultiplexer multiplexer;
    private SelectionView selectionView;
    private final float panSpeed = 0.8f;
    private int mapSize;
    private MinimapView minimap;
    private TileSelector tileSelector;
    private AnimalSelector animalSelector;
    public int tileX = -1;
    public int tileZ = -1;
    private ViewUtils viewUtils;
    private HUDView hudView;
    private CheckBox selectedChecBox;
    private ShopMenu shopMenu;
    DirectionalLight sunlight;
    private float cameraMinX, cameraMaxX;
    private float cameraMinZ, cameraMaxZ;
    private InventoryMenu inventoryMenu;
    private ShapeRenderer shapeRenderer;
    private AnimalInfoWindow animalInfoWindow;
    private ActionsMenu actionsMenu;

    private float lastCheckedDay = -1;
    private Random r;

    private BitmapFont winFont;
    private SpriteBatch winBatch;

    private Window winPopup;
    private Window lostPopup;

    public GameView(ModelBatch modelBatch, GameController controller) {
        this.modelBatch = modelBatch;
        this.controller = controller;
        this.mapSize = this.controller.getMapSize();
        this.shapeRenderer = new ShapeRenderer();
        this.r = new Random();
    }

    public void setEditMode(boolean isEditing) {
        tileSelector.setEnabled(isEditing);
    }

    @Override
    public void show() {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        camera = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());


        camera.position.set(controller.getMapSize() * 10f / 2.0f, 50f, controller.getMapSize() * 10f / 2.0f);
        camera.lookAt(controller.getMapSize() * 10f / 2.0f, 0f, 0f);
        camera.up.set(0f, 1f, 0f);
        camera.near = 0.1f;
        camera.far = 500f;
        camera.update();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
        sunlight = new DirectionalLight();
        sunlight.set(1f, 1f, 1f, -0.5f, -1f, -0.5f);

        environment.add(sunlight);

        this.viewUtils = new ViewUtils(camera, controller.getMapSize());


        // MapViewの初期化（3Dタイル作成など）
        mapView = new MapView(controller);

        minimap = new MinimapView(controller);

        hudView = new HUDView(controller);

        //side menu
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        multiplexer = new InputMultiplexer();
        stage = new Stage(new ScreenViewport());
        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        sideMenu = new SideMenu(skin, controller, this);
        shopMenu = sideMenu.getShopMenu();
        inventoryMenu = sideMenu.getInventoryMenu();
        float inventoryWidthRatio = 0.3f;
        root.add().expandY().width(Value.percentWidth(1 - inventoryWidthRatio, root));
        root.add(sideMenu)
            .width(Value.percentWidth(inventoryWidthRatio, root))
            .expandY()
            .fillY();


        animalInfoWindow = new AnimalInfoWindow(skin, controller, () -> {
            mapView.updateAnimal();
        });
        stage.addActor(animalInfoWindow);


        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(hudView.getStage());
        multiplexer.addProcessor(this);


        Gdx.input.setInputProcessor(multiplexer);
        tileSelector = new TileSelector(camera, environment, controller);
        animalSelector = new AnimalSelector(camera, environment, controller);

        float spacing = 10f;
        float mapWorldSize = mapSize * spacing;

        cameraMinX = 0f;
        cameraMaxX = mapWorldSize-6.0f;
        cameraMinZ = 0f;
        cameraMaxZ = mapWorldSize+27.0f;


        // Winning window
        winPopup = setPopupWindow(skin, "Victory", "You won the game!");
        stage.addActor(winPopup);
        // Losing window
        lostPopup = setPopupWindow(skin, "Lost", "You lost the game...");
        stage.addActor(lostPopup);
    }

    private Window setPopupWindow(Skin skin, String title, String label) {
        Window popup = new Window(title, skin);
        winFont = new BitmapFont();
        winFont.getData().setScale(3f);
        Label.LabelStyle largeLabelStyle = new Label.LabelStyle(winFont, Color.WHITE);
        Label message = new Label(label, largeLabelStyle);
        TextButton backToMenuButton = new TextButton("Back to Menu", skin);

        backToMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                MainMenuView mainMenu = new MainMenuView();
                ((Game) Gdx.app.getApplicationListener()).setScreen(mainMenu);
            }
        });

        popup.add(message).pad(10);
        popup.row();
        popup.add(backToMenuButton).pad(10);
        popup.pack();
        popup.setSize(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.3f);
        popup.setVisible(false);
        popup.setPosition(
            (Gdx.graphics.getWidth() - popup.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - popup.getHeight()) / 2f
        );
        return popup;
    }

    @Override
    public void render(float delta) {
        handleKeyboardInput(delta);

        Time.TimeOfDay timeOfDay = controller.getTime().getTimeOfDay();

        mapView.setWallTimeOfDay(timeOfDay);
        switch (timeOfDay) {
            case DAY:
                sunlight.set(1f, 1f, 1f, -0.5f, -1f, -0.5f);
                Gdx.gl.glClearColor(0.41f, 0.71f, 0.91f, 1f);
                break;
            case SUNSET:
                sunlight.set(1.0f, 0.5f, 0.2f, -0.5f, -1f, -0.5f);
                Gdx.gl.glClearColor(0.392f, 0.510f, 0.573f, 1f);
                break;
            case NIGHT:
                sunlight.set(0.0f, 0.0f, 0.0f, -0.5f, -1f, -0.5f);
                Gdx.gl.glClearColor(0f, 0.133f, 0.310f, 1f);
                break;
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        camera.update();
        modelBatch.begin(camera);
//        generateRandomPoacher(delta);
//        mapView.updateAnimal();
        mapView.render(modelBatch, environment, delta);

        viewUtils.renderDebugBoxes(camera, this.mapView.getAnimalView(), shapeRenderer);

        modelBatch.end();


        minimap.render();
        if(controller.getNeedUpdate()){
            controller.setNeedUpdate(false);
        }
        hudView.render(delta);

        tileSelector.updateHover();
        tileSelector.render();

        //side menu
        stage.act(delta);
        stage.draw();

        // Checking win or lost
        if (controller.isWon()) {
            if (!winPopup.isVisible()) {
                winPopup.setVisible(true);
            }
        } else if (controller.isLost()) {
            if (!lostPopup.isVisible()) {
                lostPopup.setVisible(true);
            }
        }
    }


    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        stage.getViewport().update(width, height, true);
        sideMenu.setSize(stage.getWidth(), stage.getHeight());
        hudView.resize(width, height);

        if (winPopup != null) {
            winPopup.setSize(width * 0.5f, height * 0.3f);
            winPopup.setPosition(
                (width - winPopup.getWidth()) / 2f,
                (height - winPopup.getHeight()) / 2f
            );
        }

        if (lostPopup != null) {
            lostPopup.setSize(width * 0.5f, height * 0.3f);
            lostPopup.setPosition(
                (width - lostPopup.getWidth()) / 2f,
                (height - lostPopup.getHeight()) / 2f
            );
        }
    }

    @Override
    public void dispose() {
        modelBatch.dispose();
        mapView.dispose();
        hudView.dispose();
        tileSelector.dispose();
        shapeRenderer.dispose();
        winFont.dispose();
    }

    private void handleKeyboardInput(float delta) {
        float newX = camera.position.x;
        float newZ = camera.position.z;

        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            newX -= panSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            newX += panSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
            newZ -= panSpeed;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) {
            newZ += panSpeed;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.P)){

            Animal animal = this.controller.getGameModel().getRandomAnimal();
//            controller.addRanger(0,0,controller);

                if(animal != null){

                    this.controller.addPoacher(0,0,controller,animal);
                    mapView.updateHuman();
                    System.out.println("poacher apper");

                }

        }

        // カメラ位置の制限
        camera.position.x = MathUtils.clamp(newX, cameraMinX, cameraMaxX);
        camera.position.z = MathUtils.clamp(newZ, cameraMinZ, cameraMaxZ);
    }

    //mouse controller
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        int[] tileXZ = viewUtils.getTileCoordinate(screenX, screenY);
        if (tileXZ[0] == -1 || tileXZ[1] == -1) return false;
        tileX = tileXZ[0];
        tileZ = tileXZ[1];
        if (tileX < 0 || tileX >= mapSize || tileZ < 0 || tileZ >= mapSize)
            return false;

        //System.out.println("Clicked tile: (" + tileX + " : " + tileZ + ")");
        //this.controller.updateTerrain(tileX, tileZ, new Tree(tileX, tileZ));
//            CheckBox selected = SideMenu.getShopMenu();

        ShopMenuControll();

        InventoryMenuControll();


        Animal selectedAnimal = animalSelector.selectClosestAnimal(camera, controller.getGameModel().getAnimals());
        if (selectedAnimal != null && mapView.shouldRenderAnimal(selectedAnimal)) {
            sideMenu.getActionsMenu().setSelectedAnimal(selectedAnimal);
        }

        mapView.updateMap(tileX, tileZ);
        return false;
    }

    private void InventoryMenuControll(){
        CheckBox selectedInventory = controller.getInventoryCheckBox();

        if(selectedInventory != null){

            Terrain terrain = controller.getTerrain(tileX, tileZ);

            if(terrain instanceof Entrance || terrain instanceof  Exit){
                return;
            }

            if (selectedInventory == inventoryMenu.getTreeButton() && this.sideMenu.getInventoryMenu().HasTerrian(selectedInventory)){
                if(! (controller.getTerrain(tileX, tileZ) instanceof Tree)){
                    controller.updateTerrain(tileX, tileZ, new Tree(tileX, tileZ));
                    addInventory(terrain);
                    controller.RemoveInventoryCount(Tree.class);
                    this.inventoryMenu.updateInventoryCount();
                }
            }else if(selectedInventory == inventoryMenu.getPondButton() && this.sideMenu.getInventoryMenu().HasTerrian(selectedInventory)){
                if(! (controller.getTerrain(tileX, tileZ) instanceof Pond)){
                    this.controller.updateTerrain(tileX, tileZ, new Pond(tileX, tileZ));
                    addInventory(terrain);
                    controller.RemoveInventoryCount(Pond.class);
                    this.inventoryMenu.updateInventoryCount();
                }
            }else if(selectedInventory == inventoryMenu.getRoadButton()  && this.sideMenu.getInventoryMenu().HasTerrian(selectedInventory)){
                if(! (controller.getTerrain(tileX, tileZ) instanceof Road)){
                    this.controller.updateTerrain(tileX, tileZ, new Road(tileX, tileZ));
                    addInventory(terrain);
                    controller.RemoveInventoryCount(Road.class);
                    this.inventoryMenu.updateInventoryCount();
                }
            }else if(selectedInventory == inventoryMenu.getBushButton()  && this.sideMenu.getInventoryMenu().HasTerrian(selectedInventory)){
                if(! (controller.getTerrain(tileX, tileZ) instanceof Bush)){
                    this.controller.updateTerrain(tileX, tileZ, new Bush(tileX, tileZ));
                    addInventory(terrain);
                    controller.RemoveInventoryCount(Bush.class);
                    this.inventoryMenu.updateInventoryCount();
                }
            }else if(selectedInventory == inventoryMenu.getGrassButton()){
                this.controller.updateTerrain(tileX, tileZ, new Grass(tileX, tileZ));
                addInventory(terrain);
                this.inventoryMenu.updateInventoryCount();
            }
        }
    }

    private void ShopMenuControll(){

        CheckBox selected = controller.getSelectedCheckBox();



        if(selected != null) {

            Terrain terrain = controller.getTerrain(tileX, tileZ);

            if(terrain instanceof Entrance || terrain instanceof  Exit){
                return;
            }

            if (selected == shopMenu.getTreeButton() && controller.getCapital().subtractMoney(shopMenu.TreePrice)) {
                if(! (terrain instanceof Tree)){
                    addInventory(terrain);
                    controller.updateTerrain(tileX, tileZ, new Tree(tileX, tileZ));
                }
            }else if(selected == shopMenu.getPondButton() && controller.getCapital().subtractMoney(shopMenu.PondPrice)){
                if(! (controller.getTerrain(tileX, tileZ) instanceof Pond)){
                    addInventory(terrain);
                    this.controller.updateTerrain(tileX, tileZ, new Pond(tileX, tileZ));
                }
            }else if(selected == shopMenu.getRoadButton()  && controller.getCapital().subtractMoney(shopMenu.RoadPrice)){
                if(! (controller.getTerrain(tileX, tileZ) instanceof Road)){
                    addInventory(terrain);
                    this.controller.updateTerrain(tileX, tileZ, new Road(tileX, tileZ));
                }
            }else if(selected == shopMenu.getBushButton()  && controller.getCapital().subtractMoney(shopMenu.BushPrice)){
                if(! (controller.getTerrain(tileX, tileZ) instanceof Bush)){
                    addInventory(terrain);
                    this.controller.updateTerrain(tileX, tileZ, new Bush(tileX, tileZ));
                }
            }else if(selected == shopMenu.getSheepButton()  && controller.getCapital().subtractMoney(shopMenu.CheetahPrice)){
                this.controller.addSheep(tileX * 10.0f, tileZ * 10.0f);
                mapView.updateAnimal();
            }else if(selected == shopMenu.getLionButton()  && controller.getCapital().subtractMoney(shopMenu.LionPrice)){
                this.controller.addLion((tileX * 10.0f), tileZ * 10.0f);
                mapView.updateAnimal();
            } else if(selected == shopMenu.getJeepButton()  && controller.getCapital().subtractMoney(shopMenu.JeepPrice)){
                this.controller.addJeep();
                mapView.updateJeep();
            } else if(selected == shopMenu.getRengerButton()){
                this.controller.addRanger(tileX * 10.0f, tileZ * 10.0f, this.controller);
                mapView.updateHuman();
            }else if(selected == shopMenu.getHorseButton() && controller.getCapital().subtractMoney(shopMenu.HoursePrice)){
                this.controller.addHourse(tileX * 10.0f, tileZ * 10.0f);
                mapView.updateAnimal();
            } else if (selected == shopMenu.getCheetahButton() && controller.getCapital().subtractMoney(shopMenu.CheetahPrice)) {
                this.controller.addCheetah(tileX * 10.0f, tileZ * 10.0f);
                mapView.updateAnimal();
            }
        }
    }

    public void addInventory(Terrain terrain){
        if(! (terrain instanceof Grass)){
            this.controller.getGameModel().getInventory().addEntity(terrain.getClass());
            this.inventoryMenu.updateInventoryCount();
        }
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            isDragging = false;
        }
        return true;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (amountY > 0) {
            camera.fieldOfView -= 1f;
        } else if (amountY < 0) {
            camera.fieldOfView += 1f;
        }
        camera.fieldOfView = MathUtils.clamp(camera.fieldOfView, 10f, 60f);
        camera.update();
        return true;
    }

    @Override
    public boolean keyDown(int i) {
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    public void setSelectedCheckBox(CheckBox checkBox) {
        this.selectedChecBox = checkBox;
    }

    public void generateRandomPoacher(float delta) {
        long currentDay = this.controller.getGameModel().getTime().getTotalElapsedGameHours() / 24;

        if (currentDay != lastCheckedDay) {
            lastCheckedDay = currentDay;
            Animal animal = this.controller.getGameModel().getRandomAnimal();
            if (r.nextFloat() < 0.02f && animal != null) {
                this.controller.addPoacher(0,0,this.controller, animal);
                mapView.updateHuman();
                System.out.println("poacher apper");
            }
        }
    }
}
