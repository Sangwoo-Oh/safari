package com.bonsai.model;

import com.badlogic.gdx.math.Vector3;
import com.bonsai.controller.GameController;

import java.util.List;

public class Ranger extends Human{


    private boolean scanMode;
    private RangerState rangerState;
    private float scanTimer = 0f;
    private final float SCAN_DURATION = 3.0f;
    private float fightTimer = 0f;
    private final float FIGHT_DURATION = 4.0f;
    private Vector3 targetPosition;
    private Animal targetAnimalToRemove;
    private boolean isOnRemoveMission = false;
    private GameController controller;
    private Poacher poacher = null;
    private boolean isArraivedToPoacher = false;
    private float RangerRadius = 20f;

    public Ranger(float x, float y, float z, GameController controller) {
        super(x, y, z);
        this.rangerState = RangerState.MOVING;
        this.controller = controller;
    }

    @Override
    public void update(float delta, Time time) {
        run(delta);
    }

    private void run(float delta) {

        Poacher poacher1 = isNearPoacher();
        if(poacher1 != null){
            this.rangerState = RangerState.FIGHTING;
            this.poacher = poacher1;
            fight(delta);
        }


        if (rangerState == RangerState.MISSION) {
            Vector3 tmp = targetAnimalToRemove.getPosition().cpy();

            this.next_position = new Vector3(tmp.x, tmp.y, tmp.z);
            moveTowardsTarget(delta);

        }else if (rangerState == RangerState.MOVING) {
            moving(delta);
        } else if (rangerState == RangerState.SCANNING) {
            scanning(delta);
        }else if( rangerState == RangerState.FIGHTING){
            fight(delta);
        }

    }

    private void moving(float delta){
        Vector3 pos = getPosition();
        // 新しい Vector3 を作成してから引き算する
        Vector3 direction = new Vector3(this.next_position).sub(pos);
        if (direction.len() != 0) {
            direction.nor();  // 単位ベクトルに変換
        }
        pos.mulAdd(direction, speed * delta);

        if (pos.dst(next_position) < 1f) {
            rangerState = RangerState.SCANNING;
            setNextPosition();
        }
    }
    private void scanning(float delta) {
        scanTimer += delta;

        if (scanTimer >= SCAN_DURATION) {
            scanTimer = 0f;
            setNextPosition(); // 次の目的地を決める
            rangerState = RangerState.MOVING;
        }
    }

    private void fight(float delta){

        if(!this.isArraivedToPoacher){
            moveToPoacher(delta);
        }else{
            fighting(delta);
        }
    }

    private void fighting(float delta){
        fightTimer += delta;
        if (fightTimer >= FIGHT_DURATION) {
            fightTimer = 0f;
            this.poacher = null;
            this.rangerState = RangerState.SCANNING;
        }
    }

    private void moveToPoacher(float delta){
        Vector3 pos = getPosition();

        Vector3 direction = new Vector3(this.poacher.getPosition()).sub(pos);
        if (direction.len() != 0) {
            direction.nor();
        }

        pos.mulAdd(direction, speed * delta);

        if (pos.dst(next_position) < 10) {
            isArraivedToPoacher = true;
            System.out.println("ranger arrived");
        }
    }

    private void moveTowardsTarget(float delta) {

        Vector3 pos = getPosition();
        // 新しい Vector3 を作成してから引き算する
        Vector3 direction = new Vector3(this.next_position).sub(pos);
        if (direction.len() != 0) {
            direction.nor();  // 単位ベクトルに変換
        }
        pos.mulAdd(direction, speed * delta);

        if (pos.dst(next_position) < 1f) {
            rangerState = RangerState.SCANNING;
            setNextPosition();
            controller.removeAnimal(targetAnimalToRemove);
            controller.getCapital().addMoney(1000);
            controller.setNeedUpdate(true);
            targetAnimalToRemove = null;
            this.speed = 10;
            System.out.println("I catch it , and i arrived");

        }


    }

    private void setNextPosition(){
        int x = r.nextInt( GameModel.MAP_SIZE);
        int y = r.nextInt( GameModel.MAP_SIZE);
        this.next_position = new Vector3(x*10.0f,0,y*10.0f);

    }


    public enum RangerState {
        MOVING,
        SCANNING,
        IDLE,
        MISSION,
        FIGHTING
    }

    public void setMission(Animal animal){
        this.targetAnimalToRemove = animal;
        this.speed = 15f;
        this.next_position = new Vector3(animal.getX(), animal.getY(), animal.getZ());
        rangerState = RangerState.MISSION;
//        System.out.println("i got mission.  " +animal.getPosition().x + " : " + animal.getZ());
    }

    public RangerState getState(){
        return this.rangerState;
    }

    public void setFightState(){
        this.rangerState = RangerState.FIGHTING;
    }

    public void setPoacher(Poacher poacher){
        this.poacher = poacher;
    }

    public void setScanMode(){
        this.rangerState = RangerState.SCANNING;
    }

    public Poacher isNearPoacher(){

        List<Poacher> poacherModelList = controller.getPoacherFromModel();
        for(Poacher poacher : poacherModelList){
            if(poacher.getPosition().dst(this.position) < RangerRadius){
                return poacher;
            }
        }
        return null;
    }


}
