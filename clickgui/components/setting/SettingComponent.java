package fun.mentalium.ui.clickgui.components.setting;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.api.interfaces.IMouse;
import fun.mentalium.constructor.modules.settings.Setting;
import fun.mentalium.ui.clickgui.components.WindowComponent;
import fun.mentalium.utils.render.font.Font;
import fun.mentalium.utils.render.font.Fonts;
import fun.mentalium.utils.render.text.TextUtils;

@Data
@Accessors(fluent = true)
@EqualsAndHashCode(callSuper = true)
public abstract class SettingComponent extends WindowComponent implements IMouse {
    public Setting<?> value;
    public final Font font = Fonts.sf_medium;
    public final float fontSize = 6;
    public float margin = 4;
    private final String splitter = "-";

    public SettingComponent(final Setting<?> value) {
        this.value = value;
        size.set(100, 20);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        hoverAnimation.update();
    }

    public float drawName(MatrixStack matrix, float width) {
        int color = textColor();
        return font.drawSplitted(matrix, value.getName(), splitter, position.x, position.y + margin, width, color, fontSize);
    }

    public float valueHeight() {
        return TextUtils.splitLineHeight(value.getName(), font, fontSize, size.x, splitter) * fontSize;
    }
}