package killercreepr.usurvive.data;

import net.kyori.adventure.resource.ResourcePackInfo;

import java.net.URI;
import java.util.UUID;

public class ResourcePackData {
    protected final UUID uuid;
    protected final String url;
    protected final String hash;

    public ResourcePackData(UUID uuid, String url, String hash) {
        this.uuid = uuid;
        this.url = url;
        this.hash = hash;
    }

    protected ResourcePackInfo cache;
    public ResourcePackInfo buildInfo(){
        if(cache != null) return cache;
        return cache = ResourcePackInfo.resourcePackInfo()
            .id(uuid)
            .uri(URI.create(url))
            .hash(hash)
            .build();
    }
}
