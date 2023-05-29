package me.krutikov.redismanager.command;

import me.krutikov.redismanager.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Set;

public class RedisControlCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("krutikov.rediscontrol")) return true;
        Bukkit.getScheduler().runTaskAsynchronously(Main.getInstance(), () -> {
            Set<String> servers = Main.getRedisService().getArray("servers");
            sender.sendMessage("§r §6§l:: §7Связанные сервера:");
            for (String s : servers) {
                String online = Main.getRedisService().getValue(s+"-online");
                String status = online.equals("-1") ? "§cнедоступен" : "§aдоступен §8[§a" + online + " §7чел.§8]";
                sender.sendMessage("§r §7- " + s + ", " + status);
            }
        });
        return false;
    }
}
