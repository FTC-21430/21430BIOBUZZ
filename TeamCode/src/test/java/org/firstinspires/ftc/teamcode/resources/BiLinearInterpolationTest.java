package org.firstinspires.ftc.teamcode.resources;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.broombots.sweep.Classes.LerpFunction;
import com.broombots.sweep.Classes.LerpFunctionBuilder;

import org.junit.Before;
import org.junit.Test;

/** Exercises example launcher maps; these values are illustrative, not robot calibration. */
public class BiLinearInterpolationTest {
    private static final double EPSILON = 1e-7;
    private BiLinearInterpolation interpolation;
    private TurretTrajectoryKinematics launcher;

    @Before
    public void setUp() {
        // Goal height: 40-50 inches; distance to the goal: 5-72 inches.
        // Example output ranges: 0-4000 RPM and 65-85 degrees.
        // Each curve contributes half of the output; tune the points independently.
        LerpFunction distanceToSpeed = new LerpFunctionBuilder()
                .addPoint(5, 0).addPoint(38.5, 1500).addPoint(72, 4000).build();
        LerpFunction distanceToAngle = new LerpFunctionBuilder()
                .addPoint(5, 85).addPoint(38.5, 75).addPoint(72, 65).build();
        LerpFunction heightToSpeed = new LerpFunctionBuilder()
                .addPoint(40, 0).addPoint(45, 2500).addPoint(50, 4000).build();
        LerpFunction heightToAngle = new LerpFunctionBuilder()
                .addPoint(40, 65).addPoint(45, 75).addPoint(50, 85).build();

        interpolation = new BiLinearInterpolation(
                distanceToSpeed, distanceToAngle, heightToSpeed, heightToAngle);
        launcher = new TurretTrajectoryKinematics(
                distanceToSpeed, distanceToAngle, heightToSpeed, heightToAngle);
    }

    @Test
    public void exampleLauncherReturnsExpectedCornerAndInteriorSolutions() {
        // Each row is distance, height, speed, angle.
        double[][] cases = {
                {5, 40, 0, 75},
                {5, 50, 2000, 85},
                {72, 40, 2000, 65},
                {72, 50, 4000, 75},
                {38.5, 45, 2000, 75},
                {38.5, 40, 750, 70},
                {38.5, 50, 2750, 80},
                {5, 45, 1250, 80},
                {72, 45, 3250, 70},
                {21.75, 42.5, 1000, 75},
                {21.75, 47.5, 2000, 80},
                {55.25, 42.5, 2000, 70},
                {55.25, 47.5, 3000, 75}
        };

        for (double[] example : cases) {
            double[] expected = {example[2], example[3]};
            String description = "distance=" + example[0] + ", height=" + example[1];
            assertArrayEquals(description, expected,
                    interpolation.getValues(example[0], example[1]), EPSILON);
            assertArrayEquals(description, expected,
                    launcher.getSolution(example[0], example[1]), EPSILON);
            assertEquals(description, example[2],
                    interpolation.getValue1(example[0], example[1]), EPSILON);
            assertEquals(description, example[3],
                    interpolation.getValue2(example[0], example[1]), EPSILON);
        }
    }

    @Test
    public void exampleOutputsStayWithinSpecifiedRangesAcrossGrid() {
        for (double distance = 5; distance <= 72; distance += 1) {
            for (double height = 40; height <= 50; height += 0.5) {
                double[] solution = launcher.getSolution(distance, height);
                assertEquals(2, solution.length);
                assertTrue("speed at " + distance + ", " + height,
                        solution[0] >= 0 && solution[0] <= 4000);
                assertTrue("angle at " + distance + ", " + height,
                        solution[1] >= 65 && solution[1] <= 85);
            }
        }
    }

    @Test
    public void outOfRangeInputsUseCurveEndpointsIndependently() {
        assertArrayEquals(launcher.getSolution(5, 45), launcher.getSolution(-20, 45), EPSILON);
        assertArrayEquals(launcher.getSolution(72, 45), launcher.getSolution(100, 45), EPSILON);
        assertArrayEquals(launcher.getSolution(38.5, 40), launcher.getSolution(38.5, 0), EPSILON);
        assertArrayEquals(launcher.getSolution(38.5, 50), launcher.getSolution(38.5, 100), EPSILON);
        assertArrayEquals(new double[]{2000, 85}, launcher.getSolution(-20, 100), EPSILON);
    }

    @Test
    public void eachBuilderCanBeTunedWithoutChangingOtherCurves() {
        LerpFunction tunedDistanceSpeed = new LerpFunctionBuilder()
                .addPoint(5, 0).addPoint(38.5, 2000).addPoint(72, 4000).build();
        TurretTrajectoryKinematics tunedLauncher = new TurretTrajectoryKinematics(
                tunedDistanceSpeed, interpolation.key1ToValue2,
                interpolation.key2ToValue1, interpolation.key2ToValue2);

        assertArrayEquals(new double[]{2000, 75}, launcher.getSolution(38.5, 45), EPSILON);
        assertArrayEquals(new double[]{2250, 75}, tunedLauncher.getSolution(38.5, 45), EPSILON);
        assertArrayEquals(launcher.getSolution(5, 40), tunedLauncher.getSolution(5, 40), EPSILON);
    }
}
