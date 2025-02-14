package com.firemerald.custombgm.config;

import com.firemerald.custombgm.api.CustomBGMAPI;
import com.firemerald.fecore.util.HorizontalAlignment;
import com.firemerald.fecore.util.VerticalAlignment;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.EnumValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

@EventBusSubscriber(modid = CustomBGMAPI.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientConfig {
	private static final BooleanValue MENU_BUTTONS_ENABLED;
	private static final EnumValue<HorizontalAlignment> MENU_BUTTONS_HORIZONTAL;
	private static final IntValue MENU_BUTTONS_HORIZONTAL_OFFSET;
	private static final EnumValue<VerticalAlignment> MENU_BUTTONS_VERTICAL;
	private static final IntValue MENU_BUTTONS_VERTICAL_OFFSET;
	private static final BooleanValue LOG_MUSIC;
	public static final ModConfigSpec SPEC;

	static {
		ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Client settings").push("client");
        MENU_BUTTONS_ENABLED = builder
        		.comment("Whether to add track control buttons to the main menu.")
        		.translation("custombgm.config.menu_buttons_enabled")
        		.define("menu_buttons_enabled", true);
        MENU_BUTTONS_HORIZONTAL = builder
        		.comment("Horizontal alignment of the track control buttons on the main menu.")
        		.translation("custombgm.config.menu_buttons_horizontal")
        		.defineEnum("menu_buttons_horizontal", HorizontalAlignment.LEFT);
        MENU_BUTTONS_HORIZONTAL_OFFSET = builder
        		.comment("Offset from the left, center, or right of the menu screen to place the track control buttons.")
        		.translation("custombgm.config.menu_buttons_horizontal_offset")
        		.defineInRange("menu_buttons_horizontal_offset", 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
        MENU_BUTTONS_VERTICAL = builder
        		.comment("Vertical alignment of the track control buttons on the main menu.")
        		.translation("custombgm.config.menu_buttons_vertical")
        		.defineEnum("menu_buttons_vertical", VerticalAlignment.TOP);
        MENU_BUTTONS_VERTICAL_OFFSET = builder
        		.comment("Offset from the top, middle, or bottom of the menu screen to place the track control buttons.")
        		.translation("custombgm.config.menu_buttons_vertical_offset")
        		.defineInRange("menu_buttons_vertical_offset", 5, Integer.MIN_VALUE, Integer.MAX_VALUE);
        LOG_MUSIC = builder
        		.comment("Output to the game log whenever the playing music track changes.")
        		.translation("custombgm.config.log_music")
        		.define("log_music", false);
        SPEC = builder.build();
	}

	public static boolean menuButtonsEnabled = true;
	public static HorizontalAlignment menuButtonsHorizontal = HorizontalAlignment.LEFT;
	public static int menuButtonsHorizontalOffset = 5;
	public static VerticalAlignment menuButtonsVertical = VerticalAlignment.TOP;
	public static int menuButtonsVerticalOffset = 5;
	public static boolean logMusic = false;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
    	if (event.getConfig().getSpec() == SPEC && SPEC.isLoaded()) loadConfig();
    }

    public static void loadConfig() {
    	menuButtonsEnabled = MENU_BUTTONS_ENABLED.getAsBoolean();
    	menuButtonsHorizontal = MENU_BUTTONS_HORIZONTAL.get();
    	menuButtonsHorizontalOffset = MENU_BUTTONS_HORIZONTAL_OFFSET.getAsInt();
    	menuButtonsVertical = MENU_BUTTONS_VERTICAL.get();
    	menuButtonsVerticalOffset = MENU_BUTTONS_VERTICAL_OFFSET.getAsInt();
    	logMusic = LOG_MUSIC.getAsBoolean();
    }
}
