package killercreepr.usurvive.api.dialog.listener;

import killercreepr.crux.api.entity.memory.EntityMemory;
import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.core.dialog.listener.PlayerDialogListener;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface DialogListener {
    static DialogListener listener(Audience e){
        if(e instanceof Entity ee) return listener(ee);
        return null;
    }
    static DialogListener listener(Entity e){
        if(e instanceof Player){
            return EntityMemory.getOrCreateDataHolder(e, PlayerDialogListener.class, mem ->{
                if(mem instanceof PlayerMemory m) return new PlayerDialogListener(m);
                return null;
            });
        }
        return null;
    }

    DialogPlayer dialogPlayer();
}
