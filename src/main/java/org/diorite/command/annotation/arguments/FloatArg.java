/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017. Diorite (by Bartłomiej Mazur (aka GotoFinal))
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.diorite.command.annotation.arguments;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Float/Double argument settings.
 */
@Target({ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FloatArg
{
    /**
     * Basic argument settings.
     *
     * @return basic argument settings.
     */
    Arg value() default @Arg;

    /**
     * If Infinity values are allowed when parsing, allowing such values can cause serious bugs if not handled correctly.
     *
     * @return true if Infinity value is allowed.
     */
    boolean allowInfinity() default false;

    /**
     * If NaN values are allowed when parsing, allowing such values can cause serious bugs if not handled correctly.
     *
     * @return true if NaN value is allowed.
     */
    boolean allowNaN() default false;

    /**
     * Minimal value of number, multiple values can be used along with {@link #max()} setting to create multiple valid ranges.
     *
     * @return minimal value of number.
     */
    double[] min() default Double.NEGATIVE_INFINITY;

    /**
     * Maximal value of number, multiple values can be used along with {@link #min()} setting to create multiple valid ranges.
     *
     * @return maximal value of number.
     */
    double[] max() default Double.POSITIVE_INFINITY;

    /**
     * Array of invalid values.
     *
     * @return array of invalid values.
     */
    double[] invalidValues() default {};

    /**
     * Array of valid values, if empty then all values are allowed.
     *
     * @return array of valid values.
     */
    double[] validValues() default {};
}
