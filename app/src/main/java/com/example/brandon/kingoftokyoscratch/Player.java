package com.example.brandon.kingoftokyoscratch;

public class Player {
    private int health;
    private int victoryPoint;
    private int energy;

    public Player() {
        health = 10;
        victoryPoint = 0;
        energy = 0;
    }

    public Player(int h, int vp, int e){
        health = h;
        victoryPoint = vp;
        energy = e;
    }

    public void takeDamage(int i){
        health -= i;
        if(health < 0){
            health = 0;
        }
    }

    public void updateHealth(int i) {
        health += i;
        if(health > 10){
            health = 10;
        }
    }

    public void updateVictoryPoint(int v) {
        victoryPoint += v;
    }

    public void updateEnergy(int e) {
        energy += e;
    }

    public int getHealth() {
        return health;
    }

    public int getVictoryPoint() {
        return victoryPoint;
    }

    public int getEnergy() {
        return energy;
    }
}