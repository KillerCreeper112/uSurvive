package killercreepr.usurvive.core.potion;

import killercreepr.crux.api.communication.CreateTitle;
import killercreepr.cruxpotions.api.potion.CruxPotion;
import killercreepr.cruxpotions.core.potions.SimpleActivePotion;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.NotNull;

public class TitleDisplayPotion extends SimpleActivePotion {
    protected final String title;
    protected final String subTitle;
    public TitleDisplayPotion(@NotNull CruxPotion potion, @NotNull Entity entity, int duration, int amplifier, String title, String subTitle) {
        super(potion, entity, duration, amplifier);
        this.title = title;
        this.subTitle = subTitle;
    }

    @Override
    protected void onStop() {
        super.onStop();
        CreateTitle.title("", null).use(entity);
    }

    @Override
    protected void onUpdate(int oldDuration, int oldAmplifier) {
        super.onUpdate(oldDuration, oldAmplifier);
        showTitle();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showTitle();
    }

    public void showTitle(){
        if(duration < 10) return;
        int fadeIn = 5;
        int stay = duration-10;
        int fadeOut = 5;

        CreateTitle.title(title, subTitle, fadeIn, stay, fadeOut).use(entity);
    }
}
