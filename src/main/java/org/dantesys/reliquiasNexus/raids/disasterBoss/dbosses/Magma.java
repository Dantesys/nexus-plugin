package org.dantesys.reliquiasNexus.raids.disasterBoss.dbosses;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.dantesys.reliquiasNexus.items.ItemsRegistro.fragMagma;

public class Magma extends DBossBase{
    public Magma(LivingEntity boss) {
        super(boss,"Magmalord");
    }
    @Override
    public void spawnMinios() {
        for (int i = 0; i < 3; i++) {
            Location loc = boss.getLocation().add(Math.random() * 3 - 1.5, 0, Math.random() * 3 - 1.5);
            MagmaCube minion = boss.getWorld().spawn(loc, MagmaCube.class);
            minion.setCustomName("Minion");
        }
    }
    @Override
    protected void specialFull() {
        Location l = boss.getLocation();
        boss.getWorld().createExplosion(l, 4f, false, false);
        cdFull = 200;
    }
    @Override
    protected void specialHalf() {
        Location loc = boss.getLocation();
        loc.getWorld().strikeLightning(loc);
        cdHalf = 400;
    }
    @Override
    protected void specialLow() {
        for (int i = 0; i < 5; i++) {
            Location loc = boss.getLocation().add(Math.random() * 5 - 2.5, 0, Math.random() * 5 - 2.5);
            loc.getWorld().createExplosion(loc, 3f, false, false);
        }
        cdLow = 600;
    }
    @Override
    public List<ItemStack> getDrops() {
        Random rd = new Random();
        List<ItemStack> drops = new ArrayList<>();
        ItemStack frag = fragMagma.clone();
        frag.setAmount(Math.max(1,rd.nextInt(5)));
        drops.add(frag);
        return drops;
    }
}
