package nl.maximumfx.mctouchbar;

import com.thizzer.jtouchbar.JTouchBar;
import com.thizzer.jtouchbar.common.Color;
import com.thizzer.jtouchbar.common.ImagePosition;
import com.thizzer.jtouchbar.item.PopoverTouchBarItem;
import com.thizzer.jtouchbar.item.TouchBarItem;
import com.thizzer.jtouchbar.item.view.TouchBarButton;
import com.thizzer.jtouchbar.item.view.TouchBarButton.ButtonType;
import com.thizzer.jtouchbar.item.view.TouchBarTextField;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import net.minecraft.client.util.ScreenshotUtils;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class Bars {

	private MainClass mc;
	private MinecraftClient mcc;
	private JTouchBar infoBar;
	private JTouchBar inGameBar;
	private JTouchBar debugBar;
	private Map<String, TouchBarButton> buttons = new HashMap<>();

	private static ButtonType press = ButtonType.MOMENTARY_PUSH_IN;
	private static ButtonType toggle = ButtonType.ON_OFF;
	private static ButtonType cycle = ButtonType.SWITCH;

	public void init(MainClass mc) {
		this.mc = mc;
		this.mcc = MinecraftClient.getInstance();
		this.infoBar = createInfoTouchBar();
		this.debugBar = createDebugBar();
		this.inGameBar = createInGameBar();
	}

	public void reload() {
		this.infoBar = createInfoTouchBar();
		this.debugBar = createDebugBar();
		this.inGameBar = createInGameBar();
	}

	public JTouchBar getInfoBar() {
		return infoBar;
	}
	public JTouchBar getDebugBar() {
		return debugBar;
	}
	public JTouchBar getInGameBar() {
		return inGameBar;
	}

	public Map<String, TouchBarButton> getButtons() {
		return buttons;
	}

	private JTouchBar createInfoTouchBar() {
		JTouchBar jTouchBar = new JTouchBar();
		jTouchBar.setCustomizationIdentifier("main");
		TouchBarTextField tbtf = new TouchBarTextField();
		tbtf.setStringValue("MCTouchBar v0.1.0-pre.2 (by MaximumFX)");
		jTouchBar.addItem(new TouchBarItem("mctb", tbtf));
		return jTouchBar;
	}

	private JTouchBar createDebugBar() {
		JTouchBar jTouchBar = new JTouchBar();
		String inGame = "debug";
		jTouchBar.setCustomizationIdentifier(inGame);
		//<editor-fold desc="Reload chunks">
		Logger.log(Level.INFO, "Creating reload chunks button...");
		TouchBarButton reloadChunks = new TBButton(press).setTitle("Reload chunks").setIcon(Icons.F3_RELOAD_CHUNKS).setImagePosition(ImagePosition.ONLY).build();
		reloadChunks.setAction(view -> {
			mcc.worldRenderer.reload();
			mc.debugWarn("debug.reload_chunks.message");
		});
		buttons.put((inGame + "/f3_reload_chunks"), reloadChunks);
		//</editor-fold>
		//<editor-fold desc="Show hitboxes">
		Logger.log(Level.INFO, "Creating show hit boxes button...");
		TouchBarButton showHitboxes = new TBButton(toggle).setEnabled(mcc.getEntityRenderManager() != null && mcc.getEntityRenderManager().shouldRenderHitboxes()).setIcon(Icons.F3_SHOW_HITBOXES).setImagePosition(ImagePosition.ONLY).build();
		showHitboxes.setAction(view -> {
			if (showHitboxes.getTitle().equalsIgnoreCase("enabled")) {
				showHitboxes.setTitle("disabled");
				showHitboxes.setImage(Icons.F3_SHOW_HITBOXES.getDefaultIcon(false));
				mcc.getEntityRenderManager().setRenderHitboxes(false);
				mc.debugWarn("debug.show_hitboxes.off");
			}
			else {
				showHitboxes.setTitle("enabled");
				showHitboxes.setImage(Icons.F3_SHOW_HITBOXES.getDefaultIcon(false));
				mcc.getEntityRenderManager().setRenderHitboxes(true);
				mc.debugWarn("debug.show_hitboxes.on");
			}
		});
		buttons.put((inGame + "/f3_show_hitboxes"), showHitboxes);
		//</editor-fold>
		//<editor-fold desc="Copy location for tp">
		Logger.log(Level.INFO, "Creating copy location button...");
		TouchBarButton copyLocation = new TBButton(press).setTitle("Copy location").setIcon(Icons.F3_COPY_LOCATION).setImagePosition(ImagePosition.ONLY).build();
		copyLocation.setAction(view -> {
			ClientPlayerEntity p = mcc.player;
			if (p != null) {
				if (!mcc.player.getReducedDebugInfo()) {
					mc.debugWarn("debug.copy_location.message");
					mcc.keyboard.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.getId(mcc.player.world.dimension.getType()), mcc.player.getX(), mcc.player.getY(), mcc.player.getZ(), mcc.player.yaw, mcc.player.pitch));
				}
			}
		});
		buttons.put((inGame + "/f3_copy_location"), copyLocation);
		//</editor-fold>
		//<editor-fold desc="Clear chat">
		Logger.log(Level.INFO, "Creating clear chat button...");
		TouchBarButton clearChat = new TBButton(press).setTitle("Clear chat").setIcon(Icons.F3_CLEAR_CHAT).setImagePosition(ImagePosition.ONLY).build();
		clearChat.setAction(view -> {
			if (mcc.inGameHud != null)
				mcc.inGameHud.getChatHud().clear(false);
		});
		buttons.put((inGame + "/f3_clear_chat"), clearChat);
		//</editor-fold>
		//<editor-fold desc="Cycle render distance (Shift to invert)">
		Logger.log(Level.INFO, "Creating cycle render distance button...");
		TouchBarButton cycleRenderDistance = new TBButton(cycle).setTitle("Cycle render distance").setIcon(Icons.F3_CYCLE_RENDER_DISTANCE).setImagePosition(ImagePosition.ONLY).build();
		cycleRenderDistance.setAction(view -> {
//			GameOption.RENDER_DISTANCE.set(mcc.options, MathHelper.clamp(mcc.options.viewDistance + (Screen.hasShiftDown() ? -1 : 1), GameOption.RENDER_DISTANCE.getMin(), GameOption.RENDER_DISTANCE.getMax()));
			mcc.options.viewDistance = (int) MathHelper.clamp(mcc.options.viewDistance + (Screen.hasShiftDown() ? -1 : 1), 2.0f, 16.0f);
			mcc.options.write();
			mcc.worldRenderer.scheduleTerrainUpdate();
			mc.debugWarn("debug.cycle_renderdistance.message", mcc.options.viewDistance);

		});
		buttons.put((inGame + "/f3_cycle_render_distance"), cycleRenderDistance);
		//</editor-fold>
		//<editor-fold desc="Show chunk boundaries">
		Logger.log(Level.INFO, "Creating show chunk boundaries button...");
		mcc.debugRenderer.toggleShowChunkBorder();
		TouchBarButton showChunkBoundaries = new TBButton(toggle).setEnabled(mcc.debugRenderer.toggleShowChunkBorder()).setIcon(Icons.F3_SHOW_CHUNK_BOUNDARIES).setImagePosition(ImagePosition.ONLY).build();
		showChunkBoundaries.setAction(view -> {
			boolean showChunkBorder = mcc.debugRenderer.toggleShowChunkBorder();
			showChunkBoundaries.setTitle(showChunkBorder ? "enabled" : "disabled");
			if (!showChunkBorder) {
				showChunkBoundaries.setImage(Icons.F3_SHOW_CHUNK_BOUNDARIES.getDefaultIcon(false));
				showChunkBoundaries.setTitle("disabled");
			}
			else {
				showChunkBoundaries.setImage(Icons.F3_SHOW_CHUNK_BOUNDARIES.getDefaultIcon(false));
				showChunkBoundaries.setTitle("enabled");
			}
			mc.debugWarn(showChunkBorder ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
		});
		buttons.put((inGame + "/f3_show_chunk_boundaries"), showChunkBoundaries);
		//</editor-fold>
		//<editor-fold desc="Advanced tooltips">
		Logger.log(Level.INFO, "Creating advanced tooltips button...");
		TouchBarButton advancedTooltips = new TBButton(toggle).setEnabled(mcc.options.advancedItemTooltips).setIcon(Icons.F3_ADVANCED_TOOLTIPS).setImagePosition(ImagePosition.ONLY).build();
		advancedTooltips.setAction(view -> {
			if (advancedTooltips.getTitle().equalsIgnoreCase("enabled")) {
				advancedTooltips.setTitle("disabled");
				advancedTooltips.setImage(Icons.F3_ADVANCED_TOOLTIPS.getDefaultIcon(false));
				mcc.options.advancedItemTooltips = false;
				mc.debugWarn("debug.advanced_tooltips.off");
			}
			else {
				advancedTooltips.setTitle("enabled");
				advancedTooltips.setImage(Icons.F3_ADVANCED_TOOLTIPS.getDefaultIcon(true));
				mcc.options.advancedItemTooltips = true;
				mc.debugWarn("debug.advanced_tooltips.on");
			}
			mcc.options.write();
		});
		buttons.put((inGame + "/f3_advanced_tooltips"), advancedTooltips);
		//</editor-fold>
		//<editor-fold desc="Copy data">
		Logger.log(Level.INFO, "Creating copy data button...");
		TouchBarButton copyData = new TBButton(press).setTitle("Copy data").setIcon(Icons.F3_COPY_DATA).setImagePosition(ImagePosition.ONLY).build();
		copyData.setAction(view -> {
			if (mcc.player != null && !mcc.player.getReducedDebugInfo()) {
				Helper.copyLookAt(mcc.player.allowsPermissionLevel(2), !Screen.hasShiftDown());
			}
		});
		buttons.put((inGame + "/f3_copy_data"), copyData);
		//</editor-fold>
		//<editor-fold desc="Cycle gamemode">
		Logger.log(Level.INFO, "Creating cycle gamemode button...");
		TouchBarButton cycleGameMode = new TBButton(cycle).setTitle("Cycle gamemode").setIcon(Icons.F3_CYCLE_GAMEMODE).setImagePosition(ImagePosition.ONLY).build();
		cycleGameMode.setAction(view -> {
			if (mcc.player != null) {
				if (!mcc.player.allowsPermissionLevel(2)) {
					mc.debugWarn("debug.creative_spectator.error");
				}
				else if (mcc.player.isCreative()) {
					mcc.player.sendChatMessage("/gamemode spectator");
				}
				else if (mcc.player.isSpectator() || !mcc.player.isCreative() && !mcc.player.isSpectator()) {
					mcc.player.sendChatMessage("/gamemode creative");
				}
			}
		});
		buttons.put((inGame + "/f3_cycle_gamemode"), cycleGameMode);
		//</editor-fold>
		//<editor-fold desc="Toggle auto pause">
		Logger.log(Level.INFO, "Creating toggle auto pause button...");
		TouchBarButton toggleAutoPause = new TBButton(toggle).setEnabled(mcc.options.pauseOnLostFocus).setIcon(Icons.F3_TOGGLE_AUTO_PAUSE).setImagePosition(ImagePosition.ONLY).build();
		toggleAutoPause.setAction(view -> {
			if (toggleAutoPause.getTitle().equalsIgnoreCase("enabled")) {
				toggleAutoPause.setTitle("disabled");
				toggleAutoPause.setImage(Icons.F3_TOGGLE_AUTO_PAUSE.getDefaultIcon(false));
				mcc.options.pauseOnLostFocus = false;
				mc.debugWarn("debug.pause_focus.off");
			}
			else {
				toggleAutoPause.setTitle("enabled");
				toggleAutoPause.setImage(Icons.F3_TOGGLE_AUTO_PAUSE.getDefaultIcon(false));
				mcc.options.pauseOnLostFocus = true;
				mc.debugWarn("debug.pause_focus.on");
			}
			mcc.options.write();
		});
		buttons.put((inGame + "/f3_toggle_auto_pause"), toggleAutoPause);
		//</editor-fold>
		//<editor-fold desc="Reload resource packs">
		Logger.log(Level.INFO, "Creating reload resource packs button...");
		TouchBarButton reloadResourcePacks = new TBButton(press).setTitle("Reload resource packs").setIcon(Icons.F3_RELOAD_RESOURCE_PACKS).setImagePosition(ImagePosition.ONLY).build();
		reloadResourcePacks.setAction(view -> {
			mc.debugWarn("debug.reload_resourcepacks.message");
			mcc.reloadResources();
			mc.reload();
		});
		buttons.put(inGame + "/f3_reload_resource_packs", reloadResourcePacks);
		//</editor-fold>
		//<editor-fold desc="Pause without pause menu">
		Logger.log(Level.INFO, "Creating pause without menu button...");
		TouchBarButton pauseWithoutPauseMenu = new TBButton(press).setTitle("Pause without pause menu").setIcon(Icons.F3_PAUSE_WITHOUT_PAUSE_MENU).setImagePosition(ImagePosition.ONLY).build();
		pauseWithoutPauseMenu.setAction(view -> mcc.openPauseMenu(false));
		buttons.put((inGame + "/f3_pause_without_pause_menu"), pauseWithoutPauseMenu);
		//</editor-fold>

		Logger.log(Level.INFO, "Creating debug popover TouchBar...");
		jTouchBar.addItem(new TouchBarItem("f3_reload_chunks", reloadChunks));
		jTouchBar.addItem(new TouchBarItem("f3_show_hitboxes", showHitboxes));
		jTouchBar.addItem(new TouchBarItem("f3_copy_location", copyLocation));
		jTouchBar.addItem(new TouchBarItem("f3_clear_chat", clearChat));
		jTouchBar.addItem(new TouchBarItem("f3_cycle_render_distance", cycleRenderDistance));
		jTouchBar.addItem(new TouchBarItem("f3_show_chunk_boundaries", showChunkBoundaries));
		jTouchBar.addItem(new TouchBarItem("f3_advanced_tooltips", advancedTooltips));
		jTouchBar.addItem(new TouchBarItem("f3_copy_data", copyData));
		jTouchBar.addItem(new TouchBarItem("f3_cycle_gamemode", cycleGameMode));
		jTouchBar.addItem(new TouchBarItem("f3_toggle_auto_pause", toggleAutoPause));
		jTouchBar.addItem(new TouchBarItem("f3_reload_resource_packs", reloadResourcePacks));
		jTouchBar.addItem(new TouchBarItem("f3_pause_without_pause_menu", pauseWithoutPauseMenu));
		return jTouchBar;
	}

	private JTouchBar createInGameBar() {
		JTouchBar jTouchBar = new JTouchBar();
		String inGame = "inGame";
		jTouchBar.setCustomizationIdentifier(inGame);
//		Color btnColor = new Color(54, 54, 54, .21f);//Color.SYSTEM_BLUE;
		//<editor-fold desc="Hud">
		Logger.log(Level.INFO, "Creating hud button...");
		TouchBarButton hud = new TBButton(toggle).setEnabled(!mcc.options.hudHidden).setIcon(Icons.TOGGLE_HUD).setImagePosition(ImagePosition.ONLY).build();
		hud.setAction(view -> {
			if (hud.getTitle().equalsIgnoreCase("enabled")) {
				hud.setTitle("disabled");
				hud.setImage(Icons.TOGGLE_HUD.getDefaultIcon(false));
				mcc.options.hudHidden = true;
			}
			else {
				hud.setTitle("enabled");
				hud.setImage(Icons.TOGGLE_HUD.getDefaultIcon(true));
				mcc.options.hudHidden = false;
			}
		});
		Logger.log(Level.INFO, "Put");
		buttons.put((inGame + "/toggle_hud"), hud);
		Logger.log(Level.INFO, "hud");
		//</editor-fold>
		//<editor-fold desc="Screenshot">
		Logger.log(Level.INFO, "Creating screenshot button...");
		TouchBarButton screenshot = new TBButton(press).setTitle("Screenshot").setIcon(Icons.SCREENSHOT).setImagePosition(ImagePosition.ONLY).build();
		screenshot.setAction(view ->
				ScreenshotUtils.saveScreenshot(mcc.runDirectory, mcc.getWindow().getFramebufferWidth(), mcc.getWindow().getFramebufferHeight(), mcc.getFramebuffer(),
						textComponent -> mcc.execute(() -> mcc.inGameHud.getChatHud().addMessage(textComponent)))
		);
		buttons.put((inGame + "/screenshot"), screenshot);
		//</editor-fold>
		//<editor-fold desc="Debug">
		Logger.log(Level.INFO, "Creating debug button...");
		TouchBarButton debug = new TBButton(toggle).setEnabled(mcc.options.debugEnabled).setIcon(Icons.DEBUG_SCREEN).setImagePosition(ImagePosition.ONLY).build();
		debug.setAction(view -> {
			if (debug.getTitle().equalsIgnoreCase("enabled")) {
				debug.setTitle("disabled");
				mcc.options.debugEnabled = false;
				debug.setImage(Icons.DEBUG_SCREEN.getDefaultIcon(false));
				mcc.options.debugProfilerEnabled = Screen.hasShiftDown();
				mcc.options.debugTpsEnabled = Screen.hasAltDown();
			}
			else {
				debug.setTitle("enabled");
				debug.setImage(Icons.DEBUG_SCREEN.getDefaultIcon(true));
				mcc.options.debugEnabled = true;
				mcc.options.debugProfilerEnabled = false;
				mcc.options.debugTpsEnabled = false;
			}
		});
		buttons.put((inGame + "/debug_screen"), debug);
		//</editor-fold>
		//<editor-fold desc="Disable shaders">
		Logger.log(Level.INFO, "Creating disable shaders button...");
		TouchBarButton disableShaders = new TBButton(press).setTitle("Disable shaders").setIcon(Icons.DISABLE_SHADERS).setImagePosition(ImagePosition.ONLY).build();
		disableShaders.setAction(view -> {
			if (mcc.gameRenderer != null)
				mcc.gameRenderer.toggleShadersEnabled();
			{
				String file = "mctouchbar/icons/" + Icons.DISABLE_SHADERS.name().toLowerCase() + ".png";
				try {
					Identifier identifier = new Identifier("minecraft", file);
					Logger.log(Level.INFO, MinecraftClient.getInstance().getResourceManager().getResource(identifier).getInputStream());
				} catch (IOException e) {
					Logger.log(Level.ERROR, "Unable to load image \"minecraft:" + file + "\"");
					Logger.log(Level.ERROR, e);
				}
			}
		});
		buttons.put((inGame + "/disable_shaders"), disableShaders);
		//</editor-fold>
		//<editor-fold desc="Cycle camera">
		Logger.log(Level.INFO, "Creating cycle camera button...");
		TouchBarButton cycleCamera = new TBButton(cycle).setTitle("1").setIcon(Icons.CYCLE_CAMERA).setImagePosition(ImagePosition.ONLY).build();
		cycleCamera.setAction(view -> {
			final GameOptions options = mcc.options;
			++options.perspective;
			if (mcc.options.perspective > 2) {
				mcc.options.perspective = 0;
			}
			if (mcc.options.perspective == 0) {
				mcc.gameRenderer.onCameraEntitySet(mcc.getCameraEntity());
			}
			else if (mcc.options.perspective == 1) {
				mcc.gameRenderer.onCameraEntitySet(null);
			}
			mcc.worldRenderer.scheduleTerrainUpdate();
		});
		buttons.put((inGame + "/cycle_camera"), cycleCamera);
		//</editor-fold>
		//<editor-fold desc="Fullscreen">
		Logger.log(Level.INFO, "Creating fullscreen button...");
		TouchBarButton fullScreen = new TBButton(toggle).setEnabled(mcc.getWindow().isFullscreen()).setIcon(Icons.TOGGLE_FULLSCREEN).setImagePosition(ImagePosition.ONLY).build();
		fullScreen.setAction(view -> {
			if (fullScreen.getTitle().equalsIgnoreCase("enabled")) {
				fullScreen.setTitle("disabled");
				fullScreen.setImage(Icons.TOGGLE_FULLSCREEN.getIcon(false));
				mcc.getWindow().toggleFullscreen();
				mcc.options.fullscreen = false;
			}
			else {
				fullScreen.setTitle("enabled");
				fullScreen.setImage(Icons.TOGGLE_FULLSCREEN.getIcon(true));
				mcc.getWindow().toggleFullscreen();
				mcc.options.fullscreen = true;
			}
		});
		buttons.put((inGame + "/toggle_fullscreen"), fullScreen);
		//</editor-fold>
		
		//<editor-fold desc="Popover">
		Logger.log(Level.INFO, "Creating debug popover...");
		
		PopoverTouchBarItem popover = new PopoverTouchBarItem("popover");
//		popover.setCollapsedRepresentationImage(Images.get(Images.DEBUG, true, true));
		popover.setCollapsedRepresentation(new TBButton(press).setTitle("Debug").setIcon(Icons.DEBUG_SCREEN).build());
		popover.setCollapsedRepresentationLabel("Debug");
		//https://fontawesome.com/icons?d=gallery&q=tachometer
		popover.setShowsCloseButton(true);
		popover.setPopoverTouchBar(this.debugBar);
		//</editor-fold>

//		GroupTouchBarItem group = new GroupTouchBarItem("group");
//		group.setGroupTouchBar(inner);
//		jTouchBar.addItem(group);

//		jTouchBar.addItem(new TouchBarItem("health", healthBtn, true));
//		jTouchBar.addItem(new TouchBarItem("arrows", arrowsBtn, true));
//		jTouchBar.addItem(new TouchBarItem("coords", coords, true));

		Logger.log(Level.INFO, "Adding hud button to in-game TouchBar...");
		jTouchBar.addItem(new TouchBarItem("toggle_hud", hud, true));
		Logger.log(Level.INFO, "Adding screenshot button to in-game TouchBar...");
		jTouchBar.addItem(new TouchBarItem("screenshot", screenshot, true));
		Logger.log(Level.INFO, "Adding debug button to in-game TouchBar...");
		jTouchBar.addItem(new TouchBarItem("debug_screen", debug, true));
		Logger.log(Level.INFO, "Adding debug popover to in-game TouchBar...");
		jTouchBar.addItem(popover);
		Logger.log(Level.INFO, "Adding disable shaders button to in-game TouchBar...");
		jTouchBar.addItem(new TouchBarItem("disable_shaders", disableShaders, true));
		Logger.log(Level.INFO, "Adding cycle camera button to in-game TouchBar...");
		jTouchBar.addItem(new TouchBarItem("cycle_camera", cycleCamera, true));
		Logger.log(Level.INFO, "Adding fullscreen button to in-game TouchBar...");
		jTouchBar.addItem(new TouchBarItem("toggle_fullscreen", fullScreen, true));

//		jTouchBar.addItem(new TouchBarItem("slider", slider, true));
//		jTouchBar.addItem(new TouchBarItem("Scrubber_1", touchBarScrubber, true));

//		jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFixedSpaceLarge));
//		jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFixedSpaceSmall));
//		jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));

		Logger.log(Level.INFO, "Populated in-game TouchBar");
		return jTouchBar;
	}

}
