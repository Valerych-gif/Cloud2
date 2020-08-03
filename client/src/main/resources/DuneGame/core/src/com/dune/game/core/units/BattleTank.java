package com.dune.game.core.units;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.units.types.TargetType;
import com.dune.game.core.units.types.UnitType;
import com.dune.game.screens.utils.Assets;
import com.dune.game.core.GameController;
import com.dune.game.core.interfaces.Targetable;
import com.dune.game.core.Weapon;
import com.dune.game.core.users_logic.BaseLogic;

public class BattleTank extends AbstractUnit {
    public BattleTank(GameController gc) {
        super(gc);
        this.textures = Assets.getInstance().getAtlas().findRegion("tankcore").split(64, 64)[0];
        this.weaponTexture = Assets.getInstance().getAtlas().findRegion("turret");
        this.minDstToActiveTarget = 240.0f;
        this.speed = 120.0f;
        this.hpMax = 100;
        this.weapon = new Weapon(1.5f, 1);
        this.containerCapacity = 50;
        this.unitType = UnitType.BATTLE_TANK;
    }

    @Override
    public void setup(BaseLogic baseLogic, float x, float y) {
        this.position.set(x, y);
        this.baseLogic = baseLogic;
        this.ownerType = baseLogic.getOwnerType();
        this.hp = this.hpMax;
        this.destination = new Vector2(position);
    }

    public void updateWeapon(float dt) {
        if (target != null) {
            if (!((AbstractUnit) target).isActive()) {
                target = null;
                return;
            }
            float angleTo = tmp.set(target.getPosition()).sub(position).angle();
            weapon.setAngle(rotateTo(weapon.getAngle(), angleTo, 180.0f, dt));
            int power = weapon.use(dt);
            if (power > -1) {
                gc.getProjectilesController().setup(this, position, weapon.getAngle());
            }
        }
        if (target == null) {
            weapon.setAngle(rotateTo(weapon.getAngle(), angle, 180.0f, dt));
        }
    }

    @Override
    public void commandAttack(Targetable target) {
        if (target.getType() == TargetType.UNIT && ((AbstractUnit) target).getOwnerType() != this.ownerType) {
            this.target = target;
        } else {
            commandMoveTo(target.getPosition());
        }
    }

    @Override
    public void renderGui(SpriteBatch batch) {
        super.renderGui(batch);
    }
}