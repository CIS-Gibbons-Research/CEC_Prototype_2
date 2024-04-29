package css.cecprototype2.fragments;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.ImageView;

import java.util.List;

import css.cecprototype2.main.MainViewModel;
import css.cecprototype2.region_logic.Region;

public class BoundingBoxOverlay {
    private List<Region> regions;
    private Paint paint;
    private Canvas canvas;

    MainViewModel mainViewModel;
    Bitmap bitmapDrawing;           // bitmap copy for drawing bounding boxes
    Bitmap currentBitmap;          // original bitmap to draw over

    public BoundingBoxOverlay(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;

    }

    public Bitmap drawBoundingBoxes(List<Region> regions, ImageView imageView ) {
        Log.i("CIS4444", "drawBoundingBoxes");
        currentBitmap = mainViewModel.cam.currentBitmap;
        this.regions = regions;
        if(currentBitmap==null) {
            Log.i("CIS4444", "drawBoundingBoxes -- currentBitmap is null");
            return(currentBitmap);
        }
        // Step 3: Initialize the Paint object
        paint = new Paint();
        paint.setColor(Color.RED);  // Set the color to red
        paint.setStyle(Paint.Style.STROKE);  // Set the style to stroke
        paint.setStrokeWidth(10);  // Set the stroke width to 5 pixels
        paint.setTextSize(100);


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
            //Log.i("CIS4444", "Box base "+ reg.xUpperLeft+" , "+reg.yUpperLeft+" , "+reg.xLowerRight+" , "+reg.yLowerRight);
            double multiplier = reg.getMultiplier();
            int conv_xUp = (int) (reg.xUpperLeft*multiplier);
            int conv_yLeft = (int) (reg.yUpperLeft*multiplier);
            int conv_xDown = (int) (reg.xLowerRight*multiplier);
            int conv_yRight = (int) (reg.yLowerRight*multiplier);
            //Log.i("CIS4444", "Box converted "+ conv_xUp+" , "+conv_yLeft+" , "+conv_xDown+" , "+conv_yRight);
            paint.setColor(Color.RED);  // Set the color to red
            canvas.drawRect(reg.xUpperLeft, reg.yUpperLeft, reg.xLowerRight, reg.yLowerRight, paint);
            canvas.drawText(reg.tag, reg.getxCenter(), reg.getyCenter(), paint);

        }

        // Step 7: Set the modified bitmap to the ImageView
        // imageView.setImageBitmap(bitmapDrawing);
        return bitmapDrawing;
    }
//    private void drawBoundingBox(int left, int top, int right, int bottom) {
//        canvas.drawRect(left, top, right, bottom, paint);
//    }
}