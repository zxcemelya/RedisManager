package me.krutikov.redismanager;

import lombok.Getter;
import me.krutikov.redismanager.command.RedisControlCommand;
import me.krutikov.redismanager.redis.IRedis;
import me.krutikov.redismanager.redis.impl.RedisService;
import me.krutikov.redismanager.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.File;

public final class Main extends JavaPlugin {

    @Getter
    private static IRedis redisService;

    @Getter
    private static Main instance;

    @Getter
    private String serverName;

    @Override
    public void onEnable() {
        redisService = new RedisService();
        serverName = getServerNameByFolder();
        instance = this;
        jedisPubSub();
        getCommand("rediscontrol").setExecutor(new RedisControlCommand());
        redisService.pub("register-service", serverName + " " + Bukkit.getServer().getPort());
        redisService.addToArray("servers", serverName);
        redisService.setValueAsync(serverName +"-online", Bukkit.getOnlinePlayers().size() + "");
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent e) {
                int online = Bukkit.getOnlinePlayers().size();
                redisService.setValueAsync(serverName +"-online", online + "");
            }
            @EventHandler
            public void onQuit(PlayerQuitEvent e) {
                int online = Bukkit.getOnlinePlayers().size() - 1;
                redisService.setValueAsync(serverName +"-online", online + "");
            }

        }, this);
        // Plugin startup logic

    }
    private String getServerNameByFolder() {
        try {
            return new File("").getCanonicalFile().getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    void jedisPubSub() {
        Runnable runnable = () -> {
            JedisPubSub jedisPubSub = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String message) {
                    switch (channel) {
                        case ("register-service"): {
                            if (message.equals("autoreg-all")) {
                                redisService.pub("register-service", getServerName() + " " + Bukkit.getServer().getPort());
                            }
                            break;
                        }
                        case ("executor-service"): {
                            if (message.startsWith("proxy")) return;
                            String[] parts = message.split(" ");
                            String serverName = parts[0];
                            String cmd = Utils.multiArgs(parts, 1);
                            if (serverName.equalsIgnoreCase(getServerName())) {
                                Utils.consoleDispatch(cmd);
                            }
                            break;
                        }
                    }
                }
            };
            try (Jedis jedis = RedisService.newJedisInstance()) {
                redisService.authenticateJedis(jedis);
                jedis.subscribe(jedisPubSub, "proxy-message", "register-service", "executor-service");
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
    @Override
    public void onDisable() {
        redisService.setValue(serverName +"-online", "-1");
    }
}
