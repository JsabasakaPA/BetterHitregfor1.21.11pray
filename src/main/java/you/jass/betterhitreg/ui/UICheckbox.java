package you.jass.betterhitreg.ui;

import net.minecraft.client.font.TextRenderer;

import java.awt.*;
import java.util.function.Consumer;

public class UICheckbox implements UIElement {
    public final int x, y, size, gap;
    public final TextRenderer textRenderer;
    public final String label;
    public boolean checked;
    public final Consumer<Boolean> onChange;

    public final Color normalTextStart, normalTextEnd;
    public final Color normalBgStart,   normalBgEnd;
    public final Color normalBorderStart, normalBorderEnd;

    public final Color selTextStart,    selTextEnd;
    public final Color selBgStart,      selBgEnd;
    public final Color selBorderStart,  selBorderEnd;

    public final Color hovTextStart,    hovTextEnd;
    public final Color hovBgStart,      hovBgEnd;
    public final Color hovBorderStart,  hovBorderEnd;

    public UICheckbox(int x, int y, int size, int gap,
                      TextRenderer textRenderer,
                      String label,
                      Theme theme,
                      boolean gradient,
                      boolean initial,
                      Consumer<Boolean> onChange) {
        this.x = x; this.y = y - 6; this.size = size; this.gap = gap;
        this.textRenderer = textRenderer;
        this.label = label;
        this.checked = initial;
        this.onChange = onChange;

        if (gradient) {
            normalTextStart   = theme.text().brighter();
            normalTextEnd     = theme.text();
            normalBgStart     = theme.background().brighter();
            normalBgEnd       = theme.background();
            normalBorderStart = theme.border().brighter();
            normalBorderEnd   = theme.border();

            selTextStart      = theme.highlighted().brighter();
            selTextEnd        = theme.highlighted();
            selBgStart        = theme.highlighted().brighter();
            selBgEnd          = theme.highlighted();
            selBorderStart    = theme.highlighted().brighter();
            selBorderEnd      = theme.highlighted();

            hovTextStart      = theme.hovered().brighter();
            hovTextEnd        = theme.hovered();
            hovBgStart        = theme.hovered().brighter();
            hovBgEnd          = theme.hovered();
            hovBorderStart    = theme.hovered().brighter();
            hovBorderEnd      = theme.hovered();
        } else {
            normalTextStart   = normalTextEnd   = theme.text();
            normalBgStart     = normalBgEnd     = theme.background();
            normalBorderStart = normalBorderEnd = theme.border();

            selTextStart      = selTextEnd      = theme.highlighted();
            selBgStart        = selBgEnd        = theme.highlighted();
            selBorderStart    = selBorderEnd    = theme.highlighted();

            hovTextStart      = hovTextEnd      = theme.hovered();
            hovBgStart        = hovBgEnd        = theme.hovered();
            hovBorderStart    = hovBorderEnd    = theme.hovered();
        }
    }

    @Override
    public void render(Object renderer, int mx, int my) {
        boolean hovered = mx >= x + gap
                && mx <= x + gap + size
                && my >= y
                && my <= y + size;

        Color tStart, tEnd, bgStart, bgEnd, bStart, bEnd;
        if (hovered) {
            tStart  = hovTextStart;  tEnd  = hovTextEnd;
            bgStart = hovBgStart;    bgEnd = hovBgEnd;
            bStart  = hovBorderStart; bEnd  = hovBorderEnd;
        } else if (checked) {
            tStart  = selTextStart;   tEnd  = selTextEnd;
            bgStart = selBgStart;     bgEnd = selBgEnd;
            bStart  = selBorderStart; bEnd  = selBorderEnd;
        } else {
            tStart  = normalTextStart;   tEnd  = normalTextEnd;
            bgStart = normalBgStart;     bgEnd = normalBgEnd;
            bStart  = normalBorderStart; bEnd  = normalBorderEnd;
        }

        UIUtils.drawGradientText(renderer, textRenderer, label, x, y + 2, tStart, tEnd, false);
        UIUtils.drawGradientBorder(renderer,
                x + gap, y, size, size,
                bStart, bEnd);

        if (checked) {
            UIUtils.drawGradientRectangle(renderer,
                    x + gap + 2, y + 2,
                    size - 4, size - 4,
                    bgStart, bgEnd);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0
            && mx >= x + gap
            && mx <= x + gap + size
            && my >= y
            && my <= y + size) {

            checked = !checked;
            onChange.accept(checked);
            return true;
        }
        return false;
    }

    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return false; }
    @Override public boolean mouseReleased(double mx, double my, int button)                 { return false; }
}
