/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.apache.hc.core5.util;

import java.text.ParseException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

/**
 * Represents a timeout value as a non-negative {@code long} time and {@link TimeUnit}.
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class Timeout extends TimeValue {

    /**
     * Creates a Timeout from a Duration.
     *
     * @param duration the time duration.
     * @return a Timeout.
     * @since 5.2
     */
    public static Timeout of(final Duration duration) {
        final long seconds = duration.getSeconds();
        final long nanoOfSecond = duration.getNano();
        if (seconds == 0) {
            // no conversion
            return of(nanoOfSecond, TimeUnit.NANOSECONDS);
        } else if (nanoOfSecond == 0) {
            // no conversion
            return of(seconds, TimeUnit.SECONDS);
        }
        // conversion attempts
        try {
            return of(duration.toNanos(), TimeUnit.NANOSECONDS);
        } catch (final ArithmeticException e) {
            try {
                return of(duration.toMillis(), TimeUnit.MILLISECONDS);
            } catch (final ArithmeticException e1) {
                // backstop
                return of(seconds, TimeUnit.SECONDS);
            }
        }
    }

    /**
     * Creates a Timeout.
     *
     * @param duration the time duration.
     * @param timeUnit the time unit for the given duration.
     * @return a Timeout
     */
    public static Timeout of(final long duration, final TimeUnit timeUnit) {
        return new Timeout(duration, timeUnit);
    }

    /**
     * Parses a Timeout in the format {@code <Integer><SPACE><TimeUnit>}, for example {@code "1,200 MILLISECONDS"}
     *
     * @param value the TimeValue to parse
     * @return a new TimeValue
     * @throws ParseException if the number cannot be parsed
     */
    public static Timeout parse(final String value) throws ParseException {
        return TimeValue.parse(value).toTimeout();
    }

    Timeout(final long duration, final TimeUnit timeUnit) {
        super(Args.notNegative(duration, "duration"), Args.notNull(timeUnit, "timeUnit"));
    }
}
