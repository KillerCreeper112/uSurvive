package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.component.DataComponentHandler;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.core.component.DialogComponents;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.*;
//todo ADD MULTI-PLAYER SUPPORT for TEXT DISPLAYS
public class SimpleDialogPlayer implements DialogPlayer {
    protected Queue<DialogContent> queue = new LinkedList<>();
    protected DialogContent dialog;
    protected String currentDialogLine;
    protected String[] currentDialogSplit;
    protected int dialogWait;
    protected final Collection<Audience> audience = new HashSet<>();
    protected final Map<Audience, TextParserContext> cachedContext = new HashMap<>();
    protected final DataComponentHandler components = new DataComponentHandler.Simple();

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
        this.cachePreParsedIndex = -1;
    }

    @Override
    public void onDialogStartLine(DialogLine line) {

    }

    @Override
    public DataComponentHandler components() {
        return components;
    }

    @Override
    public String currentDialogLineFor(Audience audience) {
        String currentDialogLine;
        if(this.currentDialogLine == null){
            if(dialog != null && dialogIndex < dialog.dialog().size()){
                currentDialogLine = dialog.dialog().get(dialogIndex).line();
            }else return null;
        }else currentDialogLine = this.currentDialogLine;
        if(cachePreParsedIndex == dialogIndex) return currentDialogLine;
        return parserContext(audience).deserializeString(currentDialogLine);
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

    public void onDialogDisplay(Audience user){
        if(currentDialogLine == null) return;
        Lang.DIALOG_LINE_DISPLAY.use(user, parserContext(user));
    }
    public void onDialogDisplaySpeaking(Audience user){
        onDialogDisplay(user);
    }

    public void onDialogWaiting(){
        for (Audience p : audience()) {
            onDialogDisplay(p);
        }
    }

    public void onDialogSpeaking(){
        for (Audience p : audience()) {
            onDialogDisplaySpeaking(p);
        }
    }

    public void onDialogTicking(){

    }

    public void dialogTick(){
        if(dialog == null){
            if(queue.isEmpty()) return;
            setDialog(queue.poll());
            return;
        }
        onDialogTicking();
        if(dialogWait > 0){
            dialogWait--;
            if(currentDialogLine != null){
                onDialogWaiting();
            }
            return;
        }

        String dialog = getToSendDialogLine();
        if(dialog == null) return;
        onDialogSpeaking();
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
        for (Audience audience1 : audiences) {
            this.cachedContext.remove(audience1);
        }
    }

    @Override
    public boolean hasAudience(Audience audience) {
        return this.audience.contains(audience);
    }

    @Override
    public Collection<Audience> audience() {
        return audience;
    }

    @Override
    public TextParserContext parserContext(Audience audience) {
        var got = cachedContext.get(audience);
        if(got != null){
            return TextParserContext.wrapped(got, buildDynamicTags(audience));
        }
        var ctx = TextParserContext.builder()
            .tags(TagContainer.merged().hook(audience))
            .build();
        //only cache if this is for an audience for this
        if(hasAudience(audience)) cachedContext.put(audience, ctx);
        return TextParserContext.wrapped(ctx, buildDynamicTags(audience));
    }

    @Override
    public MergedTagContainer buildDynamicTags(Audience audience) {
        var tags = TagContainer.merged();
        if(getCurrentLine() != null){
            tags.hook(getCurrentLine());
        }
        if(getDialog() != null && getCurrentLine() != null){
            tags.hook(new DialogContentLinePair(getDialog(), getCurrentLine()));
        }
        if(currentDialogLine != null){
            tags.add(Tag.parsed("line", currentDialogLine));
        }
        return tags;
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

    protected int cachePreParsedIndex;

    @NotNull
    private List<String> getDialogStrings() {
        String line = dialog.dialog().get(dialogIndex).line();
        if(line == null) return List.of();

        if(components.getOrDefault(DialogComponents.PRE_PARSE_FOR_SINGLE_AUDIENCE, true) && audience.size() == 1){
            if(dialogIndex != cachePreParsedIndex){
                line = currentDialogLineFor(CruxCollection.getFirst(audience()));
                cachePreParsedIndex = dialogIndex;
            }
        }

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
