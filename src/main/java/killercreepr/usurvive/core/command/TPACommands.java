package killercreepr.usurvive.core.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.component.parser.DataComponentDecoder;
import killercreepr.crux.api.text.tags.TagsPrefixBuilder;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.location.DynamicLocation;
import killercreepr.cruxfriends.api.friend.FriendsHolder;
import killercreepr.cruxfriends.core.CruxFriendsPlugin;
import killercreepr.cruxteleport.api.teleport.CruxTeleport;
import killercreepr.cruxteleport.api.teleport.CruxTeleporter;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.api.tpa.TPAHolder;
import killercreepr.usurvive.api.tpa.TPAManager;
import killercreepr.usurvive.api.tpa.TPARequest;
import killercreepr.usurvive.api.values.ValuesProvider;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.command.argument.TPARequestResolver;
import killercreepr.usurvive.core.command.argument.USurviveArgs;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class TPACommands {
    protected final @NotNull USurvivePlugin plugin;
    public TPACommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            commands.register(
                Commands.literal("tpa")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.tpa.use"))
                    .then(
                        Commands.argument("target", ArgumentTypes.player())
                            .executes(ctx -> {
                                if(!(getExecutor(ctx.getSource()) instanceof HumanEntity sender)) return -1;
                                HumanEntity target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                    .resolve(ctx.getSource()).getFirst();
                                if(target.equals(sender)){
                                    return -1;
                                }
                                return requestTPA(sender, target);
                            })
                    )
                    .build()
            );

            commands.register(
                Commands.literal("tpahere")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.tpahere.use"))
                    .then(
                        Commands.argument("target", ArgumentTypes.player())
                            .executes(ctx -> {
                                if(!(getExecutor(ctx.getSource()) instanceof HumanEntity sender)) return -1;
                                HumanEntity target = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                    .resolve(ctx.getSource()).getFirst();
                                if(target.equals(sender)){
                                    return -1;
                                }
                                return requestTPAHere(sender, target);
                            })
                    )
                    .build()
            );

            commands.register(
                Commands.literal("tpaccept")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.tpaccept.use"))
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Entity sender)) return -1;
                        return acceptTPA(sender);
                    })
                    .then(
                        Commands.argument("request", USurviveArgs.TPA_REQUEST)
                            .executes(ctx ->{
                                if(!(getExecutor(ctx.getSource()) instanceof Entity sender)) return -1;
                                TPARequest request = ctx.getArgument("request", TPARequestResolver.class)
                                    .resolve(ctx.getSource());
                                return acceptTPA(sender, request);
                            })
                    )
                    .build()
            );
            commands.register(
                Commands.literal("tpdeny")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.tpdeny.use"))
                    .executes(ctx ->{
                        if(!(getExecutor(ctx.getSource()) instanceof Entity sender)) return -1;
                        return denyTPA(sender);
                    })
                    .then(
                        Commands.argument("request", USurviveArgs.TPA_REQUEST)
                            .executes(ctx ->{
                                if(!(getExecutor(ctx.getSource()) instanceof Entity sender)) return -1;
                                TPARequest request = ctx.getArgument("request", TPARequestResolver.class)
                                    .resolve(ctx.getSource());
                                return denyTPA(sender, request);
                            })
                    )
                    .build(), List.of("tpadeny")
            );
        });
    }

    public int requestTPAHere(HumanEntity sender, HumanEntity to){
        TPAManager tpManager = plugin.getTPAManager();
        TPAHolder toHolder = tpManager.getTPAHolder(to);

        MergedTagContainer tags = TagContainer.merged()
            .hook(sender, TagsPrefixBuilder.overwriteBase("user_"))
            .hook(to, TagsPrefixBuilder.overwriteBase("target_"))
            ;

        if(toHolder != null && toHolder.getRequest(sender.getUniqueId()) != null){
            Lang.TPA_REQUEST_ALREADY_SENT.use(sender, tags);
            return 0;
        }

        PlayerSettingsHolder toSettings = plugin.getPlayerSettingsManager().getPlayerSettingsHolder(to);
        if(toSettings != null){
            if(toSettings.hasSetting(StandardPlayerSettings.TPA_HERE_REQUESTS, StandardPlayerSettingSelections.DISABLED)){
                Lang.TPA_HERE_REQUESTS_DISABLED.use(sender, tags);
                return 0;
            }
            if(toSettings.hasSetting(StandardPlayerSettings.TPA_HERE_REQUESTS, StandardPlayerSettingSelections.FRIENDS_ONLY)){
                FriendsHolder friends = CruxFriendsPlugin.inst().getFriendsManager().getFriendsHolder(to);
                if(friends == null || !friends.hasFriend(sender.getUniqueId())){
                    Lang.TPA_HERE_REQUESTS_DISABLED.use(sender, tags);
                    return 0;
                }
            }
        }

        ValuesProvider cfg = plugin.values();

        CruxTeleport.Builder builder= CruxTeleport.builder()
            .teleportTo(DynamicLocation.createEntity(sender));
        String components = cfg.TPA_TELEPORT_COMPONENTS().value();
        if(components != null){
            DataComponentDecoder.componentDecoder().parseComponents(components).forEach(builder::set);
        }

        TPARequest request = TPARequest.request(sender, builder);
        toHolder = tpManager.getOrCreateTPAHolder(to);
        toHolder.addRequest(request);

        Lang.TPA_HERE_REQUEST_SENT.use(sender, tags);
        Lang.TPA_HERE_REQUEST_SENT_RECEIVED.use(to, tags);
        return 1;
    }

    public int requestTPA(HumanEntity sender, HumanEntity to){
        TPAManager tpManager = plugin.getTPAManager();
        TPAHolder toHolder = tpManager.getTPAHolder(to);

        MergedTagContainer tags = TagContainer.merged()
            .hook(sender, TagsPrefixBuilder.overwriteBase("user_"))
            .hook(to, TagsPrefixBuilder.overwriteBase("target_"))
            ;

        if(toHolder != null && toHolder.getRequest(sender.getUniqueId()) != null){
            Lang.TPA_REQUEST_ALREADY_SENT.use(sender, tags);
            return 0;
        }

        PlayerSettingsHolder toSettings = plugin.getPlayerSettingsManager().getPlayerSettingsHolder(to);
        if(toSettings != null){
            if(toSettings.hasSetting(StandardPlayerSettings.TPA_REQUESTS, StandardPlayerSettingSelections.DISABLED)){
                Lang.TPA_REQUESTS_DISABLED.use(sender, tags);
                return 0;
            }
            if(toSettings.hasSetting(StandardPlayerSettings.TPA_REQUESTS, StandardPlayerSettingSelections.FRIENDS_ONLY)){
                FriendsHolder friends = CruxFriendsPlugin.inst().getFriendsManager().getFriendsHolder(to);
                if(friends == null || !friends.hasFriend(sender.getUniqueId())){
                    Lang.TPA_REQUESTS_DISABLED.use(sender, tags);
                    return 0;
                }
            }
        }

        ValuesProvider cfg = plugin.values();

        CruxTeleport.Builder builder= CruxTeleport.builder()
            .teleportTo(DynamicLocation.createEntity(to));
        String components = cfg.TPA_TELEPORT_COMPONENTS().value();
        if(components != null){
            DataComponentDecoder.componentDecoder().parseComponents(components).forEach(builder::set);
        }

        TPARequest request = TPARequest.request(sender, builder);
        toHolder = tpManager.getOrCreateTPAHolder(to);
        toHolder.addRequest(request);

        Lang.TPA_REQUEST_SENT.use(sender, tags);
        Lang.TPA_REQUEST_SENT_RECEIVED.use(to, tags);
        return 1;
    }

    public int acceptTPA(Entity sender){
        TPAManager tpManager = plugin.getTPAManager();
        TPAHolder holder = tpManager.getTPAHolder(sender);
        if(holder == null){
            Lang.TPA_NO_REQUESTS.use(sender);
            return 0;
        }
        Collection<TPARequest> list = holder.getRequests();
        if(list.isEmpty()){
            Lang.TPA_NO_REQUESTS.use(sender);
            return 0;
        }
        if(list.size() > 1){
            Lang.TPA_QUICK_ACCEPT_TOO_MANY_REQUESTS.use(sender);
            return 0;
        }
        for(TPARequest request : list){
            return acceptTPA(sender, request);
        }
        return 0;
    }

    public int acceptTPA(Entity sender, TPARequest request){
        MergedTagContainer tags = TagContainer.merged()
            .hook(request.sender())
            .hook(sender, TagsPrefixBuilder.overwriteBase("accepter_"))
            ;
        if(CruxTeleporter.teleporter().hasScheduledTeleport(request.sender().uuid())){
            Lang.TPA_CANNOT_ACCEPT_USER_TELEPORTING.use(sender, tags);
            return 0;
        }

        TPAManager tpManager = plugin.getTPAManager();
        TPAHolder holder = tpManager.getTPAHolder(sender);
        if(holder == null){
            Lang.TPA_NO_REQUESTS.use(sender, tags);
            return 0;
        }
        TPARequest removed = holder.removeRequest(request.sender().uuid());
        if(removed == null){
            Lang.TPA_NO_REQUEST_FROM_USER.use(sender, tags);
            return 0;
        }

        tpManager.acceptTPA(sender, removed);
        Lang.TPA_ACCEPTED.use(sender, tags);
        Entity e = removed.getSender();
        if(e != null){
            Lang.TPA_ACCEPTED_RECEIVED.use(e, tags);
        }
        return 1;
    }

    public int denyTPA(Entity sender){
        TPAManager tpManager = plugin.getTPAManager();
        TPAHolder holder = tpManager.getTPAHolder(sender);
        if(holder == null){
            Lang.TPA_NO_REQUESTS.use(sender);
            return 0;
        }
        Collection<TPARequest> list = holder.getRequests();
        if(list.isEmpty()){
            Lang.TPA_NO_REQUESTS.use(sender);
            return 0;
        }
        if(list.size() > 1){
            Lang.TPA_QUICK_DENY_TOO_MANY_REQUESTS.use(sender);
            return 0;
        }
        for(TPARequest request : list){
            return denyTPA(sender, request);
        }
        return 0;
    }

    public int denyTPA(Entity sender, TPARequest request){
        MergedTagContainer tags = TagContainer.merged()
            .hook(request.sender())
            .hook(sender, TagsPrefixBuilder.overwriteBase("accepter_"))
            ;

        TPAManager tpManager = plugin.getTPAManager();
        TPAHolder holder = tpManager.getTPAHolder(sender);
        if(holder == null){
            Lang.TPA_NO_REQUESTS.use(sender, tags);
            return 0;
        }
        TPARequest removed = holder.removeRequest(request.sender().uuid());
        if(removed == null){
            Lang.TPA_NO_REQUEST_FROM_USER.use(sender, tags);
            return 0;
        }

        Lang.TPA_DENIED.use(sender, tags);
        Entity e = removed.getSender();
        if(e != null){
            Lang.TPA_DENIED_RECEIVED.use(e, tags);
        }
        return 1;
    }

    public LiteralCommandNode<CommandSourceStack> build(LiteralArgumentBuilder<CommandSourceStack> dispatcher,
                                                        LifecycleEventManager<?> manager){
        return dispatcher.build();
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source){
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }
}
