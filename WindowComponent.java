package fun.mentalium.ui.clickgui.components;

import fun.mentalium.constructor.modules.impl.render.HUD;
import lombok.Data;
import lombok.experimental.Accessors;
import fun.mentalium.Mentalium;
import fun.mentalium.constructor.api.interfaces.IScreen;
import fun.mentalium.ui.clickgui.ClickGUIScreen;
import fun.mentalium.utils.animation.Animation;
import fun.mentalium.utils.math.Mathf;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.font.Font;
import fun.mentalium.utils.render.font.Fonts;
import org.joml.Vector2f;

@Data
@Accessors(fluent = true)
public abstract class WindowComponent implements IScreen {
    public Vector2f position = new Vector2f();
    public Vector2f size = new Vector2f();
    public final Animation hoverAnimation = new Animation();
    public final Animation expandAnimation = new Animation();
    public final Font font = Fonts.sf_medium;
    public final float moduleFontSize = 7;
    public final float categoryFontSize = 8;

    public float outline = 2F;
    public float round = 4F;

    public boolean isHover(double mouseX, double mouseY) {
        return isHover(mouseX, mouseY, position.x, position.y, size.x, size.y);
    }

    public int alpha() {
        return Math.round(alphaPC() * 255F);
    }

    public float alphaPC() {
        return Mathf.clamp01(clickgui().alpha().get());
    }

    public int backgroundColor() {
        return ColorUtil.getColor(20, 20, 28, alphaPC() / 1.5F);
    }

    public int getWhite() {
        return ColorUtil.multAlpha(-1, alpha());
    }

    public int darkClientColor() {
        return ColorUtil.multAlpha(HUD.getInstance().darkClientColor(), alphaPC());
    }

    public int clientColor() {
        return ColorUtil.multAlpha(HUD.getInstance().clientColor(), alphaPC());
    }
    public int darkTextColor() {
        return ColorUtil.multAlpha(HUD.getInstance().darkTextColor(), alphaPC());
    }
    public int textColor() {
        return ColorUtil.multAlpha(HUD.getInstance().textColor(), alphaPC());
    }
    public int iconColor() {
        return ColorUtil.multAlpha(HUD.getInstance().iconColor(), alphaPC());
    }
    public boolean isHover(int mouseX, int mouseY) {
        return isHover(mouseX, mouseY, position.x, position.y, size.x, size.y);
    }

    public ClickGUIScreen clickgui() {
        return Mentalium.getInstance().clickGui();
    }

    public Panel panel() {
        return clickgui().panel();
    }
}
