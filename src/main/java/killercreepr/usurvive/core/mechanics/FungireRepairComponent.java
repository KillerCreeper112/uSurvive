package killercreepr.usurvive.core.mechanics;

import killercreepr.crux.api.valueproviders.number.NumberProvider;
import net.kyori.adventure.key.Key;

import java.util.Map;

public class FungireRepairComponent {
    protected final float repairChance;
    protected final NumberProvider repairAmount;
    protected final float exhaustionChance;
    protected final NumberProvider exhaustionAmount;
    protected final Map<Key, WorldRepair> worldRepair;

    public FungireRepairComponent(float repairChance, NumberProvider repairAmount, float exhaustionChance, NumberProvider exhaustionAmount, Map<Key, WorldRepair> worldRepair) {
        this.repairChance = repairChance;
        this.repairAmount = repairAmount;
        this.exhaustionChance = exhaustionChance;
        this.exhaustionAmount = exhaustionAmount;
        this.worldRepair = worldRepair;
    }

    public float getRepairChance() {
        return repairChance;
    }

    public NumberProvider getRepairAmount() {
        return repairAmount;
    }

    public float getExhaustionChance() {
        return exhaustionChance;
    }

    public NumberProvider getExhaustionAmount() {
        return exhaustionAmount;
    }

    public WorldRepair getWorldRepair(Key key){
        return worldRepair == null ? null : worldRepair.get(key);
    }

    public Map<Key, WorldRepair> getWorldRepair() {
        return worldRepair;
    }

    public record WorldRepair(float repairChance, NumberProvider repairAmount){
    }
}
