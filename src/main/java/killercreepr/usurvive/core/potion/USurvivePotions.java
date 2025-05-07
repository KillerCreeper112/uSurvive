package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.ActivePotion;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.potions.DescribedPotion;
import killercreepr.cruxpotions.core.registries.CruxPotionRegistries;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class USurvivePotions {
    public static void register(){
    }
    public static final CruxPotion RECALL = register(new DescribedPotion(Crux.key("recall"),
        "Teleports you back to the location where you first drank it after it wears off." +
            " Recall I cannot teleport across worlds- doing so cancels the effect." +
            " Recall II allows cross-world teleportation.") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor potionInflictor) {
            return new RecallPotion(this, entity, duration, amplifier);
        }

        @Override
        public @Nullable Color getColor() {
            return Color.fromRGB(0xBD48FF);
        }
    });
    public static final CruxPotion WATERBOUND = register(new DescribedPotion(Crux.key("waterbound"),
        "Inverts your air supply- you suffocate on land and breathe underwater.") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor potionInflictor) {

            return new WaterboundPotion(this, entity, duration, amplifier);
        }

        @Override
        public @Nullable Color getColor() {
            return Color.fromRGB(0x80CCEA);
        }
    });
    public static final CruxPotion RAGE = register(new DescribedPotion(Crux.key("rage"),
        "EXPERIMENTAL! Causes the affected entity to lose control and attack nearby targets indiscriminately.") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor potionInflictor) {
            return new RagePotion(this, entity, duration, amplifier);
        }

        @Override
        public @Nullable Color getColor() {
            return Color.fromRGB(0xF43930);
        }
    });
    public static final CruxPotion GOOF = register(new DescribedPotion(Crux.key("goof"),
        "GOOF-Y") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new GoofPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x76C934);
        }
    });
    public static final CruxPotion ELUDING = register(new DescribedPotion(Crux.key("eluding"),
        "Reduces projectile damage and reflects incoming projectiles.") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new EludingPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x89D7D2);
        }
    });
    public static final CruxPotion ARCHITECT = register(new DescribedPotion(Crux.key("architect"),
        "Extends your block interaction range.") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new ArchitectPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xFF7B1C);
        }
    });
    public static final CruxPotion SHRINKAGE = register(new DescribedPotion(Crux.key("shrinkage"),
        "EXPERIMENTAL! Decreases the user's physical size.") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new ShrinkagePotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xFF5EFC);
        }
    });
    public static final CruxPotion ENLARGEMENT = register(new DescribedPotion(Crux.key("enlargement"),
        "EXPERIMENTAL! Increases the user's physical size.") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new EnlargementPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xF59101);
        }
    });
    public static final CruxPotion MAGNETISM = register(new DescribedPotion(Crux.key("magnetism"),
        "Attracts nearby dropped items toward the player.") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new MagnetismPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x17226B);
        }
    });

    private static CruxPotion register(CruxPotion pot){
        return CruxPotionRegistries.POTION.register(pot);
    }
}
