package killercreepr.usurvive.enchant;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.core.Crux;
import org.bukkit.enchantments.Enchantment;

public class USurviveEnchant {
    public static final Enchantment TRAMPLE = get("trample");
    public static final Enchantment JOY_WHIRL = get("joy_whirl");

    private static Enchantment get(String id){
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Crux.key(id));
    }
}
