package fun.mentalium.ui.clickgui.components.setting.impl;

import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.modules.settings.impl.BooleanSetting;
import fun.mentalium.constructor.modules.settings.impl.MultiBooleanSetting;
import fun.mentalium.ui.clickgui.components.setting.SettingComponent;
import fun.mentalium.utils.animation.util.Easings;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.draw.RenderUtil;
import fun.mentalium.utils.render.draw.Round;
import fun.mentalium.utils.other.SoundUtil;

public class MultiBooleanSettingComponent extends SettingComponent {
    private final MultiBooleanSetting value;
    private String lastChangedSetting = "";

    public MultiBooleanSettingComponent(MultiBooleanSetting value) {
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
        float valueHeight = drawName(matrix, size.x);

        float wOffset = 0;
        float hOffset = 0;
        float append = 3;
        float out = 1;

        for (BooleanSetting setting : value.getValues()) {
            if (!setting.getVisible().get()) continue;
            setting.getAnimation().update();
            setting.getAnimation().run(value.getValue(setting.getName()) ? 1 : 0, 0.25, Easings.LINEAR, true);
            float tOffset = font.getWidth(setting.getName(), fontSize) + append + 2;
            if (wOffset + tOffset >= size.x - (margin * 2)) {
                wOffset = 0;
                hOffset += fontSize + append;
            }
            RenderUtil.Rounded.smooth(matrix, position.x + wOffset - out, position.y + margin + valueHeight + margin + hOffset + margin / 2F, font.getWidth(setting.getName(), fontSize) + (out * 2) + 2, fontSize + (out * 2), ColorUtil.overCol(ColorUtil.multAlpha(darkClientColor(), 0.2f), ColorUtil.multAlpha(clientColor(), 0.2f), setting.getAnimation().get()), Round.of(2));
            font.draw(matrix, setting.getName(), position.x + wOffset + 1.25f, position.y + margin + valueHeight + margin + hOffset + margin / 2F + out, (ColorUtil.overCol(darkTextColor(), textColor(), setting.getAnimation().get())), fontSize);
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

        for (BooleanSetting setting : value.getValues()) {
            if (!setting.getVisible().get()) continue;
            float tOffset = font.getWidth(setting.getName(), fontSize) + append + 2;
            if (wOffset + tOffset >= size.x - (margin * 2)) {
                wOffset = 0;
                hOffset += fontSize + append;
            }
            if (isLClick(button) && isHover(mouseX, mouseY, position.x + wOffset - out, position.y + margin + valueHeight() + margin + hOffset + margin / 2F, font.getWidth(setting.getName(), fontSize) + (out * 2), fontSize + (out * 2))) {
                boolean newValue = !setting.getValue();
                value.get(setting.getName()).set(newValue);
                if (!setting.getName().equals(lastChangedSetting)) {
                    SoundUtil.playSound("modeselect.wav");
                    lastChangedSetting = setting.getName();
                }
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
        lastChangedSetting = "";
    }
}