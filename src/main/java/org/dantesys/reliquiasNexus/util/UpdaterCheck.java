package org.dantesys.reliquiasNexus.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.dantesys.reliquiasNexus.ReliquiasNexus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class UpdaterCheck {
    private final JavaPlugin plugin;
    private final String repo;
    public UpdaterCheck(JavaPlugin plugin, String repo) {
        this.plugin = plugin;
        this.repo = repo;
    }
    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = URI.create("https://api.github.com/repos/" + repo + "/releases/latest").toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    String latestVersion = parseTagName(response.toString());
                    String currentVersion = plugin.getPluginMeta().getVersion();
                    if (!currentVersion.equalsIgnoreCase(latestVersion)) {
                        String msg = ReliquiasNexus.getLang().getString("updater.new");
                        if(msg==null){
                            msg = "Nova versão disponível no GitHub: <new> (você está usando <used>)";
                        }
                        msg=msg.replace("<new>",latestVersion);
                        msg=msg.replace("<used>",currentVersion);
                        plugin.getLogger().warning(msg);
                        String dw = ReliquiasNexus.getLang().getString("updater.dowload");
                        if(dw==null){
                            dw = "Baixe em: <link>";
                        }
                        dw=msg.replace("<link>","https://github.com/" + repo + "/releases/latest");
                        plugin.getLogger().warning(dw);
                    } else {
                        String msg = ReliquiasNexus.getLang().getString("updater.current");
                        if(msg==null){
                            msg = "Você está usando a versão mais recente!";
                        }
                        plugin.getLogger().info(msg);
                    }
                }
            } catch (IOException e) {
                String msg = ReliquiasNexus.getLang().getString("updater.erro");
                if(msg==null){
                    msg = "Não foi possível verificar atualizações:";
                }
                plugin.getLogger().warning(msg+" " + e.getMessage());
            }
        });
    }

    private String parseTagName(String json) {
        int index = json.indexOf("\"tag_name\"");
        if (index != -1) {
            int start = json.indexOf(":", index) + 2;
            int end = json.indexOf("\"", start);
            return json.substring(start, end);
        }
        return "unknown";
    }
}
