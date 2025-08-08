package killercreepr.usurvive.core.enchant;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import killercreepr.crux.core.Crux;
import org.bukkit.enchantments.Enchantment;

public class USurviveEnchant {
    public static final Enchantment TRAMPLE = get("trample");
    public static final Enchantment JOY_WHIRL = get("joy_whirl");
    public static final Enchantment BURNING_THORNS = get("burning_thorns");
    public static final Enchantment BACKSTAB = get("backstab");
    public static final Enchantment WITHSTAND = get("withstand");
    public static final Enchantment SECOND_WIND = get("second_wind");
    public static final Enchantment BOOMSHOT = get("boomshot");
    public static final Enchantment VELOCITY = get("velocity");
    public static final Enchantment FROSTBITE = get("frostbite");
    public static final Enchantment INSIGHT = get("insight");
    public static final Enchantment PENETRATE = get("penetrate");
    public static final Enchantment BRACED = get("braced");
    public static final Enchantment TRUESHOT = get("trueshot");
    public static final Enchantment SPOREBURST = get("sporeburst");
    public static final Enchantment MIRRORSTRIKE = get("mirrorstrike");
    public static final Enchantment SNARE = get("snare");
    public static final Enchantment BLOOMING_CURSE = get("blooming_curse");
    public static final Enchantment STAGGER = get("stagger");
    public static final Enchantment HOLLOWING_CURSE = get("hollowing_curse");
    public static final Enchantment TETHER = get("tether");
    public static final Enchantment FRENZY = get("frenzy");
    public static final Enchantment SHATTER = get("shatter");
    public static final Enchantment REAP = get("reap");
    public static final Enchantment RICOCHET = get("ricochet");
    public static final Enchantment CLING = get("cling");
    public static final Enchantment FOCUS = get("focus");
    public static final Enchantment CLEANSING = get("cleansing");
    //todo public static final Enchantment RESENTMENT_CURSE = get("resentment_curse");
    public static final Enchantment SPOILAGE_CURSE = get("spoilage_curse");
    public static final Enchantment BREAKING_CURSE = get("breaking_curse");
    public static final Enchantment CHAINSHOCK = get("chainshock");
    public static final Enchantment SOULBOUND = get("soulbound");
    public static final Enchantment EMOTIVE = get("emotive");
    public static final Enchantment YOINK = get("yoink");

    //may become a charm or something else
    public static final Enchantment REMNANT = get("remnant");

    private static Enchantment get(String id){
        return RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).get(Crux.key(id));
    }
}
