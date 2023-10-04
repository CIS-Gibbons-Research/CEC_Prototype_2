package css.cecprototype2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ChemicalAnalysis {
    private Map<String, Double> chemicalReading;
    private Map<String, Integer> myMap;

    /**
     * constructor -- fills map inMap by calling runSingleAnalysis for each value
     * @param inMap
     */
    public ChemicalAnalysis(Map<String, Integer> inMap)
    {
        myMap = inMap;
        //example analysis
        for (Integer i : myMap.values())
        {
            chemicalReading.put(i.toString(), runSingleAnalysis(i));
        }
    }
    /**
     * Method to calculate and set the chemical reading based on RGB values
     * TODO: implement code processing via linear regression model
     * @param index
     * @return the processed analysis as a Double value.
     */
    public Double runSingleAnalysis(int index)
    {
        //run regression here
        Double exampleValue = Double.parseDouble(String.valueOf(index));
        // implement more complex calculations here
        // For example, you might want to convert RGB to a specific chemical property.
        return exampleValue;
    }

    /**
     *
     * @return the chemicalReading value
     */
    public Double getChemicalReading(String key)
    {
        return chemicalReading.getOrDefault(key, (Double) 0.0);
    }

}
