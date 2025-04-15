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
            .sound(CreateSound.sound(Sound.ENTITY_LEASH_KNOT_BREAK, 1.5f))
            .build()
    );

    public static final Msg GRAPPLE_HOOK = create(
        Communicator.builder()
            .sound(CreateSound.sound(Sound.ENTITY_LEASH_KNOT_PLACE, 1.5f))
            .build()
    );

    public static final Msg DISCORD = create(
        Communicator.builder()
            .chat("<yellow>Feel free to join our Discord here! <blue><click:open_url:\"https://discord.gg/twRpaSU4gV\"><hover:show_text:\"Click to join the uSurvive Discord!\">https://discord.gg/twRpaSU4gV")
            .sound(CreateSound.sound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.5f))
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

    public static Msg create(@NotNull String id, @Nullable Communicator communicator){
        return new Msg(id, communicator, Lang::lang);
    }

    public static Msg create(@Nullable Communicator communicator){
        return new Msg(communicator, Lang::lang);
    }

}
