package killercreepr.usurvive.core.registries;

import killercreepr.crux.api.registry.KeyedRegistry;
import killercreepr.crux.api.registry.Registry;
import killercreepr.crux.core.registry.SimpleKeyedRegistry;
import killercreepr.crux.core.registry.SimpleRegistry;
import killercreepr.cruxworlds.api.world.entity.NaturalEntitySpawnGroup;
import killercreepr.usurvive.api.entity.player.settings.PlayerSetting;
import killercreepr.usurvive.api.entity.player.settings.PlayerSettingSelection;
import killercreepr.usurvive.api.mechanics.grapple.GrappleHook;

public class USurviveRegistries {
    public static final KeyedRegistry<GrappleHook> GRAPPLE_HOOK = new SimpleKeyedRegistry<>();
    public static final Registry<NaturalEntitySpawnGroup> OVERWORLD_NATURAL_ENTITY_SPAWN_GROUP = SimpleRegistry.fromSet();
    public static final KeyedRegistry<PlayerSetting> PLAYER_SETTING = KeyedRegistry.keyedRegistry();
    public static final KeyedRegistry<PlayerSettingSelection> PLAYER_SETTING_SELECTION = KeyedRegistry.keyedRegistry();

}
