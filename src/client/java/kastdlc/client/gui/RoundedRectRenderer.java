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
        if (
                graphics == null
                        || width <= 0
                        || height <= 0
                        || color == 0
        ) {
            return;
        }

        int r = Math.max(
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

        int[] inset = getInsetTable(r);

        for (int row = 0; row < r; row++) {

            int left = inset[row];
            int right = width - left;

            graphics.fill(
                    x + left,
                    y + row,
                    x + right,
                    y + row + 1,
                    color
            );

            graphics.fill(
                    x + left,
                    y + height - row - 1,
                    x + right,
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
        if (
                graphics == null
                        || width <= 0
                        || height <= 0
        ) {
            return;
        }

        if (borderColor != 0) {
            draw(
                    graphics,
                    x,
                    y,
                    width,
                    height,
                    radius,
                    borderColor
            );
        }

        if (fillColor != 0) {

            int border = 1;

            draw(
                    graphics,
                    x + border,
                    y + border,
                    width - border * 2,
                    height - border * 2,
                    Math.max(
                            0.0F,
                            radius - border
                    ),
                    fillColor
            );
        }
    }

    private static int[] getInsetTable(int radius) {

        return switch (radius) {

            case 2 ->
                    new int[]{
                            1,
                            1
                    };

            case 3 ->
                    new int[]{
                            2,
                            1,
                            1
                    };

            case 4 ->
                    new int[]{
                            2,
                            1,
                            1,
                            2
                    };

            case 5 ->
                    new int[]{
                            3,
                            2,
                            1,
                            1,
                            2
                    };

            case 6 ->
                    new int[]{
                            3,
                            2,
                            1,
                            1,
                            2,
                            3
                    };

            case 7 ->
                    new int[]{
                            4,
                            3,
                            2,
                            1,
                            1,
                            2,
                            3
                    };

            case 8 ->
                    new int[]{
                            4,
                            3,
                            2,
                            1,
                            1,
                            2,
                            3,
                            4
                    };

            default ->
                    buildInsetTable(radius);
        };
    }

    private static int[] buildInsetTable(int radius) {

        int[] table =
                new int[radius];

        double radiusSquared =
                radius * radius;

        for (
                int row = 0;
                row < radius;
                row++
        ) {

            double distance =
                    radius
                            - row
                            - 0.5D;

            double inside =
                    Math.sqrt(
                            Math.max(
                                    0.0D,
                                    radiusSquared
                                            - distance
                                            * distance
                            )
                    );

            table[row] =
                    (int) Math.ceil(
                            radius - inside
                    );
        }

        return table;
    }
}