package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.Crux;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SimpleDialogPlayer implements DialogPlayer {
    protected Queue<DialogContent> queue = new LinkedList<>();
    protected DialogContent dialog;
    protected String currentDialogLine;
    protected String[] currentDialogSplit;
    protected int dialogWait;
    protected final Collection<Audience> audience = new HashSet<>();

    public DialogContent getDialog() {
        return dialog;
    }

    public void setDialog(DialogContent dialog) {
        this.dialog = dialog;
        this.dialogIndex = 0;
        this.dialogSplitIndex = 0;
        this.currentDialogLine = null;
        this.currentDialogSplit = null;
        this.lastLine = null;
    }

    @Override
    public void onDialogStartLine(DialogLine line) {

    }

    @Override
    public DialogLine getCurrentLine() {
        return lastLine;
    }

    @Override
    public void queueDialog(DialogContent dialog) {
        if(dialog == null) return;
        if(this.dialog == null){
            setDialog(dialog);
        }else queue.add(dialog);
    }

    protected int dialogWaitTime = 10;
    protected int dialogIndex = 0;
    protected int dialogSplitIndex = 0;
    protected int dialogSplitSpeed = 1;

    public void onDialogCompleteLine(DialogLine line){

    }

    public void onDialogDisplay(Player user){
        if(currentDialogLine == null) return;
        Lang.DIALOG_LINE_DISPLAY.use(user, TagContainer.merged().hook(new DialogContentLinePair(getDialog(), getCurrentLine()))
            .add(Tag.parsed("line", currentDialogLine)));
        //user.sendActionBar(Crux.format().deserialize(currentDialogLine));
    }
    public void onDialogDisplaySpeaking(Player user){
        onDialogDisplay(user);
    }

    public void onDialogWaiting(Player... user){
        for (Player p : user) {
            onDialogDisplay(p);
        }
    }

    public void onDialogSpeaking(Player... user){
        for (Player p : user) {
            onDialogDisplaySpeaking(p);
        }
    }

    public void onDialogTicking(Player... user){

    }

    public void dialogTick(Player... user){
        if(dialog == null){
            if(queue.isEmpty()) return;
            setDialog(queue.poll());
            return;
        }
        onDialogTicking(user);
        if(dialogWait > 0){
            dialogWait--;
            if(currentDialogLine != null){
                onDialogWaiting(user);
            }
            return;
        }

        String dialog = getToSendDialogLine();
        if(dialog == null) return;
        onDialogSpeaking(user);
    }

    @Override
    public String getCurrentDialogLine() {
        return currentDialogLine;
    }

    @Override
    public int getDialogWaitTime() {
        return dialogWaitTime;
    }

    @Override
    public int getDialogWaiting() {
        return dialogWait;
    }

    @Override
    public void setDialogWaiting(int time) {
        dialogWait = time;
    }

    @Override
    public int getDialogSplitSpeed() {
        return dialogSplitSpeed;
    }

    @Override
    public void setDialogSplitSpeed(int speed) {
        this.dialogSplitSpeed = speed;
    }

    @Override
    public void setDialogWaitTime(int time) {
        this.dialogWaitTime = time;
    }

    @Override
    public void addAudience(Audience... audiences) {
        this.audience.addAll(Arrays.asList(audiences));
    }

    @Override
    public void removeAudience(Audience... audiences) {
        this.audience.removeAll(Arrays.asList(audiences));
    }

    @Override
    public boolean hasAudience(Audience audience) {
        return this.audience.contains(audience);
    }

    @Override
    public Collection<Audience> audience() {
        return audience;
    }

    protected DialogLine lastLine;
    public String getToSendDialogLine() {
        if (currentDialogLine == null) {
            if (dialogIndex >= dialog.dialog().size()) {
                setDialog(null);
                return null;
            }

            // Split into characters including formatting tags
            var line = dialog.dialog().get(dialogIndex);
            if(!line.equals(lastLine)){
                this.onDialogStartLine(line);
                dialog.actions().forEach(action ->{
                    action.onStart(this);
                });
                line.actions().forEach(action ->{
                    action.onStart(this);
                });
                //if any actions change the wait time
                if(dialogWait > 0){
                    lastLine = line;
                    return null;
                }
            }
            lastLine = line;
            List<String> split = getDialogStrings();
            if(split.isEmpty()){
                return null;
            }
            currentDialogSplit = split.toArray(new String[0]);
            currentDialogLine = currentDialogSplit[0];
            dialogSplitIndex = 0; // reset index
            return currentDialogLine;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(currentDialogLine);
        int dialogSplitSpeed = getDialogSplitSpeed();
        for(int i = 0; i < dialogSplitSpeed; i++){
            dialogSplitIndex++;

            if (dialogSplitIndex >= currentDialogSplit.length) {
                if(i != 0){
                    currentDialogLine = builder.toString();
                    var line = dialog.dialog().get(dialogIndex);
                    dialog.actions().forEach(action ->{
                        action.onSpeak(this);
                    });
                    line.actions().forEach(action ->{
                        action.onSpeak(this);
                    });
                    return currentDialogLine;
                }
                var line = dialog.dialog().get(dialogIndex);
                onDialogCompleteLine(line);

                dialogSplitIndex = 0;
                dialogIndex++;
                currentDialogLine = null;
                dialogWait = getDialogWaitTime();

                dialog.actions().forEach(action ->{
                    action.onEnd(this);
                });
                line.actions().forEach(action ->{
                    action.onEnd(this);
                });
                return null;
            }

            String nextPart = currentDialogSplit[dialogSplitIndex];

            // Immediately append formatting tags
            while (nextPart.startsWith("<") && nextPart.endsWith(">")) {
                builder.append(nextPart);
                //currentDialogLine += nextPart;
                dialogSplitIndex++;
                if (dialogSplitIndex >= currentDialogSplit.length) {
                    currentDialogLine = null;
                    dialogIndex++;
                    dialogWait = getDialogWaitTime();
                    return null;
                }
                nextPart = currentDialogSplit[dialogSplitIndex];
            }

            builder.append(nextPart);
            //currentDialogLine += nextPart;
        }
        //dialogWait = 1;
        currentDialogLine = builder.toString();
        var line = dialog.dialog().get(dialogIndex);
        dialog.actions().forEach(action ->{
            action.onSpeak(this);
        });
        line.actions().forEach(action ->{
            action.onSpeak(this);
        });
        return currentDialogLine;
    }

    @NotNull
    private List<String> getDialogStrings() {
        String line = dialog.dialog().get(dialogIndex).line();
        if(line == null) return List.of();
        List<String> split = new ArrayList<>();
        for (int i = 0; i < line.length(); ) {
            if (line.charAt(i) == '<') {
                // Capture the entire tag
                int end = line.indexOf('>', i);
                if (end != -1) {
                    split.add(line.substring(i, end + 1));
                    i = end + 1;
                    continue;
                }
            }
            // Regular character
            split.add(String.valueOf(line.charAt(i)));
            i++;
        }
        return split;
    }
}
