package fun.mentalium.ui.clickgui.components.setting.impl;

import fun.mentalium.utils.animation.Animation;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.modules.settings.impl.ModeSetting;
import fun.mentalium.ui.clickgui.components.setting.SettingComponent;
import fun.mentalium.utils.animation.util.Easings;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.draw.RenderUtil;
import fun.mentalium.utils.render.draw.Round;
import fun.mentalium.utils.other.SoundUtil;

public class ModeSettingComponent extends SettingComponent {
    private final ModeSetting value;
    private String lastSelectedMode = "";
    private final Animation[] modeAnimations;

    public ModeSettingComponent(ModeSetting value) {
        super(value);
        this.value = value;
        this.lastSelectedMode = value.getValue();
        this.modeAnimations = new Animation[value.values.size()];
        for (int i = 0; i < modeAnimations.length; i++) {
            modeAnimations[i] = new Animation();
            modeAnimations[i].set(value.values.get(i).equals(value.getValue()) ? 1.0 : 0.0);
        }
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
        for (int i = 0; i < modeAnimations.length; i++) {
            modeAnimations[i].set(value.values.get(i).equals(value.getValue()) ? 1.0 : 0.0);
        }
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);
        float valueHeight = drawName(matrix, size.x);

        float wOffset = 0;
        float hOffset = 0;
        float append = 3;
        float out = 1;

        for (int i = 0; i < value.values.size(); i++) {
            String setting = value.values.get(i);
            modeAnimations[i].update();
            modeAnimations[i].run(setting.equals(value.getValue()) ? 1.0 : 0.0, 0.25, Easings.LINEAR, true);

            float tOffset = font.getWidth(setting, fontSize) + append + 2;
            if (wOffset + tOffset >= size.x - (margin * 2)) {
                wOffset = 0;
                hOffset += fontSize + append;
            }

            RenderUtil.Rounded.smooth(matrix, position.x + wOffset - out, position.y + margin + valueHeight + margin + hOffset + margin / 2F, font.getWidth(setting, fontSize) + (out * 2) + 2, fontSize + (out * 2), ColorUtil.overCol(ColorUtil.multAlpha(darkClientColor(), 0.2f), ColorUtil.multAlpha(clientColor(), 0.2f), modeAnimations[i].get()), Round.of(2));
            font.draw(matrix, setting, position.x + wOffset + 1.25f, position.y + margin + valueHeight + margin + hOffset + margin / 2F + out, ColorUtil.overCol(darkTextColor(), textColor(), modeAnimations[i].get()), fontSize);

            wOffset += tOffset;
        }

        size.y = margin + valueHeight + margin + hOffset + fontSize + (margin * 2F) + margin;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float wOffset = 0;
        float hOffset = 0;
        float append = 3;
        float out = 1;

        for (int i = 0; i < value.values.size(); i++) {
            String setting = value.values.get(i);
            float tOffset = font.getWidth(setting, fontSize) + append + 2;
            if (wOffset + tOffset >= size.x - (margin * 2)) {
                wOffset = 0;
                hOffset += fontSize + append;
            }
            if (isLClick(button) && isHover(mouseX, mouseY, position.x + wOffset - out, position.y + margin + valueHeight() + margin + hOffset + margin / 2F, font.getWidth(setting, fontSize) + (out * 2), fontSize + (out * 2))) {

                if (!setting.equals(lastSelectedMode)) {
                    SoundUtil.playSound("modeselect.wav");
                    lastSelectedMode = setting;
                }
                value.set(setting);
                return true;
            }
            wOffset += tOffset;
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
        lastSelectedMode = value.getValue();
        for (int i = 0; i < modeAnimations.length; i++) {
            modeAnimations[i].set(value.values.get(i).equals(value.getValue()) ? 1.0 : 0.0);
        }
    }
}