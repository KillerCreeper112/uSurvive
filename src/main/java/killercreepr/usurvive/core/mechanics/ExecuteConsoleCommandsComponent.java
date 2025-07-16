package killercreepr.usurvive.core.mechanics;

import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.cruxitems.api.item.component.InteractableComponent;
import killercreepr.cruxitems.api.item.interaction.ItemUseContext;
import killercreepr.cruxitems.api.item.interaction.ItemUseResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ExecuteConsoleCommandsComponent implements InteractableComponent {
    protected final List<String> commands;

    public ExecuteConsoleCommandsComponent(List<String> commands) {
        this.commands = commands;
    }

    public List<String> getCommands() {
        return commands;
    }

    @Override
    public @NotNull ItemUseResult onInteract(@NotNull ItemUseContext ctx) {
        var tags = TagContainer.merged().hook(ctx.getPlayer());
        commands.forEach(s ->{
            s = Crux.format().deserializeString(s, tags);
            Crux.getServer().dispatchCommand(Crux.getServer().getConsoleSender(), s);
        });
        return ItemUseResult.empty();
    }

    @Override
    public boolean isInteractable(@NotNull ItemUseContext ctx) {
        return ctx.getAction().isRightClick();
    }
}
