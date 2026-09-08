package fun.mentalium.ui.clickgui.components.setting.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Namespaced;
import net.minecraft.util.ResourceLocation;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.api.interfaces.IRender;
import fun.mentalium.constructor.modules.settings.impl.ColorSetting;
import fun.mentalium.ui.clickgui.components.setting.SettingComponent;
import fun.mentalium.utils.math.Mathf;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.draw.RenderUtil;
import fun.mentalium.utils.render.draw.Round;

import java.awt.*;

public class ColorSettingComponent extends SettingComponent implements IRender {
    private final ColorSetting value;
    private final ResourceLocation hueTexture = new Namespaced("textures/ui/clickgui/settings/colorsetting/hue.png");
    private boolean huePickerDown;
    private boolean saturationDown;
    private boolean lightnessDown;
    private float huePointer = 0;
    private float saturationPointer = 0;
    private float lightnessPointer = 0;
    private final float sliderHeight = 2;
    private int hueSelectorColor;
    private float hue;
    private float saturation;
    private float lightness;

    public ColorSettingComponent(ColorSetting value) {
        super(value);
        this.value = value;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {

    }

    @Override
    public void init() {
        Color color = new Color(value.getValue());
        float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);

        huePointer = hsb[0] * size.x;
        saturationPointer = hsb[1] * size.x;
        lightnessPointer = hsb[2] * size.x;

        hue = Mathf.clamp(0.01F, 0.99F, hsb[0]);
        saturation = Mathf.clamp(0.01F, 0.99F, hsb[1]);
        lightness = Mathf.clamp(0.01F, 0.99F, hsb[2]);
        hueSelectorColor = Color.getHSBColor(hue, 1F, 1F).hashCode();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);

        float valueWidth = 8;

        float valueHeight = drawName(matrix, size.x - valueWidth - margin * 2F);

        RenderUtil.Rounded.smooth(matrix, position.x + size.x - valueWidth, position.y + margin - 1, valueWidth, valueWidth, ColorUtil.replAlpha(value.getValue(), alpha()), Round.of(2));

        float xPicker = position.x;
        float yPicker = position.y + margin + valueHeight + margin;
        int black = ColorUtil.getColor(0, alphaPC());
        int white = ColorUtil.getColor(255, alphaPC());
        int hueColor = ColorUtil.replAlpha(hueSelectorColor, alphaPC());

        Round round = Round.of(1);

        RenderUtil.bindTexture(hueTexture);
        RenderUtil.Texture.smooth(matrix, xPicker, yPicker + margin, size.x, sliderHeight, alphaPC(), round);
        RenderUtil.Rounded.smooth(matrix, xPicker, yPicker + margin + sliderHeight + margin, size.x, sliderHeight, white, white, hueColor, hueColor, round);
        RenderUtil.Rounded.smooth(matrix, xPicker, yPicker + margin + sliderHeight + margin + sliderHeight + margin, size.x, sliderHeight, black, black, hueColor, hueColor, round);

        if (huePickerDown) {
            huePointer = (mouseX - xPicker);
            huePointer = Mathf.clamp(0F, size.x, huePointer);
            hue = Mathf.clamp(0.01F, 0.99F, ((huePointer) / (size.x)));
            hueSelectorColor = Color.getHSBColor(hue, 1F, 1F).hashCode();
        }
        if (saturationDown) {
            saturationPointer = (mouseX - xPicker);
            saturationPointer = Mathf.clamp(0F, size.x, saturationPointer);
            saturation = Mathf.clamp(0.01F, 0.99F, ((saturationPointer) / (size.x)));
            hueSelectorColor = Color.getHSBColor(hue, 1F, 1F).hashCode();
        }
        if (lightnessDown) {
            lightnessPointer = (mouseX - xPicker);
            lightnessPointer = Mathf.clamp(0F, size.x, lightnessPointer);
            lightness = Mathf.clamp(0.01F, 0.99F, ((lightnessPointer) / (size.x)));
            hueSelectorColor = Color.getHSBColor(hue, 1F, 1F).hashCode();
        }
        value.set(ColorUtil.replAlpha(Color.getHSBColor(hue, saturation, lightness).hashCode(), 1F));

        huePointer = Mathf.clamp(0F, size.x, huePointer);
        saturationPointer = Mathf.clamp(0F, size.x, saturationPointer);
        lightnessPointer = Mathf.clamp(0F, size.x, lightnessPointer);

        float pickerWidth = 2;
        RenderUtil.Rounded.smooth(matrix, xPicker + huePointer - (1.5F), yPicker + margin - 2, 3, sliderHeight + 4, backgroundColor(), Round.of((pickerWidth / 2F)));
        RenderUtil.Rounded.smooth(matrix, xPicker + huePointer - (pickerWidth / 2F), yPicker + margin - 2, pickerWidth, sliderHeight + 4, getWhite(), Round.of((pickerWidth / 2F)));
        RenderUtil.Rounded.smooth(matrix, xPicker + saturationPointer - (1.5F), yPicker + margin + sliderHeight + margin - 2, 3, sliderHeight + 4, backgroundColor(), Round.of((pickerWidth / 2F)));
        RenderUtil.Rounded.smooth(matrix, xPicker + saturationPointer - (pickerWidth / 2F), yPicker + margin + sliderHeight + margin - 2, pickerWidth, sliderHeight + 4, getWhite(), Round.of((pickerWidth / 2F)));
        RenderUtil.Rounded.smooth(matrix, xPicker + lightnessPointer - (1.5F), yPicker + margin + sliderHeight + margin + sliderHeight + margin - 2, 3, sliderHeight + 4, backgroundColor(), Round.of((pickerWidth / 2F)));
        RenderUtil.Rounded.smooth(matrix, xPicker + lightnessPointer - (pickerWidth / 2F), yPicker + margin + sliderHeight + margin + sliderHeight + margin - 2, pickerWidth, sliderHeight + 4, getWhite(), Round.of((pickerWidth / 2F)));

        size.y = margin + valueHeight + margin + margin + sliderHeight + margin + sliderHeight + margin + sliderHeight + margin;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float xPicker = position.x;
        float yPicker = position.y + margin + valueHeight() + margin;

        if (isLClick(button)) {
            huePickerDown = isHover(mouseX, mouseY, xPicker, yPicker + margin, size.x, sliderHeight);
            saturationDown = isHover(mouseX, mouseY, xPicker, yPicker + margin + sliderHeight + margin, size.x, sliderHeight);
            lightnessDown = isHover(mouseX, mouseY, xPicker, yPicker + margin + sliderHeight + margin + sliderHeight + margin, size.x, sliderHeight);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        huePickerDown = saturationDown = lightnessDown = false;
        value.set(ColorUtil.replAlpha(Color.getHSBColor(hue, saturation, lightness).hashCode(), 1F));
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
        huePickerDown = saturationDown = lightnessDown = false;
        value.set(ColorUtil.replAlpha(Color.getHSBColor(hue, saturation, lightness).hashCode(), 1F));
    }
}