package killercreepr.usurvive.core.entity.memory;

import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.resolvers.FinePositionResolver;
import io.papermc.paper.math.FinePosition;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.math.CruxPosition;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.crux.core.entity.memory.EntityDataHolder;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalNode;
import killercreepr.cruxentities.api.entity.mob.goal.path.GoalPath;
import killercreepr.usurvive.core.command.DevCommands;
import killercreepr.usurvive.core.entity.mob.goals.NPCControlGoal;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class NPCControllerHolder extends EntityDataHolder implements Listener {
    public static NPCControllerHolder holder(Entity e){
        return EntityMemory.getOrCreateDataHolder(e, NPCControllerHolder.class, NPCControllerHolder::new);
    }
    public static final Key KEY = Crux.key("dev/npc_controler");
    public NPCControllerHolder(@NotNull Key key, @NotNull EntityMemory parent) {
        super(key, parent);
        Crux.getServer().getPluginManager().registerEvents(this, Crux.getMainPlugin());
    }
    public NPCControllerHolder(@NotNull EntityMemory parent) {
        this(KEY, parent);
    }

    @Override
    protected void removingFromMemory(@Nullable Entity e) {
        super.removingFromMemory(e);
        HandlerList.unregisterAll(this);
    }

    public final List<NPCControlGoal> selectedNPCs = new ArrayList<>();

    public void forEachSelectedNPC(Consumer<NPCControlGoal> consumer){
        selectedNPCs.forEach(consumer);
    }

    public void deselectNPCs(){
        selectedNPCs.clear();
    }

    public void selectNPC(NPCControlGoal goal){
        deselectNPCs();
        selectedNPCs.add(goal);
    }

    public void addSelectNPC(NPCControlGoal goal){
        selectedNPCs.add(goal);
    }

    public interface Action{
        void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx);
    }

    public void performAction(String id, CommandContext<CommandSourceStack> ctx){
        Action action = ACTIONS.get(id);
        var entity = (Player) DevCommands.getExecutor(ctx.getSource());
        NPCControllerHolder holder = NPCControllerHolder.holder(entity);

        action.perform(entity, holder, ctx);
    }

    public static final Map<String, Action> ACTIONS = new MapBuilder<String, Action>()
        .put("tp", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                holder.forEachSelectedNPC(goal ->{
                    goal.getMob().teleport(p);
                });
            }
        })
        .put("rotate", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                holder.forEachSelectedNPC(goal ->{
                    goal.getMob().setRotation(p.getYaw(), p.getPitch());
                });
            }
        })
        .put("player", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                holder.forEachSelectedNPC(goal ->{
                    var disguise = new PlayerDisguise(
                        ctx.getArgument("name", String.class),
                        ctx.getArgument("skinToUse", String.class)
                    );
                    disguise.setName("");
                    disguise.setNameVisible(false);

                    DisguiseAPI.disguiseEntity(goal.getMob(), disguise);
                });
            }
        })
        .put("tickPath", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                boolean value = ctx.getArgument("value", Boolean.class);
                holder.forEachSelectedNPC(goal ->{
                    goal.tickPath = value;
                });
            }
        })
        .put("setEquipment", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                holder.forEachSelectedNPC(goal ->{
                    for(EquipmentSlot slot : EquipmentSlot.values()){
                        if(!p.canUseEquipmentSlot(slot)) continue;
                        if(!goal.getMob().canUseEquipmentSlot(slot)) continue;
                        var equip = goal.getMob().getEquipment();
                        if(equip == null) continue;

                        ItemStack item = p.getInventory().getItem(slot);
                        equip.setItem(slot, item.clone(), true);
                    }
                });
            }
        })
        .put("tick", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                boolean value = ctx.getArgument("value", Boolean.class);
                holder.forEachSelectedNPC(goal ->{
                    goal.tick = value;
                });
            }
        })
        .put("buildPath", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                holder.forEachSelectedNPC(goal ->{
                    goal.setPath(GoalPath.goalPath(new ArrayList<>(goal.buildingPathNodes)));
                });
            }
        })
        .put("naturalTarget", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                boolean value = ctx.getArgument("value", Boolean.class);
                holder.forEachSelectedNPC(goal ->{
                    goal.naturalTarget = value;
                });
            }
        })
        .put("addNode", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                FinePosition value;
                try{
                    value = ctx.getArgument("value", FinePositionResolver.class)
                        .resolve(ctx.getSource());
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                double distance;
                try{
                    distance = ctx.getArgument("distance", Double.class);
                } catch (IllegalArgumentException e) {
                    distance = 1.5D;
                }
                GoalNode node = GoalNode.distanceGoalNode(CruxPosition.location(
                    value.x(),
                    value.y(),
                    value.z()
                ), distance);
                holder.forEachSelectedNPC(goal ->{
                    goal.buildingPathNodes.add(node);
                });
            }
        })
        .put("setNode", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                FinePosition value;
                try{
                    value = ctx.getArgument("value", FinePositionResolver.class)
                        .resolve(ctx.getSource());
                } catch (CommandSyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                double distance;
                try{
                    distance = ctx.getArgument("distance", Double.class);
                } catch (IllegalArgumentException e) {
                    distance = 1.5D;
                }
                GoalNode node = GoalNode.distanceGoalNode(CruxPosition.location(
                    value.x(),
                    value.y(),
                    value.z()
                ), distance);
                holder.forEachSelectedNPC(goal ->{
                    goal.buildingPathNodes.clear();
                    goal.buildingPathNodes.add(node);
                });
            }
        })
        .put("clearNodes", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                holder.forEachSelectedNPC(goal ->{
                    goal.buildingPathNodes.clear();
                });
            }
        })
        .build();
}
