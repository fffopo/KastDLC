package kastdlc.client.gui.component;

import kastdlc.client.module.Module;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Map;

public class CategoryComponent extends Component {

    private final Font font;
    private final Module.Category category;

    private final Map<Module.Category, String> icons =
            Map.of(
                    Module.Category.COMBAT, "combat",
                    Module.Category.MOVEMENT, "movement",
                    Module.Category.RENDER, "render",
                    Module.Category.PLAYER, "player",
                    Module.Category.WORLD, "utilities"
            );

    private boolean selected;

    public CategoryComponent(
            Font font,
            Module.Category category,
            int x,
            int y,
            int width,
            int height
    ) {
        super(x, y, width, height);

        this.font = font;
        this.category = category;
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        boolean hovered =
                isHovered(
                        mouseX,
                        mouseY
                );

        if (selected) {

            drawRect(
                    graphics,
                    0xFF171B21,
                    x,
                    y,
                    width,
                    height
            );

            drawRect(
                    graphics,
                    0xFFE9EDF2,
                    x,
                    y,
                    2,
                    height
            );

        } else if (hovered) {

            drawRect(
                    graphics,
                    0xFF111419,
                    x,
                    y,
                    width,
                    height
            );
        }

        String icon =
                icons.get(category);

        if (icon != null) {

            drawTexture(
                    graphics,
                    icon,
                    x + 12,
                    y + 11,
                    18,
                    18
            );
        }

        drawText(
                graphics,
                font,
                category.name(),
                x + 42,
                y + 15,
                selected
                        ? WHITE
                        : TEXT
        );
    }

    public Module.Category getCategory() {
        return category;
    }

    public void setSelected(
            boolean selected
    ) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}