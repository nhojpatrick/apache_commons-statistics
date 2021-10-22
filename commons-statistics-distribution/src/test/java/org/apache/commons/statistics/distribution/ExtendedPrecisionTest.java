/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.statistics.distribution;

import java.math.BigDecimal;
import java.math.MathContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

/**
 * Test for {@link ExtendedPrecision}.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ExtendedPrecisionTest {
    /** sqrt(2). */
    private static final double ROOT2 = Math.sqrt(2.0);
    /** The sum of the squared ULP error for the first standard computation. */
    private static final RMS RMS1 = new RMS();
    /** The sum of the squared ULP error for the second standard computation. */
    private static final RMS RMS2 = new RMS();

    /**
     * Class to compute the root mean squared error (RMS).
     * @see <a href="https://en.wikipedia.org/wiki/Root_mean_square">Wikipedia: RMS</a>
     */
    private static class RMS {
        private double ss;
        private double max;
        private int n;

        /**
         * @param x Value (assumed to be positive)
         */
        void add(double x) {
            // Overflow is not supported.
            // Assume the expected and actual are quite close when measuring the RMS.
            ss += x * x;
            n++;
            // Absolute error when detecting the maximum
            x = Math.abs(x);
            max = max < x ? x : max;
        }

        /**
         * Gets the maximum error.
         *
         * <p>This is not used for assertions. It can be used to set maximum ULP thresholds
         * for test data if the TestUtils.assertEquals method is used with a large maxUlps
         * to measure the ulp (and effectively ignore failures) and the maximum reported
         * as the end of testing.
         *
         * @return maximum error
         */
        double getMax() {
            return max;
        }

        /**
         * Gets the root mean squared error (RMS).
         *
         * <p> Note: If no data has been added this will return 0/0 = nan.
         * This prevents using in assertions without adding data.
         *
         * @return root mean squared error (RMS)
         */
        double getRMS() {
            return Math.sqrt(ss / n);
        }
    }

    @Test
    void testSqrt2xxUnderAndOverflow() {
        final double x = 1.5;
        final double e = 2.12132034355964257320253308631;
        Assertions.assertEquals(e, ExtendedPrecision.sqrt2xx(x));
        for (final int i : new int[] {-1000, -600, -200, 200, 600, 1000}) {
            final double scale = Math.scalb(1.0, i);
            final double x1 = x * scale;
            final double e1 = e * scale;
            Assertions.assertEquals(e1, ExtendedPrecision.sqrt2xx(x1), () -> Double.toString(x1));
        }
    }

    @Test
    void testSqrt2xxExtremes() {
        // Handle big numbers
        Assertions.assertEquals(Double.POSITIVE_INFINITY, ExtendedPrecision.sqrt2xx(Double.MAX_VALUE));
        Assertions.assertEquals(Double.POSITIVE_INFINITY, ExtendedPrecision.sqrt2xx(Double.POSITIVE_INFINITY));
        Assertions.assertEquals(0.0, ExtendedPrecision.sqrt2xx(0));
        Assertions.assertEquals(ROOT2, ExtendedPrecision.sqrt2xx(1));
        Assertions.assertEquals(Math.sqrt(8), ExtendedPrecision.sqrt2xx(2));
        // Handle sub-normal numbers
        for (int i = 2; i <= 10; i++) {
            Assertions.assertEquals(i * Double.MIN_VALUE * Math.sqrt(2), ExtendedPrecision.sqrt2xx(i * Double.MIN_VALUE));
        }
        // Currently the argument is assumed to be positive.
        Assertions.assertEquals(Double.NaN, ExtendedPrecision.sqrt2xx(Double.NaN));
        // Big negative numbers overflow and the extended precision computation generates NaN.
        Assertions.assertEquals(Double.NaN, ExtendedPrecision.sqrt2xx(-1e300));
    }

    /**
     * Test the extended precision {@code sqrt(2 * x * x)}. The expected result
     * is an exact computation. For comparison ulp errors are collected for
     * two standard precision computations.
     *
     * @param x Value x
     * @param expected Expected result of {@code sqrt(2 * x * x)}.
     */
    @ParameterizedTest
    @Order(1)
    @CsvFileSource(resources = "sqrt2xx.csv")
    void testSqrt2xx(double x, BigDecimal expected) {
        final double e = expected.doubleValue();
        Assertions.assertEquals(e, ExtendedPrecision.sqrt2xx(x));
        // Compute error for the standard computations
        addError(Math.sqrt(2 * x * x), expected, e, RMS1);
        addError(x * ROOT2, expected, e, RMS2);
    }

    @Test
    void testSqrt2xxStandardPrecision1() {
        // Typical result:   max   0.7780  rms   0.2144
        assertSqrt2xxStandardPrecision(RMS1, 0.9, 0.3);
    }

    @Test
    void testSqrt2xxStandardPrecision2() {
        // Typical result:   max   1.0598  rms   0.4781
        assertSqrt2xxStandardPrecision(RMS2, 1.3, 0.6);
    }

    @Test
    private static void assertSqrt2xxStandardPrecision(RMS rms, double maxError, double rmsError) {
        Assertions.assertTrue(rms.getMax() < maxError, () -> "max error: " + rms.getMax());
        Assertions.assertTrue(rms.getRMS() < rmsError, () -> "rms error: " + rms.getRMS());
    }

    private static void addError(double z, BigDecimal expected, double e, RMS rms) {
        double error;
        if (z == e) {
            error = 0;
        } else {
            // Compute ULP error
            error = expected.subtract(new BigDecimal(z))
                .divide(new BigDecimal(Math.ulp(e)), MathContext.DECIMAL64).doubleValue();
        }
        rms.add(error);
    }
}