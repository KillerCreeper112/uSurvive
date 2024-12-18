package killercreepr.usurvive.core.lang;

import killercreepr.crux.api.communication.Communicator;
import killercreepr.crux.api.communication.CreateSound;
import killercreepr.crux.api.communication.lang.CreateLang;
import killercreepr.crux.core.communication.lang.Msg;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

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

    public static Msg create(@NotNull String id, @NotNull Communicator communicator){
        return new Msg(id, communicator, Lang::lang);
    }

    public static Msg create(@NotNull Communicator communicator){
        return new Msg(communicator, Lang::lang);
    }

}
