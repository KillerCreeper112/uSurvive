package killercreepr.usurvive.core.dialog;

import killercreepr.crux.api.component.DataComponentHandler;
import killercreepr.crux.api.data.holder.DataInfoHolder;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.crux.core.util.CruxCollection;
import killercreepr.crux.core.util.CruxMath;
import killercreepr.usurvive.api.dialog.DialogContent;
import killercreepr.usurvive.api.dialog.DialogLine;
import killercreepr.usurvive.api.dialog.DialogPlayer;
import killercreepr.usurvive.core.component.DialogComponents;
import killercreepr.usurvive.core.lang.Lang;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;

//todo ADD MULTI-PLAYER SUPPORT for TEXT DISPLAYS
public class SimpleDialogPlayer implements DialogPlayer {
    public static class Data{
        public DialogContent dialog;
        public String currentDialogLine;
        public String[] currentDialogSplit;
        public int dialogWait;
        public int dialogIndex = 0;
        public int dialogSplitIndex = 0;
        protected int cachePreParsedIndex = -1;
        protected DialogLine lastLine;
    }

    protected LinkedList<DialogContent> queue = new LinkedList<>();
    protected LinkedList<Data> pushedQueue = new LinkedList<>();
    protected Data currentDialog = new Data();
    protected int dialogWaitTime = 10;
    protected int dialogSplitSpeed = 1;

    protected final Collection<Audience> audience = new HashSet<>();
    protected final Map<Audience, TextParserContext> cachedContext = new HashMap<>();
    protected final DataComponentHandler components = new DataComponentHandler.Simple();

    @Override
    public void forcePushDialog(DialogContent content) {
        if(content == null) return;
        var current = getDialog();
        if(current == null){
            setDialog(content);
            return;
        }
        pushedQueue.add(currentDialog);
        current.onPause(this);
        setDialog(content);
    }

    @Override
    public void pushDialog(DialogContent content){
        if(content == null) return;
        var current = getDialog();
        if(current == null){
            setDialog(content);
            return;
        }
        if(content.priority() > current.priority()){
            pushedQueue.add(currentDialog);
            current.onPause(this);
            setDialog(content);
            return;
        }
        queueDialog(content);
    }

    public DialogContent getDialog() {
        return currentDialog.dialog;
    }

    public void setDialog(DialogContent dialog) {
        /*this.dialog = dialog;
        this.dialogIndex = 0;
        this.dialogSplitIndex = 0;
        this.currentDialogLine = null;
        this.currentDialogSplit = null;
        this.lastLine = null;
        this.cachePreParsedIndex = -1;*/
        this.currentDialog = new Data();
        this.currentDialog.dialog = dialog;
        if(dialog != null) dialog.onStart(this);
    }

    public <T> T getInfo(String id, BiFunction<DataInfoHolder,String, T> function){
        return getInfoOrDefault(id, function, null);
    }

    public <T> T getInfoOrDefault(String id, BiFunction<DataInfoHolder, String, T> function, T fallback){
        if(getCurrentLine() != null){
            if(getCurrentLine().info().has(id)) return function.apply(getCurrentLine(), id);
        }
        if(getDialog() != null){
            return function.apply(getDialog(), id);
        }
        return fallback;
    }

    @Override
    public void onDialogStartLine(DialogLine line) {

    }

    @Override
    public DataComponentHandler components() {
        return components;
    }

    @Override
    public float getProgress() {
        if(getDialog() == null) return 1f;
        return CruxMath.clamp((float) currentDialog.dialogIndex / (float) getDialog().dialog().size()-1, 0f, 1f);
    }

    @Override
    public boolean isEmpty() {
        return getDialog() == null && queue.isEmpty() && pushedQueue.isEmpty();
    }

    @Override
    public String currentDialogLineFor(Audience audience) {
        String currentDialogLine;
        if(currentDialog.currentDialogLine == null){
            if(getDialog() != null && currentDialog.dialogIndex < getDialog().dialog().size()){
                currentDialogLine = getDialog().dialog().get(currentDialog.dialogIndex).line();
            }else return null;
        }else currentDialogLine = currentDialog.currentDialogLine;
        if(currentDialog.cachePreParsedIndex == currentDialog.dialogIndex) return currentDialogLine;
        return audienceParserContext(audience).deserializeString(currentDialogLine);
    }

    @Override
    public DialogLine getCurrentLine() {
        return currentDialog.lastLine;
    }

    @Override
    public void queueDialog(DialogContent dialog) {
        if(dialog == null) return;
        if(getDialog() == null){
            setDialog(dialog);
        }else queue.add(dialog);
    }

    public void onDialogCompleteLine(DialogLine line){

    }

    public void onDialogDisplay(Audience user){
        if(currentDialog.currentDialogLine == null) return;
        Lang.DIALOG_LINE_DISPLAY.use(user, audienceParserContext(user));
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

    public Data pollNextPushed(){
        int index = -1;
        for (Data data : pushedQueue) {
            index++;
            if(data.dialog.canPlay(this)){
                return pushedQueue.remove(index);
            }
        }
        return null;
    }

    public DialogContent pollNextQueued(){
        int index = -1;
        for (DialogContent data : queue) {
            index++;
            if(data.canPlay(this)){
                return queue.remove(index);
            }
        }
        return null;
    }

    public void dialogTick(){
        var dialog = getDialog();
        if(dialog == null){
            if(!pushedQueue.isEmpty()){
                var nextPushed = pollNextPushed();
                if(nextPushed != null) currentDialog = nextPushed;
                return;
            }
            if(queue.isEmpty()) return;
            var next = pollNextQueued();
            if(next != null) setDialog(next);
            return;
        }
        onDialogTicking();
        if(currentDialog.dialogWait > 0){
            currentDialog.dialogWait--;
            if(currentDialog.currentDialogLine != null){
                onDialogWaiting();
            }
            return;
        }
        if(!dialog.canPlay(this)){
            dialog.onPause(this);
            var remove = getInfoOrDefault("remove_on_cant_play", (info, id) -> info.info().getOrDefault(id, Boolean.class, false), false);
            if(!remove) pushedQueue.offerFirst(currentDialog);
            setDialog(null);
            return;
        }

        String dialogText = getToSendDialogLine();
        if(dialogText == null) return;
        onDialogSpeaking();
    }

    @Override
    public String getCurrentDialogLine() {
        return currentDialog.currentDialogLine;
    }

    @Override
    public int getDialogWaitTime() {
        return dialogWaitTime;
    }

    @Override
    public int getDialogWaiting() {
        return currentDialog.dialogWait;
    }

    @Override
    public void setDialogWaiting(int time) {
        currentDialog.dialogWait = time;
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
    public TextParserContext parserContext() {
        return TextParserContext.builder()
            .tags(buildDynamicTags())
            .build();
    }

    @Override
    public TextParserContext audienceParserContext(Audience audience) {
        var got = cachedContext.get(audience);
        if(got != null){
            return TextParserContext.wrapped(got, buildAudienceDynamicTags(audience));
        }
        var ctx = TextParserContext.builder()
            .tags(TagContainer.merged().hook(audience))
            .build();
        //only cache if this is for an audience for this
        if(hasAudience(audience)) cachedContext.put(audience, ctx);
        return TextParserContext.wrapped(ctx, buildAudienceDynamicTags(audience));
    }

    @Override
    public MergedTagContainer buildDynamicTags() {
        var tags = TagContainer.merged();
        if(getCurrentLine() != null){
            tags.hook(getCurrentLine());
        }
        if(getDialog() != null && getCurrentLine() != null){
            tags.hook(new DialogContentLinePair(getDialog(), getCurrentLine()));
        }
        if(currentDialog.currentDialogLine != null){
            tags.add(Tag.parsed("line", currentDialog.currentDialogLine));
        }
        return tags;
    }

    @Override
    public MergedTagContainer buildAudienceDynamicTags(Audience audience) {
        return buildDynamicTags();
    }

    public String getToSendDialogLine() {
        if (currentDialog.currentDialogLine == null) {
            if (currentDialog.dialogIndex >= getDialog().dialog().size()) {
                var current = getDialog();
                setDialog(null);
                if(current != null){
                    current.onDialogEnd(this);
                    current.onEnd(this);
                }
                return null;
            }

            // Split into characters including formatting tags
            var line = getDialog().dialog().get(currentDialog.dialogIndex);
            if(!line.equals(currentDialog.lastLine)){
                this.onDialogStartLine(line);
                getDialog().actions().forEach(action ->{
                    action.onStart(this);
                });
                line.actions().forEach(action ->{
                    action.onStart(this);
                });
                //if any actions change the wait time
                if(currentDialog.dialogWait > 0){
                    currentDialog.lastLine = line;
                    return null;
                }
            }
            currentDialog.lastLine = line;
            List<String> split = getDialogStrings();
            if(split.isEmpty()){
                return null;
            }
            currentDialog.currentDialogSplit = split.toArray(new String[0]);
            currentDialog.currentDialogLine = currentDialog.currentDialogSplit[0];
            currentDialog.dialogSplitIndex = 0; // reset index
            return currentDialog.currentDialogLine;
        }

        StringBuilder builder = new StringBuilder();
        builder.append(currentDialog.currentDialogLine);
        int dialogSplitSpeed = getDialogSplitSpeed();
        for(int i = 0; i < dialogSplitSpeed; i++){
            currentDialog.dialogSplitIndex++;

            if (currentDialog.dialogSplitIndex >= currentDialog.currentDialogSplit.length) {
                if(i != 0){
                    currentDialog.currentDialogLine = builder.toString();
                    var line = getDialog().dialog().get(currentDialog.dialogIndex);
                    getDialog().actions().forEach(action ->{
                        action.onSpeak(this);
                    });
                    line.actions().forEach(action ->{
                        action.onSpeak(this);
                    });
                    return currentDialog.currentDialogLine;
                }
                var line = getDialog().dialog().get(currentDialog.dialogIndex);
                onDialogCompleteLine(line);

                currentDialog.dialogSplitIndex = 0;
                currentDialog.dialogIndex++;
                currentDialog.currentDialogLine = null;
                currentDialog.dialogWait = getDialogWaitTime();

                getDialog().actions().forEach(action ->{
                    action.onEnd(this);
                });
                line.actions().forEach(action ->{
                    action.onEnd(this);
                });
                return null;
            }

            String nextPart = currentDialog.currentDialogSplit[currentDialog.dialogSplitIndex];

            // Immediately append formatting tags
            while (nextPart.startsWith("<") && nextPart.endsWith(">")) {
                builder.append(nextPart);
                //currentDialogLine += nextPart;
                currentDialog.dialogSplitIndex++;
                if (currentDialog.dialogSplitIndex >= currentDialog.currentDialogSplit.length) {
                    currentDialog.currentDialogLine = null;
                    currentDialog.dialogIndex++;
                    currentDialog.dialogWait = getDialogWaitTime();
                    return null;
                }
                nextPart = currentDialog.currentDialogSplit[currentDialog.dialogSplitIndex];
            }

            builder.append(nextPart);
            //currentDialogLine += nextPart;
        }
        //dialogWait = 1;
        currentDialog.currentDialogLine = builder.toString();
        var line = getDialog().dialog().get(currentDialog.dialogIndex);
        getDialog().actions().forEach(action ->{
            action.onSpeak(this);
        });
        line.actions().forEach(action ->{
            action.onSpeak(this);
        });
        return currentDialog.currentDialogLine;
    }

    @NotNull
    private List<String> getDialogStrings() {
        String line = getDialog().dialog().get(currentDialog.dialogIndex).line();
        if(line == null) return List.of();

        if(components.getOrDefault(DialogComponents.PRE_PARSE_FOR_SINGLE_AUDIENCE, true) && audience.size() == 1){
            if(currentDialog.dialogIndex != currentDialog.cachePreParsedIndex){
                line = currentDialogLineFor(CruxCollection.getFirst(audience()));
                currentDialog.cachePreParsedIndex = currentDialog.dialogIndex;
            }
        }
        if(currentDialog.cachePreParsedIndex != currentDialog.dialogIndex && components.getOrDefault(DialogComponents.PRE_PARSE_GLOBAL, true)){
            line = parserContext().deserializeString(line);
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
