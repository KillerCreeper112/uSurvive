package killercreepr.usurvive.core.mechanics;

import com.destroystokyo.paper.ParticleBuilder;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.crux.api.loot.item.ItemLootTable;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.util.CruxMath;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.block.Lidded;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EjectiveItemLootComponent {
  public static final Map<UUID, Collection<Task>> CURRENT_TASKS = new HashMap<>();

  public static void addTask(UUID uuid, Task task){
    CURRENT_TASKS.computeIfAbsent(uuid, k -> new ArrayList<>()).add(task);
  }
  public static void removeTask(UUID uuid, Task task){
    CURRENT_TASKS.computeIfPresent(uuid, (k, tasks) ->{
      tasks.remove(task);
      return tasks.isEmpty() ? null : tasks;
    });
  }


  public final List<ItemLootTable> loot;
  public final int ejectRate;

  public EjectiveItemLootComponent(List<ItemLootTable> loot, int ejectRate) {
    this.loot = loot;
    this.ejectRate = ejectRate;
  }

  public Task open(Block block, Player user, Consumer<Item> consumer){
    Location spawn = block.getLocation().toCenterLocation().subtract(0, .5, 0)
      .add(0, block.getBoundingBox().getHeight() + 0.05, 0);
    var ctx = LootContext.builder()
      .looted(block)
      .looter(user)
      .location(spawn)
      .build();
    return open(block, user, ctx, consumer);
  }

  public Task open(Block block, Player user, LootContext ctx, Consumer<Item> consumer){
    if(block.getState() instanceof Lidded cc){
      cc.open();
    }
    Location spawn = block.getLocation().toCenterLocation().subtract(0, .5, 0)
      .add(0, block.getBoundingBox().getHeight() + 0.05, 0);

    List<ItemStack> items = new ArrayList<>();
    loot.forEach(l -> items.addAll(l.populateLoot(ctx)));

    var p = user;
    var originalKey = Crux.handlers().block().getType(block);
    var task = new Task(
      user.getUniqueId(),
      this, items,
      t ->{
        var currentKey = Crux.handlers().block().getType(block);
      return currentKey.equals(originalKey);
      },
      5,
      spawn,
      consumer
    ){
      @Override
      public void stop() {
        super.stop();
        if(block.getState() instanceof Lidded cc){
          cc.close();
        }
      }

      @Override
      public void ejectNext() {
        super.ejectNext();
        p.playSound(spawn, Sound.BLOCK_VAULT_EJECT_ITEM, SoundCategory.BLOCKS, 0.5f, 1.5f);
      }
    };
    task.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);

    addTask(user.getUniqueId(), task);
    return task;
  }

  public static class Task extends BukkitRunnable {
    public final UUID user;
    public final EjectiveItemLootComponent data;
    public final List<ItemStack> loot;
    public final Predicate<Task> isValid;
    public final int checkValidRate;
    public final Location spawnLoc;
    public final Consumer<Item> spawnConsumer;
    public int currentIndex = -1;
    public int tick = 0;

    public Task(UUID user, EjectiveItemLootComponent data, List<ItemStack> loot, Predicate<Task> isValid, int checkValidRate, Location spawnLoc, Consumer<Item> spawnConsumer) {
      this.user = user;
      this.data = data;
      this.loot = loot;
      this.isValid = isValid;
      this.checkValidRate = checkValidRate;
      this.spawnLoc = spawnLoc;
      this.spawnConsumer = spawnConsumer;
    }

    public void onIsEmpty(){
      spawnCloudParticles();
    }

    public void spawnCloudParticles(){
      new ParticleBuilder(Particle.CLOUD)
        .location(spawnLoc)
        .count(CruxMath.random(6, 10))
        .offset(.3, .3, .3)
        .speed(.1)
        .spawn();
    }

    public void onNoLongerValid(){
      spawnCloudParticles();
    }

    public void ejectNext(){
      currentIndex++;
      var item =  loot.get(currentIndex);
      spawnLoc.getWorld().dropItem(spawnLoc, item, drop ->{
        drop.setCanMobPickup(false);
        drop.setOwner(user);
        drop.setVelocity(new Vector(0, 0.25, 0));
        if(spawnConsumer != null){
          spawnConsumer.accept(drop);
        }
      });
      new ParticleBuilder(Particle.SMOKE)
        .location(spawnLoc)
        .offset(.2, .2, .2)
        .speed(.05)
        .count(CruxMath.random(3, 4))
        .spawn();
    }

    public void onFinish(){

    }

    public void stop(){
      cancel();
      removeTask(user, this);
    }

    @Override
    public void run() {
      tick++;
      if(loot.isEmpty()){
        if(tick > 15){
          stop();
          onIsEmpty();
        }
        return;
      }
      if(tick % checkValidRate == 0){
        if(!isValid.test(this)){
          stop();
          onNoLongerValid();
          return;
        }
      }

      if(tick % data.ejectRate != 0) return;
      ejectNext();
      if(currentIndex+1 >= loot.size()){
        stop();
        onFinish();
      }
    }
  }
}
