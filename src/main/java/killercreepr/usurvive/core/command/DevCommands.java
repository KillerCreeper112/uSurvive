package killercreepr.usurvive.core.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.EntitySelectorArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.TextDataComponentDecoder;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.CruxTag;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.api.event.CruxAdvancementRewardEvent;
import killercreepr.cruxadvancements.core.entity.memory.AdvancementHolder;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxconfig.config.common.FileContext;
import killercreepr.cruxconfig.config.common.FileRegistry;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.cruxconfig.config.registry.CfgRegistries;
import killercreepr.cruxcore.CruxCore;
import killercreepr.cruxentities.command.argument.CruxEntitiesArguments;
import killercreepr.cruxentities.entity.CruxMob;
import killercreepr.cruxworlds.api.world.CruxWorld;
import killercreepr.cruxworlds.core.command.arguments.CruxWorldArgument;
import killercreepr.usurvive.api.story.StoryHolder;
import killercreepr.usurvive.api.story.StoryProgression;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.config.CfgHook;
import killercreepr.usurvive.core.entity.USurviveMobs;
import killercreepr.usurvive.core.entity.memory.NPCControllerHolder;
import killercreepr.usurvive.core.entity.mob.goals.NPCControlGoal;
import killercreepr.usurvive.core.guide.GuideHolder;
import killercreepr.usurvive.core.guide.progression.GuideProgression;
import killercreepr.usurvive.core.listener.EnchantsListener;
import killercreepr.usurvive.core.listener.MaintenanceListener;
import killercreepr.usurvive.core.registries.USurviveRegistries;
import killercreepr.usurvive.core.vanish.VanishHandler;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class DevCommands implements Listener {
    protected final @NotNull USurvivePlugin plugin;
    public DevCommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
    }

    public final Map<String, AdvanceCreator> advanceCreators = new MapBuilder<String, AdvanceCreator>()
        .put("killMob", new KillMobs())
        .put("harvestCrop", new HarvestCrop())
        .put("catchFish", new CatchFish())
        .put("breakBlock", new BreakBlock())
        .put("menuModule", new MenuModule())
        .build();

    protected final Collection<UUID> fix = new HashSet<>();
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCruxAdvancementReward(CruxAdvancementRewardEvent event) {
        if(fix.contains(event.getWho())){
            event.setCancelled(true);
        }
    }

    public static List<Key> getTrackTime(UUID uuid){
        CruxJson file = AdvancementHolder.getDefaultSaveFile(uuid);
        if(!(file.getElement("time_started") instanceof FileObject a)){
            file.close();
            return List.of();
        }
        file.close();

        List<Key> list = new ArrayList<>();
        a.keySet().forEach(id ->{
            var key = Crux.key(id);
            list.add(key);
        });
        return list;
    }

    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            //id=blaze,title="Blaze Buster",material=blaze_rod,amounts=[10, 25, 50, 100, 250, 500, 1000],levels=7
            //manager="usurvive:combat"
            commands.register(
                Commands.literal("udev")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.udev.use"))
                    .then(
                        Commands.literal("story")
                            .then(
                                Commands.literal("progress")
                                    .then(
                                        Commands.literal("clear")
                                            .then(
                                                Commands.argument("targets", ArgumentTypes.players())
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource());
                                                        targets.forEach(p ->{
                                                            StoryHolder holder = StoryHolder.storyHolder(p);
                                                            holder.getStoryData().clear();
                                                            sender.sendMessage("Cleared " + p.getName() + "'s completed story progress.");
                                                        });
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(
                                Commands.literal("complete")
                                    .then(
                                        Commands.argument("story_id", ArgumentTypes.key())
                                            .suggests((source, builder) ->{
                                                for (Key key : CfgHook.FILE_STORY_PROGRESSION.getHandlers().keySet()) {
                                                    builder.suggest(key.asString());
                                                }
                                                return builder.buildFuture();
                                            }).then(
                                                Commands.argument("targets", ArgumentTypes.players())
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource());
                                                        Key key = ctx.getArgument("story_id", Key.class);
                                                        targets.forEach(p ->{
                                                            StoryHolder holder = StoryHolder.storyHolder(p);
                                                            if(holder != null){
                                                                var got = holder.getStoryProgression(key);
                                                                if(got != null){
                                                                    holder.complete(got);
                                                                    sender.sendMessage("Completed " + key + " for " + p.getName());
                                                                }
                                                            }
                                                        });
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(
                                Commands.literal("set")
                                    .then(
                                        Commands.argument("targets", ArgumentTypes.players())
                                            .then(
                                                Commands.argument("story_id", ArgumentTypes.key())
                                                    .suggests((source, builder) ->{
                                                        for (Key key : CfgHook.FILE_STORY_PROGRESSION.getHandlers().keySet()) {
                                                            builder.suggest(key.asString());
                                                        }
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource());
                                                        targets.forEach(p ->{
                                                            StoryProgression progression = CfgHook.FILE_STORY_PROGRESSION.getHandlers()
                                                                .get(ctx.getArgument("guide_progress", Key.class))
                                                                .tempDataHandler()
                                                                .deserializeFromFile(new FileContext<>(CfgRegistries.JSON), new FileObject());
                                                            if(progression == null){
                                                                sender.sendMessage("Progression could not be loaded");
                                                                return;
                                                            }
                                                            StoryHolder holder = StoryHolder.storyHolder(p);
                                                            holder.addProgression(progression);
                                                            sender.sendMessage("Added " + p.getName() + " story progress to " + progression.key());
                                                        });
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )
                    /*.then(
                        Commands.literal("guide")
                            .then(
                                Commands.literal("progress")
                                    .then(
                                        Commands.literal("clear")
                                            .then(
                                                Commands.argument("targets", ArgumentTypes.players())
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource());
                                                        targets.forEach(p ->{
                                                            GuideHolder holder = EntityMemory.getDataHolder(p, GuideHolder.class);
                                                            holder.getCompleted().clear();
                                                            sender.sendMessage("Cleared " + p.getName() + "'s completed guide progress.");
                                                        });
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                            .then(
                                Commands.literal("completeAndClearCurrent")
                                    .then(
                                        Commands.argument("targets", ArgumentTypes.players())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource());
                                                targets.forEach(p ->{
                                                    GuideHolder holder = EntityMemory.getDataHolder(p, GuideHolder.class);
                                                    holder.completeAndClearCurrent();
                                                    sender.sendMessage("Completed and cleared " + p.getName() + "'s current guide progress.");
                                                });
                                                return 1;
                                            })
                                    )
                            )
                            .then(
                                Commands.literal("set")
                                    .then(
                                        Commands.argument("targets", ArgumentTypes.players())
                                            .then(
                                                Commands.argument("guide_progress", ArgumentTypes.key())
                                                    .suggests((source, builder) ->{
                                                        for (Key key : CfgHook.FILE_GUIDE_PROGRESSION.getHandlers().keySet()) {
                                                            builder.suggest(key.asString());
                                                        }
                                                        return builder.buildFuture();
                                                    })
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class)
                                                            .resolve(ctx.getSource());
                                                        targets.forEach(p ->{
                                                            GuideProgression progression = CfgHook.FILE_GUIDE_PROGRESSION.getHandlers()
                                                                .get(ctx.getArgument("guide_progress", Key.class))
                                                                .deserializeFromFile(new FileContext<>(CfgRegistries.JSON), new FileObject());
                                                            GuideHolder holder = EntityMemory.getDataHolder(p, GuideHolder.class);
                                                            holder.setCurrentProgression(progression);
                                                            sender.sendMessage("Set " + p.getName() + " guide progress to " + progression.key());
                                                        });
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )*/
                    .then(
                        Commands.literal("vanish")
                            .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.udev.vanish"))
                            .executes(ctx ->{
                                if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                                p.sendMessage("Vanish set to: " + VanishHandler.toggleVanish(p));
                                return 1;
                            })
                            .then(
                                Commands.literal("msg")
                                    .executes(ctx ->{
                                        if(!(getExecutor(ctx.getSource()) instanceof Player p)) return -1;
                                        p.sendMessage("Vanish set to: " + VanishHandler.toggleVanish(p));

                                        if(VanishHandler.isVanished(p)){
                                            Bukkit.broadcast(Crux.format().deserialize("<yellow><player_name> left the game", TagContainer.merged()
                                                .hook(p)));
                                        }else{
                                            Bukkit.broadcast(Crux.format().deserialize("<yellow><player_name> joined the game", TagContainer.merged()
                                                .hook(p)));
                                        }
                                        return 1;
                                    })
                            )
                    ).then(
                        Commands.literal("advancelostfix")
                            .then(
                                Commands.argument("target", ArgumentTypes.player())
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var p = ctx.getArgument("target", PlayerSelectorArgumentResolver.class)
                                                .resolve(ctx.getSource()).getFirst();

                                        List<Key> list = getTrackTime(p.getUniqueId());

                                        fix.add(p.getUniqueId());
                                        list.forEach(key ->{
                                            String value = key.value();
                                            Key managerKey = Crux.key(
                                                key.namespace() + ":" + value.split("/", 2)[0]
                                            );
                                            CruxAdvancementManager manager = AdvancementRegistries.ADVANCEMENT_MANAGERS.get(managerKey);
                                            if(manager == null){
                                                sender.sendMessage("Manager " + managerKey + " not found");
                                                return;
                                            }
                                            var advance = manager.getAdvancement(key);
                                            manager.grantAdvancement(p, advance);

                                        });
                                        fix.remove(p.getUniqueId());

                                        sender.sendMessage("Fixed advance " + p.getName() + " " + list.size());
                                        return 1;
                                    })
                            )
                    )
                    .then(
                        Commands.literal("maintenance")
                            .executes(ctx ->{
                                var sender = getExecutor(ctx.getSource());
                                MaintenanceListener.maintenance = !MaintenanceListener.maintenance;
                                sender.sendMessage("Maintenance: " + MaintenanceListener.maintenance);
                                return 1;
                            })
                    ).then(
                        Commands.literal("world")
                            .then(
                                Commands.literal("resource")
                                    .then(
                                        Commands.argument("world", new CruxWorldArgument(CruxCore.core().worldManager()))
                                            .then(
                                                Commands.literal("reset")
                                                    .executes(ctx ->{
                                                        CruxWorld world = ctx.getArgument("world", CruxWorld.class);
                                                        CruxTag.remove(world.toBukkitWorld(), "last_reset");
                                                        getExecutor(ctx.getSource())
                                                            .sendMessage(world.key() + " will reset next time it ticks");
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                    )
                    .then(
                        Commands.literal("etether")
                            .then(
                                Commands.argument("targets", ArgumentTypes.entities())
                                    .then(
                                        Commands.argument("level", IntegerArgumentType.integer())
                                            .executes(ctx ->{
                                                if(!(getExecutor(ctx.getSource()) instanceof Entity sender)) return -1;
                                                int level = ctx.getArgument("level", Integer.class);
                                                ctx.getArgument("targets", EntitySelectorArgumentResolver.class)
                                                    .resolve(ctx.getSource()).forEach(hit ->{
                                                        if(hit.equals(sender)) return;
                                                        EnchantsListener.onTether(sender, hit, level);
                                                    });
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .then(
                        Commands.literal("kickothers")
                            .then(
                                Commands.argument("reason", StringArgumentType.greedyString())
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        String reason = ctx.getArgument("reason", String.class);
                                        Component c = Crux.format().deserialize(reason);
                                        for(Player p : plugin.getServer().getOnlinePlayers()){
                                            if(p.equals(sender)) continue;
                                            p.kick(c);
                                        }
                                        return 1;
                                    })
                            )
                    )
                    .then(
                        Commands.literal("advancement")
                            .then(
                                Commands.literal("create")
                                    .then(
                                        Commands.argument("id", StringArgumentType.word())
                                            .suggests((ctx, builder) ->{
                                                for (String s : advanceCreators.keySet()) {
                                                    builder.suggest(s);
                                                }
                                                return builder.buildFuture();
                                            })
                                            .then(
                                                Commands.argument("text", StringArgumentType.greedyString())
                                                    .executes(ctx ->{
                                                        String text = ctx.getArgument("text", String.class);
                                                        var data = TextDataComponentDecoder.parseComponentMap(text);
                                                        AdvanceCreator creator = advanceCreators.get(ctx.getArgument("id", String.class));
                                                        creator.generateFile(data);
                                                        return 1;
                                                    })
                                            )
                                    )
                            ).then(
                                Commands.literal("prefix")
                                    .then(
                                        Commands.argument("replace", StringArgumentType.string())
                                            .then(
                                                Commands.argument("with", StringArgumentType.string())
                                                    .then(
                                                        Commands.argument("replace2", StringArgumentType.string())
                                                            .then(
                                                                Commands.argument("with2", StringArgumentType.string())
                                                                    .then(
                                                                        Commands.argument("path", StringArgumentType.string())
                                                                            .executes(ctx ->{
                                                                                String replace = ctx.getArgument("replace", String.class);
                                                                                String with = ctx.getArgument("with", String.class);
                                                                                String replace2 = ctx.getArgument("replace2", String.class);
                                                                                String with2 = ctx.getArgument("with2", String.class);
                                                                                String path = ctx.getArgument("path", String.class);

                                                                                File[] files = new CruxFolder(plugin, path).file().listFiles();
                                                                                for(File f : files){
                                                                                    String content = null;
                                                                                    try {
                                                                                        content = new String(Files.readAllBytes(f.toPath()));
                                                                                    } catch (IOException e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }

                                                                                    var jsonFile =new CruxJson(plugin, "dev/generated/prefixed/" + with + "/"
                                                                                        + CruxFolder.withoutFileExtension(f.getName()));
                                                                                    jsonFile.createNewFileIfNeeded();
                                                                                    jsonFile.close();
                                                                                    File newFile = jsonFile.file();

                                                                                    // Step 2: Replace text (simple string replacement)
                                                                                    content = content.replace(replace, with)
                                                                                        .replace(replace2, with2);

                                                                                    // Step 3: Optionally parse it to ensure it's still valid JSON
                                                                                    JsonElement jsonElement = JsonParser.parseString(content);

                                                                                    // Step 4: Write it back to the file
                                                                                    try (FileWriter writer = new FileWriter(newFile)) {
                                                                                        new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement, writer);
                                                                                    } catch (Exception e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }
                                                                                }
                                                                                return 1;
                                                                            })
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            ).then(
                                Commands.literal("replacein")
                                    .then(
                                        Commands.argument("path", StringArgumentType.string())
                                            .then(
                                                Commands.argument("args", StringArgumentType.greedyString())
                                                    .executes(ctx ->{
                                                        String text = ctx.getArgument("args", String.class);
                                                        var data = TextDataComponentDecoder.parseComponentMap(text);
                                                        getExecutor(ctx.getSource())
                                                            .sendMessage(data + "");

                                                        String path = ctx.getArgument("path", String.class);
                                                        File[] files = new CruxFolder(plugin, path).file().listFiles();
                                                        for(File f : files){
                                                            String content = null;
                                                            try {
                                                                content = new String(Files.readAllBytes(f.toPath()));
                                                            } catch (IOException e) {
                                                                throw new RuntimeException(e);
                                                            }

                                                            for(var entry : data.entrySet()){
                                                                if(entry.getValue() instanceof List<?> list){
                                                                    content = content.replace(list.getFirst().toString(), list.get(1).toString());
                                                                }
                                                            }
                                                            // Step 3: Optionally parse it to ensure it's still valid JSON
                                                            JsonElement jsonElement = JsonParser.parseString(content);

                                                            // Step 4: Write it back to the file
                                                            try (FileWriter writer = new FileWriter(f)) {
                                                                new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement, writer);
                                                            } catch (Exception e) {
                                                                throw new RuntimeException(e);
                                                            }
                                                        }

                                                        return 1;
                                                    })
                                            )
                                    )

                            )
                    )
                    .then(
                        Commands.literal("npc_control")
                            .then(
                                Commands.literal("tp")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Entity) sender;
                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                        holder.performAction("tp", ctx);
                                        return 1;
                                    })
                            ).then(
                                Commands.literal("rotate")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Entity) sender;
                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                        holder.performAction("rotate", ctx);
                                        return 1;
                                    })
                            ).then(
                                Commands.literal("tickPath")
                                    .then(
                                        Commands.argument("value", BoolArgumentType.bool())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var entity = (Entity) sender;
                                                NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                holder.performAction("tickPath", ctx);
                                                return 1;
                                            })
                                    )
                            ).then(
                                Commands.literal("tick")
                                    .then(
                                        Commands.argument("value", BoolArgumentType.bool())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var entity = (Entity) sender;
                                                NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                holder.performAction("tick", ctx);
                                                return 1;
                                            })
                                    )
                            ).then(
                                Commands.literal("naturalTarget")
                                    .then(
                                        Commands.argument("value", BoolArgumentType.bool())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var entity = (Entity) sender;
                                                NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                holder.performAction("naturalTarget", ctx);
                                                return 1;
                                            })
                                    )
                            ).then(
                                Commands.literal("addNode")
                                    .then(
                                        Commands.argument("value", ArgumentTypes.finePosition())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var entity = (Entity) sender;
                                                NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                holder.performAction("addNode", ctx);
                                                return 1;
                                            })
                                            .then(
                                                Commands.argument("distance", DoubleArgumentType.doubleArg())
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var entity = (Entity) sender;
                                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                        holder.performAction("addNode", ctx);
                                                        return 1;
                                                    })
                                            )
                                    )
                            ).then(
                                Commands.literal("setNode")
                                    .then(
                                        Commands.argument("value", ArgumentTypes.finePosition())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var entity = (Entity) sender;
                                                NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                holder.performAction("setNode", ctx);
                                                return 1;
                                            })
                                            .then(
                                                Commands.argument("distance", DoubleArgumentType.doubleArg())
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var entity = (Entity) sender;
                                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                        holder.performAction("setNode", ctx);
                                                        return 1;
                                                    })
                                            )
                                    )
                            ).then(
                                Commands.literal("clearNodes")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Entity) sender;
                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                        holder.performAction("clearNodes", ctx);
                                        return 1;
                                    })
                            ).then(
                                Commands.literal("select")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Player) sender;
                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                        if(entity.getTargetEntity(10) instanceof Mob mob){
                                            if(Crux.getServer().getMobGoals().getGoal(mob, NPCControlGoal.defaultKey()) instanceof NPCControlGoal g){
                                                holder.deselectNPCs();
                                                holder.addSelectNPC(g);
                                                sender.sendMessage("Selected " + mob.getName());
                                            }
                                        }
                                        return 1;
                                    })
                            ).then(
                                Commands.literal("selectAdd")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Player) sender;
                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                        if(entity.getTargetEntity(10) instanceof Mob mob){
                                            if(Crux.getServer().getMobGoals().getGoal(mob, NPCControlGoal.defaultKey()) instanceof NPCControlGoal g){
                                                holder.addSelectNPC(g);
                                                sender.sendMessage("Add selected " + mob.getName());
                                            }
                                        }
                                        return 1;
                                    })
                            ).then(
                                Commands.literal("clearSelection")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Entity) sender;
                                        var mem = EntityMemory.get(entity);
                                        if(mem != null){
                                            mem.getDataHolders().remove(NPCControllerHolder.KEY);
                                        }
                                        return 1;
                                    })
                            ).then(
                                Commands.literal("buildPath")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Entity) sender;
                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                        holder.performAction("buildPath", ctx);
                                        return 1;
                                    })
                            ).then(
                                Commands.literal("setEquipment")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Entity) sender;
                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                        holder.performAction("setEquipment", ctx);
                                        return 1;
                                    })
                            ).then(
                                Commands.literal("player")
                                    .then(
                                        Commands.argument("name", StringArgumentType.string())
                                            .then(
                                                Commands.argument("skinToUse", StringArgumentType.string())
                                                    .executes(ctx ->{
                                                        var sender = getExecutor(ctx.getSource());
                                                        var entity = (Entity) sender;
                                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                        holder.performAction("player", ctx);
                                                        return 1;
                                                    })
                                            )
                                    )
                            ).then(
                                Commands.literal("spawn")
                                    .executes(ctx ->{
                                        var sender = getExecutor(ctx.getSource());
                                        var entity = (Entity) sender;
                                        NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                        CruxMob cruxEntity = USurviveMobs.NPC_CONTROL;
                                        if(cruxEntity.spawn(entity.getLocation()) instanceof Mob mob){
                                            if(Crux.getServer().getMobGoals().getGoal(mob, NPCControlGoal.defaultKey()) instanceof NPCControlGoal g){
                                                holder.addSelectNPC(g);
                                                sender.sendMessage("Spawned and selected " + mob.getName());
                                            }
                                        }
                                        return 1;
                                    })
                                    .then(
                                        Commands.argument("entity", CruxEntitiesArguments.cruxMob())
                                            .executes(ctx ->{
                                                var sender = getExecutor(ctx.getSource());
                                                var entity = (Entity) sender;
                                                NPCControllerHolder holder = NPCControllerHolder.holder(entity);
                                                CruxMob cruxEntity = ctx.getArgument("entity", CruxMob.class);
                                                if(cruxEntity.spawn(entity.getLocation()) instanceof Mob mob){
                                                    if(Crux.getServer().getMobGoals().getGoal(mob, NPCControlGoal.defaultKey()) instanceof NPCControlGoal g){
                                                        holder.addSelectNPC(g);
                                                        sender.sendMessage("Spawned and selected " + mob.getName());
                                                    }
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build()
            );
        });
    }

    public static @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source){
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

    public interface AdvanceCreator{
        void generateFile(Map<String, Object> data);
    }

    public static List<Integer> amounts(Object o){
        List<Integer> list = new ArrayList<>();
        for(Object s : (List) o){
            list.add((int) CruxMath.evaluate(s.toString()));
        }
        return list;
    }

    //key=combat/kill/monster/,levels=7,slot=10,parent=2
    public static class MenuModule implements AdvanceCreator{

        @Override
        public void generateFile(Map<String, Object> data) {
            String key = data.get("key").toString();
            if(key.split(":", 2).length == 1){
                key = "usurvive:" + key;
            }
            //usurvive:combat/kill/monster/
            int levels = (int) CruxMath.evaluate(data.getOrDefault("levels", "7").toString());
            int slot = (int) CruxMath.evaluate(data.getOrDefault("slot", "10").toString());
            int parent = (int) CruxMath.evaluate(data.getOrDefault("parent", "2").toString());

            CruxConfig cfg = new CruxConfig(USurvivePlugin.inst(), "dev/generated/menu/modules");

            //- type: items_container
            //  base_items: quest_items
            //  base_item:
            //    slot: 11
            //    data:
            //      advancement: usurvive:combat/kill/monster/2
            FileArray a;
            if(cfg.getElement("modules") instanceof FileArray dd){
                a = dd;
            }else a = new FileArray();
            for(int i = 1; i <= levels; i++){
                FileObject o = new FileObject()
                    .addProperty("type", "items_container")
                    .addProperty("base_items", "quest_items")
                    .add("base_item",
                        new FileObject()
                            .addProperty("slot", slot)
                            .add("data",
                                new FileObject().addProperty("advancement", key + i)
                            )
                    );

                //  items:
                //    default:
                //      base: parent_requirement
                if(i >= parent){
                    o.add("items", new FileObject().add("default", new FileObject().addProperty("base", "parent_requirement")));
                }

                a.add(o);
                slot++;
            }

            cfg.serialize("modules", a);
            cfg.save();
        }
    }

    //id=blaze,title="Blaze Buster",material=blaze_rod,amounts=[10, 25, 50, 100, 250, 500, 1000],levels=7
    public static class KillMobs implements AdvanceCreator{
        protected final Map<Integer, Integer> orbits = Map.of(
            1, 5,
            2, 25,
            3, 70,
            4, 150,
            5, 250,
            6, 500,
            7, 1000
        );
        protected final Map<Integer, Integer> exp = Map.of(
            1, 10,
            2, 20,
            3, 35,
            4, 50,
            5, 65,
            6, 200,
            7, 400
        );
        @Override
        public void generateFile(Map<String, Object> data) {
            String id = data.get("id").toString();
            String title = data.getOrDefault("title", "none").toString();
            String material = data.getOrDefault("material", "stone").toString();
            List<Integer> amounts = amounts(data.get("amounts"));
            int levels = (int) CruxMath.evaluate(data.get("levels").toString());
            String entityType = data.getOrDefault("entity", id).toString();

            Key managerKey = Crux.key(data.getOrDefault("manager", "usurvive:combat").toString());


            int level = 0;
            for(int amount : amounts){
                level++;

                String x = data.getOrDefault("x", "1").toString();
                String y = data.getOrDefault("y", "0").toString();
                if(level == 1){
                    x = "1";
                    y = "0";
                }

                String advanceID = managerKey + "/" + id + "/" + level;

                String parent = level == 1 ? (managerKey.asString() + "/root") :
                    (managerKey.asString() + "/kill/" + id + "/" + (level-1));
                String objectiveAmount = amount + "";
                String entityPredicate = entityType;
                String criteriaRequirement = objectiveAmount;
                String titleName = title + " " + CruxMath.numeral(level);
                String materialID = material;
                String description = "Kill " + CruxMath.format(amount) + " " + addS(id);
                String visibilityType = level == 1 ? "vanilla" : "parent_granted";

                String orbits = data.getOrDefault("orbits", this.orbits.get(level) + "").toString();
                String exp = data.getOrDefault("exp", this.exp.get(level) + "").toString();

                String jsonText = String.format("""
                {
                  "parent": "%s",
                  "objectives": {
                    "a": {
                      "objective": {
                        "type": "kill_entity",
                        "amount": %s
                      },
                      "conditions": {
                        "condition": "entity",
                        "target": "looted",
                        "entity_predicate": "%s"
                      }
                    }
                  },
                  "update_advancement_period": 1,
                  "criteria": {
                    "criteria": "number",
                    "requirements": %s
                  },
                  "reward": {
                    "reward": "reward_package",
                    "rewards": [
                      {
                        "reward": "crux_currency",
                        "currency": "crux:orbit",
                        "amount": %s
                      },
                      {
                        "reward": "experience",
                        "amount": %s
                      }
                    ],
                    "display": {
                      "name": "%s",
                      "material": "%s"
                    }
                  },
                  "display": {
                    "position_origin": "%s",
                    "icon": {
                      "material": "%s"
                    },
                    "title": "%s",
                    "description": "%s",
                    "frame": "task",
                    "visibility": {
                      "type": "%s"
                    },
                    "x": %s,
                    "y": %s
                  }
                }
        """, parent, objectiveAmount, entityPredicate, criteriaRequirement, orbits, exp, titleName, materialID, parent, materialID, titleName, description, visibilityType,
                    x, y);

                Crux.logInfo(jsonText);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                var fileJson = new CruxJson(USurvivePlugin.inst(), "dev/generated/advancement/" + advanceID.replace(":", "/"), false);
                try {
                    // Ensure the directory exists
                    File file = fileJson.file();
                    file.getParentFile().mkdirs();

                    // Write to the file
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileJson.close();

                if(level >= levels){
                    break;
                }
            }
        }
    }

    public static String addS(String s){
        return addS(s, true);
    }

    public static String addS(String s, boolean formatUnderscore){
        if(formatUnderscore) s = s.replace("_", " ");
        if(s.endsWith("s")) return s;
        return s + "s";
    }

    public static class HarvestCrop implements AdvanceCreator{
        protected final Map<Integer, Integer> orbits = Map.of(
            1, 5,
            2, 20,
            3, 65,
            4, 140,
            5, 240,
            6, 480,
            7, 800
        );
        protected final Map<Integer, Integer> exp = Map.of(
            1, 10,
            2, 30,
            3, 50,
            4, 75,
            5, 100,
            6, 200,
            7, 350
        );
        @Override
        public void generateFile(Map<String, Object> data) {
            String id = data.get("id").toString();
            String title = data.getOrDefault("title", "none").toString();
            String material = data.getOrDefault("material", "stone").toString();
            List<Integer> amounts = amounts(data.get("amounts"));
            int levels = (int) CruxMath.evaluate(data.get("levels").toString());
            String block = data.getOrDefault("block", id).toString();

            Key managerKey = Crux.key(data.getOrDefault("manager", "usurvive:farming").toString());


            int level = 0;
            for(int amount : amounts){
                level++;
                String x = data.getOrDefault("x", "1").toString();
                String y = data.getOrDefault("y", "0").toString();
                if(level == 1){
                    x = "1";
                    y = "0";
                }
                String advanceID = managerKey + "/" + id + "/" + level;

                String parent = level == 1 ? (managerKey.asString() + "/root") :
                    (managerKey.asString() + "/harvest/" + id + "/" + (level-1));
                String objectiveAmount = amount + "";
                String blockPredicate = block;
                String criteriaRequirement = objectiveAmount;
                String titleName = title + " " + CruxMath.numeral(level);
                String materialID = material;
                String description = "Plant and harvest " + CruxMath.format(amount) + " " + addS(id);
                String visibilityType = level == 1 ? "vanilla" : "parent_granted";

                String orbits = data.getOrDefault("orbits", this.orbits.get(level) + "").toString();
                String exp = data.getOrDefault("exp", this.exp.get(level) + "").toString();

                String jsonText = String.format("""
                {
                  "parent": "%s",
                  "objectives": {
                    "a": {
                      "objective": {
                        "type": "break_block_drop",
                        "amount": %s
                      },
                      "conditions": {
                        "condition": "block_state",
                        "target": "block_state",
                        "block": "%s",
                        "age": "<block_state_data/ageable/max_age>"
                      }
                    },
                    "b": {
                      "objective": {
                        "type": "place_block",
                        "amount": %s
                      },
                      "conditions": {
                        "condition": "block",
                        "target": "looted",
                        "block_predicate": "%s"
                      }
                    }
                  },
                  "update_advancement_period": 5,
                  "criteria": {
                    "criteria": "number",
                    "requirements": %s
                  },
                  "reward": {
                    "reward": "reward_package",
                    "rewards": [
                      {
                        "reward": "crux_currency",
                        "currency": "crux:orbit",
                        "amount": %s
                      },
                      {
                        "reward": "experience",
                        "amount": %s
                      }
                    ],
                    "display": {
                      "name": "%s",
                      "material": "%s"
                    }
                  },
                  "display": {
                    "position_origin": "%s",
                    "icon": {
                      "material": "%s"
                    },
                    "title": "%s",
                    "description": "%s",
                    "frame": "task",
                    "visibility": {
                      "type": "%s"
                    },
                    "x": %s,
                    "y": %s
                  }
                }
        """, parent, objectiveAmount, blockPredicate, objectiveAmount, blockPredicate, (amount * 2)+"", orbits, exp, titleName, materialID,
                    parent, materialID, titleName, description, visibilityType,
                    x, y);

                Crux.logInfo(jsonText);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                var fileJson = new CruxJson(USurvivePlugin.inst(), "dev/generated/advancement/" + advanceID.replace(":", "/"), false);
                try {
                    // Ensure the directory exists
                    File file = fileJson.file();
                    file.getParentFile().mkdirs();

                    // Write to the file
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileJson.close();

                if(level >= levels){
                    break;
                }
            }
        }
    }

    public static class CatchFish implements AdvanceCreator{
        protected final Map<Integer, Integer> orbits = Map.of(
            1, 5,
            2, 25,
            3, 70,
            4, 150,
            5, 250,
            6, 500,
            7, 1000
        );
        protected final Map<Integer, Integer> exp = Map.of(
            1, 20,
            2, 40,
            3, 70,
            4, 100,
            5, 200,
            6, 400,
            7, 800
        );
        @Override
        public void generateFile(Map<String, Object> data) {
            String id = data.get("id").toString();
            String title = data.getOrDefault("title", "none").toString();
            String material = data.getOrDefault("material", id).toString();
            List<Integer> amounts = amounts(data.get("amounts"));
            int levels = (int) CruxMath.evaluate(data.get("levels").toString());
            String item = data.getOrDefault("item", id).toString();

            Key managerKey = Crux.key(data.getOrDefault("manager", "usurvive:fishing").toString());


            int level = 0;
            for(int amount : amounts){
                level++;

                String x = data.getOrDefault("x", "1").toString();
                String y = data.getOrDefault("y", "0").toString();
                if(level == 1){
                    x = "1";
                    y = "0";
                }

                String advanceID = managerKey + "/" + id + "/" + level;

                String parent = level == 1 ? (managerKey.asString() + "/root") :
                    (managerKey.asString() + "/catch/" + id + "/" + (level-1));
                String objectiveAmount = amount + "";
                String itemPredicate = item;
                String criteriaRequirement = objectiveAmount;
                String titleName = title + " " + CruxMath.numeral(level);
                String materialID = material;
                String description = "Catch " + CruxMath.format(amount) + " " + addS(id);
                String visibilityType = level == 1 ? "vanilla" : "parent_granted";

                String orbits = data.getOrDefault("orbits", this.orbits.get(level) + "").toString();
                String exp = data.getOrDefault("exp", this.exp.get(level) + "").toString();

                String jsonText = String.format("""
                {
                  "parent": "%s",
                  "objectives": {
                    "a": {
                      "objective": {
                        "type": "catch_fish",
                        "amount": %s
                      },
                      "conditions": {
                        "condition": "item",
                        "target": "looted",
                        "item_predicate": "%s"
                      }
                    }
                  },
                  "update_advancement_period": 1,
                  "criteria": {
                    "criteria": "number",
                    "requirements": %s
                  },
                  "reward": {
                    "reward": "reward_package",
                    "rewards": [
                      {
                        "reward": "crux_currency",
                        "currency": "crux:orbit",
                        "amount": %s
                      },
                      {
                        "reward": "experience",
                        "amount": %s
                      }
                    ],
                    "display": {
                      "name": "%s",
                      "material": "%s"
                    }
                  },
                  "display": {
                    "position_origin": "%s",
                    "icon": {
                      "material": "%s"
                    },
                    "title": "%s",
                    "description": "%s",
                    "frame": "task",
                    "visibility": {
                      "type": "%s"
                    },
                    "x": %s,
                    "y": %s
                  }
                }
        """, parent, objectiveAmount, itemPredicate, criteriaRequirement, orbits, exp, titleName, materialID, parent, materialID, titleName, description, visibilityType,
                    x, y);

                Crux.logInfo(jsonText);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                var fileJson = new CruxJson(USurvivePlugin.inst(), "dev/generated/advancement/" + advanceID.replace(":", "/"), false);
                try {
                    // Ensure the directory exists
                    File file = fileJson.file();
                    file.getParentFile().mkdirs();

                    // Write to the file
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileJson.close();

                if(level >= levels){
                    break;
                }
            }
        }
    }

    public static class BreakBlock implements AdvanceCreator{
        protected final Map<Integer, Integer> orbits = Map.of(
            1, 5,
            2, 25,
            3, 70,
            4, 150,
            5, 250,
            6, 500,
            7, 1000
        );
        protected final Map<Integer, Integer> exp = Map.of(
            1, 10,
            2, 20,
            3, 35,
            4, 50,
            5, 65,
            6, 200,
            7, 400
        );
        @Override
        public void generateFile(Map<String, Object> data) {
            String id = data.get("id").toString();
            String title = data.getOrDefault("title", "none").toString();
            String material = data.getOrDefault("material", id).toString();
            List<Integer> amounts = amounts(data.get("amounts"));
            int levels = (int) CruxMath.evaluate(data.get("levels").toString());
            String block = data.getOrDefault("block", id).toString();

            Key managerKey = Crux.key(data.getOrDefault("manager", "usurvive:mining").toString());

            int level = 0;
            for(int amount : amounts){
                level++;

                String x = data.getOrDefault("x", "1").toString();
                String y = data.getOrDefault("y", "0").toString();
                if(level == 1){
                    x = "1";
                    y = "0";
                }

                String advanceID = managerKey + "/" + id + "/" + level;

                String parent = level == 1 ? (managerKey.asString() + "/root") :
                    (managerKey.asString() + "/mine/" + id + "/" + (level-1));
                String objectiveAmount = amount + "";
                String blockPredicate = block;
                String criteriaRequirement = objectiveAmount;
                String titleName = title + " " + CruxMath.numeral(level);
                String materialID = material;
                String description = "Mine " + CruxMath.format(amount) + " " + addS(id);
                String visibilityType = level == 1 ? "vanilla" : "parent_granted";

                String orbits = data.getOrDefault("orbits", this.orbits.get(level) + "").toString();
                String exp = data.getOrDefault("exp", this.exp.get(level) + "").toString();

                //     "conditions": {
                //        "condition": "all_of",
                //        "terms": [
                //          {
                //            "condition": "block",
                //            "target": "looted",
                //            "block_predicate": "minecraft:acacia_log"
                //          },
                //          {
                //            "condition": "invert",
                //            "term": {
                //              "condition": "block_entity_placed",
                //              "target": "looted"
                //            }
                //          }
                //        ]
                //      }
                String jsonText = String.format("""
                {
                  "parent": "%s",
                  "objectives": {
                    "a": {
                      "objective": {
                        "type": "break_block",
                        "amount": %s
                      },
                      "conditions": {
                          "condition": "all_of",
                          "terms": [
                            {
                              "condition": "block",
                              "target": "looted",
                              "block_predicate": "%s"
                            },
                            {
                              "condition": "invert",
                              "term": {
                                "condition": "block_entity_placed",
                                "target": "looted"
                              }
                            }
                          ]
                        }
                    }
                  },
                  "update_advancement_period": 10,
                  "criteria": {
                    "criteria": "number",
                    "requirements": %s
                  },
                  "reward": {
                    "reward": "reward_package",
                    "rewards": [
                      {
                        "reward": "crux_currency",
                        "currency": "crux:orbit",
                        "amount": %s
                      },
                      {
                        "reward": "experience",
                        "amount": %s
                      }
                    ],
                    "display": {
                      "name": "%s",
                      "material": "%s"
                    }
                  },
                  "display": {
                    "position_origin": "%s",
                    "icon": {
                      "material": "%s"
                    },
                    "title": "%s",
                    "description": "%s",
                    "frame": "task",
                    "visibility": {
                      "type": "%s"
                    },
                    "x": %s,
                    "y": %s
                  }
                }
        """, parent, objectiveAmount, blockPredicate, criteriaRequirement, orbits, exp, titleName, materialID, parent, materialID, titleName, description, visibilityType,
                    x, y);

                Crux.logInfo(jsonText);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                var fileJson = new CruxJson(USurvivePlugin.inst(), "dev/generated/advancement/" + advanceID.replace(":", "/"), false);
                try {
                    // Ensure the directory exists
                    File file = fileJson.file();
                    file.getParentFile().mkdirs();

                    // Write to the file
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileJson.close();

                if(level >= levels){
                    break;
                }
            }
        }
    }
}
