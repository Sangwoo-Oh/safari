package com.bonsai.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.bonsai.model.Entity;

public class SelectionView {

    private int x, y;
    private ModelInstance linesInstance;

    public SelectionView(int x, int y){
        this.x = x;
        this.y = y;

        makeMesh();
    }

    public void makeMesh(){

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder mpb = modelBuilder.part(
            "lines",
            GL20.GL_LINES,
            VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked,
            new Material()
        );

        Gdx.gl.glLineWidth(3);  // 太さ3にする

        // x軸 (赤): -10～+10
        mpb.setColor(Color.RED);
        mpb.line(-10,0,0, 10,0,0);

        // y軸 (緑): -10～+10
        mpb.setColor(Color.GREEN);
        mpb.line(0,-10,0, 0,10,0);

        // z軸 (青): -10～+10
        mpb.setColor(Color.BLUE);
        mpb.line(0,0,-10, 0,0,10);

        Model axisModel = modelBuilder.end();
        linesInstance = new ModelInstance(axisModel);

        linesInstance.transform.setToTranslation(x * 10, 0, y * 10);


    }

    public ModelInstance getModelInstance(){
        return linesInstance;
    }

    public void render(ModelBatch modelBatch, Environment environment){

        modelBatch.render(linesInstance, environment);

    }
}
