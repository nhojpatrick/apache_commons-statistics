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

import org.apache.commons.rng.UniformRandomProvider;

/**
 * Base interface for distributions on the reals.
 */
public interface ContinuousDistribution {
    /**
     * Returns the probability density function (PDF) of this distribution
     * evaluated at the specified point {@code x}.
     * In general, the PDF is the derivative of the {@link #cumulativeProbability(double) CDF}.
     * If the derivative does not exist at {@code x}, then an appropriate
     * replacement should be returned, e.g. {@code Double.POSITIVE_INFINITY},
     * {@code Double.NaN}, or the limit inferior or limit superior of the
     * difference quotient.
     *
     * @param x Point at which the PDF is evaluated.
     * @return the value of the probability density function at {@code x}.
     */
    double density(double x);

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(x0 < X <= x1)}.
     * The default implementation uses the identity
     * {@code P(x0 < X <= x1) = P(X <= x1) - P(X <= x0)}
     *
     * @param x0 Lower bound (exclusive).
     * @param x1 Upper bound (inclusive).
     * @return the probability that a random variable with this distribution
     * takes a value between {@code x0} and {@code x1},  excluding the lower
     * and including the upper endpoint.
     * @throws IllegalArgumentException if {@code x0 > x1}.
     */
    default double probability(double x0,
                               double x1) {
        if (x0 > x1) {
            throw new DistributionException(DistributionException.INVALID_RANGE_LOW_GT_HIGH, x0, x1);
        }
        return cumulativeProbability(x1) - cumulativeProbability(x0);
    }

    /**
     * Returns the natural logarithm of the probability density function
     * (PDF) of this distribution evaluated at the specified point {@code x}.
     *
     * @param x Point at which the PDF is evaluated.
     * @return the logarithm of the value of the probability density function
     * at {@code x}.
     */
    default double logDensity(double x) {
        return Math.log(density(x));
    }

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X <= x)}.
     * In other words, this method represents the (cumulative) distribution
     * function (CDF) for this distribution.
     *
     * @param x Point at which the CDF is evaluated.
     * @return the probability that a random variable with this
     * distribution takes a value less than or equal to {@code x}.
     */
    double cumulativeProbability(double x);

    /**
     * For a random variable {@code X} whose values are distributed according
     * to this distribution, this method returns {@code P(X > x)}.
     * In other words, this method represents the complementary cumulative
     * distribution function.
     *
     * <p>By default, this is defined as {@code 1 - cumulativeProbability(x)}, but
     * the specific implementation may be more accurate.
     *
     * @param x Point at which the survival function is evaluated.
     * @return the probability that a random variable with this
     * distribution takes a value greater than {@code x}.
     */
    default double survivalProbability(double x) {
        return 1.0 - cumulativeProbability(x);
    }

    /**
     * Computes the quantile function of this distribution. For a random
     * variable {@code X} distributed according to this distribution, the
     * returned value is
     * <ul>
     * <li>{@code inf{x in R | P(X<=x) >= p}} for {@code 0 < p <= 1},</li>
     * <li>{@code inf{x in R | P(X<=x) > 0}} for {@code p = 0}.</li>
     * </ul>
     *
     * @param p Cumulative probability.
     * @return the smallest {@code p}-quantile of this distribution
     * (largest 0-quantile for {@code p = 0}).
     * @throws IllegalArgumentException if {@code p < 0} or {@code p > 1}.
     */
    double inverseCumulativeProbability(double p);

    /**
     * Computes the inverse survival probability function of this distribution. For a random
     * variable {@code X} distributed according to this distribution, the
     * returned value is
     * <ul>
     * <li>{@code inf{x in R | P(X>=x) <= p}} for {@code 0 <= p < 1},</li>
     * <li>{@code inf{x in R | P(X>=x) < 1}} for {@code p = 1}.</li>
     * </ul>
     *
     * <p>By default, this is defined as {@code inverseCumulativeProbability(1 - p)}, but
     * the specific implementation may be more accurate.
     *
     * @param p Survival probability.
     * @return the smallest {@code (1-p)}-quantile of this distribution
     * (largest 0-quantile for {@code p = 1}).
     * @throws IllegalArgumentException if {@code p < 0} or {@code p > 1}.
     */
    default double inverseSurvivalProbability(double p) {
        return inverseCumulativeProbability(1 - p);
    }

    /**
     * Gets the mean of this distribution.
     *
     * @return the mean, or {@code Double.NaN} if it is not defined.
     */
    double getMean();

    /**
     * Gets the variance of this distribution.
     *
     * @return the variance, or {@code Double.NaN} if it is not defined.
     */
    double getVariance();

    /**
     * Gets the lower bound of the support.
     * It must return the same value as
     * {@code inverseCumulativeProbability(0)}, i.e.
     * {@code inf {x in R | P(X <= x) > 0}}.
     *
     * @return the lower bound of the support.
     */
    double getSupportLowerBound();

    /**
     * Gets the upper bound of the support.
     * It must return the same
     * value as {@code inverseCumulativeProbability(1)}, i.e.
     * {@code inf {x in R | P(X <= x) = 1}}.
     *
     * @return the upper bound of the support.
     */
    double getSupportUpperBound();

    /**
     * Creates a sampler.
     *
     * @param rng Generator of uniformly distributed numbers.
     * @return a sampler that produces random numbers according this
     * distribution.
     */
    Sampler createSampler(UniformRandomProvider rng);

    /**
     * Sampling functionality.
     */
    interface Sampler {
        /**
         * Generates a random value sampled from this distribution.
         *
         * @return a random value.
         */
        double sample();
    }
}
