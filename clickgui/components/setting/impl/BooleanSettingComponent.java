package fun.mentalium.ui.clickgui.components.setting.impl;

import fun.mentalium.utils.render.font.Fonts;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.modules.settings.impl.BooleanSetting;
import fun.mentalium.ui.clickgui.components.setting.SettingComponent;
import fun.mentalium.utils.animation.util.Easings;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.draw.RenderUtil;
import fun.mentalium.utils.render.draw.Round;

public class BooleanSettingComponent extends SettingComponent {
    private final BooleanSetting value;

    public BooleanSettingComponent(BooleanSetting value) {
        super(value);
        this.value = value;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {

    }

    @Override
    public void init() {

    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);
        value.getAnimation().update();

        float checkboxWidth = 8;
        float checkboxSize = 8;

        float valueHeight = drawName(matrix, size.x - checkboxWidth - margin);

        value.getAnimation().run(value.getValue() ? 1 : 0, 0.25, Easings.SINE_OUT, true);
        int backColorDark = ColorUtil.overCol(ColorUtil.multAlpha(darkClientColor(), 0.2f), ColorUtil.multAlpha(clientColor(), 0.2f), value.getAnimation().get());
        int checkIconColor = ColorUtil.overCol(ColorUtil.multAlpha(iconColor(), 0), iconColor(), value.getAnimation().get());
        RenderUtil.Rounded.smooth(matrix, position.x + size.x - checkboxWidth, position.y + margin - 1, checkboxWidth, checkboxSize, backColorDark, Round.of(2));
        Fonts.icon.draw(matrix, "N", position.x + size.x - checkboxWidth + (value.getAnimation().get() * (checkboxWidth - checkboxSize)) + 1, position.y + margin + 0.5F, checkIconColor, 6);

        size.y = margin + valueHeight + margin;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float checkboxWidth = 8;
        float checkboxSize = 8;

        if (isLClick(button) && isHover(mouseX, mouseY, position.x + size.x - checkboxWidth, position.y + margin - 1, checkboxWidth, checkboxSize)) {
            value.set(!value.getValue());
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return false;
    }

    @Override
    public void onClose() {

    }
}