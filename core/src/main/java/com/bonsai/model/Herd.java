package com.bonsai.model;

import java.util.ArrayList;
import java.util.List;

public class Herd {
    private Animal leader;
    List<Animal> members = new ArrayList<>();
    private List<Terrain> sharedPonds = new ArrayList<>();
    private List<Terrain> sharedFoodSources = new ArrayList<>();

    public Herd(Animal leader) {
        this.leader = leader;
        this.members.add(leader);
        leader.setHerd(this);
    }

    public void addMember(Animal animal) {
        if (!members.contains(animal)) {
            members.add(animal);
            animal.setHerd(this);
        }
    }

    public void removeMember(Animal animal) {
        members.remove(animal);
        animal.setHerd(null);

        if (animal == leader) {
            if (!members.isEmpty()) {
                leader = members.get(0); // 新しいリーダーを任命
            } else {
                leader = null;
            }
        }

        // リーダーがいなくなったら全員ソロ行動へ（群れを解散）
        if (leader == null) {
            for (Animal member : new ArrayList<>(members)) {
                member.setHerd(null);
            }
            members.clear();
        }
    }

    public Animal getLeader() {
        return leader;
    }

    public List<Terrain> getSharedPonds() {
        return sharedPonds;
    }

    public List<Terrain> getSharedFoodSources() {
        return sharedFoodSources;
    }

    public void addSharedPond(Terrain pond) {
        if (!sharedPonds.contains(pond)) {
            sharedPonds.add(pond);
        }
    }

    public void addSharedFoodSource(Terrain foodSource) {
        if (!sharedFoodSources.contains(foodSource)) {
            sharedFoodSources.add(foodSource);
        }
    }

    public void disband() {
        for (Animal member : members) {
            member.setHerd(null);  // 各メンバーの herd 参照をクリア
        }
        members.clear(); // 群れのメンバーリストもクリア
        leader = null;
    }
}
