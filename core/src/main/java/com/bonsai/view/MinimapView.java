package com.bonsai.view;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.bonsai.controller.GameController;
import com.bonsai.model.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

public class MinimapView {
    private GameController controller;
    private int mapSize;
    private ShapeRenderer shapeRenderer;
    private SpriteBatch spriteBatch;
    private Texture treeTexture;
    private Texture bushTexture;
    private Texture sheepTexture;
    private Texture lionTexture;
    private Texture horseTexture;
    private Texture cheetahTexture;
    private Texture entranceTexture;
    private Texture exitTexture;
    private Texture jeepTexture;
    private float xPosition = Gdx.graphics.getWidth() * 0.52f;
    private float yPosition = Gdx.graphics.getHeight() * 0.8f;
    private List<Animal> animals;
    private List<Jeep> jeeps;
    public MinimapView(GameController controller) {
        this.controller = controller;
        this.mapSize = controller.getMapSize();
        this.shapeRenderer = new ShapeRenderer();
        this.spriteBatch = new SpriteBatch();
        animals = new ArrayList<>();
        jeeps = new ArrayList<>();
        treeTexture = new Texture(Gdx.files.internal("tree.png"));
        bushTexture = new Texture(Gdx.files.internal("bush_test.png"));
        sheepTexture = new Texture(Gdx.files.internal("sheep.png"));
        horseTexture = new Texture(Gdx.files.internal("horse.png"));
        lionTexture = new Texture(Gdx.files.internal("Lion.png"));
        cheetahTexture = new Texture(Gdx.files.internal("cheetah.png"));
        entranceTexture = new Texture(Gdx.files.internal("entrance.png"));
        exitTexture = new Texture(Gdx.files.internal("exit.png"));
        jeepTexture = new Texture(Gdx.files.internal("jeep.png"));
    }

    public void render() {
        float minimapWidth = 100f;
        float minimapHeight = 100f;
        float tileSize = minimapWidth / mapSize;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Terrain[][] map = controller.getGameModel().getMap();
        animals = controller.getAnimalsFromModel();
        jeeps = controller.getJeepsFromModel();

        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Terrain terrain = map[i][j];
                float offsetX = xPosition + j * tileSize;
                float offsetY = yPosition + (mapSize - i - 1) * tileSize;

                drawTile(terrain);
                shapeRenderer.rect(offsetX, offsetY, tileSize, tileSize);
            }
        }


        shapeRenderer.end();

        spriteBatch.begin();
        for (int i = 0; i < mapSize; i++) {
            for (int j = 0; j < mapSize; j++) {
                Terrain terrain = map[i][j];
                float offsetX = xPosition + j * tileSize;
                float offsetY = yPosition + (mapSize - i - 1) * tileSize;

                drawEntranceOrExit(terrain, offsetX, offsetY, tileSize);
                drawPlant(terrain, offsetX, offsetY, tileSize);
            }
        }
        for(Animal al : animals){
            float offsetX = xPosition + (al.getX() / 10) * tileSize;
            float offsetY = yPosition + (mapSize - (al.getZ() /10) - 1) * tileSize;
            drawAnimal(al, offsetX, offsetY, tileSize);
        }

        for(Jeep jeep : jeeps){
            float offsetX = xPosition + (jeep.getX() / 10) * tileSize;
            float offsetY = yPosition + (mapSize - (jeep.getZ() / 10) - 1) * tileSize;
            drawJeep(jeep, offsetX, offsetY, tileSize);
        }
        spriteBatch.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
        spriteBatch.dispose();
        treeTexture.dispose();
        bushTexture.dispose();
        entranceTexture.dispose();
        exitTexture.dispose();
        jeepTexture.dispose();
    }

    private void drawTile(Terrain terrain) {
        if (terrain instanceof Tree || terrain instanceof Bush) {
            shapeRenderer.setColor(new Color(0.7f, 1.0f, 0.3f, 1.0f));
        } else if (terrain instanceof Grass) {
            shapeRenderer.setColor(new Color(0.7f, 1.0f, 0.3f, 1.0f));
        } else if (terrain instanceof Pond) {
            shapeRenderer.setColor(Color.SKY);
        } else if (terrain instanceof Road) {
            shapeRenderer.setColor(Color.GRAY);
        } else if (terrain instanceof Entrance || terrain instanceof Exit) {
            shapeRenderer.setColor(Color.GRAY);
        }
    }

    private void drawEntranceOrExit(Terrain terrain, float x, float y, float size){
        if (terrain instanceof Entrance) {
            spriteBatch.draw(entranceTexture, x, y, size, size);
        } else if(terrain instanceof Exit){
            spriteBatch.draw(exitTexture, x, y, size, size);
        } else {
            return;
        }
    }

    private void drawPlant(Terrain terrain, float x, float y, float size){
        if (terrain instanceof Tree) {
            spriteBatch.draw(treeTexture, x, y, size, size);
        } else if(terrain instanceof Bush){
            spriteBatch.draw(bushTexture, x, y, size, size);
        }
    }

    private void drawAnimal(Animal animal, float x, float y, float size){
        if(animal instanceof Sheep){
            spriteBatch.draw(sheepTexture, x , y, size, size);
        }else if(animal instanceof Lion){
            spriteBatch.draw(lionTexture, x , y, size, size);
        } else if(animal instanceof Horse){
            spriteBatch.draw(horseTexture, x , y, size, size);
        }else if(animal instanceof Cheetah){
            spriteBatch.draw(cheetahTexture, x , y, size, size);
        }
    }

    private void drawJeep(Jeep jeep, float x, float y, float size){
        spriteBatch.draw(jeepTexture, x , y, size, size);
    }
}



