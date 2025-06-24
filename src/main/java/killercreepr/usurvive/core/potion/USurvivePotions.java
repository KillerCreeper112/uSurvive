package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.ActivePotion;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.potions.DescribedPotion;
import killercreepr.cruxpotions.core.registries.CruxPotionRegistries;
import org.bukkit.Color;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class USurvivePotions {
    public static void register(){
    }
    public static final CruxPotion RECALL = register(new DescribedPotion(Crux.key("recall"),
        "Teleports the user back to the location where they first drank it after it wears off." +
            " Recall I cannot teleport across worlds- doing so cancels the effect." +
            " Recall II allows cross-world teleportation") {
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
        "Inverts the user's air supply, causing them to suffocate on land and breathe underwater") {
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
        "EXPERIMENTAL! Causes the user to lose control and attack nearby targets indiscriminately") {
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
        "Reduces projectile damage and reflects incoming projectiles") {
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
        "Extends the user's block interaction range") {
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
        "EXPERIMENTAL! Decreases the user's physical size") {
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
        "EXPERIMENTAL! Increases the user's physical size") {
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
        "Attracts nearby dropped items toward the user") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new MagnetismPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x17226B);
        }
    });
    public static final CruxPotion DISORIENTATION = register(new DescribedPotion(Crux.key("disorientation"),
        "Causes the user's head to twitch") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new DisorientationPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x7A00FF);
        }
    });
    public static final CruxPotion SCRAMBLE = register(new DescribedPotion(Crux.key("scramble"),
        "Your items slip through your fingers as if possessed by another will") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new ScramblePotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xFFD700);
        }
    });
    public static final CruxPotion DEAFENED = register(new DescribedPotion(Crux.key("deafened"),
        "Prevents the user from hearing clearly") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new DeafenedPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x1F1F1F);
        }
    });
    public static final CruxPotion LANDING_SLOW_FALLING = register(new DescribedPotion(Crux.key("landing_slow_falling"),
        "Provides slow falling until the user lands on the ground") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new LandingSlowFallingPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xF3CFB9);
        }
    });
    public static final CruxPotion SPORELINK = register(new DescribedPotion(Crux.key("sporelink"),
        "Shares damage between all nearby spore-linked entities") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new SporelinkPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x8CA755);
        }
    });
    public static final CruxPotion ENCUMBERED = register(new DescribedPotion(Crux.key("encumbered"),
        "Makes movement difficult") {
        final Map<Attribute, AttributesPotion.PotModCreator> potions = Map.of(
            Attribute.MOVEMENT_SPEED,
            new AttributesPotion.PotModCreator() {
                @Override
                public double calculateAmount(LivingEntity e, int amplifier) {
                    return (amplifier+1) * -0.1f;
                }

                @Override
                public AttributeModifier.Operation getOperation() {
                    return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
                }
            },
            Attribute.JUMP_STRENGTH,
            new AttributesPotion.PotModCreator() {
                @Override
                public double calculateAmount(LivingEntity e, int amplifier) {
                    return (amplifier+1) * -0.1f;
                }

                @Override
                public AttributeModifier.Operation getOperation() {
                    return AttributeModifier.Operation.MULTIPLY_SCALAR_1;
                }
            }
        );
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new AttributesPotion(this, e, duration, amplifier, potions);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x555555);
        }
    });
    public static final CruxPotion THORNLASH = register(new DescribedPotion(Crux.key("thornlash"),
        "Thorns in your feet") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new ThornlashPotion(this, e, duration, amplifier, inflictor);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x4C9A50);
        }
    });
    public static final CruxPotion SPETTLE = register(new DescribedPotion(Crux.key("spettle"),
        "Eww") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new TitleDisplayPotion(this, e, duration, amplifier,
                "<!shadow><font:'crux:spettle'>1", null);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x4682B4);
        }
    });
    public static final CruxPotion EXPOSED = register(new DescribedPotion(Crux.key("exposed"),
        "Incoming attacks deal extra damage") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new ExposedPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xFF6347);
        }
    });
    public static final CruxPotion ENDURANCE = register(new DescribedPotion(Crux.key("endurance"),
        "Slows hunger depletion rate") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new EndurancePotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x8B5A2B);
        }
    });
    public static final CruxPotion PACK_TACTICS = register(new DescribedPotion(Crux.key("pack_tactics"),
        "Nearby allies deal more damage") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new PackTacticsPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x5E8D46);
        }
    });
    public static final CruxPotion PACK_ALLIANCE = register(new DescribedPotion(Crux.key("pack_alliance"),
        "Increases damage dealt. Applies when near an ally with the Pack Tactics effect") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new PackAlliancePotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xAA77CC);
        }
    });
    public static final CruxPotion PIERCING = register(new DescribedPotion(Crux.key("piercing"),
        "Projectiles pass through entities and ignore a portion of armor") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new PiercingPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xA52A2A);
        }
    });
    public static final CruxPotion SEVERING = register(new DescribedPotion(Crux.key("severing"),
        "Increases damage dealt to undead mobs") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new SeveringPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0x6C4A7E);
        }
    });
    public static final CruxPotion SHATTERING = register(new DescribedPotion(Crux.key("shattering"),
        "Increases damage dealt to armored entities and durability damage to shields") {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new ShatteringPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xD2691E);
        }
    });
    public static final CruxPotion BRACING = register(new DescribedPotion(Crux.key("bracing"),
        "Reduces knockback taken") {
        final Map<Attribute, AttributesPotion.PotModCreator> potions = Map.of(
            Attribute.KNOCKBACK_RESISTANCE,
            (e, amplifier) -> (amplifier+1) * 0.15f
        );
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new AttributesPotion(this, e, duration, amplifier, potions);
        }

        @Override
        public @NotNull Color getColor() {
            return Color.fromRGB(0xB0C4DE);
        }
    });

    private static CruxPotion register(CruxPotion pot){
        return CruxPotionRegistries.POTION.register(pot);
    }
}
