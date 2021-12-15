package DarwinGame.gui;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.util.Arrays;

public class GuiHelpers {
    public static Image generateSolidColorImage(int width, int height, double red, double green, double blue, double opacity) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter pw = img.getPixelWriter();

        // Should really verify 0.0 <= red, green, blue, opacity <= 1.0
        int alpha = (int) (opacity * 255) ;
        int r = (int) (red * 255) ;
        int g = (int) (green * 255) ;
        int b = (int) (blue * 255) ;

        int pixel = (alpha << 24) | (r << 16) | (g << 8) | b ;
        int[] pixels = new int[width * height];
        Arrays.fill(pixels, pixel);

        pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        return img;
    }
}
