package killercreepr.usurvive.core.entity.player;

import killercreepr.crux.api.math.CruxLocation;
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
import killercreepr.usurvive.api.entity.player.UPlayer;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataUPlayer implements UPlayer {
    protected final @NotNull UUID uuid;
    protected long lastLogout;
    protected CruxLocation lastKnownPosition;
    protected String lastKnownWorldName;
    protected String lastKnownName;

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
    public String getLastKnownWorld() {
        return lastKnownWorldName;
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
    public void setLastKnownWorld(@Nullable String name) {
        this.lastKnownWorldName = name;
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
        file.serialize("last_known_location_world_name", lastKnownWorldName);
        file.serialize("last_logout", lastLogout);
        file.save();
    }

    @Override
    public void load(@NotNull Plugin plugin) {
        DataFile file = getDataFile(plugin, uuid);
        if(file == null) return;
        lastKnownName = file.deserialize("last_known_name", String.class);
        lastKnownPosition = file.deserialize("last_known_location", CruxLocation.class);
        lastKnownWorldName = file.deserialize("last_known_location_world_name", String.class);
        Number n = file.deserialize("last_logout", Number.class);
        if(n != null) lastLogout = n.longValue();
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
}
