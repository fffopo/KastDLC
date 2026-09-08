package kastdlc.client.gui;

import kastdlc.client.KastDLCClient;
import kastdlc.client.module.Module;
import kastdlc.client.module.settings.BooleanSetting;
import kastdlc.client.module.settings.ModeSetting;
import kastdlc.client.module.settings.NumberSetting;
import kastdlc.client.module.settings.Setting;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Locale;

public final class ClickGuiRenderer {

    public static final float WIDTH = 620.0f;
    public static final float HEIGHT = 390.0f;

    private static final float SIDEBAR_WIDTH = 116.0f;

    private static final float CONTENT_PADDING = 18.0f;
    private static final float CARD_HEIGHT = 46.0f;
    private static final float CARD_GAP = 8.0f;

    private static final int OVERLAY = 0x70000000;

    private static final int BACKGROUND = 0xFF0A0C10;
    private static final int SIDEBAR = 0xFF0D1015;

    private static final int CARD = 0xFF11151B;
    private static final int CARD_HOVER = 0xFF171C23;
    private static final int CARD_ENABLED = 0xFF1B2028;

    private static final int SETTING = 0xFF11151B;
    private static final int SETTING_HOVER = 0xFF181D24;

    private static final int BORDER = 0xFF1C222A;
    private static final int BORDER_HOVER = 0xFF303842;

    private static final int TEXT = 0xFFE8ECF1;
    private static final int TEXT_SECONDARY = 0xFFB4BBC5;
    private static final int TEXT_MUTED = 0xFF68717C;

    private static final int ACCENT = 0xFFE6EAF0;
    private static final int ACCENT_DARK = 0xFF252B33;

    private static final int SLIDER = 0xFF282E37;

    private static final int SHADOW = 0x50000000;

    private static final int RADIUS = 12;

    /*
     * ВАЖНО:
     * withFont() в твоей версии MC принимает FontDescription,
     * поэтому Identifier напрямую туда передавать нельзя.
     */
    private static final FontDescription CUSTOM_FONT =
            new FontDescription.Resource(
                    Identifier.fromNamespaceAndPath(
                            "kastdlc",
                            "captura_now"
                    )
            );

    private final Screen screen;
    private final Font font;

    public ClickGuiRenderer(
            Screen screen,
            Font font
    ) {
        this.screen = screen;
        this.font = font;
    }

    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            Module.Category selectedCategory,
            String search
    ) {

        float x =
                (screen.width - WIDTH) / 2.0f;

        float y =
                (screen.height - HEIGHT) / 2.0f;

        drawOverlay(graphics);

        drawShadow(
                graphics,
                x,
                y
        );

        drawRounded(
                graphics,
                x,
                y,
                WIDTH,
                HEIGHT,
                RADIUS,
                BACKGROUND
        );

        drawSidebar(
                graphics,
                x,
                y,
                selectedCategory,
                mouseX,
                mouseY
        );

        drawContent(
                graphics,
                x,
                y,
                selectedCategory,
                search,
                mouseX,
                mouseY
        );
    }

    private void drawOverlay(
            GuiGraphics graphics
    ) {

        graphics.fill(
                0,
                0,
                screen.width,
                screen.height,
                OVERLAY
        );
    }

    private void drawShadow(
            GuiGraphics graphics,
            float x,
            float y
    ) {

        drawRounded(
                graphics,
                x - 5,
                y - 5,
                WIDTH + 10,
                HEIGHT + 10,
                RADIUS + 4,
                SHADOW
        );
    }

    private void drawSidebar(
            GuiGraphics graphics,
            float x,
            float y,
            Module.Category selectedCategory,
            int mouseX,
            int mouseY
    ) {

        drawRounded(
                graphics,
                x,
                y,
                SIDEBAR_WIDTH,
                HEIGHT,
                RADIUS,
                SIDEBAR
        );

        drawText(
                graphics,
                "KAST",
                x + 19,
                y + 18,
                TEXT,
                9.0f
        );

        drawText(
                graphics,
                "DLC",
                x + 19,
                y + 32,
                TEXT_MUTED,
                6.5f
        );

        Module.Category[] categories = {
                Module.Category.COMBAT,
                Module.Category.MOVEMENT,
                Module.Category.RENDER,
                Module.Category.PLAYER,
                Module.Category.WORLD
        };

        float startY =
                y + 62.0f;

        for (int i = 0; i < categories.length; i++) {

            Module.Category category =
                    categories[i];

            float itemY =
                    startY + i * 43.0f;

            boolean isSelected =
                    category == selectedCategory;

            boolean hovered =
                    mouseX >= x + 8
                            && mouseX <=
                            x + SIDEBAR_WIDTH - 8
                            && mouseY >= itemY
                            && mouseY <= itemY + 34;

            if (isSelected || hovered) {

                drawRounded(
                        graphics,
                        x + 8,
                        itemY,
                        SIDEBAR_WIDTH - 16,
                        34,
                        8,
                        isSelected
                                ? 0xFF191E25
                                : 0xFF13171D
                );
            }

            if (isSelected) {

                drawRounded(
                        graphics,
                        x + 8,
                        itemY + 8,
                        2,
                        18,
                        1,
                        ACCENT
                );
            }

            drawCategoryIcon(
                    graphics,
                    category,
                    x + 20,
                    itemY + 10,
                    isSelected
                            ? TEXT
                            : TEXT_MUTED
            );

            drawText(
                    graphics,
                    categoryName(category),
                    x + 41,
                    itemY + 10,
                    isSelected
                            ? TEXT
                            : TEXT_SECONDARY,
                    7.5f
            );
        }

        drawText(
                graphics,
                "RSHIFT",
                x + 19,
                y + HEIGHT - 31,
                TEXT_MUTED,
                6.0f
        );

        drawText(
                graphics,
                "MENU",
                x + 19,
                y + HEIGHT - 18,
                0xFF414852,
                5.5f
        );
    }

    private void drawContent(
            GuiGraphics graphics,
            float x,
            float y,
            Module.Category category,
            String search,
            int mouseX,
            int mouseY
    ) {

        float contentX =
                x + SIDEBAR_WIDTH + CONTENT_PADDING;

        float contentWidth =
                WIDTH
                        - SIDEBAR_WIDTH
                        - CONTENT_PADDING * 2.0f;

        drawText(
                graphics,
                categoryName(category),
                contentX,
                y + 19,
                TEXT,
                10.0f
        );

        List<Module> modules =
                getModules(
                        category,
                        search
                );

        drawText(
                graphics,
                modules.size()
                        + (
                        modules.size() == 1
                                ? " module"
                                : " modules"
                ),
                contentX,
                y + 36,
                TEXT_MUTED,
                6.0f
        );

        drawSearch(
                graphics,
                contentX + contentWidth - 145,
                y + 13,
                145,
                search,
                mouseX,
                mouseY
        );

        float top =
                y + 58.0f;

        float gap =
                CARD_GAP;

        float cardWidth =
                (contentWidth - gap) / 2.0f;

        for (int i = 0; i < modules.size(); i++) {

            Module module =
                    modules.get(i);

            int column =
                    i % 2;

            int row =
                    i / 2;

            float cardX =
                    contentX
                            + column
                            * (
                            cardWidth + gap
                    );

            float cardY =
                    top
                            + row
                            * (
                            CARD_HEIGHT + gap
                    );

            drawModule(
                    graphics,
                    module,
                    cardX,
                    cardY,
                    cardWidth,
                    mouseX,
                    mouseY
            );
        }
    }

    private void drawModule(
            GuiGraphics graphics,
            Module module,
            float x,
            float y,
            float width,
            int mouseX,
            int mouseY
    ) {

        boolean hovered =
                mouseX >= x
                        && mouseX <= x + width
                        && mouseY >= y
                        && mouseY <= y + CARD_HEIGHT;

        int background;

        if (module.isEnabled()) {

            background =
                    CARD_ENABLED;

        } else if (hovered) {

            background =
                    CARD_HOVER;

        } else {

            background =
                    CARD;
        }

        drawRounded(
                graphics,
                x,
                y,
                width,
                CARD_HEIGHT,
                9,
                background
        );

        drawBorder(
                graphics,
                x,
                y,
                width,
                CARD_HEIGHT,
                hovered
                        ? BORDER_HOVER
                        : BORDER
        );

        drawStatusDot(
                graphics,
                x + 13,
                y + 19,
                module.isEnabled()
        );

        drawText(
                graphics,
                module.getName(),
                x + 27,
                y + 10,
                module.isEnabled()
                        ? TEXT
                        : TEXT_SECONDARY,
                7.5f
        );

        drawText(
                graphics,
                categoryName(
                        module.getCategory()
                ),
                x + 27,
                y + 26,
                TEXT_MUTED,
                5.8f
        );

        drawChevron(
                graphics,
                x + width - 18,
                y + 19,
                hovered
                        ? TEXT_SECONDARY
                        : TEXT_MUTED
        );
    }

    private void drawStatusDot(
            GuiGraphics graphics,
            float x,
            float y,
            boolean enabled
    ) {

        drawRounded(
                graphics,
                x,
                y,
                7,
                7,
                3,
                enabled
                        ? ACCENT
                        : ACCENT_DARK
        );
    }

    private void drawSearch(
            GuiGraphics graphics,
            float x,
            float y,
            float width,
            String search,
            int mouseX,
            int mouseY
    ) {

        boolean hovered =
                mouseX >= x
                        && mouseX <= x + width
                        && mouseY >= y
                        && mouseY <= y + 25;

        drawRounded(
                graphics,
                x,
                y,
                width,
                25,
                7,
                hovered
                        ? 0xFF181D24
                        : 0xFF12161B
        );

        drawSearchIcon(
                graphics,
                x + 10,
                y + 8,
                TEXT_MUTED
        );

        String text =
                search == null || search.isEmpty()
                        ? "Search..."
                        : search;

        drawText(
                graphics,
                text,
                x + 27,
                y + 8,
                search == null || search.isEmpty()
                        ? TEXT_MUTED
                        : TEXT_SECONDARY,
                6.5f
        );
    }

    private void drawSearchIcon(
            GuiGraphics graphics,
            float x,
            float y,
            int color
    ) {

        drawRounded(
                graphics,
                x,
                y,
                7,
                7,
                4,
                color
        );

        graphics.fill(
                (int) x + 5,
                (int) y + 6,
                (int) x + 10,
                (int) y + 7,
                color
        );
    }

    private void drawChevron(
            GuiGraphics graphics,
            float x,
            float y,
            int color
    ) {

        graphics.fill(
                (int) x,
                (int) y,
                (int) x + 5,
                (int) y + 1,
                color
        );

        graphics.fill(
                (int) x + 4,
                (int) y + 1,
                (int) x + 6,
                (int) y + 2,
                color
        );

        graphics.fill(
                (int) x + 5,
                (int) y + 2,
                (int) x + 7,
                (int) y + 3,
                color
        );
    }

    private void drawCategoryIcon(
            GuiGraphics graphics,
            Module.Category category,
            float x,
            float y,
            int color
    ) {

        String icon =
                switch (category) {
                    case COMBAT -> "C";
                    case MOVEMENT -> "M";
                    case RENDER -> "R";
                    case PLAYER -> "P";
                    case WORLD -> "W";
                };

        drawText(
                graphics,
                icon,
                x,
                y,
                color,
                7.0f
        );
    }

    private void drawText(
            GuiGraphics graphics,
            String text,
            float x,
            float y,
            int color,
            float size
    ) {

        /*
         * size оставлен для будущего масштабирования.
         * Сейчас главное — корректно подключить captura_now.
         */

        Style style =
                Style.EMPTY.withFont(
                        CUSTOM_FONT
                );

        Component component =
                Component.literal(text)
                        .withStyle(style);

        graphics.drawString(
                font,
                component,
                Math.round(x),
                Math.round(y),
                color,
                false
        );
    }

    private void drawRounded(
            GuiGraphics graphics,
            float x,
            float y,
            float width,
            float height,
            int radius,
            int color
    ) {

        if (width <= 0 || height <= 0) {
            return;
        }

        int ix =
                Math.round(x);

        int iy =
                Math.round(y);

        int iw =
                Math.round(width);

        int ih =
                Math.round(height);

        int r =
                Math.max(
                        1,
                        Math.min(
                                radius,
                                Math.min(iw, ih) / 2
                        )
                );

        graphics.fill(
                ix + r,
                iy,
                ix + iw - r,
                iy + ih,
                color
        );

        graphics.fill(
                ix,
                iy + r,
                ix + iw,
                iy + ih - r,
                color
        );

        for (int i = 0; i < r; i++) {

            double distance =
                    r - i - 0.5;

            double length =
                    Math.sqrt(
                            Math.max(
                                    0.0,
                                    r * r
                                            - distance * distance
                            )
                    );

            int inset =
                    (int) Math.ceil(
                            r - length
                    );

            graphics.fill(
                    ix + inset,
                    iy + i,
                    ix + iw - inset,
                    iy + i + 1,
                    color
            );

            graphics.fill(
                    ix + inset,
                    iy + ih - i - 1,
                    ix + iw - inset,
                    iy + ih - i,
                    color
            );
        }
    }

    private void drawBorder(
            GuiGraphics graphics,
            float x,
            float y,
            float width,
            float height,
            int color
    ) {

        int ix =
                Math.round(x);

        int iy =
                Math.round(y);

        int iw =
                Math.round(width);

        int ih =
                Math.round(height);

        graphics.fill(
                ix,
                iy,
                ix + iw,
                iy + 1,
                color
        );

        graphics.fill(
                ix,
                iy + ih - 1,
                ix + iw,
                iy + ih,
                color
        );

        graphics.fill(
                ix,
                iy,
                ix + 1,
                iy + ih,
                color
        );

        graphics.fill(
                ix + iw - 1,
                iy,
                ix + iw,
                iy + ih,
                color
        );
    }

    private List<Module> getModules(
            Module.Category category,
            String search
    ) {

        String query =
                search == null
                        ? ""
                        : search
                        .trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return KastDLCClient
                .MODULE_MANAGER
                .getModules(category)
                .stream()
                .filter(module ->
                        query.isEmpty()
                                || module.getName()
                                .toLowerCase(
                                        Locale.ROOT
                                )
                                .contains(query)
                )
                .toList();
    }

    private String categoryName(
            Module.Category category
    ) {

        if (category == Module.Category.WORLD) {
            return "Utilities";
        }

        String value =
                category.name()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return value.substring(0, 1)
                .toUpperCase(Locale.ROOT)
                + value.substring(1);
    }

}
