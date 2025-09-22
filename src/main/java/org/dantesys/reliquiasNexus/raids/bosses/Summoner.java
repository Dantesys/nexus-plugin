package org.dantesys.reliquiasNexus.raids.bosses;

import org.bukkit.Location;
import org.bukkit.entity.*;

public class Summoner extends BossBase{
    public Summoner(LivingEntity boss, int level) {
        super(boss, level);
    }

    @Override
    protected void special() {
        // Invoca entre 1 e 3 minions por uso
        int qtd = 1 + random.nextInt(3);
        for(int i=0; i<qtd; i++){
            spawnMinion();
        }
    }

    private void spawnMinion(){
        Location loc = boss.getLocation().add(random.nextDouble()*3-1.5, 0, random.nextDouble()*3-1.5);
        LivingEntity minion;

        switch(level){
            case 2 -> minion = loc.getWorld().spawn(loc, Spider.class);
            case 3 -> minion = loc.getWorld().spawn(loc, CaveSpider.class);
            case 4 -> minion = loc.getWorld().spawn(loc, Zombie.class);
            case 5 -> minion = loc.getWorld().spawn(loc, Skeleton.class);
            default -> minion = loc.getWorld().spawn(loc, Silverfish.class);
        }

        minion.setCustomName("Minion do Summoner");
        minion.setCustomNameVisible(true);

        // Pode aumentar atributos com level
        minion.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(10 + level * 5);
        minion.setHealth(minion.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue());
    }

}
