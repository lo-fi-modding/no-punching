package lofimodding.nopunching;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public final class Config {
  private Config() { }

  private static final ForgeConfigSpec SERVER_CONFIG;

  private static final String GENERAL_CATEGORY = "general";
  public static final ForgeConfigSpec.BooleanValue ENABLED;
  public static final ForgeConfigSpec.DoubleValue MAX_HARDNESS;
  public static final ForgeConfigSpec.EnumValue<PreventionMode> PREVENTION_MODE;
  public static final ForgeConfigSpec.DoubleValue SPEED_MULTIPLIER;

  static {
    final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

    builder.comment("General configuration").push(GENERAL_CATEGORY);

    ENABLED = builder
      .comment("Enables or disables the entire mod")
      .define("enabled", true);

    MAX_HARDNESS = builder
      .comment("The maximum hardness a player can break without the correct tool")
      .defineInRange("max_hardness", 1.0d, 0.0d, 1000000.0d);

    PREVENTION_MODE = builder
      .comment("Choose whether to slow or outright prevent block-breaking")
      .defineEnum("mode", PreventionMode.PREVENT);

    SPEED_MULTIPLIER = builder
      .comment("If set to \"slow\" mode, how slow should it be?")
      .defineInRange("speed_multiplier", 0.1d, 0.0d, 1.0d);

    builder.pop();

    SERVER_CONFIG = builder.build();
  }

  static void registerConfig() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SERVER_CONFIG);
  }

  public enum PreventionMode {
    SLOW, PREVENT
  }
}
