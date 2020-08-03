package com.dune.game.core;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.interfaces.Poolable;
import com.dune.game.core.units.AbstractUnit;

public class Projectile extends GameObject implements Poolable {
    private AbstractUnit owner;
    private TextureRegion texture;
    private Vector2 velocity;
    private float speed;
    private float angle;
    private boolean active;

    public Vector2 getVelocity() {
        return velocity;
    }

    public AbstractUnit getOwner() {
        return owner;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public Projectile(GameController gc) {
        super(gc);
        this.velocity = new Vector2();
        this.speed = 640.0f;
    }

    public void setup(AbstractUnit owner, Vector2 startPosition, float angle, TextureRegion texture) {
        this.owner = owner;
        this.texture = texture;
        this.position.set(startPosition);
        this.angle = angle;
        this.velocity.set(speed * MathUtils.cosDeg(angle), speed * MathUtils.sinDeg(angle));
        this.active = true;
    }

    public void render(SpriteBatch batch) {
        // batch.draw(texture, position.x - 8, position.y - 8);
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        for (int i = 0; i < 4; i++) {
            gc.getParticleController().setup(position.x, position.y, MathUtils.random(-40, 40), MathUtils.random(-40, 40) , 0.3f, 0.8f, 0.6f,
                    1, 1, 0, 1, 1f, 0f, 0, 0.8f);
        }

        if (position.x < 0 || position.x > BattleMap.MAP_WIDTH_PX || position.y < 0 || position.y > BattleMap.MAP_HEIGHT_PX) {
            deactivate();
        }
    }
}
