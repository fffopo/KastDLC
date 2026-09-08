package kastdlc.client.gui.component;

import kastdlc.client.module.Module;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class WindowComponent extends Component {

    private static final int CATEGORY_WIDTH = 110;
    private static final int CATEGORY_HEIGHT = 20;
    private static final int CATEGORY_GAP = 6;

    private static final int CONTENT_GAP = 18;

    private static final int WINDOW_MARGIN_X = 48;
    private static final int WINDOW_MARGIN_Y = 42;

    private static final int CATEGORY_PADDING = 7;

    private final Font font;

    private final List<CategoryComponent> categories =
            new ArrayList<>();

    private final List<Panel> panels =
            new ArrayList<>();

    private Module.Category selectedCategory;

    private boolean overlayOnly;

    private String searchText = "";

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
                y + CATEGORY_PADDING;

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
                            CATEGORY_WIDTH,
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

        for (
                Module.Category category
                : values
        ) {

            Panel panel =
                    new Panel(
                            font,
                            category,
                            x + CATEGORY_WIDTH
                                    + CONTENT_GAP,
                            y,
                            Math.max(
                                    1,
                                    width
                                            - CATEGORY_WIDTH
                                            - CONTENT_GAP
                            ),
                            height
                    );

            panel.setSearchText(
                    searchText
            );

            panels.add(panel);
        }
    }

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

    private void renderMain(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        /*
         * Very soft shadow around the whole GUI.
         */

        drawSoftRect(
                graphics,
                x - 6,
                y - 6,
                width + 12,
                height + 12,
                10,
                0x12000000
        );

        /*
         * Category floating container.
         */

        int categoryContainerHeight =
                categories.size()
                        * CATEGORY_HEIGHT
                        + Math.max(
                        0,
                        categories.size() - 1
                                * CATEGORY_GAP
                )
                        + CATEGORY_PADDING * 2;

        drawSoftRect(
                graphics,
                x,
                y,
                CATEGORY_WIDTH
                        + CATEGORY_PADDING * 2,
                categoryContainerHeight,
                9,
                0xE30D1014
        );

        drawBorder(
                graphics,
                0x20282F38,
                x,
                y,
                CATEGORY_WIDTH
                        + CATEGORY_PADDING * 2,
                categoryContainerHeight
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
         * Selected module panel.
         */

        for (
                Panel panel
                : panels
        ) {

            if (
                    panel.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            int panelX =
                    x
                            + CATEGORY_WIDTH
                            + CONTENT_GAP
                            + CATEGORY_PADDING;

            int panelY =
                    y
                            + CATEGORY_PADDING;

            int panelWidth =
                    Math.max(
                            1,
                            width
                                    - CATEGORY_WIDTH
                                    - CONTENT_GAP
                                    - CATEGORY_PADDING
                    );

            int panelHeight =
                    Math.max(
                            1,
                            height
                                    - CATEGORY_PADDING * 2
                    );

            panel.setPosition(
                    panelX,
                    panelY
            );

            panel.setSize(
                    panelWidth,
                    panelHeight
            );

            panel.setSearchText(
                    searchText
            );

            panel.render(
                    graphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

            break;
        }
    }

    private void renderOverlay(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        /*
         * Overlay is intentionally cleaner:
         * no category column, selected panel gets
         * almost the entire screen.
         */

        drawSoftRect(
                graphics,
                x - 6,
                y - 6,
                width + 12,
                height + 12,
                10,
                0x12000000
        );

        drawSoftRect(
                graphics,
                x,
                y,
                width,
                height,
                10,
                0xE30D1014
        );

        drawBorder(
                graphics,
                0x20282F38,
                x,
                y,
                width,
                height
        );

        for (
                Panel panel
                : panels
        ) {

            if (
                    panel.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            int panelX =
                    x + 12;

            int panelY =
                    y + 12;

            int panelWidth =
                    Math.max(
                            1,
                            width - 24
                    );

            int panelHeight =
                    Math.max(
                            1,
                            height - 24
                    );

            panel.setPosition(
                    panelX,
                    panelY
            );

            panel.setSize(
                    panelWidth,
                    panelHeight
            );

            panel.setSearchText(
                    searchText
            );

            panel.render(
                    graphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

            break;
        }
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        /*
         * Category selection.
         */

        if (!overlayOnly) {

            for (
                    CategoryComponent category
                    : categories
            ) {

                if (
                        !category.isHovered(
                                mouseX,
                                mouseY
                        )
                ) {
                    continue;
                }

                if (button != 0) {
                    continue;
                }

                setSelectedCategory(
                        category.getCategory()
                );

                return true;
            }
        }

        /*
         * Current panel.
         */

        for (
                Panel panel
                : panels
        ) {

            if (
                    panel.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            if (
                    panel.mouseClicked(
                            mouseX,
                            mouseY,
                            button
                    )
            ) {
                return true;
            }

            break;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (
                Panel panel
                : panels
        ) {

            if (
                    panel.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            if (
                    panel.mouseReleased(
                            mouseX,
                            mouseY,
                            button
                    )
            ) {
                return true;
            }

            break;
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

        for (
                Panel panel
                : panels
        ) {

            if (
                    panel.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            if (
                    panel.mouseDragged(
                            mouseX,
                            mouseY,
                            button,
                            deltaX,
                            deltaY
                    )
            ) {
                return true;
            }

            break;
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

        for (
                Panel panel
                : panels
        ) {

            if (
                    panel.getCategory()
                            != selectedCategory
            ) {
                continue;
            }

            if (
                    panel.mouseScrolled(
                            mouseX,
                            mouseY,
                            horizontalAmount,
                            verticalAmount
                    )
            ) {
                return true;
            }

            break;
        }

        return false;
    }

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

    public Module.Category getSelectedCategory() {
        return selectedCategory;
    }

    public List<Panel> getPanels() {
        return panels;
    }

    public List<CategoryComponent> getCategories() {
        return categories;
    }

    public boolean isOverlayOnly() {
        return overlayOnly;
    }

    public void setOverlayOnly(
            boolean overlayOnly
    ) {
        this.overlayOnly = overlayOnly;
    }

    public void toggleOverlay() {
        overlayOnly = !overlayOnly;
    }

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

        for (int i = 0; i < radius; i++) {

            double distance =
                    radius - i - 0.5;

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
                            radius - inside
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