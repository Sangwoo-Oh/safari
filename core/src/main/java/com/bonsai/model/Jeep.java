package com.bonsai.model;

import com.badlogic.gdx.math.Vector3;

import java.util.*;

public class Jeep extends Entity {
    private JeepManager manager;
    private List<Vector3> path;
    private int currentTargetIndex = 0;
    private float speed = 10;
    private float waitTimer = 0f;
    private boolean isWaiting = false;
    private static final float WAIT_DURATION = 1.0f;
    private List<Tourist> passengers = new ArrayList<>();
    private boolean hasStarted = false;
    private GameModel gameModel;
    private Set<Class<? extends Animal>> encounteredAnimalTypes = new HashSet<>();

    public Jeep(JeepManager manager) {
        super(manager.getEntranceX()*10.0f, 0, manager.getEntranceY()*10.0f);
        this.manager = manager;
        this.gameModel = manager.getGameModel();
        this.path = dfsPath();
    }

    public void update(float delta, Time time) {
        if (time.getHoursToAdvance() == Time.Speed.HOUR) {
            this.speed = 10 * Time.Speed.HOUR.getHours();
        } else if (time.getHoursToAdvance() == Time.Speed.DAY) {
            this.speed = 10 * Time.Speed.DAY.getHours();
        } else if (time.getHoursToAdvance() == Time.Speed.WEEK) {
            this.speed = 10 * Time.Speed.WEEK.getHours();
        }

        if (isWaiting) {
            waitTimer += delta;
            if (waitTimer >= WAIT_DURATION) {
                isWaiting = false;
            }
            return;
        }

        if (!hasStarted) {
            if (passengers.isEmpty()) {
                if (isAtStartPosition()) {
                    manager.reassignTouristsToJeep(this);
                }
                return;
            } else {
                hasStarted = true;
                move(delta);
            }
        } else {
            move(delta);
        }
    }

    public void refreshPath() {
        this.position.set(manager.getEntranceX()*10.0f, 0, manager.getEntranceY()*10.0f);
        this.path = dfsPath();
        this.currentTargetIndex = 0;
    }

    public void refreshPathWithDelay(float delay) {
        this.position.set(manager.getEntranceX()*10.0f, 0, manager.getEntranceY()*10.0f);
        this.isWaiting = true;
        this.waitTimer = -delay;
        this.path = dfsPath();
        this.currentTargetIndex = 0;
        this.hasStarted = false;
    }

    private List<Vector3> dfsPath() {
        char[][] graph = manager.getGraph();
        boolean[][] visited = new boolean[graph.length][graph[0].length];
        List<Vector3> path = new ArrayList<>();
        dfsUtil(manager.getEntranceX(), manager.getEntranceY(), visited, path, graph);
        return path;
    }

    private boolean dfsUtil(int x, int y, boolean[][] visited, List<Vector3> path, char[][] graph) {
        if (x < 0 || y < 0 || x >= graph.length || y >= graph[0].length) return false;
        if (visited[y][x] || graph[y][x] == '#') return false;
        visited[y][x] = true;
        path.add(new Vector3(x*10.0f, 0, y*10.0f));
        if (graph[y][x] == 'G') return true;

        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        List<int[]> shuffled = new ArrayList<>();
        for (int[] d : directions) shuffled.add(Arrays.copyOf(d, d.length));
        Collections.shuffle(shuffled);

        for (int[] dir : shuffled) {
            if (dfsUtil(x + dir[0], y + dir[1], visited, path, graph)) return true;
        }
        path.remove(path.size() - 1);
        return false;
    }

    private void move(float delta) {
        if (path == null || path.isEmpty() || currentTargetIndex >= path.size()) return;

        Vector3 target = path.get(currentTargetIndex);
        Vector3 direction = new Vector3(target).sub(position);
        float distanceToTarget = direction.len();

        float moveDistance = speed * delta;

        if (distanceToTarget <= moveDistance) {
            position.set(target);
            currentTargetIndex++;

            if (currentTargetIndex >= path.size()) { // when jeep reaches goal
                gameModel.incrementVisitorCount(passengers.size());
                gameModel.getCapital().addMoney(100 * passengers.size() + 100 * getEncounteredAnimalCount());
                resetEncounteredAnimalTypes();
                passengers.clear();
                this.position.set(manager.getEntranceX()*10.0f, 0, manager.getEntranceY()*10.0f);
                manager.reassignTouristsToJeep(this);
                refreshPathWithDelay(1.5f);
            }

            return;
        }

        direction.nor();
        position.mulAdd(direction, moveDistance);
    }
    public void setPassengers(List<Tourist> passengers) {
        this.passengers = passengers;
    }

    public List<Tourist> getPassengers() {
        return passengers;
    }

    private boolean isAtStartPosition() {
        float startX = manager.getEntranceX() * 10.0f;
        float startZ = manager.getEntranceY() * 10.0f;
        return position.epsilonEquals(new Vector3(startX, 0, startZ), 0.1f);
    }

    public void observeNearbyAnimals(List<Animal> animals) {
        for (Animal animal : animals) {
            if (this.getPosition().dst(animal.getPosition()) < 20f) {
                this.observeAnimal(animal);
            }
        }
    }

    public void observeAnimal(Animal animal) {
        encounteredAnimalTypes.add(animal.getClass());
    }

    public int getEncounteredAnimalCount() {
        return encounteredAnimalTypes.size();
    }

    public void resetEncounteredAnimalTypes() {
        encounteredAnimalTypes.clear();
    }
}
