package css.cecprototype2.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.List;

import css.cecprototype2.main.MainViewModel;
import css.cecprototype2.region_logic.Region;

public class BoundingBoxOverlay {
    private Paint paint;
    private Canvas canvas;

    MainViewModel mainViewModel;
    Bitmap bitmapDrawing;           // bitmap copy for drawing bounding boxes
    Bitmap currentBitmap;          // original bitmap to draw over
    List<Region> regions;

    public BoundingBoxOverlay(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
        currentBitmap = mainViewModel.currentBitmap;
        regions = mainViewModel.regions;
    }

    public Bitmap drawBoundingBoxes() {
        // Step 3: Initialize the Paint object
        paint = new Paint();
        paint.setColor(Color.RED);  // Set the color to red
        paint.setStyle(Paint.Style.STROKE);  // Set the style to stroke
        paint.setStrokeWidth(5);  // Set the stroke width to 5 pixels

        // Step 4: Create a new Bitmap to draw on
        bitmapDrawing = Bitmap.createBitmap(
                currentBitmap.getWidth(),
                currentBitmap.getHeight(),
                currentBitmap.getConfig()
        );
        // Step 5: Create a canvas using the new Bitmap
        canvas = new Canvas(bitmapDrawing);
        canvas.drawBitmap(currentBitmap, 0, 0, null);

        // Step 6: Draw bounding box on the canvas
        for (Region reg: regions) {
            Log.i("CIS4444", "Bounding box for region at "reg.xUpperLeft, reg.yLowerRight, reg.xLowerRight, reg.yLowerRight);

            drawBoundingBox(reg.xUpperLeft, reg.yLowerRight, reg.xLowerRight, reg.yLowerRight);
        }

        // Step 7: Set the modified bitmap to the ImageView
        // imageView.setImageBitmap(bitmapDrawing);
        return bitmapDrawing;
    }
    private void drawBoundingBox(int left, int top, int right, int bottom) {
        canvas.drawRect(left, top, right, bottom, paint);
    }

}
