/*
 * Copyright 2020 Shinya Mochida
 *
 * Licensed under the Apache License,Version2.0(the"License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,software
 * Distributed under the License is distributed on an"AS IS"BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeBaseIdGenerator implements IdGenerator {

  private final Clock clock;
  private final Instant base;
  private final ConcurrentMap<Instant, AtomicInteger> map;

  public TimeBaseIdGenerator() {
    this(Clock.systemUTC(), OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant());
  }

  TimeBaseIdGenerator(Clock clock, Instant base) {
    this.clock = clock;
    this.base = base;
    this.map = new ConcurrentHashMap<>();
  }

  @Override
  public long getLong() {
    Instant now = Instant.now(clock).truncatedTo(ChronoUnit.SECONDS);
    AtomicInteger atomicInteger = map.computeIfAbsent(now, instant -> new AtomicInteger(0));
    int duration = (int) (Duration.between(base, now).toMillis() / 1_000L);
    ByteBuffer buffer = ByteBuffer.allocate(8);
    return buffer.putInt(duration).putInt(atomicInteger.incrementAndGet()).flip().getLong();
  }
}
