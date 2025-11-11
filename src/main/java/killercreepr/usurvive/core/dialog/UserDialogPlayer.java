package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettingSelections;
import killercreepr.usurvive.core.entity.player.settings.standard.StandardPlayerSettings;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.entity.Player;

public class UserDialogPlayer extends SimpleDialogPlayer{
    @Override
    public void onDialogCompleteLine(DialogLine line) {
        super.onDialogCompleteLine(line);
        audience().forEach(audience ->{
            Lang.DIALOG_LINE_COMPLETE.use(audience, TagContainer.merged().hook(new DialogContentLinePair(getDialog(), line)));
        });
    }

    @Override
    public void onDialogStartLine(DialogLine line) {
        super.onDialogStartLine(line);
        if(!(CruxCollection.getFirst(audience()) instanceof Player user)) return;

        var holder = USurvivePlugin.inst().getPlayerSettingsManager().getPlayerSettingsHolder(user);
        if(holder == null) return;
        var setting = holder.getSetting(StandardPlayerSettings.DIALOG_SPEED);
        int speed;
        if(setting == StandardPlayerSettingSelections.SLOW){
            speed = 1;
        }else if(setting == StandardPlayerSettingSelections.FAST){
            speed = 3;
        }else speed = 2;
        if(getDialogSplitSpeed() != speed) setDialogSplitSpeed(speed);


        setting = holder.getSetting(StandardPlayerSettings.DIALOG_WAIT_TIME);
        if(setting == StandardPlayerSettingSelections.SLOW){
            speed = 18;
        }else if(setting == StandardPlayerSettingSelections.FAST){
            speed = 8;
        }else speed = 12;

        if(getDialogWaitTime() != speed) setDialogWaitTime(speed);
    }
}
