package killercreepr.usurvive.core.enchant;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.core.Crux;
import org.bukkit.enchantments.Enchantment;

public class USurviveEnchant {
    public static final Enchantment TRAMPLE = get("trample");
    public static final Enchantment JOY_WHIRL = get("joy_whirl");
    public static final Enchantment BURNING_THORNS = get("burning_thorns");
    public static final Enchantment BACKSTAB = get("backstab");
    public static final Enchantment WITHSTAND = get("withstand");
    public static final Enchantment SECOND_WIND = get("second_wind");
    public static final Enchantment BOOMSHOT = get("boomshot");
    public static final Enchantment VELOCITY = get("velocity");
    public static final Enchantment FROSTBITE = get("frostbite");
    public static final Enchantment INSIGHT = get("insight");

    private static Enchantment get(String id){
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Crux.key(id));
    }
}
