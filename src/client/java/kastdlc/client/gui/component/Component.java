package kastdlc.client.gui.component;

import kastdlc.client.gui.RoundedRectRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;

public abstract class Component {

    protected static final int WHITE = 0xFFE9EDF2;
    protected static final int TEXT = 0xFFE9EDF2;
    protected static final int MUTED = 0xFF707985;

    protected int x;
    protected int y;
    protected int width;
    protected int height;

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

    public boolean isInside(
            double mouseX,
            double mouseY
    ) {
        return isHovered(
                mouseX,
                mouseY
        );
    }

    public int getHeight() {
        return height;
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
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

    public void setBounds(
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

    protected void drawSoftRect(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            int radius,
            int color
    ) {
        RoundedRectRenderer.draw(
                graphics,
                x,
                y,
                width,
                height,
                radius,
                color
        );
    }

    protected void drawSoftRect(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            float radius,
            int color
    ) {
        RoundedRectRenderer.draw(
                graphics,
                x,
                y,
                width,
                height,
                radius,
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
        RoundedRectRenderer.drawBorder(
                graphics,
                x,
                y,
                width,
                height,
                8.0F,
                color,
                0x00000000
        );
    }

    protected void drawBorder(
            GuiGraphics graphics,
            int x,
            int y,
            int width,
            int height,
            float radius,
            int borderColor,
            int fillColor
    ) {
        RoundedRectRenderer.drawBorder(
                graphics,
                x,
                y,
                width,
                height,
                radius,
                borderColor,
                fillColor
        );
    }

    protected void drawText(
            GuiGraphics graphics,
            Font font,
            String text,
            int x,
            int y,
            int color
    ) {
        if (
                graphics == null
                        || font == null
                        || text == null
                        || text.isEmpty()
        ) {
            return;
        }

        graphics.drawString(
                font,
                net.minecraft.network.chat.Component.literal(
                        text
                ),
                x,
                y,
                color,
                false
        );
    }

    protected int textWidth(
            Font font,
            String text
    ) {
        if (
                font == null
                        || text == null
        ) {
            return 0;
        }

        return font.width(
                net.minecraft.network.chat.Component.literal(
                        text
                )
        );
    }

    protected void drawTexture(
            GuiGraphics graphics,
            String texture,
            int x,
            int y,
            int width,
            int height
    ) {
        if (
                graphics == null
                        || texture == null
                        || texture.isEmpty()
                        || width <= 0
                        || height <= 0
        ) {
            return;
        }

        Identifier identifier =
                Identifier.fromNamespaceAndPath(
                        "kastdlc",
                        "textures/gui/" + texture + ".png"
                );

        graphics.blit(
                identifier,
                x,
                y,
                0,
                0,
                width,
                height,
                width,
                height
        );
    }

    protected String formatNumber(
            double value
    ) {
        if (value == (long) value) {
            return String.valueOf(
                    (long) value
            );
        }

        return String.format(
                java.util.Locale.US,
                "%.2f",
                value
        );
    }

    public void tick() {
    }

    public boolean isVisible() {
        return true;
    }
}