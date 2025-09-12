package you.jass.betterhitreg.ui;

import net.minecraft.client.font.TextRenderer;

import java.awt.*;
import java.util.function.Consumer;

public class UISlider implements UIElement {
    public final int x, y, width;
    public final float min, max;
    public float value;
    public final int gap;
    public final float precision;
    public final String prefix, suffix;
    public final TextRenderer textRenderer;
    private final float initialValue;
    public boolean dragging;
    public final Consumer<Float> onDrag;
    public final Consumer<Integer> onStop;

    public final Color normalTextStart, normalTextEnd;
    public final Color normalTrackStart, normalTrackEnd;
    public final Color normalThumbStart, normalThumbEnd;

    public final Color selTextStart, selTextEnd;
    public final Color selTrackStart, selTrackEnd;
    public final Color selThumbStart, selThumbEnd;

    public final Color hovTextStart, hovTextEnd;
    public final Color hovTrackStart, hovTrackEnd;
    public final Color hovThumbStart, hovThumbEnd;

    public UISlider(int x, int y, int width,
                    float min, float max, float initial, int gap, float precision,
                    String prefix, String suffix, TextRenderer textRenderer,
                    Theme theme,
                    boolean gradient,
                    Consumer<Float> onDrag,
                    Consumer<Integer> onStop) {
        this.x              = x;
        this.y              = y;
        this.width          = width;
        this.min            = min;
        this.max            = max;
        this.gap            = gap;
        this.precision      = precision;
        this.prefix         = prefix;
        this.suffix         = suffix;
        this.textRenderer   = textRenderer;
        this.onDrag         = onDrag;
        this.onStop         = onStop;
        this.initialValue = initial;
        this.value        = initial;

        if (gradient) {
            normalTextStart   = theme.text().brighter();
            normalTextEnd     = theme.text();
            normalTrackStart  = theme.background().brighter();
            normalTrackEnd    = theme.background();
            normalThumbStart  = theme.border().brighter();
            normalThumbEnd    = theme.border();

            selTextStart      = theme.text().brighter();
            selTextEnd        = theme.text();
            selTrackStart     = theme.background().brighter();
            selTrackEnd       = theme.background();
            selThumbStart     = theme.border().brighter();
            selThumbEnd       = theme.border();

            hovTextStart      = theme.text().brighter();
            hovTextEnd        = theme.text();
            hovTrackStart     = theme.background().brighter();
            hovTrackEnd       = theme.background();
            hovThumbStart     = theme.border().brighter().brighter();
            hovThumbEnd       = theme.border().brighter();
        } else {
            normalTextStart   = normalTextEnd   = theme.text();
            normalTrackStart  = normalTrackEnd  = theme.background();
            normalThumbStart  = normalThumbEnd  = theme.border();

            selTextStart      = selTextEnd      = theme.text();
            selTrackStart     = selTrackEnd     = theme.background();
            selThumbStart     = selThumbEnd     = theme.border();

            hovTextStart      = hovTextEnd      = theme.text();
            hovTrackStart     = hovTrackEnd     = theme.background();
            hovThumbStart     = hovThumbEnd     = theme.border();
        }
    }

    @Override
    public void render(Object renderer, int mx, int my) {
        boolean hovered = mx >= x && mx <= x + width && my >= y - 3 && my <= y + 4;
        boolean changed = value != initialValue;

        Color tStart, tEnd, trStart, trEnd, thStart, thEnd;
        if (changed) {
            tStart  = selTextStart;   tEnd  = selTextEnd;
            trStart = selTrackStart;  trEnd = selTrackEnd;
            thStart = selThumbStart;  thEnd = selThumbEnd;
        } else if (hovered) {
            tStart  = hovTextStart;   tEnd  = hovTextEnd;
            trStart = hovTrackStart;  trEnd = hovTrackEnd;
            thStart = hovThumbStart;  thEnd = hovThumbEnd;
        } else {
            tStart  = normalTextStart;   tEnd  = normalTextEnd;
            trStart = normalTrackStart;  trEnd = normalTrackEnd;
            thStart = normalThumbStart;  thEnd = normalThumbEnd;
        }

        UIUtils.drawHorizontalGradient(renderer, x, y, width, 2, trStart, trEnd);

        double clampedValue = Math.max(min, Math.min(max - 5, value));
        double normalized = (clampedValue - min) / (max - min);
        int tx = (int) (x + normalized * width);

        UIUtils.drawGradientRectangle(renderer, tx, y - 3, 2, 8, thStart, thEnd);

        String s = Integer.toString((int) value);
        UIUtils.drawGradientText(renderer, textRenderer, prefix + s + suffix, x + width + gap, y - 3, tStart, tEnd, true);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0 && mx >= x && mx <= x + width && my >= y - 3 && my <= y + 4) {
            dragging = true;
            updateValue(mx);
            onDrag.accept(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) {
        if (dragging) {
            updateValue(mx);
            onDrag.accept(value);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mx, double my, int button) {
        if (dragging && button == 0) {
            dragging = false;
            onStop.accept((int) value);
            return true;
        }
        return false;
    }

    private void updateValue(double mx) {
        double frac = (mx - x) / (width - 2.0);
        frac = Math.max(0.0, Math.min(1.0, frac));
        double raw = min + frac * (max - min);
        double stepped = Math.round(raw / precision) * precision;
        this.value = (float)Math.max(min, Math.min(max, stepped));
    }

    public float getValue() {
        return value;
    }
}
