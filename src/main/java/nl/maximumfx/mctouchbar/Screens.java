package nl.maximumfx.mctouchbar;

import net.minecraft.client.MinecraftClient;

import java.util.Arrays;

public enum Screens {
	TITLE_SCREEN("Title Screen"),
	SELECT_WORLD("Select World"),
	CREATE_NEW_WORLD("Create New World"),
	PLAY_MULTIPLAYER("Play Multiplayer"),
	EDIT_SERVER_INFO("Edit Server Info"),
	DIRECT_CONNECT("Direct Connect"),
	OPTIONS("Options"),
	GAME_MENU("Game Menu"),
	INGAME("");

	private String name;
	Screens(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	public static Screens getActive() {
		if (MinecraftClient.getInstance().currentScreen != null) {
			String title = MinecraftClient.getInstance().currentScreen.getTitle().asString();
			if (title == null || title.equalsIgnoreCase("")) return INGAME;
			else return Arrays.stream(Screens.values()).filter(o -> title.equalsIgnoreCase(o.name)).findFirst().orElse(null);
		}
		return INGAME;
	}
}
