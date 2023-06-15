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
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.apache.hc.core5.annotation.Contract;
import org.apache.hc.core5.annotation.ThreadingBehavior;

/**
 * Represents a time value as a {@code long} time and a {@link TimeUnit}.
 *
 * @since 5.0
 */
@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class TimeValue implements Comparable<TimeValue> {
    /**
     * Creates a TimeValue.
     *
     * @param duration the time duration in the given {@code timeUnit}.
     * @param timeUnit the time unit for the given duration.
     * @return a Timeout.
     */
    public static TimeValue of(final long duration, final TimeUnit timeUnit) {
        return new TimeValue(duration, timeUnit);
    }

    /**
     * Creates a TimeValue from a Duration.
     *
     * @param duration the time duration.
     * @return a Timeout
     * @since 5.2
     */
    public static TimeValue of(final Duration duration) {
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
     * Parses a TimeValue in the format {@code <Long><SPACE><TimeUnit>}, for example {@code "1200 MILLISECONDS"}.
     * <p>
     * Parses:
     * </p>
     * <ul>
     * <li>{@code "1200 MILLISECONDS"}.</li>
     * <li>{@code " 1200 MILLISECONDS "}, spaces are ignored.</li>
     * <li>{@code "1 MINUTE"}, singular units.</li>
     * <li></li>
     * </ul>
     *
     *
     * @param value the TimeValue to parse
     * @return a new TimeValue
     * @throws ParseException if the number cannot be parsed
     */
    public static TimeValue parse(final String value) throws ParseException {
        final String split[] = value.trim().split("\\s+");
        if (split.length < 2) {
            throw new IllegalArgumentException(
                    String.format("Expected format for <Long><SPACE><java.util.concurrent.TimeUnit>: %s", value));
        }
        final String clean0 = split[0].trim();
        final String clean1 = split[1].trim().toUpperCase(Locale.ROOT);
        final String timeUnitStr = clean1.endsWith("S") ? clean1 : clean1 + "S";
        return TimeValue.of(Long.parseLong(clean0), TimeUnit.valueOf(timeUnitStr));
    }

    private final long duration;

    private final TimeUnit timeUnit;

    TimeValue(final long duration, final TimeUnit timeUnit) {
        super();
        this.duration = duration;
        this.timeUnit = Args.notNull(timeUnit, "timeUnit");
    }

    public long convert(final TimeUnit targetTimeUnit) {
        Args.notNull(targetTimeUnit, "timeUnit");
        return targetTimeUnit.convert(duration, timeUnit);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof TimeValue) {
            final TimeValue that = (TimeValue) obj;
            final long thisDuration = this.convert(TimeUnit.NANOSECONDS);
            final long thatDuration = that.convert(TimeUnit.NANOSECONDS);
            return thisDuration == thatDuration;
        }
        return false;
    }

    public TimeUnit getTimeUnit() {
        return timeUnit;
    }

    @Override
    public int hashCode() {
        int hash = LangUtils.HASH_SEED;
        hash = LangUtils.hashCode(hash, this.convert(TimeUnit.NANOSECONDS));
        return hash;
    }

    public TimeValue min(final TimeValue other) {
        return this.compareTo(other) > 0 ? other : this;
    }

    private TimeUnit min(final TimeUnit other) {
        return scale() > scale(other) ? other : getTimeUnit();
    }

    private int scale() {
        return scale(timeUnit);
    }

    /**
     * Returns a made up scale for TimeUnits.
     *
     * @param tUnit
     *            a TimeUnit
     * @return a number from 1 to 7, where 1 is NANOSECONDS and 7 DAYS.
     */
    private int scale(final TimeUnit tUnit) {
        switch (tUnit) {
        case NANOSECONDS:
            return 1;
        case MICROSECONDS:
            return 2;
        case MILLISECONDS:
            return 3;
        case SECONDS:
            return 4;
        case MINUTES:
            return 5;
        case HOURS:
            return 6;
        case DAYS:
            return 7;
        default:
            // Should never happens unless Java adds to the enum.
            throw new IllegalStateException();
        }
    }

    @Override
    public int compareTo(final TimeValue other) {
        final TimeUnit targetTimeUnit = min(other.getTimeUnit());
        return Long.compare(convert(targetTimeUnit), other.convert(targetTimeUnit));
    }

    @Override
    public String toString() {
        return String.format("%d %s", duration, timeUnit);
    }

    public Timeout toTimeout() {
        return Timeout.of(duration, timeUnit);
    }

}
