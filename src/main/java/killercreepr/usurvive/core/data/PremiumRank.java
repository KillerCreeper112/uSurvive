package killercreepr.usurvive.core.data;

import java.util.List;

public class PremiumRank {
    protected final String permission;
    protected final double cost;
    protected final List<String> reached_commands;

    public PremiumRank(String permission, double cost, List<String> reachedCommands) {
        this.permission = permission;
        this.cost = cost;
        this.reached_commands = reachedCommands;
    }

    public String getPermission() {
        return permission;
    }

    public double getCost() {
        return cost;
    }

    public List<String> getReachedCommands() {
        return reached_commands;
    }
}
