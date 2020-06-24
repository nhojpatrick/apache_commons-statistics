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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases for BinomialDistribution. Extends DiscreteDistributionAbstractTest.
 * See class javadoc for DiscreteDistributionAbstractTest for details.
 *
 */
public class BinomialDistributionTest extends DiscreteDistributionAbstractTest {

    //---------------------- Override tolerance --------------------------------

    @BeforeEach
    public void customSetUp() {
        setTolerance(1e-12);
    }

    //-------------- Implementations for abstract methods ----------------------

    /** Creates the default discrete distribution instance to use in tests. */
    @Override
    public DiscreteDistribution makeDistribution() {
        return new BinomialDistribution(10, 0.70);
    }

    /** Creates the default probability density test input values. */
    @Override
    public int[] makeDensityTestPoints() {
        return new int[] {-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    }

    /**
     * Creates the default probability density test expected values.
     * Reference values are from R, version 2.15.3.
     */
    @Override
    public double[] makeDensityTestValues() {
        return new double[] {0d, 0.0000059049d, 0.000137781d, 0.0014467005,
                             0.009001692, 0.036756909, 0.1029193452, 0.200120949, 0.266827932,
                             0.2334744405, 0.121060821, 0.0282475249, 0d};
    }

    /** Creates the default cumulative probability density test input values */
    @Override
    public int[] makeCumulativeTestPoints() {
        return makeDensityTestPoints();
    }

    /**
     * Creates the default cumulative probability density test expected values.
     * Reference values are from R, version 2.15.3.
     */
    @Override
    public double[] makeCumulativeTestValues() {
        return new double[] {0d, 5.9049e-06, 0.0001436859, 0.0015903864, 0.0105920784,  0.0473489874,
                             0.1502683326, 0.3503892816, 0.6172172136, 0.8506916541, 0.9717524751, 1d, 1d};
    }

    /** Creates the default inverse cumulative probability test input values */
    @Override
    public double[] makeInverseCumulativeTestPoints() {
        return new double[] {0, 0.001d, 0.010d, 0.025d, 0.050d, 0.100d,
                             0.999d, 0.990d, 0.975d, 0.950d, 0.900d, 1d};
    }

    /**
     * Creates the default inverse cumulative probability density test expected
     * values
     */
    @Override
    public int[] makeInverseCumulativeTestValues() {
        return new int[] {0, 2, 3, 4, 5, 5, 10, 10, 10, 9, 9, 10};
    }

    //-------------------- Additional test cases -------------------------------

    /** Test degenerate case p = 0 */
    @Test
    public void testDegenerate0() {
        BinomialDistribution dist = new BinomialDistribution(5, 0.0d);
        setDistribution(dist);
        setCumulativeTestPoints(new int[] {-1, 0, 1, 5, 10});
        setCumulativeTestValues(new double[] {0d, 1d, 1d, 1d, 1d});
        setDensityTestPoints(new int[] {-1, 0, 1, 10, 11});
        setDensityTestValues(new double[] {0d, 1d, 0d, 0d, 0d});
        setInverseCumulativeTestPoints(new double[] {0.1d, 0.5d});
        setInverseCumulativeTestValues(new int[] {0, 0});
        verifyDensities();
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
        Assertions.assertEquals(0, dist.getSupportLowerBound());
        Assertions.assertEquals(0, dist.getSupportUpperBound());
    }

    /** Test degenerate case p = 1 */
    @Test
    public void testDegenerate1() {
        BinomialDistribution dist = new BinomialDistribution(5, 1.0d);
        setDistribution(dist);
        setCumulativeTestPoints(new int[] {-1, 0, 1, 2, 5, 10});
        setCumulativeTestValues(new double[] {0d, 0d, 0d, 0d, 1d, 1d});
        setDensityTestPoints(new int[] {-1, 0, 1, 2, 5, 10});
        setDensityTestValues(new double[] {0d, 0d, 0d, 0d, 1d, 0d});
        setInverseCumulativeTestPoints(new double[] {0.1d, 0.5d});
        setInverseCumulativeTestValues(new int[] {5, 5});
        verifyDensities();
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
        Assertions.assertEquals(5, dist.getSupportLowerBound());
        Assertions.assertEquals(5, dist.getSupportUpperBound());
    }

    /** Test degenerate case n = 0 */
    @Test
    public void testDegenerate2() {
        BinomialDistribution dist = new BinomialDistribution(0, 0.01d);
        setDistribution(dist);
        setCumulativeTestPoints(new int[] {-1, 0, 1, 2, 5, 10});
        setCumulativeTestValues(new double[] {0d, 1d, 1d, 1d, 1d, 1d});
        setDensityTestPoints(new int[] {-1, 0, 1, 2, 5, 10});
        setDensityTestValues(new double[] {0d, 1d, 0d, 0d, 0d, 0d});
        setInverseCumulativeTestPoints(new double[] {0.1d, 0.5d});
        setInverseCumulativeTestValues(new int[] {0, 0});
        verifyDensities();
        verifyCumulativeProbabilities();
        verifyInverseCumulativeProbabilities();
        Assertions.assertEquals(0, dist.getSupportLowerBound());
        Assertions.assertEquals(0, dist.getSupportUpperBound());
    }

    @Test
    public void testParameterAccessors() {
        for (final int n : new int[] {11, 42, 999}) {
            for (final double p : new double[] {0.1, 0.456, 0.999}) {
                final BinomialDistribution dist = new BinomialDistribution(n, p);
                Assertions.assertEquals(n, dist.getNumberOfTrials());
                Assertions.assertEquals(p, dist.getProbabilityOfSuccess());
            }
        }
    }

    @Test
    public void testConstructorPrecondition1() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BinomialDistribution(-1, 0.1));
    }

    @Test
    public void testConstructorPrecondition2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BinomialDistribution(10, -0.1));
    }

    @Test
    public void testConstructorPrecondition3() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new BinomialDistribution(10, 1.1));
    }

    @Test
    public void testMoments() {
        final double tol = 1e-9;
        BinomialDistribution dist;

        dist = new BinomialDistribution(10, 0.5);
        Assertions.assertEquals(10d * 0.5d, dist.getMean(), tol);
        Assertions.assertEquals(10d * 0.5d * 0.5d, dist.getVariance(), tol);

        dist = new BinomialDistribution(30, 0.3);
        Assertions.assertEquals(30d * 0.3d, dist.getMean(), tol);
        Assertions.assertEquals(30d * 0.3d * (1d - 0.3d), dist.getVariance(), tol);
    }

    @Test
    public void testMath718() {
        // for large trials the evaluation of ContinuedFraction was inaccurate
        // do a sweep over several large trials to test if the current implementation is
        // numerically stable.

        for (int trials = 500000; trials < 20000000; trials += 100000) {
            BinomialDistribution dist = new BinomialDistribution(trials, 0.5);
            int p = dist.inverseCumulativeProbability(0.5);
            Assertions.assertEquals(trials / 2, p);
        }
    }
}
