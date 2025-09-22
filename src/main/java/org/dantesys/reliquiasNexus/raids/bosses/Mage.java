package org.dantesys.reliquiasNexus.raids.bosses;

import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;

public class Mage extends BossBase{
    public Mage(LivingEntity boss, int level) {
        super(boss, level);
    }

    @Override
    protected void special() {
        boss.getWorld().spawn(boss.getLocation().add(0,1,0), Fireball.class, proj -> {
            proj.setYield(level);
            proj.setVelocity(boss.getLocation().getDirection().multiply(1.5));
            proj.setShooter(boss);
        });
    }
}
