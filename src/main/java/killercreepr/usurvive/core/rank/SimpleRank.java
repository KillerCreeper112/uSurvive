package killercreepr.usurvive.core.rank;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.data.SimpleKeyed;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.rank.Rank;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SimpleRank extends SimpleKeyed implements Rank {
    protected final DataExchange info;
    protected final List<String> rankUpCommands;

    public SimpleRank(Key key, DataExchange info, List<String> rankUpCommands) {
        super(key);
        this.info = info;
        this.rankUpCommands = rankUpCommands;
    }

    @NotNull
    @Override
    public DataExchange info() {
        return info;
    }

    @Override
    public void onRankUp(Player p) {
        var tags = TagContainer.merged().hook(p)
            .add(Tag.parsed("rank", key.value()))
            .hookAllWithPrefix(info)
            ;
        if(rankUpCommands != null){
           for (String s : Crux.format().deserializeStringList(rankUpCommands, tags)) {
               Crux.getServer().dispatchCommand(Crux.getServer().getConsoleSender(), s);
           }
       }
    }
}
