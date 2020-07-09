package io.github.winx64.sse.listener;

import io.github.winx64.sse.data.PlayerRepository;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class PlayerInOutListener implements Listener {

    private final PlayerRepository repository;

    public PlayerInOutListener(PlayerRepository repository) {
        this.repository = repository;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        repository.registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        repository.unregisterPlayer(event.getPlayer());
    }
}
