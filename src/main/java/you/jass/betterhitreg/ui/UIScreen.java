package you.jass.betterhitreg.ui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import you.jass.betterhitreg.hitreg.Hitreg;
import you.jass.betterhitreg.util.Settings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UIScreen extends Screen {
    private final List<UIElement> widgets = new ArrayList<>();

    public UIScreen() {
        super(Text.of("Custom Settings"));
    }

    private final Color background = new Color(30, 30, 30, 230);
    private final Color border = new Color(100, 100, 100, 255);
    private final Color text = new Color(222, 222, 222, 255);
    private final Color hovered = new Color(255, 243, 166, 255);
    private final Color highlighted = new Color(255, 232, 108, 255);
    private final Theme label = new Theme(background, border, text, hovered, highlighted);
    private final Theme checkbox = new Theme(background, border, text, hovered, highlighted);
    private final Theme slider = new Theme(background.darker(), border, text, hovered, highlighted);
    private final Theme panel = new Theme(background, border, text, hovered, highlighted);
    private final Theme header = new Theme(highlighted, highlighted, highlighted, highlighted, highlighted);
    private final Theme footer = new Theme(text.darker().darker(), text.darker().darker(), text.darker().darker(), text.darker().darker(), text.darker().darker());

    @Override
    protected void init() {
        super.init();
        if (Settings.isTutorial()) Settings.set("tutorial", "false");

        widgets.clear();

        int hsize = 150;
        int vsize = 260;
        int vertical = vsize / 2;
        int horizontal = hsize / 2;
        int centerX = width / 2;
        int centerY = height / 2;
        int start = vertical - 10;
        int vgap = 12;
        int hgap = 120;
        int sgap = 16;
        int swidth = 77;
        int sstart = 35;
        int textAlignment = 66;

        widgets.add(new UIPanel(centerX - horizontal, centerY - vertical, hsize, vsize, panel, false));

        widgets.add(new UILabel(
                centerX,
                centerY - vertical + 10,
                textRenderer, "BetterHitreg v1.0.0",
                header, true, true
        ));

        widgets.add(new UILabel(
                centerX,
                centerY + vertical - 10,
                textRenderer, "Made by Jass",
                footer, true, true
        ));

        widgets.add(new UILabel(
                centerX - textAlignment,
                centerY - start + vgap,
                textRenderer, "Hitreg",
                label, false, false
        ));

        widgets.add(new UISlider(
                centerX - sstart,
                centerY - start + vgap,
                swidth, 0, 300, Settings.getHitreg(), sgap, 5,
                "", "ᴍs",
                textRenderer, slider, false,
                v -> {},
                v -> Settings.set("hitreg", String.valueOf(v))
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 2,
                10, hgap,
                textRenderer, "Enabled",
                checkbox, false,
                Settings.isToggled(),
                checked -> Settings.toggle("toggled")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 3,
                10, hgap,
                textRenderer, "Safe Regs Only",
                checkbox, false,
                Settings.isSafeRegsOnly(),
                checked -> Settings.toggle("safeRegsOnly")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 4,
                10, hgap,
                textRenderer, "Mute Other Fights",
                checkbox, false,
                Settings.isSilenceOtherFights(),
                checked -> Settings.toggle("silenceOtherFights")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 5,
                10, hgap,
                textRenderer, "Hide Other Fights",
                checkbox, false,
                Settings.isHideOtherFights(),
                checked -> Settings.toggle("hideOtherFights")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 6,
                10, hgap,
                textRenderer, "Hide Animations",
                checkbox, false,
                Settings.isHideAnimations(),
                checked -> Settings.toggle("hideAnimations")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 7,
                10, hgap,
                textRenderer, "Hide Armor",
                checkbox, false,
                Settings.isHideArmor(),
                checked -> Settings.toggle("hideArmor")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 8,
                10, hgap,
                textRenderer, "Hide Crit Particles",
                checkbox, false,
                Settings.isHideCritParticles(),
                checked -> Settings.toggle("hideCritParticles")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 9,
                10, hgap,
                textRenderer, "Hide Enchant Particles",
                checkbox, false,
                Settings.isHideEnchantParticles(),
                checked -> Settings.toggle("hideEnchantParticles")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 10,
                10, hgap,
                textRenderer, "Always Hit Particles",
                checkbox, false,
                Settings.isParticlesEveryHit(),
                checked -> Settings.toggle("particlesEveryHit")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 11,
                10, hgap,
                textRenderer, "1.8 Hit Sounds",
                checkbox, false,
                Settings.isLegacySounds(),
                checked -> Settings.toggle("legacySounds")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 12,
                10, hgap,
                textRenderer, "Mute Non-hit Sounds",
                checkbox, false,
                Settings.isSilenceNonHits(),
                checked -> Settings.toggle("silenceNonHits")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 13,
                10, hgap,
                textRenderer, "Mute Your Hits",
                checkbox, false,
                Settings.isSilenceSelf(),
                checked -> Settings.toggle("silenceSelf")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 14,
                10, hgap,
                textRenderer, "Mute Their Hits",
                checkbox, false,
                Settings.isSilenceThem(),
                checked -> Settings.toggle("silenceThem")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 15,
                10, hgap,
                textRenderer, "Muffled Hit Sounds",
                checkbox, false,
                Settings.isMuffledHitsounds(),
                checked -> Settings.toggle("muffledHitsounds")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 16,
                10, hgap,
                textRenderer, "Render Target Hitbox",
                checkbox, false,
                Settings.isRenderHitbox(),
                checked -> Settings.toggle("renderHitbox")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 17,
                10, hgap,
                textRenderer, "Alert Delays (" + Hitreg.last100Regs.getAverageDelay() + "ms)",
                checkbox, false,
                Settings.isAlertDelays(),
                checked -> Settings.toggle("alertDelays")
        ));

        widgets.add(new UICheckbox(
                centerX - textAlignment,
                centerY - start + vgap * 18,
                10, hgap,
                textRenderer, "Alert Ghosts (" + Hitreg.last100Regs.getGhostRatio() + "%)",
                checkbox, false,
                Settings.isAlertGhosts(),
                checked -> Settings.toggle("alertGhosts")
        ));

        widgets.add(new UILabel(
                centerX,
                centerY - start + vgap * 19,
                textRenderer, "You: " + (Hitreg.getPlayersPing() > -1 ? Hitreg.getPlayersPing() : "?") + "ᴍs"
                + " Them: " + (Hitreg.getTargetsPing() > -1 ? Hitreg.getTargetsPing() : "?") + "ᴍs",
                label, false, true
        ));
    }

    //TODO 1.19.4
//    @Override
//    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float delta) {
//        for (UIElement w : widgets) {
//            w.render(matrixStack, mouseX, mouseY);
//        }
//
//        super.render(matrixStack, mouseX, mouseY, delta);
//    }

//    @Override
//    public void renderBackground(MatrixStack matrixStack) {}

    //TODO 1.20 - 1.21.8
    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        for (UIElement w : widgets) {
            w.render(ctx, mouseX, mouseY);
        }

        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {}

    //TODO 1.20 - 1.20.1
//    @Override
//    public void renderBackground(DrawContext context) {}

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        for (UIElement w : widgets) {
            if (w.mouseClicked(mx, my, button)) return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        for (UIElement w : widgets) {
            if (w.mouseDragged(mx, my, button, dx, dy)) return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        for (UIElement w : widgets) {
            if (w.mouseReleased(mx, my, button)) return true;
        }
        return super.mouseReleased(mx, my, button);
    }
}