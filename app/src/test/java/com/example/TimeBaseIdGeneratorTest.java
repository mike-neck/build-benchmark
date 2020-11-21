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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.nio.ByteBuffer;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class TimeBaseIdGeneratorTest {

  @Test
  void getLong() {
    Clock fixed =
        Clock.fixed(
            OffsetDateTime.of(2020, 4, 10, 0, 0, 0, 200, ZoneOffset.UTC).toInstant(),
            ZoneOffset.UTC);
    TimeBaseIdGenerator idGenerator =
        new TimeBaseIdGenerator(
            fixed, OffsetDateTime.of(2020, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toInstant());

    long id1 = idGenerator.getLong();
    long id2 = idGenerator.getLong();

    long expectedId1 = ByteBuffer.allocate(8).putInt(24 * 60 * 60 * 100).putInt(1).flip().getLong();

    assertAll(
        () -> assertThat(id1).describedAs("id1").isEqualTo(expectedId1),
        () -> assertThat(id2).describedAs("id2").isGreaterThan(id1).isEqualTo(id1 + 1));
  }
}
