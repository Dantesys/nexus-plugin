package org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class DBossBase {
    protected LivingEntity boss;
    protected int cdFull;
    protected int cdHalf;
    protected int cdLow;
    public DBossBase(LivingEntity boss){
        this.boss=boss;
        cdFull=0;
        cdHalf=0;
        cdLow=0;
    }
    public DBossBase(LivingEntity boss,String nome){
        boss.setCustomName(nome);
        boss.setCustomNameVisible(true);
        this(boss);
        this.boss=boss;
        cdFull=0;
        cdHalf=0;
        cdLow=0;
    }
    public void useSpecial(){
        double max = boss.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        double atual = boss.getHealth();
        if(cdFull<=0){
            specialFull();
        }else cdFull--;
        double half = 0.6;
        if(cdHalf<=0 && atual/max<=half){
            specialHalf();
        }else cdHalf--;
        double low = 0.3;
        if(cdLow<=0 && atual/max<=low){
            specialLow();
        }else cdLow--;
    }
    public abstract void spawnMinios();
    protected abstract void specialFull();
    protected abstract void specialHalf();
    protected abstract void specialLow();
    public abstract List<ItemStack> getDrops();
    public LivingEntity getBoss() {
        return boss;
    }
}
