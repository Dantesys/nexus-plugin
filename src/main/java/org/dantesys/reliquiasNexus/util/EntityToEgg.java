package org.dantesys.reliquiasNexus.util;

import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.entity.boat.*;
import org.bukkit.entity.minecart.*;

public enum EntityToEgg {
    ALLAY(EntityType.ALLAY, Material.ALLAY_SPAWN_EGG),
    ARMADILLO(EntityType.ARMADILLO, Material.ARMADILLO_SPAWN_EGG),
    AXOLOTL(EntityType.AXOLOTL, Material.AXOLOTL_SPAWN_EGG),
    BAT(EntityType.BAT, Material.BAT_SPAWN_EGG),
    BEE(EntityType.BEE, Material.BEE_SPAWN_EGG),
    BLAZE(EntityType.BLAZE, Material.BLAZE_SPAWN_EGG),
    BOGGED(EntityType.BOGGED, Material.BOGGED_SPAWN_EGG),
    BREEZE(EntityType.BREEZE, Material.BREEZE_SPAWN_EGG),
    CAMEL(EntityType.CAMEL, Material.CAMEL_SPAWN_EGG),
    CAT(EntityType.CAT, Material.CAT_SPAWN_EGG),
    CAVE_SPIDER(EntityType.CAVE_SPIDER, Material.CAVE_SPIDER_SPAWN_EGG),
    CHICKEN(EntityType.CHICKEN, Material.CHICKEN_SPAWN_EGG),
    COD(EntityType.COD, Material.COD_SPAWN_EGG),
    COW(EntityType.COW, Material.COW_SPAWN_EGG),
    CREAKING(EntityType.CREAKING, Material.CREAKING_SPAWN_EGG),
    CREEPER(EntityType.CREEPER, Material.CREEPER_SPAWN_EGG),
    DOLPHIN(EntityType.DOLPHIN, Material.DOLPHIN_SPAWN_EGG),
    DONKEY(EntityType.DONKEY, Material.DONKEY_SPAWN_EGG),
    DROWNED(EntityType.DROWNED, Material.DROWNED_SPAWN_EGG),
    ELDER_GUARDIAN(EntityType.ELDER_GUARDIAN, Material.ELDER_GUARDIAN_SPAWN_EGG),
    ENDER_DRAGON(EntityType.ENDER_DRAGON, Material.ENDER_DRAGON_SPAWN_EGG),
    ENDERMAN(EntityType.ENDERMAN, Material.ENDERMAN_SPAWN_EGG),
    ENDERMITE(EntityType.ENDERMITE, Material.ENDERMITE_SPAWN_EGG),
    EVOKER(EntityType.EVOKER, Material.EVOKER_SPAWN_EGG),
    FOX(EntityType.FOX, Material.FOX_SPAWN_EGG),
    FROG(EntityType.FROG, Material.FROG_SPAWN_EGG),
    GHAST(EntityType.GHAST, Material.GHAST_SPAWN_EGG),
    GLOW_SQUID(EntityType.GLOW_SQUID, Material.GLOW_SQUID_SPAWN_EGG),
    GOAT(EntityType.GOAT, Material.GOAT_SPAWN_EGG),
    GUARDIAN(EntityType.GUARDIAN, Material.GUARDIAN_SPAWN_EGG),
    HAPPY_GHAST(EntityType.HAPPY_GHAST, Material.HAPPY_GHAST_SPAWN_EGG),
    HOGLIN(EntityType.HOGLIN, Material.HOGLIN_SPAWN_EGG),
    HORSE(EntityType.HORSE, Material.HORSE_SPAWN_EGG),
    HUSK(EntityType.HUSK, Material.HUSK_SPAWN_EGG),
    IRON_GOLEM(EntityType.IRON_GOLEM, Material.IRON_GOLEM_SPAWN_EGG),
    LLAMA(EntityType.LLAMA, Material.LLAMA_SPAWN_EGG),
    MAGMA_CUBE(EntityType.MAGMA_CUBE, Material.MAGMA_CUBE_SPAWN_EGG),
    MOOSHROOM(EntityType.MOOSHROOM, Material.MOOSHROOM_SPAWN_EGG),
    MULE(EntityType.MULE, Material.MULE_SPAWN_EGG),
    OCELOT(EntityType.OCELOT, Material.OCELOT_SPAWN_EGG),
    PANDA(EntityType.PANDA, Material.PANDA_SPAWN_EGG),
    PARROT(EntityType.PARROT, Material.PARROT_SPAWN_EGG),
    PHANTOM(EntityType.PHANTOM, Material.PHANTOM_SPAWN_EGG),
    PIG(EntityType.PIG, Material.PIG_SPAWN_EGG),
    PIGLIN(EntityType.PIGLIN, Material.PIGLIN_SPAWN_EGG),
    PIGLIN_BRUTE(EntityType.PIGLIN_BRUTE, Material.PIGLIN_BRUTE_SPAWN_EGG),
    PILLAGER(EntityType.PILLAGER, Material.PILLAGER_SPAWN_EGG),
    POLAR_BEAR(EntityType.POLAR_BEAR, Material.POLAR_BEAR_SPAWN_EGG),
    PUFFERFISH(EntityType.PUFFERFISH, Material.PUFFERFISH_SPAWN_EGG),
    RABBIT(EntityType.RABBIT, Material.RABBIT_SPAWN_EGG),
    RAVAGER(EntityType.RAVAGER, Material.RAVAGER_SPAWN_EGG),
    SALMON(EntityType.SALMON, Material.SALMON_SPAWN_EGG),
    SHEEP(EntityType.SHEEP, Material.SHEEP_SPAWN_EGG),
    SHULKER(EntityType.SHULKER, Material.SHULKER_SPAWN_EGG),
    SILVERFISH(EntityType.SILVERFISH, Material.SILVERFISH_SPAWN_EGG),
    SKELETON(EntityType.SKELETON, Material.SKELETON_SPAWN_EGG),
    SKELETON_HORSE(EntityType.SKELETON_HORSE, Material.SKELETON_HORSE_SPAWN_EGG),
    SLIME(EntityType.SLIME, Material.SLIME_SPAWN_EGG),
    SNIFFER(EntityType.SNIFFER, Material.SNIFFER_SPAWN_EGG),
    SNOW_GOLEM(EntityType.SNOW_GOLEM, Material.SNOW_GOLEM_SPAWN_EGG),
    SPIDER(EntityType.SPIDER, Material.SPIDER_SPAWN_EGG),
    SQUID(EntityType.SQUID, Material.SQUID_SPAWN_EGG),
    STRAY(EntityType.STRAY, Material.STRAY_SPAWN_EGG),
    STRIDER(EntityType.STRIDER, Material.STRIDER_SPAWN_EGG),
    TADPOLE(EntityType.TADPOLE, Material.TADPOLE_SPAWN_EGG),
    TROPICAL_FISH(EntityType.TADPOLE, Material.TADPOLE_SPAWN_EGG),
    TURTLE(EntityType.TURTLE, Material.TURTLE_SPAWN_EGG),
    VEX(EntityType.VEX, Material.VEX_SPAWN_EGG),
    VILLAGER(EntityType.VILLAGER, Material.VILLAGER_SPAWN_EGG),
    VINDICATOR(EntityType.VINDICATOR, Material.VINDICATOR_SPAWN_EGG),
    WANDERING_TRADER(EntityType.WANDERING_TRADER, Material.WANDERING_TRADER_SPAWN_EGG),
    WARDEN(EntityType.WARDEN, Material.WARDEN_SPAWN_EGG),
    WITCH(EntityType.WITCH, Material.WITCH_SPAWN_EGG),
    WITHER(EntityType.WITHER, Material.WITHER_SPAWN_EGG),
    WITHER_SKELETON(EntityType.WITHER_SKELETON, Material.WITHER_SKELETON_SPAWN_EGG),
    WOLF(EntityType.WOLF, Material.WOLF_SPAWN_EGG),
    ZOGLIN(EntityType.ZOGLIN, Material.ZOGLIN_SPAWN_EGG),
    ZOMBIE(EntityType.ZOMBIE, Material.ZOMBIE_SPAWN_EGG),
    ZOMBIE_HORSE(EntityType.ZOMBIE_HORSE, Material.ZOMBIE_HORSE_SPAWN_EGG),
    ZOMBIE_VILLAGER(EntityType.ZOMBIE_VILLAGER, Material.ZOMBIE_VILLAGER_SPAWN_EGG),
    ZOMBIFIED_PIGLIN(EntityType.ZOMBIFIED_PIGLIN, Material.ZOMBIFIED_PIGLIN_SPAWN_EGG);
    public final EntityType type;
    public final Material material;
    EntityToEgg(EntityType entityType, Material material) {
        this.type=entityType;
        this.material=material;
    }
    public static Material getEntityEgg(EntityType e){
        for(EntityToEgg et:values()){
            if(et.type.equals(e)){
                return et.material;
            }
        }
        return null;
    }
}
