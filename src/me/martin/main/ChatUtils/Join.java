package me.martin.main.ChatUtils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

    @EventHandler
    public void join(PlayerJoinEvent e){

        e.setJoinMessage(null);
    }

}
