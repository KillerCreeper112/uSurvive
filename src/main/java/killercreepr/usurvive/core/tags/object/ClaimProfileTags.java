package killercreepr.usurvive.core.tags.object;

import killercreepr.crux.api.text.format.FormatPrefix;
import killercreepr.crux.api.text.hook.ObjectTag;
import killercreepr.crux.api.text.resolver.StringResolver;
import killercreepr.crux.api.text.tags.TagParser;
import killercreepr.crux.api.text.tags.container.TagContainer;
import killercreepr.crux.core.text.resolver.Tag;
import killercreepr.cruxclaims.api.claiming.Claim;
import killercreepr.cruxclaims.api.claiming.ClaimProfile;
import killercreepr.usurvive.core.listener.ClaimingListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ClaimProfileTags implements ObjectTag<ClaimProfile> {
    @NotNull
    @Override
    public Class<ClaimProfile> getObjectType() {
        return ClaimProfile.class;
    }

    @Override
    public @NotNull FormatPrefix defaultPrefix() {
        return FormatPrefix.simple("claim_profile_usurvive_");
    }

    @Override
    public @Nullable TagContainer<StringResolver> requestStrings(@NotNull ClaimProfile object, @NotNull TagParser tags) {
        return TagContainer.string(tags)
            .add(Tag.string("has_set_world_claimed", (args, ctx) ->{
                for(Claim c : object.claims()){
                    if(ClaimingListener.isSetSettingWorld(c.chunk().worldKey())) return "1";
                }
                return "0";
            }))
            ;
    }
}
