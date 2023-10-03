package css.cecprototype2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ChemicalAnalysis {
    private Double chemicalReading;

    private Integer pixelRed;
    private Integer pixelGreen;
    private Integer pixelBlue;

    public ChemicalAnalysis() {}

    // Method to calculate and set the chemical reading based on RGB values
    public Double runAnalysis(Integer red, Integer green, Integer blue)
    {
        // Replace this logic with your actual chemical analysis calculation
        // For demonstration purposes, we'll calculate it based on the sum of RGB values
        Double sumRGB = Double.parseDouble(red.toString()) + Double.parseDouble(green.toString()) + Double.parseDouble(blue.toString());

        // implement more complex calculations here
        // For example, you might want to convert RGB to a specific chemical property.
        this.chemicalReading = sumRGB;
        return sumRGB;
    }


    // Method to get the stored chemical reading
    public Double getChemicalReading()
    {
        return this.chemicalReading;
    }


}
