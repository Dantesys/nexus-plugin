package org.dantesys.reliquiasNexus.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class BedrockCompat {
    private static boolean enabled=false;
    private static Class<?> floodgateApiClass;
    private static Class<?> simpleFormClass;
    public static void setup(){
        if(Bukkit.getPluginManager().getPlugin("floodgate")!=null){
            try{
                floodgateApiClass = Class.forName("org.geysermc.floodgate.api.FloodgateApi");
                simpleFormClass = Class.forName("org.geysermc.cumulus.form.SimpleForm");
                enabled=true;
                Bukkit.getLogger().info("[Nexus] Bedrock support actived!");
            } catch (ClassNotFoundException e) {
                Bukkit.getLogger().warning("[Nexus] Bedrock support error!");
            }
        }
    }
    public static boolean isBedrockPlayer(Player player){
        if(!enabled)return false;
        if(Bukkit.getPluginManager().getPlugin("floodgate")==null)return false;
        try{
            Object api = floodgateApiClass.getMethod("getInstance").invoke(null);
            Method isFloodgate = floodgateApiClass.getMethod("isFloodgatePlayer", UUID.class);
            return (boolean) isFloodgate.invoke(api,player.getUniqueId());
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }
    public static void sendSimpleForm(Player player,String titulo,String texto,List<String> botoes, BiConsumer<Player,Integer> click){
        if(!isBedrockPlayer(player))return;
        try{
            Object formBuilder = simpleFormClass.getMethod("builder").invoke(null);
            formBuilder = formBuilder.getClass().getMethod("title",String.class).invoke(formBuilder,titulo);
            formBuilder = formBuilder.getClass().getMethod("content",String.class).invoke(formBuilder,texto);
            for(String btn:botoes){
                formBuilder = formBuilder.getClass().getMethod("button",String.class).invoke(formBuilder,btn);
            }
            Class<?> responseClass = Class.forName("org.geysermc.cumulus.response.SimpleFormResponse");
            BiConsumer<Player,Object> wrapped = (p,reponse) -> {
                try{
                    int clicked = (int) responseClass.getMethod("clickedButtonId").invoke(reponse);
                    click.accept(p,clicked);
                } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            };
            formBuilder = formBuilder.getClass().getMethod("validResultHandler", BiConsumer.class).invoke(formBuilder,wrapped);
            Object form = formBuilder.getClass().getMethod("build").invoke(formBuilder);
            Object api = floodgateApiClass.getMethod("getInstance").invoke(null);
            Object playerBR = api.getClass().getMethod("getPlayer").invoke(player.getUniqueId());
            playerBR.getClass().getMethod("sendForm").invoke(form);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    public static void sendForm(Player player,String titulo,String texto,List<String> botoes, List<String> comandos){
        sendSimpleForm(player,titulo,texto,botoes,(p,click) -> {
            if(click>=0 && click<comandos.size()){
                Bukkit.dispatchCommand(p,comandos.get(click));
            }
        });
    }

}
