package com.bonsai.model;

import com.badlogic.gdx.math.Vector3;
import com.bonsai.controller.GameController;
import java.util.List;


public class Poacher extends Human{

    private Animal targetAnimal;
    private GameController controller;
    private PoacherState state;
    //private int numberOfPoacher;
    private float huntTimer = 0f;
    private final float SCAN_DURATION = 5.0f;
    private float fightTimer = 0f;
    private final float FIGHT_DURATION = 4.0f;
    private float RangerRadius = 20f;
    private Ranger ranger;
    private boolean isArraivedToRanger = false;
    private boolean isChatch = false;


    public Poacher(float x, float y, float z, GameController controller, Animal targetAnimal) {
        super(x, y, z);
        this.controller = controller;
        this.state = PoacherState.MOVING;
        this.targetAnimal = targetAnimal;
        this.next_position = targetAnimal.getPosition();
        targetAnimal.setPoacher(this);
        this.speed = 12f;
    }


    @Override
    public void update(float delta, Time time){run(delta);}

    private void run(float delta){

        if( this.state != PoacherState.FIGHTING && isNearRanger() != null){
            System.out.println("near");
            this.targetAnimal.setPoacher(null);
            this.state = PoacherState.FIGHTING;
            this.targetAnimal.setPoacher(null);
            return;
        }

        if(state == PoacherState.MOVING){
            Vector3 tmp = targetAnimal.getPosition().cpy();
            this.next_position = new Vector3(tmp.x, tmp.y, tmp.z);
            move(delta);
        }else if(state == PoacherState.HUNTING){
            hunt(delta);
        } else if (state == PoacherState.BACKING) {
            this.speed = 10;
            back(delta);
        } else if(state == PoacherState.FIGHTING){
            fight(delta);
        }else if(state == PoacherState.IDLE){
            lose();
        }

    }



    private void move(float delta){

        Vector3 pos = getPosition();

        Vector3 direction = new Vector3(this.next_position).sub(pos);
        if (direction.len() != 0) {
            direction.nor();  // 単位ベクトルに変換
        }
        pos.mulAdd(direction, speed * delta);

        if (pos.dst(next_position) < 1f) {
            isChatch = true;
            state = PoacherState.HUNTING;
        }
    }

    private void hunt(float delta){
        huntTimer += delta;
        if (huntTimer >= SCAN_DURATION) {
            huntTimer = 0f;
            state = PoacherState.BACKING;

        }

    }

    private void back(float delta){
        next_position = new Vector3(-2,0,-2);

        Vector3 pos = getPosition();
        // 新しい Vector3 を作成してから引き算する
        Vector3 direction = new Vector3(this.next_position).sub(pos);
        if (direction.len() != 0) {
            direction.nor();  // 単位ベクトルに変換
        }
        pos.mulAdd(direction, speed * delta);
        this.position.set(pos);
        if (pos.dst(next_position) < 1f) {
            controller.getGameModel().addHumanToRemove(this);
            this.state = PoacherState.IDLE;
        }
    }

    private void fight(float delta){

        if(this.isArraivedToRanger){
            fighting(delta);
        }else{
            moveToFight(delta);
        }



    }

    private void moveToFight(float delta){

        Vector3 pos = getPosition();

        Vector3 direction = new Vector3(this.ranger.getPosition()).sub(pos);
        if (direction.len() != 0) {
            direction.nor();
        }

        pos.mulAdd(direction, speed * delta);

        if (pos.dst(ranger.getPosition()) < 10) {
            isArraivedToRanger = true;
            System.out.println("poacher arrived near ranger");
        }
    }

    private void fighting(float delta){
        fightTimer += delta;
        if (fightTimer >= FIGHT_DURATION) {
            fightTimer = 0f;
            this.setPosition(getX(), -100, getZ());
            this.state = PoacherState.IDLE;
            this.ranger.setScanMode();
        }
    }

    public enum PoacherState{
        MOVING,
        HUNTING,
        BACKING,
        FIGHTING,
        IDLE

    }

    public PoacherState getState(){
        return this.state;
    }

    public Ranger isNearRanger(){

        List<Ranger> rangerModelList = controller.getRangerFromModel();
        for(Ranger ranger : rangerModelList){
            if(ranger.getPosition().dst(this.position) < RangerRadius){
                ranger.setFightState();
                this.ranger = ranger;
                this.ranger.setPoacher(this);
                return ranger;
            }
        }
        return null;
    }

    public void setBackState(){

        this.state = PoacherState.BACKING;

    }

    public boolean getIsCathch(){
        return this.isChatch;
    }

    public void lose(){
        controller.getGameModel().addHumanToRemove(this);
    }


}
