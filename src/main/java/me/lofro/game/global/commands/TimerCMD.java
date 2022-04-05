package me.lofro.game.global.commands;

import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import me.lofro.game.SquidGame;
import me.lofro.game.global.utils.Strings;
import org.bukkit.command.CommandSender;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import me.lofro.game.global.utils.timer.BukkitTimer;

@CommandAlias("timer")
@CommandPermission("admin.perm")
public class TimerCMD extends BaseCommand {

    private BukkitTimer bukkitTimer;

    @Subcommand("start")
    @CommandCompletion("seconds")
    public void startTimer(CommandSender sender, int time) {
        this.bukkitTimer = BukkitTimer.bTimer(time);

        bukkitTimer.addAllViewers();

        sender.sendMessage(Strings.format(SquidGame.prefix + "&bEl timer ha sido iniciado con éxito."));
        bukkitTimer.start().whenComplete((t, th) -> {
            if (th != null) {
                sender.sendMessage(Strings.format(SquidGame.prefix + "&cError " + th.getMessage()));
            } else {
                sender.sendMessage(Strings.format(SquidGame.prefix + "&bEl timer ha terminado de ejecutarse."));
            }

        });

    }

    @Subcommand("end")
    public void endTimer(CommandSender sender) {
        var notValidTimerMSG = Strings.format(SquidGame.prefix + "&cEl timer no está siendo ejecutado.");

        if (bukkitTimer == null) {
            sender.sendMessage(notValidTimerMSG);
            return;
        }

        if (bukkitTimer.isActive()) {
            bukkitTimer.end();
        } else {
            sender.sendMessage(notValidTimerMSG);
        }
    }


}
