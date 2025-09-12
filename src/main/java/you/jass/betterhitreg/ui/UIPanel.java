package you.jass.betterhitreg.ui;

import java.awt.*;

public class UIPanel implements UIElement {
    public final int x, y, width, height;
    public final Color bgStart, bgEnd;
    public final Color borderStart, borderEnd;

    public UIPanel(int x, int y, int width, int height,
                   Theme theme, boolean gradient) {
        this.x               = x;
        this.y               = y;
        this.width           = width;
        this.height          = height;

        if (gradient) {
            bgStart     = theme.background().brighter();
            bgEnd       = theme.background();
            borderStart = theme.border().brighter();
            borderEnd   = theme.border();
        } else {
            bgStart     = bgEnd     = theme.background();
            borderStart = borderEnd = theme.border();
        }
    }

    @Override
    public void render(Object renderer, int mouseX, int mouseY) {
        UIUtils.drawGradientRectangle(renderer, x, y, width, height, bgStart, bgEnd);
        UIUtils.drawGradientBorder(renderer, x, y, width, height, borderStart, borderEnd);
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button)  { return false; }
    @Override
    public boolean mouseDragged(double mx, double my, int button, double dx, double dy) { return false; }
    @Override
    public boolean mouseReleased(double mx, double my, int button) { return false; }
}
