package killercreepr.usurvive.core.block;

import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxEntityUtil;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.crux.core.util.GetEntityNear;
import killercreepr.cruxblocks.api.block.CruxBlock;
import killercreepr.cruxblocks.api.block.active.ActiveCruxTickedBlock;
import killercreepr.cruxblocks.api.block.context.BlockContext;
import killercreepr.cruxblocks.api.block.group.CruxBlockGroup;
import killercreepr.cruxblocks.core.block.active.standard.ActiveEntitySpawner;
import killercreepr.cruxblocks.core.block.component.CruxBlockComponents;
import killercreepr.cruxblocks.core.block.component.standard.EntitySpawnerComponent;
import killercreepr.cruxblocks.core.block.data.CustomBlockData;
import killercreepr.cruxblocks.core.registries.CruxBlocksRegistries;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawner;
import killercreepr.usurvive.core.component.impl.WatcherPillarComponent;
import killercreepr.usurvive.core.component.impl.WatcherPillarOpenComponent;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class ActiveWatcherPillar extends ActiveEntitySpawner implements ActiveCruxTickedBlock {
    protected final WatcherPillarComponent watcherData;
    protected WatcherPillarOpenComponent openData;
    protected final Location center;
    public ActiveWatcherPillar(@NotNull Block block, @NotNull CruxBlock cruxBlock, @NotNull EntitySpawnerComponent data, @NotNull NaturalEntitySpawner spawner, WatcherPillarComponent watcherData) {
        super(block, cruxBlock, data, spawner);
        this.watcherData = watcherData;
        this.openData = watcherData instanceof WatcherPillarOpenComponent d ? d : null;
        this.center = block.getLocation().toCenterLocation();
    }

    public boolean isOpen(){
        return watcherData.open;
    }

    @Override
    public void stopped() {
        super.stopped();
        if(!isValid()) return;
        save();
    }

    @Override
    public void started() {
        super.started();
        load();
        update();
    }

    protected boolean finishedNightTask = false;
    public void load(){
        CustomBlockData data = CustomBlockData.wrap(this.getBlock());
        finishedNightTask = data.get("finished_night_task", PersistentDataType.BOOLEAN, false);
        lastNightTick = data.get("last_night_tick", PersistentDataType.LONG, 0L);
    }

    public void save(){
        CustomBlockData data = CustomBlockData.wrap(this.getBlock());
        data.set("finished_night_task", PersistentDataType.BOOLEAN, finishedNightTask);
        data.set("last_night_tick", PersistentDataType.LONG, lastNightTick);
    }

    public CruxBlock getOtherBlock(){
        CruxBlockGroup group = getOtherBlockGroup();
        var thisDirection = cruxBlock.getComponents().get(CruxBlockComponents.DIRECTIONAL_BLOCK);
        if(thisDirection == null) return group.getBaseBlock();
        var otherDirection = group.getComponents().get(CruxBlockComponents.DIRECTIONAL_GROUP).getBlock(thisDirection.getDirection());
        if(otherDirection == null) return group.getBaseBlock();
        return otherDirection;
    }

    public CruxBlockGroup getOtherBlockGroup(){
        return CruxBlocksRegistries.BLOCK.getGroup(watcherData.otherBlockGroup);
    }

    public boolean changeToOtherBlockState(){
        CruxBlock block = getOtherBlock();
        if(cruxBlock.getTextureData().compareTexture(block.getTextureData())) return false;
        var gotted = block.setBlock(BlockContext.context(this.block, null), false, false);
        if(gotted != null){
            if(gotted instanceof ActiveWatcherPillar d){
                d.copyData(this);
            }
            if(watcherData.changeToOtherSound != null){
                Crux.scheduler().runTaskMain(() ->{
                    watcherData.changeToOtherSound.playAt(center);
                });
            }
        }
        return true;
    }

    @Override
    public void navigateSpawner() {
        super.navigateSpawner();
        waitTilSpawnToChange = false;
        actionsDoneThisNight++;

        if(actionsDoneThisNight < maxNightActions){
            changeInSeconds = 1;
            return;
        }
        finishedNightTask = true;
        changeToOtherBlockState();
    }

    protected long lastNightTick = -1;
    protected byte actionsDoneThisNight = 0;
    protected byte maxNightActions;
    protected byte changeInSeconds;
    protected boolean waitTilSpawnToChange;
    @Override
    public void tick() {
        if(!finishedNightTask && (isOpen() || (lastNightTick != -1 && openData != null))){
            if(!isOpen() && !waitTilSpawnToChange){
                if(this.delay < 30){
                    changeToOtherBlockState();
                    waitTilSpawnToChange = true;
                    return;
                }
            }
            spawnerTick();
        }

        if(Crux.getCurrentTick() % 20 != 0) return;
        if(changeInSeconds > 0){
            changeInSeconds--;
            if(changeInSeconds < 1){
                changeToOtherBlockState();
                return;
            }
        }

        World world = block.getWorld();
        if(world.isDayTime()){
            lastNightTick = -1;
            actionsDoneThisNight = 0;
            finishedNightTask = false;
            if(isOpen()){
                changeToOtherBlockState();
            }
            return;
        }

        long currentTime = world.getTime();
        if (lastNightTick == -1 || currentTime <= lastNightTick) {
            lastNightTick = currentTime+5;
            actionsDoneThisNight = 0;
            maxNightActions = watcherData.maxSpawnsPerNight.value().byteValue();
            finishedNightTask = false;
        }

        if(isOpen() || (lastNightTick != -1 && openData != null)){
            onOpenTick();
            return;
        }
        if(finishedNightTask || !CruxMath.testChance(watcherData.nightTimeOpenChance)) return;
        changeToOtherBlockState();
    }

    public void copyData(ActiveWatcherPillar from){
        this.actionsDoneThisNight = from.actionsDoneThisNight;
        this.finishedNightTask = from.finishedNightTask;
        this.maxNightActions = from.maxNightActions;
        this.lastNightTick = from.lastNightTick;
        if(from.openData != null) this.openData = from.openData;
        this.changeInSeconds = from.changeInSeconds;
        this.waitTilSpawnToChange = from.waitTilSpawnToChange;

        this.delay = from.delay;
    }

    public void onOpenTick(){
        if(openData.particle != null){
            openData.particle.build().location(center).spawn();
        }
        if(openData.potionEffectRange > 0f && openData.nearbyPotionEffects != null){
            Crux.scheduler().runTaskMain(() ->{
                new GetEntityNear<>(HumanEntity.class).range(openData.potionEffectRange)
                    .center(center)
                    .find().forEach(hit ->{
                        if(CruxEntityUtil.isNonSurvival(hit)) return;
                        hit.addPotionEffects(openData.nearbyPotionEffects);
                    });
            });
        }
    }
}