package kastdlc.client.gui.component;

import kastdlc.client.module.Module;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Locale;
import java.util.Map;

public class CategoryComponent extends Component {

    private static final int HEIGHT = 30;

    private static final int ICON_SIZE = 16;
    private static final int ICON_X = 15;
    private static final int ICON_Y = 7;

    private static final int TEXT_X = 39;
    private static final int TEXT_Y = 9;

    private static final int RADIUS = 8;

    private static final int SELECTED_BACKGROUND = 0xFF191E25;
    private static final int HOVER_BACKGROUND = 0xFF14191F;
    private static final int SELECTED_ACCENT = 0xFFE9EDF2;

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
        super(
                x,
                y,
                width,
                height
        );

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
            drawSoftRect(
                    graphics,
                    x,
                    y,
                    width,
                    HEIGHT,
                    RADIUS,
                    SELECTED_BACKGROUND
            );
        } else if (hovered) {
            drawSoftRect(
                    graphics,
                    x,
                    y,
                    width,
                    HEIGHT,
                    RADIUS,
                    HOVER_BACKGROUND
            );
        }

        if (selected) {
            drawSoftRect(
                    graphics,
                    x + 5,
                    y + 7,
                    3,
                    HEIGHT - 14,
                    2,
                    SELECTED_ACCENT
            );
        }

        String icon =
                icons.get(category);

        if (icon != null) {
            drawTexture(
                    graphics,
                    icon,
                    x + ICON_X,
                    y + ICON_Y,
                    ICON_SIZE,
                    ICON_SIZE
            );
        }

        int textColor;

        if (selected) {
            textColor = WHITE;
        } else if (hovered) {
            textColor = TEXT;
        } else {
            textColor = MUTED;
        }

        drawText(
                graphics,
                font,
                formatCategory(),
                x + TEXT_X,
                y + TEXT_Y,
                textColor
        );
    }

    private String formatCategory() {
        if (category == null) {
            return "";
        }

        if (category == Module.Category.WORLD) {
            return "Utilities";
        }

        String value =
                category.name()
                        .toLowerCase(Locale.ROOT);

        if (value.isEmpty()) {
            return "";
        }

        return value.substring(
                0,
                1
        ).toUpperCase(Locale.ROOT)
                + value.substring(1);
    }

    public Module.Category getCategory() {
        return category;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(
            boolean selected
    ) {
        this.selected = selected;
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        return isHovered(
                mouseX,
                mouseY
        );
    }
}