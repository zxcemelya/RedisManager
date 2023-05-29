package me.krutikov.redismanager.utils;

import lombok.experimental.UtilityClass;
import me.krutikov.redismanager.Main;
import org.bukkit.Bukkit;

@UtilityClass
public class Utils {
    public String multiArgs(String[] args, int number) {
        String text = "";
        for (int i = number; i < args.length; i++) {
            if (text.equals("")) text = args[i];
            else text += " " + args[i];
        }
        return text;
    }
    public void consoleDispatch(String command) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), () -> {
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), command);
        });
    }
}
