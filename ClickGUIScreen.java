package fun.mentalium.ui.clickgui;

import fun.mentalium.constructor.modules.impl.render.HUD;
import fun.mentalium.constructor.modules.impl.render.ClickGUI;
import fun.mentalium.utils.render.draw.RectUtil;
import lombok.Getter;
import lombok.experimental.Accessors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.StringTextComponent;
import net.mojang.blaze3d.matrix.MatrixStack;
import fun.mentalium.Mentalium;
import fun.mentalium.constructor.api.interfaces.IMinecraft;
import fun.mentalium.constructor.api.interfaces.IMouse;
import fun.mentalium.constructor.api.interfaces.IWindow;
import fun.mentalium.ui.clickgui.components.Panel;
import fun.mentalium.ui.clickgui.components.category.CategoryComponent;
import fun.mentalium.ui.clickgui.components.module.ModuleComponent;
import fun.mentalium.ui.clickgui.components.setting.impl.StringSettingComponent;
import fun.mentalium.utils.animation.Animation;
import fun.mentalium.utils.keyboard.Keyboard;
import fun.mentalium.utils.math.ScaleMath;
import fun.mentalium.utils.other.SoundUtil;
import fun.mentalium.utils.render.color.ColorUtil;
import fun.mentalium.utils.render.font.Font;
import fun.mentalium.utils.render.font.Fonts;
import fun.mentalium.utils.render.text.TextAlign;
import fun.mentalium.utils.render.text.TextBox;
import org.joml.Vector2f;

@Getter
@Accessors(fluent = true)
public class ClickGUIScreen extends Screen implements IMinecraft, IWindow, IMouse {
    private boolean exit = false;
    private final Animation alpha = new Animation();
    private final Animation scale = new Animation();
    private final float categoryWidth = 110, categoryHeight = 20, categoryOffset = 10;
    private final Panel panel = new Panel(this);
    private boolean overlayOnly = false;
    private boolean overlayTogglePending = false;
    private float overlayBtnX, overlayBtnY, overlayBtnW = 22f, overlayBtnH = 12f;
    private float backBtnX, backBtnY, backBtnW = 22f, backBtnH = 12f;
    private float searchIconX, searchIconY, searchIconSize = 12f;
    private final Animation searchAnim = new Animation();
    private final Animation overlayBtnAlpha = new Animation();
    private final Animation backBtnAlpha = new Animation();

    private final TextBox searchField = new TextBox(new Vector2f(), Fonts.sf_medium, 8, HUD.getInstance().textColor(), TextAlign.LEFT, "Поиск...", 0, false, false);

    private boolean hasResized = false;

    public ClickGUIScreen() {
        super(StringTextComponent.EMPTY);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);
        this.panel.resize(minecraft, width, height);
        hasResized = true;
    }

    @Override
    protected void init() {
        super.init();
        SoundUtil.playSound("guiopen.wav", 0.75);
        Mentalium.getInstance().configManager().set();
        searchField.setText("");
        searchField.setSelected(false);

        alpha.set(1.0);
        scale.set(1.0);
        exit = false;
        hasResized = false;
        overlayBtnAlpha.set(overlayOnly ? 0.0 : 1.0);
        backBtnAlpha.set(overlayOnly ? 1.0 : 0.0);

        int width = mc.getMainWindow().getScaledWidth();
        int height = mc.getMainWindow().getScaledHeight();
        this.panel.resize(mc, width, height);

        panel.init();

        searchAnim.set(0.0);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (!hasResized) {
            resize(mc, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
        }

        Vector2f mouse = ScaleMath.getMouse(mouseX, mouseY);
        int finalMouseX = (int) mouse.x;
        int finalMouseY = (int) mouse.y;
        draw(matrixStack, finalMouseX, finalMouseY, partialTicks);
    }

    public void draw(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        alpha.update();
        scale.update();
        searchAnim.update();
        overlayBtnAlpha.update();
        backBtnAlpha.update();

        this.mouseCheck();
        this.closeCheck();

        renderBackground(matrixStack);

        if (ClickGUI.getInstance().dimBackground.getValue()) {
            HUD hud = HUD.getInstance();
            int dimColor = ColorUtil.multAlpha(hud.clientColor(), 0.25f);
            RectUtil.drawRect(matrixStack, 0, 0, scaled().x, scaled().y, dimColor);
        }

        if (!searchField.isSelected()) {
            renderSearchField(matrixStack);
        }
        this.drawPanel(matrixStack, mouseX, mouseY, partialTicks);

        HUD hud = HUD.getInstance();
        int iconColor = hud.iconColor();
        float overlayIconSize = 8f;
        float overlayIconY = overlayBtnY + (overlayBtnH - overlayIconSize) / 2f;
        
        // Кнопка ">" справа от поиска (плавно исчезает при переходе на overlay)
        float overlayAlpha = overlayBtnAlpha.get();
        if (!overlayOnly && overlayAlpha > 0.01f) {

            hud.drawClientRect(matrixStack, overlayBtnX, overlayBtnY, overlayBtnW, overlayBtnH, overlayAlpha);
            int iconColorAlpha = ColorUtil.multAlpha(iconColor, overlayAlpha);
            Fonts.sf_medium.drawCenter(matrixStack, ">", (overlayBtnX + overlayBtnW / 2f), overlayIconY, iconColorAlpha, overlayIconSize);
        }
        
        // Кнопка "<" слева от иконки поиска (плавно появляется при переходе на overlay)
        float backAlpha = backBtnAlpha.get();
        if (overlayOnly && backAlpha > 0.01f) {
            // Стиль FunTimeRenderer
            hud.drawClientRect(matrixStack, backBtnX, backBtnY, backBtnW, backBtnH, backAlpha);
            int iconColorAlpha = ColorUtil.multAlpha(iconColor, backAlpha);
            Fonts.sf_medium.drawCenter(matrixStack, "<", (backBtnX + backBtnW / 2f), overlayIconY, iconColorAlpha, overlayIconSize);
        }

        // прежняя кнопка Aa удалена

        // Переключение после схлопывания масштаба
        if (overlayTogglePending && scale.get() <= 0.02) {
            overlayOnly = !overlayOnly;
            panel.resize(mc, mc.getMainWindow().getScaledWidth(), mc.getMainWindow().getScaledHeight());
            scale.set(0.0);
            scale.run(1.0, 0.25);
            overlayTogglePending = false;
            
            // Анимация кнопок
            if (overlayOnly) {
                // Переключаемся на overlay - кнопка > исчезает, < появляется
                overlayBtnAlpha.run(0.0, 0.25, fun.mentalium.utils.animation.util.Easings.EXPO_IN, false);
                backBtnAlpha.set(0.0);
                backBtnAlpha.run(1.0, 0.25, fun.mentalium.utils.animation.util.Easings.EXPO_OUT, false);
            } else {
                // Возвращаемся - кнопка < исчезает, > появляется
                backBtnAlpha.run(0.0, 0.25, fun.mentalium.utils.animation.util.Easings.EXPO_IN, false);
                overlayBtnAlpha.set(0.0);
                overlayBtnAlpha.run(1.0, 0.25, fun.mentalium.utils.animation.util.Easings.EXPO_OUT, false);
            }
        }

        renderHoveredModuleDescription(matrixStack, mouseX, mouseY);

        if (searchField.isSelected()) {
            renderSearchField(matrixStack);
        }
    }

    public boolean overlayOnly() { return overlayOnly; }

    private void renderHoveredModuleDescription(MatrixStack matrixStack, int mouseX, int mouseY) {
        for (CategoryComponent category : panel.getCategoryComponents()) {
            if (!category.isExactlyHovered(mouseX, mouseY)) continue;

            float contentStartY = category.position().y + category.size().y;
            float contentEndY = contentStartY + category.getMaxVisibleHeight();

            for (ModuleComponent module : category.getModuleComponents()) {
                if (Mentalium.getInstance().clickGui().searchCheck(module.getModule().getName())) continue;

                float moduleTopY = module.position().y;
                float moduleBottomY = moduleTopY + module.size().y;
                boolean isModuleVisible = (moduleBottomY > contentStartY) && (moduleTopY < contentEndY);
                if (!isModuleVisible) continue;

                if (module.isHovered() && !module.getModule().getDescription().isEmpty()) {
                    String description = module.getModule().getDescription();
                    float centerX = scaled().x / 2f;
                    float topY = 13;

                    float maxWidth = scaled().x * 0.8f;
                    Font font = Fonts.sf_medium;
                    float fontSize = 8;
                    HUD hud = HUD.getInstance();
                    int textColor = hud.textColor();

                    float textWidth = font.getWidth(description, fontSize);
                    if (textWidth > maxWidth) {
                        String[] words = description.split(" ");
                        StringBuilder currentLine = new StringBuilder();
                        float lineHeight = fontSize * 1.5f;
                        float currentY = topY;

                        for (String word : words) {
                            String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
                            if (font.getWidth(testLine, fontSize) <= maxWidth) {
                                if (currentLine.length() > 0) currentLine.append(" ");
                                currentLine.append(word);
                            } else {
                                font.drawCenter(matrixStack, currentLine.toString(), centerX, currentY, textColor, fontSize);
                                currentY += lineHeight;
                                currentLine = new StringBuilder(word);
                            }
                        }

                        if (currentLine.length() > 0) {
                            font.drawCenter(matrixStack, currentLine.toString(), centerX, currentY, textColor, fontSize);
                        }
                    } else {
                        font.drawCenter(matrixStack, description, centerX, topY, textColor, fontSize);
                    }

                    return;
                }
            }
        }
    }

    private void renderSearchField(MatrixStack matrixStack) {
        searchField.setEmptyText("Поиск...");
        float searchFieldHeight = 20;

        HUD hud = HUD.getInstance();

        int iconColor = hud.iconColor();
        if (this.searchField.isEmpty()) {
            iconColor = ColorUtil.multAlpha(hud.iconColor(), 0.5f);
        } else {
            iconColor = hud.iconColor();
        }
        // Позиция: снизу по центру, под GUI
        float margin = 8f;
        float totalMaxWidth = searchIconSize + 8f + 110f + 6f + overlayBtnW; // icon + gap + max search + gap + overlay
        float centerX = scaled().x / 2f;
        float baseY = scaled().y - margin - searchFieldHeight - 16f; // чуть выше низа

        float currentWidth = (float) (110f * searchAnim.get());
        float totalCurrentWidth = searchIconSize + 8f + currentWidth + 6f + overlayBtnW;
        float startX = centerX - totalCurrentWidth / 2f;

        // Квадрат под иконку поиска в стиле FunTimeRenderer
        searchIconX = startX;
        searchIconY = baseY + (searchFieldHeight - searchIconSize) / 2f;
        hud.drawClientRect(matrixStack, searchIconX, searchIconY, searchIconSize, searchIconSize, 1f);
        Fonts.icon.drawCenter(matrixStack, "G", searchIconX + searchIconSize / 2f, searchIconY + (searchIconSize - 8f) / 2f, iconColor, 8);

        // Поле поиска выезжает анимацией ширины справа от иконки
        float boxX = searchIconX + searchIconSize + 8f;
        float boxY = baseY;
        if (currentWidth > 0.5f) {
            hud.drawClientDownRect(matrixStack, boxX, boxY, currentWidth, searchFieldHeight, 1);
        }

        searchField.position.set(boxX + 7, boxY + ((searchFieldHeight / 2F) - (searchField.getFontSize() / 2F)) + 0.5f);
        searchField.setWidth(Math.max(0, currentWidth - 27));
        searchField.setColor(ColorUtil.multAlpha(hud.textColor(), 1.0f));

        if (currentWidth > 10f) {
            searchField.draw(matrixStack);
        }

        // Кнопка Overlay справа от поля, а кнопка "<" — слева от иконки поиска
        overlayBtnX = (currentWidth > 0.5f ? (boxX + currentWidth + 6f) : (searchIconX + searchIconSize + 6f));
        overlayBtnY = baseY + (searchFieldHeight - overlayBtnH) / 2f;
        backBtnX = searchIconX - backBtnW - 6f;
        backBtnY = overlayBtnY;
    }

    private void drawPanel(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        panel.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Vector2f mouse = ScaleMath.getMouse(mouseX, mouseY);
        int finalMouseX = (int) mouse.x;
        int finalMouseY = (int) mouse.y;

        if (!exit) {
            // Тоггл анимации поиска по клику на квадрат с иконкой
            if (isHover(finalMouseX, finalMouseY, searchIconX, searchIconY, searchIconSize, searchIconSize)) {
                if (searchAnim.get() < 0.5) {
                    searchAnim.run(1.0, 0.25);
                } else {
                    searchAnim.run(0.0, 0.25);
                    searchField.setText("");
                    searchField.setSelected(false);
                }
                return true;
            }
            // Выбор текста в поле, если оно раскрыто
            if (searchAnim.get() > 0.5) {
                if (isHover(finalMouseX, finalMouseY, searchField.position.x - 7, searchField.position.y - searchField.getFontSize() / 2, searchField.getWidth() + 14, searchField.getFontSize() * 2.5)) {
                    searchField.setSelected(true);
                    return true;
                }
                if (searchField.isSelected() && !isHover(finalMouseX, finalMouseY, searchField.position.x - 7, searchField.position.y - searchField.getFontSize() / 2, searchField.getWidth() + 14, searchField.getFontSize() * 2.5)) {
                    searchField.setSelected(false);
                }
            }
            // Нажатие на ">" (когда не в overlay) или "<" (когда в overlay) рядом с поиском
            if (!overlayOnly && overlayBtnAlpha.get() > 0.5f && isHover(finalMouseX, finalMouseY, overlayBtnX, overlayBtnY, overlayBtnW, overlayBtnH)) {
                // Плавное исчезновение кнопки >
                overlayBtnAlpha.run(0.0, 0.25, fun.mentalium.utils.animation.util.Easings.EXPO_IN, false);
                // анимация: схлопнуть, после чего в draw() переключим режим и развернём
                scale.run(0.0, 0.25);
                overlayTogglePending = true;
                return true;
            }
            if (overlayOnly && backBtnAlpha.get() > 0.5f && isHover(finalMouseX, finalMouseY, backBtnX, backBtnY, backBtnW, backBtnH)) {
                // Плавное исчезновение кнопки <
                backBtnAlpha.run(0.0, 0.25, fun.mentalium.utils.animation.util.Easings.EXPO_IN, false);
                // анимация: схлопнуть, после чего в draw() переключим режим и развернём
                scale.run(0.0, 0.25);
                overlayTogglePending = true;
                return true;
            }
            if (!searchField.isSelected()) {
                panel.mouseClicked(finalMouseX, finalMouseY, button);
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Vector2f mouse = ScaleMath.getMouse(mouseX, mouseY);
        int finalMouseX = (int) mouse.x;
        int finalMouseY = (int) mouse.y;
        if (!searchField.isSelected()) {
            panel.mouseReleased(finalMouseX, finalMouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (exit || searchField.isSelected()) {
            return super.mouseScrolled(mouseX, mouseY, delta);
        }

        Vector2f mouse = ScaleMath.getMouse(mouseX, mouseY);
        int finalMouseX = (int) mouse.x;
        int finalMouseY = (int) mouse.y;

        for (CategoryComponent category : panel.getCategoryComponents()) {
            if (category.isExactlyHovered(finalMouseX, finalMouseY)) {
                float scrollAmount = (float) (delta * 15);
                category.adjustScroll(scrollAmount);
                return true;
            }
        }

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!exit) {
            if (searchField.isSelected() && keyCode == Keyboard.KEY_DELETE.getKey()) {
                searchField.setSelected(false);
                return true;
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_LEFT_CONTROL.getKey()) && keyCode == Keyboard.KEY_F.getKey()) {
                searchField.setSelected(!searchField.isSelected());
            }
            searchField.keyPressed(keyCode);
            if (!searchField.isSelected()) {
                panel.keyPressed(keyCode, scanCode, modifiers);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        panel.keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (!exit) {
            searchField.charTyped(codePoint);
            if (!searchField.isSelected()) {
                panel.charTyped(codePoint, modifiers);
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        boolean noneMatch = panel.getCategoryComponents().stream().noneMatch(category -> category.getModuleComponents().stream().anyMatch(ModuleComponent::isBinding));
        if (!exit && noneMatch) {
            alpha.set(0.0);
            scale.set(0.0);
            exit = true;
            SoundUtil.playSound("guiclose.wav", 0.75);
            mc.mouseHelper.forceGrabMouse(false);
            mc.displayScreen(null);
            return true;
        }
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        super.onClose();
        Mentalium.getInstance().configManager().set();
        panel.onClose();
        searchField.setText("");
        searchField.setSelected(false);
    }

    private void mouseCheck() {
        boolean noneMatch = panel.getCategoryComponents()
                .stream()
                .noneMatch(category -> category.getModuleComponents()
                        .stream()
                        .anyMatch(module -> module.settingComponents
                                .stream()
                                .anyMatch(settingComponent -> settingComponent instanceof StringSettingComponent component && component.textBox.selected)
                        )
                );

        if (!Minecraft.IS_RUNNING_ON_MAC && noneMatch) {
            KeyBinding.updateKeyBindState();
        }
        if (mc.mouseHelper.isMouseGrabbed()) {
            mc.mouseHelper.ungrabMouse();
        }
    }

    private void closeCheck() {
        boolean noneMatch = panel.getCategoryComponents().stream().noneMatch(category -> category.getModuleComponents().stream().anyMatch(ModuleComponent::isBinding));

        if (exit && noneMatch) {
            mc.displayScreen(null);
            exit = false;
        }
    }

    public boolean isSearching() {
        return !searchField.isEmpty();
    }

    public String getSearchText() {
        return searchField.getText();
    }

    public boolean searchCheck(String text) {
        return isSearching() && !text
                .replaceAll(" ", "")
                .trim()
                .toLowerCase()
                .contains(getSearchText()
                        .replaceAll(" ", "")
                        .trim()
                        .toLowerCase());
    }
}