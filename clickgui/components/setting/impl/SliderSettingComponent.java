package fun.mentalium.ui.clickgui.components.setting.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.modules.settings.impl.SliderSetting;
import fun.mentalium.ui.clickgui.components.setting.SettingComponent;
import fun.mentalium.utils.animation.util.Easings;
import fun.mentalium.utils.math.Mathf;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.draw.RenderUtil;
import fun.mentalium.utils.render.draw.Round;
import fun.mentalium.utils.other.SoundUtil;

public class SliderSettingComponent extends SettingComponent {
    private final SliderSetting value;
    private final SliderSetting localValue;
    private float lastValue;

    public SliderSettingComponent(SliderSetting value) {
        super(value);
        this.value = this.localValue = value;
        this.lastValue = value.getValue();
    }

    private boolean drag;

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

        float sliderHeight = 2;

        String currentValue = String.valueOf(localValue.getValue());

        float valueHeight = drawName(matrix, size.x);

        value.getAnimation().run(Mathf.step(size.x * (localValue.getValue() - value.min) / (value.max - value.min), value.increment / size.x), 0.25, Easings.CUBIC_OUT, true);

        int textColorDark = ColorUtil.multDark(textColor(), 0.5F);

        RenderUtil.Rounded.smooth(matrix, position.x, position.y + margin + valueHeight + margin, size.x, sliderHeight, ColorUtil.multAlpha(darkClientColor(), 0.2f), Round.of(1));
        RenderUtil.Rounded.smooth(matrix, position.x, position.y + margin + valueHeight + margin, (float) value.getAnimation().getValue(), sliderHeight, darkClientColor(), darkClientColor(), clientColor(), clientColor(), Round.of(1));

        if (drag) {
            float newValue = (float) MathHelper.clamp(Mathf.step((mouseX - position.x) / size.x * (value.max - value.min) + value.min, value.increment), value.min, value.max);
            localValue.set(newValue);

            if (newValue != lastValue) {
                SoundUtil.playSound("slidermove.wav");
                lastValue = newValue;
            }
        }

        font.draw(matrix, String.valueOf(value.min), position.x, position.y + margin + valueHeight + margin + sliderHeight + margin, textColorDark, fontSize);
        font.drawCenter(matrix, currentValue, position.x + size.x / 2F, position.y + margin + valueHeight + margin + sliderHeight + margin, textColor(), fontSize);
        font.drawRight(matrix, String.valueOf(value.max), position.x + size.x, position.y + margin + valueHeight + margin + sliderHeight + margin, textColorDark, fontSize);

        size.y = margin + valueHeight + margin + sliderHeight + margin + fontSize + margin;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isHover(mouseX, mouseY, position.x, position.y + margin, size.x, size.y - (margin * 2F))) {
            drag = true;
            SoundUtil.playSound("slidermove.wav");
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (drag) {
            SoundUtil.playSound("slidermove.wav");
        }
        drag = false;
        value.set(localValue.getValue());
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
        drag = false;
        value.set(localValue.getValue());
    }
}