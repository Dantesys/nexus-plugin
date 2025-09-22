package org.dantesys.reliquiasNexus.raids.boss.bosses;

import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

import static org.dantesys.reliquiasNexus.util.NexusKeys.NEXUS;

public abstract class BossBase {
    protected final LivingEntity boss;
    protected int level;
    private int cooldown;
    protected final Random random = new Random();

    protected BossBase(LivingEntity boss,int level) {
        boss.getPersistentDataContainer().set(NEXUS.key, PersistentDataType.STRING,"BOSS");
        this.boss = boss;
        this.level = level;
        cooldown=0;
    }
    public void useSpecial(){
        if(cooldown<=0){
            special();
            this.cooldown = 200 + random.nextInt(200);
        }else{
            update();
        }
    }
    protected abstract void special();
    public void update(){
        if(cooldown > 0) cooldown--;
    }
    public LivingEntity getBoss() {
        return boss;
    }
}
