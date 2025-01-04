package killercreepr.usurvive.api.tpa;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.UUID;

public interface TPAHolder {
    void addRequest(@NotNull TPARequest request);
    @Nullable TPARequest getRequest(@NotNull UUID from);
    @Nullable TPARequest getRequest(@NotNull String from);
    @Nullable TPARequest removeRequest(@NotNull UUID from);
    @Nullable TPARequest removeRequest(@NotNull String from);
    @NotNull Collection<TPARequest> getRequests();
}
