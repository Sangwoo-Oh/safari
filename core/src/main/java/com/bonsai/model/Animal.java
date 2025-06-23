package com.bonsai.model;


import com.badlogic.gdx.math.Vector3;
import com.bonsai.controller.GameController;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class Animal extends Entity {
    protected float age = 0;
    protected final long THIRST_VALUE = 10;
    protected final long LIFESPAN_VALUE = 200;
    protected long thirst = THIRST_VALUE;
    protected long lifespan = LIFESPAN_VALUE;
    protected boolean hungry = false;
    protected boolean thirsty = false;
    protected Vector3 next_position;
    protected Random r;
    protected float speed = 10;
    protected long bornTime;
    protected long hungerTime;
    protected long thirstTime;
    protected boolean dead = false;
    protected boolean markedForRemoval = false;
    protected Terrain terrain;
    protected GameModel gameModel;
    GameController gameController = GameController.getInstance(gameModel);
    protected List<Terrain> visitedPond = new ArrayList<>();
    protected Vector3 pondTargetPosition = null;
    protected float arrivalTime = 3;
    protected boolean isDrinking = false;
    protected boolean isResting = false;

    protected Poacher poacher = null;
    protected Herd herd;
    private Vector3 lastLeaderTarget = null;
    private boolean hasLocationChip = false;




    public Animal(float x, float y, float z, Time time) {
        super(x, y, z);
        r = new Random();
        setNextPosition();
        bornTime = time.getTotalElapsedGameHours() / 24;
        hungerTime = time.getTotalElapsedGameHours() / 24;
        thirstTime = time.getTotalElapsedGameHours() / 24;
        dead = false;

    }

    public void update(float delta, Time time, List<Animal> allAnimals) {
        visitPond();
        formHerd(allAnimals);
        this.overLifespan(time);
        this.removeChangedPonds();
        checkMergeWithNearbyHerd();


        if (dead) {
            this.markedForRemoval = true;
        }

        if (poacher != null && poacher.getIsCathch()) {
            followingPoacher(delta);
            return;
        }

        if (herd != null && herd.getLeader() != null) {
            Animal leader = herd.getLeader();
            if (leader.isDead() || leader.poacher != null && leader.poacher.getIsCathch()) {
                herd.disband();  // ← Herd クラスに群れ解散メソッドが必要（後述）
                herd = null;
            }
        }

        if (herd != null && herd.getLeader() != this) { // herd and not leader(member of herd)
            this.memberMove(delta);

        } else { // leader or alone (same move)
            this.getHungry(time);
            this.getThirsty(time);
            this.visitPond();
            leaderAloneMove(delta, time);
        }

    }

    private void setNextPosition() {
        int x = r.nextInt(GameModel.MAP_SIZE * 10);
        int y = r.nextInt(GameModel.MAP_SIZE * 10);

        this.next_position = new Vector3(x, 0, y);
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
            isResting = true;
            arrivalTime = 3;
        }
    }

    //    protected void animalMoveSet(float delta, Time time) {
//        this.removeChangedPonds();
//
//        // ランダム地点に到着して休憩する状態
//        if (isResting) {
//            arrivalTime -= delta;
//            if (arrivalTime <= 0) {
//                arrivalTime = 3;
//                isResting = false;
//                setNextPosition();  // 次のランダムな目的地を決める
//            }
//        } else {
//            this.move(delta);  // 通常の移動
//        }
//
//        this.overLifespan(time);
//        this.getHungry(time);
//        this.getThirsty(time);
//        this.visitPond();
//    }
    protected void memberMove(float delta) {
        Vector3 leaderTarget = herd.getLeader().next_position;

        if (leaderTarget != null) {
            float mapLimit = GameModel.MAP_SIZE * 10f;

            // === リーダーの目的地が変わったら再設定 ===
            if (lastLeaderTarget == null || !lastLeaderTarget.epsilonEquals(leaderTarget, 0.1f)) {
                List<Vector3> adjacentTiles = new ArrayList<>();
                int[] offsets = {-10, -7, -5, -2, 0, 2, 5, 7, 10};

                for (int dx : offsets) {
                    for (int dz : offsets) {
                        if (dx == 0 && dz == 0) continue;

                        float newX = leaderTarget.x + dx;
                        float newZ = leaderTarget.z + dz;

                        // マップ範囲内のみに限定
                        if (newX >= 0 && newX < mapLimit && newZ >= 0 && newZ < mapLimit) {
                            adjacentTiles.add(new Vector3(newX, 0, newZ));
                        }
                    }
                }

                // 候補がある場合のみ目的地を設定
                if (!adjacentTiles.isEmpty()) {
                    next_position = adjacentTiles.get(r.nextInt(adjacentTiles.size()));
                    lastLeaderTarget = new Vector3(leaderTarget); // 更新
                }
            }

            // === 移動処理 ===
            if (next_position != null && getPosition().dst(next_position) > 0.5f) {
                Vector3 direction = new Vector3(next_position).sub(getPosition()).nor();
                Vector3 newPos = getPosition().mulAdd(direction, speed * delta);

                // 新しい位置もマップ内に制限
                newPos.x = Math.max(0, Math.min(newPos.x, mapLimit - 1));
                newPos.z = Math.max(0, Math.min(newPos.z, mapLimit - 1));

                setPosition(newPos);
            }
        }
    }

    protected void leaderAloneMove(float delta, Time time) {
        if (isDrinking) {
            this.thirstTime = time.getTotalElapsedGameHours() / 24;
            this.arrivalTime -= delta;  // 減算してカウントダウン
            if (this.arrivalTime <= 0) {
                // 待機が終了したら次の目的地に向かう
                this.arrivalTime = 3;
                isDrinking = false;
                this.fillThirst(time);
            }
        } else if (this.thirsty) {
            this.moveToPond(delta, time);
            if (pondTargetPosition == null) {
                this.move(delta);
            }
        }else if (isResting) {
            arrivalTime -= delta;
            if (arrivalTime <= 0) {
                arrivalTime = 3;
                isResting = false;
                setNextPosition();
            }
        } else {
            this.move(delta);
        }
    }

//    protected void animalMoveSet(float delta, Time time) {
//        checkMergeWithNearbyHerd();
//        if (herd != null && herd.getLeader() != this) {
//            Vector3 leaderTarget = herd.getLeader().next_position;
//
//            if (leaderTarget != null) {
//                float mapLimit = GameModel.MAP_SIZE * 10f;
//
//                // === リーダーの目的地が変わったら再設定 ===
//                if (lastLeaderTarget == null || !lastLeaderTarget.epsilonEquals(leaderTarget, 0.1f)) {
//                    List<Vector3> adjacentTiles = new ArrayList<>();
//                    int[] offsets = {-10, -7, -5, -2, 0, 2, 5, 7, 10};
//
//                    for (int dx : offsets) {
//                        for (int dz : offsets) {
//                            if (dx == 0 && dz == 0) continue;
//
//                            float newX = leaderTarget.x + dx;
//                            float newZ = leaderTarget.z + dz;
//
//                            // マップ範囲内のみに限定
//                            if (newX >= 0 && newX < mapLimit && newZ >= 0 && newZ < mapLimit) {
//                                adjacentTiles.add(new Vector3(newX, 0, newZ));
//                            }
//                        }
//                    }
//
//                    // 候補がある場合のみ目的地を設定
//                    if (!adjacentTiles.isEmpty()) {
//                        next_position = adjacentTiles.get(r.nextInt(adjacentTiles.size()));
//                        lastLeaderTarget = new Vector3(leaderTarget); // 更新
//                    }
//                }
//
//                // === 移動処理 ===
//                if (next_position != null && getPosition().dst(next_position) > 0.5f) {
//                    Vector3 direction = new Vector3(next_position).sub(getPosition()).nor();
//                    Vector3 newPos = getPosition().mulAdd(direction, speed * delta);
//
//                    // 新しい位置もマップ内に制限
//                    newPos.x = Math.max(0, Math.min(newPos.x, mapLimit - 1));
//                    newPos.z = Math.max(0, Math.min(newPos.z, mapLimit - 1));
//
//                    setPosition(newPos);
//                }
//            }
//
//        } else {
//            // === リーダーまたは群れに属していない動物 ===
//            this.removeChangedPonds();
//
//            if (isResting) {
//                arrivalTime -= delta;
//                if (arrivalTime <= 0) {
//                    arrivalTime = 3;
//                    isResting = false;
//                    setNextPosition();
//                }
//            } else {
//                this.move(delta);
//            }
//
//            this.overLifespan(time);
//            this.getHungry(time);
//            this.getThirsty(time);
//            this.visitPond();
//        }
//    }


    //    今の時間-生成されたときの時間 (生成されてからの時間) がlifespanを超えたら死ぬ
    public void overLifespan(Time time) {
        this.age = (int) (time.getTotalElapsedGameHours() / 24 - this.bornTime);
//        System.out.println("age is :" + age);
        if (age >= this.lifespan) {
            System.out.println("Animal has exceeded its lifespan.");
            this.die();
        }
    }


    public void getHungry(Time time) {
    }

    public void getThirsty(Time time) {
        if (age / lifespan <= 0.3) {
            gettingThirsty(time, 1.5f);
        } else if (0.3 < age / lifespan && age / lifespan <= 0.6) {
            gettingThirsty(time, 2f);
        } else if (age / lifespan > 0.6) {
            gettingThirsty(time, 3.5f);
        }
    }

    private void gettingThirsty(Time time, float x) {
        float elapsedTime = time.getTotalElapsedGameHours() / 24f - this.thirstTime;
        if (elapsedTime >= this.thirst) {
            this.die();
            System.out.println("too thirsty");
        } else if (elapsedTime >= (float) this.thirst / x) {
            this.thirsty = true;
        }
    }

//    protected void visitPond() {
//        Terrain currentTerrain = gameController.getCurrentTerrain(this);
//        if ((currentTerrain instanceof Pond) && !visitedPond.contains(currentTerrain)) {
//            visitedPond.add(currentTerrain);

    /// /            System.out.println("added pond");
//        }
//    }
    protected void visitPond() {
        Terrain currentTerrain = gameController.getCurrentTerrain(this);
        if ((currentTerrain instanceof Pond) && !visitedPond.contains(currentTerrain)) {
            System.out.println("X: " + currentTerrain.getX() + ", Z: " + currentTerrain.getZ());

            visitedPond.add(currentTerrain);
            System.out.println("add to visited pond");
        }
    }

//    public void findPond() {
//        Terrain closestPond = findClosestPond();
//
//        if (closestPond != null) {
//            this.pondTargetPosition = new Vector3(closestPond.getX(), 0, closestPond.getZ()); // ★追加
//        }
//    }

//    public void findPond() {
//        Terrain closestPond = null;
//        float minDistance = Float.MAX_VALUE;
//        Vector3 currentPosition = getPosition();
//
//        // リーダーの visitedPond の中から探す
//        if (herd != null && herd.getLeader() != null) {
//            Animal leader = herd.getLeader();
//            for (Terrain pond : leader.visitedPond) {
//                float distance = currentPosition.dst(new Vector3(pond.getX()*10, 0, pond.getZ()*10));
//                if (distance < minDistance) {
//                    minDistance = distance;
//                    closestPond = pond;
//                }
//            }
//        }
//
//        if (closestPond != null) {
//            pondTargetPosition = new Vector3(closestPond.getX(), 0, closestPond.getZ());
//        } else {
//            // リーダーがいない or 訪問した池がない場合は何もしない
//            pondTargetPosition = null;
//        }
//    }

    private Terrain findClosestPond() {
        Terrain closestPond = null;
        float minDistance = Float.MAX_VALUE;
        Vector3 currentPosition = getPosition();

        // 自分の訪問履歴
        for (Terrain pond : visitedPond) {
            float distance = currentPosition.dst(new Vector3(pond.getZ() * 10, 0, pond.getX() * 10));
            if (distance < minDistance) {
                minDistance = distance;
                closestPond = pond;
            }
        }

        return closestPond;
    }

    private void moveToPond(float delta, Time time) {
        // 最も近い食物を含む地形（草や木）を見つける
        Terrain closestPond = findClosestPond();

        if (closestPond != null) {
            // 現在の位置を取得
            Vector3 pos = getPosition();
            // 最寄のターゲット地形の位置を取得
            this.pondTargetPosition = new Vector3(closestPond.getZ() * 10, 0, closestPond.getX() * 10);

            // 新しい次の位置を設定
            this.next_position = pondTargetPosition;

            // 目標地形に向かって移動する方向ベクトルを計算
            Vector3 direction = new Vector3(next_position).sub(pos);
            if (direction.len() != 0) {
                direction.nor();  // 単位ベクトルに変換
            }

            // 現在位置を更新
            pos.mulAdd(direction, speed * delta);

            // 新しい位置を設定
            setPosition(pos);

            // 目標地形に到達したかチェック
            if (pos.dst(next_position) < 1f) {
                this.isDrinking = true;
                fillThirst(time);
//                System.out.println(isDrinking);
            }
        } else {
            System.out.println("closest pond is null");
        }
    }

    private void fillThirst(Time time) {
        this.thirstTime = time.getTotalElapsedGameHours() / 24;
        this.thirsty = false;
        System.out.println("drunk water");
    }

    private void removeChangedPonds() {
        List<Terrain> toRemove = new ArrayList<>();
        for (Terrain pond : visitedPond) {
            Terrain currentTerrain = gameController.getTerrian((int) pond.getPosition().z, (int) pond.getPosition().x);
//            System.out.println("1111X: " + currentTerrain.getX() + ", Z: " + currentTerrain.getZ());

            if (!(currentTerrain instanceof Pond)) {
                toRemove.add(pond);
            }
        }
        visitedPond.removeAll(toRemove);
    }


    public Poacher getPoacher() {
        return this.poacher;
    }


    public void die() {

        if (this.poacher != null) {
            this.poacher.setBackState();
        }
        this.dead = true;
    }

    public boolean isDead(){
        return this.dead;
    }


    public boolean getMarkedForRemoval() {
        return this.markedForRemoval;
    }


    public void setPoacher(Poacher poacher) {
        this.poacher = poacher;
    }

    private void followingPoacher(float delta) {

        Vector3 target = poacher.getPositionCPY().cpy();
        target = new Vector3(target.x + 1, target.y, target.z + 1);
        this.next_position = target;


//        System.out.println(next_position.toString());

        Vector3 pos = getPosition();
        Vector3 direction = new Vector3(next_position).sub(pos);

        if (direction.len() != 0) {
            direction.nor();
        }

        pos.mulAdd(direction, speed * delta);


        if (poacher.getState() == Poacher.PoacherState.IDLE) {
            die();
        }
    }

    public void setHerd(Herd herd) {
        this.herd = herd;
    }

    public Herd getHerd() {
        return herd;
    }

    public void formHerd(List<Animal> allAnimals) {
        if (this.herd != null) return; // すでに群れに所属している場合は何もしない

        for (Animal other : allAnimals) {
            if (other == this) continue;
            if (!this.getClass().equals(other.getClass())) continue; // 同じ種類のみ
            if (other.getPosition().dst(this.getPosition()) < 30f) { // 一定距離以内
                if (other.getHerd() != null) {
                    other.getHerd().addMember(this);
                    return;
                }
            }
        }

        // 近くに群れがなければ、自分がリーダーになって新しい群れを作る
        Herd newHerd = new Herd(this);
    }


    protected Vector3 getNext_position() {
        return this.next_position;
    }

    private void checkMergeWithNearbyHerd() {
//        if (gameModel == null) return;
        // 自分がリーダーでない、または群れがないなら無視
        if (herd == null || herd.getLeader() != this) return;

        for (Animal other : gameModel.getAnimals()) {
            if (other == this) continue;
            if (other.herd == null || other.herd == this.herd) continue;
            if (other.herd.getLeader() != other) continue;
            if (!this.getClass().equals(other.getClass())) continue;

            float dist = this.getPosition().dst(other.getPosition());
            if (dist < 30f) {
                // どちらの群れが吸収するかをランダムで決める
                Herd mainHerd = new Random().nextBoolean() ? this.herd : other.herd;
                Herd subHerd = (mainHerd == this.herd) ? other.herd : this.herd;

                // サブ群れのメンバー全員をメイン群れに移動
                for (Animal member : new ArrayList<>(subHerd.members)) {
                    mainHerd.addMember(member);
                }

                // サブ群れを空にする（必要ならそのまま放置でも良い）
                subHerd.members.clear();

                // リーダーを再設定
                this.herd = mainHerd;
                other.herd = mainHerd;

                System.out.println("Merged herd " + subHerd + " into " + mainHerd);
                return; // 1つだけマージして終了
            }
        }
    }

    public void setGameModel(GameModel model) {
        this.gameModel = model;
    }

    public boolean hasLocationChip() {
        return hasLocationChip;
    }

    public void setLocationChip() {
        this.hasLocationChip = true;
    }
}

