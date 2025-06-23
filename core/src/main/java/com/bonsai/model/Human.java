package com.bonsai.model;

import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public abstract class Human extends Entity{

    protected Vector3 next_position;
    protected float speed;
    protected Random r;

    public Human(float x, float y, float z){
        super(x,y,z);
        this.r = new Random();
        this.speed = 10;
        setNextPosition();
    }



    public void update(float delta, Time time) {

        move(delta);

    }

    private void setNextPosition(){
        int x = r.nextInt(GameModel.MAP_SIZE);
        int y = r.nextInt(GameModel.MAP_SIZE);

        this.next_position = new Vector3(x*10.0f,0,y*10.0f);
        //System.out.println(x + " : " + y);
    }

    private void move(float delta) {
        Vector3 pos = getPosition();
        // 新しい Vector3 を作成してから引き算する
        Vector3 direction = new Vector3(next_position).sub(pos);
        if (direction.len() != 0) {
            direction.nor();  // 単位ベクトルに変換
        }
        pos.mulAdd(direction, speed * delta);

        if (pos.dst(next_position) < 1f) {
            setNextPosition();
        }
    }




}
