package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.interfaces.Poolable;
import com.dune.game.core.users_logic.BaseLogic;
import com.dune.game.screens.utils.Assets;

public class Building extends GameObject implements Poolable {
    public enum Type {
        STOCK
    }

    // * * *
    // * P *
    //   E
    private BaseLogic ownerLogic;
    private Type type;
    private TextureRegion texture;
    private Vector2 textureWorldPosition;
    private int hpMax;
    private int hp;
    private int cellX, cellY;

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public Type getType() {
        return type;
    }

    public BaseLogic getOwnerLogic() {
        return ownerLogic;
    }

    public Building(GameController gc) {
        super(gc);
        this.texture = Assets.getInstance().getAtlas().findRegion("grass");
        this.textureWorldPosition = new Vector2();
    }

    public void setup(BaseLogic ownerLogic, int cellX, int cellY) {
        this.ownerLogic = ownerLogic;
        this.position.set(cellX * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2, cellY * BattleMap.CELL_SIZE + BattleMap.CELL_SIZE / 2);
        this.cellX = cellX;
        this.cellY = cellY;
        this.hpMax = 1000;
        this.hp = this.hpMax;
        this.textureWorldPosition.set((cellX - 1) * BattleMap.CELL_SIZE, cellY * BattleMap.CELL_SIZE);
        this.type = Type.STOCK;

//        for (int i = cellX - 1; i <= cellX + 1; i++) {
//            for (int j = cellY; j <= cellY + 1; j++) {
//                gc.getMap().blockGroundCell(i, j);
//            }
//        }
        gc.getMap().setupBuildingEntrance(cellX, cellY - 1, this);
    }

    public void render(SpriteBatch batch) {
        batch.setColor(0.5f, 0.5f, 0.5f, 0.6f);
        batch.draw(texture, textureWorldPosition.x, textureWorldPosition.y, BattleMap.CELL_SIZE * 3, BattleMap.CELL_SIZE * 2);
        batch.setColor(0.5f, 0.2f, 0.2f, 0.8f);
        batch.draw(texture, textureWorldPosition.x + BattleMap.CELL_SIZE, textureWorldPosition.y - BattleMap.CELL_SIZE, BattleMap.CELL_SIZE, BattleMap.CELL_SIZE);
        batch.setColor(1, 1, 1, 1);
    }

    public void update(float dt) {
    }

    public void destroy() {
        for (int i = cellX - 1; i <= cellX + 1; i++) {
            for (int j = cellY; j <= cellY + 1; j++) {
                gc.getMap().unblockGroundCell(i, j);
            }
        }
        gc.getMap().setupBuildingEntrance(cellX, cellY - 1, null);
    }
}