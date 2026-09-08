package kastdlc.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;
import net.minecraft.network.chat.Style;
import net.minecraft.client.gui.render.RenderPipelines;

public abstract class Component {

    protected int x;
    protected int y;
    protected int width;
    protected int height;

    protected static final int WHITE = 0xFFE9EDF2;
    protected static final int TEXT = 0xFFB7BEC8;
    protected static final int MUTED = 0xFF707985;

    protected static final int PANEL = 0xFF111419;
    protected static final int PANEL_HOVER = 0xFF171B21;
    protected static final int BORDER = 0xFF20252C;
    protected static final int BORDER_HOVER = 0xFF303741;

    protected static final int SETTINGS_BG = 0xF0181B20;
    protected static final int SETTINGS_BORDER = 0xFF292F37;
    protected static final int SETTING_HOVER = 0xFF20252C;

    protected static final Identifier CUSTOM_FONT =
            Identifier.fromNamespaceAndPath(
                    "kastdlc",
                    "captura_now"
            );

    protected Component(
            int x,
            int y,
            int width,
            int height
    ) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    );

    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        return false;
    }

    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {
        return false;
    }

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double deltaX,
            double deltaY
    ) {
        return false;
    }

    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontalAmount,
            double verticalAmount
    ) {
        return false;
    }

    public boolean isHovered(
            double mouseX,
            double mouseY
    ) {
        return mouseX >= x
                && mouseX <= x + width
                && mouseY >= y
                && mouseY <= y + height;
    }

    protected void drawText(
            GuiGraphics graphics,
            net.minecraft.client.gui.Font font,
            String text,
            int x,
            int y,
            int color
    ) {
        Component textComponent =
                Component.literal(text)
                        .withStyle(
                                style ->
                                        style.withFont(
                                                CUSTOM_FONT
                                        )
                        );

        graphics.drawString(
                font,
                textComponent,
                x,
                y,
                color,
                false
        );
    }

    protected void drawRect(
            GuiGraphics graphics,
            int color,
            int x,
            int y,
            int width,
            int height
    ) {
        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                color
        );
    }

    protected void drawBorder(
            GuiGraphics graphics,
            int color,
            int x,
            int y,
            int width,
            int height
    ) {
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

    protected void drawTexture(
            GuiGraphics graphics,
            String name,
            int x,
            int y,
            int width,
            int height
    ) {
        Identifier texture =
                Identifier.fromNamespaceAndPath(
                        "kastdlc",
                        "textures/gui/" + name + ".png"
                );

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                texture,
                x,
                y,
                0.0f,
                0.0f,
                width,
                height,
                128,
                128
        );
    }

    protected String formatNumber(double value) {

        if (value == (long) value) {
            return String.valueOf((long) value);
        }

        return String.format(
                java.util.Locale.US,
                "%.2f",
                value
        );
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setPosition(
            int x,
            int y
    ) {
        this.x = x;
        this.y = y;
    }

    public void setSize(
            int width,
            int height
    ) {
        this.width = width;
        this.height = height;
    }
}