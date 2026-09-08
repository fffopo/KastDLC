package kastdlc.client.gui.component;

import kastdlc.client.module.Module;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class WindowComponent extends Component {

    private final Font font;

    private final List<Panel> panels =
            new ArrayList<>();

    private Module.Category selectedCategory;

    public WindowComponent(
            Font font,
            int x,
            int y,
            int width,
            int height
    ) {
        super(x, y, width, height);

        this.font = font;

        this.selectedCategory =
                Module.Category.RENDER;

        buildPanels();
    }

    private void buildPanels() {

        panels.clear();

        Module.Category[] categories = {
                Module.Category.COMBAT,
                Module.Category.MOVEMENT,
                Module.Category.RENDER,
                Module.Category.PLAYER,
                Module.Category.WORLD
        };

        int panelY = y + 58;

        for (Module.Category category : categories) {

            panels.add(
                    new Panel(
                            font,
                            category,
                            x,
                            panelY,
                            width,
                            height - 58
                    )
            );
        }
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        drawRect(
                graphics,
                0xFF080A0D,
                x,
                y,
                width,
                height
        );

        drawBorder(
                graphics,
                BORDER,
                x,
                y,
                width,
                height
        );

        drawText(
                graphics,
                font,
                "KastDLC",
                x + 20,
                y + 20,
                WHITE
        );

        for (Panel panel : panels) {

            if (panel.getCategory() == selectedCategory) {

                panel.setPosition(
                        x,
                        y + 58
                );

                panel.setSize(
                        width,
                        height - 58
                );

                panel.render(
                        graphics,
                        mouseX,
                        mouseY,
                        partialTick
                );
            }
        }
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (Panel panel : panels) {

            if (panel.getCategory() == selectedCategory) {

                if (panel.mouseClicked(
                        mouseX,
                        mouseY,
                        button
                )) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (Panel panel : panels) {

            if (panel.getCategory() == selectedCategory) {

                if (panel.mouseReleased(
                        mouseX,
                        mouseY,
                        button
                )) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {

        for (Panel panel : panels) {

            if (panel.getCategory() == selectedCategory) {

                if (panel.mouseDragged(
                        mouseX,
                        mouseY,
                        button,
                        deltaX,
                        deltaY
                )) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontalAmount,
            double verticalAmount
    ) {

        for (Panel panel : panels) {

            if (panel.getCategory() == selectedCategory) {

                if (panel.mouseScrolled(
                        mouseX,
                        mouseY,
                        horizontalAmount,
                        verticalAmount
                )) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setSelectedCategory(
            Module.Category category
    ) {
        this.selectedCategory = category;
    }

    public Module.Category getSelectedCategory() {
        return selectedCategory;
    }

    public List<Panel> getPanels() {
        return panels;
    }
}