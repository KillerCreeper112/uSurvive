package killercreepr.usurvive.core.command.argument;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import killercreepr.usurvive.api.tpa.TPARequest;

public interface TPARequestResolver {
    TPARequest resolve(CommandSourceStack source) throws CommandSyntaxException;
}
