package kastdlc.client.gui.component;

import kastdlc.client.gui.RoundedRectRenderer;
import kastdlc.client.module.Module;
import kastdlc.client.module.settings.BooleanSetting;
import kastdlc.client.module.settings.ModeSetting;
import kastdlc.client.module.settings.NumberSetting;
import kastdlc.client.module.settings.Setting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;
import java.util.Locale;

public class ModuleComponent extends Component {

    /*
     * ============================================================
     * CARD
     * ============================================================
     */

    private static final int BASE_HEIGHT = 58;

    private static final int CARD_RADIUS = 9;

    private static final int LEFT_PADDING = 14;

    private static final int TITLE_Y = 15;
    private static final int DESCRIPTION_Y = 34;

    /*
     * ============================================================
     * SETTINGS
     * ============================================================
     */

    private static final int SETTINGS_MARGIN = 6;
    private static final int SETTINGS_PADDING = 7;

    private static final int SETTING_HEIGHT = 30;
    private static final int SETTING_GAP = 5;

    /*
     * ============================================================
     * TOGGLE
     * ============================================================
     */

    private static final int TOGGLE_WIDTH = 25;
    private static final int TOGGLE_HEIGHT = 13;
    private static final int TOGGLE_KNOB = 9;

    /*
     * ============================================================
     * COLORS
     * ============================================================
     */

    private static final int CARD =
            0xF411151B;

    private static final int CARD_HOVER =
            0xF71A1F27;

    private static final int CARD_ENABLED =
            0xF61B212A;

    private static final int CARD_ENABLED_HOVER =
            0xFA202731;

    private static final int CARD_BORDER =
            0x302C333D;

    private static final int CARD_BORDER_HOVER =
            0x553A424E;

    private static final int CARD_BORDER_ENABLED =
            0x60464F5C;

    private static final int TEXT_PRIMARY =
            0xFFE9EDF2;

    private static final int TEXT_SECONDARY =
            0xFFB7BEC8;

    private static final int TEXT_MUTED =
            0xFF707985;

    private static final int ACCENT =
            0xFFE9EDF2;

    private static final int ACCENT_DARK =
            0xFF242A32;

    /*
     * ============================================================
     * SETTINGS COLORS
     * ============================================================
     */

    private static final int SETTINGS_BACKGROUND =
            0xF20F1319;

    private static final int SETTINGS_HOVER =
            0xFF191E25;

    private static final int SETTINGS_BORDER =
            0x3A2B323C;

    /*
     * ============================================================
     * SLIDER
     * ============================================================
     */

    private static final int SLIDER_BACKGROUND =
            0xFF292F38;

    private static final int SLIDER_PROGRESS =
            0xFFE1E5EA;

    /*
     * ============================================================
     * STATE
     * ============================================================
     */

    private final Font font;

    private final Module module;

    private boolean expanded;

    private boolean draggingSlider;

    /*
     * ============================================================
     * CONSTRUCTOR
     * ============================================================
     */

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

        this.expanded = false;
        this.draggingSlider = false;
    }

    /*
     * ============================================================
     * RENDER
     * ============================================================
     */

    @Override
    public void render(
            GuiGraphics graphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        boolean hovered =
                mouseX >= x
                        && mouseX <= x + width
                        && mouseY >= y
                        && mouseY <= y + BASE_HEIGHT;

        drawCard(
                graphics,
                hovered
        );

        drawTitle(
                graphics
        );

        drawSubtitle(
                graphics
        );

        drawToggle(
                graphics,
                x + width - TOGGLE_WIDTH - 12,
                y + 22,
                module.isEnabled()
        );

        if (expanded) {

            renderSettings(
                    graphics,
                    mouseX,
                    mouseY
            );
        }
    }

    /*
     * ============================================================
     * CARD
     * ============================================================
     */

    private void drawCard(
            GuiGraphics graphics,
            boolean hovered
    ) {

        int background;

        if (module.isEnabled()) {

            background =
                    hovered
                            ? CARD_ENABLED_HOVER
                            : CARD_ENABLED;

        } else {

            background =
                    hovered
                            ? CARD_HOVER
                            : CARD;
        }

        RoundedRectRenderer.draw(
                graphics,
                x,
                y,
                width,
                BASE_HEIGHT,
                CARD_RADIUS,
                background
        );

        int border;

        if (module.isEnabled()) {

            border =
                    hovered
                            ? CARD_BORDER_HOVER
                            : CARD_BORDER_ENABLED;

        } else {

            border =
                    hovered
                            ? CARD_BORDER_HOVER
                            : CARD_BORDER;
        }

        RoundedRectRenderer.drawBorder(
                graphics,
                x,
                y,
                width,
                BASE_HEIGHT,
                CARD_RADIUS,
                border,
                background
        );
    }

    /*
     * ============================================================
     * TITLE
     * ============================================================
     */

    private void drawTitle(
            GuiGraphics graphics
    ) {

        drawText(
                graphics,
                font,
                module.getName(),
                x + LEFT_PADDING,
                y + TITLE_Y,
                module.isEnabled()
                        ? TEXT_PRIMARY
                        : TEXT_SECONDARY
        );
    }

    /*
     * ============================================================
     * SUBTITLE
     * ============================================================
     */

    private void drawSubtitle(
            GuiGraphics graphics
    ) {

        drawText(
                graphics,
                font,
                formatCategory(),
                x + LEFT_PADDING,
                y + DESCRIPTION_Y,
                TEXT_MUTED
        );
    }

    /*
     * ============================================================
     * TOGGLE
     * ============================================================
     */

    private void drawToggle(
            GuiGraphics graphics,
            int toggleX,
            int toggleY,
            boolean enabled
    ) {

        RoundedRectRenderer.draw(
                graphics,
                toggleX,
                toggleY,
                TOGGLE_WIDTH,
                TOGGLE_HEIGHT,
                7,
                enabled
                        ? ACCENT
                        : ACCENT_DARK
        );

        int knobX =
                enabled
                        ? toggleX
                        + TOGGLE_WIDTH
                        - TOGGLE_KNOB
                        - 2
                        : toggleX + 2;

        int knobY =
                toggleY
                        + (TOGGLE_HEIGHT - TOGGLE_KNOB) / 2;

        RoundedRectRenderer.draw(
                graphics,
                knobX,
                knobY,
                TOGGLE_KNOB,
                TOGGLE_KNOB,
                5,
                enabled
                        ? 0xFF101419
                        : 0xFF69717D
        );
    }

    /*
     * ============================================================
     * SETTINGS
     * ============================================================
     */

    private void renderSettings(
            GuiGraphics graphics,
            int mouseX,
            int mouseY
    ) {

        List<Setting<?>> settings =
                module.getSettings();

        if (settings.isEmpty()) {
            return;
        }

        int settingsX = x;

        int settingsY =
                y
                        + BASE_HEIGHT
                        + SETTINGS_MARGIN;

        int settingsWidth = width;

        int settingsHeight =
                getSettingsHeight();

        RoundedRectRenderer.draw(
                graphics,
                settingsX,
                settingsY,
                settingsWidth,
                settingsHeight,
                CARD_RADIUS,
                SETTINGS_BACKGROUND
        );

        RoundedRectRenderer.drawBorder(
                graphics,
                settingsX,
                settingsY,
                settingsWidth,
                settingsHeight,
                CARD_RADIUS,
                SETTINGS_BORDER,
                SETTINGS_BACKGROUND
        );

        int currentY =
                settingsY + SETTINGS_PADDING;

        for (Setting<?> setting : settings) {

            renderSetting(
                    graphics,
                    setting,
                    currentY,
                    mouseX,
                    mouseY
            );

            currentY +=
                    SETTING_HEIGHT + SETTING_GAP;
        }
    }

    /*
     * ============================================================
     * SETTING
     * ============================================================
     */

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

            RoundedRectRenderer.draw(
                    graphics,
                    x + 5,
                    settingY,
                    width - 10,
                    SETTING_HEIGHT,
                    6,
                    SETTINGS_HOVER
            );
        }

        drawText(
                graphics,
                font,
                setting.getName(),
                x + 11,
                settingY + 9,
                TEXT_SECONDARY
        );

        if (setting instanceof BooleanSetting booleanSetting) {

            renderBooleanSetting(
                    graphics,
                    booleanSetting,
                    settingY
            );

            return;
        }

        if (setting instanceof ModeSetting modeSetting) {

            renderModeSetting(
                    graphics,
                    modeSetting,
                    settingY
            );

            return;
        }

        if (setting instanceof NumberSetting numberSetting) {

            renderNumberSetting(
                    graphics,
                    numberSetting,
                    settingY
            );
        }
    }

    /*
     * ============================================================
     * BOOLEAN SETTING
     * ============================================================
     */

    private void renderBooleanSetting(
            GuiGraphics graphics,
            BooleanSetting setting,
            int settingY
    ) {

        int toggleX =
                x
                        + width
                        - TOGGLE_WIDTH
                        - 11;

        int toggleY =
                settingY + 8;

        drawToggle(
                graphics,
                toggleX,
                toggleY,
                setting.isEnabled()
        );
    }

    /*
     * ============================================================
     * MODE SETTING
     * ============================================================
     */

    private void renderModeSetting(
            GuiGraphics graphics,
            ModeSetting setting,
            int settingY
    ) {

        String mode =
                setting.getMode();

        int right =
                x + width - 10;

        int modeWidth =
                font.width(
                        net.minecraft.network.chat.Component.literal(
                                mode
                        )
                );

        drawText(
                graphics,
                font,
                "<",
                right - modeWidth - 17,
                settingY + 9,
                TEXT_MUTED
        );

        drawText(
                graphics,
                font,
                mode,
                right - modeWidth,
                settingY + 9,
                TEXT_PRIMARY
        );

        drawText(
                graphics,
                font,
                ">",
                right + 2,
                settingY + 9,
                TEXT_MUTED
        );
    }

    /*
     * ============================================================
     * NUMBER SETTING
     * ============================================================
     */

    private void renderNumberSetting(
            GuiGraphics graphics,
            NumberSetting setting,
            int settingY
    ) {

        double min =
                setting.getMin();

        double max =
                setting.getMax();

        double value =
                setting.getValue();

        double percentage = 0.0;

        if (max > min) {

            percentage =
                    (value - min)
                            / (max - min);
        }

        percentage =
                Math.max(
                        0.0,
                        Math.min(
                                1.0,
                                percentage
                        )
                );

        int sliderX =
                x + 10;

        int sliderY =
                settingY + 21;

        int sliderWidth =
                width - 20;

        if (sliderWidth <= 0) {
            return;
        }

        /*
         * Track.
         */

        RoundedRectRenderer.draw(
                graphics,
                sliderX,
                sliderY,
                sliderWidth,
                3,
                2,
                SLIDER_BACKGROUND
        );

        /*
         * Progress.
         */

        int progressWidth =
                (int) (
                        sliderWidth
                                * percentage
                );

        progressWidth =
                Math.max(
                        0,
                        Math.min(
                                sliderWidth,
                                progressWidth
                        )
                );

        if (progressWidth > 0) {

            RoundedRectRenderer.draw(
                    graphics,
                    sliderX,
                    sliderY,
                    progressWidth,
                    3,
                    2,
                    SLIDER_PROGRESS
            );
        }

        /*
         * Knob.
         */

        int knobX =
                sliderX
                        + (int) (
                        sliderWidth
                                * percentage
                );

        RoundedRectRenderer.draw(
                graphics,
                knobX - 3,
                sliderY - 2,
                6,
                7,
                3,
                TEXT_PRIMARY
        );

        /*
         * Value.
         */

        String valueText =
                formatNumber(value);

        int valueWidth =
                font.width(
                        net.minecraft.network.chat.Component.literal(
                                valueText
                        )
                );

        drawText(
                graphics,
                font,
                valueText,
                x + width - valueWidth - 11,
                settingY + 5,
                TEXT_PRIMARY
        );
    }

    /*
     * ============================================================
     * CLICK
     * ============================================================
     */

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
             * Left click.
             */

            if (button == 0) {

                int toggleX =
                        x
                                + width
                                - TOGGLE_WIDTH
                                - 12;

                int toggleY =
                        y + 22;

                /*
                 * Toggle itself.
                 */

                if (
                        mouseX >= toggleX
                                && mouseX <= toggleX + TOGGLE_WIDTH
                                && mouseY >= toggleY
                                && mouseY <= toggleY + TOGGLE_HEIGHT
                ) {

                    module.toggle();

                    return true;
                }

                /*
                 * Right side of card expands settings.
                 */

                if (
                        !module.getSettings().isEmpty()
                                && mouseX >= x + width - 55
                ) {

                    expanded =
                            !expanded;

                    return true;
                }

                /*
                 * Normal click toggles module.
                 */

                module.toggle();

                return true;
            }

            /*
             * Right click expands settings.
             */

            if (
                    button == 1
                            && !module.getSettings().isEmpty()
            ) {

                expanded =
                        !expanded;

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

    /*
     * ============================================================
     * SETTINGS CLICK
     * ============================================================
     */

    private boolean clickSettings(
            double mouseX,
            double mouseY,
            int button
    ) {

        int settingY =
                y
                        + BASE_HEIGHT
                        + SETTINGS_MARGIN
                        + SETTINGS_PADDING;

        for (Setting<?> setting : module.getSettings()) {

            if (
                    mouseY >= settingY
                            && mouseY <= settingY + SETTING_HEIGHT
                            && mouseX >= x
                            && mouseX <= x + width
            ) {

                /*
                 * Boolean.
                 */

                if (
                        setting instanceof BooleanSetting booleanSetting
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
                        setting instanceof ModeSetting modeSetting
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
                        setting instanceof NumberSetting numberSetting
                ) {

                    if (
                            button == 0
                                    && mouseY >= settingY + 14
                    ) {

                        setSliderValue(
                                numberSetting,
                                mouseX
                        );

                        draggingSlider =
                                true;

                        return true;
                    }
                }
            }

            settingY +=
                    SETTING_HEIGHT + SETTING_GAP;
        }

        return false;
    }

    /*
     * ============================================================
     * RELEASE
     * ============================================================
     */

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {

        if (button == 0) {

            draggingSlider =
                    false;
        }

        return false;
    }

    /*
     * ============================================================
     * DRAG
     * ============================================================
     */

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

        int settingY =
                y
                        + BASE_HEIGHT
                        + SETTINGS_MARGIN
                        + SETTINGS_PADDING;

        for (Setting<?> setting : module.getSettings()) {

            if (
                    setting instanceof NumberSetting numberSetting
            ) {

                if (
                        mouseY >= settingY
                                && mouseY <= settingY + SETTING_HEIGHT
                ) {

                    setSliderValue(
                            numberSetting,
                            mouseX
                    );

                    return true;
                }
            }

            settingY +=
                    SETTING_HEIGHT + SETTING_GAP;
        }

        return false;
    }

    /*
     * ============================================================
     * SLIDER
     * ============================================================
     */

    private void setSliderValue(
            NumberSetting setting,
            double mouseX
    ) {

        double sliderX =
                x + 10;

        double sliderWidth =
                width - 20;

        if (sliderWidth <= 0) {
            return;
        }

        double percentage =
                (mouseX - sliderX)
                        / sliderWidth;

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

    /*
     * ============================================================
     * MODE BACKWARDS
     * ============================================================
     */

    private void cycleModeBackwards(
            ModeSetting setting
    ) {

        List<String> modes =
                setting.getModes();

        if (modes == null || modes.isEmpty()) {
            return;
        }

        int current =
                modes.indexOf(
                        setting.getMode()
                );

        if (current <= 0) {

            setting.setMode(
                    modes.get(
                            modes.size() - 1
                    )
            );

            return;
        }

        setting.setMode(
                modes.get(current - 1)
        );
    }

    /*
     * ============================================================
     * HEIGHT
     * ============================================================
     */

    @Override
    public int getHeight() {

        return BASE_HEIGHT
                + (
                expanded
                        ? SETTINGS_MARGIN
                        + getSettingsHeight()
                        : 0
        );
    }

    public int getSettingsHeight() {

        if (!expanded) {
            return 0;
        }

        List<Setting<?>> settings =
                module.getSettings();

        if (settings == null || settings.isEmpty()) {
            return 0;
        }

        int count =
                settings.size();

        return SETTINGS_PADDING * 2
                + count * SETTING_HEIGHT
                + Math.max(
                0,
                count - 1
        ) * SETTING_GAP;
    }

    /*
     * ============================================================
     * ACCESSORS
     * ============================================================
     */

    public Module getModule() {
        return module;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(
            boolean expanded
    ) {

        this.expanded =
                expanded;
    }

    /*
     * ============================================================
     * CATEGORY
     * ============================================================
     */

    private String formatCategory() {

        Module.Category moduleCategory =
                module.getCategory();

        if (
                moduleCategory
                        == Module.Category.WORLD
        ) {

            return "Utilities";
        }

        if (moduleCategory == null) {
            return "";
        }

        String value =
                moduleCategory
                        .name()
                        .toLowerCase(
                                Locale.ROOT
                        );

        if (value.isEmpty()) {
            return "";
        }

        return value.substring(0, 1)
                .toUpperCase(Locale.ROOT)
                + value.substring(1);
    }
}