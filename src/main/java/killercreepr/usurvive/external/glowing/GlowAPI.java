package killercreepr.usurvive.external.glowing;

import fr.skytasul.glowingentities.GlowingEntities;

public class GlowAPI {
    public static GlowingEntities glowingEntities;

    public static GlowingEntities glowingEntities() {
        return glowingEntities;
    }

    public static void glowingEntities(GlowingEntities glowingEntities) {
        GlowAPI.glowingEntities = glowingEntities;
    }
}
