package fun.mentalium.ui.clickgui.components.setting.impl;

import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.modules.settings.impl.BindSetting;
import fun.mentalium.ui.clickgui.components.setting.SettingComponent;
import fun.mentalium.utils.animation.Animation;
import fun.mentalium.utils.animation.util.Easings;
import fun.mentalium.utils.keyboard.Keyboard;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.draw.RenderUtil;
import fun.mentalium.utils.render.draw.Round;
import fun.mentalium.utils.render.font.Font;
import fun.mentalium.utils.render.font.Fonts;
import fun.mentalium.utils.other.SoundUtil;

public class BindSettingComponent extends SettingComponent {
    private final BindSetting value;
    private final Font localFont = Fonts.sf_medium;
    private final Animation bindAnimation = new Animation();
    private boolean binding = false;

    public BindSettingComponent(BindSetting value) {
        super(value);
        this.value = value;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
        binding = false;
        bindAnimation.set(0);
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);

        value.getAnimation().update();
        bindAnimation.update();

        String valueName = (binding ? "Жду..." : Keyboard.keyName(value.getValue()));
        float valueWidth = localFont.getWidth(valueName, fontSize) + 2;
        float out = 1F;

        float valueHeight = drawName(matrix, size.x - valueWidth - margin);

        value.getAnimation().run(binding ? 1 : 0, 0.25, Easings.QUAD_OUT);
        bindAnimation.run(binding ? 1 : 0, 0.25, Easings.SINE_IN_OUT, true);

        int baseRectColor = ColorUtil.multAlpha(darkClientColor(), 0.2f);
        int activeRectColor = ColorUtil.multAlpha(clientColor(), 0.2f);
        int rectColor = ColorUtil.overCol(baseRectColor, activeRectColor, value.getAnimation().get());

        int baseTextColor = darkTextColor();
        int activeTextColor = textColor();
        int textColor = ColorUtil.overCol(baseTextColor, activeTextColor, value.getAnimation().get());

        RenderUtil.Rounded.smooth(matrix, position.x + size.x - valueWidth - out, position.y + margin - out, valueWidth + (out * 2), fontSize + (out * 2), rectColor, Round.of(2));

        localFont.drawRight(matrix, valueName, position.x + size.x - 0.75f, position.y + margin, textColor, fontSize);

        size.y = margin + valueHeight + margin;
    }

    private boolean isHovered(int mouseX, int mouseY) {
        String valueName = Keyboard.keyName(value.getValue());
        float valueWidth = localFont.getWidth(valueName, fontSize) + 2;
        float out = 1;
        return isHover(mouseX, mouseY, position.x + size.x - valueWidth - out, position.y + margin, valueWidth + (out * 2), fontSize + (out * 2));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean currentHover = isHovered((int)mouseX, (int)mouseY);
        boolean valid = button != Keyboard.MOUSE_RIGHT.getKey() && button != Keyboard.MOUSE_LEFT.getKey();

        if (currentHover && !valid) {
            if (!binding) {
                SoundUtil.playSound("modeselect.wav", 0.7f);
            }
            binding = !binding;
            return true;
        } else if (binding) {
            if (valid) {
                value.set(button);
                SoundUtil.playSound("guiclose.wav", 0.7f);
            }
            binding = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (binding) {
            boolean valid = keyCode != Keyboard.KEY_DELETE.getKey() && keyCode != Keyboard.KEY_ESCAPE.getKey() && keyCode != Keyboard.KEY_SPACE.getKey() && keyCode != value.getParent().getKey();

            if (valid) {
                value.set(keyCode);
                SoundUtil.playSound("guiopen.wav", 0.7f);
            } else {
                value.set(Keyboard.KEY_NONE.getKey());
                SoundUtil.playSound("guiclose.wav", 0.7f);
            }
            binding = false;
            return true;
        }
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
        binding = false;
        bindAnimation.set(0);
    }
}