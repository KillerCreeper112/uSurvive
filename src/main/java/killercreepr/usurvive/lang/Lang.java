package killercreepr.usurvive.lang;

import killercreepr.crux.data.communication.Communicator;
import killercreepr.crux.data.communication.CreateLang;
import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.data.communication.Msg;
import org.bukkit.Sound;
import org.jetbrains.annotations.NotNull;

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

    public static Msg create(@NotNull String id, @NotNull Communicator communicator){
        return new Msg(id, communicator, Lang::lang);
    }

    public static Msg create(@NotNull Communicator communicator){
        return new Msg(communicator, Lang::lang);
    }

}
