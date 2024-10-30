package killercreepr.usurvive.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import killercreepr.usurvive.death.PlayerDeath;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface TargetPlayerDeathResolver {
    @NotNull
    PlayerDeath resolve(@NotNull CommandSourceStack source, @NotNull Player target) throws CommandSyntaxException;
}
