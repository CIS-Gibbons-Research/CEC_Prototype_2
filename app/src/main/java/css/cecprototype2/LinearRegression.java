package css.cecprototype2;

import android.util.Log;

import org.apache.commons.math3.stat.regression.SimpleRegression;

import java.util.Arrays;
import java.util.List;

public class LinearRegression {
    // Data
    List<Double> fluorescenceValues; // = Arrays.asList(1200.0, 1310.0, 1430.0, 1580.0);
    List<Double> concentrationValues; // = Arrays.asList(0.2, 0.4, 0.6, 0.8);
    public Double m_slope = 0.0;        // slope of linear regression line
    public Double b_intercept = 0.0;   // y-intercept

    // Create a SimpleRegression instance
    SimpleRegression regression;

    public LinearRegression(List<Double> fluorescenceValues, List<Double> concentrationValues) {
        Log.d("LinearRegression", "Fluorescence values size: " + fluorescenceValues.size());
        Log.d("LinearRegression", "Concentration values size: " + concentrationValues.size());

        if (fluorescenceValues.size() != concentrationValues.size()) {
            throw new IllegalArgumentException("Lists must have the same size");
        }

        this.fluorescenceValues = fluorescenceValues;
        this.concentrationValues = concentrationValues;
        leastSquaresAnalysis();
    }

    private void leastSquaresAnalysis() {
        // Create a SimpleRegression instance
        regression = new SimpleRegression();
        // Add data points to the regression model
        for (int i = 0; i < fluorescenceValues.size(); i++) {
            regression.addData(fluorescenceValues.get(i), concentrationValues.get(i));
            Log.d("LinearRegression","Adding in x = " + fluorescenceValues.get(i)+ " and y = "+concentrationValues.get(i));
        }
        // Perform linear regression
        m_slope = regression.getSlope();
        b_intercept = regression.getIntercept();
    }

    // redict a value given a new sample using the linear regression model
    public Double predict(Double newFluorescence) {
        return regression.predict(newFluorescence);
    }


}
