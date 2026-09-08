package fun.mentalium.ui.clickgui.components.module;

import fun.mentalium.constructor.mods.fastrandom.FastRandom;
import fun.mentalium.constructor.mods.taskript.Script;
import fun.mentalium.constructor.modules.impl.render.HUD;
import fun.mentalium.utils.render.draw.RectUtil;
import fun.mentalium.utils.render.draw.Round;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.modules.Module;
import fun.mentalium.constructor.modules.settings.Setting;
import fun.mentalium.constructor.modules.settings.impl.*;
import fun.mentalium.ui.clickgui.ClickGUIScreen;
import fun.mentalium.ui.clickgui.components.WindowComponent;
import fun.mentalium.ui.clickgui.components.setting.SettingComponent;
import fun.mentalium.ui.clickgui.components.setting.impl.*;
import fun.mentalium.utils.animation.util.Easings;
import fun.mentalium.utils.keyboard.Keyboard;
import fun.mentalium.utils.math.Mathf;
import fun.mentalium.utils.other.SoundUtil;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.draw.RenderUtil;
import fun.mentalium.utils.render.draw.StencilUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
public class ModuleComponent extends WindowComponent {
    private final Module module;
    public List<SettingComponent> settingComponents = new ArrayList<>();
    private boolean expanded = false;
    private float settingHeight = 0;
    private final float margin = 5;
    private final Script script = new Script();
    private final Random random = new FastRandom();
    private boolean wasHovered = false;

    @Getter
    private boolean isHovered = false;

    public ModuleComponent(Module module, ClickGUIScreen clickGui) {
        this.module = module;
        for (Setting<?> setting : module.getSettings()) {
            if (setting instanceof BindSetting value) {
                settingComponents.add(new BindSettingComponent(value));
            }
            if (setting instanceof BooleanSetting value) {
                settingComponents.add(new BooleanSettingComponent(value));
            }
            if (setting instanceof ColorSetting value) {
                settingComponents.add(new ColorSettingComponent(value));
            }
            if (setting instanceof ListSetting<?> value) {
                settingComponents.add(new ListSettingComponent(value));
            }
            if (setting instanceof ModeSetting value) {
                settingComponents.add(new ModeSettingComponent(value));
            }
            if (setting instanceof MultiBooleanSetting value) {
                settingComponents.add(new MultiBooleanSettingComponent(value));
            }
            if (setting instanceof SliderSetting value) {
                settingComponents.add(new SliderSettingComponent(value));
            }
            if (setting instanceof StringSetting value) {
                settingComponents.add(new StringSettingComponent(value));
            }
        }
        size.set(clickGui.categoryWidth(), clickGui.categoryHeight());
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        settingComponents.forEach(component -> component.resize(minecraft, width, height));
    }

    @Override
    public void init() {
        settingComponents.forEach(SettingComponent::init);
        if (expanded && panel().getExpandedModule() != this) {
            expandAnimation.set(0F);
            expanded = false;
        }
        wasHovered = false;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        script.update();
        expandAnimation.update();
        hoverAnimation.update();
        HUD hud = HUD.getInstance();

        if (expanded && panel().getExpandedModule() != this) {
            expandAnimation.run(0, 0.25, Easings.QUART_OUT, true).onFinished(() -> expanded = false);
        }

        float append = 2F;
        float angle = (float) Mathf.clamp(0F, 1F, ((Math.sin(System.currentTimeMillis() / 200D) + 1F) / 2F));

        boolean isHover = isHover(mouseX, mouseY, position.x + append, position.y + append, size.x - (append * 2), size.y - (append * 2));

        if (isHover && !wasHovered) {
            SoundUtil.playSound("hovered.wav", 0.1f);
        }
        wasHovered = isHover;
        isHovered = isHover;

        hoverAnimation.run(binding && !script.isFinished() ? 1.5 : (isHover ? 1 : expanded ? angle : 0), 0.25, binding && !script.isFinished() ? Easings.BACK_OUT : Easings.QUAD_OUT, true);

        int textColor = module.isEnabled() ? hud.textColor() : ColorUtil.multAlpha(hud.textColor(), 0.5f);

        if (!module.getSettings().isEmpty()) {
            font.draw(matrix, "...", position.x + size.x - 14, position.y + size.y / 2 - 13 / 2, textColor, 8);
        }

        int moduleColor = module.isEnabled() ? hud.outlineColor() : ColorUtil.multAlpha(hud.outlineColor(), 0.5f);

        RenderUtil.Rounded.roundedOutline(matrix, position.x + append, position.y + append, size.x - (append * 2), (size.y - (append * 2)), 0.7f, moduleColor, Round.of(3));

        boolean noneMatch = panel().getCategoryComponents().stream().noneMatch(category -> category.getModuleComponents().stream().anyMatch(module -> module.isBinding() || module.settingComponents.stream().anyMatch(settingComponent -> settingComponent instanceof StringSettingComponent component && component.textBox.selected)));

        String moduleText = binding ? "Клавиша " + Keyboard.keyName(module.getKey()) : (Keyboard.KEY_RIGHT_CONTROL.isKeyDown() && noneMatch) ? Keyboard.keyName(module.getKey()) + " | " + module.getName() : module.getName();
        font.draw(matrix, moduleText, position.x + 7, position.y + (size.y / 2F) - (moduleFontSize / 2F) + 0.5f, textColor, moduleFontSize);

        if (hoverAnimation.getValue() != 0) {
            RenderUtil.Rounded.roundedOutline(matrix, position.x + append, position.y + append, size.x - (append * 2), (size.y - (append * 2)), 0.7f, ColorUtil.multAlpha(hud.outlineColor(), hoverAnimation().get() * 1), Round.of(3));
            if (!module.getSettings().isEmpty()) {
                font.draw(matrix, "...", position.x + size.x - 14, position.y + size.y / 2 - 13 / 2, ColorUtil.multAlpha(textColor, hoverAnimation().get() * 1), 8);
            }
            font.draw(matrix, moduleText, position.x + 7, position.y + (size.y / 2F) - (moduleFontSize / 2F) + 0.5f, ColorUtil.multAlpha(textColor, hoverAnimation().get() * 1), moduleFontSize);
        }

        StencilUtil.enable();
        RectUtil.drawRect(matrix, position.x, position.y + size.y, size.x, expandAnimation.getValue() * settingHeight, ColorUtil.getColor(128, 128));
        StencilUtil.read(1);

        settingHeight = 0;
        if ((expanded || !expandAnimation.isFinished()) && !settingComponents.isEmpty()) {
            float offset = 0;
            for (SettingComponent component : settingComponents) {
                if (!component.value().getVisible().get()) continue;
                component.position().set(position.x + margin + 2, position.y + size.y + offset);
                component.size().x = size.x - (margin * 2) - 4;
                component.render(matrix, mouseX, mouseY, partialTicks);
                offset += component.size().y;
            }
            settingHeight = offset;
        }
        StencilUtil.disable();
    }

    @Getter
    @Setter
    private boolean binding = false;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float append = 2F;
        if (isHover(mouseX, mouseY, position.x + append, position.y + append, size.x - (append * 2), size.y - (append * 2))) {
            if (isMClick(button)) {
                panel().getCategoryComponents().stream().filter(component -> component.getCategory().equals(module.getCategory())).flatMap(component -> component.getModuleComponents().stream()).filter(module -> module != this).forEach(module -> module.setBinding(false));
                setBinding(!isBinding());
            }
            if (!isBinding() && isLClick(button)) module.toggle();
            if (isRClick(button) && !settingComponents.isEmpty()) {
                expanded = !expanded;
                expandAnimation.run(expanded ? 1 : 0, 0.25, Easings.QUART_OUT);
                SoundUtil.playSound(expanded ? "moduleopen.wav" : "moduleclose.wav");
                if (expanded) panel().setExpandedModule(this);
            }
        }

        boolean valid = button != Keyboard.MOUSE_MIDDLE.getKey() && button != Keyboard.MOUSE_RIGHT.getKey() && button != Keyboard.MOUSE_LEFT.getKey();

        if (isBinding()) {
            if (valid && script.isFinished()) {
                module.setKey(button);
                stopBinding();
            }
        } else {
            setBinding(false);
        }

        if (expanded && !settingComponents.isEmpty() && expandAnimation.get() == 1.0F && expandAnimation.isFinished()) {
            settingComponents.stream().filter(component -> component.value().getVisible().get()).forEach(component -> component.mouseClicked(mouseX, mouseY, button));
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!settingComponents.isEmpty()) {
            settingComponents.stream().filter(component -> component.value().getVisible().get()).forEach(component -> component.mouseReleased(mouseX, mouseY, button));
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (isBinding()) {
            if (keyCode == Keyboard.KEY_ESCAPE.getKey() || keyCode == Keyboard.KEY_DELETE.getKey()) {
                module.setKey(Keyboard.KEY_NONE.getKey());
                stopBinding();
                return true;
            }
            if (script.isFinished()) {
                module.setKey(keyCode);
                stopBinding();
            }
        }
        if (expanded && !settingComponents.isEmpty()) {
            settingComponents.stream().filter(component -> component.value().getVisible().get()).forEach(component -> component.keyPressed(keyCode, scanCode, modifiers));
        }
        return false;
    }

    private void stopBinding() {
        SoundUtil.playSound("guiclose.wav");
        script.cleanup().addStep(500, () -> {
            SoundUtil.playSound("guiopen.wav");
            setBinding(false);
        });
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!expanded || settingComponents.isEmpty()) return false;
        for (SettingComponent component : settingComponents) {
            if (component.value().getVisible().get()) {
                component.keyReleased(keyCode, scanCode, modifiers);
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!expanded || settingComponents.isEmpty()) return false;
        for (SettingComponent component : settingComponents) {
            if (component.value().getVisible().get()) {
                component.charTyped(codePoint, modifiers);
            }
        }
        return false;
    }

    @Override
    public void onClose() {
        setBinding(false);
        settingComponents.forEach(SettingComponent::onClose);
    }
}