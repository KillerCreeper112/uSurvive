package killercreepr.usurvive.core.mechanics.bone_flute;

import killercreepr.crux.api.communication.CreateSound;

import java.util.List;

public interface FluteSoundSequence {
    static FluteSoundSequence fluteSoundSequence(List<FluteSound> sounds){
        return new Simple(sounds);
    }
    static FluteSound fluteSound(CreateSound sound, int waitTime){
        return new FluteSound.Simple(sound, waitTime);
    }

    List<FluteSound> sounds();

    class Simple implements FluteSoundSequence{
        protected final List<FluteSound> sounds;

        public Simple(List<FluteSound> sounds) {
            this.sounds = sounds;
        }

        @Override
        public List<FluteSound> sounds() {
            return sounds;
        }
    }

    interface FluteSound{
        CreateSound sound();
        int waitTime();

        class Simple implements FluteSound{
            protected final CreateSound sound;
            protected final int waitTime;

            public Simple(CreateSound sound, int waitTime) {
                this.sound = sound;
                this.waitTime = waitTime;
            }

            @Override
            public CreateSound sound() {
                return sound;
            }

            @Override
            public int waitTime() {
                return waitTime;
            }
        }
    }
}
