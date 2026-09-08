package kastdlc.client.gui;

import kastdlc.client.gui.component.WindowComponent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class ClickGuiScreen extends Screen {

    private static final FontDescription CUSTOM_FONT =
            new FontDescription.Resource(
                    Identifier.fromNamespaceAndPath(
                            "kastdlc",
                            "captura_now"
                    )
            );

    private static final int SEARCH_HEIGHT = 18;
    private static final int SEARCH_ICON_WIDTH = 18;
    private static final int SEARCH_WIDTH = 110;

    private static final int OVERLAY_WIDTH = 22;
    private static final int OVERLAY_HEIGHT = 18;

    private WindowComponent window;

    private String searchText = "";
    private boolean searchFocused;

    public ClickGuiScreen() {
        super(Component.literal("KastDLC"));
    }

    @Override
    protected void init() {

        window = new WindowComponent(
                this.font,
                0,
                0,
                this.width,
                this.height
        );

        window.resize(
                this.width,
                this.height
        );
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {

        /*
         * Background
         */

        graphics.fill(
                0,
                0,
                this.width,
                this.height,
                0xB8000000
        );

        /*
         * Main GUI
         */

        if (window != null) {

            window.render(
                    graphics,
                    mouseX,
                    mouseY,
                    delta
            );
        }

        /*
         * Bottom search / overlay controls
         */

        renderSearch(
                graphics,
                mouseX,
                mouseY
        );
    }

    private void renderSearch(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        int searchY =
                this.height - 32;

        int totalWidth =
                SEARCH_WIDTH
                        + OVERLAY_WIDTH
                        + 4;

        int searchX =
                this.width / 2
                        - totalWidth / 2;

        int searchWidth =
                searchFocused
                        ? SEARCH_WIDTH
                        : SEARCH_ICON_WIDTH;

        boolean searchHovered =
                mouseX >= searchX
                        && mouseX <= searchX + searchWidth
                        && mouseY >= searchY
                        && mouseY <= searchY + SEARCH_HEIGHT;

        /*
         * Search background
         */

        drawSoftRect(
                graphics,
                searchX,
                searchY,
                searchWidth,
                SEARCH_HEIGHT,
                6,
                searchFocused
                        ? 0xF0181C22
                        : searchHovered
                        ? 0xE014181E
                        : 0xD811151A
        );

        /*
         * Search text / icon
         */

        if (searchFocused) {

            String text =
                    searchText.isEmpty()
                            ? "Search..."
                            : searchText;

            drawCustomText(
                    graphics,
                    text,
                    searchX + 8,
                    searchY + 5,
                    searchText.isEmpty()
                            ? 0xFF707985
                            : 0xFFE9EDF2
            );

        } else {

            drawCustomText(
                    graphics,
                    "G",
                    searchX + 6,
                    searchY + 4,
                    0xFFB7BEC8
            );
        }

        /*
         * Overlay button
         */

        int overlayX =
                searchX
                        + searchWidth
                        + 4;

        boolean overlayHovered =
                mouseX >= overlayX
                        && mouseX <= overlayX + OVERLAY_WIDTH
                        && mouseY >= searchY
                        && mouseY <= searchY + OVERLAY_HEIGHT;

        drawSoftRect(
                graphics,
                overlayX,
                searchY,
                OVERLAY_WIDTH,
                OVERLAY_HEIGHT,
                5,
                overlayHovered
                        ? 0xFF181D23
                        : 0xD811151A
        );

        String overlaySymbol =
                window != null && window.isOverlayOnly()
                        ? "<"
                        : ">";

        drawCustomText(
                graphics,
                overlaySymbol,
                overlayX + 7,
                searchY + 4,
                overlayHovered
                        ? 0xFFE9EDF2
                        : 0xFF707985
        );
    }

    @Override
    public boolean mouseClicked(
            MouseButtonEvent click,
            boolean doubleClick
    ) {

        double mouseX =
                click.x();

        double mouseY =
                click.y();

        int button =
                click.button();

        /*
         * Search
         */

        int searchY =
                this.height - 32;

        int totalWidth =
                SEARCH_WIDTH
                        + OVERLAY_WIDTH
                        + 4;

        int searchX =
                this.width / 2
                        - totalWidth / 2;

        int searchWidth =
                searchFocused
                        ? SEARCH_WIDTH
                        : SEARCH_ICON_WIDTH;

        if (
                mouseX >= searchX
                        && mouseX <= searchX + searchWidth
                        && mouseY >= searchY
                        && mouseY <= searchY + SEARCH_HEIGHT
        ) {

            if (button == 0) {

                searchFocused = true;

                return true;
            }
        }

        /*
         * Overlay
         */

        int overlayX =
                searchX
                        + searchWidth
                        + 4;

        if (
                mouseX >= overlayX
                        && mouseX <= overlayX + OVERLAY_WIDTH
                        && mouseY >= searchY
                        && mouseY <= searchY + OVERLAY_HEIGHT
        ) {

            if (button == 0 && window != null) {

                window.toggleOverlay();

                return true;
            }
        }

        /*
         * Main GUI
         */

        if (window != null) {

            if (
                    window.mouseClicked(
                            mouseX,
                            mouseY,
                            button
                    )
            ) {
                return true;
            }
        }

        return super.mouseClicked(
                click,
                doubleClick
        );
    }

    @Override
    public boolean mouseReleased(
            MouseButtonEvent click
    ) {

        if (window != null) {

            if (
                    window.mouseReleased(
                            click.x(),
                            click.y(),
                            click.button()
                    )
            ) {
                return true;
            }
        }

        return super.mouseReleased(click);
    }

    @Override
    public boolean mouseDragged(
            MouseButtonEvent click,
            double offsetX,
            double offsetY
    ) {

        if (window != null) {

            if (
                    window.mouseDragged(
                            click.x(),
                            click.y(),
                            click.button(),
                            offsetX,
                            offsetY
                    )
            ) {
                return true;
            }
        }

        return super.mouseDragged(
                click,
                offsetX,
                offsetY
        );
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontalAmount,
            double verticalAmount
    ) {

        if (window != null) {

            if (
                    window.mouseScrolled(
                            mouseX,
                            mouseY,
                            horizontalAmount,
                            verticalAmount
                    )
            ) {
                return true;
            }
        }

        return super.mouseScrolled(
                mouseX,
                mouseY,
                horizontalAmount,
                verticalAmount
        );
    }

    @Override
    public boolean keyPressed(
            KeyEvent input
    ) {

        if (searchFocused) {

            /*
             * Close search
             */

            if (
                    input.key()
                            == GLFW.GLFW_KEY_ESCAPE
            ) {

                searchFocused = false;

                return true;
            }

            /*
             * Backspace
             */

            if (
                    input.key()
                            == GLFW.GLFW_KEY_BACKSPACE
            ) {

                if (!searchText.isEmpty()) {

                    int length =
                            searchText.length();

                    searchText =
                            searchText.substring(
                                    0,
                                    length - 1
                            );
                }

                return true;
            }
        }

        /*
         * Close GUI
         */

        if (
                input.key()
                        == GLFW.GLFW_KEY_ESCAPE
        ) {

            onClose();

            return true;
        }

        return super.keyPressed(input);
    }

    @Override
    public boolean charTyped(
            CharacterEvent input
    ) {

        if (!searchFocused) {
            return super.charTyped(input);
        }

        int codepoint =
                input.codepoint();

        if (codepoint <= 0) {
            return false;
        }

        String character =
                new String(
                        Character.toChars(codepoint)
                );

        if (
                !character.isEmpty()
                        && !Character.isISOControl(
                        codepoint
                )
        ) {

            searchText += character;

            return true;
        }

        return false;
    }

    @Override
    public void resize(
            int width,
            int height
    ) {

        super.resize(
                width,
                height
        );

        if (window != null) {

            window.resize(
                    width,
                    height
            );
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void drawCustomText(
            GuiGraphics graphics,
            String text,
            int x,
            int y,
            int color
    ) {

        Component component =
                Component.literal(
                        text
                ).withStyle(
                        style ->
                                style.withFont(
                                        CUSTOM_FONT
                                )
                );

        graphics.drawString(
                this.font,
                component,
                x,
                y,
                color,
                false
        );
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