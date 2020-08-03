package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.screens.utils.Assets;

public class BattleMap {
    private class Cell {
        private Building buildingEntrance;
        private int cellX, cellY;
        private int resource;
        private float resourceRegenerationRate;
        private float resourceRegenerationTime;
        private boolean groundPassable;

        public Cell(int cellX, int cellY) {
            this.cellX = cellX;
            this.cellY = cellY;
            if (MathUtils.random() < 0.1f) {
                resource = MathUtils.random(1, 3);
            }
            resourceRegenerationRate = MathUtils.random(5.0f) - 4.5f;
            if (resourceRegenerationRate < 0.0f) {
                resourceRegenerationRate = 0.0f;
            } else {
                resourceRegenerationRate *= 20.0f;
                resourceRegenerationRate += 10.0f;
            }
            this.groundPassable = true;
        }

        private void update(float dt) {
            if (resourceRegenerationRate > 0.01f) {
                resourceRegenerationTime += dt;
                if (resourceRegenerationTime > resourceRegenerationRate) {
                    resourceRegenerationTime = 0.0f;
                    resource++;
                    if (resource > 5) {
                        resource = 5;
                    }
                }
            }
        }

        private void render(SpriteBatch batch) {
            if (resource > 0) {
                float scale = 0.5f + resource * 0.2f;
                batch.draw(resourceTexture, cellX * CELL_SIZE, cellY * CELL_SIZE, CELL_SIZE / 2, CELL_SIZE / 2, CELL_SIZE, CELL_SIZE, scale, scale, 0.0f);
            } else {
                if (resourceRegenerationRate > 0.01f) {
                    batch.draw(resourceTexture, cellX * CELL_SIZE, cellY * CELL_SIZE, CELL_SIZE / 2, CELL_SIZE / 2, CELL_SIZE, CELL_SIZE, 0.1f, 0.1f, 0.0f);
                }
            }
        }

        public void blockGroundPass() {
            groundPassable = false;
            resourceRegenerationRate = 0.0f;
            resource = 0;
        }

        public void unblockGroundPass() {
            groundPassable = true;
        }
    }

    public static final int COLUMNS_COUNT = 24;
    public static final int ROWS_COUNT = 16;
    public static final int CELL_SIZE = 60;
    public static final int MAP_WIDTH_PX = COLUMNS_COUNT * CELL_SIZE;
    public static final int MAP_HEIGHT_PX = ROWS_COUNT * CELL_SIZE;

    private TextureRegion grassTexture;
    private TextureRegion resourceTexture;
    private Cell[][] cells;

    public void blockGroundCell(int cellX, int cellY) {
        cells[cellX][cellY].blockGroundPass();
    }

    public void unblockGroundCell(int cellX, int cellY) {
        cells[cellX][cellY].unblockGroundPass();
    }

    public void setupBuildingEntrance(int cellX, int cellY, Building building) {
        cells[cellX][cellY].buildingEntrance = building;
    }

    public BattleMap() {
        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.resourceTexture = Assets.getInstance().getAtlas().findRegion("resource");
        this.cells = new Cell[COLUMNS_COUNT][ROWS_COUNT];
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    public boolean isCellGroundPassable(Vector2 position) {
        int cellX = (int) (position.x / BattleMap.CELL_SIZE);
        int cellY = (int) (position.y / BattleMap.CELL_SIZE);
        if (cellX < 0 || cellY < 0 || cellX >= COLUMNS_COUNT || cellY >= ROWS_COUNT) {
            return false;
        }
        return cells[cellX][cellY].groundPassable;
    }

    public int getResourceCount(Vector2 point) {
        int cx = (int) (point.x / CELL_SIZE);
        int cy = (int) (point.y / CELL_SIZE);
        return cells[cx][cy].resource;
    }

    public int harvestResource(Vector2 point, int power) {
        int value = 0;
        int cx = (int) (point.x / CELL_SIZE);
        int cy = (int) (point.y / CELL_SIZE);
        if (cells[cx][cy].resource >= power) {
            value = power;
            cells[cx][cy].resource -= power;
        } else {
            value = cells[cx][cy].resource;
            cells[cx][cy].resource = 0;
        }
        return value;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                batch.draw(grassTexture, i * CELL_SIZE, j * CELL_SIZE);
                cells[i][j].render(batch);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < COLUMNS_COUNT; i++) {
            for (int j = 0; j < ROWS_COUNT; j++) {
                cells[i][j].update(dt);
            }
        }
    }

    public Building getBuildingEntrance(int cellX, int cellY) {
        return cells[cellX][cellY].buildingEntrance;
    }
}
