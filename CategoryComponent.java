package fun.mentalium.ui.clickgui.components.category;

import fun.mentalium.Mentalium;
import fun.mentalium.constructor.modules.impl.render.HUD;
import fun.mentalium.utils.animation.Animation;
import fun.mentalium.utils.animation.util.Easings;
import fun.mentalium.utils.render.draw.RectUtil;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.modules.Category;
import fun.mentalium.constructor.modules.Module;
import fun.mentalium.ui.clickgui.ClickGUIScreen;
import fun.mentalium.ui.clickgui.components.WindowComponent;
import fun.mentalium.ui.clickgui.components.module.ModuleComponent;
import fun.mentalium.utils.math.Mathf;
import fun.mentalium.utils.other.Instance;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.font.Fonts;
import fun.mentalium.utils.render.scroll.ScrollUtil;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class CategoryComponent extends WindowComponent {
    private final Category category;
    private final List<ModuleComponent> moduleComponents = new ArrayList<>();
    private final ScrollUtil categoryScroll = new ScrollUtil();
    private boolean isHovered = false;
    private final float maxVisibleHeight = 240;
    private boolean isInitialized = false;
    private float totalContentHeight = 0;
    private final float visibleContentHeight = 240;
    private float scrollOffset = 0;
    private boolean needsScroll = false;
    private final Animation scrollAnimation = new Animation();
    private float targetScrollOffset = 0;
    private float headerPadLeft = 0;
    private float headerPadRight = 0;

    public CategoryComponent(Category category, ClickGUIScreen clickGui) {
        this.category = category;
        List<Module> sortedModules = new ArrayList<>(Instance.get(category));
        sortedModules.sort(Comparator.comparing((Module mod) -> Fonts.sf_medium.getWidth(mod.getName(), moduleFontSize)).reversed().thenComparing(Module::getName));
        this.moduleComponents.addAll(sortedModules.stream().map(mod -> new ModuleComponent(mod, clickGui)).toList());
        size.set(clickGui.categoryWidth(), clickGui.categoryHeight());

        categoryScroll.setEnabled(true);
        categoryScroll.setAutoReset(false);
        categoryScroll.setWheel(0);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
    }

    @Override
    public void init() {
        if (!isInitialized) {
            moduleComponents.forEach(ModuleComponent::init);
            isInitialized = true;
            calculateContentHeight();
        }
    }
    private void updateScrollMax() {
        float maxOffset = Math.max(0, totalContentHeight - visibleContentHeight);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxOffset));
    }

    public void calculateContentHeight() {
        totalContentHeight = 0;
        for (ModuleComponent module : moduleComponents) {
            if (Mentalium.getInstance().clickGui().searchCheck(module.getModule().getName())) continue;
            totalContentHeight += module.size().y + (module.expandAnimation().getValue() * module.getSettingHeight());
        }
        needsScroll = totalContentHeight > visibleContentHeight;
        targetScrollOffset = Math.min(targetScrollOffset, Math.max(0, totalContentHeight - visibleContentHeight));
        scrollOffset = Math.min(scrollOffset, Math.max(0, totalContentHeight - visibleContentHeight));
    }

    public void setHeaderPad(float left, float right) {
        this.headerPadLeft = left;
        this.headerPadRight = right;
    }

    public void adjustScroll(float amount) {
        if (!needsScroll) return;
        float newOffset = targetScrollOffset - amount;
        targetScrollOffset = Mathf.clamp(newOffset, 0, Math.max(0, totalContentHeight - visibleContentHeight));
    }

    public boolean isExactlyHovered(int mouseX, int mouseY) {
        float headerEndY = position.y + size.y;
        float contentEndY = headerEndY + visibleContentHeight;
        return mouseX >= position.x && mouseX <= position.x + size.x && mouseY >= position.y && mouseY <= contentEndY;
    }

    public float getMaxVisibleHeight() {
        return maxVisibleHeight;
    }


    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (!isInitialized) {
            init();
        }

        if (needsScroll) {
            scrollAnimation.update();
            scrollAnimation.run(targetScrollOffset, 0.25, Easings.QUAD_OUT, true);
            scrollOffset = (float) scrollAnimation.getValue();
        } else {
            scrollOffset = 0;
            targetScrollOffset = 0;
        }

        HUD hud = HUD.getInstance();
        calculateContentHeight();
        updateScrollMax();
        categoryScroll.update();
        isHovered = isExactlyHovered(mouseX, mouseY);

        float headerHeight = size.y;
        float contentAreaY = position.y + headerHeight;

        float unifiedHeight = headerHeight + visibleContentHeight;
        hud.drawClientRect(matrix, position.x - headerPadLeft, position.y, size.x + headerPadLeft + headerPadRight, unifiedHeight, alphaPC());
        boolean overlayView = mc.currentScreen instanceof fun.mentalium.ui.clickgui.ClickGUIScreen scr && scr.overlayOnly();
        String title = overlayView ? "Overlay" : category.getName();
        String icon = overlayView ? ">" : category.getIcon();
        Fonts.icon.draw(matrix, icon, position.x + 95, position.y + 6.5f, ColorUtil.multAlpha(hud.iconColor(), alphaPC()), categoryFontSize);
        font.draw(matrix, title, position.x + 7, position.y + 6.25f, ColorUtil.multAlpha(hud.textColor(), alphaPC()), categoryFontSize);

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        int scaleFactor = (int) mc.getMainWindow().getScaleFactor();
        float clipX = position.x;
        float clipY = contentAreaY;
        float clipWidth = size.x;
        float clipHeight = visibleContentHeight;
        int glScissorX = (int) (clipX * scaleFactor);
        int glScissorY = (int) (mc.getMainWindow().getHeight() - (clipY + clipHeight) * scaleFactor);
        int glScissorWidth = (int) (clipWidth * scaleFactor);
        int glScissorHeight = (int) (clipHeight * scaleFactor);
        GL11.glScissor(glScissorX, Math.max(0, glScissorY), glScissorWidth, glScissorHeight);

        float renderY = contentAreaY - scrollOffset;
        for (ModuleComponent module : moduleComponents) {
            if (Mentalium.getInstance().clickGui().searchCheck(module.getModule().getName())) continue;

            float moduleHeight = module.size().y + (float) (module.expandAnimation().getValue() * module.getSettingHeight());

            if (renderY + moduleHeight > contentAreaY && renderY < contentAreaY + visibleContentHeight) {
                module.position().set(position.x + 4, renderY);
                module.size().x = size.x - 8;
                module.render(matrix, mouseX, mouseY, partialTicks);
            }
            renderY += moduleHeight;
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        if (needsScroll) {
            float scrollRatio = scrollOffset / (totalContentHeight - visibleContentHeight);
            float scrollbarHeight = Math.max(20, (visibleContentHeight / totalContentHeight) * visibleContentHeight);
            float scrollbarY = contentAreaY + (visibleContentHeight - scrollbarHeight) * scrollRatio;

            RectUtil.drawRect(matrix, position.x + size.x - 3, scrollbarY + 2, 0.5f, scrollbarHeight - 4, ColorUtil.multAlpha(hud.clientColor(), 0.5f));
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isExactlyHovered((int)mouseX, (int)mouseY)) {
            return false;
        }

        float contentStartY = position.y + size.y;
        float contentEndY = contentStartY + visibleContentHeight;

        if (mouseY >= contentStartY && mouseY <= contentEndY) {
            float moduleY = contentStartY - scrollOffset;

            for (ModuleComponent module : moduleComponents) {
                if (Mentalium.getInstance().clickGui().searchCheck(module.getModule().getName())) continue;

                float moduleHeight = module.size().y;
                if (!module.isExpanded()) {
                    if (mouseY >= moduleY && mouseY <= moduleY + moduleHeight) {
                        module.position().set(position.x + 4, moduleY);
                        return module.mouseClicked(mouseX, mouseY, button);
                    }
                } else {
                    float expandedHeight = moduleHeight + module.getSettingHeight();
                    if (mouseY >= moduleY && mouseY <= moduleY + expandedHeight) {
                        module.position().set(position.x + 4, moduleY);
                        return module.mouseClicked(mouseX, mouseY, button);
                    }
                }
                moduleY += module.isExpanded() ? moduleHeight + module.getSettingHeight() : moduleHeight;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {

        boolean handled = false;
        for (ModuleComponent component : moduleComponents) {

            if (component.mouseReleased(mouseX, mouseY, button)) {
                handled = true;
            }
        }

        return handled || isExactlyHovered((int)mouseX, (int)mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean handled = false;
        for (ModuleComponent component : moduleComponents) {
            if (component.keyPressed(keyCode, scanCode, modifiers)) {
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        boolean handled = false;
        for (ModuleComponent component : moduleComponents) {
            if (component.keyReleased(keyCode, scanCode, modifiers)) {
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        boolean handled = false;
        for (ModuleComponent component : moduleComponents) {
            if (component.charTyped(codePoint, modifiers)) {
                handled = true;
            }
        }
        return handled;
    }

    @Override
    public void onClose() {
        moduleComponents.forEach(ModuleComponent::onClose);
        scrollOffset = 0;
        isInitialized = false;
    }
}