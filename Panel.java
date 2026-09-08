package fun.mentalium.ui.clickgui.components;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.constructor.api.interfaces.IScreen;
import fun.mentalium.constructor.api.interfaces.IWindow;
import fun.mentalium.constructor.modules.Category;
import fun.mentalium.ui.clickgui.ClickGUIScreen;
import fun.mentalium.ui.clickgui.components.category.CategoryComponent;
import fun.mentalium.ui.clickgui.components.module.ModuleComponent;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Panel implements IScreen, IWindow {
    private final ClickGUIScreen clickGui;
    private final List<CategoryComponent> categoryComponents = new ArrayList<>();
    @Setter
    public ModuleComponent expandedModule = null;
    private boolean firstInit = true;
    private boolean initialized = false;

    public Panel(ClickGUIScreen clickGui) {
        this.clickGui = clickGui;
    }

    private void resetPositions(int width, int height) {
        categoryComponents.clear();
        float categoryWidth = clickGui.categoryWidth();
        float spacing = 5;
        float categoryHeight = clickGui.categoryHeight();
        float modulePanelHeight = 276f;
        float totalHeight = categoryHeight + modulePanelHeight;
        float startY = (height - totalHeight) / 2f;

        Category[] cats = Category.values();
        if (clickGui.overlayOnly()) {
            CategoryComponent component = new CategoryComponent(Category.OVERLAY, clickGui);
            float startX = (width - categoryWidth) / 2f;
            component.position().set(startX, startY);
            component.setHeaderPad(0, 0);
            categoryComponents.add(component);
        } else {
            int visibleCount = 0;
            for (Category c : cats) if (c != Category.OVERLAY) visibleCount++;
            float totalCategoriesWidth = visibleCount * categoryWidth;
            float totalWidth = totalCategoriesWidth + (spacing * (visibleCount - 1));
            float startX = (width - totalWidth) / 2f;
            for (Category category : cats) {
                if (category == Category.OVERLAY) continue;
                CategoryComponent component = new CategoryComponent(category, clickGui);
                component.position().set(startX, startY);
                component.setHeaderPad(0, 0);
                categoryComponents.add(component);
                startX += categoryWidth + spacing;
            }
        }
        for (CategoryComponent component : categoryComponents) {
            component.init();
        }

        initialized = true;
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        resetPositions(width, height);
    }

    @Override
    public void init() {
        resetPositions(mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
        firstInit = false;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
        if (!initialized) {
            init();
        }

        float scale = clickGui.scale().get();
        if (scale <= 0.01f) return;

        matrix.push();
        float cx = mc.getMainWindow().getScaledWidth() / 2f;
        float cy = mc.getMainWindow().getScaledHeight() / 2f;
        matrix.translate(cx * (1 - scale), cy * (1 - scale), 0);
        matrix.scale(scale, scale, scale);

        for (CategoryComponent component : categoryComponents) {
            component.render(matrix, mouseX, mouseY, partialTicks);
        }

        matrix.pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!initialized) return false;
        for (CategoryComponent component : categoryComponents) {
            if (component.isExactlyHovered((int)mouseX, (int)mouseY)) {
                if (component.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!initialized) return false;
        for (CategoryComponent component : categoryComponents) {
            if (component.isExactlyHovered((int)mouseX, (int)mouseY)) {
                if (component.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (CategoryComponent component : categoryComponents) {
            component.keyPressed(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (CategoryComponent component : categoryComponents) {
            component.keyReleased(keyCode, scanCode, modifiers);
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (CategoryComponent component : categoryComponents) {
            component.charTyped(codePoint, modifiers);
        }
        return false;
    }

    @Override
    public void onClose() {
        for (CategoryComponent component : categoryComponents) {
            component.onClose();
        }
    }
}