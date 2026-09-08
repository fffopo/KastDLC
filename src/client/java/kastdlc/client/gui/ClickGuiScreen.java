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
import org.joml.Matrix3x2fStack;
import org.lwjgl.glfw.GLFW;

public class ClickGuiScreen extends Screen {

    private static final FontDescription CUSTOM_FONT =
            new FontDescription.Resource(
                    Identifier.fromNamespaceAndPath(
                            "kastdlc",
                            "captura_now"
                    )
            );

    /*
     * =========================
     * SEARCH
     * =========================
     */

    private static final int SEARCH_HEIGHT = 18;
    private static final int SEARCH_COLLAPSED_WIDTH = 18;
    private static final int SEARCH_EXPANDED_WIDTH = 110;

    private static final int SEARCH_OVERLAY_GAP = 4;

    private static final int OVERLAY_WIDTH = 22;
    private static final int OVERLAY_HEIGHT = 18;

    /*
     * =========================
     * ANIMATION
     * =========================
     */

    private static final long ANIMATION_TIME = 260L;
    private static final long SEARCH_ANIMATION_TIME = 180L;

    private long openAnimationStart;
    private long searchAnimationStart;

    private boolean opening = true;
    private boolean searchAnimating;

    /*
     * =========================
     * GUI
     * =========================
     */

    private WindowComponent window;

    private String searchText = "";
    private boolean searchFocused;

    public ClickGuiScreen() {
        super(Component.literal("KastDLC"));

        openAnimationStart = System.currentTimeMillis();
        searchAnimationStart = System.currentTimeMillis();
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

        window.setSearchText(
                searchText
        );

        openAnimationStart =
                System.currentTimeMillis();

        opening = true;
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {

        long now =
                System.currentTimeMillis();

        float openProgress =
                getAnimationProgress(
                        now,
                        openAnimationStart,
                        ANIMATION_TIME
                );

        if (openProgress >= 1.0F) {
            opening = false;
        }

        /*
         * =========================
         * BACKGROUND
         * =========================
         */

        int backgroundAlpha =
                Math.round(
                        150.0F
                                * easeOutCubic(
                                openProgress
                        )
                );

        graphics.fill(
                0,
                0,
                this.width,
                this.height,
                (backgroundAlpha << 24)
                        | 0x000000
        );

        /*
         * =========================
         * WINDOW
         * =========================
         */

        if (window != null) {

            float eased =
                    easeOutBack(
                            openProgress
                    );

            float scale =
                    0.94F
                            + eased * 0.06F;

            Matrix3x2fStack pose =
                    graphics.pose();

            pose.pushMatrix();

            float centerX =
                    this.width / 2.0F;

            float centerY =
                    this.height / 2.0F;

            pose.translate(
                    centerX,
                    centerY
            );

            pose.scale(
                    scale,
                    scale
            );

            pose.translate(
                    -centerX,
                    -centerY
            );

            window.render(
                    graphics,
                    mouseX,
                    mouseY,
                    delta
            );

            pose.popMatrix();
        }

        /*
         * =========================
         * SEARCH
         * =========================
         */

        renderSearch(
                graphics,
                mouseX,
                mouseY,
                now
        );
    }

    private void renderSearch(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            long now
    ) {

        int searchY =
                this.height - 32;

        int totalWidth =
                SEARCH_EXPANDED_WIDTH
                        + SEARCH_OVERLAY_GAP
                        + OVERLAY_WIDTH;

        int searchX =
                this.width / 2
                        - totalWidth / 2;

        /*
         * =========================
         * SEARCH ANIMATION
         * =========================
         */

        float searchProgress;

        if (searchFocused) {

            searchProgress =
                    getAnimationProgress(
                            now,
                            searchAnimationStart,
                            SEARCH_ANIMATION_TIME
                    );

        } else {

            searchProgress =
                    1.0F
                            - getAnimationProgress(
                            now,
                            searchAnimationStart,
                            SEARCH_ANIMATION_TIME
                    );
        }

        searchProgress =
                easeOutCubic(
                        searchProgress
                );

        int searchWidth =
                Math.round(
                        SEARCH_COLLAPSED_WIDTH
                                + (
                                SEARCH_EXPANDED_WIDTH
                                        - SEARCH_COLLAPSED_WIDTH
                        ) * searchProgress
                );

        /*
         * =========================
         * SEARCH HOVER
         * =========================
         */

        boolean searchHovered =
                mouseX >= searchX
                        && mouseX <= searchX + searchWidth
                        && mouseY >= searchY
                        && mouseY <= searchY + SEARCH_HEIGHT;

        int searchColor;

        if (searchFocused) {

            searchColor =
                    0xF0181C22;

        } else if (searchHovered) {

            searchColor =
                    0xE014181E;

        } else {

            searchColor =
                    0xD811151A;
        }

        RoundedRectRenderer.draw(
                graphics,
                searchX,
                searchY,
                searchWidth,
                SEARCH_HEIGHT,
                6.0F,
                searchColor
        );

        /*
         * =========================
         * SEARCH CONTENT
         * =========================
         */

        if (searchWidth > 40) {

            String text =
                    searchText.isEmpty()
                            ? "Search..."
                            : searchText;

            int textColor =
                    searchText.isEmpty()
                            ? 0xFF707985
                            : 0xFFE9EDF2;

            drawCustomText(
                    graphics,
                    text,
                    searchX + 8,
                    searchY + 5,
                    textColor
            );

        } else {

            drawCustomText(
                    graphics,
                    "G",
                    searchX + 6,
                    searchY + 4,
                    searchHovered
                            ? 0xFFE9EDF2
                            : 0xFFB7BEC8
            );
        }

        /*
         * =========================
         * OVERLAY BUTTON
         * =========================
         */

        int overlayX =
                searchX
                        + searchWidth
                        + SEARCH_OVERLAY_GAP;

        boolean overlayHovered =
                mouseX >= overlayX
                        && mouseX <= overlayX + OVERLAY_WIDTH
                        && mouseY >= searchY
                        && mouseY <= searchY + OVERLAY_HEIGHT;

        int overlayColor =
                overlayHovered
                        ? 0xFF181D23
                        : 0xD811151A;

        RoundedRectRenderer.draw(
                graphics,
                overlayX,
                searchY,
                OVERLAY_WIDTH,
                OVERLAY_HEIGHT,
                5.0F,
                overlayColor
        );

        String overlaySymbol =
                window != null
                        && window.isOverlayOnly()
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

    /*
     * =========================
     * MOUSE
     * =========================
     */

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

        int searchY =
                this.height - 32;

        int totalWidth =
                SEARCH_EXPANDED_WIDTH
                        + SEARCH_OVERLAY_GAP
                        + OVERLAY_WIDTH;

        int searchX =
                this.width / 2
                        - totalWidth / 2;

        int searchWidth =
                searchFocused
                        ? SEARCH_EXPANDED_WIDTH
                        : SEARCH_COLLAPSED_WIDTH;

        /*
         * SEARCH
         */

        if (
                mouseX >= searchX
                        && mouseX <= searchX + searchWidth
                        && mouseY >= searchY
                        && mouseY <= searchY + SEARCH_HEIGHT
        ) {

            if (button == 0) {

                if (!searchFocused) {

                    searchFocused = true;

                    searchAnimationStart =
                            System.currentTimeMillis();

                }

                return true;
            }
        }

        /*
         * OVERLAY
         */

        int overlayX =
                searchX
                        + searchWidth
                        + SEARCH_OVERLAY_GAP;

        if (
                mouseX >= overlayX
                        && mouseX <= overlayX + OVERLAY_WIDTH
                        && mouseY >= searchY
                        && mouseY <= searchY + OVERLAY_HEIGHT
        ) {

            if (
                    button == 0
                            && window != null
            ) {

                window.toggleOverlay();

                return true;
            }
        }

        /*
         * WINDOW
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

        return super.mouseReleased(
                click
        );
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

    /*
     * =========================
     * KEYBOARD
     * =========================
     */

    @Override
    public boolean keyPressed(
            KeyEvent input
    ) {

        int key =
                input.key();

        /*
         * SEARCH ESCAPE
         */

        if (searchFocused) {

            if (
                    key
                            == GLFW.GLFW_KEY_ESCAPE
            ) {

                searchFocused = false;

                searchAnimationStart =
                        System.currentTimeMillis();

                return true;
            }

            /*
             * BACKSPACE
             */

            if (
                    key
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

                    if (window != null) {

                        window.setSearchText(
                                searchText
                        );
                    }
                }

                return true;
            }
        }

        /*
         * CLOSE GUI
         */

        if (
                key
                        == GLFW.GLFW_KEY_ESCAPE
        ) {

            onClose();

            return true;
        }

        return super.keyPressed(
                input
        );
    }

    @Override
    public boolean charTyped(
            CharacterEvent input
    ) {

        if (!searchFocused) {

            return super.charTyped(
                    input
            );
        }

        int codepoint =
                input.codepoint();

        if (codepoint <= 0) {
            return false;
        }

        if (
                Character.isISOControl(
                        codepoint
                )
        ) {
            return false;
        }

        String character =
                new String(
                        Character.toChars(
                                codepoint
                        )
                );

        if (character.isEmpty()) {
            return false;
        }

        searchText += character;

        if (window != null) {

            window.setSearchText(
                    searchText
            );
        }

        return true;
    }

    /*
     * =========================
     * RESIZE
     * =========================
     */

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

    /*
     * =========================
     * SCREEN
     * =========================
     */

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    /*
     * =========================
     * TEXT
     * =========================
     */

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

    /*
     * =========================
     * ANIMATION HELPERS
     * =========================
     */

    private float getAnimationProgress(
            long now,
            long start,
            long duration
    ) {

        if (duration <= 0L) {
            return 1.0F;
        }

        float progress =
                (now - start)
                        / (float) duration;

        return Math.max(
                0.0F,
                Math.min(
                        1.0F,
                        progress
                )
        );
    }

    private float easeOutCubic(
            float value
    ) {

        value =
                Math.max(
                        0.0F,
                        Math.min(
                                1.0F,
                                value
                        )
                );

        float inverse =
                1.0F - value;

        return 1.0F
                - inverse
                * inverse
                * inverse;
    }

    private float easeOutBack(
            float value
    ) {

        value =
                Math.max(
                        0.0F,
                        Math.min(
                                1.0F,
                                value
                        )
                );

        float c1 =
                1.70158F;

        float c3 =
                c1 + 1.0F;

        float inverse =
                value - 1.0F;

        return 1.0F
                + c3
                * inverse
                * inverse
                * inverse
                + c1
                * inverse
                * inverse;
    }
}
