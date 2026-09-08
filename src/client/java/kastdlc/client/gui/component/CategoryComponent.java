package kastdlc.client.gui.component;

import kastdlc.client.module.Module;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Map;

public class CategoryComponent extends Component {

    private static final int HEIGHT = 20;

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

        /*
         * Фон активной/наведённой категории.
         */

        if (selected) {

            drawSoftRect(
                    graphics,
                    x,
                    y,
                    width,
                    HEIGHT,
                    6,
                    0xFF1A1E24
            );

        } else if (hovered) {

            drawSoftRect(
                    graphics,
                    x,
                    y,
                    width,
                    HEIGHT,
                    6,
                    0xFF13171C
            );
        }

        /*
         * Индикатор активной категории.
         */

        if (selected) {

            drawSoftRect(
                    graphics,
                    x,
                    y + 4,
                    2,
                    HEIGHT - 8,
                    1,
                    WHITE
            );
        }

        /*
         * Иконка.
         */

        String icon =
                icons.get(category);

        if (icon != null) {

            drawTexture(
                    graphics,
                    icon,
                    x + 9,
                    y + 2,
                    16,
                    16
            );
        }

        /*
         * Название.
         */

        drawText(
                graphics,
                font,
                formatCategory(),
                x + 32,
                y + 5,
                selected
                        ? WHITE
                        : hovered
                        ? TEXT
                        : MUTED
        );
    }

    private String formatCategory() {

        if (category ==
                Module.Category.WORLD) {

            return "Utilities";
        }

        String value =
                category.name()
                        .toLowerCase();

        return value.substring(
                0,
                1
        ).toUpperCase()
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

    private void drawSoftRect(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int radius,
            int color
    ) {

        if (width <= 0 || height <= 0) {
            return;
        }

        radius =
                Math.min(
                        radius,
                        Math.min(
                                width,
                                height
                        ) / 2
                );

        if (radius <= 0) {

            graphics.fill(
                    x,
                    y,
                    x + width,
                    y + height,
                    color
            );

            return;
        }

        graphics.fill(
                x + radius,
                y,
                x + width - radius,
                y + height,
                color
        );

        graphics.fill(
                x,
                y + radius,
                x + width,
                y + height - radius,
                color
        );

        for (int i = 0;
             i < radius;
             i++) {

            double distance =
                    radius
                            - i
                            - 0.5;

            double inside =
                    Math.sqrt(
                            Math.max(
                                    0.0,
                                    radius * radius
                                            - distance
                                            * distance
                            )
                    );

            int inset =
                    (int) Math.ceil(
                            radius
                                    - inside
                    );

            graphics.fill(
                    x + inset,
                    y + i,
                    x + width - inset,
                    y + i + 1,
                    color
            );

            graphics.fill(
                    x + inset,
                    y + height - i - 1,
                    x + width - inset,
                    y + height - i,
                    color
            );
        }
    }
}