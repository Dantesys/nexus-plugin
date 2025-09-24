package org.dantesys.reliquiasNexus.SpeciaisPassivas;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.Temporizador;

import java.util.*;

public class Alquimista {
    private static final List<PotionEffectType> EFEITOS_NEGATIVOS = Arrays.asList(
            PotionEffectType.SLOWNESS,PotionEffectType.MINING_FATIGUE,
            PotionEffectType.WEAKNESS,PotionEffectType.POISON,
            PotionEffectType.HUNGER,PotionEffectType.BLINDNESS,
            PotionEffectType.NAUSEA,PotionEffectType.WITHER,
            PotionEffectType.DARKNESS,PotionEffectType.LEVITATION,
            PotionEffectType.INSTANT_DAMAGE,PotionEffectType.UNLUCK
    );
    private static final Map<Material,Material> TRANSMUTACAO = new HashMap<>() {{
        put(Material.STONE,Material.COAL_ORE);
        put(Material.COAL_ORE,Material.COPPER_ORE);
        put(Material.COPPER_ORE,Material.LAPIS_ORE);
        put(Material.LAPIS_ORE,Material.IRON_ORE);
        put(Material.IRON_ORE,Material.REDSTONE_ORE);
        put(Material.REDSTONE_ORE,Material.GOLD_ORE);
        put(Material.GOLD_ORE,Material.EMERALD_ORE);
        put(Material.EMERALD_ORE,Material.DIAMOND_ORE);
        put(Material.DEEPSLATE,Material.DEEPSLATE_COAL_ORE);
        put(Material.DEEPSLATE_COAL_ORE,Material.DEEPSLATE_COPPER_ORE);
        put(Material.DEEPSLATE_COPPER_ORE,Material.DEEPSLATE_LAPIS_ORE);
        put(Material.DEEPSLATE_LAPIS_ORE,Material.DEEPSLATE_IRON_ORE);
        put(Material.DEEPSLATE_IRON_ORE,Material.DEEPSLATE_REDSTONE_ORE);
        put(Material.DEEPSLATE_REDSTONE_ORE,Material.DEEPSLATE_GOLD_ORE);
        put(Material.DEEPSLATE_GOLD_ORE,Material.DEEPSLATE_EMERALD_ORE);
        put(Material.DEEPSLATE_EMERALD_ORE,Material.DEEPSLATE_DIAMOND_ORE);
        put(Material.NETHERRACK,Material.NETHER_GOLD_ORE);
        put(Material.NETHER_GOLD_ORE,Material.NETHER_QUARTZ_ORE);
        put(Material.NETHER_QUARTZ_ORE,Material.ANCIENT_DEBRIS);
    }};
    public static void getSpecialbyLevel(int level, Player player){
        if(level<8){//1-7
            explosaoDeElixires(level,player);
        }else if(level<16){//8-15
            bolasDeElixir(level,player);
        }else{//16-20
            transmutacao(level,player);
        }
    }
    private static final Map<EntityType, EntityType> MOBS_TRANSFORMACAO = Map.ofEntries(
            Map.entry(EntityType.ZOMBIE, EntityType.PIG),
            Map.entry(EntityType.SKELETON, EntityType.COW),
            Map.entry(EntityType.CREEPER, EntityType.SHEEP),
            Map.entry(EntityType.SPIDER, EntityType.CHICKEN),
            Map.entry(EntityType.ENDERMAN, EntityType.RABBIT),
            Map.entry(EntityType.WITCH, EntityType.FROG),
            Map.entry(EntityType.PILLAGER, EntityType.VILLAGER),
            Map.entry(EntityType.VINDICATOR, EntityType.VILLAGER),
            Map.entry(EntityType.EVOKER, EntityType.VILLAGER),
            Map.entry(EntityType.RAVAGER, EntityType.IRON_GOLEM),
            Map.entry(EntityType.HUSK, EntityType.PIG),
            Map.entry(EntityType.STRAY, EntityType.SHEEP),
            Map.entry(EntityType.WITHER_SKELETON, EntityType.WANDERING_TRADER),
            Map.entry(EntityType.DROWNED, EntityType.TROPICAL_FISH),
            Map.entry(EntityType.GHAST, EntityType.HAPPY_GHAST),
            Map.entry(EntityType.MAGMA_CUBE, EntityType.SLIME),
            Map.entry(EntityType.BLAZE, EntityType.SNOW_GOLEM),
            Map.entry(EntityType.PHANTOM, EntityType.CHICKEN),
            Map.entry(EntityType.SHULKER, EntityType.CHEST_MINECART)
            // adicionar outros hostis conforme necessário
    );
    private static void explosaoDeElixires(int level,Player player) {
        World world = player.getWorld();
        Location centro = player.getLocation();

        // Definir raio da área, aumenta com o level
        double raio = 5 + level;
        Random random = new Random();
        // Listar entidades próximas
        for (Entity entity : world.getNearbyEntities(centro, raio, raio, raio)) {
            if (entity instanceof LivingEntity living && living!=player) {
                int qtdEfeitos = 1+random.nextInt(3);
                for (int i = 0; i < qtdEfeitos; i++) {
                    PotionEffectType efeito = EFEITOS_NEGATIVOS.get(random.nextInt(EFEITOS_NEGATIVOS.size()));
                    int duracao = 60 + random.nextInt(40) + level*10; // duração escalável
                    int amplificador = random.nextInt(level + 1); // amplificador escalável
                    living.addPotionEffect(new PotionEffect(efeito, duracao, amplificador));
                }
            }
        }

        // Partículas para dar feedback visual
        world.spawnParticle(Particle.WITCH, centro.add(0,1,0), 50, raio, 1, raio, 0.1);
        world.playSound(centro, Sound.ENTITY_WITCH_THROW, 1f, 1f);
    }
    private static void bolasDeElixir(int level,Player player) {
        World world = player.getWorld();
        Location origem = player.getEyeLocation();
        Vector direcao = origem.getDirection().normalize();

        int qtdEsferas = 3 + level; // número de bolas aumenta com o level
        double velocidade = 0.6 + 0.1 * level; // velocidade das bolas

        for (int i = 0; i < qtdEsferas; i++) {
            // Cada esfera é executada em um temporizador para se mover
            new Temporizador(ReliquiasNexus.getPlugin(ReliquiasNexus.class), 20,
                    () -> {},
                    () -> {},
                    t -> {
                        Location loc = origem.clone().add(direcao.clone().multiply(t.getSegundosRestantes()*velocidade));
                        // partículas da esfera
                        world.spawnParticle(Particle.WITCH, loc, 2, 0.2, 0.2, 0.2, 0);

                        // checar colisão com entidades
                        for (Entity entity : world.getNearbyEntities(loc, 1,1,1)) {
                            if (entity instanceof LivingEntity living && !living.equals(player)) {
                                Random random = new Random();
                                int qtdEfeitos = 1 + random.nextInt(3);
                                for (int j = 0; j < qtdEfeitos; j++) {
                                    PotionEffectType efeito = EFEITOS_NEGATIVOS.get(random.nextInt(EFEITOS_NEGATIVOS.size()));
                                    living.addPotionEffect(new PotionEffect(efeito, 60 + 20*level, level));
                                }
                                t.stop(); // para a esfera ao atingir alguém
                            }
                        }

                        // checar colisão com bloco
                        Block bloco = loc.getBlock();
                        if (!bloco.getType().isAir()) {
                            t.stop(); // explode na colisão
                        }

                    }).scheduleTimer(1L);
        }

        // Som de lançamento
        world.playSound(player.getLocation(), Sound.ENTITY_SPLASH_POTION_THROW, 1f, 1f);
    }
    private static void transmutacao(int level,Player player) {
        World world = player.getWorld();
        Location centro = player.getLocation();
        int raio = 5 + level; // aumenta com level

        // Transforma mobs agressivos
        for (Entity e : world.getNearbyEntities(centro, raio, raio, raio)) {
            if (e instanceof Monster && MOBS_TRANSFORMACAO.containsKey(e.getType())) {
                EntityType novo = MOBS_TRANSFORMACAO.get(e.getType());
                Location loc = e.getLocation();
                e.remove();
                world.spawnEntity(loc, novo);
            }
        }

        // Transforma blocos
        for (int x = -raio; x <= raio; x++) {
            for (int y = -raio; y <= raio; y++) {
                for (int z = -raio; z <= raio; z++) {
                    Block bloco = world.getBlockAt(centro.clone().add(x, y, z));
                    Material transformado = TRANSMUTACAO.get(bloco.getType());
                    if (transformado != null) {
                        bloco.setType(transformado);
                        // partículas mágicas
                        world.spawnParticle(Particle.ENCHANT, bloco.getLocation().add(0.5,0.5,0.5), 5, 0.3, 0.3, 0.3, 0);
                    }
                }
            }
        }

        world.playSound(centro, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1f, 1f);
    }
}
