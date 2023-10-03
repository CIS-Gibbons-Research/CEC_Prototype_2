package css.cecprototype2;

import android.media.Image;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CircleIntensityExtractor
{
    private Image image;
    private List<Region> circleRegions;

    public CircleIntensityExtractor(Image image, List<Region> circleRegions)
    {
        this.image = image;
        this.circleRegions = circleRegions;
    }

    public Map<String, Double> extractCircleIntensities() {
        Map<String, Double> circleIntensityMap = new HashMap<>();

        for (int i = 0; i < circleRegions.size(); i++) {
            Region region = circleRegions.get(i);
            Image circleMask = createCircularMask(region);
            double circleIntensity = calculateAverageIntensity(image, circleMask);
            circleIntensityMap.put("Circle " + (i + 1), circleIntensity);
        }

        return circleIntensityMap;
    }

    private Image createCircularMask(Region region) {

        return null;
    }

    private double calculateAverageIntensity(Image image, Image mask) {

        return 0.0;
    }

    private Image loadImage(String filePath)
    {
        return null;
    }

    private class Region
    {
        private int x;
        private int y;
        private int width;
        private int height;

        public Region(int x, int y, int width, int height)
        {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
