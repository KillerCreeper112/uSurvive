package killercreepr.usurvive.lang;

import killercreepr.crux.data.communication.CreateLang;
import killercreepr.crux.data.communication.CreateSound;
import killercreepr.crux.data.communication.MsgContainer;
import org.bukkit.Sound;

public class Lang {
    private static CreateLang lang;
    public static CreateLang lang(){
        return lang;
    }
    public static CreateLang setLang(CreateLang l){
        lang = l;
        return l;
    }

    public static final Msg ENTER_CLAIM_VIEW = new Msg("claim_enter_view",
        new MsgContainer.Builder()
            .actionBar("<red><latinfont:Astraclaim Entered>")
            .sound(new CreateSound(Sound.BLOCK_BEACON_ACTIVATE, 1.5f))
            .build()
    );

}
