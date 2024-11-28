package killercreepr.usurvive.enchant.logic;

import com.destroystokyo.paper.ParticleBuilder;
import io.papermc.paper.entity.TeleportFlag;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class JoyWhirl {
    protected final Plugin plugin;
    protected final Entity target;
    public JoyWhirl(Plugin plugin, Entity target){
        this.plugin = plugin;
        this.target = target;
    }

    public void start(int level) {
        target.setVelocity(new Vector(0, (level * .6), 0));
        final float speed = (level * 10);

        target.getWorld().playSound(target, Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.7f);
        if(target instanceof Player p){
            p.playSound(p, Sound.ITEM_ELYTRA_FLYING, 1f, 1f);
        }

        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                tick++;
                if(tick > 5){
                    if(!target.isValid() || target.getLocation().subtract(0, .5, 0).getBlock().isSolid()){
                        cancel();
                        if(target instanceof Player p) p.stopSound(Sound.ITEM_ELYTRA_FLYING);
                        return;
                    }
                }
                if(target instanceof Player p){
                    setRotation(p, speed, 0f);
                }else{
                    target.setRotation(target.getLocation().getYaw() + (60f + speed), 0f);
                }
                new ParticleBuilder(Particle.DUST)
                    .offset(.5, .5, .5)
                    .data(new Particle.DustTransition(Color.fromRGB(0x65C9FF), Color.fromRGB(0x659BFF), 1f))
                    .count(3)
                    .location(target.getLocation())
                    .spawn()
                ;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void setRotation(Player p, float yaw, float pitch){
        Location loc = new Location(
            p.getWorld(), 0D, 0D, 0D, yaw, pitch
        );
        p.teleport(
            loc, TeleportFlag.Relative.values()
        );
    /*    Set<ClientboundPlayerPositionPacket.RelativeArgument> flags = new HashSet<>();
        flags.add(ClientboundPlayerPositionPacket.RelativeArgument.X);
        flags.add(ClientboundPlayerPositionPacket.RelativeArgument.Y);
        flags.add(ClientboundPlayerPositionPacket.RelativeArgument.Z);
        flags.add(ClientboundPlayerPositionPacket.RelativeArgument.Y_ROT);
        flags.add(ClientboundPlayerPositionPacket.RelativeArgument.X_ROT);
        ClientboundPlayerPositionPacket packet = new ClientboundPlayerPositionPacket(0, 0, 0, yaw, pitch, flags, 0, false);

        ((CraftPlayer) p).getHandle().connection.send(packet);*/
    }
}
