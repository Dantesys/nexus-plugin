package org.dantesys.reliquiasNexus.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdaterCheck {
    private final JavaPlugin plugin;
    private final String repoOwner;
    private final String repoName;

    public UpdaterCheck(JavaPlugin plugin, String repo) {
        this.plugin = plugin;
        String[] splitRepo = repo.split("/");
        this.repoOwner = splitRepo[0];
        this.repoName = splitRepo[1];
    }

    public void checkForUpdates() {
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                URL url = new URL("https://api.github.com/repos/" + repoOwner + "/" + repoName + "/releases/latest");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0");

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject();
                    String latestVersion = jsonObject.get("tag_name").getAsString();

                    if (isUpdateAvailable(plugin.getDescription().getVersion(), latestVersion)) {
                        plugin.getLogger().info("-------------------------------------------");
                        plugin.getLogger().warning("New version " + latestVersion + " is available!");
                        plugin.getLogger().warning("You are currently using version " + plugin.getDescription().getVersion() + ".");
                        plugin.getLogger().warning("Download the new version from: " + jsonObject.get("html_url").getAsString());
                        plugin.getLogger().info("-------------------------------------------");
                    } else {
                        plugin.getLogger().info("You are using the latest version of the plugin!");
                    }
                } else {
                    plugin.getLogger().warning("Failed to check for updates. HTTP response code: " + responseCode);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    private boolean isUpdateAvailable(String currentVersion, String latestVersion) {
        String[] current = currentVersion.replace("v", "").split("\\.");
        String[] latest = latestVersion.replace("v", "").split("\\.");

        for (int i = 0; i < Math.min(current.length, latest.length); i++) {
            int currentPart = Integer.parseInt(current[i]);
            int latestPart = Integer.parseInt(latest[i]);

            if (latestPart > currentPart) {
                return true;
            }
            if (latestPart < currentPart) {
                return false;
            }
        }

        return latest.length > current.length;
    }
}