package killercreepr.usurvive.api.dialog;

public interface DialogAction {
    void onStart(DialogPlayer player);
    void onEnd(DialogPlayer player);
    void onSpeak(DialogPlayer player);
}
