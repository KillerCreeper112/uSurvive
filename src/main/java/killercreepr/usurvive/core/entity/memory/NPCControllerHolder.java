package killercreepr.usurvive.core.entity.memory;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.crux.core.entity.memory.EntityDataHolder;
import killercreepr.usurvive.core.command.DevCommands;
import killercreepr.usurvive.core.entity.mob.goals.NPCControlGoal;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
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
        .put("tickPath", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                boolean value = ctx.getArgument("value", Boolean.class);
                holder.forEachSelectedNPC(goal ->{
                    goal.tickPath = value;
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
        .put("naturalTarget", new Action() {
            @Override
            public void perform(Player p, NPCControllerHolder holder, CommandContext<CommandSourceStack> ctx) {
                boolean value = ctx.getArgument("value", Boolean.class);
                holder.forEachSelectedNPC(goal ->{
                    goal.naturalTarget = value;
                });
            }
        })
        .build();
}
