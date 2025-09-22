package org.dantesys.reliquiasNexus.raids.bosses;


import org.bukkit.Location;
import org.bukkit.entity.*;

import java.util.List;

public class Ranged extends BossBase {
    public Ranged(LivingEntity boss, int level) {
        super(boss, level);
    }

    @Override
    protected void special() {
        // Encontra jogadores próximos
        List<Player> players = boss.getNearbyEntities(15, 10, 15).stream()
                .filter(e -> e instanceof org.bukkit.entity.Player)
                .map(e -> (org.bukkit.entity.Player) e)
                .toList();

        if(players.isEmpty()) return;

        // Escolhe um jogador aleatório para atacar
        org.bukkit.entity.Player target = players.get(random.nextInt(players.size()));

        // Calcula direção
        Location loc = boss.getLocation().add(0, 1.5, 0);
        org.bukkit.util.Vector direction = target.getLocation().add(0,1,0).toVector().subtract(loc.toVector()).normalize().multiply(1.5);

        // Spawn do projétil dependendo do level
        Projectile proj;
        if(level <= 2){
            proj = boss.getWorld().spawn(loc, Arrow.class);
        } else if(level <= 4){
            proj = boss.getWorld().spawn(loc, Trident.class);
        } else {
            proj = boss.getWorld().spawn(loc, ShulkerBullet.class);
        }

        proj.setVelocity(direction);
        proj.setShooter(boss);
    }
}
