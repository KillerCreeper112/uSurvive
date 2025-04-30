package killercreepr.usurvive.core.charm;

import killercreepr.crux.api.component.parser.hybrid.PersistTextParser;
import killercreepr.crux.api.component.parser.hybrid.TextInputField;
import killercreepr.crux.api.valueproviders.number.NumberProvider;
import killercreepr.crux.core.component.parser.type.ComponentInputParsers;
import killercreepr.crux.paper.ItemHolder;
import killercreepr.cruxcharms.api.charm.Charm;
import killercreepr.cruxcharms.core.component.charm.SimpleCharm;
import net.kyori.adventure.key.Key;

public class CharmCompParsers {
    public static final PersistTextParser<MineBonusCharm> MINE_BONUS = PersistTextParser.mapBuilder(MineBonusCharm.class)
        .field("type", TextInputField.field(PersistTextParser.KEY, Charm::key))
        .field("name", TextInputField.field(PersistTextParser.STRING, SimpleCharm::getName))
        .field("item", TextInputField.field(ComponentInputParsers.ITEM_HOLDER, Charm::getItem))
        .field("use_slot", TextInputField.field(PersistTextParser.BOOLEAN, Charm::usesCharmSlot))
        .field("chance", TextInputField.field(PersistTextParser.FLOAT, MineBonusCharm::getChance))
        .field("drop_amount", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, MineBonusCharm::getDropAmount))
        .field("effect_item_amount", TextInputField.field(ComponentInputParsers.NUMBER_PROVIDER, MineBonusCharm::getItemEffectAmount))
        .field("block", TextInputField.field(ComponentInputParsers.BLOCK_PREDICATE, MineBonusCharm::getPredicate))
        .field("include_player_placed", TextInputField.field(PersistTextParser.BOOLEAN, MineBonusCharm::isIncludePlayerPlaced))
        .apply(ctx ->{
            Key type = ctx.get("type");
            ItemHolder item = ctx.getOptional("item");
            String name = ctx.getOptional("name");
            boolean useSlot = ctx.getOptional("use_slot", true);
            return new MineBonusCharm(type, name, item, useSlot,
                ctx.getOptional("chance", 100f),
                ctx.getOptional("effect_item_amount", NumberProvider.constant(1)),
                ctx.get("drop_amount"),
                ctx.getOptional("block"),
                ctx.getOptional("include_player_placed", false)
            );
        });
}
