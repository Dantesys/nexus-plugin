package org.dantesys.reliquiasNexus.missoes;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.generator.structure.StructureType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StructureSearchResult;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MissoesManager implements Listener {
    private static final Map<UUID, Missao> missaoAtiva = new ConcurrentHashMap<>();
    private final JavaPlugin plugin;
    public MissoesManager(JavaPlugin plugin){
        this.plugin=plugin;
    }
    public List<Missao> gerarMissoes(Player player){
        Random rd = new Random();
        int playerLevel = Math.max(player.getLevel(), 1);
        // Gerar dificuldades aleatórias para cada tipo de missão
        MissaoDificuldade difColeta = MissaoDificuldade.getByLevel(rd.nextInt(playerLevel));
        MissaoDificuldade difMineracao = MissaoDificuldade.getByLevel(rd.nextInt(playerLevel));
        MissaoDificuldade difLenhador = MissaoDificuldade.getByLevel(rd.nextInt(playerLevel));
        MissaoDificuldade difCaca = MissaoDificuldade.getByLevel(rd.nextInt(playerLevel));
        MissaoDificuldade difBiome = MissaoDificuldade.getByLevel(rd.nextInt(playerLevel));
        // Escolher materiais e entidades aleatórios dentro da dificuldade
        Material coleta = ColetaDif.getByDif(rd.nextInt(difColeta.dificuldade) + 1);
        Material mineracao = MineracaoDif.getByDif(rd.nextInt(difMineracao.dificuldade) + 1);
        Material lenhador = LenhadorDif.getByDif(rd.nextInt(difLenhador.dificuldade) + 1);
        EntityType caca = CacaDif.getByDif(rd.nextInt(difCaca.dificuldade) + 1);
        Biome biome = ExploracaoBiomeDif.getByDif(rd.nextInt(difBiome.dificuldade) + 1);

        // Criar missões com tempo e quantidade proporcionais à dificuldade
        Missao missaoColeta = new Missao(difColeta, 720 * difColeta.dificuldade, rd.nextInt(10) + 1, plugin, coleta, false, false);
        Missao missaoMineracao = new Missao(difMineracao, 720 * difMineracao.dificuldade, rd.nextInt(10) + 1, plugin, mineracao, true, false);
        Missao missaoLenhador = new Missao(difLenhador, 720 * difLenhador.dificuldade, rd.nextInt(10) + 1, plugin, lenhador, false, true);
        Missao missaoCaca = new Missao(difCaca, 720 * difCaca.dificuldade, rd.nextInt(10) + 1, plugin, caca);
        Missao missaoBiome = new Missao(difBiome, 720 * difBiome.dificuldade * 2, plugin, biome);

        return List.of(missaoColeta, missaoMineracao, missaoLenhador, missaoCaca, missaoBiome);
    }
    public void aceitarMissao(UUID uuid,Missao missao){
        missaoAtiva.put(uuid,missao);
    }
    public void aceitarMissao(Player player,Missao missao){
        missao.iniciar(player);
        aceitarMissao(player.getUniqueId(),missao);
    }
    public void reiniciarMissao(Player player){
        if(missaoAtiva.containsKey(player.getUniqueId()))missaoAtiva.get(player.getUniqueId()).reiniciar(player);
    }
    public void pausarMissao(Player player){
        if(missaoAtiva.containsKey(player.getUniqueId()))missaoAtiva.get(player.getUniqueId()).pausar();
    }
    public void cancelarMissao(Player player){
        if(missaoAtiva.containsKey(player.getUniqueId()))missaoAtiva.remove(player.getUniqueId()).cancelar();
    }
    public void save(YamlConfiguration missaoAtivaBK){
        List<String> uuids = new ArrayList<>();
        missaoAtiva.forEach((uuid,missao) ->{
            uuids.add(uuid.toString());
            missao.pausar();
            missaoAtivaBK.set("missoes."+ uuid,missao);
        });
        missaoAtivaBK.set("players",uuids);
        File ms = new File(plugin.getDataFolder(), "missaoAtiva.yml");
        try {
            missaoAtivaBK.save(ms);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @EventHandler
    public void pickUp(EntityPickupItemEvent event){
        ItemStack item = event.getItem().getItemStack();
        if(event.getEntity() instanceof Player player && !event.getItem().hasMetadata("DROPADO")){
            if(missaoAtiva.containsKey(player.getUniqueId())){
                Missao m = missaoAtiva.get(player.getUniqueId());
                if(m.getTipo().equals(MissaoTipo.COLETA.nome)){
                    Material mat = (Material) m.get("material");
                    if(mat.equals(item.getType())){
                        m.atualizaCondicao(item.getAmount());
                        missaoAtiva.replace(player.getUniqueId(),m);
                    }
                }
            }
        }
    }
    @EventHandler
    public void breakBlock(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(missaoAtiva.containsKey(player.getUniqueId())){
            Missao m = missaoAtiva.get(player.getUniqueId());
            String tipo = m.getTipo();
            if(tipo.equals(MissaoTipo.MINERACAO.nome) || tipo.equals(MissaoTipo.LENHADOR.nome)){
                Material mat = (Material) m.get("material");
                Block bloco = event.getBlock();
                if(bloco.getType().equals(mat)){
                    m.atualizaCondicao();
                    missaoAtiva.replace(player.getUniqueId(),m);
                }
            }
        }
    }
    @EventHandler
    public void derrotou(EntityDeathEvent event){
        if (!(event.getEntity().getKiller() instanceof Player player)) return;
        if(missaoAtiva.containsKey(player.getUniqueId())){
            Missao m = missaoAtiva.get(player.getUniqueId());
            String tipo = m.getTipo();
            if(tipo.equals(MissaoTipo.CACA.nome)){
                EntityType mat = (EntityType) m.get("entity");
                EntityType entity = event.getEntity().getType();
                if(entity.equals(mat)){
                    m.atualizaCondicao();
                    missaoAtiva.replace(player.getUniqueId(),m);
                }
            }
        }
    }
    @EventHandler
    public void achou(ServerTickEndEvent event){
        if(event.getTickNumber()%20==0){
            Bukkit.getServer().getOnlinePlayers().forEach(player -> {
                if(missaoAtiva.containsKey(player.getUniqueId())){
                    Missao m = missaoAtiva.get(player.getUniqueId());
                    String tipo = m.getTipo();
                    if(tipo.equals(MissaoTipo.EXPLORACAO.nome)){
                        StructureType mat = (StructureType) m.get("structure");
                        Location loc = player.getLocation();
                        if(mat==null){
                            Biome bio = (Biome) m.get("biome");
                            if(player.getWorld().getBiome(loc).equals(bio)){
                                m.atualizaCondicao();
                                missaoAtiva.replace(player.getUniqueId(),m);
                            }
                        }else{
                            StructureSearchResult result = player.getWorld().locateNearestStructure(loc, mat, 50, false);
                            if(result != null){
                                Location structureLoc = result.getLocation();
                                if(structureLoc.distance(loc) < 10){ // ajuste a tolerância
                                    m.atualizaCondicao();
                                    missaoAtiva.replace(player.getUniqueId(), m);
                                }
                            }
                        }
                    }
                }
            });
        }
    }
}