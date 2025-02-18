package killercreepr.usurvive.core.entity.sleeper.effects;

import killercreepr.crux.api.valueproviders.number.NumberProvider;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class SleepWeaknessCalculator {
    private static class SleepWeaknessEntry {
        final int level;
        final int minTicks;
        final int maxTicks;

        SleepWeaknessEntry(int level, int minTicks, int maxTicks) {
            this.level = level;
            this.minTicks = minTicks;
            this.maxTicks = maxTicks;
        }
    }

    public SleepWeaknessCalculator(Map<Integer, NumberProvider> map) {
        sortedSleepWeaknessLevels = new ArrayList<>(map.size());
        for (Map.Entry<Integer, NumberProvider> entry : map.entrySet()) {
            int min = entry.getValue().getMinValue().intValue();
            int max = entry.getValue().getMaxValue().intValue();
            sortedSleepWeaknessLevels.add(new SleepWeaknessEntry(entry.getKey(), min, max));
        }
        // Sort by minTicks for binary search efficiency
        sortedSleepWeaknessLevels.sort(Comparator.comparingInt(e -> e.minTicks));
    }

    private final List<SleepWeaknessEntry> sortedSleepWeaknessLevels;

    public int calculateSleepWeaknessLevel(int slumberTicks) {
        if (sortedSleepWeaknessLevels.isEmpty()) return 0;

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
                return entry.level; // Found the correct range
            }
        }
        return 0;
    }
}
