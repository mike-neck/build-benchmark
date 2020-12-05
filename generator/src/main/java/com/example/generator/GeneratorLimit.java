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
package com.example.generator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GeneratorLimit<@NotNull T> extends UnaryOperator<@NotNull Iterable<T>> {

  @NotNull
  static <T> GeneratorLimit<T> unlimited() {
    return collection -> collection;
  }

  @NotNull
  static <T> GeneratorLimit<T> limited(final int size) {
    assert size > 0 : "invalid number (%d), expected more than 0".formatted(size);
    return iterable -> {
      List<T> list =
          StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
      Collections.shuffle(list);
      return list.stream().limit(size).collect(Collectors.toUnmodifiableList());
    };
  }

  @NotNull
  static <@NotNull T> Optional<@NotNull GeneratorLimit<T>> fromString(
      @NotNull final String string) {
    if (!string.startsWith("limit:")) {
      return Optional.empty();
    }
    String expectInt = string.replace("limit:", "");
    try {
      int size = Integer.parseInt(expectInt);
      if (size < 0) {
        return Optional.empty();
      }
      if (size == 0) {
        return Optional.of(unlimited());
      }
      return Optional.of(limited(size));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  @NotNull
  static <@NotNull T> GeneratorLimit<T> fromParams(String... args) {
    return Arrays.stream(args)
        .<Optional<GeneratorLimit<T>>>map(GeneratorLimit::fromString)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElseGet(GeneratorLimit::unlimited);
  }
}
