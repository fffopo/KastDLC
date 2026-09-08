package kastdlc.client.gui.component;

import kastdlc.client.module.Module;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class WindowComponent extends Component {

    /*
     * ============================================================
     * WINDOW
     * ============================================================
     */

    private static final int WINDOW_MARGIN_X = 32;
    private static final int WINDOW_MARGIN_Y = 24;

    private static final int SIDEBAR_WIDTH = 128;

    private static final int WINDOW_RADIUS = 14;
    private static final int SIDEBAR_RADIUS = 14;

    /*
     * ============================================================
     * CATEGORIES
     * ============================================================
     */

    private static final int CATEGORY_HEIGHT = 36;
    private static final int CATEGORY_GAP = 4;
    private static final int CATEGORY_PADDING = 8;

    /*
     * ============================================================
     * CONTENT
     * ============================================================
     */

    private static final int CONTENT_PADDING = 18;
    private static final int HEADER_HEIGHT = 48;

    /*
     * ============================================================
     * COLORS
     * ============================================================
     */

    private static final int WINDOW =
            0xF30B0E13;

    private static final int WINDOW_TOP =
            0xF50D1016;

    private static final int SIDEBAR =
            0xF2080B10;

    private static final int SIDEBAR_HOVER =
            0xFF12171E;

    private static final int SIDEBAR_SELECTED =
            0xFF171D25;

    private static final int SIDEBAR_ACCENT =
            0xFFE8EDF3;

    private static final int CONTENT =
            0xF20C1015;

    private static final int BORDER =
            0x302A313A;

    private static final int BORDER_LIGHT =
            0x45333B46;

    private static final int TEXT =
            0xFFF0F2F5;

    private static final int TEXT_SECONDARY =
            0xFFB9C0CA;

    private static final int MUTED =
            0xFF68717D;

    private static final int SHADOW =
            0x60000000;

    /*
     * ============================================================
     * STATE
     * ============================================================
     */

    private final Font font;

    private final List<CategoryComponent> categories =
            new ArrayList<>();

    private final List<Panel> panels =
            new ArrayList<>();

    private Module.Category selectedCategory;

    private boolean overlayOnly;

    private String searchText = "";

    /*
     * ============================================================
     * CONSTRUCTOR
     * ============================================================
     */

    public WindowComponent(
            Font font,
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

        selectedCategory =
                Module.Category.RENDER;

        build();
    }

    /*
     * ============================================================
     * BUILD
     * ============================================================
     */

    private void build() {

        categories.clear();
        panels.clear();

        buildCategories();
        buildPanels();

        updateSelection();
    }

    private void buildCategories() {

        Module.Category[] values = {

                Module.Category.COMBAT,
                Module.Category.MOVEMENT,
                Module.Category.RENDER,
                Module.Category.PLAYER,
                Module.Category.WORLD

        };

        int categoryX =
                x + CATEGORY_PADDING;

        int categoryY =
                y + 58;

        int categoryWidth =
                SIDEBAR_WIDTH
                        - CATEGORY_PADDING * 2;

        for (
                Module.Category category
                : values
        ) {

            CategoryComponent component =
                    new CategoryComponent(
                            font,
                            category,
                            categoryX,
                            categoryY,
                            categoryWidth,
                            CATEGORY_HEIGHT
                    );

            categories.add(component);

            categoryY +=
                    CATEGORY_HEIGHT
                            + CATEGORY_GAP;
        }
    }

    private void buildPanels() {

        Module.Category[] values = {

                Module.Category.COMBAT,
                Module.Category.MOVEMENT,
                Module.Category.RENDER,
                Module.Category.PLAYER,
                Module.Category.WORLD

        };

        int panelX =
                x
                        + SIDEBAR_WIDTH
                        + CONTENT_PADDING;

        int panelY =
                y
                        + CONTENT_PADDING;

        int panelWidth =
                Math.max(
                        1,
                        width
                                - SIDEBAR_WIDTH
                                - CONTENT_PADDING * 2
                );

        int panelHeight =
                Math.max(
                        1,
                        height
                                - CONTENT_PADDING * 2
                );

        for (
                Module.Category category
                : values
        ) {

            Panel panel =
                    new Panel(
                            font,
                            category,
                            panelX,
                            panelY,
                            panelWidth,
                            panelHeight
                    );

            panel.setSearchText(
                    searchText
            );

            panels.add(panel);
        }
    }

    /*
     * ============================================================
     * RENDER
     * ============================================================
     */

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        if (overlayOnly) {

            renderOverlay(
                    graphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

            return;
        }

        renderMain(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    /*
     * ============================================================
     * MAIN WINDOW
     * ============================================================
     */

    private void renderMain(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        /*
         * Shadow.
         */

        drawSoftRect(
                graphics,
                x - 7,
                y - 7,
                width + 14,
                height + 14,
                WINDOW_RADIUS + 6,
                SHADOW
        );

        /*
         * Window.
         */

        drawSoftRect(
                graphics,
                x,
                y,
                width,
                height,
                WINDOW_RADIUS,
                WINDOW
        );

        /*
         * Top content surface.
         */

        drawSoftRect(
                graphics,
                x + SIDEBAR_WIDTH,
                y,
                Math.max(
                        1,
                        width - SIDEBAR_WIDTH
                ),
                height,
                WINDOW_RADIUS,
                CONTENT
        );

        /*
         * Border.
         */

        drawBorder(
                graphics,
                BORDER,
                x,
                y,
                width,
                height
        );

        /*
         * Sidebar.
         */

        drawSoftRect(
                graphics,
                x,
                y,
                SIDEBAR_WIDTH,
                height,
                SIDEBAR_RADIUS,
                SIDEBAR
        );

        /*
         * Sidebar separator.
         */

        graphics.fill(
                x + SIDEBAR_WIDTH - 1,
                y + 14,
                x + SIDEBAR_WIDTH,
                y + height - 14,
                BORDER
        );

        /*
         * Brand.
         */

        drawText(
                graphics,
                font,
                "KAST",
                x + 18,
                y + 17,
                TEXT
        );

        drawText(
                graphics,
                font,
                "DLC",
                x + 19,
                y + 30,
                MUTED
        );

        /*
         * Brand separator.
         */

        graphics.fill(
                x + 16,
                y + 45,
                x + SIDEBAR_WIDTH - 16,
                y + 46,
                BORDER
        );

        /*
         * Categories.
         */

        for (
                CategoryComponent category
                : categories
        ) {

            category.render(
                    graphics,
                    mouseX,
                    mouseY,
                    partialTick
            );
        }

        /*
         * Current panel.
         */

        Panel selectedPanel =
                getSelectedPanel();

        if (selectedPanel == null) {
            return;
        }

        int panelX =
                x
                        + SIDEBAR_WIDTH
                        + CONTENT_PADDING;

        int panelY =
                y
                        + CONTENT_PADDING;

        int panelWidth =
                Math.max(
                        1,
                        width
                                - SIDEBAR_WIDTH
                                - CONTENT_PADDING * 2
                );

        int panelHeight =
                Math.max(
                        1,
                        height
                                - CONTENT_PADDING * 2
                );

        selectedPanel.setPosition(
                panelX,
                panelY
        );

        selectedPanel.setSize(
                panelWidth,
                panelHeight
        );

        selectedPanel.setSearchText(
                searchText
        );

        selectedPanel.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    /*
     * ============================================================
     * OVERLAY MODE
     * ============================================================
     */

    private void renderOverlay(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        /*
         * Shadow.
         */

        drawSoftRect(
                graphics,
                x - 7,
                y - 7,
                width + 14,
                height + 14,
                WINDOW_RADIUS + 6,
                SHADOW
        );

        /*
         * Window.
         */

        drawSoftRect(
                graphics,
                x,
                y,
                width,
                height,
                WINDOW_RADIUS,
                WINDOW
        );

        drawBorder(
                graphics,
                BORDER,
                x,
                y,
                width,
                height
        );

        Panel selectedPanel =
                getSelectedPanel();

        if (selectedPanel == null) {
            return;
        }

        int padding = 18;

        selectedPanel.setPosition(
                x + padding,
                y + padding
        );

        selectedPanel.setSize(
                Math.max(
                        1,
                        width - padding * 2
                ),
                Math.max(
                        1,
                        height - padding * 2
                )
        );

        selectedPanel.setSearchText(
                searchText
        );

        selectedPanel.render(
                graphics,
                mouseX,
                mouseY,
                partialTick
        );
    }

    /*
     * ============================================================
     * PANEL
     * ============================================================
     */

    private Panel getSelectedPanel() {

        for (
                Panel panel
                : panels
        ) {

            if (
                    panel.getCategory()
                            == selectedCategory
            ) {

                return panel;
            }
        }

        return null;
    }

    /*
     * ============================================================
     * MOUSE
     * ============================================================
     */

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        /*
         * Categories.
         */

        if (
                !overlayOnly
                        && button == 0
        ) {

            for (
                    CategoryComponent category
                    : categories
            ) {

                if (
                        category.isHovered(
                                mouseX,
                                mouseY
                        )
                ) {

                    setSelectedCategory(
                            category.getCategory()
                    );

                    return true;
                }
            }
        }

        /*
         * Panel.
         */

        Panel panel =
                getSelectedPanel();

        if (panel != null) {

            return panel.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            );
        }

        return false;
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        Panel panel =
                getSelectedPanel();

        if (panel != null) {

            return panel.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            );
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

        Panel panel =
                getSelectedPanel();

        if (panel != null) {

            return panel.mouseDragged(
                    mouseX,
                    mouseY,
                    button,
                    deltaX,
                    deltaY
            );
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

        Panel panel =
                getSelectedPanel();

        if (panel != null) {

            return panel.mouseScrolled(
                    mouseX,
                    mouseY,
                    horizontalAmount,
                    verticalAmount
            );
        }

        return false;
    }

    /*
     * ============================================================
     * CATEGORY
     * ============================================================
     */

    public void setSelectedCategory(
            Module.Category category
    ) {

        if (category == null) {
            return;
        }

        selectedCategory =
                category;

        updateSelection();
    }

    private void updateSelection() {

        for (
                CategoryComponent category
                : categories
        ) {

            category.setSelected(
                    category.getCategory()
                            == selectedCategory
            );
        }
    }

    public Module.Category getSelectedCategory() {
        return selectedCategory;
    }

    /*
     * ============================================================
     * SEARCH
     * ============================================================
     */

    public void setSearchText(
            String searchText
    ) {

        this.searchText =
                searchText == null
                        ? ""
                        : searchText;

        for (
                Panel panel
                : panels
        ) {

            panel.setSearchText(
                    this.searchText
            );
        }
    }

    public String getSearchText() {
        return searchText;
    }

    /*
     * ============================================================
     * OVERLAY
     * ============================================================
     */

    public boolean isOverlayOnly() {
        return overlayOnly;
    }

    public void setOverlayOnly(
            boolean overlayOnly
    ) {

        this.overlayOnly =
                overlayOnly;
    }

    public void toggleOverlay() {

        overlayOnly =
                !overlayOnly;
    }

    /*
     * ============================================================
     * RESIZE
     * ============================================================
     */

    public void resize(
            int width,
            int height
    ) {

        this.x =
                WINDOW_MARGIN_X;

        this.y =
                WINDOW_MARGIN_Y;

        this.width =
                Math.max(
                        1,
                        width
                                - WINDOW_MARGIN_X * 2
                );

        this.height =
                Math.max(
                        1,
                        height
                                - WINDOW_MARGIN_Y * 2
                );

        build();
    }

    /*
     * ============================================================
     * SOFT RECTANGLE
     * ============================================================
     */

    private void drawSoftRect(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int radius,
            int color
    ) {

        if (
                width <= 0
                        || height <= 0
        ) {

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

        /*
         * Main body.
         */

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

        /*
         * Rounded corners.
         */

        for (
                int row = 0;
                row < radius;
                row++
        ) {

            double distance =
                    radius
                            - row
                            - 0.5;

            double inside =
                    Math.sqrt(
                            Math.max(
                                    0.0,
                                    radius * radius
                                            - distance * distance
                            )
                    );

            int inset =
                    (int) Math.ceil(
                            radius
                                    - inside
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

    /*
     * ============================================================
     * BORDER
     * ============================================================
     */

    @Override
    protected void drawBorder(
            GuiGraphics graphics,
            int color,
            int x,
            int y,
            int width,
            int height
    ) {

        if (
                width <= 0
                        || height <= 0
        ) {

            return;
        }

        graphics.fill(
                x,
                y,
                x + width,
                y + 1,
                color
        );

        graphics.fill(
                x,
                y + height - 1,
                x + width,
                y + height,
                color
        );

        graphics.fill(
                x,
                y,
                x + 1,
                y + height,
                color
        );

        graphics.fill(
                x + width - 1,
                y,
                x + width,
                y + height,
                color
        );
    }
}