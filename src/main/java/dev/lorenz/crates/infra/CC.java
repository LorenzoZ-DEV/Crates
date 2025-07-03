package dev.lorenz.crates.infra;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public interface CC {

    static String translate(String message){
        if(message == null){
            return null;
        }
        return ChatColor.translateAlternateColorCodes ('&', message);
    }

    static List<String> translateStrings(List<String> untranslated) {
        List<String> translated = new ArrayList ();

        for(String line : untranslated) {
            if (!line.isEmpty()) {
                translated.add(translate(line));
            }
        }

        return translated;
    }


    static void info(String message){
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = "&e[CRATES] &7 " + message;
        Bukkit.getConsoleSender().sendMessage( CC.translate ( message ));
    }

    static void warning(String message){
        Bukkit.getLogger().warning("[CRATES] " + message);
    }

    static void error(String message){
        Bukkit.getLogger().severe("[CRATES] " + message);
    }

    static void database(String message){
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = "&2[DATABASE] &7 " + message;
        Bukkit.getConsoleSender().sendMessage( CC.translate ( message ));
    }

    static void line(){
        Bukkit.getConsoleSender().sendMessage( CC.translate ( "&7----------------------------------------" ));
    }

    static void debug(String message){
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = "&4[DEBUG] &7 " + message;
        Bukkit.getConsoleSender().sendMessage( CC.translate ( message ));
    }
    }
