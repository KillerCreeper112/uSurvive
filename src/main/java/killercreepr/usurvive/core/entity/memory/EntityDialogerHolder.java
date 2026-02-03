package killercreepr.usurvive.core.entity.memory;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.crux.core.entity.memory.SimplePlayerMemoryMainThread;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxString;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.api.dialog.DialogTalker;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.dialog.DialogContentLinePair;
import killercreepr.usurvive.core.dialog.UserDialogPlayer;
import net.kyori.adventure.key.Key;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EntityDialogerHolder extends PlayerTickedDataHolder {
  public static EntityDialogerHolder holder(Entity p) {
    var mem = EntityMemory.getMain(p.getUniqueId());
    if (mem == null) return null;
    return mem.getDataHolder(EntityDialogerHolder.class);
    //return EntityMemory.getDataHolder(p, EntityDialogerHolder.class);
  }

  public static EntityDialogerHolder holderOrCreate(Entity e) {
    if (!(e instanceof Player p)) return null;

    var mem = EntityMemory.getMain(p.getUniqueId());
    if (mem == null) {
      mem = new SimplePlayerMemoryMainThread(p);
      EntityMemory.registerSilent(mem);
    }
    return mem.getDataHolderOrCompute(EntityDialogerHolder.class, mm -> {
      if (!(mm instanceof PlayerMemory m)) return null;
      return new EntityDialogerHolder(m);
    });

    /*return EntityMemory.getOrCreateDataHolder(p, EntityDialogerHolder.class, mem ->{
      if(!(mem instanceof PlayerMemory m)) return null;
      return new EntityDialogerHolder(m);
    });*/
  }

  public static final Key KEY = Crux.key("quest/entity_dialoger");

  public EntityDialogerHolder(@NotNull Key key, @NotNull PlayerMemory parent) {
    super(key, parent);
  }

  public EntityDialogerHolder(@NotNull PlayerMemory parent) {
    this(KEY, parent);
  }

  @Override
  public boolean shouldRemoveFromMemory(@Nullable Player e) {
    return super.shouldRemoveFromMemory(e) || (removeCount < 1 && entries.isEmpty());
  }

  @Override
  protected void removingFromMemory(@Nullable Player e) {
    super.removingFromMemory(e);
    Crux.scheduler().runTaskMain(this::cleanUp);
  }

  @Override
  public void onMemoryUnload(@NotNull Entity e) {
    super.onMemoryUnload(e);
    Crux.scheduler().runTaskMain(this::cleanUp);
  }

  public void cleanUp() {
    for (Entry value : entries.values()) {
      value.cleanUp();
    }
    entries.clear();
  }


  protected final Map<UUID, Entry> entries = new HashMap<>();

  protected Entry[] removeBuffer = new Entry[16];
  protected int removeCount = 0;

  protected void bufferRemove() {
    for (int i = 0; i < removeCount; i++) {
      var got = removeBuffer[i];
      if (got == null) continue;
      entries.remove(got.entityUUID);
      removeBuffer[i] = null;
      got.onRemoved();
    }
    removeCount = 0;
  }

  protected void addRemove(Entry holder) {
    if (removeCount == removeBuffer.length) {
      Entry[] newBuf =
        new Entry[removeBuffer.length * 2];
      System.arraycopy(removeBuffer, 0, newBuf, 0, removeBuffer.length);
      removeBuffer = newBuf;
    }
    removeBuffer[removeCount++] = holder;
  }

  @Override
  protected void onTick(@NotNull Player e) {
    super.onTick(e);

    removeCount = 0;

    for (Entry holder : entries.values()) {
      if (holder.shouldRemove()) {
        addRemove(holder);
        holder.cleanUp();
      }
      holder.tick(e);
    }
    bufferRemove();
  }

  public void addEntry(Entry entry, Consumer<DialogPlayer> dialogPlayer) {
    var current = entries.get(entry.entityUUID);
    if (current != null) {
      if (!current.shouldRemove()) {
        dialogPlayer.accept(current.dialogPlayer);
        return;
      }
    }
    var e = parent.value();
    if (e == null) return;

    var display = entry.displaySpawner.get();
    entry.display = new WeakReference<>(display);

    entries.put(entry.entityUUID, entry);
    entry.dialogPlayer.addAudience(e);
    dialogPlayer.accept(entry.dialogPlayer);
  }

  public Entry.Builder entryBuilder() {
    var builder = new Entry.Builder();
    builder.holder = this;
    return builder;
  }

  public Supplier<TextDisplay> defaultDisplaySpawner(Location loc, Player user) {
    return () -> spawnDefaultTextDisplay(loc, user);
  }

  public TextDisplay spawnDefaultTextDisplay(Location loc, Player user) {
    TextDisplay text = loc.getWorld().spawn(loc, TextDisplay.class, e -> {
      e.setPersistent(false);
      e.setSeeThrough(true);
      e.setBillboard(Display.Billboard.CENTER);
      e.setVisibleByDefault(false);
      user.showEntity(Crux.getMainPlugin(), e);
    });
    return text;
  }

  public Map<UUID, Entry> getEntries() {
    return entries;
  }

  public static class Entry {
    public static class Builder {
      public EntityDialogerHolder holder;
      public UUID entityUUID;
      public Reference<Entity> entity;
      public Reference<TextDisplay> display;
      public Supplier<TextDisplay> displaySpawner;
      public Runnable onRemoved;

      public Builder entity(Entity entity) {
        this.entityUUID = entity.getUniqueId();
        this.entity = new WeakReference<>(entity);
        return this;
      }

      public Builder display(TextDisplay display) {
        this.display = new WeakReference<>(display);
        return this;
      }

      public Builder onRemoved(Runnable runnable) {
        onRemoved = runnable;
        return this;
      }

      public Entry build() {
        var entry = new Entry(holder, entityUUID, entity, display, displaySpawner);
        entry.onRemoved = onRemoved;
        return entry;
      }
    }

    public final EntityDialogerHolder holder;
    public final UUID entityUUID;
    public final Reference<Entity> entity;
    public Reference<TextDisplay> display;
    public final Supplier<TextDisplay> displaySpawner;
    public Runnable onRemoved;

    public DialogPlayer dialogPlayer = new Dialoger() {
      @Override
      public Entity getTalker() {
        return entity.get();
      }

      @Override
      public void onDialogTicking() {
        super.onDialogTicking();
        var user = holder.parent.value();
        if (user == null) return;
        var talker = getTalker();
        if (talker == null) return;
        if (!user.getWorld().equals(talker.getWorld())) return;
        Mob mob = talker instanceof Mob m ? m : null;

        if (mob == null) return;
        if (getInfoOrDefault("look_at", (info, id) ->
          info.info().getOrDefault(id, Boolean.class, !mob.getPathfinder().hasPath()), !mob.getPathfinder().hasPath())) {
          mob.lookAt(user);
        }
      }

      @Override
      public void onDialogSpeaking() {
        super.onDialogSpeaking();
        var user = holder.parent.value();
        if (user == null) return;
        updateText(display.get(), currentDialogLineFor(user));
      }

      @Override
      public void onDialogEnd(Data data) {
        super.onDialogEnd(data);
        if (isEmpty()) {
          scheduleRemoval();
        }
      }
    };

    public Entry(EntityDialogerHolder holder, UUID entityUUID, Reference<Entity> entity, Reference<TextDisplay> display,
                 Supplier<TextDisplay> displaySpawner) {
      this.holder = holder;
      this.entityUUID = entityUUID;
      this.entity = entity;
      this.display = display;
      this.displaySpawner = displaySpawner;
    }

    public void onRemoved() {
      if (onRemoved != null) onRemoved.run();
    }

    public Location getDialogLocation() {
      var e = entity.get();
      if (e == null) return null;
      return e.getLocation().add(0, 0.5 + e.getHeight(), 0);
    }

    public void cleanUp() {
      var e = display.get();
      if (e == null) return;
      if (!e.isValid()) return;
      e.remove();
    }

    public void tick(Player p) {
      dialogPlayer.dialogTick();

      var display = this.display.get();
      if (display == null) return;
      var loc = getDialogLocation();
      if (loc == null) return;
      display.teleport(loc);
    }

    protected boolean scheduledRemove = false;

    public void scheduleRemoval() {
      scheduledRemove = true;
    }

    public boolean shouldRemove() {
      return scheduledRemove || !isEntityValid() || !isDisplayValid();
    }

    public boolean isEntityValid() {
      var e = entity.get();
      if (e == null) return false;
      return e.isValid();
    }

    public boolean isDisplayValid() {
      var e = display.get();
      if (e == null) return false;
      return e.isValid();
    }

    public void updateText(TextDisplay display, String msg) {
      if (display == null) return;
      if (msg == null || msg.isEmpty()) {
        display.text(null);
        return;
      }
      List<String> format = USurvivePlugin.inst().values().TEXT_DISPLAY_DIALOG_FORMAT().value();
      if (format == null) return;
      var tags = TagContainer.merged()
        .hook(new DialogContentLinePair(dialogPlayer.getDialog(), dialogPlayer.getCurrentLine()))
        .add(Tag.parsed("line", msg));

      tags.add(Tag.stringList("line", (args, ctx) -> {
        String lineFormat = USurvivePlugin.inst().values().TEXT_DISPLAY_DIALOG_FORMAT_LINE().value();
        if (lineFormat == null) return null;
        String msgToFormat = ctx.deserializeString(lineFormat, tags);
        List<String> list = new ArrayList<>();
        CruxString.buildDescription(
          msgToFormat, list::add,
          30
        );
        return list;
      }));

      List<String> formatted = Crux.format().deserializeStringList(format, tags);
      String finalMsg = String.join("\n", formatted);
      display.text(Crux.format().deserialize(finalMsg));
    }
  }

  public abstract static class Dialoger extends UserDialogPlayer implements DialogTalker {

  }
}
