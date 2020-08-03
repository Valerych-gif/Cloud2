package com.dune.game.core;

import com.badlogic.gdx.math.Vector2;

public abstract class GameObject {
    protected GameController gc;
    protected Vector2 position;
    protected Vector2 tmp;

    public Vector2 getPosition() {
        return position;
    }

    public int getCellX() {
        return (int) (position.x / BattleMap.CELL_SIZE);
    }

    public int getCellY() {
        return (int) (position.y / BattleMap.CELL_SIZE);
    }

    public GameObject(GameController gc) {
        this.gc = gc;
        this.position = new Vector2();
        this.tmp = new Vector2();
    }
}