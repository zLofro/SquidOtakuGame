package me.lofro.game.games.finalfight;

import lombok.Getter;
import lombok.Setter;
import me.lofro.game.games.GameManager;
import me.lofro.game.games.finalfight.listeners.FinalFightListener;
import me.lofro.game.global.enums.PvPState;
import me.lofro.game.players.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class FinalFightManager {

    private final @Getter GameManager gameManager;
    private final @Getter FinalFightListener finalFightListener;

    private @Getter boolean isRunning = false;

    private @Getter @Setter int playerLimit;

    private final @Getter List<Player> eliminated = new ArrayList<>();

    public FinalFightManager(GameManager gameManager) {
        this.gameManager = gameManager;

        this.finalFightListener = new FinalFightListener(this);
    }

    public void runGame(int playerLimit) {
        this.isRunning = true;

        this.playerLimit = playerLimit;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather thunder");

        Bukkit.getOnlinePlayers().forEach(p -> p.playSound(p.getLocation(), "sfx.final_fight", 1f, 1f));

        gameManager.gameData().setPvPState(PvPState.ALL);

        gameManager.getSquidInstance().registerListener(finalFightListener);
    }

    public void endGame() {
        this.isRunning = false;

        eliminated.clear();

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "weather clear");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stopsound @a");

        gameManager.gameData().setPvPState(PvPState.ONLY_GUARDS);

        gameManager.getSquidInstance().unregisterListener(finalFightListener);
    }

    public PlayerManager playerManager() {
        return gameManager.getSquidInstance().getPManager();
    }

}
