package killercreepr.usurvive.data;

import net.kyori.adventure.resource.ResourcePackInfo;
import net.kyori.adventure.resource.ResourcePackRequest;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.Collection;

public class ResourcePackRequestData {
    protected final Component prompt;
    protected final boolean force;
    protected final Collection<ResourcePackData> resourcePacks;

    protected ResourcePackRequest cache;

    public ResourcePackRequestData(Component prompt, boolean force, Collection<ResourcePackData> resourcePacks) {
        this.prompt = prompt;
        this.force = force;
        this.resourcePacks = resourcePacks;
    }

    public ResourcePackRequest buildRequest(){
        if(cache != null) return cache;
        Collection<ResourcePackInfo> infos = new ArrayList<>();
        resourcePacks.forEach(data -> infos.add(data.buildInfo()));
        return cache = ResourcePackRequest.resourcePackRequest()
            .required(force)
            .prompt(prompt)
            .packs(infos)
            .build()
            ;
    }
}
