package kastdlc.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public final class RoundedRectRenderer {

    private static final String MOD_ID = "kastdlc";

    private static final Identifier ROUNDED_RECT =
            Identifier.fromNamespaceAndPath(
                    MOD_ID,
                    "textures/gui/rounded_rect.png"
            );

    /*
     * Размер исходной текстуры.
     *
     * Она специально большая, чтобы после масштабирования
     * края не выглядели как набор крупных квадратных пикселей.
     */
    private static final int TEXTURE_SIZE = 256;

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

        int radiusPixels =
                clampRadius(
                        radius,
                        width,
                        height
                );

        if (radiusPixels <= 0) {

            graphics.fill(
                    x,
                    y,
                    x + width,
                    y + height,
                    color
            );

            return;
        }

        /*
         * Рисуем всю фигуру одной текстурой.
         *
         * Minecraft сам масштабирует её до нужного размера,
         * поэтому Java больше не рисует каждый пиксель угла.
         */
        graphics.blit(
                ROUNDED_RECT,
                x,
                y,
                0,
                0,
                width,
                height,
                TEXTURE_SIZE,
                TEXTURE_SIZE,
                color
        );
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

        /*
         * Если есть фон — сначала рисуем его.
         */
        if (fillColor != 0) {

            draw(
                    graphics,
                    x,
                    y,
                    width,
                    height,
                    radius,
                    fillColor
            );
        }

        /*
         * Тонкая рамка.
         *
         * Рисуем внешнюю фигуру и поверх неё внутреннюю.
         * Это не pixel-by-pixel построение углов.
         */
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

            int innerWidth =
                    width - 2;

            int innerHeight =
                    height - 2;

            if (
                    innerWidth > 0
                            && innerHeight > 0
            ) {

                draw(
                        graphics,
                        x + 1,
                        y + 1,
                        innerWidth,
                        innerHeight,
                        Math.max(
                                0.0F,
                                radius - 1.0F
                        ),
                        fillColor
                );
            }
        }
    }

    private static int clampRadius(
            float radius,
            int width,
            int height
    ) {

        return Math.max(
                0,
                Math.min(
                        Math.round(radius),
                        Math.min(
                                width,
                                height
                        ) / 2
                )
        );
    }
}