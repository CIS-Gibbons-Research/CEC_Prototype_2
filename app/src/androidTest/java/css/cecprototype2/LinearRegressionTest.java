package css.cecprototype2;

import junit.framework.TestCase;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import css.cecprototype2.analysis_logic.LinearRegression;

public class LinearRegressionTest extends TestCase {
    @Test
    public void testTwoValueTest() {
        // Arrange
        List<Double> x = Arrays.asList(10.0, 20.0);
        List<Double> y = Arrays.asList(100.0, 150.0);
        // Act
        LinearRegression lenReg = new LinearRegression(x,y);
        // Assert
        assertEquals(5.0, lenReg.m_slope, 0.01);
        assertEquals(50.0, lenReg.b_intercept, 0.01);
    }

    @Test
    public void testFourValueTest() {
        // Arrange
        List<Double> x = Arrays.asList(1200.0, 1310.0, 1430.0,1580.0);
        List<Double> y = Arrays.asList(0.2, 0.4, 0.6, 0.8);
        // Act
        LinearRegression lenReg = new LinearRegression(x,y);
        // Assert
        assertEquals(0.00157, lenReg.m_slope, 0.0001);
        assertEquals(-1.6789, lenReg.b_intercept, 0.0001);
    }
    @Test
    public void testPredict() {
        // Arrange
        List<Double> x = Arrays.asList(1200.0, 1310.0, 1430.0,1580.0);
        List<Double> y = Arrays.asList(0.2, 0.4, 0.6, 0.8);
        // Act
        LinearRegression lenReg = new LinearRegression(x,y);
        // Assert
        assertEquals(0.4, lenReg.predict(1310.0), 0.05);
        assertEquals(0.8, lenReg.predict(1580.0), 0.05);
    }
}
