package killercreepr.usurvive.core.enchant.logic;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.core.util.CruxEntityUtil;
import net.minecraft.network.protocol.game.ClientboundPlayerPositionPacket;
import net.minecraft.world.entity.PositionMoveRotation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class JoyWhirl {
    public static final Map<UUID, JoyWhirl> ACTIVE = new HashMap<>();

    protected final Plugin plugin;
    protected final UUID uuid;
    protected final Entity target;
    public JoyWhirl(Plugin plugin, Entity target){
        this.plugin = plugin;
        this.uuid = target.getUniqueId();
        this.target = target;
    }

    public void start(int level) {
        ACTIVE.put(uuid, this);
        target.setVelocity(new Vector(0, (level * .45), 0));
        final float speed = (level * 10);

        target.getWorld().playSound(target, Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1.7f);
        if(target instanceof Player p){
            p.playSound(p, Sound.ITEM_ELYTRA_FLYING, 1f, 1f);
        }

        final int maxTicks = 20 + ((level-1) * 20);

        new BukkitRunnable() {
            int tick = 0;
            @Override
            public void run() {
                tick++;
                if(tick >= maxTicks){
                    cancel();
                    stop();
                    return;
                }
                if(tick > 5){
                    if(!target.isValid() || CruxEntityUtil.getBlockStandingOn(target) != null){
                        cancel();
                        stop();
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

    public void stop(){
        ACTIVE.remove(uuid);
    }

    public static void setRotation(Player p, float yaw, float pitch){
        /*Location loc = new Location(
            p.getWorld(), 0D, 0D, 0D, yaw, pitch
        );
        p.teleport(
            loc, TeleportFlag.Relative.values()
        );*/
        Set<Relative> flags = new HashSet<>();
        flags.add(Relative.X);
        flags.add(Relative.Y);
        flags.add(Relative.Z);
        flags.add(Relative.Y_ROT);
        flags.add(Relative.X_ROT);
        ClientboundPlayerPositionPacket packet = new ClientboundPlayerPositionPacket(0, new PositionMoveRotation(Vec3.ZERO, Vec3.ZERO, yaw, pitch), flags);

        ((CraftPlayer) p).getHandle().connection.send(packet);
    }
}
