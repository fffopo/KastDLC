package kastdlc.client.gui.component;

import kastdlc.client.module.Module;
import kastdlc.client.module.settings.BooleanSetting;
import kastdlc.client.module.settings.ModeSetting;
import kastdlc.client.module.settings.NumberSetting;
import kastdlc.client.module.settings.Setting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

public class ModuleComponent extends Component {

    private static final int BASE_HEIGHT = 58;

    private static final int ICON_SIZE = 42;
    private static final int ICON_MARGIN = 8;

    private static final int SETTING_HEIGHT = 30;
    private static final int SETTING_GAP = 5;

    private static final int TOGGLE_WIDTH = 24;
    private static final int TOGGLE_HEIGHT = 12;

    private final Font font;
    private final Module module;

    private boolean expanded;

    private boolean draggingSlider;

    public ModuleComponent(
            Font font,
            Module module,
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
        this.module = module;
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        boolean hovered =
                isHovered(
                        mouseX,
                        mouseY
                );

        /*
         * Main card.
         */

        drawSoftRect(
                graphics,
                x,
                y,
                width,
                BASE_HEIGHT,
                8,
                module.isEnabled()
                        ? 0xFF151A20
                        : hovered
                        ? 0xFF14181D
                        : 0xFF111419
        );

        /*
         * Subtle border.
         */

        drawBorder(
                graphics,
                module.isEnabled()
                        ? 0xFF2C333C
                        : hovered
                        ? 0xFF262D35
                        : 0xFF1D2229,
                x,
                y,
                width,
                BASE_HEIGHT
        );

        /*
         * Icon container.
         */

        drawSoftRect(
                graphics,
                x + ICON_MARGIN,
                y + 8,
                ICON_SIZE,
                ICON_SIZE,
                7,
                module.isEnabled()
                        ? 0xFF20262D
                        : 0xFF191E24
        );

        /*
         * Module icon.
         */

        String icon =
                getIconName();

        if (icon != null) {

            drawTexture(
                    graphics,
                    icon,
                    x + ICON_MARGIN + 13,
                    y + 21,
                    16,
                    16
            );
        }

        /*
         * Module name.
         */

        drawText(
                graphics,
                font,
                module.getName(),
                x + 58,
                y + 18,
                module.isEnabled()
                        ? WHITE
                        : TEXT
        );

        /*
         * Category label.
         */

        drawText(
                graphics,
                font,
                formatCategory(),
                x + 58,
                y + 34,
                MUTED
        );

        /*
         * Settings indicator.
         */

        if (!module.getSettings().isEmpty()) {

            drawSettingsIcon(
                    graphics,
                    x + width - 48,
                    y + 21,
                    hovered
            );
        }

        /*
         * Toggle.
         */

        int toggleX =
                x + width - TOGGLE_WIDTH - 10;

        int toggleY =
                y + 23;

        drawToggle(
                graphics,
                toggleX,
                toggleY
        );

        /*
         * Expanded settings.
         */

        if (expanded) {

            renderSettings(
                    graphics,
                    mouseX,
                    mouseY,
                    partialTick
            );
        }
    }

    private void renderSettings(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        List<Setting<?>> settings =
                module.getSettings();

        if (settings.isEmpty()) {
            return;
        }

        int settingsX =
                x;

        int settingsY =
                y + BASE_HEIGHT + 5;

        int settingsWidth =
                width;

        int settingsHeight =
                getSettingsHeight();

        /*
         * Settings background.
         */

        drawSoftRect(
                graphics,
                settingsX,
                settingsY,
                settingsWidth,
                settingsHeight,
                8,
                SETTINGS_BG
        );

        drawBorder(
                graphics,
                SETTINGS_BORDER,
                settingsX,
                settingsY,
                settingsWidth,
                settingsHeight
        );

        int currentY =
                settingsY + 7;

        for (
                Setting<?> setting
                : settings
        ) {

            renderSetting(
                    graphics,
                    setting,
                    currentY,
                    mouseX,
                    mouseY
            );

            currentY +=
                    SETTING_HEIGHT
                            + SETTING_GAP;
        }
    }

    private void renderSetting(
            GuiGraphics graphics,
            Setting<?> setting,
            int settingY,
            int mouseX,
            int mouseY
    ) {

        boolean hovered =
                mouseX >= x
                        && mouseX <= x + width
                        && mouseY >= settingY
                        && mouseY <= settingY + SETTING_HEIGHT;

        if (hovered) {

            drawSoftRect(
                    graphics,
                    x + 5,
                    settingY,
                    width - 10,
                    SETTING_HEIGHT,
                    5,
                    SETTING_HOVER
            );
        }

        drawText(
                graphics,
                font,
                setting.getName(),
                x + 11,
                settingY + 10,
                TEXT
        );

        /*
         * Boolean
         */

        if (setting instanceof BooleanSetting booleanSetting) {

            int toggleX =
                    x + width - TOGGLE_WIDTH - 11;

            int toggleY =
                    settingY + 9;

            drawToggle(
                    graphics,
                    toggleX,
                    toggleY,
                    booleanSetting.isEnabled()
            );

            return;
        }

        /*
         * Mode
         */

        if (setting instanceof ModeSetting modeSetting) {

            String mode =
                    modeSetting.getMode();

            int rightX =
                    x + width - 10;

            int textWidth =
                    font.width(
                            net.minecraft.network.chat.Component.literal(
                                    mode
                            )
                    );

            drawText(
                    graphics,
                    font,
                    "<",
                    rightX - textWidth - 19,
                    settingY + 9,
                    MUTED
            );

            drawText(
                    graphics,
                    font,
                    mode,
                    rightX - textWidth,
                    settingY + 9,
                    WHITE
            );

            drawText(
                    graphics,
                    font,
                    ">",
                    rightX + 2,
                    settingY + 9,
                    MUTED
            );

            return;
        }

        /*
         * Number
         */

        if (setting instanceof NumberSetting numberSetting) {

            drawSlider(
                    graphics,
                    numberSetting,
                    settingY,
                    mouseX
            );
        }
    }

    private void drawSlider(
            GuiGraphics graphics,
            NumberSetting setting,
            int settingY,
            int mouseX
    ) {

        int sliderX =
                x + 10;

        int sliderY =
                settingY + 20;

        int sliderWidth =
                width - 20;

        int sliderHeight =
                3;

        double min =
                setting.getMin();

        double max =
                setting.getMax();

        double value =
                setting.getValue();

        double percentage =
                max <= min
                        ? 0.0
                        : (
                        value - min
                ) / (
                        max - min
                );

        percentage =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                percentage
                        )
                );

        /*
         * Track.
         */

        drawSoftRect(
                graphics,
                sliderX,
                sliderY,
                sliderWidth,
                sliderHeight,
                2,
                0xFF292F37
        );

        /*
         * Progress.
         */

        int progressWidth =
                (int) (
                        sliderWidth
                                * percentage
                );

        if (progressWidth > 0) {

            drawSoftRect(
                    graphics,
                    sliderX,
                    sliderY,
                    progressWidth,
                    sliderHeight,
                    2,
                    0xFFB7BEC8
            );
        }

        /*
         * Value.
         */

        String valueText =
                formatNumber(value);

        drawText(
                graphics,
                font,
                valueText,
                x + width
                        - font.width(
                        net.minecraft.network.chat.Component.literal(
                                valueText
                        )
                )
                        - 11,
                settingY + 6,
                WHITE
        );

        /*
         * Slider knob.
         */

        int knobX =
                sliderX
                        + (int) (
                        sliderWidth
                                * percentage
                );

        drawSoftRect(
                graphics,
                knobX - 3,
                sliderY - 2,
                6,
                7,
                3,
                WHITE
        );
    }

    private void drawToggle(
            GuiGraphics graphics,
            int x,
            int y
    ) {

        drawToggle(
                graphics,
                x,
                y,
                module.isEnabled()
        );
    }

    private void drawToggle(
            GuiGraphics graphics,
            int x,
            int y,
            boolean enabled
    ) {

        drawSoftRect(
                graphics,
                x,
                y,
                TOGGLE_WIDTH,
                TOGGLE_HEIGHT,
                6,
                enabled
                        ? 0xFFE9EDF2
                        : 0xFF292F37
        );

        int knobSize = 8;

        int knobX =
                enabled
                        ? x + TOGGLE_WIDTH - knobSize - 2
                        : x + 2;

        int knobY =
                y + 2;

        drawSoftRect(
                graphics,
                knobX,
                knobY,
                knobSize,
                knobSize,
                4,
                enabled
                        ? 0xFF111419
                        : 0xFF707985
        );
    }

    private void drawSettingsIcon(
            GuiGraphics graphics,
            int x,
            int y,
            boolean hovered
    ) {

        int color =
                hovered
                        ? WHITE
                        : MUTED;

        /*
         * Minimal three-line settings glyph.
         */

        graphics.fill(
                x,
                y,
                x + 12,
                y + 1,
                color
        );

        graphics.fill(
                x,
                y + 5,
                x + 12,
                y + 6,
                color
        );

        graphics.fill(
                x,
                y + 10,
                x + 12,
                y + 11,
                color
        );

        graphics.fill(
                x + 3,
                y - 1,
                x + 5,
                y + 2,
                color
        );

        graphics.fill(
                x + 8,
                y + 4,
                x + 10,
                y + 7,
                color
        );

        graphics.fill(
                x + 5,
                y + 9,
                x + 7,
                y + 12,
                color
        );
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {

        /*
         * Main card.
         */

        if (
                mouseX >= x
                        && mouseX <= x + width
                        && mouseY >= y
                        && mouseY <= y + BASE_HEIGHT
        ) {

            /*
             * Left click = toggle.
             */

            if (button == 0) {

                int toggleX =
                        x + width
                                - TOGGLE_WIDTH
                                - 10;

                int toggleY =
                        y + 23;

                if (
                        mouseX >= toggleX
                                && mouseX <= toggleX
                                + TOGGLE_WIDTH
                                && mouseY >= toggleY
                                && mouseY <= toggleY
                                + TOGGLE_HEIGHT
                ) {

                    module.toggle();

                    return true;
                }

                /*
                 * Settings area.
                 */

                if (
                        !module.getSettings().isEmpty()
                                &&
                                mouseX >= x + width - 58
                ) {

                    expanded = !expanded;

                    return true;
                }

                /*
                 * Clicking the card itself also toggles.
                 */

                module.toggle();

                return true;
            }

            /*
             * Right click = expand settings.
             */

            if (
                    button == 1
                            && !module.getSettings().isEmpty()
            ) {

                expanded = !expanded;

                return true;
            }
        }

        /*
         * Settings.
         */

        if (expanded) {

            return clickSettings(
                    mouseX,
                    mouseY,
                    button
            );
        }

        return false;
    }

    private boolean clickSettings(
            double mouseX,
            double mouseY,
            int button
    ) {

        int settingsY =
                y + BASE_HEIGHT + 5;

        List<Setting<?>> settings =
                module.getSettings();

        for (
                Setting<?> setting
                : settings
        ) {

            if (
                    mouseY < settingsY
                            ||
                            mouseY > settingsY
                                    + SETTING_HEIGHT
            ) {

                settingsY +=
                        SETTING_HEIGHT
                                + SETTING_GAP;

                continue;
            }

            /*
             * Boolean.
             */

            if (
                    setting instanceof BooleanSetting
                            booleanSetting
            ) {

                if (button == 0) {

                    booleanSetting.toggle();

                    return true;
                }
            }

            /*
             * Mode.
             */

            if (
                    setting instanceof ModeSetting
                            modeSetting
            ) {

                if (button == 0) {

                    modeSetting.cycle();

                    return true;
                }

                if (button == 1) {

                    cycleModeBackwards(
                            modeSetting
                    );

                    return true;
                }
            }

            /*
             * Number.
             */

            if (
                    setting instanceof NumberSetting
                            numberSetting
            ) {

                if (
                        button == 0
                                &&
                                mouseY >= settingsY + 16
                ) {

                    setSliderValue(
                            numberSetting,
                            mouseX
                    );

                    draggingSlider = true;

                    return true;
                }
            }

            return false;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button == 0) {
            draggingSlider = false;
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

        if (
                !draggingSlider
                        || button != 0
                        || !expanded
        ) {
            return false;
        }

        int settingsY =
                y + BASE_HEIGHT + 5;

        for (
                Setting<?> setting
                : module.getSettings()
        ) {

            if (
                    setting instanceof NumberSetting
                            numberSetting
            ) {

                if (
                        mouseY >= settingsY
                                &&
                                mouseY <= settingsY
                                        + SETTING_HEIGHT
                ) {

                    setSliderValue(
                            numberSetting,
                            mouseX
                    );

                    return true;
                }
            }

            settingsY +=
                    SETTING_HEIGHT
                            + SETTING_GAP;
        }

        return false;
    }

    private void setSliderValue(
            NumberSetting setting,
            double mouseX
    ) {

        double sliderX =
                x + 10;

        double sliderWidth =
                width - 20;

        double percentage =
                (
                        mouseX - sliderX
                ) / sliderWidth;

        percentage =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                percentage
                        )
                );

        double value =
                setting.getMin()
                        + (
                        setting.getMax()
                                - setting.getMin()
                ) * percentage;

        setting.setValue(value);
    }

    private void cycleModeBackwards(
            ModeSetting setting
    ) {

        List<String> modes =
                setting.getModes();

        if (modes.isEmpty()) {
            return;
        }

        int index =
                modes.indexOf(
                        setting.getMode()
                );

        if (index <= 0) {

            setting.setMode(
                    modes.get(
                            modes.size() - 1
                    )
            );

            return;
        }

        setting.setMode(
                modes.get(index - 1)
        );
    }

    public int getSettingsHeight() {

        if (!expanded) {
            return 0;
        }

        int count =
                module.getSettings().size();

        if (count == 0) {
            return 0;
        }

        return 14
                + count * SETTING_HEIGHT
                + Math.max(
                0,
                count - 1
                        * SETTING_GAP
        );
    }

    @Override
    public int getHeight() {

        return BASE_HEIGHT
                + (
                expanded
                        ? getSettingsHeight()
                        + 5
                        : 0
        );
    }

    public Module getModule() {
        return module;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(
            boolean expanded
    ) {
        this.expanded = expanded;
    }

    private String formatCategory() {

        if (
                module.getCategory()
                        == Module.Category.WORLD
        ) {
            return "Utilities";
        }

        String value =
                module.getCategory()
                        .name()
                        .toLowerCase();

        return value.substring(
                0,
                1
        ).toUpperCase()
                + value.substring(1);
    }

    private String getIconName() {

        return switch (
                module.getCategory()
                ) {

            case COMBAT ->
                    "combat";

            case MOVEMENT ->
                    "movement";

            case RENDER ->
                    "render";

            case PLAYER ->
                    "player";

            case WORLD ->
                    "utilities";
        };
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