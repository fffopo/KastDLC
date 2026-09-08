package fun.mentalium.ui.clickgui.components.setting.impl;

import fun.mentalium.constructor.modules.impl.render.HUD;
import fun.mentalium.utils.render.color.ColorUtil;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.modules.settings.impl.StringSetting;
import fun.mentalium.ui.clickgui.components.setting.SettingComponent;
import fun.mentalium.utils.keyboard.Keyboard;
import fun.mentalium.utils.render.draw.RenderUtil;
import fun.mentalium.utils.render.draw.Round;
import fun.mentalium.utils.render.text.TextAlign;
import fun.mentalium.utils.render.text.TextBox;
import org.joml.Vector2f;

public class StringSettingComponent extends SettingComponent {
    private final StringSetting value;
    private final StringSetting localValue;
    public final TextBox textBox;

    public StringSettingComponent(StringSetting value) {
        super(value);
        this.value = this.localValue = value;
        textBox = new TextBox(new Vector2f(), font(), fontSize(), HUD.getInstance().textColor(), TextAlign.LEFT, "Введите текст...", 0, false, value.isOnlyNumber());
        textBox.setText(localValue.getValue());
        textBox.setCursor(localValue.getValue().length());
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
        textBox.setText(localValue.getValue());
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        super.render(matrix, mouseX, mouseY, partialTicks);
        float valueHeight = drawName(matrix, size.x);
        RenderUtil.Rounded.smooth(matrix, position.x, position.y + margin + valueHeight + margin, size.x, 12, ColorUtil.multAlpha(darkClientColor(), 0.2f), Round.of(2));

        textBox.setColor(textColor());
        textBox.getPosition().set(position.x + margin, position.y + margin + valueHeight + margin + 3.25f);
        textBox.setWidth(size.x - (margin * 2));
        textBox.draw(matrix);
        localValue.set(textBox.getText());

        size.y = margin + valueHeight + margin + margin + margin + margin + 3;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isLClick(button))
            textBox.mouse(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        textBox.keyPressed(keyCode);

        if (Keyboard.KEY_ENTER.isKey(keyCode)) {
            value.set(localValue.getValue());
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        textBox.charTyped(codePoint);
        return false;
    }

    @Override
    public void onClose() {
        textBox.selected = false;
        value.set(localValue.getValue());
    }
}