package killercreepr.usurvive.core.command.argument;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import killercreepr.usurvive.api.tpa.TPAHolder;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.command.USurviveCommands;
import org.bukkit.entity.HumanEntity;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class TPARequestArgument implements CustomArgumentType.Converted<TPARequestResolver, String> {

    @Override
    public TPARequestResolver convert(String nativeType) throws CommandSyntaxException {
        return source ->{
            if(!(USurviveCommands.getExecutor(source) instanceof HumanEntity e)) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                .dispatcherParseException().create("Source not a player.");
            TPAHolder holder = USurvivePlugin.inst().getTPAManager().getTPAHolder(e);
            if(holder == null) throw CommandSyntaxException.BUILT_IN_EXCEPTIONS
                .dispatcherParseException().create("No teleport request found.");

            try{
                return holder.getRequest(UUID.fromString(nativeType));
            }catch (IllegalArgumentException ignored){}

            return holder.getRequest(nativeType);
        };
    }

    @Override
    public ArgumentType<String> getNativeType() {
        return StringArgumentType.string();
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        if(!(context.getSource() instanceof CommandSourceStack source)) return builder.buildFuture();
        if(!(USurviveCommands.getExecutor(source) instanceof HumanEntity e)) return builder.buildFuture();
        TPAHolder holder = USurvivePlugin.inst().getTPAManager().getTPAHolder(e);
        if(holder == null) return builder.buildFuture();
        holder.getRequests().forEach(tpa ->{
            builder.suggest(tpa.sender().name());
        });
        return builder.buildFuture();
    }
}
