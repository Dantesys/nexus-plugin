package org.dantesys.reliquiasNexus.loja;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.dantesys.reliquiasNexus.ReliquiasNexus;
import org.dantesys.reliquiasNexus.util.NexusKeys;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static org.dantesys.reliquiasNexus.util.NexusKeys.LOJAPLAYER;
import static org.dantesys.reliquiasNexus.util.NexusKeys.SALDO;

public class LojaManager implements Listener {
    private final JavaPlugin plugin;
    private List<LojaItem> todosItens;
    private List<LojaItem> itensAtuais;
    private LojaPage page;
    private final String MAIN_MENU_TITLE;
    private final String SERVER_ITEMS_MENU_TITLE;
    private final String NORMAL_ITEMS_MENU_TITLE;
    public LojaManager(JavaPlugin plugin){
        this.plugin=plugin;
        this.MAIN_MENU_TITLE = ReliquiasNexus.getLang().getString("loja.titulo","Loja Nexus");
        this.SERVER_ITEMS_MENU_TITLE = ReliquiasNexus.getLang().getString("loja.serverItens","Itens do servidor");
        this.NORMAL_ITEMS_MENU_TITLE = ReliquiasNexus.getLang().getString("loja.playerItens","Itens de jogadores");

    }
    private Map<String, Object> criarItem(ItemStack item, double preco) {
        Map<String, Object> map = new HashMap<>();
        map.put("item", item);
        map.put("preco", preco);
        return map;
    }
    public void load(YamlConfiguration lojaSV){
        todosItens = new ArrayList<>();
        List<Map<String, Object>> itensSalvos = (List<Map<String, Object>>) lojaSV.getList("servidor");
        if(itensSalvos == null) return;

        for(Map<String, Object> map : itensSalvos){
            ItemStack item = (ItemStack) map.get("item");
            double preco = 0;
            Object precoObj = map.get("preco");
            if(precoObj instanceof Double){
                preco = (Double) precoObj;
            } else if(precoObj instanceof Integer){
                preco = ((Integer) precoObj).doubleValue();
            }
            LojaItem lojaItem = new LojaItem(item, preco);
            todosItens.add(lojaItem);
        }
        gerarItensAtuais();
        List<Map<String, Object>> itensSalvosPlayers = (List<Map<String, Object>>) lojaSV.getList("players");
        if(itensSalvosPlayers == null) return;
        List<LojaItem> lojaPlayer = new ArrayList<>();
        for(Map<String, Object> map : itensSalvosPlayers){
            ItemStack item = (ItemStack) map.get("item");
            double preco = 0;
            Object precoObj = map.get("preco");
            if(precoObj instanceof Double){
                preco = (Double) precoObj;
            } else if(precoObj instanceof Integer){
                preco = ((Integer) precoObj).doubleValue();
            }
            LojaItem lojaItem = new LojaItem(item, preco);
            lojaPlayer.add(lojaItem);
        }
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§c"+ReliquiasNexus.getLang().getString("loja.voltar","Voltar"),
                Arrays.asList("§7"+ReliquiasNexus.getLang().getString("loja.backTooltip","Clique para voltar a pagina")),
                "back_button_player");
        ItemStack nextArrow = criarCabecaComID(
                "MHF_ArrowRight",
                "§c"+ReliquiasNexus.getLang().getString("loja.proximo","Proximo"),
                Arrays.asList("§7"+ReliquiasNexus.getLang().getString("loja.proximoTooltip","Clique para ir a próxima pagina")),
                "next_button_player");
        page = new LojaPage(lojaPlayer,backArrow,nextArrow);
    }
    private void gerarItensAtuais(){
        itensAtuais = new ArrayList<>();
        List<LojaItem> copy = new ArrayList<>(todosItens);
        Collections.shuffle(copy); // embaralha
        for(int i = 0; i < 21 && i < copy.size(); i++){
            LojaItem base = copy.get(i);
            base.gerarVenda();
            itensAtuais.add(base);
        }
    }
    public void gerarDefault(YamlConfiguration lojaSV){
        List<Map<String, Object>> itens = new ArrayList<>();
        itens.add(criarItem(new ItemStack(Material.WHEAT,16),80.0));
        itens.add(criarItem(new ItemStack(Material.WHEAT_SEEDS,16),40.0));
        itens.add(criarItem(new ItemStack(Material.CARROT,16),100.0));
        itens.add(criarItem(new ItemStack(Material.POTATO,16),100.0));
        itens.add(criarItem(new ItemStack(Material.BEETROOT,16),90.0));
        itens.add(criarItem(new ItemStack(Material.SUGAR_CANE,16),120.0));
        itens.add(criarItem(new ItemStack(Material.CACTUS,16),100.0));
        itens.add(criarItem(new ItemStack(Material.MELON,8),150.0));
        itens.add(criarItem(new ItemStack(Material.PUMPKIN,8),200.0));
        itens.add(criarItem(new ItemStack(Material.HONEY_BOTTLE,1),300.0));
        itens.add(criarItem(new ItemStack(Material.EGG,8),80.0));
        itens.add(criarItem(new ItemStack(Material.LEATHER,8),250.0));
        itens.add(criarItem(new ItemStack(Material.OAK_LOG,16),50.0));
        itens.add(criarItem(new ItemStack(Material.BIRCH_LOG,16),50.0));
        itens.add(criarItem(new ItemStack(Material.COBBLESTONE, 32),40.0));
        itens.add(criarItem(new ItemStack(Material.COAL,16),100.0));
        itens.add(criarItem(new ItemStack(Material.IRON_INGOT,8),250.0));
        itens.add(criarItem(new ItemStack(Material.CLAY_BALL,16),120.0));
        itens.add(criarItem(new ItemStack(Material.SAND,32),60.0));
        itens.add(criarItem(new ItemStack(Material.GRAVEL,32),60.0));
        itens.add(criarItem(new ItemStack(Material.GLASS,16),200.0));
        itens.add(criarItem(new ItemStack(Material.BRICK,16),300.0));
        itens.add(criarItem(new ItemStack(Material.GOLD_INGOT, 8),500));
        itens.add(criarItem(new ItemStack(Material.REDSTONE,32),400.0));
        itens.add(criarItem(new ItemStack(Material.LAPIS_LAZULI,32),350.0));
        itens.add(criarItem(new ItemStack(Material.QUARTZ,16),300.0));
        itens.add(criarItem(new ItemStack(Material.GLOWSTONE_DUST,8),450.0));
        itens.add(criarItem(new ItemStack(Material.ENDER_PEARL,1),700.0));
        itens.add(criarItem(new ItemStack(Material.SLIME_BALL,4),600.0));
        itens.add(criarItem(new ItemStack(Material.BLAZE_POWDER,2),800.0));
        itens.add(criarItem(new ItemStack(Material.EMERALD,4),1000.0));
        itens.add(criarItem(new ItemStack(Material.DIAMOND,2),1500.0));
        itens.add(criarItem(new ItemStack(Material.GOLDEN_APPLE,1),2500.0));
        itens.add(criarItem(new ItemStack(Material.ENDER_EYE,2),2000.0));
        itens.add(criarItem(new ItemStack(Material.OBSIDIAN,8),1200.0));
        itens.add(criarItem(new ItemStack(Material.NETHERITE_SCRAP,1),4000.0));
        itens.add(criarItem(new ItemStack(Material.WITHER_SKELETON_SKULL,1),7000.0));
        itens.add(criarItem(new ItemStack(Material.ENDER_CHEST,1),5000.0));
        itens.add(criarItem(new ItemStack(Material.SHULKER_BOX,1),4000.0));
        itens.add(criarItem(new ItemStack(Material.TOTEM_OF_UNDYING,1),8000.0));
        itens.add(criarItem(new ItemStack(Material.ELYTRA,1),15000.0));
        itens.add(criarItem(new ItemStack(Material.SADDLE,1),1200.0));
        itens.add(criarItem(new ItemStack(Material.NAME_TAG,1),900.0));
        itens.add(criarItem(new ItemStack(Material.DRAGON_HEAD,1),20000.0));
        itens.add(criarItem(new ItemStack(Material.NETHERITE_SWORD,1),12000.0));
        itens.add(criarItem(new ItemStack(Material.TRIDENT,1),15000.0));
        itens.add(criarItem(new ItemStack(Material.BEACON,1),30000.0));
        itens.add(criarItem(new ItemStack(Material.FIREWORK_ROCKET,64),1000.0));
        itens.add(criarItem(new ItemStack(Material.CREEPER_HEAD,1),5000.0));
        itens.add(criarItem(new ItemStack(Material.SKELETON_SKULL,1),5000.0));
        itens.add(criarItem(new ItemStack(Material.ZOMBIE_HEAD,1),5000.0));
        itens.add(criarItem(new ItemStack(Material.MUSIC_DISC_LAVA_CHICKEN,1),10000.0));
        itens.add(criarItem(new ItemStack(Material.TURTLE_EGG,1),1000.0));
        itens.add(criarItem(new ItemStack(Material.HEAVY_CORE,1),50000.0));
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.MENDING,0,false);
        item.setItemMeta(meta);
        itens.add(criarItem(item,100000.0));
        ItemStack item2 = new ItemStack(Material.BOOK);
        ItemMeta meta2 = item2.getItemMeta();
        meta2.addEnchant(Enchantment.SILK_TOUCH,0,false);
        item2.setItemMeta(meta2);
        itens.add(criarItem(item2,50000.0));
        ItemStack item3 = new ItemStack(Material.BOOK);
        ItemMeta meta3 = item3.getItemMeta();
        meta3.addEnchant(Enchantment.FORTUNE,2,false);
        item3.setItemMeta(meta3);
        itens.add(criarItem(item3,50000.0));
        ItemStack item4 = new ItemStack(Material.BOOK);
        ItemMeta meta4 = item4.getItemMeta();
        meta4.addEnchant(Enchantment.THORNS,2,false);
        item4.setItemMeta(meta4);
        itens.add(criarItem(item4,25000.0));
        ItemStack item5 = new ItemStack(Material.BOOK);
        ItemMeta meta5 = item5.getItemMeta();
        meta5.addEnchant(Enchantment.FROST_WALKER,1,false);
        item5.setItemMeta(meta5);
        itens.add(criarItem(item5,25000.0));
        ItemStack item6 = new ItemStack(Material.BOOK);
        ItemMeta meta6 = item6.getItemMeta();
        meta6.addEnchant(Enchantment.LOOTING,2,false);
        item6.setItemMeta(meta6);
        itens.add(criarItem(item6,25000.0));
        ItemStack item7 = new ItemStack(Material.BOOK);
        ItemMeta meta7 = item7.getItemMeta();
        meta7.addEnchant(Enchantment.PROTECTION,3,false);
        item7.setItemMeta(meta7);
        itens.add(criarItem(item7,20000.0));
        ItemStack item8 = new ItemStack(Material.BOOK);
        ItemMeta meta8 = item8.getItemMeta();
        meta8.addEnchant(Enchantment.UNBREAKING,2,false);
        item8.setItemMeta(meta8);
        itens.add(criarItem(item8,20000.0));
        ItemStack item9 = new ItemStack(Material.BOOK);
        ItemMeta meta9 = item9.getItemMeta();
        meta9.addEnchant(Enchantment.SHARPNESS,4,false);
        item9.setItemMeta(meta9);
        itens.add(criarItem(item9,20000.0));
        ItemStack item10 = new ItemStack(Material.BOOK);
        ItemMeta meta10 = item10.getItemMeta();
        meta10.addEnchant(Enchantment.SOUL_SPEED,2,false);
        item10.setItemMeta(meta10);
        itens.add(criarItem(item10,20000.0));
        lojaSV.set("servidor",itens);
        save(lojaSV);
    }
    public void save(YamlConfiguration lojaSV){
        File ms = new File(plugin.getDataFolder(), "loja.yml");
        try {
            lojaSV.save(ms);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void abrirMenuPrincipal(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, Component.text(MAIN_MENU_TITLE));
        // Adicionar bordas cinzas
        ItemStack borda = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta metaBorda = borda.getItemMeta();
        metaBorda.displayName(Component.text(" "));
        borda.setItemMeta(metaBorda);

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, borda);
        }

        ItemStack opItems = criarItemComID(Material.DIAMOND_BLOCK, "§b"+SERVER_ITEMS_MENU_TITLE,
                Arrays.asList("§7"+ReliquiasNexus.getLang().getString("loja.serverTooltip","Clique para ver os itens do servidor")), "op_items");

        ItemStack normalItems = criarItemComID(Material.IRON_BLOCK, "§a"+NORMAL_ITEMS_MENU_TITLE,
                Arrays.asList("§7"+ReliquiasNexus.getLang().getString("loja.playerTooltip","Clique para ver os itens de jogadores")), "normal_items");

        inv.setItem(11, opItems);
        inv.setItem(15, normalItems);

        player.openInventory(inv);
        playOpenSound(player);
    }
    private void showView(Inventory inv){
        int slot=10;
        int cont=1;
        for(LojaItem item: itensAtuais){
            inv.setItem(slot,item.getItem());
            cont++;
            if(cont>=7){
                slot+=2;
            }else{
                slot++;
            }
        }
    }
    private LojaItem getBySlot(int slot){
        return switch(slot){
            case 10 -> itensAtuais.getFirst();
            case 11 -> itensAtuais.get(1);
            case 12 -> itensAtuais.get(2);
            case 13 -> itensAtuais.get(3);
            case 14 -> itensAtuais.get(4);
            case 15 -> itensAtuais.get(5);
            case 16 -> itensAtuais.get(6);
            case 19 -> itensAtuais.get(7);
            case 20 -> itensAtuais.get(8);
            case 21 -> itensAtuais.get(9);
            case 22 -> itensAtuais.get(10);
            case 23 -> itensAtuais.get(11);
            case 24 -> itensAtuais.get(12);
            case 25 -> itensAtuais.get(13);
            case 28 -> itensAtuais.get(14);
            case 29 -> itensAtuais.get(15);
            case 30 -> itensAtuais.get(16);
            case 31 -> itensAtuais.get(17);
            case 32 -> itensAtuais.get(18);
            case 33 -> itensAtuais.get(19);
            case 34 -> itensAtuais.get(20);
            default -> null;
        };
    }
    private int getIndex(int slot){
        return switch (slot){
            case 10 -> 0;
            case 11 -> 1;
            case 12 -> 2;
            case 13 -> 3;
            case 14 -> 4;
            case 15 -> 5;
            case 16 -> 6;
            case 19 -> 7;
            case 20 -> 8;
            case 21 -> 9;
            case 22 -> 10;
            case 23 -> 11;
            case 24 -> 12;
            case 25 -> 13;
            case 28 -> 14;
            case 29 -> 15;
            case 30 -> 16;
            case 31 -> 17;
            case 32 -> 18;
            case 33 -> 19;
            case 34 -> 20;
            default -> -1;
        };
    }
    private void abrirMenuOpItems(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text(SERVER_ITEMS_MENU_TITLE));

        // Adicionar bordas
        ItemStack borda = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta metaBorda = borda.getItemMeta();
        metaBorda.displayName(Component.text(" "));
        borda.setItemMeta(metaBorda);

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, borda);
        }

        // Baú do Fim (Ender Chest) - Item especial que libera comando /ec
        ItemStack enderChestItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta enderChestMeta = enderChestItem.getItemMeta();
        List<Component> enderLore = new ArrayList<>();
        double preco = plugin.getConfig().getDouble("recursos.enderchestcost",5000.0);
        enderLore.add(Component.text("§7"+ReliquiasNexus.getLang().getString("loja.ec","Permite o uso do comando ")+" §e/ec§7."));
        enderLore.add(Component.text(""));
        enderLore.add(Component.text("§aPreço: §6$"+preco+" "+plugin.getConfig().getString("recursos.moneyName","moly")));
        enderChestMeta.lore(enderLore);
        enderChestMeta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, "ender_chest");
        enderChestMeta.getPersistentDataContainer().set(new NamespacedKey(plugin, "preco"), PersistentDataType.DOUBLE, (double) preco);
        enderChestItem.setItemMeta(enderChestMeta);
        inv.setItem(4, enderChestItem); // Posição destacada no topo
        showView(inv);
        // Botão de voltar
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§c"+ReliquiasNexus.getLang().getString("loja.voltar","Voltar"),
                Arrays.asList("§7"+ReliquiasNexus.getLang().getString("loja.voltarTooltip","Clique para voltar ao menu principal")),
                "back_button");
        inv.setItem(49, backArrow);

        player.openInventory(inv);
        playOpenSound(player);
    }
    //TODO LOJA DOS PLAYERS
    private void abrirMenuNormalItems(Player player) {
        Inventory inv = Bukkit.createInventory(null, 54, Component.text(NORMAL_ITEMS_MENU_TITLE));

        // Adicionar bordas
        ItemStack borda = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta metaBorda = borda.getItemMeta();
        metaBorda.displayName(Component.text(" "));
        borda.setItemMeta(metaBorda);

        for (int i = 0; i < 54; i++) {
            inv.setItem(i, borda);
        }
        page.showPage(inv);
        // Botão de voltar
        ItemStack backArrow = criarCabecaComID(
                "MHF_ArrowLeft",
                "§c"+ReliquiasNexus.getLang().getString("loja.voltar","Voltar"),
                Arrays.asList("§7"+ReliquiasNexus.getLang().getString("loja.voltarTooltip","Clique para voltar ao menu principal")),
                "back_button");
        inv.setItem(49, backArrow);

        player.openInventory(inv);
        playOpenSound(player);
    }

    private ItemStack criarItemComID(Material material, String nome, List<String> lore, String id) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text(nome));

        List<Component> componentLore = new ArrayList<>();
        for (String line : lore) {
            componentLore.add(Component.text(line));
        }
        meta.lore(componentLore);

        meta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, id);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack criarCabecaComID(String owner, String nome, List<String> lore, String id) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();
        meta.setOwner(owner);
        meta.displayName(Component.text(nome));

        List<Component> componentLore = new ArrayList<>();
        for (String line : lore) {
            componentLore.add(Component.text(line));
        }
        meta.lore(componentLore);

        meta.getPersistentDataContainer().set(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING, id);
        head.setItemMeta(meta);
        return head;
    }

    private void playOpenSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);

        // Animação simples de abrir menu
        new BukkitRunnable() {
            int count = 0;
            @Override
            public void run() {
                if (count < 3) {
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f + (count * 0.2f));
                    count++;
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 2L, 2L);
    }

    private void playBuySound(Player player) {
        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.8f);
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
    }

    private void playErrorSound(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.5f, 0.8f);
    }

    // Método auxiliar para converter Component para String
    private String componentToString(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

    @EventHandler
    public void tickUpdate(ServerTickEndEvent event){
        if(event.getTickNumber()%72000==0){
            gerarItensAtuais();
        }
        if(event.getTickNumber()%200==0){
            List<Map<String, Object>> itensSalvosPlayers = (List<Map<String, Object>>) ReliquiasNexus.getLoja().getList("players");
            if(itensSalvosPlayers == null) return;
            List<LojaItem> lojaPlayer = new ArrayList<>();
            for(Map<String, Object> map : itensSalvosPlayers){
                ItemStack item = (ItemStack) map.get("item");
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(LOJAPLAYER.key,PersistentDataType.BOOLEAN,true);
                item.setItemMeta(meta);
                double preco = 0;
                Object precoObj = map.get("preco");
                if(precoObj instanceof Double){
                    preco = (Double) precoObj;
                } else if(precoObj instanceof Integer){
                    preco = ((Integer) precoObj).doubleValue();
                }
                LojaItem lojaItem = new LojaItem(item, preco);
                lojaPlayer.add(lojaItem);
            }
            page.updateItens(lojaPlayer);
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        String inventoryTitle = componentToString(event.getView().title());
        ItemMeta meta = clickedItem.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        double saldo = player.getPersistentDataContainer().getOrDefault(SALDO.key,PersistentDataType.DOUBLE,0.0);
        if (inventoryTitle.equals(MAIN_MENU_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);
                if(itemId==null)itemId="";
                switch (itemId) {
                    case "op_items":
                        abrirMenuOpItems(player);
                        break;
                    case "normal_items":
                        abrirMenuNormalItems(player);
                        break;
                    default:
                        break;
                }
            }
        } else if (inventoryTitle.equals(SERVER_ITEMS_MENU_TITLE)) {
            event.setCancelled(true);
            if (data.has(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING)) {
                String itemId = data.get(NexusKeys.LOJA_ITEM_KEY.key, PersistentDataType.STRING);

                if ("back_button".equals(itemId)) {
                    abrirMenuPrincipal(player);
                    return;
                }
                if ("back_button_player".equals(itemId)) {
                    page.backPage();
                    abrirMenuNormalItems(player);
                    return;
                }
                if ("next_button_player".equals(itemId)) {
                    page.nextPage();
                    abrirMenuNormalItems(player);
                    return;
                }

                // Lógica específica para o Baú do Fim
                if (itemId!=null && itemId.equals("ender_chest")) {
                    double preco = plugin.getConfig().getDouble("recursos.enderchestcost",5000.0);
                    if (saldo >= preco) {
                        PersistentDataContainer playerData = player.getPersistentDataContainer();
                        if (playerData.has(NexusKeys.ENDER_CHEST_OWNED.key, PersistentDataType.BOOLEAN)) {
                            player.sendMessage(Component.text("❌ "+ReliquiasNexus.getLang().getString("loja.falhaEnd","Você já comprou o Baú do Fim. Esta é uma compra única.")).color(NamedTextColor.RED));
                            playErrorSound(player);
                            return;
                        }
                        playerData.set(NexusKeys.ENDER_CHEST_OWNED.key, PersistentDataType.BOOLEAN, true);
                        player.getPersistentDataContainer().set(SALDO.key,PersistentDataType.DOUBLE,saldo-preco);
                        String nome = ReliquiasNexus.getLang().getString("loja.end","acesso ao /ec");
                        String precoStr = String.format("%.2f", preco);
                        player.sendMessage(Component.text("✅ "+ReliquiasNexus.getLang().getString("loja.comprou","Você comprou <item> por <cost>").replace("<item>",nome).replace("<cost>",precoStr)).color(NamedTextColor.GREEN));
                        playBuySound(player);
                    } else {
                        player.sendMessage(Component.text("❌ "+ReliquiasNexus.getLang().getString("loja.falhaSaldo","Você não tem saldo o suficiente")).color(NamedTextColor.RED));
                        playErrorSound(player);
                    }
                    return;
                }
                // Verificar se o item tem preço
                LojaItem ljItem = getBySlot(event.getSlot());
                int index = getIndex(event.getSlot());
                if (ljItem!=null && index>=0) {
                    if(ljItem.getEstoque()>0){
                        double preco = ljItem.getPreco(false);
                        if (saldo >= preco) {
                            if (player.getInventory().firstEmpty() != -1) {
                                ljItem.compra();
                                itensAtuais.set(index,ljItem);
                                player.getPersistentDataContainer().set(SALDO.key,PersistentDataType.DOUBLE,saldo-preco);
                                player.getInventory().addItem(ljItem.getItem());
                                Component nomeComp = ljItem.getItem().displayName();
                                String nome = PlainTextComponentSerializer.plainText().serialize(nomeComp);
                                String precoStr = String.format("%.2f", preco);
                                player.sendMessage(Component.text("✅ "+ReliquiasNexus.getLang().getString("loja.comprou","Você comprou <item> por <cost>").replace("<item>",nome).replace("<cost>",precoStr)).color(NamedTextColor.GREEN));
                                playBuySound(player);
                            } else {
                                player.sendMessage(Component.text("❌ "+ReliquiasNexus.getLang().getString("loja.falhaInv","Seu inventario está cheio!")).color(NamedTextColor.RED));
                                playErrorSound(player);
                            }
                        } else {
                            player.sendMessage(Component.text("❌ "+ReliquiasNexus.getLang().getString("loja.falhaSaldo","Você não tem saldo o suficiente")).color(NamedTextColor.RED));
                            playErrorSound(player);
                        }
                    }else{
                        player.sendMessage(Component.text("❌ "+ReliquiasNexus.getLang().getString("loja.falhaEstoque","Item sem estoque")).color(NamedTextColor.RED));
                        playErrorSound(player);
                    }
                }else{
                    LojaItem item = page.getItem(clickedItem);
                    LojaPageResult result = page.comprar(item,player);
                    switch(result){
                        case NULO -> {
                            player.sendMessage(Component.text("❌ "+ReliquiasNexus.getLang().getString("loja.falhaEstoque","Item sem estoque")).color(NamedTextColor.RED));
                            playErrorSound(player);
                        }
                        case INVFULL -> {
                            player.sendMessage(Component.text("❌ "+ReliquiasNexus.getLang().getString("loja.falhaInv","Seu inventario está cheio!")).color(NamedTextColor.RED));
                            playErrorSound(player);
                        }
                        case SEMSALDO -> {
                            player.sendMessage(Component.text("❌ "+ReliquiasNexus.getLang().getString("loja.falhaSaldo","Você não tem saldo o suficiente")).color(NamedTextColor.RED));
                            playErrorSound(player);
                        }
                        case SUCCESS -> {
                            Component nomeComp = item.getItem().displayName();
                            String nome = PlainTextComponentSerializer.plainText().serialize(nomeComp);
                            String precoStr = String.format("%.2f", item.getPreco(true));
                            player.sendMessage(Component.text("✅ "+ReliquiasNexus.getLang().getString("loja.comprou","Você comprou <item> por <cost>").replace("<item>",nome).replace("<cost>",precoStr)).color(NamedTextColor.GREEN));
                            playBuySound(player);
                            List<Map<String,Object>> itensSalvos = (List<Map<String,Object>>) ReliquiasNexus.getLoja().getList("players", new ArrayList<>());
                            itensSalvos.removeIf(map -> {
                                ItemStack stack = (ItemStack) map.get("item");
                                return stack != null && stack.equals(item.getItem());
                            });
                            ReliquiasNexus.getLoja().set("players", itensSalvos);
                            save(ReliquiasNexus.getLoja());
                        }
                    }
                }
            }
        } else if(inventoryTitle.equals(NORMAL_ITEMS_MENU_TITLE)){
            event.setCancelled(true);
        }
    }
}
