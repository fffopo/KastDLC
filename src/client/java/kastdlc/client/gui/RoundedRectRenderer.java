package kastdlc.client.gui;

import net.minecraft.client.gui.GuiGraphics;

public final class RoundedRectRenderer {

    private RoundedRectRenderer() {
    }

    public static void draw(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            float radius,
            int color
    ) {

        if (width <= 0 || height <= 0 || color == 0) {
            return;
        }

        int r =
                Math.max(
                        0,
                        Math.min(
                                Math.round(radius),
                                Math.min(width, height) / 2
                        )
                );

        if (r <= 1) {

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
                x + r,
                y,
                x + width - r,
                y + height,
                color
        );

        graphics.fill(
                x,
                y + r,
                x + width,
                y + height - r,
                color
        );

        for (int row = 0; row < r; row++) {

            double distance =
                    r - row - 0.5;

            int inset =
                    (int) Math.ceil(
                            r
                                    - Math.sqrt(
                                    Math.max(
                                            0.0,
                                            r * r
                                                    - distance * distance
                                    )
                            )
                    );

            graphics.fill(
                    x + inset,
                    y + row,
                    x + width - inset,
                    y + row + 1,
                    color
            );

            graphics.fill(
                    x + inset,
                    y + height - row - 1,
                    x + width - inset,
                    y + height - row,
                    color
            );
        }
    }

    public static void drawBorder(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            float radius,
            int borderColor,
            int fillColor
    ) {

        if (width <= 0 || height <= 0) {
            return;
        }

        draw(
                graphics,
                x,
                y,
                width,
                height,
                radius,
                borderColor
        );

        draw(
                graphics,
                x + 1,
                y + 1,
                width - 2,
                height - 2,
                Math.max(
                        0.0F,
                        radius - 1.0F
                ),
                fillColor
        );
    }
}