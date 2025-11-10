package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.core.lang.Lang;
import org.bukkit.entity.Player;

public class UserDialogPlayer extends SimpleDialogPlayer{
    protected Player user;

    public UserDialogPlayer() {
    }

    public UserDialogPlayer(Player user) {
        this.user = user;
    }

    public Player getUser() {
        return user;
    }

    public void setUser(Player user) {
        this.user = user;
    }

    @Override
    public void onDialogCompleteLine(DialogLine line) {
        super.onDialogCompleteLine(line);
        if(user == null) return;
        Lang.GUIDE_DIALOG_LINE_COMPLETE.use(user, TagContainer.merged().add(Tag.parsed("line", line.line())));
    }
}
