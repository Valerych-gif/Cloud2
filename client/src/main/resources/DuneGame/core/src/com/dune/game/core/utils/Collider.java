package com.dune.game.core.utils;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dune.game.core.GameController;
import com.dune.game.core.Projectile;
import com.dune.game.core.units.AbstractUnit;
import com.dune.game.core.units.BattleTank;

import java.util.List;

public class Collider {
    private GameController gc;
    private Vector2 tmp;

    public Collider(GameController gc) {
        this.gc = gc;
        this.tmp = new Vector2();
    }

    public void checkCollisions() {
        List<AbstractUnit> units = gc.getUnitsController().getUnits();
        for (int i = 0; i < units.size() - 1; i++) {
            AbstractUnit u1 = units.get(i);
            for (int j = i + 1; j < units.size(); j++) {
                AbstractUnit u2 = units.get(j);
                float dst = u1.getPosition().dst(u2.getPosition());
                if (dst < 30 + 30) {
                    float colLengthD2 = (60 - dst) / 2;
                    tmp.set(u2.getPosition()).sub(u1.getPosition()).nor().scl(colLengthD2);
                    u2.moveBy(tmp);
                    tmp.scl(-1);
                    u1.moveBy(tmp);
                }
            }
        }
        for (int i = 0; i < gc.getProjectilesController().activeSize(); i++) {
            Projectile p = gc.getProjectilesController().getActiveList().get(i);
            for (int j = 0; j < gc.getUnitsController().getUnits().size(); j++) {
                AbstractUnit u = gc.getUnitsController().getUnits().get(j);
                if (p.getOwner().getBaseLogic() != u.getBaseLogic() && p.getPosition().dst(u.getPosition()) < 30) {
                    for (int k = 0; k < 25; k++) {
                        tmp.set(p.getVelocity()).nor().scl(120.0f).add(MathUtils.random(-40, 40), MathUtils.random(-40, 40));
                        gc.getParticleController().setup(
                                p.getPosition().x, p.getPosition().y, tmp.x, tmp.y, 0.4f, 1.0f, 0.2f,
                                1, 0, 0, 1, 1, 1, 0, 0.6f);
                    }
                    p.deactivate();
                    u.takeDamage(5);
                }
            }
        }
    }


}
