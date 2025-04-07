package killercreepr.usurvive.core.advancement.auto;

import killercreepr.crux.api.entity.predicate.EntityPredicate;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.core.loot.conditions.entity.EntityCondition;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxadvancements.api.advancement.CruxAdvancement;
import killercreepr.cruxadvancements.api.advancement.CruxRewardAdvancement;
import killercreepr.cruxadvancements.api.advancement.criteria.CruxCriteria;
import killercreepr.cruxadvancements.api.advancement.flag.CruxAdvancementFlag;
import killercreepr.cruxadvancements.api.advancement.manager.CruxAdvancementManager;
import killercreepr.cruxadvancements.api.advancement.objective.AdvancementObjective;
import killercreepr.cruxadvancements.api.advancement.reward.CruxAdvanceReward;
import killercreepr.cruxadvancements.api.crazy.advancement.AdvancementFrame;
import killercreepr.cruxadvancements.api.crazy.advancement.AdvancementVisibility;
import killercreepr.cruxadvancements.core.advancement.criteria.NumberCriteria;
import killercreepr.cruxadvancements.core.advancement.objective.ObjectiveCommonData;
import killercreepr.cruxadvancements.core.advancement.objective.standard.KillEntityObjective;
import killercreepr.cruxadvancements.core.registries.AdvancementRegistries;
import killercreepr.cruxadvancements.crazy.advancement.CrazyAdvancement;
import killercreepr.cruxadvancements.crazy.advancement.CrazyAdvancementDisplay;
import killercreepr.usurvive.core.USurvivePlugin;
import net.kyori.adventure.key.Key;

import java.util.List;
import java.util.Map;

public class KillMobAdvanceMaker {
    private static int step = 0;
    public static void registerAll(){
        step = 0;
        new KillMobAdvanceMaker("blaze", "Blaze Buster", "blaze_rod",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("creeper", "Creeper Crusher", "gunpowder",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("skeleton", "Skeleton Snapper","bone",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("slime", "Slime Slicer","slime_ball",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("spider", "Spider Slayer","spider_eye",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("zombie", "Zombie Zapper","rotten_flesh",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("pillager", "Pillager Punisher","pillager_spawn_egg",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("vindicator", "Vindicator Victor","vindicator_spawn_egg",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("cave_spider", "Cave Culler","cave_spider_spawn_egg",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
    }

    protected final String id;
    protected final String title;
    protected final String material;
    protected final List<Integer> amounts;
    protected final EntityPredicate predicate;

    public KillMobAdvanceMaker(String id, String title, String material, List<Integer> amounts, EntityPredicate predicate) {
        this.id = id;
        this.title = title;
        this.material = material;
        this.amounts = amounts;
        this.predicate = predicate;
    }
    public KillMobAdvanceMaker(String id, String title, String material, List<Integer> amounts) {
        this.id = id;
        this.title = title;
        this.material = material;
        this.amounts = amounts;
        this.predicate = EntityPredicate.fromType(Key.key(id));
    }

    public void createAll(){
        step++;
        int level = 0;
        for(int amount : amounts){
            level++;
            CruxAdvancement advance = createKill(id, title, level, amount, predicate);
            getCombat().registerAdvancement(advance);
        }
        USurvivePlugin.inst().log("Registered advancements. " + level + " levels of: " + title);
    }

    public CruxAdvancement createKill(String id, String title, int level, int amount, EntityPredicate type){
        var monsterAdvance = getCombat().getAdvancement(USurvivePlugin.inst().key("combat/kill/monster/" + level));
        CruxAdvanceReward reward = null;
        if(monsterAdvance instanceof CruxRewardAdvancement s){
            reward = s.getReward();
        }

        int parentLevel = level - 1;
        Key parentKey;
        if(parentLevel < 1) parentKey = USurvivePlugin.inst().key("combat/root");
        else parentKey = USurvivePlugin.inst().key("combat/kill/" + id + "/" + parentLevel);

        CrazyAdvancementDisplay display = new CrazyAdvancementDisplay(
            DynamicItem.builder().material(material).build(), title + " " + CruxMath.numeral(level),
            "Kill " + CruxMath.format(amount) + " " + id + "s",
            AdvancementFrame.TASK,
            level == 1 ? AdvancementVisibility.VANILLA : AdvancementVisibility.PARENT_GRANTED,
            null,
            1f, parentLevel < 1 ? step : 0,
            parentKey, null
        );

        CruxCriteria criteria = new NumberCriteria(amount);
        AdvancementObjective objective = new KillEntityObjective(
            new ObjectiveCommonData("a", new EntityCondition("looted", type, null, null)),
            amount
        );

        return new CrazyAdvancement(
            USurvivePlugin.inst().key("combat/kill/" + id + "/" + level),
            parentKey,
            display, criteria, reward,
            new CruxAdvancementFlag[]{CruxAdvancementFlag.SHOW_TOAST},
            Map.of("a", objective),
            1
        );
    }

    public CruxAdvancementManager getCombat(){
        return AdvancementRegistries.ADVANCEMENT_MANAGERS.get(USurvivePlugin.inst().key("combat"));
    }
}
