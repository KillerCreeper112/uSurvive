package killercreepr.usurvive.core.mechanics.bone_flute;

import killercreepr.crux.core.Crux;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FlutePlayer {
    protected final FluteSoundSequence sequence;
    protected final Consumer<FluteSoundSequence.FluteSound> onPlay;
    protected final Supplier<Boolean> isValid;

    public FlutePlayer(FluteSoundSequence sequence, Consumer<FluteSoundSequence.FluteSound> onPlay, Supplier<Boolean> isValid) {
        this.sequence = sequence;
        this.onPlay = onPlay;
        this.isValid = isValid;
    }

    protected BukkitRunnable runnable;
    protected int tick = 0;
    protected int index = 0;
    protected int nextTarget = 0; // tick target for the next sound

    public FluteSoundSequence.FluteSound getCurrentSound() {
        return sequence.sounds().get(index);
    }

    public boolean hasNext() {
        return index + 1 < sequence.sounds().size();
    }

    public FluteSoundSequence.FluteSound next(double scale) {
        index++;
        FluteSoundSequence.FluteSound sound = getCurrentSound();
        nextTarget += (int) Math.round(sound.waitTime() * scale);
        return sound;
    }

    public void stop() {
        try {
            runnable.cancel();
        } catch (NullPointerException | IllegalStateException ignored) {}
    }

    public FlutePlayer play(int consumeTime) {
        List<FluteSoundSequence.FluteSound> sounds = sequence.sounds();
        if (sounds.isEmpty()) return this;

        // total sequence length in ticks (sum of all waits)
        int totalWait = sounds.stream().mapToInt(FluteSoundSequence.FluteSound::waitTime).sum();
        if (totalWait <= 0) totalWait = 1;

        // scaling factor to match consume time
        double scale = (double) (consumeTime-2) / totalWait;

        // initialize target with first sound
        FluteSoundSequence.FluteSound current = getCurrentSound();
        nextTarget = (int) Math.floor(current.waitTime() * scale);

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick++;

                if (tick >= nextTarget) {
                    onPlay.accept(getCurrentSound());

                    if (!hasNext()) {
                        cancel();
                        return;
                    }
                    next(scale);
                }

                if (!isValid.get()) {
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
        return this;
    }


    /*protected final FluteSoundSequence sequence;
    protected final Consumer<FluteSoundSequence.FluteSound> onPlay;
    protected final Supplier<Boolean> isValid;

    public FlutePlayer(FluteSoundSequence sequence, Consumer<FluteSoundSequence.FluteSound> onPlay, Supplier<Boolean> isValid) {
        this.sequence = sequence;
        this.onPlay = onPlay;
        this.isValid = isValid;
    }

    protected BukkitRunnable runnable;

    public FluteSoundSequence.FluteSound getCurrentSound(){
        return sequence.sounds().get(index);
    }

    public boolean hasNext(){
        return index + 1 < sequence.sounds().size();
    }

    public FluteSoundSequence.FluteSound next(){
        index++;
        return getCurrentSound();
    }

    public void stop(){
        try{
            runnable.cancel();
        } catch (NullPointerException | IllegalStateException e) {
        }
    }

    protected int tick = 0;
    protected int index = 0;
    public FlutePlayer play(int consumeTime) {
        List<FluteSoundSequence.FluteSound> sounds = sequence.sounds();
        if (sounds.isEmpty()) return this;

        // total sequence length in ticks (sum of all waits)
        int totalWait = sounds.stream().mapToInt(FluteSoundSequence.FluteSound::waitTime).sum();
        if (totalWait <= 0) totalWait = 1;

        // scaling factor to match consume time
        double scale = (double) consumeTime / totalWait;

        runnable = new BukkitRunnable() {
            @Override
            public void run() {
                tick++;
                FluteSoundSequence.FluteSound sound = getCurrentSound();
                int adjustedWait = (int) Math.round(sound.waitTime() * scale);

                if(tick == adjustedWait || adjustedWait == 0){
                    onPlay.accept(sound);

                    if(!hasNext()){
                        cancel();
                        return;
                    }
                    next();
                }
                if(!isValid.get()){
                    cancel();
                }
            }
        };
        runnable.runTaskTimer(Crux.getMainPlugin(), 0L, 1L);
        return this;
    }*/
}
