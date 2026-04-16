package killercreepr.usurvive.api.values;

import com.google.common.reflect.TypeToken;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.cruxconfig.config.bukkit.value.CfgValue;
import killercreepr.cruxconfig.config.common.handler.AutoFileHandler;
import killercreepr.cruxconfig.config.common.handler.AutoFileOptions;
import net.kyori.adventure.key.Key;

import java.util.Map;

public interface AnimalsCfg {
    CfgValue<Map<Key, NumberProvider>> ANIMAL_ATTRIBUTES();
    CfgValue<Map<Key, AnimalAttributeData>> ANIMAL_ATTRIBUTES_PER_TYPE();

    record AnimalAttributeData(Map<Key, NumberProvider> attributes, boolean use_global){
        public static final AutoFileHandler<AnimalAttributeData> FILE_HANDLER =
          new AutoFileHandler<>(AnimalsCfg.AnimalAttributeData.class, AutoFileOptions.builder()
            .addTypeToken("attributes", new TypeToken<Map<Key, NumberProvider>>() {})
            .build());
    }
}
