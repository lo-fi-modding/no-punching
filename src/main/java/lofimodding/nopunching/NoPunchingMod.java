package lofimodding.nopunching;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(NoPunchingMod.MOD_ID)
public class NoPunchingMod {
  public static final String MOD_ID = "no-punching";
  public static final Logger LOGGER = LogManager.getLogger();

  public NoPunchingMod() {
    Config.registerConfig();
    MinecraftForge.EVENT_BUS.addListener(this::disablePunching);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigReload);
  }

  private void onConfigReload(final ModConfig.Reloading event) {
    LOGGER.info("Reloading config {}", event.getConfig().getFileName());
  }

  private void disablePunching(final PlayerEvent.BreakSpeed event) {
    if(!Config.ENABLED.get()) {
      return;
    }

    final BlockState state = event.getState();
    final PlayerEntity player = event.getPlayer();

    if(state.getHarvestTool() == null || state.getBlockHardness(player.getEntityWorld(), event.getPos()) <= Config.MAX_HARDNESS.get()) {
      return;
    }

    final ItemStack held = player.getHeldItemMainhand();

    if(!held.isEmpty()) {
      if(held.canHarvestBlock(state)) {
        return;
      }

      for(final ToolType toolType : held.getToolTypes()) {
        if(state.isToolEffective(toolType)) {
          return;
        }
      }
    }

    if(Config.PREVENTION_MODE.get() == Config.PreventionMode.SLOW) {
      event.setNewSpeed((float)(event.getOriginalSpeed() * Config.SPEED_MULTIPLIER.get()));
    } else {
      event.setCanceled(true);
    }
  }
}
