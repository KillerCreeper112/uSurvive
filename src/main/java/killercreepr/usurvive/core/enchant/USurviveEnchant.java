package killercreepr.usurvive.core.enchant;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.core.Crux;
import org.bukkit.enchantments.Enchantment;

public class USurviveEnchant {
    public static final Enchantment TRAMPLE = get("trample");
    public static final Enchantment JOY_WHIRL = get("joy_whirl");
    public static final Enchantment BURNING_THORNS = get("burning_thorns");

    private static Enchantment get(String id){
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Crux.key(id));
    }
}
