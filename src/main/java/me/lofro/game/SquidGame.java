package me.lofro.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import me.lofro.game.data.DataManager;
import me.lofro.game.data.adapters.LocationSerializer;
import me.lofro.game.games.GameManager;
import me.lofro.game.global.commands.TimerCMD;
import me.lofro.game.global.listeners.GlobalListener;
import me.lofro.game.global.utils.Strings;
import me.lofro.game.players.PlayerManager;
import me.lofro.game.players.adapters.RuntimeTypeAdapterFactory;
import me.lofro.game.players.objects.SquidGuard;
import me.lofro.game.players.objects.SquidParticipant;
import me.lofro.game.players.objects.SquidPlayer;
import net.kyori.adventure.text.minimessage.MiniMessage;

/**
 * Entrypoint for Squid Otaku Game plugin.
 * 
 * 
 * @author <a href="https://github.com/zLofro">Lofro</a> - Developer.
 *
 *
 */
public class SquidGame extends JavaPlugin {
    private @Getter static SquidGame instance;
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    /** GSON instance with custom serializers & config */
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Location.class, new LocationSerializer())
            .registerTypeAdapter(Location[].class, LocationSerializer.getArraySerializer())
            .registerTypeAdapterFactory(RuntimeTypeAdapterFactory
                    .of(SquidParticipant.class, "type")
                    .registerSubtype(SquidPlayer.class)
                    .registerSubtype(SquidGuard.class))
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    private @Getter PaperCommandManager commandManager;

    private @Getter GameManager gManager;
    private @Getter DataManager dManager;
    private @Getter PlayerManager pManager;

    public static String prefix = Strings.format("&f&lSquid&d&lOtaku&f&lGame &7>> &r");

    @Override
    public void onEnable() {
        instance = this;

        this.commandManager = new PaperCommandManager(this);

        try {
            this.dManager = new DataManager(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.pManager = new PlayerManager(this);
        this.gManager = new GameManager(this);

        var globalListener = new GlobalListener(pManager, gManager);

        registerListeners(
                globalListener
        );

        Bukkit.getOnlinePlayers().forEach(p -> globalListener.getHasSeenCredits().put(p.getName(), false));

        registerCommands(commandManager,
                new TimerCMD()
        );

        Bukkit.getLogger().info(Strings.format(SquidGame.prefix + "&aEl plugin ha sido iniciado correctamente."));
    }

    @Override
    public void onDisable() {
        // Backup data onDisable
        this.dManager.save();

        this.gManager.getTimer().removePlayers();

        if (this.gManager.getGreenLightManager().getSensei() != null) this.gManager.getGreenLightManager().removeSensei();
        if (this.gManager.getPurgeManager().getFoodPlate() != null) this.gManager.getPurgeManager().removeFoodPlate();

        Bukkit.getWorlds().forEach(w -> w.setGameRule(GameRule.KEEP_INVENTORY, true));

        Bukkit.getLogger().info(Strings.format(SquidGame.prefix + "&aEl plugin ha sido desactivado correctamente."));
    }

    /**
     * A static method to access the Gson object globally.
     * 
     * @return gson with all adapter types registered.
     */
    public static Gson gson() {
        return gson;
    }

    /**
     * A static method to access the MiniMessage object globally.
     * 
     * @return MiniMessage object.
     */
    public static MiniMessage miniMessage() {
        return miniMessage;
    }

    public void registerCommands(PaperCommandManager manager, BaseCommand... commandExecutors) {
        for (BaseCommand commandExecutor : commandExecutors) {
            manager.registerCommand(commandExecutor);
        }
    }

    public void registerListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    public void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            Bukkit.getPluginManager().registerEvents(listener, this);
        }
    }

    public void unregisterListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

}
