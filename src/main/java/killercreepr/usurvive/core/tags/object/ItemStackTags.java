package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.item.CruxItem;
import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringListResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.usurvive.api.component.InfusedPotions;
import killercreepr.usurvive.core.USurvivePlugin;
import killercreepr.usurvive.core.component.USurviveComponents;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemStackTags implements ObjectTag<ItemStack> {
    @NotNull
    @Override
    public Class<ItemStack> getObjectType() {
        return ItemStack.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("item_");
    }

    @Nullable
    @Override
    public TagContainer<StringListResolver> requestStringLists(@NotNull ItemStack object, @NotNull TagParser tags) {
        return TagContainer.stringList(tags)
            .add(Tag.stringList("infused_potions", (args, ctx) ->{
                CruxItem crux = CruxItem.wrap(object);
                InfusedPotions infused = crux.getOrDefaultData(USurviveComponents.INFUSED_POTIONS);
                if(infused==null) return List.of();

                List<String> format = USurvivePlugin.inst().values().INFUSED_POTIONS_TAG_FORMAT().value();
                if(format==null) return List.of();
                List<String> potionsFormat = USurvivePlugin.inst().values().INFUSED_POTIONS_EFFECTS_LIST_TAG_FORMAT().value();
                if(potionsFormat==null) return List.of();

                List<String> list = new ArrayList<>();
                infused.getInfusedPotions().forEach(group ->{
                    format.forEach(s ->{
                        list.add(ctx.deserializeString(s, TagContainer.string(ctx.getFormat().tags()).add(
                            Tag.parsed("crux_slot_group_when_in_slot", group.getSlot().getWhenInSlot())
                        )));
                    });
                    group.getPotionEffects().forEach(pot ->{
                        potionsFormat.forEach(s ->{
                            list.add(ctx.deserializeString(s, TagContainer.string(ctx.getFormat().tags()).hook(pot)));
                        });
                    });
                });
                return list;
            }))
            ;
    }
}
