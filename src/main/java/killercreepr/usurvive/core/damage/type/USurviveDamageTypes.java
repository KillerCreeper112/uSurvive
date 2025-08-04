package killercreepr.usurvive.core.damage.type;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.core.Crux;
import org.bukkit.damage.DamageType;

public class USurviveDamageTypes {
    public static final DamageType THORNLASH = RegistryAccess.registryAccess()
        .getRegistry(RegistryKey.DAMAGE_TYPE).get(Crux.key("thornlash"));

    public static final DamageType ENTITY_ATTACK_BYPASS_SHIELD = RegistryAccess.registryAccess()
        .getRegistry(RegistryKey.DAMAGE_TYPE).get(Crux.key("entity_attack_bypass_shield"));

    public static final DamageType MIRRORSTRIKE = RegistryAccess.registryAccess()
        .getRegistry(RegistryKey.DAMAGE_TYPE).get(Crux.key("mirrorstrike"));
}
