package killercreepr.usurvive.core.entity.player;

import killercreepr.crux.api.math.CruxLocation;
import killercreepr.crux.api.text.provider.StringTagProvider;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.cruxconfig.config.bukkit.file.BukkitDataFile;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxconfig.config.common.file.DataFile;
import killercreepr.cruxfriends.api.friend.FriendsHolder;
import killercreepr.cruxfriends.core.CruxFriendsPlugin;
import killercreepr.cruxparty.api.party.Party;
import killercreepr.cruxparty.api.party.PartyHolder;
import killercreepr.cruxparty.core.CruxPartyPlugin;
import killercreepr.usurvive.api.entity.player.ShopSpender;
import killercreepr.usurvive.api.entity.player.UPlayer;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.data.PremiumRank;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

public class DataUPlayer implements UPlayer, ShopSpender {
    protected final @NotNull UUID uuid;
    protected long lastLogout;
    protected CruxLocation lastKnownPosition;
    protected Key lastKnownWorld;
    protected String lastKnownName;
    protected double totalPaid;

    public DataUPlayer(@NotNull UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getLastKnownName() {
        return lastKnownName;
    }

    @NotNull
    @Override
    public Collection<UUID> getFriends() {
        FriendsHolder friendsHolder = CruxFriendsPlugin.inst().getFriendsManager().getFriendsHolder(this.uuid);
        if(friendsHolder == null) return Set.of();
        Collection<UUID> friends = new HashSet<>();
        friendsHolder.getFriends().forEach(f -> friends.add(f.user().uuid()));
        return friends;
    }

    @NotNull
    @Override
    public Collection<UUID> getPartyMembers() {
        PartyHolder partyHolder = CruxPartyPlugin.inst().getPartyManager().getPartyHolder(this.uuid);
        if(partyHolder == null) return Set.of();
        Party party = partyHolder.getParty();
        if(party == null) return Set.of();
        return party.members();
    }

    @Override
    public void setLastKnownName(String lastKnownName) {
        this.lastKnownName = lastKnownName;
    }

    @Override
    public boolean hasFriend(@NotNull UUID uuid) {
        FriendsHolder friendsHolder = CruxFriendsPlugin.inst().getFriendsManager().getFriendsHolder(this.uuid);
        if(friendsHolder == null) return false;
        return friendsHolder.hasFriend(uuid);
    }

    @Override
    public boolean isApartOfParty(@NotNull UUID uuid) {
        PartyHolder partyHolder = CruxPartyPlugin.inst().getPartyManager().getPartyHolder(this.uuid);
        if(partyHolder == null) return false;
        Party party = partyHolder.getParty();
        if(party == null) return false;
        return party.owner().equals(uuid) || party.hasMember(uuid);
    }

    @Override
    public boolean isOnline() {
        return Crux.getServer().getPlayer(uuid) != null;
    }

    @NotNull
    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public long getLastLogout() {
        return lastLogout;
    }

    @Nullable
    @Override
    public CruxLocation getLastKnownPosition() {
        return lastKnownPosition;
    }

    @Nullable
    @Override
    public Key getLastKnownWorld() {
        return lastKnownWorld;
    }

    @Override
    public void setLastLogout(long time) {
        this.lastLogout = time;
    }

    @Override
    public void setLastKnownPosition(@Nullable CruxLocation pos) {
        this.lastKnownPosition = pos;
    }

    @Override
    public void setLastKnownWorld(@Nullable Key name) {
        this.lastKnownWorld = name;
    }

    @Override
    public void save() {
        save(Crux.getMainPlugin());
    }

    @Override
    public void load() {
        load(Crux.getMainPlugin());
    }

    @Override
    public void save(@NotNull Plugin plugin) {
        DataFile file = getOrCreateDataFile(plugin, uuid);
        if(file == null) return;
        file.serialize("last_known_name", lastKnownName);
        file.serialize("last_known_location", lastKnownPosition);
        file.serialize("last_known_location_world", lastKnownWorld);
        file.serialize("last_logout", lastLogout);
        file.serialize("total_premium_paid", totalPaid);
        file.save();
    }

    @Override
    public void load(@NotNull Plugin plugin) {
        DataFile file = getDataFile(plugin, uuid);
        if(file == null) return;
        lastKnownName = file.deserialize("last_known_name", String.class);
        lastKnownPosition = file.deserialize("last_known_location", CruxLocation.class);
        lastKnownWorld = file.deserialize("last_known_location_world", Key.class);
        Long n = file.deserialize("last_logout", Long.class);
        if(n != null) lastLogout = n;
        Double totalPaid = file.deserialize("total_premium_paid", Double.class);
        if(totalPaid != null) this.totalPaid = totalPaid;
        file.close();
    }

    public DataFile getDataFile(Plugin plugin, UUID p){
        File f = CruxFolder.file(plugin, "data/usurvive/player/" + p + ".json");
        if(BukkitDataFile.isSupported(f)) return BukkitDataFile.parse(f);
        return null;
    }

    public DataFile getOrCreateDataFile(Plugin plugin, UUID p){
        File f = CruxFolder.file(plugin, "data/usurvive/player/" + p + ".json");
        if(!f.exists()){
            CruxJson cfg = new CruxJson(f);
            cfg.reloadIfNeeded();
            cfg.close();
        }
        if(BukkitDataFile.isSupported(f)) return BukkitDataFile.parse(f);
        return null;
    }

    @Override
    public double getTotalPayed() {
        return totalPaid;
    }

    @Override
    public void addTotalPayed(double amount) {
        this.totalPaid += amount;
    }

    @Override
    public void setTotalPayed(double amount) {
        this.totalPaid = amount;
    }

    @Override
    public void onPurchase(double spent, String reason) {
        addTotalPayed(spent);
        Player p = Crux.getServer().getPlayer(uuid);
        if(p == null) return;
        List<PremiumRank> ranks = USurvivePlugin.inst().values().PREMIUM_RANKS().value();
        if(ranks == null || ranks.isEmpty()) return;

        for(PremiumRank rank : ranks){
            if(totalPaid < rank.getCost()) continue;
            if(p.hasPermission(rank.getPermission())) continue;

            List<String> cmds = rank.getReachedCommands();
            if(cmds != null){
                StringTagProvider tags = TagContainer.string().hook(p);
                cmds.forEach(cc ->{
                    cc = Crux.format().deserializeString(cc, tags);
                    Crux.getServer().dispatchCommand(Crux.getServer().getConsoleSender(), cc);
                });
            }
        }
    }
}
