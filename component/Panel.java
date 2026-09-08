package kastdlc.client.gui.component;

import kastdlc.client.KastDLCClient;
import kastdlc.client.module.Module;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;
import java.util.List;

public class Panel extends Component {

    private final Font font;
    private final Module.Category category;

    private final List<ModuleComponent> modules =
            new ArrayList<>();

    private float scroll;

    public Panel(
            Font font,
            Module.Category category,
            int x,
            int y,
            int width,
            int height
    ) {
        super(x, y, width, height);

        this.font = font;
        this.category = category;

        rebuild();
    }

    private void rebuild() {

        modules.clear();

        for (Module module :
                KastDLCClient.MODULE_MANAGER
                        .getModules(category)) {

            modules.add(
                    new ModuleComponent(
                            font,
                            module,
                            x + 20,
                            y + 20,
                            230,
                            78
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

        drawRect(
                graphics,
                PANEL,
                x,
                y,
                width,
                height
        );

        drawText(
                graphics,
                font,
                category.name(),
                x + 20,
                y + 20,
                WHITE
        );

        int currentY =
                y + 45 + (int) scroll;

        for (ModuleComponent module :
                modules) {

            module.setPosition(
                    x + 20,
                    currentY
            );

            module.render(
                    graphics,
                    mouseX,
                    mouseY,
                    partialTick
            );

            currentY +=
                    module.getHeight() + 8;
        }
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        for (ModuleComponent module :
                modules) {

            if (module.mouseClicked(
                    mouseX,
                    mouseY,
                    button
            )) {
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

        for (ModuleComponent module :
                modules) {

            if (module.mouseReleased(
                    mouseX,
                    mouseY,
                    button
            )) {
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

        for (ModuleComponent module :
                modules) {

            if (module.mouseDragged(
                    mouseX,
                    mouseY,
                    button,
                    deltaX,
                    deltaY
            )) {
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

        scroll +=
                (float) (verticalAmount * 18.0);

        scroll =
                Math.min(
                        0,
                        scroll
                );

        return true;
    }

    public Module.Category getCategory() {
        return category;
    }

    public List<ModuleComponent> getModules() {
        return modules;
    }
}