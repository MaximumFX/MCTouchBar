package nl.maximumfx.mctouchbar;

import com.thizzer.jtouchbar.JTouchBar;
import com.thizzer.jtouchbar.item.view.TouchBarButton;
import com.thizzer.jtouchbar.item.view.TouchBarTextField;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.client.ClientTickCallback;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.lwjgl.glfw.GLFWNativeCocoa;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainClass implements ModInitializer {

	private static MinecraftClient mcc;
	private static long window;
	private Screens active;
	private Bars bars;

	private static JTouchBar inGameTouchBar;
	private static JTouchBar mainTouchBar;

	private TouchBarButton healthBtn;
	private int oldHealth = 0;

	private TouchBarButton arrowsBtn;
	private int oldArrows = 0;

	private TouchBarTextField coords;
	private int[] oldCoords = new int[]{0, 0, 0};

	private TouchBarButton hud, screenshot, debug, shaders, cycleCamera, streamOnOff, streamPause, fullScreen;

//	private TouchBarButton hitBox;

	@Override
	public void onInitialize() {
		if (System.getProperty("os.name").toLowerCase().startsWith("mac")) {
			Logger.log(Level.INFO, "Initialised MCTouchBar");
			mcc = MinecraftClient.getInstance();
			Logger.log(Level.INFO, "Waiting on window...");

			Executors.newSingleThreadScheduledExecutor().schedule(() -> {
				Helper.init(this);
				Logger.log(Level.INFO, "Loading icons...");
				Icons.init();
				Logger.log(Level.INFO, "Creating TouchBars...");
				bars = new Bars();
				bars.init(this);
				mainTouchBar = bars.getInfoBar();
				inGameTouchBar = bars.getInGameBar();
				Logger.log(Level.INFO, "TouchBar content created");
				if (mcc.getWindow() != null) {
					window = GLFWNativeCocoa.glfwGetCocoaWindow(mcc.getWindow().getHandle());
					Logger.log(Level.INFO, "Info TouchBar Shown");

					ClientTickCallback.EVENT.register(client -> {
						//TODO Page specific bars
						if (active != Screens.getActive()) {
							active = Screens.getActive();
							Logger.log(Level.DEBUG, active);
							if (mcc.currentScreen == null) Logger.log(Level.INFO, "CS null");
							else if (active == null) Logger.log(Level.INFO, mcc.currentScreen.getTitle().asFormattedString());
							if (active == Screens.INGAME || active == Screens.GAME_MENU) {
								show(inGameTouchBar);
							}
							else {
								show(bars.getDebugBar());
							}
						}

						//Multiplayer: https://github.com/CCXia1997/minecraft1.14-dp/blob/34daddc03be27d5a0ee2ab9bc8b1deb050277208/net/minecraft/client/gui/menu/MultiplayerScreen.java
//						ClientPlayerEntity p = mcc.player;
//						if (p != null) {
////					int health = (int) p.getHealth();
////					if (health != oldHealth) {
////						healthBtn.setTitle(health + "");
////						oldHealth = health;
////					}
//
//							int arrows = 0;
//							for (int slot = 0; slot < p.inventory.getInvSize(); slot++) {
//								ItemStack stack = p.inventory.getInvStack(slot);
//
//								if (stack.getItem() instanceof ArrowItem) {
//									arrows = arrows + stack.getCount();
//								}
//							}
//							if (arrows != oldArrows || arrows == 0) {
//								arrowsBtn.setTitle(arrows + "");
//								oldArrows = arrows;
//							}
//
//							// Coordinates
//							int[] xyz = new int[]{(int) p.getX(), (int) p.getY(), (int) p.getZ()};
//							if (xyz != oldCoords) {
//								coords.setStringValue(xyz[0] + " " + xyz[1] + " " + xyz[2]);
//								oldCoords = xyz;
//							}
//
//				/*
//				if (healthEffectName != oldHealthEffectName) {
//					if (healthEffectName == "effect.absorption") {
//						MCTouchBar.touchBarButtonLifeImg.setImage(MCTouchBar.ABSO_IMAGE);
//						MCTouchBar.touchBarButtonLifeImg.setBezelColor(Color.YELLOW);
//					} else if (healthEffectName == "effect.poison") {
//						MCTouchBar.touchBarButtonLifeImg.setImage(MCTouchBar.POISON_IMAGE);
//						MCTouchBar.touchBarButtonLifeImg.setBezelColor(Color.GREEN);
//					} else if (healthEffectName == "effect.wither") {
//						MCTouchBar.touchBarButtonLifeImg.setImage(MCTouchBar.WITHER_IMAGE);
//						MCTouchBar.touchBarButtonLifeImg.setBezelColor(Color.PURPLE);
//					} else if (healthEffectName == "effect.regeneration") {
//						MCTouchBar.touchBarButtonLifeImg.setImage(MCTouchBar.HEALTH_IMAGE);
//						MCTouchBar.touchBarButtonLifeImg.setBezelColor(Color.MAGENTA);
//					} else if (healthEffectName == "effect.healthBoost") {
//						MCTouchBar.touchBarButtonLifeImg.setImage(MCTouchBar.BOOST_IMAGE);
//						MCTouchBar.touchBarButtonLifeImg.setBezelColor(Color.ORANGE);
//					} else if (healthEffectName == "effect.resistance") {
//						MCTouchBar.touchBarButtonLifeImg.setImage(MCTouchBar.RESISTANCE_IMAGE);
//						MCTouchBar.touchBarButtonLifeImg.setBezelColor(Color.GRAY);
//					} else {
//						MCTouchBar.touchBarButtonLifeImg.setImage(MCTouchBar.HEALTH_IMAGE);
//						MCTouchBar.touchBarButtonLifeImg.setBezelColor(Color.RED);
//					}
//					oldHealthEffectName = healthEffectName;
//				}
//				 */
//						}
//						else if (!arrowsBtn.getTitle().equalsIgnoreCase("--")) {
////							healthBtn.setTitle("--");
//							arrowsBtn.setTitle("--");
//							coords.setStringValue("-- -- --");
//						}
					});
				}
				else Logger.log(Level.ERROR, "Can't setup TouchBar");
			}, 5, TimeUnit.SECONDS);
		}
		else Logger.log(Level.FATAL, "Can't initialize MCTouchBar. This device is not a Mac.");
	}

	void reload() {
		Logger.log(Level.INFO, "Reloading MCTouchBar...");
		Icons.reload();
		bars.reload();
		mainTouchBar = bars.getInfoBar();
		inGameTouchBar = bars.getInGameBar();
		//TODO set updated bar
		Logger.log(Level.INFO, "Reloaded MCTouchBar.");
	}

	void show(JTouchBar touchBar) {
		touchBar.show(window);
	}
//
//	private JTouchBar getMainTouchBar() {
//		JTouchBar jTouchBar = new JTouchBar();
//		jTouchBar.setCustomizationIdentifier("main");
//		TouchBarTextField tbtf = new TouchBarTextField();
//		tbtf.setStringValue("MCTouchBar (by MaximumFX)");
//		jTouchBar.addItem(new TouchBarItem("mctb", tbtf));
//		return jTouchBar;
//	}
//
//	private JTouchBar getInGameTouchBar() {
//		JTouchBar jTouchBar = new JTouchBar();
//		jTouchBar.setCustomizationIdentifier("inGame");
//		//Health button
////		healthBtn = getButton("--", null, Color.RED, "regeneration.png", true, ImagePosition.RIGHT);
//		// Arrows button
//		arrowsBtn = getButton("--", null, Color.BLACK, "arrow_2x.png", true, ImagePosition.RIGHT);
//		// Coordinates
//		coords = new TouchBarTextField();
//		coords.setStringValue("-- -- --");
//
//		Color btnColor = new Color(54, 54, 54, .21f);//Color.SYSTEM_BLUE;
////		new Color(0, 100, 0);
//
//		//https://feathericons.com/
//
//		Logger.log(Level.INFO, "Creating hud button...");
//		//<editor-fold desc="Hud">
//		hud = getBoolButton(!mcc.options.hudHidden, ButtonType.ON_OFF, Icons.TOGGLE_HUD.getDefaultIcon(), ImagePosition.ONLY);
//		hud.setAction(view -> {
//			if (hud.getTitle().equalsIgnoreCase("enabled")) {
//				hud.setTitle("disabled");
//				hud.setImage(Icons.TOGGLE_HUD.getDefaultIcon(false));
//				mcc.options.hudHidden = true;
//			}
//			else {
//				hud.setTitle("enabled");
//				hud.setImage(Icons.TOGGLE_HUD.getDefaultIcon(true));
//				mcc.options.hudHidden = false;
//			}
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating screenshot button...");
//		//<editor-fold desc="Screenshot">
//		screenshot = getButton("Screenshot", ButtonType.SWITCH, btnColor, Icons.SCREENSHOT.getDefaultIcon(), ImagePosition.ONLY);
//		screenshot.setAction(view -> {
////				ScreenshotUtils.takeScreenshot(mcc.getWindow().getWidth(), mcc.getWindow().getHeight(), mcc.getFramebuffer());
//			ScreenshotUtils.saveScreenshot(mcc.runDirectory, mcc.getWindow().getFramebufferWidth(), mcc.getWindow().getFramebufferHeight(), mcc.getFramebuffer(),
//					textComponent -> mcc.execute(() -> mcc.inGameHud.getChatHud().addMessage(textComponent)));
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating debug button...");
//		//<editor-fold desc="Debug">
//		debug = getBoolButton(mcc.options.debugEnabled, ButtonType.SWITCH, Icons.DEBUG_SCREEN.getDefaultIcon(mcc.options.debugEnabled), ImagePosition.ONLY);
//		debug.setAction(view -> {
//			if (debug.getTitle().equalsIgnoreCase("enabled")) {
//				debug.setTitle("disabled");
//				mcc.options.debugEnabled = false;
//				debug.setImage(Icons.DEBUG_SCREEN.getDefaultIcon(false));
//				mcc.options.debugProfilerEnabled = Screen.hasShiftDown();
//				mcc.options.debugTpsEnabled = Screen.hasAltDown();
//			}
//			else {
//				debug.setTitle("enabled");
//				debug.setImage(Icons.DEBUG_SCREEN.getDefaultIcon(true));
//				mcc.options.debugEnabled = true;
//				mcc.options.debugProfilerEnabled = false;
//				mcc.options.debugTpsEnabled = false;
//			}
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating disable shaders button...");
//		//<editor-fold desc="Disable shaders">
//		shaders = getButton("Disable shaders", null, btnColor, Icons.DISABLE_SHADERS.getDefaultIcon(), ImagePosition.ONLY);
//		shaders.setAction(view -> {
//			if (mcc.gameRenderer != null)
//				mcc.gameRenderer.toggleShadersEnabled();
//			String file = "mctouchbar/icons/" + Icons.DISABLE_SHADERS.name().toLowerCase() + ".png";
//			try {
//				Identifier identifier = new Identifier("minecraft", file);
//				Logger.log(Level.INFO, MinecraftClient.getInstance().getResourceManager().getResource(identifier).getInputStream());
//			} catch (IOException e) {
//				Logger.log(Level.ERROR, "Unable to load image \"minecraft:" + file + "\"");
//				Logger.log(Level.ERROR, e);
//			}
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating cycle camera button...");
//		//<editor-fold desc="Cycle camera">
//		cycleCamera = getButton("1", ButtonType.SWITCH, btnColor, Icons.CYCLE_CAMERA.getDefaultIcon(), ImagePosition.ONLY);
//		cycleCamera.setAction(view -> {
//			final GameOptions options = mcc.options;
//			++options.perspective;
//			if (mcc.options.perspective > 2) {
//				mcc.options.perspective = 0;
//			}
//			if (mcc.options.perspective == 0) {
//				mcc.gameRenderer.onCameraEntitySet(mcc.getCameraEntity());
//			}
//			else if (mcc.options.perspective == 1) {
//				mcc.gameRenderer.onCameraEntitySet(null);
//			}
//			mcc.worldRenderer.scheduleTerrainUpdate();
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating fullscreen button...");
//		//<editor-fold desc="Fullscreen">
//		fullScreen = getBoolButton(mcc.getWindow().isFullscreen(), ButtonType.SWITCH, Icons.TOGGLE_FULLSCREEN.getDefaultIcon(mcc.getWindow().isFullscreen()), ImagePosition.ONLY);
//		fullScreen.setAction(view -> {
//			if (fullScreen.getTitle().equalsIgnoreCase("enabled")) {
//				fullScreen.setTitle("disabled");
//				fullScreen.setImage(Icons.TOGGLE_FULLSCREEN.getIcon(false));
//				mcc.getWindow().toggleFullscreen();
//				mcc.options.fullscreen = false;
//			}
//			else {
//				fullScreen.setTitle("enabled");
//				fullScreen.setImage(Icons.TOGGLE_FULLSCREEN.getIcon(true));
//				mcc.getWindow().toggleFullscreen();
//				mcc.options.fullscreen = true;
//			}
//		});
//		//</editor-fold>
//
//		//<editor-fold desc="Slider">
////		TouchBarSlider slider = new TouchBarSlider();
////		slider.setMinValue(1.0);
////		slider.setMaxValue(1000.0);
////		slider.setActionListener((slider1, value) -> {
////			Logger.log(Level.INFO, "Selected Scrubber Index: " + value);
////		});
//		//</editor-fold>
//
//		//<editor-fold desc="Scrubber">
//		/*TouchBarScrubber touchBarScrubber = new TouchBarScrubber();
//		touchBarScrubber.setActionListener((scrubber, index) -> Logger.log(Level.INFO, "Selected Scrubber Index: " + index));
//		touchBarScrubber.setDataSource(new ScrubberDataSource() {
//			@Override
//			public int getNumberOfItems(TouchBarScrubber scrubber) {
//				return 2;
//			}
//			@Override
//			public ScrubberView getViewForIndex(TouchBarScrubber scrubber, long index) {
//				if (index == 0) {
//					ScrubberTextItemView textItemView = new ScrubberTextItemView();
//					textItemView.setIdentifier("ScrubberItem_1");
//					textItemView.setStringValue("Scrubber TextItem");
//					return textItemView;
//				}
//				else {
//					ScrubberImageItemView imageItemView = new ScrubberImageItemView();
//					imageItemView.setIdentifier("ScrubberItem_2");
//					imageItemView.setImage(new Image(ImageName.NSImageNameTouchBarAlarmTemplate, false));
//					imageItemView.setAlignment(ImageAlignment.CENTER);
//					return imageItemView;
//				}
//			}
//		});
//		touchBarScrubber.setMode(ScrubberMode.FREE);
//		touchBarScrubber.setShowsArrowButtons(true);
//		jTouchBar.addItem(new TouchBarItem("Scrubber_1", touchBarScrubber, true));
//
//		TouchBarScrubber touchBarScrubber = new TouchBarScrubber();
//		touchBarScrubber.setActionListener((scrubber, index) -> Logger.log(Level.INFO, "Selected Scrubber Index: " + index));
//		touchBarScrubber.setDataSource(new ScrubberDataSource() {
//			@Override
//			public ScrubberView getViewForIndex(TouchBarScrubber scrubber, long index) {
//				if (index == 0) {
//					ScrubberTextItemView textItemView = new ScrubberTextItemView();
//					textItemView.setIdentifier("ScrubberItem_1");
//					textItemView.setStringValue("Scrubber TextItem");
//
//					return textItemView;
//				} else {
//					ScrubberImageItemView imageItemView = new ScrubberImageItemView();
//					imageItemView.setIdentifier("ScrubberItem_2");
//					imageItemView.setImage(getTouchBarImageForPath("/assets/mctouchbar/TB_hitbox_enabled_white.png"));
//
////                    MinecraftClient.getInstance().getItemRenderer().renderGuiItem(new ItemStack(Items.ACACIA_LOG, 1), 0, 0);
////					OptionParser optionParser = new OptionParser();
////					optionParser.allowsUnrecognizedOptions();
////					OptionSpec<File> optionSpec4 = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
////
////					File file2 = optionSet.has(optionSpec4) ? (File)getOption(optionSet, optionSpec4) : new File(file, "assets/");
//
////					try {
////					    Identifier item = Registry.ITEM.getId(Items.STONE);
////						LOGGER.info(item);
////						MinecraftClient.getInstance().getResourceManager().getResource(new Identifier("minecraft:textures/atlas/mob_effects.png"));
////					} catch (IOException e) {
////						e.printStackTrace();
////					}
////					textureManager.getTexture(new Identifier("minecraft:textures/atlas/blocks.png")).getSprite(new Identifier("minecraft:block/oak_door_top"));
//					imageItemView.setAlignment(ImageAlignment.CENTER);
//
//					return imageItemView;
//				}
//			}
//
//			@Override
//			public int getNumberOfItems(TouchBarScrubber scrubber) {
//				return 2;
//			}
//		});
//
//		jTouchBar.addItem(new TouchBarItem("Scrubber_1", touchBarScrubber, true));
//		 */
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating debug popover...");
//
//		//<editor-fold desc="Popover">
//		PopoverTouchBarItem popover = new PopoverTouchBarItem("popover");
////		popover.setCollapsedRepresentationImage(Images.get(Images.DEBUG, true, true));
//		popover.setCollapsedRepresentation(getButton(null, null, btnColor, Icons.DEBUG_SCREEN.getDefaultIcon(), ImagePosition.ONLY));
//		popover.setCollapsedRepresentationLabel("Debug");
//		//https://fontawesome.com/icons?d=gallery&q=tachometer
//		popover.setShowsCloseButton(true);
//		//<editor-fold desc="Reload chunks">
//		Logger.log(Level.INFO, "Creating reload chunks button...");
//		TouchBarButton reloadChunks = getButton("Reload chunks", ButtonType.MOMENTARY_PUSH_IN, btnColor, Icons.F3_RELOAD_CHUNKS.getDefaultIcon(), ImagePosition.ONLY);
//		reloadChunks.setAction(view -> {
//			mcc.worldRenderer.reload();
//			debugWarn("debug.reload_chunks.message");
//		});
//		//</editor-fold>
//		//<editor-fold desc="Show hitboxes">
//		Logger.log(Level.INFO, "Creating show hit boxes button...");
//		TouchBarButton showHitboxes = getBoolButton(mcc.getEntityRenderManager() != null && mcc.getEntityRenderManager().shouldRenderHitboxes(), ButtonType.ON_OFF, Icons.F3_SHOW_HITBOXES.getDefaultIcon(), ImagePosition.ONLY);
//		showHitboxes.setAction(view -> {
//			if (showHitboxes.getTitle().equalsIgnoreCase("enabled")) {
//				showHitboxes.setTitle("disabled");
//				showHitboxes.setBezelColor(Color.DARK_GRAY);
//				mcc.getEntityRenderManager().setRenderHitboxes(false);
//				debugWarn("debug.show_hitboxes.off");
//			}
//			else {
//				showHitboxes.setTitle("enabled");
//				showHitboxes.setBezelColor(Color.GRAY);
//				mcc.getEntityRenderManager().setRenderHitboxes(true);
//				debugWarn("debug.show_hitboxes.on");
//			}
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating copy location button...");
//		//<editor-fold desc="Copy location for tp">
//		TouchBarButton copyLocation = getButton("Copy location", ButtonType.MOMENTARY_PUSH_IN, btnColor, Icons.F3_COPY_LOCATION.getDefaultIcon(), ImagePosition.ONLY);
//		copyLocation.setAction(view -> {
//			ClientPlayerEntity p = mcc.player;
//			if (p != null) {
//				if (!mcc.player.getReducedDebugInfo()) {
//					debugWarn("debug.copy_location.message");
//					mcc.keyboard.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", DimensionType.getId(mcc.player.world.dimension.getType()), mcc.player.getX(), mcc.player.getY(), mcc.player.getZ(), mcc.player.yaw, mcc.player.pitch));
//				}
//			}
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating clear chat button...");
//		//<editor-fold desc="Clear chat">
//		TouchBarButton clearChat = getButton("Clear chat", ButtonType.MOMENTARY_PUSH_IN, btnColor, Icons.F3_CLEAR_CHAT.getDefaultIcon(), ImagePosition.ONLY);//NSImageNameTouchBarTextStrikethroughTemplate
//		clearChat.setAction(view -> {
//			if (mcc.inGameHud != null)
//				mcc.inGameHud.getChatHud().clear(false);
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating cycle render distance button...");
//		//<editor-fold desc="Cycle render distance (Shift to invert)">
//		TouchBarButton cycleRenderDistance = getButton("Cycle render distance", ButtonType.ACCELERATOR, btnColor, Icons.F3_CYCLE_RENDER_DISTANCE.getDefaultIcon(), ImagePosition.ONLY);
//		cycleRenderDistance.setAction(view -> {
////			GameOption.RENDER_DISTANCE.set(mcc.options, MathHelper.clamp(mcc.options.viewDistance + (Screen.hasShiftDown() ? -1 : 1), GameOption.RENDER_DISTANCE.getMin(), GameOption.RENDER_DISTANCE.getMax()));
//			mcc.options.viewDistance = (int) MathHelper.clamp(mcc.options.viewDistance + (Screen.hasShiftDown() ? -1 : 1), 2.0f, 16.0f);
//			mcc.options.write();
//			mcc.worldRenderer.scheduleTerrainUpdate();
//			debugWarn("debug.cycle_renderdistance.message", mcc.options.viewDistance);
//
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating show chunk boundaries button...");
//		//<editor-fold desc="Show chunk boundaries">
//		TouchBarButton showChunkBoundaries = getBoolButton(false, ButtonType.TOGGLE, Icons.F3_SHOW_CHUNK_BOUNDARIES.getDefaultIcon(), ImagePosition.ONLY);
//		showChunkBoundaries.setAction(view -> {
//			boolean showChunkBorder = mcc.debugRenderer.toggleShowChunkBorder();
//			showChunkBoundaries.setTitle(showChunkBorder ? "enabled" : "disabled");
//			if (!showChunkBorder) {
//				showChunkBoundaries.setTitle("disabled");
//				showChunkBoundaries.setBezelColor(Color.DARK_GRAY);
//			}
//			else {
//				showChunkBoundaries.setTitle("enabled");
//				showChunkBoundaries.setBezelColor(Color.GRAY);
//			}
//			debugWarn(showChunkBorder ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating advanced tooltips button...");
//		//<editor-fold desc="Advanced tooltips">
//		TouchBarButton advancedTooltips = getBoolButton(mcc.options.advancedItemTooltips, ButtonType.PUSH_ON_PUSH_OFF, Icons.F3_ADVANCED_TOOLTIPS.getDefaultIcon(), ImagePosition.ONLY);
//		advancedTooltips.setAction(view -> {
//			if (debug.getTitle().equalsIgnoreCase("enabled")) {
//				debug.setTitle("disabled");
//				debug.setBezelColor(Color.DARK_GRAY);
//				mcc.options.advancedItemTooltips = false;
//				debugWarn("debug.advanced_tooltips.off");
//			}
//			else {
//				debug.setTitle("enabled");
//				debug.setBezelColor(Color.GRAY);
//				mcc.options.advancedItemTooltips = true;
//				debugWarn("debug.advanced_tooltips.on");
//			}
//			mcc.options.write();
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating copy data button...");
//		//<editor-fold desc="Copy data">
//		TouchBarButton copyData = getButton("Copy data", ButtonType.MOMENTARY_PUSH_IN, btnColor, Icons.F3_COPY_DATA.getDefaultIcon(), ImagePosition.ONLY);
//		copyData.setAction(view -> {
//			if (mcc.player != null && !mcc.player.getReducedDebugInfo()) {
//				copyLookAt(mcc.player.allowsPermissionLevel(2), !Screen.hasShiftDown());
//			}
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating cycle gamemode button...");
//		//<editor-fold desc="Cycle gamemode">
//		TouchBarButton cycleGameMode = getButton("Cycle gamemode", ButtonType.SWITCH, btnColor, Icons.F3_CYCLE_GAMEMODE.getDefaultIcon(), ImagePosition.ONLY);
//		cycleGameMode.setAction(view -> {
//			if (mcc.player != null) {
//				if (!mcc.player.allowsPermissionLevel(2)) {
//					debugWarn("debug.creative_spectator.error");
//				}
//				else if (mcc.player.isCreative()) {
//					mcc.player.sendChatMessage("/gamemode spectator");
//				}
//				else if (mcc.player.isSpectator() || !mcc.player.isCreative() && !mcc.player.isSpectator()) {
//					mcc.player.sendChatMessage("/gamemode creative");
//				}
//			}
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating toggle auto pause button...");
//		//<editor-fold desc="Toggle auto pause">
//		TouchBarButton toggleAutoPause = getBoolButton(mcc.options.pauseOnLostFocus, ButtonType.TOGGLE, Icons.F3_TOGGLE_AUTO_PAUSE.getDefaultIcon(), ImagePosition.ONLY);
//		toggleAutoPause.setAction(view -> {
//			if (toggleAutoPause.getTitle().equalsIgnoreCase("enabled")) {
//				toggleAutoPause.setTitle("disabled");
//				toggleAutoPause.setBezelColor(Color.DARK_GRAY);
//				mcc.options.pauseOnLostFocus = false;
//				debugWarn("debug.pause_focus.off");
//			}
//			else {
//				toggleAutoPause.setTitle("enabled");
//				toggleAutoPause.setBezelColor(Color.GRAY);
//				mcc.options.pauseOnLostFocus = true;
//				debugWarn("debug.pause_focus.on");
//			}
//			mcc.options.write();
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating reload resource packs button...");
//		//<editor-fold desc="Reload resource packs">
//		TouchBarButton reloadResourcePacks = getButton("Reload resource packs", ButtonType.MOMENTARY_PUSH_IN, btnColor, Icons.F3_RELOAD_RESOURCE_PACKS.getDefaultIcon(), ImagePosition.ONLY);
//		reloadResourcePacks.setAction(view -> {
//			debugWarn("debug.reload_resourcepacks.message");
//			mcc.reloadResources();
//			Icons.reload();
//			//TODO recreate touch bar
//		});
//		//</editor-fold>
//		Logger.log(Level.INFO, "Creating pause without menu button...");
//		//<editor-fold desc="Pause without pause menu">
//		TouchBarButton pauseWithoutPauseMenu = getButton("Pause without pause menu", ButtonType.MOMENTARY_PUSH_IN, btnColor, Icons.F3_PAUSE_WITHOUT_PAUSE_MENU.getDefaultIcon(), ImagePosition.ONLY);
//		pauseWithoutPauseMenu.setAction(view -> {
//			mcc.openPauseMenu(false);
//		});
//		//</editor-fold>
//
//		Logger.log(Level.INFO, "Creating debug popover TouchBar...");
//		JTouchBar inner = new JTouchBar();
//		inner.setCustomizationIdentifier("innerTouch");
//		inner.addItem(new TouchBarItem("reloadChunks", reloadChunks));
//		inner.addItem(new TouchBarItem("showHitboxes", showHitboxes));
//		inner.addItem(new TouchBarItem("copyLocation", copyLocation));
//		inner.addItem(new TouchBarItem("clearChat", clearChat));
//		inner.addItem(new TouchBarItem("cycleRenderDistance", cycleRenderDistance));
//		inner.addItem(new TouchBarItem("showChunkBoundaries", showChunkBoundaries));
//		inner.addItem(new TouchBarItem("advancedTooltips", advancedTooltips));
//		inner.addItem(new TouchBarItem("copyData", copyData));
//		inner.addItem(new TouchBarItem("cycleGameMode", cycleGameMode));
//		inner.addItem(new TouchBarItem("toggleAutoPause", toggleAutoPause));
//		inner.addItem(new TouchBarItem("reloadResourcePacks", reloadResourcePacks));
//		inner.addItem(new TouchBarItem("pauseWithoutPauseMenu", pauseWithoutPauseMenu));
//		popover.setPopoverTouchBar(inner);
//		//</editor-fold>
//
////		GroupTouchBarItem group = new GroupTouchBarItem("group");
////		group.setGroupTouchBar(inner);
////		jTouchBar.addItem(group);
//
////		jTouchBar.addItem(new TouchBarItem("health", healthBtn, true));
////		jTouchBar.addItem(new TouchBarItem("arrows", arrowsBtn, true));
////		jTouchBar.addItem(new TouchBarItem("coords", coords, true));
//
//		Logger.log(Level.INFO, "Adding hud button to in-game TouchBar...");
//		jTouchBar.addItem(new TouchBarItem("hud", hud, true));
//		Logger.log(Level.INFO, "Adding screenshot button to in-game TouchBar...");
//		jTouchBar.addItem(new TouchBarItem("screenshot", screenshot, true));
//		Logger.log(Level.INFO, "Adding debug button to in-game TouchBar...");
//		jTouchBar.addItem(new TouchBarItem("debug", debug, true));
//		Logger.log(Level.INFO, "Adding debug popover to in-game TouchBar...");
//		jTouchBar.addItem(popover);
//		Logger.log(Level.INFO, "Adding disable shaders button to in-game TouchBar...");
//		jTouchBar.addItem(new TouchBarItem("shaders", shaders, true));
//		Logger.log(Level.INFO, "Adding cycle camera button to in-game TouchBar...");
//		jTouchBar.addItem(new TouchBarItem("cycleCamera", cycleCamera, true));
//		Logger.log(Level.INFO, "Adding fullscreen button to in-game TouchBar...");
//		jTouchBar.addItem(new TouchBarItem("fullScreen", fullScreen, true));
//
////		jTouchBar.addItem(new TouchBarItem("slider", slider, true));
////		jTouchBar.addItem(new TouchBarItem("Scrubber_1", touchBarScrubber, true));
//
////		jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFixedSpaceLarge));
////		jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFixedSpaceSmall));
////		jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));
//
//		Logger.log(Level.INFO, "Populated in-game TouchBar");
//		return jTouchBar;
//	}
//
//	private TouchBarButton getButton(String title, ButtonType buttonType, Color color, String image, boolean isPath, int postition) {
//		TouchBarButton btn = new TouchBarButton();
//		if (title != null) btn.setTitle(title);
//		if (buttonType != null) btn.setType(buttonType);
//		if (color != null) btn.setBezelColor(color);
//		if (color != null) btn.setBezelColor(color);
//		if (!image.equalsIgnoreCase("")) {
//			if (isPath) btn.setImage(getTouchBarImageForPath("/assets/mctouchbar/" + image));
//			else btn.setImage(new Image(image, false));
//		}
//		if (postition > -1) btn.setImagePosition(postition);
//		return btn;
//	}
//
//	private TouchBarButton getButton(String title, ButtonType buttonType, Color color, Image image, int postition) {
//		TouchBarButton btn = new TouchBarButton();
//		btn.setTitle(title);
//		if (buttonType != null) btn.setType(buttonType);
//		if (color != null) btn.setBezelColor(color);
//		if (color != null) btn.setBezelColor(color);
//		btn.setImage(image);
//		if (postition > -1) btn.setImagePosition(postition);
//		return btn;
//	}
//	private TouchBarButton getBoolButton(boolean status, ButtonType buttonType, Image image, int postition) {
//		return getButton(status ? "enabled" : "disabled", buttonType, new Color(54, 54, 54, .21f), image, postition);//status ? Color.GRAY : Color.DARK_GRAY
//	}
//
//	private Image getTouchBarImageForPath(String path) {
//		try {
//			InputStream stream = MainClass.class.getResourceAsStream(path);
//			return new Image(stream);
//		} catch(Exception e) {
//			Logger.log(Level.INFO, "Unable to load image \"" + path + "\"");
//			byte[] nullImageByteArray = new byte[] {};
//			return new Image(nullImageByteArray);
//		}
//	}


//	https://github.com/CCXia1997/minecraft1.14-dp/blob/34daddc03be27d5a0ee2ab9bc8b1deb050277208/net/minecraft/client/Keyboard.java#L168
	public void debugWarn(final String string, final Object... arr) {
		mcc.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix").formatted(Formatting.YELLOW, Formatting.BOLD)).append(" ").append(new TranslatableText(string, arr)));
	}

//	public void debugError(final String string, final Object... arr) {
//		mcc.inGameHud.getChatHud().addMessage(new LiteralText("").append(new TranslatableText("debug.prefix").formatted(Formatting.RED, Formatting.BOLD)).append(" ").append(new TranslatableText(string, arr)));
//	}

}
