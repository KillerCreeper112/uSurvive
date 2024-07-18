package killercreepr.usurvive.lang;

import killercreepr.crux.data.communication.Communicator;
import killercreepr.crux.data.communication.TranslateMsg;
import killercreepr.crux.tags.container.MergedTagContainer;
import net.kyori.adventure.audience.Audience;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Msg extends TranslateMsg implements Communicator {
    public Msg(@NotNull String id) {
        super(id);
    }

    public Msg(@NotNull String id, @Nullable Communicator defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public Communicator use(@NotNull Audience audience, @Nullable OfflinePlayer offlinePlayer, @Nullable MergedTagContainer mergedTagContainer) {
        return Lang.lang().use(id, audience, offlinePlayer, mergedTagContainer);
    }

    @Override
    public Communicator broadcast(@Nullable MergedTagContainer mergedTagContainer) {
        return Lang.lang().broadcast(id, mergedTagContainer);
    }
}
