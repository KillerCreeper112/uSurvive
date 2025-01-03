package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.HookedObjectContainer;
import killercreepr.crux.api.text.hook.HookedPrefixBuilder;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.core.text.hook.StringHookedObjectTag;
import killercreepr.crux.core.text.hook.StringListHookedObjectTag;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingsHolder;
import killercreepr.usurvive.core.data.entity.MemoryPlayerSettingsHolder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerTags implements ObjectTag<Player> {
    @NotNull
    @Override
    public Class<Player> getObjectType() {
        return Player.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("player_");
    }

    @Override
    public @Nullable HookedObjectContainer<StringHookedObjectTag<?>> hookStrings(@NotNull Player object, @NotNull TagParser tags) {
        var c = HookedObjectContainer.string();
        PlayerSettingsHolder holder = EntityMemory.getOrCreateDataHolder(object, MemoryPlayerSettingsHolder.class);
        if(holder != null){
            c.addAll(tags.hookStrings(holder, HookedPrefixBuilder.overwrite(FormatPrefix.simple(
                "player_settings/"
            ))));
        }
        return c;
    }

    @Nullable
    @Override
    public HookedObjectContainer<StringListHookedObjectTag<?>> hookStringLists(@NotNull Player object, @NotNull TagParser tags) {
        var c = HookedObjectContainer.stringList();
        PlayerSettingsHolder holder = EntityMemory.getOrCreateDataHolder(object, MemoryPlayerSettingsHolder.class);
        if(holder != null){
            c.addAll(tags.hookStringLists(holder, HookedPrefixBuilder.overwrite(FormatPrefix.simple(
                "player_settings/"
            ))));
        }
        return c;
    }
}
