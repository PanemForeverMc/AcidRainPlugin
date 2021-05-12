package com.ashawsolutions.acidrain;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public final class AcidRain extends JavaPlugin {

    private World acidWorld;
    private boolean isAcidRainActive = false;

    private long timeInterval = 5000;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("AcidRain has loaded.");

        new BukkitRunnable(){
            public void run(){
                if(isAcidRainActive){

                    //Check if it's still raining, if not start it up again.
                    if(!acidWorld.hasStorm()){
                        acidWorld.setStorm(true);
                    }

                    PotionEffectType effectType = PotionEffectType.WITHER;
                    PotionEffect effect = effectType.createEffect(100, 1);

                    for(Player p: Bukkit.getOnlinePlayers()){
                        if(p.getWorld().getName().equals(acidWorld.getName())){
                            int blockLocation = p.getLocation().getWorld().getHighestBlockYAt(p.getLocation());
                            if(blockLocation <= p.getLocation().getY()){
                                p.addPotionEffect(effect);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        isAcidRainActive = false;
        getLogger().info("AcidRain has unloaded.");
    }

    private List<String> _playersInAcidWorld = new ArrayList<String>();


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(label.equalsIgnoreCase("acidrain")){

            if(!sender.hasPermission("panemforever.gamemaker")){
                sender.sendMessage(ChatColor.RED + "" + "You do not have permission to use this command.");
                return false;
            }

            if(args.length == 0 || args.length > 1){
                sender.sendMessage(ChatColor.RED + "" + "Invalid parameters. Please do /acidrain <mode>!");
                return false;
            }

            if(!(sender instanceof Player)){
                sender.sendMessage("You must use this command as a player.");
                return false;
            }

            Player localPlayer = (Player)sender;

            World acidRainWorld = localPlayer.getWorld();
            acidWorld = acidRainWorld;

            String mode = args[0];
            switch(mode){
                case "enable":
                case "on":
                    for (Player p : Bukkit.getOnlinePlayers()){
                        if(p.getWorld().getName().equals(localPlayer.getWorld().getName())){
                            if(_playersInAcidWorld.contains(p.getName())){
                                continue;
                            }
                            p.sendTitle( ChatColor.RED + "" + "Alert", ChatColor.DARK_RED + "" + "Storm clouds begin to form.");
                            p.playSound(p.getLocation(), Sound.MUSIC_DISC_STAL, Float.MAX_VALUE, 1.2f);
                            _playersInAcidWorld.add(p.getName());
                        }
                    }
                    acidRainWorld.setStorm(true);
                    isAcidRainActive = true;

                    break;
                case "disable":
                case "off":
                    //Disable acid rain and resume previous world.
                    _playersInAcidWorld.clear();
                    acidRainWorld.setStorm(false);
                    for(Player p: Bukkit.getOnlinePlayers()){
                        if(p.getWorld().getName().equals(localPlayer.getWorld().getName())){
                            p.playSound(p.getLocation(), Sound.MUSIC_DISC_STAL, Float.MAX_VALUE, 1.2f);
                            p.sendTitle(ChatColor.GREEN + "" + "Alert",  ChatColor.DARK_GREEN + "" + "The sun appears again.");
                        }
                    }
                    isAcidRainActive = false;
                    break;
                default:
                    sender.sendMessage(ChatColor.RED + "" + "Invalid mode. Please use ON/ENABLE or OFF/DISABLE");
                    return false;
            }
        }
        return true;
    }
}
