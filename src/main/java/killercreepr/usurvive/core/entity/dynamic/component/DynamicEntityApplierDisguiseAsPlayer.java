package killercreepr.usurvive.core.entity.dynamic.component;

import killercreepr.crux.api.entity.CruxEntity;
import killercreepr.crux.api.entity.dynamic.DynamicEntityApplierComponent;
import killercreepr.crux.api.entity.dynamic.DynamicEntityApplierSingleComponent;
import killercreepr.crux.api.text.context.TextParserContext;
import me.libraryaddict.disguise.DisguiseAPI;
import me.libraryaddict.disguise.disguisetypes.PlayerDisguise;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class DynamicEntityApplierDisguiseAsPlayer extends DynamicEntityApplierSingleComponent {
  public DynamicEntityApplierDisguiseAsPlayer(Object name) {
    super(name);
  }

  @NotNull
  @Override
  public String name() {
    return "libs_disguises/disguise_as_player";
  }

  @Override
  public void apply(@NotNull CruxEntity entity, @NotNull TextParserContext ctx) {
    var disguise = new PlayerDisguise(ctx.input(value.toString()));
    disguise.setNameVisible(false);
    disguise.setName("");
    //DisguiseAPI.disguiseNextEntity(disguise);
    DisguiseAPI.disguiseEntity(entity.entity(), disguise);
  }
}
