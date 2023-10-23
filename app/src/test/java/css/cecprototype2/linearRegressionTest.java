package css.cecprototype2;

import junit.framework.TestCase;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class linearRegressionTest extends TestCase {

    @Test
    public void testTwoValueTest() {
    // Arrange
    List<Double> x = Arrays.asList(10.0, 20.0);
    List<Double> y = Arrays.asList(100.0, 150.0);
    // Act
    linearRegression lenReg = new linearRegression(x,y);
    // Assert
    assertEquals(5.0, lenReg.m_slope, 0.01);
    assertEquals(50.0, lenReg.b_intercept, 0.01);
    }

    @Test
    public void testFourValueTest() {
        // Arrange
        List<Double> x = Arrays.asList(0.2, 0.4, 0.6, 0.8);
        List<Double> y = Arrays.asList(1200.0, 1310.0, 1430.0,1580.0);
        // Act
        linearRegression lenReg = new linearRegression(x,y);
        // Assert
        assertEquals(630.0, lenReg.m_slope, 0.01);
        assertEquals(1065.0, lenReg.b_intercept, 0.01);
    }
}