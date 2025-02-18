package killercreepr.usurvive.core.entity.sleeper.effects;

import killercreepr.crux.core.data.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SleepWeaknessWarning {
    private static class SleepWeaknessEntry {
        final SleepWeaknessWarningData level;
        final int minTicks;
        final int maxTicks;

        SleepWeaknessEntry(SleepWeaknessWarningData level, int minTicks, int maxTicks) {
            this.level = level;
            this.minTicks = minTicks;
            this.maxTicks = maxTicks;
        }
    }

    public SleepWeaknessWarning(Collection<SleepWeaknessWarningData> map) {
        sortedSleepWeaknessLevels = new ArrayList<>(map.size());
        for (SleepWeaknessWarningData entry : map) {
            int min = entry.minSlumberTicks;
            int max = entry.maxSlumberTicks;
            sortedSleepWeaknessLevels.add(new SleepWeaknessEntry(entry, min, max));
        }
        // Sort by minTicks for binary search efficiency
        sortedSleepWeaknessLevels.sort(Comparator.comparingInt(e -> e.minTicks));
    }

    private final List<SleepWeaknessEntry> sortedSleepWeaknessLevels;
    public Pair<SleepWeaknessWarningData, Integer> calculateWarningData(int slumberTicks) {
        if (sortedSleepWeaknessLevels.isEmpty()) return null;

        // Binary search to find the closest valid range
        int left = 0, right = sortedSleepWeaknessLevels.size() - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            SleepWeaknessEntry entry = sortedSleepWeaknessLevels.get(mid);

            if (slumberTicks < entry.minTicks) {
                right = mid - 1;
            } else if (slumberTicks > entry.maxTicks) {
                left = mid + 1;
            } else {
                return new Pair<>(entry.level, mid); // Found the correct range
            }
        }
        return null;
    }
}
