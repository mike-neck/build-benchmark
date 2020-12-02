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
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public enum GeneratorLog {
  NO {
    @Override
    <T> void iterate(
        @NotNull Iterable<T> iterable, @NotNull Function<? super T, ? extends String> eachAction) {
      iterable.forEach(eachAction::apply);
    }
  },
  SIZE {
    @Override
    <T> void iterate(
        @NotNull Iterable<T> iterable, @NotNull Function<? super T, ? extends String> eachAction) {
      int size = 0;
      for (T target : iterable) {
        eachAction.apply(target);
        size++;
      }
      System.out.printf("%d performed%n", size);
    }
  },
  ALL {
    @Override
    <T> void iterate(
        @NotNull Iterable<T> iterable, @NotNull Function<? super T, ? extends String> eachAction) {
      for (T target : iterable) {
        String result = eachAction.apply(target);
        System.out.println(result);
      }
    }
  },
  ;

  abstract <@NotNull T> void iterate(
      @NotNull Iterable<T> iterable,
      @NotNull Function<@NotNull ? super T, @NotNull ? extends String> eachAction);

  @NotNull
  static GeneratorLog fromEnvironment() {
    String log = System.getenv("LOG");
    if (log == null || log.isEmpty() || log.isBlank()) {
      return ALL;
    }
    return fromString(log);
  }

  @NotNull
  static GeneratorLog fromString(@NotNull String input) {
    String upperCase = input.toUpperCase();
    return Arrays.stream(values())
        .filter(gl -> upperCase.equals(gl.name()))
        .findFirst()
        .orElse(ALL);
  }
}
