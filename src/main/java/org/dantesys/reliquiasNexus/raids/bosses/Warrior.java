package org.dantesys.reliquiasNexus.raids.bosses;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Warrior extends BossBase{
    public Warrior(LivingEntity boss,int level) {
        super(boss,level);
    }

    @Override
    protected void special() {
        // Ataque em área
        boss.getNearbyEntities(5,5,5).forEach(entity -> {
            if(entity instanceof Player p){
                p.damage(10+level, boss);
                p.getWorld().spawnParticle(Particle.SWEEP_ATTACK,p.getLocation(),2);
            }
        });
    }
}
