package css.cecprototype2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ChemicalAnalysis {
    private Double chemicalReading;

    private Map<String, Integer> myMap;

    public ChemicalAnalysis(Map<String, Integer> inMap)
    {
        myMap = inMap;

        //example analysis
        for (Integer i : myMap.values())
        {
            runSingleAnalysis(i);
        }
    }

    // Method to calculate and set the chemical reading based on RGB values
    public Double runSingleAnalysis(int index)
    {
        //run regression here
        Double exampleValue = Double.parseDouble(String.valueOf(index));
        // implement more complex calculations here
        // For example, you might want to convert RGB to a specific chemical property.
        return exampleValue;
    }

    // Method to get the stored chemical reading
    public Double getChemicalReading()
    {
        return this.chemicalReading;
    }


}
