package DarwinGame.gui;


import javafx.scene.input.MouseEvent;

public interface IGuiWorldMapElementClickObserver {
    void guiWorldMapElementClicked(GuiMapElement guiMapElement, MouseEvent event);
}
