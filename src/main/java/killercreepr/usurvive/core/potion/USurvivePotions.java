package killercreepr.usurvive.core.potion;

import killercreepr.crux.core.Crux;
import killercreepr.cruxpotions.api.potion.ActivePotion;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.api.potion.inflictor.PotionInflictor;
import killercreepr.cruxpotions.core.potions.GenericPotion;
import killercreepr.cruxpotions.core.registries.CruxPotionRegistries;
import org.bukkit.Color;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class USurvivePotions {
    public static void register(){
    }
    public static final CruxPotion RECALL = register(new GenericPotion(Crux.key("recall")) {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor potionInflictor) {
            return new RecallPotion(this, entity, duration, amplifier);
        }

        @Override
        public @Nullable Color getColor() {
            return Color.fromRGB(0xBD48FF);
        }
    });
    public static final CruxPotion WATERBOUND = register(new GenericPotion(Crux.key("waterbound")) {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor potionInflictor) {

            return new WaterboundPotion(this, entity, duration, amplifier);
        }

        @Override
        public @Nullable Color getColor() {
            return Color.fromRGB(0x80CCEA);
        }
    });
    public static final CruxPotion RAGE = register(new GenericPotion(Crux.key("rage")) {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity entity, int duration, int amplifier, @Nullable PotionInflictor potionInflictor) {
            return new RagePotion(this, entity, duration, amplifier);
        }

        @Override
        public @Nullable Color getColor() {
            return Color.fromRGB(0xF43930);
        }
    });
    public static final CruxPotion GOOF = register(new GenericPotion(Crux.key("goof")) {
        @Override
        public @NotNull ActivePotion create(@NotNull Entity e, int duration, int amplifier, @Nullable PotionInflictor inflictor) {
            return new GoofPotion(this, e, duration, amplifier);
        }

        @Override
        public @NotNull Color getColor() {
            //new java.awt.Color(0x76C934);
            return Color.fromRGB(0x76C934);
        }
    });

    private static CruxPotion register(CruxPotion pot){
        return CruxPotionRegistries.POTION.register(pot);
    }
}
