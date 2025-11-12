package killercreepr.usurvive.core.dialog.listener;

import killercreepr.crux.api.entity.memory.PlayerMemory;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.entity.memory.PlayerTickedDataHolder;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.api.dialog.listener.DialogListener;
import killercreepr.usurvive.core.dialog.UserDialogPlayer;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerDialogListener extends PlayerTickedDataHolder implements DialogListener {
    public static final Key KEY = Crux.key("dialog/player/listener");
    public PlayerDialogListener(@NotNull Key key, @NotNull PlayerMemory parent) {
        super(key, parent);
    }
    public PlayerDialogListener(@NotNull PlayerMemory parent) {
        this(KEY, parent);
    }

    @Override
    public boolean shouldRemoveFromMemory(@Nullable Player e) {
        return super.shouldRemoveFromMemory(e) || (dialogPlayer.getDialog() == null && lastHadDialog > 0 && !CruxMath.hasOccurredWithin(lastHadDialog, 60));
    }

    protected int tickRate = 2;
    protected long lastHadDialog;
    protected DialogPlayer dialogPlayer = new UserDialogPlayer();


    public int getTickRate() {
        return tickRate;
    }

    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }

    public long getLastHadDialog() {
        return lastHadDialog;
    }

    public void setLastHadDialog(long lastHadDialog) {
        this.lastHadDialog = lastHadDialog;
    }

    public DialogPlayer getDialogPlayer() {
        return dialogPlayer;
    }

    public void setDialogPlayer(DialogPlayer dialogPlayer) {
        this.dialogPlayer = dialogPlayer;
    }

    @Override
    protected void onTick(@NotNull Player e) {
        super.onTick(e);
        if(dialogPlayer.getDialog() != null){
            lastHadDialog = System.currentTimeMillis();
        }
        if(Crux.getCurrentTick() % tickRate == 0){
            dialogPlayer.dialogTick();
        }
    }

    @Override
    public void adding() {
        super.adding();
        var got = parent.value();
        if(got != null) dialogPlayer.addAudience(got);
    }

    @Override
    public DialogPlayer dialogPlayer() {
        return dialogPlayer;
    }
}
