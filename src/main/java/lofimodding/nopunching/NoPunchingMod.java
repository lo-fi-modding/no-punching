package lofimodding.nopunching;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeConfigSpec;
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

  private final ForgeConfigSpec SERVER_CONFIG;

  public final ForgeConfigSpec.BooleanValue ENABLED;
  public final ForgeConfigSpec.DoubleValue MAX_HARDNESS;
  public final ForgeConfigSpec.EnumValue<PreventionMode> PREVENTION_MODE;
  public final ForgeConfigSpec.DoubleValue SPEED_MULTIPLIER;

  public NoPunchingMod() {
    final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    builder.comment("General configuration").push("general");

    this.ENABLED = builder
      .comment("Enables or disables the entire mod")
      .define("enabled", true);

    this.MAX_HARDNESS = builder
      .comment("The maximum hardness a player can break without the correct tool")
      .defineInRange("max_hardness", 1.0d, 0.0d, 1000000.0d);

    this.PREVENTION_MODE = builder
      .comment("Choose whether to slow or outright prevent block-breaking")
      .defineEnum("mode", PreventionMode.PREVENT);

    this.SPEED_MULTIPLIER = builder
      .comment("If set to \"slow\" mode, how slow should it be?")
      .defineInRange("speed_multiplier", 0.1d, 0.0d, 1.0d);

    builder.pop();

    this.SERVER_CONFIG = builder.build();

    MinecraftForge.EVENT_BUS.addListener(this::disablePunching);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onConfigReload);
  }

  private void onConfigReload(final ModConfig.Reloading event) {
    LOGGER.info("Reloading config {}", event.getConfig().getFileName());
  }

  private void disablePunching(final PlayerEvent.BreakSpeed event) {
    if(!this.ENABLED.get()) {
      return;
    }

    final BlockState state = event.getState();
    final PlayerEntity player = event.getPlayer();

    if(state.getHarvestTool() == null || state.getBlockHardness(player.getEntityWorld(), event.getPos()) <= this.MAX_HARDNESS.get()) {
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

    if(this.PREVENTION_MODE.get() == PreventionMode.SLOW) {
      event.setNewSpeed((float)(event.getOriginalSpeed() * this.SPEED_MULTIPLIER.get()));
    } else {
      event.setCanceled(true);
    }
  }

  public enum PreventionMode {
    SLOW, PREVENT
  }
}
