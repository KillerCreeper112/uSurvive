package killercreepr.usurvive.core.advancement.auto;

import killercreepr.crux.api.entity.predicate.EntityPredicate;
import killercreepr.crux.api.item.dynamic.DynamicItem;
import killercreepr.crux.core.item.dynamic.component.DynamicItemName;
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
import killercreepr.cruxrewards.core.cruxadvancements.AdvanceRewardPackage;
import killercreepr.cruxrewards.core.reward.SimpleRewardDisplay;
import killercreepr.cruxrewards.core.reward.container.SimpleRewardPackage;
import killercreepr.usurvive.core.USurvivePlugin;
import net.kyori.adventure.key.Key;

import java.util.List;
import java.util.Map;

public class KillMobAdvanceMaker {
    private static int step = 0;
    public static void registerAll(){
        step = 0;
        new KillMobAdvanceMaker("zombie", "Zombie Zapper","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("zombie_villager", "Village Vanquisher","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("husk", "Heat Haunter","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("drowned", "Drenched Demise","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("skeleton", "Skeleton Snapper","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("stray", "Snowy Slayer","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("bogged", "Bogged Breaker","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("creeper", "Creeper Crusher", "",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("spider", "Spider Slayer","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("cave_spider", "Cave Culler","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("slime", "Slime Slicer","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("witch", "Witch Waster","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("silverfish", "Silver Squasher","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("enderman", "Ender Ender","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("pillager", "Pillager Punisher","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("vindicator", "Vindicator Victor","",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("ravager", "Rampage Ripper","",
            List.of(5, 10, 25, 50, 100, 250, 400)).createAll();

        new KillMobAdvanceMaker("piglin", "Piglin Pummeler", "",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("piglin_brute", "Brute Basher", "",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("hoglin", "Hog Hunter", "",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("wither_skeleton", "Wither Wacker", "",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("blaze", "Blaze Buster", "",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("magma_cube", "Molten Masher", "",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("ghast", "Ghast Getter", "",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();

        new KillMobAdvanceMaker("wither", "Wither Wrecker", "wither_skeleton_skull",
            List.of(1,2,3,4,5,6,7)).createAll();


        new KillMobAdvanceMaker("crux:toxicator", "Toxic Terminator", "crux:toxicator_mask_wounded",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("crux:scourger", "Scourge Slayer", "crux:scourger_horns",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("crux:abyssal_eyevine", "Eyestalk Eraser", "crux:abyssal_tendril",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
        new KillMobAdvanceMaker("crux:plaguewing", "Plaguewing Purger", "crux:plague_wing",
            List.of(10, 25, 50, 100, 250, 500, 1000)).createAll();
    }

    protected final String id;
    protected final String title;
    protected final String material;
    protected final List<Integer> amounts;
    protected final EntityPredicate predicate;

    public KillMobAdvanceMaker(String id, String title, String material, List<Integer> amounts, EntityPredicate predicate) {
        this.id = removeColon(id);
        this.title = title;
        this.material = material.isBlank() ? (id + "_spawn_egg") : material;
        this.amounts = amounts;
        this.predicate = predicate;
    }
    public KillMobAdvanceMaker(String id, String title, String material, List<Integer> amounts) {
        this.id = removeColon(id);
        this.title = title;
        this.material = material.isBlank() ? (id + "_spawn_egg") : material;
        this.amounts = amounts;
        this.predicate = EntityPredicate.fromType(Key.key(id));
    }

    public static String removeColon(String d){
        String[] split = d.split(":", 2);
        return split.length > 1 ? split[1] : split[0];
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
            if(s.getReward() instanceof AdvanceRewardPackage pack){
                if(pack.getRewards() instanceof SimpleRewardPackage simple && simple.getDisplay() instanceof SimpleRewardDisplay d){
                    reward = new AdvanceRewardPackage(new SimpleRewardPackage(
                        new SimpleRewardDisplay(DynamicItem.builder().material(material).addComponent(
                            new DynamicItemName(title + " " + CruxMath.numeral(level))
                        ).build()), simple.getRewards()
                    ));
                }
            }

        }

        int parentLevel = level - 1;
        Key parentKey;
        if(parentLevel < 1) parentKey = USurvivePlugin.inst().key("combat/root");
        else parentKey = USurvivePlugin.inst().key("combat/kill/" + id + "/" + parentLevel);

        CrazyAdvancementDisplay display = new CrazyAdvancementDisplay(
            DynamicItem.builder().material(material).build(), title + " " + CruxMath.numeral(level),
            "Kill " + CruxMath.format(amount) + " " + id.replace("_", " ") + "s",
            AdvancementFrame.TASK,
            level == 1 ? AdvancementVisibility.VANILLA : AdvancementVisibility.PARENT_GRANTED,
            null,
            1f, parentLevel < 1 ? step : 0,
            parentKey, null
        );

        CruxCriteria criteria = new NumberCriteria(amount);
        AdvancementObjective objective = new KillEntityObjective(
            new ObjectiveCommonData("a", new EntityCondition("looted", type, null, null,null)),
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
