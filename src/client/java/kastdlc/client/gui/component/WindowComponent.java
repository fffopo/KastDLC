package kastdlc.client.gui.component;

import kastdlc.client.KastDLCClient;
import kastdlc.client.module.Module;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WindowComponent extends Component {

    /*
     * =========================
     * GEOMETRY
     * =========================
     */

    private static final int WINDOW_MARGIN_X = 42;
    private static final int WINDOW_MARGIN_Y = 30;

    private static final int SIDEBAR_WIDTH = 126;

    private static final int WINDOW_RADIUS = 14;
    private static final int SIDEBAR_RADIUS = 12;

    private static final int CATEGORY_HEIGHT = 34;
    private static final int CATEGORY_GAP = 5;
    private static final int CATEGORY_PADDING = 8;

    private static final int CONTENT_PADDING = 18;
    private static final int HEADER_HEIGHT = 42;

    /*
     * =========================
     * COLORS
     * =========================
     */

    private static final int WINDOW =
            0xF20C0F14;

    private static final int SIDEBAR =
            0xF20A0D12;

    private static final int BORDER =
            0x302C333D;

    private static final int TEXT =
            0xFFE9EDF3;

    private static final int MUTED =
            0xFF707985;

    private static final int SIDEBAR_SEPARATOR =
            0x302C333D;

    /*
     * =========================
     * COMPONENTS
     * =========================
     */

    private final Font font;

    private final List<CategoryComponent> categories =
            new ArrayList<>();

    private Panel selectedPanel;

    private Module.Category selectedCategory =
            Module.Category.RENDER;

    private boolean overlayOnly;

    /*
     * =========================
     * SEARCH
     * =========================
     */

    private String searchText = "";

    /*
     * =========================
     * CONSTRUCTOR
     * =========================
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

        createCategories();
        rebuildPanel();
    }

    /*
     * =========================
     * CATEGORIES
     * =========================
     */

    private void createCategories() {

        categories.clear();

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

            component.setSelected(
                    category == selectedCategory
            );

            categories.add(component);

            categoryY +=
                    CATEGORY_HEIGHT
                            + CATEGORY_GAP;
        }
    }

    private void rebuildPanel() {

        int contentX =
                x
                        + SIDEBAR_WIDTH
                        + CONTENT_PADDING;

        int contentY =
                y
                        + HEADER_HEIGHT
                        + CONTENT_PADDING;

        int contentWidth =
                width
                        - SIDEBAR_WIDTH
                        - CONTENT_PADDING * 2;

        int contentHeight =
                height
                        - HEADER_HEIGHT
                        - CONTENT_PADDING * 2;

        contentWidth =
                Math.max(
                        1,
                        contentWidth
                );

        contentHeight =
                Math.max(
                        1,
                        contentHeight
                );

        selectedPanel =
                new Panel(
                        font,
                        selectedCategory,
                        contentX,
                        contentY,
                        contentWidth,
                        contentHeight
                );

        selectedPanel.setSearchText(
                searchText
        );
    }

    /*
     * =========================
     * RESIZE
     * =========================
     */

    public void resize(
            int screenWidth,
            int screenHeight
    ) {

        int newX =
                WINDOW_MARGIN_X;

        int newY =
                WINDOW_MARGIN_Y;

        int newWidth =
                Math.max(
                        1,
                        screenWidth
                                - WINDOW_MARGIN_X * 2
                );

        int newHeight =
                Math.max(
                        1,
                        screenHeight
                                - WINDOW_MARGIN_Y * 2
                );

        setPosition(
                newX,
                newY
        );

        setSize(
                newWidth,
                newHeight
        );

        createCategories();
        rebuildPanel();
    }

    /*
     * =========================
     * RENDER
     * =========================
     */

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        if (
                graphics == null
                        || width <= 0
                        || height <= 0
        ) {
            return;
        }

        /*
         * =========================
         * WINDOW
         * =========================
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
         * =========================
         * WINDOW BORDER
         * =========================
         */

        drawBorder(
                graphics,
                x,
                y,
                width,
                height,
                WINDOW_RADIUS,
                BORDER,
                WINDOW
        );

        /*
         * =========================
         * SIDEBAR
         * =========================
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
                x + SIDEBAR_WIDTH,
                y + 10,
                x + SIDEBAR_WIDTH + 1,
                y + height - 10,
                SIDEBAR_SEPARATOR
        );

        /*
         * =========================
         * LOGO
         * =========================
         */

        drawText(
                graphics,
                font,
                "KAST",
                x + 18,
                y + 15,
                TEXT
        );

        drawText(
                graphics,
                font,
                "DLC",
                x + 18,
                y + 29,
                MUTED
        );

        /*
         * =========================
         * CATEGORIES
         * =========================
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
         * =========================
         * CONTENT HEADER
         * =========================
         */

        int headerX =
                x
                        + SIDEBAR_WIDTH
                        + CONTENT_PADDING;

        int headerY =
                y + 13;

        drawText(
                graphics,
                font,
                formatCategory(
                        selectedCategory
                ),
                headerX,
                headerY,
                TEXT
        );

        int moduleCount =
                KastDLCClient.MODULE_MANAGER
                        .getModules(
                                selectedCategory
                        )
                        .size();

        String countText =
                moduleCount
                        + (
                        moduleCount == 1
                                ? " module"
                                : " modules"
                );

        drawText(
                graphics,
                font,
                countText,
                headerX,
                headerY + 17,
                MUTED
        );

        /*
         * =========================
         * PANEL
         * =========================
         */

        if (selectedPanel != null) {

            if (overlayOnly) {

                int overlayX =
                        x
                                + SIDEBAR_WIDTH
                                + CONTENT_PADDING;

                int overlayY =
                        y
                                + CONTENT_PADDING;

                int overlayWidth =
                        width
                                - SIDEBAR_WIDTH
                                - CONTENT_PADDING * 2;

                int overlayHeight =
                        height
                                - CONTENT_PADDING * 2;

                overlayWidth =
                        Math.max(
                                1,
                                overlayWidth
                        );

                overlayHeight =
                        Math.max(
                                1,
                                overlayHeight
                        );

                selectedPanel.setPosition(
                        overlayX,
                        overlayY
                );

                selectedPanel.setSize(
                        overlayWidth,
                        overlayHeight
                );

            } else {

                int panelX =
                        x
                                + SIDEBAR_WIDTH
                                + CONTENT_PADDING;

                int panelY =
                        y
                                + HEADER_HEIGHT
                                + CONTENT_PADDING;

                int panelWidth =
                        width
                                - SIDEBAR_WIDTH
                                - CONTENT_PADDING * 2;

                int panelHeight =
                        height
                                - HEADER_HEIGHT
                                - CONTENT_PADDING * 2;

                panelWidth =
                        Math.max(
                                1,
                                panelWidth
                        );

                panelHeight =
                        Math.max(
                                1,
                                panelHeight
                        );

                selectedPanel.setPosition(
                        panelX,
                        panelY
                );

                selectedPanel.setSize(
                        panelWidth,
                        panelHeight
                );
            }

            selectedPanel.render(
                    graphics,
                    mouseX,
                    mouseY,
                    partialTick
            );
        }
    }

    /*
     * =========================
     * MOUSE CLICK
     * =========================
     */

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        /*
         * Categories first.
         */

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

                if (button == 0) {

                    selectCategory(
                            category.getCategory()
                    );

                    return true;
                }
            }
        }

        /*
         * Panel.
         */

        if (selectedPanel != null) {

            if (
                    selectedPanel.mouseClicked(
                            mouseX,
                            mouseY,
                            button
                    )
            ) {
                return true;
            }
        }

        return false;
    }

    /*
     * =========================
     * MOUSE RELEASE
     * =========================
     */

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (selectedPanel != null) {

            if (
                    selectedPanel.mouseReleased(
                            mouseX,
                            mouseY,
                            button
                    )
            ) {
                return true;
            }
        }

        for (
                CategoryComponent category
                : categories
        ) {

            if (
                    category.mouseReleased(
                            mouseX,
                            mouseY,
                            button
                    )
            ) {
                return true;
            }
        }

        return false;
    }

    /*
     * =========================
     * DRAG
     * =========================
     */

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {

        if (selectedPanel != null) {

            if (
                    selectedPanel.mouseDragged(
                            mouseX,
                            mouseY,
                            button,
                            deltaX,
                            deltaY
                    )
            ) {
                return true;
            }
        }

        return false;
    }

    /*
     * =========================
     * SCROLL
     * =========================
     */

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontalAmount,
            double verticalAmount
    ) {

        if (selectedPanel != null) {

            if (
                    selectedPanel.mouseScrolled(
                            mouseX,
                            mouseY,
                            horizontalAmount,
                            verticalAmount
                    )
            ) {
                return true;
            }
        }

        return false;
    }

    /*
     * =========================
     * CATEGORY
     * =========================
     */

    private void selectCategory(
            Module.Category category
    ) {

        if (
                category == null
                        || category == selectedCategory
        ) {
            return;
        }

        selectedCategory =
                category;

        for (
                CategoryComponent component
                : categories
        ) {

            component.setSelected(
                    component.getCategory()
                            == selectedCategory
            );
        }

        rebuildPanel();
    }

    private String formatCategory(
            Module.Category category
    ) {

        if (category == null) {
            return "";
        }

        if (
                category
                        == Module.Category.WORLD
        ) {
            return "Utilities";
        }

        String value =
                category.name()
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (value.isEmpty()) {
            return "";
        }

        return value.substring(
                0,
                1
        ).toUpperCase(
                Locale.ROOT
        ) + value.substring(1);
    }

    /*
     * =========================
     * SEARCH
     * =========================
     */

    public void setSearchText(
            String searchText
    ) {

        this.searchText =
                searchText == null
                        ? ""
                        : searchText;

        if (selectedPanel != null) {

            selectedPanel.setSearchText(
                    this.searchText
            );
        }
    }

    public String getSearchText() {
        return searchText;
    }

    /*
     * =========================
     * OVERLAY
     * =========================
     */

    public void toggleOverlay() {
        overlayOnly = !overlayOnly;
    }

    public boolean isOverlayOnly() {
        return overlayOnly;
    }
}