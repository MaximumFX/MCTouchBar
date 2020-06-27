package nl.maximumfx.mctouchbar;

import com.thizzer.jtouchbar.common.Image;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public enum Icons {
	TOGGLE_HUD(true),
	SCREENSHOT,
	DEBUG_SCREEN(true),
	F3_HELP,
	F3_RELOAD_CHUNKS,
	F3_SHOW_HITBOXES,
	F3_COPY_LOCATION,
	F3_CLEAR_CHAT,
	F3_CYCLE_RENDER_DISTANCE(true),
	F3_SHOW_CHUNK_BOUNDARIES,
	F3_ADVANCED_TOOLTIPS(true),
	F3_COPY_DATA,
	F3_CYCLE_GAMEMODE,
	F3_TOGGLE_AUTO_PAUSE,
	F3_RELOAD_RESOURCE_PACKS,
	F3_PAUSE_WITHOUT_PAUSE_MENU,
	DISABLE_SHADERS,
	CYCLE_CAMERA,
	STREAM_ON_OF,
	PAUSE_STREAM,
	TOGGLE_FULLSCREEN(true);

	private boolean hasDisabled;
	Icons() {
		this.hasDisabled = false;
	}
	Icons(boolean hasDisabled) {
		this.hasDisabled = hasDisabled;
	}

	public boolean hasDisabled() {
		return hasDisabled;
	}

	private static Map<Icons, Image> enabledIcons;
	private static Map<Icons, Image> disabledIcons;

	public static void init() {
		enabledIcons = new HashMap<>();
		disabledIcons = new HashMap<>();
		for (Icons icon: Icons.values()) {
			System.out.println(icon.name());
			System.out.println(icon.hasDisabled() + "");
			icon.getDefaultIcon();
			System.out.println(1);
			if (icon.hasDisabled()) icon.getDefaultIcon(false);
			System.out.println(2);
		}
	}
	public static void reload() {
		Logger.log(Level.INFO, "Reloading icons.");
		enabledIcons = new HashMap<>();
		disabledIcons = new HashMap<>();
		for (Icons icon: Icons.values()) {
			icon.getIcon();
			if (icon.hasDisabled) icon.getIcon(false);
		}
	}

	public Image getDefaultIcon() {
		return getDefaultIcon(true);
	}
	public Image getDefaultIcon(boolean enabled) {
		if (enabled && enabledIcons.containsKey(this)) return enabledIcons.get(this);
		if (!enabled && disabledIcons.containsKey(this)) return disabledIcons.get(this);
		String file = "/assets/mctouchbar/icons/" + this.name().toLowerCase() + (enabled ? "" : "_disabled") + ".png";
		try {
			InputStream stream = MainClass.class.getResourceAsStream(file);
			Image img = getScaledImage(stream);
			if (enabled) enabledIcons.put(this, img);
			else disabledIcons.put(this, img);
			return img;
		} catch (IOException e) {
			Logger.log(Level.ERROR, "Unable to load image \"" + file + "\".");
			return new Image(new byte[]{});
		}
	}

	public Image getIcon() {
		return getIcon(true);
	}
	public Image getIcon(boolean enabled) {
		if (enabled && enabledIcons.containsKey(this)) return enabledIcons.get(this);
		if (!enabled && disabledIcons.containsKey(this)) return disabledIcons.get(this);
		String file = "mctouchbar/icons/" + this.name().toLowerCase() + (enabled ? "" : "_disabled") + ".png";
		try {
			Identifier identifier = new Identifier("minecraft", file);
			Image img = getScaledImage(MinecraftClient.getInstance().getResourceManager().getResource(identifier).getInputStream());
			if (enabled) enabledIcons.put(this, img);
			else disabledIcons.put(this, img);
			return img;
		} catch (IOException e) {
			Logger.log(Level.ERROR, "Unable to load image \"minecraft:" + file + "\", trying default image.");
			return getDefaultIcon();
		}
	}

	public Image getScaledImage(InputStream inputStream) throws IOException {
		int imgSize = 44;
		int scaled = 32;
		BufferedImage originalImage = ImageIO.read(inputStream);
		if (originalImage.getWidth() >= imgSize)
			return new Image(inputStream);
		inputStream.close();
		BufferedImage newImage = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = newImage.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2.drawImage(originalImage, (imgSize - scaled)/2, (imgSize - scaled)/2, scaled, scaled, null);
		g2.dispose();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			ImageIO.write(newImage, "png", stream);
		} catch (IOException e) {
			Logger.log(Level.ERROR, "Unable to load image.");
			return new Image(new byte[]{});
		}
		return new Image(new ByteArrayInputStream(stream.toByteArray()));
	}
}
