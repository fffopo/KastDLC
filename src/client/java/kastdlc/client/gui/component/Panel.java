package kastdlc.client.gui.component;

import kastdlc.client.KastDLCClient;
import kastdlc.client.module.Module;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Panel extends Component {

    private static final int HEADER_HEIGHT = 24;

    private static final int CARD_WIDTH = 180;
    private static final int CARD_HEIGHT = 58;

    private static final int COLUMN_GAP = 10;
    private static final int ROW_GAP = 8;

    private static final int PADDING = 4;

    private final Font font;
    private final Module.Category category;

    private final List<ModuleComponent> modules =
            new ArrayList<>();

    private float scroll;

    private String searchText = "";

    public Panel(
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

        rebuild();
    }

    private void rebuild() {

        modules.clear();

        List<Module> categoryModules =
                KastDLCClient.MODULE_MANAGER
                        .getModules(category);

        for (Module module : categoryModules) {

            modules.add(
                    new ModuleComponent(
                            font,
                            module,
                            x,
                            y,
                            CARD_WIDTH,
                            CARD_HEIGHT
                    )
            );
        }
    }

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
         * HEADER
         * =========================
         */

        drawText(
                graphics,
                font,
                formatCategory(),
                x,
                y,
                WHITE
        );

        /*
         * =========================
         * CONTENT
         * =========================
         */

        int contentX =
                x;

        int contentY =
                y + HEADER_HEIGHT;

        int availableWidth =
                Math.max(
                        CARD_WIDTH,
                        width - PADDING * 2
                );

        int columns =
                Math.max(
                        1,
                        (
                                availableWidth
                                        + COLUMN_GAP
                        )
                                / (
                                CARD_WIDTH
                                        + COLUMN_GAP
                        )
                );

        /*
         * =========================
         * FILTER
         * =========================
         */

        List<ModuleComponent> visibleModules =
                new ArrayList<>();

        for (ModuleComponent module : modules) {

            if (matchesSearch(module)) {
                visibleModules.add(module);
            }
        }

        /*
         * =========================
         * ROW HEIGHTS
         * =========================
         *
         * Expanded modules can be taller
         * than the normal 58px card.
         */

        List<Integer> rowHeights =
                new ArrayList<>();

        for (
                int index = 0;
                index < visibleModules.size();
                index += columns
        ) {

            int rowHeight =
                    CARD_HEIGHT;

            int rowEnd =
                    Math.min(
                            index + columns,
                            visibleModules.size()
                    );

            for (
                    int i = index;
                    i < rowEnd;
                    i++
            ) {

                ModuleComponent module =
                        visibleModules.get(i);

                rowHeight =
                        Math.max(
                                rowHeight,
                                module.getHeight()
                        );
            }

            rowHeights.add(
                    rowHeight
            );
        }

        /*
         * =========================
         * POSITION MODULES
         * =========================
         */

        int currentY =
                contentY;

        for (
                int row = 0;
                row < rowHeights.size();
                row++
        ) {

            int rowHeight =
                    rowHeights.get(row);

            int rowStart =
                    row * columns;

            int rowEnd =
                    Math.min(
                            rowStart + columns,
                            visibleModules.size()
                    );

            for (
                    int index = rowStart;
                    index < rowEnd;
                    index++
            ) {

                ModuleComponent module =
                        visibleModules.get(index);

                int column =
                        index % columns;

                int moduleX =
                        contentX
                                + column
                                * (
                                CARD_WIDTH
                                        + COLUMN_GAP
                        );

                int moduleY =
                        currentY
                                - (int) scroll;

                module.setPosition(
                        moduleX,
                        moduleY
                );

                module.setSize(
                        CARD_WIDTH,
                        module.getHeight()
                );

                /*
                 * Render only modules which
                 * intersect the visible area.
                 */

                if (
                        moduleY
                                + module.getHeight()
                                >= y + HEADER_HEIGHT
                                &&
                                moduleY
                                        <= y + height
                ) {

                    module.render(
                            graphics,
                            mouseX,
                            mouseY,
                            partialTick
                    );
                }
            }

            currentY +=
                    rowHeight
                            + ROW_GAP;
        }

        /*
         * =========================
         * CONTENT HEIGHT
         * =========================
         */

        int contentHeight =
                rowHeights.isEmpty()
                        ? 0
                        : currentY
                        - contentY
                        - ROW_GAP;

        int viewportHeight =
                Math.max(
                        1,
                        height - HEADER_HEIGHT
                );

        int maxScroll =
                Math.max(
                        0,
                        contentHeight
                                - viewportHeight
                );

        /*
         * Clamp scroll.
         */

        scroll =
                Math.max(
                        0,
                        Math.min(
                                scroll,
                                maxScroll
                        )
                );

        /*
         * =========================
         * SCROLLBAR
         * =========================
         */

        if (maxScroll > 0) {

            drawScrollbar(
                    graphics,
                    viewportHeight,
                    maxScroll
            );
        }
    }

    private boolean matchesSearch(
            ModuleComponent module
    ) {

        if (searchText.isEmpty()) {
            return true;
        }

        return module
                .getModule()
                .getName()
                .toLowerCase(Locale.ROOT)
                .contains(searchText);
    }

    private void drawScrollbar(
            GuiGraphics graphics,
            int viewportHeight,
            int maxScroll
    ) {

        int scrollbarX =
                x + width - 3;

        int trackY =
                y + HEADER_HEIGHT;

        int trackHeight =
                viewportHeight;

        /*
         * Track.
         */

        drawSoftRect(
                graphics,
                scrollbarX,
                trackY,
                3,
                trackHeight,
                2,
                0x181F252C
        );

        /*
         * Thumb.
         */

        int thumbHeight =
                Math.max(
                        24,
                        (int) (
                                (
                                        (double) viewportHeight
                                                / (
                                                viewportHeight
                                                        + maxScroll
                                        )
                                ) * trackHeight
                        )
                );

        double percentage =
                maxScroll <= 0
                        ? 0.0
                        : scroll / maxScroll;

        int thumbY =
                trackY
                        + (int) (
                        (
                                trackHeight
                                        - thumbHeight
                        ) * percentage
                );

        drawSoftRect(
                graphics,
                scrollbarX,
                thumbY,
                3,
                thumbHeight,
                2,
                0xFF3A414B
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (ModuleComponent module : modules) {

            if (!matchesSearch(module)) {
                continue;
            }

            if (
                    module.mouseClicked(
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

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (ModuleComponent module : modules) {

            if (!matchesSearch(module)) {
                continue;
            }

            if (
                    module.mouseReleased(
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

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {

        for (ModuleComponent module : modules) {

            if (!matchesSearch(module)) {
                continue;
            }

            if (
                    module.mouseDragged(
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

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontalAmount,
            double verticalAmount
    ) {

        if (!isHovered(mouseX, mouseY)) {
            return false;
        }

        scroll -=
                (float) (
                        verticalAmount * 18.0
                );

        scroll =
                Math.max(
                        0,
                        scroll
                );

        return true;
    }

    public void setSearchText(
            String searchText
    ) {

        this.searchText =
                searchText == null
                        ? ""
                        : searchText
                        .trim()
                        .toLowerCase(Locale.ROOT);

        scroll = 0;
    }

    public String getSearchText() {
        return searchText;
    }

    public Module.Category getCategory() {
        return category;
    }

    public List<ModuleComponent> getModules() {
        return modules;
    }

    private String formatCategory() {

        if (
                category
                        == Module.Category.WORLD
        ) {
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
}