package org.dantesys.reliquiasNexus.eventos;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.items.ItemsRegistro;
import org.dantesys.reliquiasNexus.items.Nexus;


import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.*;

public class LimitadorEvent implements Listener {
    private final ReliquiasNexus plugin;
    public LimitadorEvent(ReliquiasNexus plugin){
        this.plugin=plugin;
    }
    private final Map<UUID, List<ItemStack>> reliquiasSalvas = new HashMap<>();
    private final Map<UUID,ItemStack> bussolaMortal = new HashMap<>();
    @EventHandler
    public void pegouChao(EntityPickupItemEvent event){
        if(event.getEntity() instanceof Player player){
            ItemStack stack = event.getItem().getItemStack();
            ItemMeta meta = stack.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(NEXUS.key,PersistentDataType.STRING)){
                String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                if(nome!=null && !nome.isBlank()){
                    Nexus n = ItemsRegistro.getFromNome(nome);
                    PersistentDataContainer playerData = player.getPersistentDataContainer();
                    if(playerData.has(QTD.key,PersistentDataType.INTEGER) && n!=null){
                        int qtd = playerData.getOrDefault(QTD.key,PersistentDataType.INTEGER,0);
                        qtd++;
                        int level=1;
                        NamespacedKey key = getKey(nome);
                        if(key!=null && playerData.has(key,PersistentDataType.INTEGER)){
                            level=playerData.getOrDefault(key,PersistentDataType.INTEGER,1);
                        }
                        stack = n.getItem(level);
                        meta = stack.getItemMeta();
                        data = meta.getPersistentDataContainer();
                        data.set(DONO.key,PersistentDataType.STRING,player.getUniqueId().toString());
                        playerData.set(QTD.key,PersistentDataType.INTEGER,qtd);
                        ReliquiasNexus.setConfigSave("nexus."+nome,player.getUniqueId().toString());
                        plugin.saveConfig();
                    }
                }
            }
        }
    }
    @EventHandler
    public void limitesCara(PlayerDeathEvent event){
        Player player = event.getPlayer();
        PlayerInventory inv = player.getInventory();
        List<ItemStack> manterRelics = new ArrayList<>();
        List<ItemStack> bau = new ArrayList<>();
        Location localMorte = player.getLocation();
        if(player.getPersistentDataContainer().has(RENASCER.key,PersistentDataType.INTEGER)){
            int tempo = player.getPersistentDataContainer().getOrDefault(RENASCER.key,PersistentDataType.INTEGER,0);
            if(tempo>0){
                player.setHealth(20d);
                event.setCancelled(true);
            }
        }
        for(ItemStack item: inv.getContents()){
            if(item!=null){
                ItemMeta meta = item.getItemMeta();
                if(meta!=null){
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    if(data.has(NEXUS.key,PersistentDataType.STRING)){
                        Player assasino = player.getKiller();
                        boolean expurgo = ReliquiasNexus.getNexusConfig().getBoolean("expurgo");
                        if(assasino!=null && expurgo){
                            String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                            data.set(DONO.key,PersistentDataType.STRING,assasino.getUniqueId().toString());
                            ReliquiasNexus.setConfigSave("nexus."+nome,assasino.getUniqueId().toString());
                            plugin.saveConfig();
                            assasino.getInventory().addItem(item);
                            int qa = assasino.getPersistentDataContainer().getOrDefault(QTD.key,PersistentDataType.INTEGER,0);
                            qa++;
                            assasino.getPersistentDataContainer().set(QTD.key,PersistentDataType.INTEGER,qa);
                            event.getDrops().remove(item);
                        }
                        else{
                            manterRelics.add(item);
                            event.getDrops().remove(item);
                        }
                    }else{
                        bau.add(item);
                    }
                }else{
                    bau.add(item);
                }
            }
        }
        if(player.getPersistentDataContainer().has(PROTECAO.key,PersistentDataType.STRING)){
            String peixe = player.getPersistentDataContainer().get(PROTECAO.key,PersistentDataType.STRING);
            if(peixe!=null && !peixe.isBlank()){
                String msgAll = ReliquiasNexus.getLang().getString("morreumsg.peixe.all");
                if(msgAll==null){
                    msgAll="O Jogador <player> foi tranformado em um <fish>!";
                }
                msgAll=msgAll.replace("<player>",player.getName());
                msgAll=msgAll.replace("<fish>",peixe);
                String msg = ReliquiasNexus.getLang().getString("morreumsg.peixe.player");
                if(msg==null){
                    msg="Você foi tranformado em um <fish>!";
                }
                msg=msg.replace("<fish>",peixe);
                event.deathMessage(Component.text("§c"+msgAll));
                event.deathScreenMessageOverride(Component.text("§c"+msg));
            }
        }
        int limite = plugin.getConfig().getInt("limite",4);
        if(player.getPersistentDataContainer().getOrDefault(QTD.key,PersistentDataType.INTEGER,0)>limite){
            player.getPersistentDataContainer().set(QTD.key,PersistentDataType.INTEGER,limite);
            Collections.shuffle(manterRelics);
            List<ItemStack> itensAtuais = new ArrayList<>();
            for(int i = 0; i < limite && i < manterRelics.size(); i++){
                ItemStack item = manterRelics.get(i);
                itensAtuais.add(item);
            }
            manterRelics.removeIf(itensAtuais::contains);
            manterRelics.forEach(r -> {
                ItemMeta meta = r.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if(data.has(NEXUS.key,PersistentDataType.STRING)){
                    String nome = data.get(NEXUS.key,PersistentDataType.STRING);
                    data.set(DONO.key,PersistentDataType.STRING,"");
                    ReliquiasNexus.setConfigSave("nexus."+nome,"");
                    plugin.saveConfig();
                }
            });
            manterRelics = itensAtuais;
        }
        if (!manterRelics.isEmpty()) {
            reliquiasSalvas.put(player.getUniqueId(), manterRelics);
        }else{
            player.getPersistentDataContainer().set(QTD.key,PersistentDataType.INTEGER,0);
        }
        player.getPersistentDataContainer().set(PROTECAO.key,PersistentDataType.STRING,"");
        if(plugin.getConfig().getBoolean("recursos.bauMorte",true) && !bau.isEmpty()){
            if (localMorte.getY() < player.getWorld().getMinHeight()) {
                localMorte = getSafeChestLocation(player.getWorld(), localMorte);
            }
            // === 1. Criar o túmulo (cabeça + baú) ===
            Block blocoBau = localMorte.getBlock();
            Block blocoBau2;
            blocoBau.setType(Material.CHEST);
            // colocar a cabeça em cima do baú
            Block blocoCabeca = blocoBau.getRelative(0, 1, 0);
            blocoCabeca.setType(Material.PLAYER_HEAD);
            if (blocoCabeca.getState() instanceof Skull skull) {
                skull.setOwningPlayer(player);
                skull.update();
            }
            List<ItemStack> overflow;
            if(bau.size()>27){
                blocoBau2 = localMorte.clone().add(1,0,0).getBlock();
                blocoBau2.setType(Material.CHEST);
                overflow=bau.subList(27,bau.size());
                bau=bau.subList(0,27);
            } else {
                blocoBau2 = null;
                overflow = null;
            }
            // 3) agenda pro próximo tick para garantir que o bloco/estado foi aplicado
            List<ItemStack> finalBau = bau;
            Bukkit.getScheduler().runTask(plugin, () -> {
                BlockState state = blocoBau.getState();
                if (state instanceof Chest chest) {
                    Inventory chestInv = chest.getSnapshotInventory();
                    finalBau.forEach(chestInv::addItem);
                    chest.update(true,true);
                } else {
                    plugin.getLogger().warning("Esperava CHEST, mas encontrei: " + state.getClass().getName());
                }
                if(overflow != null && blocoBau2.getState() instanceof Chest chest){
                    Inventory chestInv = chest.getSnapshotInventory();
                    overflow.forEach(chestInv::addItem);
                    chest.update(true,true);
                }
            });

            // limpar drops do chão (já que foram para o baú)
            event.getDrops().clear();
            ItemStack bussola = new ItemStack(Material.COMPASS);
            CompassMeta meta = (CompassMeta) bussola.getItemMeta();
            if (meta != null) {
                meta.setLodestone(localMorte);
                meta.setLodestoneTracked(false);
                bussola.setItemMeta(meta);
            }
            bussolaMortal.put(player.getUniqueId(),bussola);
        }
    }
    private Location getSafeChestLocation(World world, Location deathLoc) {
        int x = deathLoc.getBlockX();
        int z = deathLoc.getBlockZ();

        // Começa no topo do mundo
        int y = world.getMaxHeight() - 1;

        // Procura o primeiro bloco sólido de cima pra baixo
        while (y > world.getMinHeight()) {
            Block block = world.getBlockAt(x, y, z);
            if (!block.getType().isAir()) {
                // achou um bloco sólido, coloca o baú em cima dele
                return new Location(world, x, y + 1, z);
            }
            y--;
        }

        // fallback: caso não ache nada (ex: mundo plano sem chão)
        return world.getSpawnLocation();
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        List<ItemStack> reliquias = reliquiasSalvas.remove(player.getUniqueId());
        if(bussolaMortal.containsKey(player.getUniqueId())){
            player.getInventory().addItem(bussolaMortal.remove(player.getUniqueId()));
        }
        if (reliquias != null) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                for (ItemStack item : reliquias) {
                    player.getInventory().addItem(item);
                }
            }, 1L);
        }
    }
}