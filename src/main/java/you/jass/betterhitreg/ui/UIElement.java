package you.jass.betterhitreg.ui;

public interface UIElement {
    void render(Object renderer, int mouseX, int mouseY);
    boolean mouseClicked(double mouseX, double mouseY, int button);
    boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY);
    boolean mouseReleased(double mouseX, double mouseY, int button);
}
