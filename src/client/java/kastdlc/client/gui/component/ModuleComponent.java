package kastdlc.client.gui.component;

import kastdlc.client.module.Module;
import kastdlc.client.module.settings.BooleanSetting;
import kastdlc.client.module.settings.ModeSetting;
import kastdlc.client.module.settings.NumberSetting;
import kastdlc.client.module.settings.Setting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.Locale;

public class ModuleComponent extends Component {

    private static final int BASE_HEIGHT = 58;

    private static final int CARD_RADIUS = 9;

    private static final int LEFT_PADDING = 14;

    private static final int TITLE_Y = 15;
    private static final int SUBTITLE_Y = 34;

    private static final int TOGGLE_WIDTH = 25;
    private static final int TOGGLE_HEIGHT = 13;

    private static final int TOGGLE_KNOB = 9;

    private static final int SETTINGS_MARGIN = 6;
    private static final int SETTINGS_PADDING = 7;

    private static final int SETTING_HEIGHT = 30;
    private static final int SETTING_GAP = 5;

    private static final int CARD_COLOR = 0xFF111419;
    private static final int CARD_HOVER_COLOR = 0xFF171B21;
    private static final int CARD_ENABLED_COLOR = 0xFF151A20;

    private static final int TITLE_COLOR = 0xFFE9EDF2;
    private static final int SUBTITLE_COLOR = 0xFF707985;

    private static final int TOGGLE_OFF = 0xFF272D35;
    private static final int TOGGLE_ON = 0xFFE9EDF2;

    private static final int KNOB_OFF = 0xFF707985;
    private static final int KNOB_ON = 0xFF111419;

    private static final int SETTINGS_BACKGROUND = 0xF0181B20;
    private static final int SETTINGS_BORDER = 0xFF292F37;
    private static final int SETTING_BACKGROUND = 0xFF181C22;
    private static final int SETTING_HOVER_BACKGROUND = 0xFF20252C;

    private static final int SETTING_TEXT = 0xFFE9EDF2;
    private static final int SETTING_MUTED = 0xFF8B949F;

    private static final int SLIDER_BACKGROUND = 0xFF292F37;
    private static final int SLIDER_FILL = 0xFFE9EDF2;

    private static final int SETTING_RADIUS = 7;

    private final Font font;
    private final Module module;

    private boolean expanded;

    private boolean draggingNumber;

    private NumberSetting draggingSetting;

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

        updateHeight();
    }

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        updateHeight();

        boolean hovered =
                isHovered(
                        mouseX,
                        mouseY
                );

        int cardColor;

        if (module.isEnabled()) {
            cardColor = CARD_ENABLED_COLOR;
        } else if (hovered) {
            cardColor = CARD_HOVER_COLOR;
        } else {
            cardColor = CARD_COLOR;
        }

        drawSoftRect(
                graphics,
                x,
                y,
                width,
                BASE_HEIGHT,
                CARD_RADIUS,
                cardColor
        );

        drawModuleName(
                graphics
        );

        drawModuleCategory(
                graphics
        );

        drawToggle(
                graphics
        );

        if (expanded && !module.getSettings().isEmpty()) {
            renderSettings(
                    graphics,
                    mouseX,
                    mouseY
            );
        }
    }

    private void drawModuleName(
            GuiGraphics graphics
    ) {
        drawText(
                graphics,
                font,
                module.getName(),
                x + LEFT_PADDING,
                y + TITLE_Y,
                TITLE_COLOR
        );
    }

    private void drawModuleCategory(
            GuiGraphics graphics
    ) {
        String category =
                formatCategory(
                        module.getCategory()
                );

        drawText(
                graphics,
                font,
                category,
                x + LEFT_PADDING,
                y + SUBTITLE_Y,
                SUBTITLE_COLOR
        );
    }

    private void drawToggle(
            GuiGraphics graphics
    ) {
        int toggleX =
                x + width
                        - LEFT_PADDING
                        - TOGGLE_WIDTH;

        int toggleY =
                y + 22;

        int toggleColor =
                module.isEnabled()
                        ? TOGGLE_ON
                        : TOGGLE_OFF;

        drawSoftRect(
                graphics,
                toggleX,
                toggleY,
                TOGGLE_WIDTH,
                TOGGLE_HEIGHT,
                7,
                toggleColor
        );

        int knobX;

        if (module.isEnabled()) {
            knobX =
                    toggleX
                            + TOGGLE_WIDTH
                            - TOGGLE_KNOB
                            - 2;
        } else {
            knobX =
                    toggleX + 2;
        }

        int knobY =
                toggleY
                        + (TOGGLE_HEIGHT - TOGGLE_KNOB) / 2;

        int knobColor =
                module.isEnabled()
                        ? KNOB_ON
                        : KNOB_OFF;

        drawSoftRect(
                graphics,
                knobX,
                knobY,
                TOGGLE_KNOB,
                TOGGLE_KNOB,
                5,
                knobColor
        );
    }

    private void renderSettings(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {
        int settingsX =
                x + SETTINGS_MARGIN;

        int settingsY =
                y + BASE_HEIGHT + SETTINGS_MARGIN;

        int settingsWidth =
                width - SETTINGS_MARGIN * 2;

        int settingsHeight =
                getSettingsHeight();

        drawBorder(
                graphics,
                settingsX,
                settingsY,
                settingsWidth,
                settingsHeight,
                8.0F,
                SETTINGS_BORDER,
                SETTINGS_BACKGROUND
        );

        int currentY =
                settingsY + SETTINGS_PADDING;

        for (Setting<?> setting : module.getSettings()) {

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
        int settingX =
                x
                        + SETTINGS_MARGIN
                        + SETTINGS_PADDING;

        int settingWidth =
                width
                        - SETTINGS_MARGIN * 2
                        - SETTINGS_PADDING * 2;

        boolean hovered =
                mouseX >= settingX
                        && mouseX <= settingX + settingWidth
                        && mouseY >= settingY
                        && mouseY <= settingY + SETTING_HEIGHT;

        int background =
                hovered
                        ? SETTING_HOVER_BACKGROUND
                        : SETTING_BACKGROUND;

        drawSoftRect(
                graphics,
                settingX,
                settingY,
                settingWidth,
                SETTING_HEIGHT,
                SETTING_RADIUS,
                background
        );

        drawText(
                graphics,
                font,
                setting.getName(),
                settingX + 9,
                settingY + 10,
                SETTING_TEXT
        );

        if (setting instanceof BooleanSetting booleanSetting) {
            renderBooleanSetting(
                    graphics,
                    booleanSetting,
                    settingX,
                    settingY,
                    settingWidth
            );

            return;
        }

        if (setting instanceof ModeSetting modeSetting) {
            renderModeSetting(
                    graphics,
                    modeSetting,
                    settingX,
                    settingY,
                    settingWidth
            );

            return;
        }

        if (setting instanceof NumberSetting numberSetting) {
            renderNumberSetting(
                    graphics,
                    numberSetting,
                    settingX,
                    settingY,
                    settingWidth
            );
        }
    }

    private void renderBooleanSetting(
            GuiGraphics graphics,
            BooleanSetting setting,
            int settingX,
            int settingY,
            int settingWidth
    ) {
        int toggleX =
                settingX
                        + settingWidth
                        - TOGGLE_WIDTH
                        - 8;

        int toggleY =
                settingY + 9;

        int toggleColor =
                setting.isEnabled()
                        ? TOGGLE_ON
                        : TOGGLE_OFF;

        drawSoftRect(
                graphics,
                toggleX,
                toggleY,
                TOGGLE_WIDTH,
                TOGGLE_HEIGHT,
                7,
                toggleColor
        );

        int knobX =
                setting.isEnabled()
                        ? toggleX
                        + TOGGLE_WIDTH
                        - TOGGLE_KNOB
                        - 2
                        : toggleX + 2;

        int knobY =
                toggleY
                        + (TOGGLE_HEIGHT - TOGGLE_KNOB) / 2;

        int knobColor =
                setting.isEnabled()
                        ? KNOB_ON
                        : KNOB_OFF;

        drawSoftRect(
                graphics,
                knobX,
                knobY,
                TOGGLE_KNOB,
                TOGGLE_KNOB,
                5,
                knobColor
        );
    }

    private void renderModeSetting(
            GuiGraphics graphics,
            ModeSetting setting,
            int settingX,
            int settingY,
            int settingWidth
    ) {
        String value =
                setting.getMode();

        int textWidth =
                textWidth(
                        font,
                        value
                );

        drawText(
                graphics,
                font,
                value,
                settingX
                        + settingWidth
                        - textWidth
                        - 9,
                settingY + 10,
                SETTING_MUTED
        );
    }

    private void renderNumberSetting(
            GuiGraphics graphics,
            NumberSetting setting,
            int settingX,
            int settingY,
            int settingWidth
    ) {
        String value =
                formatNumber(
                        setting.getValue()
                );

        int valueWidth =
                textWidth(
                        font,
                        value
                );

        drawText(
                graphics,
                font,
                value,
                settingX
                        + settingWidth
                        - valueWidth
                        - 9,
                settingY + 10,
                SETTING_MUTED
        );

        int sliderX =
                settingX + 9;

        int sliderY =
                settingY
                        + SETTING_HEIGHT
                        - 5;

        int sliderWidth =
                settingWidth - 18;

        if (sliderWidth <= 0) {
            return;
        }

        drawSoftRect(
                graphics,
                sliderX,
                sliderY,
                sliderWidth,
                2,
                1,
                SLIDER_BACKGROUND
        );

        double range =
                setting.getMax()
                        - setting.getMin();

        double progress;

        if (range <= 0.0) {
            progress = 0.0;
        } else {
            progress =
                    (setting.getValue()
                            - setting.getMin())
                            / range;
        }

        progress =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                progress
                        )
                );

        int fillWidth =
                (int) Math.round(
                        sliderWidth * progress
                );

        if (fillWidth > 0) {
            drawSoftRect(
                    graphics,
                    sliderX,
                    sliderY,
                    fillWidth,
                    2,
                    1,
                    SLIDER_FILL
            );
        }
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (!isHovered(mouseX, mouseY)) {
            return false;
        }

        /*
         * Settings have priority over the module card.
         */
        if (expanded && !module.getSettings().isEmpty()) {

            int settingY =
                    y
                            + BASE_HEIGHT
                            + SETTINGS_MARGIN
                            + SETTINGS_PADDING;

            for (Setting<?> setting : module.getSettings()) {

                int settingX =
                        x
                                + SETTINGS_MARGIN
                                + SETTINGS_PADDING;

                int settingWidth =
                        width
                                - SETTINGS_MARGIN * 2
                                - SETTINGS_PADDING * 2;

                boolean settingHovered =
                        mouseX >= settingX
                                && mouseX <= settingX + settingWidth
                                && mouseY >= settingY
                                && mouseY <= settingY + SETTING_HEIGHT;

                if (settingHovered) {

                    if (setting instanceof BooleanSetting booleanSetting) {

                        if (button == 0) {
                            booleanSetting.toggle();
                            return true;
                        }
                    }

                    if (setting instanceof ModeSetting modeSetting) {

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

                    if (setting instanceof NumberSetting numberSetting) {

                        if (button == 0) {
                            draggingNumber = true;
                            draggingSetting = numberSetting;

                            updateNumberFromMouse(
                                    numberSetting,
                                    mouseX,
                                    settingX,
                                    settingWidth
                            );

                            return true;
                        }
                    }
                }

                settingY +=
                        SETTING_HEIGHT
                                + SETTING_GAP;
            }
        }

        /*
         * Right click expands/collapses settings.
         */
        if (button == 1 && !module.getSettings().isEmpty()) {
            expanded = !expanded;

            updateHeight();

            return true;
        }

        /*
         * Left click toggles the module.
         */
        if (button == 0) {
            module.toggle();

            return true;
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
            draggingNumber = false;
            draggingSetting = null;
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
                button != 0
                        || !draggingNumber
                        || draggingSetting == null
        ) {
            return false;
        }

        int settingX =
                x
                        + SETTINGS_MARGIN
                        + SETTINGS_PADDING;

        int settingWidth =
                width
                        - SETTINGS_MARGIN * 2
                        - SETTINGS_PADDING * 2;

        updateNumberFromMouse(
                draggingSetting,
                mouseX,
                settingX,
                settingWidth
        );

        return true;
    }

    private void updateNumberFromMouse(
            NumberSetting setting,
            double mouseX,
            int settingX,
            int settingWidth
    ) {
        int sliderX =
                settingX + 9;

        int sliderWidth =
                settingWidth - 18;

        if (sliderWidth <= 0) {
            return;
        }

        double progress =
                (mouseX - sliderX)
                        / sliderWidth;

        progress =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                progress
                        )
                );

        double value =
                setting.getMin()
                        + (
                        setting.getMax()
                                - setting.getMin()
                ) * progress;

        setting.setValue(value);
    }

    private void cycleModeBackwards(
            ModeSetting setting
    ) {
        if (setting.getModes().isEmpty()) {
            return;
        }

        int index =
                setting.getModes()
                        .indexOf(
                                setting.getMode()
                        );

        if (index <= 0) {
            index =
                    setting.getModes().size() - 1;
        } else {
            index--;
        }

        setting.setMode(
                setting.getModes()
                        .get(index)
        );
    }

    private void updateHeight() {
        if (!expanded || module.getSettings().isEmpty()) {
            height = BASE_HEIGHT;
            return;
        }

        height =
                BASE_HEIGHT
                        + SETTINGS_MARGIN
                        + getSettingsHeight();
    }

    public int getSettingsHeight() {
        if (module.getSettings().isEmpty()) {
            return 0;
        }

        return SETTINGS_PADDING * 2
                + module.getSettings().size()
                * SETTING_HEIGHT
                + (module.getSettings().size() - 1)
                * SETTING_GAP;
    }

    @Override
    public int getHeight() {
        updateHeight();
        return height;
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
        updateHeight();
    }

    private String formatCategory(
            Module.Category category
    ) {
        if (category == null) {
            return "";
        }

        if (category == Module.Category.WORLD) {
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
}