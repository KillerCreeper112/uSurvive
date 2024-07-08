package killercreepr.usurvive.block;

import killercreepr.crux.Crux;
import killercreepr.cruxblocks.block.group.CruxBlockGroup;
import killercreepr.cruxblocks.block.group.CruxDirectionalBlockGroup;
import killercreepr.cruxblocks.block.group.GenericDirectionalBlockGroup;
import killercreepr.cruxblocks.block.texture.NoteTextureData;
import killercreepr.cruxblocks.registeries.CruxBlocksRegistries;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

public class USurviveBlocks {
    public static void register(){}
    public static final CruxDirectionalBlockGroup CHARRED_LOG = CruxBlocksRegistries.BLOCKS.registerGroup(new GenericDirectionalBlockGroup(
        Crux.key("charred_log"), true,
        new CharredLogBlock(Crux.key("charred_log_x"), new NoteTextureData(new Note(0), Instrument.BANJO, false),BlockFace.EAST),
        new CharredLogBlock(Crux.key("charred_log_y"), new NoteTextureData(new Note(1), Instrument.BANJO, false),BlockFace.UP),
        new CharredLogBlock(Crux.key("charred_log_z"), new NoteTextureData(new Note(2), Instrument.BANJO, false),BlockFace.NORTH)
    ) {
        @Override
        public float getHardness() {
            return 1f;
        }

        @Override
        public @NotNull SoundGroup getSoundGroup() {
            return new SoundGroup() {
                @Override
                public float getVolume() {
                    return 1f;
                }

                @Override
                public float getPitch() {
                    return 1f;
                }

                @Override
                public @NotNull Sound getBreakSound() {
                    return Sound.BLOCK_WOOD_BREAK;
                }

                @Override
                public @NotNull Sound getStepSound() {
                    return null;
                }

                @Override
                public @NotNull Sound getPlaceSound() {
                    return Sound.BLOCK_WOOD_PLACE;
                }

                @Override
                public @NotNull Sound getHitSound() {
                    return null;
                }

                @Override
                public @NotNull Sound getFallSound() {
                    return null;
                }
            };
        }
    });
}
