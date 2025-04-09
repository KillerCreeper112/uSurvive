package killercreepr.usurvive.core.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.arguments.StringArgumentType;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.component.parser.TextDataComponentDecoder;
import killercreepr.crux.core.data.util.MapBuilder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.cruxconfig.config.bukkit.file.CruxConfig;
import killercreepr.cruxconfig.config.bukkit.file.CruxFolder;
import killercreepr.cruxconfig.config.bukkit.file.CruxJson;
import killercreepr.cruxconfig.config.common.element.FileArray;
import killercreepr.cruxconfig.config.common.element.FileObject;
import killercreepr.usurvive.core.USurvivePlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class DevCommands {
    protected final @NotNull USurvivePlugin plugin;
    public DevCommands(@NotNull USurvivePlugin plugin) {
        this.plugin = plugin;
    }

    public final Map<String, AdvanceCreator> advanceCreators = new MapBuilder<String, AdvanceCreator>()
        .put("killMob", new KillMobs())
        .put("harvestCrop", new HarvestCrop())
        .put("catchFish", new CatchFish())
        .put("breakBlock", new BreakBlock())
        .put("menuModule", new MenuModule())
        .build();
    public void register() {
        plugin.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();

            //id=blaze,title="Blaze Buster",material=blaze_rod,amounts=[10, 25, 50, 100, 250, 500, 1000],levels=7
            //manager="usurvive:combat"
            commands.register(
                Commands.literal("udev")
                    .requires(source -> getExecutor(source).hasPermission("usurvive.cmds.udev.use"))
                    .then(
                        Commands.literal("advancement")
                            .then(
                                Commands.literal("create")
                                    .then(
                                        Commands.argument("id", StringArgumentType.word())
                                            .suggests((ctx, builder) ->{
                                                for (String s : advanceCreators.keySet()) {
                                                    builder.suggest(s);
                                                }
                                                return builder.buildFuture();
                                            })
                                            .then(
                                                Commands.argument("text", StringArgumentType.greedyString())
                                                    .executes(ctx ->{
                                                        String text = ctx.getArgument("text", String.class);
                                                        var data = TextDataComponentDecoder.parseComponentMap(text);
                                                        AdvanceCreator creator = advanceCreators.get(ctx.getArgument("id", String.class));
                                                        creator.generateFile(data);
                                                        return 1;
                                                    })
                                            )
                                    )
                            ).then(
                                Commands.literal("prefix")
                                    .then(
                                        Commands.argument("replace", StringArgumentType.string())
                                            .then(
                                                Commands.argument("with", StringArgumentType.string())
                                                    .then(
                                                        Commands.argument("replace2", StringArgumentType.string())
                                                            .then(
                                                                Commands.argument("with2", StringArgumentType.string())
                                                                    .then(
                                                                        Commands.argument("path", StringArgumentType.string())
                                                                            .executes(ctx ->{
                                                                                String replace = ctx.getArgument("replace", String.class);
                                                                                String with = ctx.getArgument("with", String.class);
                                                                                String replace2 = ctx.getArgument("replace2", String.class);
                                                                                String with2 = ctx.getArgument("with2", String.class);
                                                                                String path = ctx.getArgument("path", String.class);

                                                                                File[] files = new CruxFolder(plugin, path).file().listFiles();
                                                                                for(File f : files){
                                                                                    String content = null;
                                                                                    try {
                                                                                        content = new String(Files.readAllBytes(f.toPath()));
                                                                                    } catch (IOException e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }

                                                                                    var jsonFile =new CruxJson(plugin, "dev/generated/prefixed/" + with + "/"
                                                                                        + CruxFolder.withoutFileExtension(f.getName()));
                                                                                    jsonFile.createNewFileIfNeeded();
                                                                                    jsonFile.close();
                                                                                    File newFile = jsonFile.file();

                                                                                    // Step 2: Replace text (simple string replacement)
                                                                                    content = content.replace(replace, with)
                                                                                        .replace(replace2, with2);

                                                                                    // Step 3: Optionally parse it to ensure it's still valid JSON
                                                                                    JsonElement jsonElement = JsonParser.parseString(content);

                                                                                    // Step 4: Write it back to the file
                                                                                    try (FileWriter writer = new FileWriter(newFile)) {
                                                                                        new GsonBuilder().setPrettyPrinting().create().toJson(jsonElement, writer);
                                                                                    } catch (Exception e) {
                                                                                        throw new RuntimeException(e);
                                                                                    }
                                                                                }
                                                                                return 1;
                                                                            })
                                                                    )
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
                    .build()
            );
        });
    }

    public @NotNull CommandSender getExecutor(@NotNull CommandSourceStack source){
        return Objects.requireNonNullElse(source.getExecutor(), source.getSender());
    }

    public interface AdvanceCreator{
        void generateFile(Map<String, Object> data);
    }

    public static List<Integer> amounts(Object o){
        List<Integer> list = new ArrayList<>();
        for(Object s : (List) o){
            list.add((int) CruxMath.evaluate(s.toString()));
        }
        return list;
    }

    //key=combat/kill/monster/,levels=7,slot=10,parent=2
    public static class MenuModule implements AdvanceCreator{

        @Override
        public void generateFile(Map<String, Object> data) {
            String key = data.get("key").toString();
            if(key.split(":", 2).length == 1){
                key = "usurvive:" + key;
            }
            //usurvive:combat/kill/monster/
            int levels = (int) CruxMath.evaluate(data.getOrDefault("levels", "7").toString());
            int slot = (int) CruxMath.evaluate(data.getOrDefault("slot", "10").toString());
            int parent = (int) CruxMath.evaluate(data.getOrDefault("parent", "2").toString());

            CruxConfig cfg = new CruxConfig(USurvivePlugin.inst(), "dev/generated/menu/modules");

            //- type: items_container
            //  base_items: quest_items
            //  base_item:
            //    slot: 11
            //    data:
            //      advancement: usurvive:combat/kill/monster/2
            FileArray a;
            if(cfg.getElement("modules") instanceof FileArray dd){
                a = dd;
            }else a = new FileArray();
            for(int i = 1; i <= levels; i++){
                FileObject o = new FileObject()
                    .addProperty("type", "items_container")
                    .addProperty("base_items", "quest_items")
                    .add("base_item",
                        new FileObject()
                            .addProperty("slot", slot)
                            .add("data",
                                new FileObject().addProperty("advancement", key + i)
                            )
                    );

                //  items:
                //    default:
                //      base: parent_requirement
                if(i >= parent){
                    o.add("items", new FileObject().add("default", new FileObject().addProperty("base", "parent_requirement")));
                }

                a.add(o);
                slot++;
            }

            cfg.serialize("modules", a);
            cfg.save();
        }
    }

    //id=blaze,title="Blaze Buster",material=blaze_rod,amounts=[10, 25, 50, 100, 250, 500, 1000],levels=7
    public static class KillMobs implements AdvanceCreator{
        protected final Map<Integer, Integer> orbits = Map.of(
            1, 5,
            2, 25,
            3, 70,
            4, 150,
            5, 250,
            6, 500,
            7, 1000
        );
        protected final Map<Integer, Integer> exp = Map.of(
            1, 10,
            2, 20,
            3, 35,
            4, 50,
            5, 65,
            6, 200,
            7, 400
        );
        @Override
        public void generateFile(Map<String, Object> data) {
            String id = data.get("id").toString();
            String title = data.getOrDefault("title", "none").toString();
            String material = data.getOrDefault("material", "stone").toString();
            List<Integer> amounts = amounts(data.get("amounts"));
            int levels = (int) CruxMath.evaluate(data.get("levels").toString());
            String entityType = data.getOrDefault("entity", id).toString();

            Key managerKey = Crux.key(data.getOrDefault("manager", "usurvive:combat").toString());


            int level = 0;
            for(int amount : amounts){
                level++;

                String x = data.getOrDefault("x", "1").toString();
                String y = data.getOrDefault("y", "0").toString();
                if(level == 1){
                    x = "1";
                    y = "0";
                }

                String advanceID = managerKey + "/" + id + "/" + level;

                String parent = level == 1 ? (managerKey.asString() + "/root") :
                    (managerKey.asString() + "/kill/" + id + "/" + (level-1));
                String objectiveAmount = amount + "";
                String entityPredicate = entityType;
                String criteriaRequirement = objectiveAmount;
                String titleName = title + " " + CruxMath.numeral(level);
                String materialID = material;
                String description = "Kill " + CruxMath.format(amount) + " " + addS(id);
                String visibilityType = level == 1 ? "vanilla" : "parent_granted";

                String orbits = data.getOrDefault("orbits", this.orbits.get(level) + "").toString();
                String exp = data.getOrDefault("exp", this.exp.get(level) + "").toString();

                String jsonText = String.format("""
                {
                  "parent": "%s",
                  "objectives": {
                    "a": {
                      "objective": {
                        "type": "kill_entity",
                        "amount": %s
                      },
                      "conditions": {
                        "condition": "entity",
                        "target": "looted",
                        "entity_predicate": "%s"
                      }
                    }
                  },
                  "update_advancement_period": 1,
                  "criteria": {
                    "criteria": "number",
                    "requirements": %s
                  },
                  "reward": {
                    "reward": "reward_package",
                    "rewards": [
                      {
                        "reward": "crux_currency",
                        "currency": "crux:orbit",
                        "amount": %s
                      },
                      {
                        "reward": "experience",
                        "amount": %s
                      }
                    ],
                    "display": {
                      "name": "%s",
                      "material": "%s"
                    }
                  },
                  "display": {
                    "position_origin": "%s",
                    "icon": {
                      "material": "%s"
                    },
                    "title": "%s",
                    "description": "%s",
                    "frame": "task",
                    "visibility": {
                      "type": "%s"
                    },
                    "x": %s,
                    "y": %s
                  }
                }
        """, parent, objectiveAmount, entityPredicate, criteriaRequirement, orbits, exp, titleName, materialID, parent, materialID, titleName, description, visibilityType,
                    x, y);

                Crux.logInfo(jsonText);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                var fileJson = new CruxJson(USurvivePlugin.inst(), "dev/generated/advancement/" + advanceID.replace(":", "/"), false);
                try {
                    // Ensure the directory exists
                    File file = fileJson.file();
                    file.getParentFile().mkdirs();

                    // Write to the file
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileJson.close();

                if(level >= levels){
                    break;
                }
            }
        }
    }

    public static String addS(String s){
        return addS(s, true);
    }

    public static String addS(String s, boolean formatUnderscore){
        if(formatUnderscore) s = s.replace("_", " ");
        if(s.endsWith("s")) return s;
        return s + "s";
    }

    public static class HarvestCrop implements AdvanceCreator{
        protected final Map<Integer, Integer> orbits = Map.of(
            1, 5,
            2, 20,
            3, 65,
            4, 140,
            5, 240,
            6, 480,
            7, 800
        );
        protected final Map<Integer, Integer> exp = Map.of(
            1, 10,
            2, 30,
            3, 50,
            4, 75,
            5, 100,
            6, 200,
            7, 350
        );
        @Override
        public void generateFile(Map<String, Object> data) {
            String id = data.get("id").toString();
            String title = data.getOrDefault("title", "none").toString();
            String material = data.getOrDefault("material", "stone").toString();
            List<Integer> amounts = amounts(data.get("amounts"));
            int levels = (int) CruxMath.evaluate(data.get("levels").toString());
            String block = data.getOrDefault("block", id).toString();

            Key managerKey = Crux.key(data.getOrDefault("manager", "usurvive:farming").toString());


            int level = 0;
            for(int amount : amounts){
                level++;
                String x = data.getOrDefault("x", "1").toString();
                String y = data.getOrDefault("y", "0").toString();
                if(level == 1){
                    x = "1";
                    y = "0";
                }
                String advanceID = managerKey + "/" + id + "/" + level;

                String parent = level == 1 ? (managerKey.asString() + "/root") :
                    (managerKey.asString() + "/harvest/" + id + "/" + (level-1));
                String objectiveAmount = amount + "";
                String blockPredicate = block;
                String criteriaRequirement = objectiveAmount;
                String titleName = title + " " + CruxMath.numeral(level);
                String materialID = material;
                String description = "Plant and harvest " + CruxMath.format(amount) + " " + addS(id);
                String visibilityType = level == 1 ? "vanilla" : "parent_granted";

                String orbits = data.getOrDefault("orbits", this.orbits.get(level) + "").toString();
                String exp = data.getOrDefault("exp", this.exp.get(level) + "").toString();

                String jsonText = String.format("""
                {
                  "parent": "%s",
                  "objectives": {
                    "a": {
                      "objective": {
                        "type": "break_block_drop",
                        "amount": %s
                      },
                      "conditions": {
                        "condition": "block_state",
                        "target": "block_state",
                        "block": "%s",
                        "age": "<block_state_data/ageable/max_age>"
                      }
                    },
                    "b": {
                      "objective": {
                        "type": "place_block",
                        "amount": %s
                      },
                      "conditions": {
                        "condition": "block",
                        "target": "looted",
                        "block_predicate": "%s"
                      }
                    }
                  },
                  "update_advancement_period": 5,
                  "criteria": {
                    "criteria": "number",
                    "requirements": %s
                  },
                  "reward": {
                    "reward": "reward_package",
                    "rewards": [
                      {
                        "reward": "crux_currency",
                        "currency": "crux:orbit",
                        "amount": %s
                      },
                      {
                        "reward": "experience",
                        "amount": %s
                      }
                    ],
                    "display": {
                      "name": "%s",
                      "material": "%s"
                    }
                  },
                  "display": {
                    "position_origin": "%s",
                    "icon": {
                      "material": "%s"
                    },
                    "title": "%s",
                    "description": "%s",
                    "frame": "task",
                    "visibility": {
                      "type": "%s"
                    },
                    "x": %s,
                    "y": %s
                  }
                }
        """, parent, objectiveAmount, blockPredicate, objectiveAmount, blockPredicate, (amount * 2)+"", orbits, exp, titleName, materialID,
                    parent, materialID, titleName, description, visibilityType,
                    x, y);

                Crux.logInfo(jsonText);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                var fileJson = new CruxJson(USurvivePlugin.inst(), "dev/generated/advancement/" + advanceID.replace(":", "/"), false);
                try {
                    // Ensure the directory exists
                    File file = fileJson.file();
                    file.getParentFile().mkdirs();

                    // Write to the file
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileJson.close();

                if(level >= levels){
                    break;
                }
            }
        }
    }

    public static class CatchFish implements AdvanceCreator{
        protected final Map<Integer, Integer> orbits = Map.of(
            1, 5,
            2, 25,
            3, 70,
            4, 150,
            5, 250,
            6, 500,
            7, 1000
        );
        protected final Map<Integer, Integer> exp = Map.of(
            1, 20,
            2, 40,
            3, 70,
            4, 100,
            5, 200,
            6, 400,
            7, 800
        );
        @Override
        public void generateFile(Map<String, Object> data) {
            String id = data.get("id").toString();
            String title = data.getOrDefault("title", "none").toString();
            String material = data.getOrDefault("material", id).toString();
            List<Integer> amounts = amounts(data.get("amounts"));
            int levels = (int) CruxMath.evaluate(data.get("levels").toString());
            String item = data.getOrDefault("item", id).toString();

            Key managerKey = Crux.key(data.getOrDefault("manager", "usurvive:fishing").toString());


            int level = 0;
            for(int amount : amounts){
                level++;

                String x = data.getOrDefault("x", "1").toString();
                String y = data.getOrDefault("y", "0").toString();
                if(level == 1){
                    x = "1";
                    y = "0";
                }

                String advanceID = managerKey + "/" + id + "/" + level;

                String parent = level == 1 ? (managerKey.asString() + "/root") :
                    (managerKey.asString() + "/catch/" + id + "/" + (level-1));
                String objectiveAmount = amount + "";
                String itemPredicate = item;
                String criteriaRequirement = objectiveAmount;
                String titleName = title + " " + CruxMath.numeral(level);
                String materialID = material;
                String description = "Catch " + CruxMath.format(amount) + " " + addS(id);
                String visibilityType = level == 1 ? "vanilla" : "parent_granted";

                String orbits = data.getOrDefault("orbits", this.orbits.get(level) + "").toString();
                String exp = data.getOrDefault("exp", this.exp.get(level) + "").toString();

                String jsonText = String.format("""
                {
                  "parent": "%s",
                  "objectives": {
                    "a": {
                      "objective": {
                        "type": "catch_fish",
                        "amount": %s
                      },
                      "conditions": {
                        "condition": "item",
                        "target": "looted",
                        "item_predicate": "%s"
                      }
                    }
                  },
                  "update_advancement_period": 1,
                  "criteria": {
                    "criteria": "number",
                    "requirements": %s
                  },
                  "reward": {
                    "reward": "reward_package",
                    "rewards": [
                      {
                        "reward": "crux_currency",
                        "currency": "crux:orbit",
                        "amount": %s
                      },
                      {
                        "reward": "experience",
                        "amount": %s
                      }
                    ],
                    "display": {
                      "name": "%s",
                      "material": "%s"
                    }
                  },
                  "display": {
                    "position_origin": "%s",
                    "icon": {
                      "material": "%s"
                    },
                    "title": "%s",
                    "description": "%s",
                    "frame": "task",
                    "visibility": {
                      "type": "%s"
                    },
                    "x": %s,
                    "y": %s
                  }
                }
        """, parent, objectiveAmount, itemPredicate, criteriaRequirement, orbits, exp, titleName, materialID, parent, materialID, titleName, description, visibilityType,
                    x, y);

                Crux.logInfo(jsonText);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                var fileJson = new CruxJson(USurvivePlugin.inst(), "dev/generated/advancement/" + advanceID.replace(":", "/"), false);
                try {
                    // Ensure the directory exists
                    File file = fileJson.file();
                    file.getParentFile().mkdirs();

                    // Write to the file
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileJson.close();

                if(level >= levels){
                    break;
                }
            }
        }
    }

    public static class BreakBlock implements AdvanceCreator{
        protected final Map<Integer, Integer> orbits = Map.of(
            1, 5,
            2, 25,
            3, 70,
            4, 150,
            5, 250,
            6, 500,
            7, 1000
        );
        protected final Map<Integer, Integer> exp = Map.of(
            1, 10,
            2, 20,
            3, 35,
            4, 50,
            5, 65,
            6, 200,
            7, 400
        );
        @Override
        public void generateFile(Map<String, Object> data) {
            String id = data.get("id").toString();
            String title = data.getOrDefault("title", "none").toString();
            String material = data.getOrDefault("material", id).toString();
            List<Integer> amounts = amounts(data.get("amounts"));
            int levels = (int) CruxMath.evaluate(data.get("levels").toString());
            String block = data.getOrDefault("block", id).toString();

            Key managerKey = Crux.key(data.getOrDefault("manager", "usurvive:mining").toString());

            int level = 0;
            for(int amount : amounts){
                level++;

                String x = data.getOrDefault("x", "1").toString();
                String y = data.getOrDefault("y", "0").toString();
                if(level == 1){
                    x = "1";
                    y = "0";
                }

                String advanceID = managerKey + "/" + id + "/" + level;

                String parent = level == 1 ? (managerKey.asString() + "/root") :
                    (managerKey.asString() + "/mine/" + id + "/" + (level-1));
                String objectiveAmount = amount + "";
                String blockPredicate = block;
                String criteriaRequirement = objectiveAmount;
                String titleName = title + " " + CruxMath.numeral(level);
                String materialID = material;
                String description = "Mine " + CruxMath.format(amount) + " " + addS(id);
                String visibilityType = level == 1 ? "vanilla" : "parent_granted";

                String orbits = data.getOrDefault("orbits", this.orbits.get(level) + "").toString();
                String exp = data.getOrDefault("exp", this.exp.get(level) + "").toString();

                //     "conditions": {
                //        "condition": "all_of",
                //        "terms": [
                //          {
                //            "condition": "block",
                //            "target": "looted",
                //            "block_predicate": "minecraft:acacia_log"
                //          },
                //          {
                //            "condition": "invert",
                //            "term": {
                //              "condition": "block_entity_placed",
                //              "target": "looted"
                //            }
                //          }
                //        ]
                //      }
                String jsonText = String.format("""
                {
                  "parent": "%s",
                  "objectives": {
                    "a": {
                      "objective": {
                        "type": "break_block",
                        "amount": %s
                      },
                      "conditions": {
                          "condition": "all_of",
                          "terms": [
                            {
                              "condition": "block",
                              "target": "looted",
                              "block_predicate": "%s"
                            },
                            {
                              "condition": "invert",
                              "term": {
                                "condition": "block_entity_placed",
                                "target": "looted"
                              }
                            }
                          ]
                        }
                    }
                  },
                  "update_advancement_period": 10,
                  "criteria": {
                    "criteria": "number",
                    "requirements": %s
                  },
                  "reward": {
                    "reward": "reward_package",
                    "rewards": [
                      {
                        "reward": "crux_currency",
                        "currency": "crux:orbit",
                        "amount": %s
                      },
                      {
                        "reward": "experience",
                        "amount": %s
                      }
                    ],
                    "display": {
                      "name": "%s",
                      "material": "%s"
                    }
                  },
                  "display": {
                    "position_origin": "%s",
                    "icon": {
                      "material": "%s"
                    },
                    "title": "%s",
                    "description": "%s",
                    "frame": "task",
                    "visibility": {
                      "type": "%s"
                    },
                    "x": %s,
                    "y": %s
                  }
                }
        """, parent, objectiveAmount, blockPredicate, criteriaRequirement, orbits, exp, titleName, materialID, parent, materialID, titleName, description, visibilityType,
                    x, y);

                Crux.logInfo(jsonText);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();

                var fileJson = new CruxJson(USurvivePlugin.inst(), "dev/generated/advancement/" + advanceID.replace(":", "/"), false);
                try {
                    // Ensure the directory exists
                    File file = fileJson.file();
                    file.getParentFile().mkdirs();

                    // Write to the file
                    try (FileWriter writer = new FileWriter(file)) {
                        gson.toJson(gson.fromJson(jsonText, Object.class), writer);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileJson.close();

                if(level >= levels){
                    break;
                }
            }
        }
    }
}
