package nl.maximumfx.mctouchbar;

import com.thizzer.jtouchbar.common.Color;
import com.thizzer.jtouchbar.common.Image;
import com.thizzer.jtouchbar.common.ImagePosition;
import com.thizzer.jtouchbar.item.view.TouchBarButton;
import com.thizzer.jtouchbar.item.view.TouchBarButton.ButtonType;
import com.thizzer.jtouchbar.item.view.action.TouchBarViewAction;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TBButton {

	private ButtonType buttonType;
	private String title = "";
	private Boolean enabled;
	private Color color;
	private Image image;
	private Icons icon;
	private int imagePosition = ImagePosition.NO;
	private TouchBarViewAction action;

	TBButton(@NotNull ButtonType buttonType) {
		this.buttonType = buttonType;
	}

	public TBButton setButtonType(@NotNull ButtonType buttonType) {
		this.buttonType = buttonType;
		return this;
	}
	@NotNull
	public ButtonType getButtonType() {
		return buttonType;
	}

	TBButton setTitle(@Nullable String title) {
		this.title = title;
		return this;
	}
	@Nullable
	public String getTitle() {
		return title;
	}

	TBButton setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.title = enabled ? "enabled" : "disabled";
		return this;
	}
	public Boolean getEnabled() {
		return enabled;
	}

	public TBButton setColor(@Nullable Color color) {
		this.color = color;
		return this;
	}
	@Nullable
	public Color getColor() {
		return color;
	}

	public TBButton setImage(@Nullable Image image) {
		this.image = image;
		return this;
	}
	@Nullable
	public Image getImage() {
		return image;
	}

	TBButton setIcon(@Nullable Icons icon) {
		this.icon = icon;
		if (icon == null) this.image = null;
		else {
			if (enabled != null)
				this.image = icon.getIcon(enabled);
			else
				this.image = icon.getIcon();
		}
		return this;
	}
	@Nullable
	public Icons getIcon() {
		return icon;
	}

	TBButton setImagePosition(int imagePosition) {
		this.imagePosition = imagePosition;
		return this;
	}
	public int getImagePosition() {
		return imagePosition;
	}

	public TBButton setAction(@Nullable TouchBarViewAction action) {
		this.action = action;
		return this;
	}
	@Nullable
	public TouchBarViewAction getAction() {
		return action;
	}

	TouchBarButton build() {
		TouchBarButton btn = new TouchBarButton();
		if (title != null) btn.setTitle(title);
		if (buttonType != null) btn.setType(buttonType);
		if (color != null) btn.setBezelColor(color);
		if (image != null) btn.setImage(image);
		btn.setImagePosition(imagePosition);
		if (action != null) btn.setAction(action);
		return btn;
	}
}
