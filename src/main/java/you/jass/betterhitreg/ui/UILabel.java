package you.jass.betterhitreg.ui;

import net.minecraft.client.font.TextRenderer;

import java.awt.*;

public class UILabel implements UIElement {
    public final int x, y;
    public final TextRenderer textRenderer;
    public final String text;
    public final boolean centered;
    public final Color start;
    public final Color end;

    public UILabel(int x, int y,
                   TextRenderer textRenderer,
                   String text, Theme theme, boolean gradient, boolean centered) {
        this.x            = x;
        this.y            = y;
        this.textRenderer = textRenderer;
        this.text         = text;
        this.centered     = centered;

        if (gradient) {
            start = theme.text().brighter();
            end   = theme.text();
        } else {
            start = end = theme.text();
        }
    }

    @Override
    public void render(Object renderer, int mx, int my) {
        UIUtils.drawGradientText(renderer, textRenderer,
                text, x, y - 4,
                start, end, centered
        );
    }

    @Override public boolean mouseClicked(double mx, double my, int button) { return false; }
    @Override public boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return false; }
    @Override public boolean mouseReleased(double mx, double my, int button) { return false; }
}