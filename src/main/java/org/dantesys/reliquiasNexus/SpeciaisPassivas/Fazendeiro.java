package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.dantesys.reliquiasNexus.util.EntityToEgg;

import java.util.List;
import java.util.Random;

public class Fazendeiro {
    private static final List<EntityType> FARM_ANIMALS = List.of(
            EntityType.COW,
            EntityType.SHEEP,
            EntityType.PIG,
            EntityType.CHICKEN,
            EntityType.HORSE,
            EntityType.DONKEY,
            EntityType.MULE,
            EntityType.LLAMA,
            EntityType.CAMEL,
            EntityType.GOAT,
            EntityType.RABBIT,
            EntityType.BEE,
            EntityType.CAT,
            EntityType.WOLF
    );
    public static void getPassivabyLevel(int level, Player player){
        if(level<10){
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK,600,0));
        }else if(level<15){
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK,600,1));
        }else if(level<20){
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK,600,2));
        }else{
            player.addPotionEffect(new PotionEffect(PotionEffectType.LUCK,600,3));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,600,0));
        }
    }
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            greenHand(level,player);
        }else if(level<16){//8-15
            giftCreation(level,player);
        }else{//16-20
            essenceCreation(level,player);
        }
    }
    private static void greenHand(int level, Player player){
        World world = player.getWorld();
        Location loc = player.getLocation();
        for (int x = -level; x <= level; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -level; z <= level; z++) {
                    Block block = world.getBlockAt(loc.clone().add(x, y, z));
                    if (block.getBlockData() instanceof Ageable) {
                        block.applyBoneMeal(BlockFace.UP);
                        world.spawnParticle(Particle.HAPPY_VILLAGER, block.getLocation().add(0.5, 0.5, 0.5), 10, 0.3, 0.3, 0.3, 0.1);
                    }
                }
            }
        }
    }
    private static void giftCreation(int level, Player player){
        World world = player.getWorld();
        Location baseLoc = player.getLocation();
        Random random = new Random();
        int amount = random.nextInt(3,level);

        for (int i = 0; i < amount; i++) {
            EntityType type = FARM_ANIMALS.get(random.nextInt(FARM_ANIMALS.size()));
            Location spawnLoc = baseLoc.clone().add(
                    random.nextInt(7) - 3,
                    2,
                    random.nextInt(7) - 3
            );
            world.spawnEntity(spawnLoc, type);
        }
    }
    private static void essenceCreation(int level, Player player){
        for(Entity e: player.getNearbyEntities(level,level,level)){
            if(e instanceof LivingEntity vivo){
                if(FARM_ANIMALS.contains(vivo.getType())){
                    Material egg = EntityToEgg.getEntityEgg(vivo.getType());
                    if(egg!=null){
                        ItemStack stack = new ItemStack(egg);
                        e.getWorld().spawnParticle(Particle.HEART,e.getLocation(),5);
                        e.getWorld().dropItemNaturally(e.getLocation(),stack);
                    }
                }
            }
        }
    }
}
