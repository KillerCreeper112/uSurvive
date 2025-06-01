package killercreepr.usurvive.core.block;

import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxBlockUtil;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxInteractable;
import killercreepr.cruxblocks.api.block.active.ActiveCruxRedstonePowerable;
import killercreepr.cruxblocks.api.block.flag.BlockBreakFlag;
import killercreepr.cruxblocks.api.block.flag.BlockBreakFlags;
import killercreepr.cruxblocks.api.mining.user.Miner;
import killercreepr.cruxblocks.core.block.active.SimpleActiveCruxBlock;
import killercreepr.cruxblocks.core.mining.user.BlockMiner;
import killercreepr.cruxblocks.core.mining.user.EntityMiner;
import killercreepr.usurvive.core.entity.USurviveMobs;
import net.kyori.adventure.key.Key;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ActiveGustTNT extends SimpleActiveCruxBlock implements ActiveCruxRedstonePowerable, ActiveCruxInteractable {
    public ActiveGustTNT(@NotNull Block block, @NotNull CruxBlock cruxBlock) {
        super(block, cruxBlock);
    }

    @Override
    public boolean isRedstonePowerable() {
        return true;
    }

    @Override
    public void redstonePowerChanged(Block from, int newPower) {
        super.redstonePowerChanged(from, newPower);
        update();
    }

    @Override
    public void update() {
        super.update();
        if(!isValid()) return;
        var powered = CruxBlockUtil.findPowerSource(block);
        if(powered == null) return;
        explode(new BlockMiner(powered));
    }

    @Override
    public Event.Result interact(@NotNull PlayerInteractEvent event) {
        var result = super.interact(event);
        if(result != null) return result;

        if(!event.getAction().isRightClick()) return null;
        ItemStack item = event.getItem();
        if(CruxItem.isEmpty(item)) return null;
        if(!Crux.handlers().item().getType(item).equals(Key.key("flint_and_steel"))) return null;

        var p = event.getPlayer();
        if(p.getGameMode() != GameMode.CREATIVE){
            Crux.handlers().item().damageItem(item, 1, p);
        }
        explode(new EntityMiner(item, p, event.getHand()));

        return Event.Result.ALLOW;
    }

    public void explode(Miner miner){
        BlockData data = block.getBlockData().clone();

        breakBlock(miner, BlockBreakFlags.flags(BlockBreakFlag.DISABLE_DROPS, BlockBreakFlag.DISABLE_SOUND));

        Location loc = block.getLocation().toCenterLocation().subtract(0, 0.5, 0);
        CreateSound.sound(Sound.ENTITY_TNT_PRIMED, 0.7f, 1.4f).playAt(loc);

        USurviveMobs.GUST_TNT.spawn(loc, e ->{
            if(!(e instanceof TNTPrimed t)) return;
            t.setBlockData(data);
            if(miner instanceof EntityMiner m){
                t.setSource(m.getEntity());
            }
        });
    }
}