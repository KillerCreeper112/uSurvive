package killercreepr.usurvive.api.dialog;

import killercreepr.crux.api.component.DataComponentHandler;
import killercreepr.crux.api.data.holder.DataInfoHolder;
import killercreepr.crux.api.text.context.TextParserContext;
import killercreepr.crux.api.text.tags.container.MergedTagContainer;
import net.kyori.adventure.audience.Audience;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface DialogPlayer {
    DialogContent getDialog();
    void setDialog(DialogContent dialog);
    void onDialogStartLine(DialogLine line);
    DataComponentHandler components();
    float getProgress();

    boolean isEmpty();
    boolean isPlaying();
    void endCurrentDialog();

    <T> T getInfo(String id, BiFunction<DataInfoHolder, String, T> function);
    <T> T getInfoOrDefault(String id, BiFunction<DataInfoHolder, String, T> function, T fallback);

    String currentDialogLineFor(Audience audience);

    DialogLine getCurrentLine();

    void queueDialog(DialogContent dialog);
    void pushDialog(DialogContent content);
    void forcePushDialog(DialogContent content);

    void onDialogDisplay(Audience user);
    void dialogTick();
    String getCurrentDialogLine();
    int getDialogWaitTime();

    int getDialogWaiting();
    void setDialogWaiting(int time);

    int getDialogSplitSpeed();
    void setDialogSplitSpeed(int speed);

    void setDialogWaitTime(int time);

    void addAudience(Audience... audiences);
    void removeAudience(Audience... audiences);
    boolean hasAudience(Audience audience);
    Collection<Audience> audience();
    void forEachAudience(Consumer<Audience> consumer);
    TextParserContext parserContext();
    TextParserContext audienceParserContext(Audience audience);
    MergedTagContainer buildAudienceDynamicTags(Audience audience);
    MergedTagContainer buildDynamicTags();
}
