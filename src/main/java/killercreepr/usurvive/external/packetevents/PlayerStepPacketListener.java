package killercreepr.usurvive.external.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.sound.SoundCategory;
import com.github.retrooper.packetevents.util.Vector3i;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSoundEffect;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.enchant.USurviveEnchant;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStepPacketListener implements PacketListener {
  @Override
  public void onPacketReceive(PacketReceiveEvent e) {
  }

  public final Map<UUID, Collection<Vector3i>> sending = new ConcurrentHashMap<>();
  @Override
  public void onPacketSend(PacketSendEvent e) {
    if(!(e.getPlayer() instanceof Player player)) return;
    if(e.getPacketType() != PacketType.Play.Server.SOUND_EFFECT) return;
    WrapperPlayServerSoundEffect event = new WrapperPlayServerSoundEffect(e);
    var soundCategory = event.getSoundCategory();
    if(soundCategory == null || !soundCategory.equals(SoundCategory.PLAYER)) return;
    Vector3i pos = event.getEffectPosition();

    var already = sending.get(player.getUniqueId());
    if(already != null){
      if(already.remove(pos)){
        if(already.isEmpty()) sending.remove(player.getUniqueId());
        return;
      }
    }

    e.setCancelled(true);
    Location location = new Location(player.getWorld(), (double) pos.getX() / 8, (double) pos.getY() / 8, (double) pos.getZ() / 8);

    Bukkit.getScheduler().runTask(USurvivePlugin.inst(), () -> {
      var entities = player.getWorld().getNearbyPlayers(location, 0.05, 0.05, 0.05, check -> !check.equals(player));
      Runnable resendTask = () -> {
        sending.computeIfAbsent(player.getUniqueId(), (uuid) -> new HashSet<>()).add(pos);
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, event);
      };
      if(entities.size() != 1){
        resendTask.run();
        return;
      }
      var source = CruxCollection.getFirst(entities);
      var boots = source.getInventory().getBoots();
      if(CruxItem.isEmpty(boots)){
        resendTask.run();
        return;
      }
      var softStep = boots.getEnchantmentLevel(USurviveEnchant.SOFT_STEP);
      if(softStep < 1){
        resendTask.run();
        return;
      }
      var volumeMultiplier = 1f - (softStep * 0.17f);
      if(volumeMultiplier <= 0f){
        return;
      }
      event.setVolume(event.getVolume() * volumeMultiplier);
      resendTask.run();
    });
  }


}
