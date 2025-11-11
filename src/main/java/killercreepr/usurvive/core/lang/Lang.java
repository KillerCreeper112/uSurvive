package killercreepr.usurvive.core.lang;

import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.crux.api.communication.boss.CreateBossBar;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.core.communication.lang.Msg;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Lang {
    private static CreateLang lang;
    public static CreateLang lang(){
        return lang;
    }
    public static CreateLang setLang(CreateLang l){
        lang = l;
        return l;
    }

    public static final Msg GRAPPLE_USE = create(
        Communicator.builder()
            .sound(CreateSound.sound(Sound.ITEM_LEAD_BREAK, 1.5f))
            .build()
    );

    public static final Msg GRAPPLE_USE_SWAP_HANDS = create(
        Communicator.builder()
            .actionBar("<latinfont:swaphands & look up/down to climb/descend>")
            .build()
    );

    public static final Msg GRAPPLE_HOOK = create(
        Communicator.builder()
            .sound(CreateSound.sound(Sound.ITEM_LEAD_TIED, 1.5f))
            .build()
    );

    public static final Msg DISCORD = create(
        Communicator.builder()
            .chat("<yellow>Feel free to join our Discord here! <blue><click:open_url:\"https://discord.gg/twRpaSU4gV\"><hover:show_text:\"Click to join the uSurvive Discord!\">https://discord.gg/twRpaSU4gV")
            .sound(CreateSound.sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f))
            .build()
    );

    public static final Msg SPORELINK_DAMAGED = create(
        Communicator.builder()
            .chat("<<crux_potion_color>><latinfont:\"<crux_potion_name>\"> <red>Spores flare around you as linked pain is shared. <green><bold><hover:show_text:\"You have taken damage because a nearby<newline>spore-linked entity was harmed!<newline><newline>Sporelink is a new custom effect<newline>that shares damage between all<newline>entities who have the Sporelink effect.<newline>You can press TAB to see your<newline>active custom potion effects.\">HOVER FOR MORE INFO")
            .build()
    );

    public static final Msg CHECK_ORBITS = create(
        Communicator.builder()
            .chat("<yellow>You currently have <gold>{{<orbits>}[0]} orbits<yellow>.")
            .build()
    );

    public static final Msg ADVANCEMENT_PROGRESS = create(
        Communicator.builder()
            .chat(List.of(
                "<yellow><latinfont:advancement progress updated>",
                "      <gold><advancement_icon/title>",
                "      <#FF8954>{{<advancement_objective_progress_percent:<player_uuid>> * 100}[1]}% Complete"
            ))
            .build()
    );

    public static final Msg ADVANCEMENT_PROGRESS_CHALLENGE = create(
        Communicator.builder()
            .chat(List.of(
                "<yellow><latinfont:advancement progress updated>",
                "      <gold><advancement_icon/title>",
                "      <#FF8954>{{<advancement_objective_progress_percent:<player_uuid>> * 100}[1]}% Complete"
            ))
            .build()
    );

    public static final Msg ADVANCEMENT_PROGRESS_CRITERION_DISPLAY = create(
        Communicator.builder()
            .chat(List.of(
                "<yellow><latinfont:advancement progress updated>",
                "      <gold><advancement_icon/title>",
                "      <#FF8954>{{<advancement_objective_progress_percent:<player_uuid>> * 100}[1]}% Complete",
                "      <#9BEBE3><format> <progress>/<max>"
            ))
            .build()
    );

    public static final Msg ADVANCEMENT_PROGRESS_CRITERION_DISPLAY_CHALLENGE = create(
        Communicator.builder()
            .chat(List.of(
                "<yellow><latinfont:advancement progress updated>",
                "      <gold><advancement_icon/title>",
                "      <#FF8954>{{<advancement_objective_progress_percent:<player_uuid>> * 100}[1]}% Complete",
                "      <#9BEBE3><format> <progress>/<max>"
            ))
            .build()
    );

    public static final Msg ADVANCEMENT_COMPLETED = create(
        Communicator.builder()
            .build()
    );

    public static final Msg DEFAULT_TELEPORT_TICK_MSG = create(
        Communicator.builder()
            .actionBar("<yellow>Teleporting in <gold>{{<time_left> / 20}[1]} seconds")
            .sound(CreateSound.sound(Sound.ENTITY_SLIME_SQUISH, 2))
            .build()
    );
    public static final Msg DEFAULT_TELEPORT_SUCCESS_MSG = create(
        Communicator.builder()
            .actionBar("<green>Successfully teleported!")
            .sound(CreateSound.sound(Sound.ENTITY_PLAYER_TELEPORT))
            .build()
    );
    public static final Msg DEFAULT_TELEPORT_FAIL_MSG = create(
        Communicator.builder()
            .actionBar("<red>Teleport failed.")
            .sound(CreateSound.sound(Sound.ENTITY_VILLAGER_NO))
            .build()
    );
    public static final Msg FIRST_JOIN_WELCOME = create(
        Communicator.builder()
            .chat("<#C4F6EE>Welcome, <#82F5F0><player_name><#C4F6EE>, to uSurvive!")
            .broadcast(true)
            .build()
    );
    public static final Msg TPA_ACCEPTED = create(
        Communicator.builder()
            .chat("You have accepted <user_name>'s teleport request.")
            .build()
    );
    public static final Msg TPA_ACCEPTED_RECEIVED = create(
        Communicator.builder()
            .chat("<accepter_name> has accepted your teleport request!")
            .build()
    );
    public static final Msg TPA_DENIED = create(
        Communicator.builder()
            .chat("You have denied <user_name>'s teleport request.")
            .build()
    );
    public static final Msg TPA_DENIED_RECEIVED = create(
        Communicator.builder()
            .chat("<red><accepter_name> has denied your teleport request.")
            .build()
    );
    public static final Msg TPA_QUICK_ACCEPT_TOO_MANY_REQUESTS = create(
        Communicator.builder()
            .chat("<red>You have more than one teleport request. Use <white>/tpaccept <name></white>.")
            .build()
    );
    public static final Msg TPA_QUICK_DENY_TOO_MANY_REQUESTS = create(
        Communicator.builder()
            .chat("<red>You have more than one teleport request. Use <white>/tpdeny <name></white>.")
            .build()
    );
    public static final Msg TPA_CANNOT_ACCEPT_USER_TELEPORTING = create(
        Communicator.builder()
            .chat("<red><user_name> is currently teleporting. Their request cannot be accepted at this time.")
            .build()
    );
    public static final Msg TPA_NO_REQUESTS = create(
        Communicator.builder()
            .chat("<red>You have not received have any teleport requests.")
            .build()
    );
    public static final Msg TPA_NO_REQUEST_FROM_USER = create(
        Communicator.builder()
            .chat("<red>You have not received have any teleport requests from <user_name>, or it has expired.")
            .build()
    );
    public static final Msg TPA_CONFIRM_ALREADY_TELEPORTING = create(
        Communicator.builder()
            .chat("<red>You cannot confirm the teleportation while you are already set to teleport.")
            .build()
    );
    public static final Msg TPA_CONFIRM_RECEIVED = create(
        Communicator.builder()
            .chat("<yellow><user_name> has confirmed the teleportation and is now set to teleport to you!")
            .build()
    );
    public static final Msg TPA_CONFIRM_EXPIRED = create(
        Communicator.builder()
            .chat("<red>The teleport request has expired.")
            .build()
    );
    public static final Msg TPA_CONFIRM_CANCEL = create(
        Communicator.builder()
            .chat("<red>You have canceled the teleport.")
            .build()
    );
    public static final Msg TPA_CONFIRM_CANCEL_RECEIVED = create(
        Communicator.builder()
            .chat("<red><user_name> has decided to cancel the teleport.")
            .build()
    );
    public static final Msg TPA_REQUEST_SENT = create(
        Communicator.builder()
            .chat("<yellow>Teleport request sent to <target_name>. They have 60 seconds to accept.")
            .build()
    );
    public static final Msg TPA_REQUEST_SENT_RECEIVED = create(
        Communicator.builder()
            .chat("<yellow><user_name> has requested to teleport to you! Use <click:suggest_command:\"/tpaccept <user_name>\"><hover:show_text:\"Click to accept\n<user_name>'s teleport request\"><white>/tpaccept <user_name></white></hover></click> to accept.")
            .build()
    );
    public static final Msg TPA_HERE_REQUEST_SENT = create(
        Communicator.builder()
            .chat("<yellow>Teleport HERE request sent to <target_name>. They have 60 seconds to accept.")
            .build()
    );
    public static final Msg TPA_HERE_REQUEST_SENT_RECEIVED = create(
        Communicator.builder()
            .chat("<yellow><user_name> has requested that <latinfont:you> teleport to <latinfont:them>! Use <click:suggest_command:\"/tpaccept <user_name>\"><hover:show_text:\"Click to accept\n<user_name>'s teleport here request\"><white>/tpaccept <user_name></white></hover></click> to accept their teleport HERE request.")
            .build()
    );
    public static final Msg TPA_REQUEST_ALREADY_SENT = create(
        Communicator.builder()
            .chat("<red>You have already sent a teleport request to <target_name>.")
            .build()
    );
    public static final Msg TPA_REQUESTS_DISABLED = create(
        Communicator.builder()
            .chat("<red>You may not send a teleport request to <target_name>.")
            .build()
    );
    public static final Msg TPA_HERE_REQUESTS_DISABLED = create(
        Communicator.builder()
            .chat("<red>You may not send a teleport here request to <target_name>.")
            .build()
    );
    public static final Msg START_SLEEP = create(
        Communicator.builder()
            .title(CreateTitle.title(
                "<black><font:'crux:title'>1", null, 100, 999999999, 100
            ))
            .build()
    );
    public static final Msg START_DEEP_SLEEP = create(
        null
    );
    public static final Msg WAKE_FROM_SLEEP = create(
        Communicator.builder()
            .title(CreateTitle.title(
                "<black><font:'crux:title'>1", null, 0, 1, 25
            ))
            .build()
    );
    public static final Msg SLEEPING_TICK = create(
        Communicator.builder()
            .chat("<white>Sneak to wake up.")
            .build()
    );
    public static final Msg DEEP_SLEEPING_TICK = create(
        Communicator.builder()
            .chat("")
            .build()
    );
    public static final Msg BED_OBSTRUCTED = create(
        Communicator.builder()
            .actionBar("<red>Bed is obstructed.")
            .build()
    );
    public static final Msg BED_MONSTERS_NEARBY = create(
        Communicator.builder()
            .chat("You may not rest now; there are monster nearby.")
            .build()
    );
    public static final Msg BED_OCCUPIED = create(
        Communicator.builder()
            .actionBar("<red>Bed is occupied.")
            .build()
    );
    public static final Msg BED_TOO_FAR = create(
        Communicator.builder()
            .chat("You may not rest now; the best is too far away.")
            .build()
    );
    public static final Msg BED_CANNOT_USE_HERE = create(
        Communicator.builder()
            .actionBar("<red>Beds may not be used here.")
            .build()
    );
    public static final Msg BED_NOT_IN_HEALTHY_STATE = create(
        Communicator.builder()
            .chat("You may not rest now; you are not in a healthy state.")
            .build()
    );
    public static final Msg RESOURCE_WORLD_RESETTING = create(
        Communicator.builder()
            .chat("<aqua><yellow>The world is now resetting! You will be teleported to spawn...")
            .build()
    );
    public static final Msg RESOURCE_WORLD_RESETTING_IN = create(
        Communicator.builder()
            .chat("<aqua><latinfont:resource world> <yellow>The world is going to reset in <duration:\"<resetting_in>\">...")
            .build()
    );
    public static final Msg RESOURCE_WORLD_RESETTING_IN_TICK = create(
        Communicator.builder()
            .bossBar(CreateBossBar.builder()
                .key("resource_world")
                .progress("<last_reset>/<reset_time>")
                .color("yellow")
                .duration("100")
                .build())
            .build()
    );
    public static final Msg WARP_CANNOT_CREATE_IN_RESOURCE_WORLD = create(
        Communicator.builder()
            .chat("<red>You may not create warps in resource worlds.")
            .sound(CreateSound.sound(Sound.ENTITY_VILLAGER_NO))
            .build()
    );
    public static final Msg CLAIM_SETTINGS_CHANGED_SET = create(
        Communicator.builder()
            .chat("<gold>Note! <yellow>Your claim settings have been changed because you claimed a chunk in this world.")
            .sound(CreateSound.sound(Sound.BLOCK_SNIFFER_EGG_PLOP, 2f))
            .build()
    );
    public static final Msg GUIDE_STRUCTURE_CANNOT_BE_PLACED = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<gold>Whoops! <yellow>The fallen meteor structure could not be placed nearby. If you wish to continue the starting guide, please return to spawn and randomly teleport again. <click:run_command:\"/spawn\"><hover:show_text:\"Click to run /spawn\"><bold>CLICK HERE"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_SNIFFER_EGG_PLOP, 1.2f))
            .build()
    );
    public static final Msg GUIDE_STRUCTURE_PLACED = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Flame particles guide you toward the fallen meteor... Follow them."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg DIALOG_LINE_COMPLETE = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "{add_if:\"IF(<dialog_pair_has_info:prefix> == true, true, false)\":\"<dialog_pair_info:prefix_color:\"\"><latinfont:\"<dialog_pair_info:prefix>\">\"}",
                "<dialog_pair_info:line_color:\"\"><dialog_pair_line/line>"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 0.5f, 1.8f))
            .build()
    );
    public static final Msg DIALOG_LINE_DISPLAY = create(
        Communicator.builder()
            .actionBar("<dialog_pair_info:line_color:\"\"><line>")
            .build()
    );
    public static final Msg GUIDE_STRUCTURE_NEARBY = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>The fallen meteor is nearby! It contains a new ore inside. You'll need an iron pickaxe to mine it! Before you start getting supplies, you can set your home here using <hover:show_text:\"Click to use /sethome \"><click:suggest_command:\"/sethome\"><white>/sethome <name></white></click></hover>. Or you can use <hover:show_text:\"Click to use /homes\"><click:run_command:\"/homes\"><white>/homes</white></click></hover>!",
                "",
                "<yellow>When you are done with that, look around for a <white>campfire and a chest</white>. It contains supplies that will kick start your adventure!"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_WORLD_RESET = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>The world reset. Travel back to the Resource Overworld to discover your fallen meteor!"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_SHARD_USE = create(
        Communicator.builder()
            .title(CreateTitle.title(
                "<yellow>You have chosen", "<item_name><yellow>!",
                10, 40, 10
            ))
            .build()
    );
    public static final Msg GUIDE_1_CANNOT_USE_COMMANDS = create(
        Communicator.builder()
            .chat("<red>You cannot use commands until you enter the <blue>Resource Overworld</blue>.")
            .build()
    );
    public static final Msg GUIDE_FIRST_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Welcome to uSurvive!",
                "<yellow>Nice to meet you, fella! My name is not important and I need your attention now!",
                "<yellow>A meteor has fallen! Enter the <white>Resource Overworld</white> to find it <click:run_command:\"/rtp\"><hover:show_text:\"Click to run /rtp\"><white><bold>(CLICK)</bold></white></hover></click>."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_SNIFFER_EGG_PLOP, 1.2f))
            .build()
    );
    public static final Msg GUIDE_SECOND_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Great! You have just mined <white>Orbit Ore</white>, which drops <white>Orbits</white>! Orbits are uSurvive's main global currency. They can be used to purchase various things, like items from the <white>Merchants</white> at spawn. You can head back to spawn using <click:run_command:\"/spawn\"><hover:show_text:\"Click to run /spawn\"><white>/spawn</white></hover></click>."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_SECOND_GUIDE_SPAWN = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Now, to use orbits, they must be deposited into your <white>Orbit Stash</white>. You can do this by going to an <white>Orbit Conduit</white>. Follow the particles. Make sure you don't accidentally fall into any portals. "
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_THIRD_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>Perfect! Now, try selling or buying items from the merchants at spawn!"
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_FOURTH_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>You have done great! It may be a good time to settle in and create a base so you can store all your goodies securely. Head to the <white>Main Overworld <hover:show_text:\"Click to use /rtp\"><click:run_command:\"/rtp\"><bold>(CLICK)</bold></click></hover></white>. It will never get reset so it is a perfect place to build a base and <white>Claim</white>!"
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_FIFTH_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>Now that you're here, you can use <hover:show_text:\"Click to use /claim\"><click:run_command:\"/claim\"><white>/claim</white></click></hover> and start claiming some land!"
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_SIXTH_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>You can exit Astraclaim View by clicking anywhere in your inventory. Once you do that, use <hover:show_text:\"Click to use /quests\"><click:run_command:\"/quests\"><white>/quests</white></click></hover> and complete one of the quests! There are plenty to choose from."
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_SEVENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>And that is a basic precept of some of the features that uSurvive has! If you need help, we have <hover:show_text:\"Click to use /guide\"><click:run_command:\"/guide\"><white>/guide</white></click></hover> and <hover:show_text:\"Click to use /discord\"><click:run_command:\"/discord\"><white>/discord</white></click></hover>.",
                "<yellow>There are still many things to discover... including the Abyss Dimension! But first, get a full set of Diamond armor or Rose Gold <hover:show_text:\"Click to use /help rosegold\"><click:run_command:\"/help rosegold\"><white><bold>(?)</bold></white></click></hover> armor. Then we'll talk. Stay safe, you never know what's out there.."
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_EIGHTH_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>You have been doing a fantastic job! Have you been exploring and figuring things out on your own? Before thinking about entering the Abyss, it's best you gear up. It is a nasty place.",
                "<yellow>Craft a Copper Staff, then we'll go from there. You can find the recipe if you use <hover:show_text:\"Click to use /recipes\"><click:run_command:\"/recipes\"><white>/recipes</white></click></hover>. I assume you've collected at least one copper ingot, eh?"
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    //obtain soul essence
    public static final Msg GUIDE_NINTH_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>Great! But not so great is the copper staff by itself is quite weak and boring. However, you can apply spells to it! You're going to need some Soul Essence and soul sand first. Head into the Nether and dig around in soul sand or soul soil areas to find Spectral Soil- it has blue streaks in it.",
                "<yellow>When looking for materials in general, it's best to look in the resource worlds since they reset periodically. To get to the Resource Nether, head to the Resource Overworld and enter a nether portal."
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    //craft soul orb
    public static final Msg GUIDE_TENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>You got some Soul Essence- perfect! Now you need to make that into a Soul Orb. Again, the recipe can be found within <hover:show_text:\"Click to use /recipes\"><click:run_command:\"/recipes\"><white>/recipes</white></click></hover>.",
                "<yellow>Pro tip: If you click on the result item while viewing a recipe in the custom recipe book, it'll automatically try to find a crafting table and place all the ingredients in it."
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_ELEVENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                    "",
                    "",
                    "<dark_aqua><latinfont:Guide>",
                    "<yellow>Here comes the tricky part. You're going to need to kill a certain mob while holding the Soul Orb in one of your hands.",
                "<yellow>The goal is to consume the mob's soul. Each mob soul has a different spell attached to it. Not all mobs have been implemented though. Check <hover:show_text:\"Click to use /help copper_staff\"><click:run_command:\"/help copper_staff\"><white>/help copper_staff</white></click></hover> and navigate to Current Mobs to see all of the currently implemented mobs. You may need to kill a bunch of the same mob to extract a soul from them."
                ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_TWELFTH_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>That's the spirit! When the Soul Orb reaches 100% and then you kill one last mob (of the same type), that is when their soul will release! Then you can capture it within the Soul Orb."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_THIRTEENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Fantastic work! To apply the Soul Orb onto your Copper Staff, you'll need to build an Abyss Altar. Sounds scary, huh? Cause it is... kind of. Anyway, open your journal, <hover:show_text:\"Click to use /help abyss\"><click:run_command:\"/help abyss\"><white>/help abyss</white></click></hover>, and navigate to the Traveling to the Abyss section to see how to build the Abyss Altar."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_FOURTEENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Go to your Abyss Altar, make sure the candles are lit. Take your Copper Staff and right click the enchanting table part. After that, do the same with your Soul Orb. Then.. watch some magic happen!"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_FIFTEENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Haha, you did it! How about you try it out? Hold right click while holding the Copper Staff to cast the spell!"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_SIXTEENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Whadja' think? Excited? Disappointed? Regardless, there are more spells out there for you to try. Look, I'm not so sure if you're ready for the Abyss. It's dangerous. It's unforgiving.",
                "<yellow>Ah, whatever. You're here for adventure, right? Hold a Red Abyss Crystal in your hand and right click the enchanting table from the Abyss Altar. Make sure the candles are lit. Red Abyss Crystals can be found all over the world, where the Abyss is seeping through. Look for them in the Resource Overworld. <hover:show_text:\"Click to use /help abyss\"><click:run_command:\"/help abyss\"><white>/help abyss</white></click></hover>"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_SEVENTEENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Well, don't say I didn't warn you. This is on you."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_EIGHTEENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<gold>Wow, you're really gullible. sknaht."
            ))
            .sound(CreateSound.sound(Sound.ENTITY_ENDERMAN_AMBIENT, .3f))
            .build()
    );
    public static final Msg GUIDE_EIGHTEENTH_START_2 = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>You did great! Hopefully you can get out of here safely, eh? You got your Diamond or Rose Gold gear? You're going to need it to get Mouldite <hover:show_text:\"Click to use /help mouldite\"><click:run_command:\"/help mouldite\"><white><bold>(?)</bold></white></click></hover>. Mouldite is required to mine ancient debris to get netherite.",
                "<yellow>First, you'll need to get a Mouldite Upgrade. Travel around on the surface and look for ruined obsidian tower-looking structures. You need to defeat some trials, obtain Plague Keys and unlock vaults to try to get Mouldite Upgrades."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_NINETEENTH_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Hm. I'm surprised you've made it this far. Interesting...",
                "<yellow>Well, great! Now to upgrade your gear with Mouldite, you're going to need to mine for Mouldite. This item can be tricky to get.. What are the developers thinking? Two difficult to obtain items just to upgrade one item... Don't trust them, they are against us!",
                "<yellow>Mouldite Crust generates all the way down near the void... Good luck!"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_20_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>I am intrigued by your resilience. Most people don't make it far... Do you want to upgrade your gear before you leave the Abyss? Too bad. We're leaving first. This might be one of the trickiest tasks.",
                "<yellow>You need to find an Abyss Safezone. "
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_21_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Go to the center of the safezone. You should find a unique block in the middle of it. When you walk close to it, it will open up a portal that teleports you to spawn, so you can get out of this mess."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_22_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>What did you think of the Abyss? There's a lot more to it, if you haven't explored around. Especially if you're into capturing territory, factions-style, there are Abyss Outposts. Check out <hover:show_text:\"Click to use /help abyss\"><click:run_command:\"/help abyss\"><white>/help abyss</white></click></hover> for more information. The guide has a lot of useful info.",
                "<yellow>Now, how about you upgrade your Diamond or Rose Gold gear to Mouldite? Get to a smithing table and input 1 Mouldite Upgrade item, 1 Diamond or Rose Gold item, and 1 Mouldite. And bam! (FYI: You may way to duplicate the Mouldite Upgrade <hover:show_text:\"Click to use /recipes\"><click:run_command:\"/recipes\"><white>/recipes</white></click></hover>)"
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );
    public static final Msg GUIDE_23_START = create(
        Communicator.builder()
            .chat(List.of(
                "",
                "",
                "<dark_aqua><latinfont:Guide>",
                "<yellow>Well, you did it. And the journey is just getting started. Who knew a fella like you could do so well.",
                "<yellow>That's it from me for now. I'll be in hidden corners that you'll never find. Hahaha, until next time."
            ))
            .sound(CreateSound.sound(Sound.BLOCK_COMPARATOR_CLICK, 1.8f))
            .build()
    );

    public static Msg create(@NotNull String id, @Nullable Communicator communicator){
        return new Msg(id, communicator, Lang::lang);
    }

    public static Msg create(@Nullable Communicator communicator){
        return new Msg(communicator, Lang::lang);
    }

}
