package killercreepr.usurvive.core.advancement.objective;

import killercreepr.crux.api.data.DataExchange;
import killercreepr.crux.api.loot.LootContext;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.GenericEventObjective;
import killercreepr.cruxentities.api.combat.EntityDamager;
import killercreepr.usurvive.api.event.ProjectileRicochetEvent;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class ReflectProjectileObjective extends GenericEventObjective<ProjectileRicochetEvent> {
    public ReflectProjectileObjective(@NotNull ObjectiveCommonData data, int maxProgress) {
        super(data, maxProgress);
    }

    @Override
    protected LootContext buildContext(ProjectileRicochetEvent event) {
        Entity owner = EntityDamager.getOwner(event.getNewProjectile());
        return LootContext.builder()
            .info(
                DataExchange.builder()
                    .putAll(event.getProjectile(), "old_projectile")
                    .putAll(event.getNewProjectile(), "new_projectile")
                    .putAll(event.getHitBlock(), "hit_block")
                    .putAll(event.getHitEntity(), "hit_entity")
                    .putAll(event.getHitBlockFace(), "hit_block_face")
                    .putAll(event.getReflectDirection(), "reflect_direction")
                    .putAll(event.getReflectedAmount(), "reflected_amount")
                    .build()
            )
            .looter(owner)
            .looted(event.getNewProjectile())
            .build();
    }

}
