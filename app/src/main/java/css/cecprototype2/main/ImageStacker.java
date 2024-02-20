package css.cecprototype2.main;

import android.graphics.Bitmap;

import android.graphics.Color;

import java.util.List;

public class ImageStacker {
    public Bitmap averagePixelValues(List<Bitmap> images) {
        int numOfLayers = images.size();
        int numOfPixels = images.get(0).getWidth() * images.get(0).getHeight();

        int[] averages = new int[numOfPixels];

        for (int pixelIndex = 0; pixelIndex < numOfPixels; pixelIndex++) {
            int sum = 0;

            for (int layerIndex = 0; layerIndex < numOfLayers; layerIndex++) {
                int pixelColor = images.get(layerIndex).getPixel(pixelIndex % images.get(0).getWidth(),
                        pixelIndex / images.get(0).getWidth());

                int pixelValue = Color.red(pixelColor) + Color.green(pixelColor) + Color.blue(pixelColor);
                sum += pixelValue;
            }

            averages[pixelIndex] = sum / numOfLayers;
        }

        Bitmap averagedBitmap = createBitmapFromAverages(averages, images.get(0).getWidth(), images.get(0).getHeight());
        return averagedBitmap;
    }

    private Bitmap createBitmapFromAverages(int[] averages, int width, int height) {
        Bitmap averagedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelValue = averages[y * width + x];
                int pixelColor = Color.rgb(pixelValue, pixelValue, pixelValue);

                averagedBitmap.setPixel(x, y, pixelColor);
            }
        }

        return averagedBitmap;
    }

}