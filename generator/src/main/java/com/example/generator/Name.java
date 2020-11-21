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

import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public record Name(@NotNull String rawValue) {

  @NotNull
  String typeName() {
    return formatted(String::toUpperCase);
  }

  @NotNull
  String fieldName() {
    return formatted(String::toLowerCase);
  }

  @NotNull
  private String formatted(@NotNull Function<? super String, ? extends String> transform) {
    String first = this.rawValue.substring(0, 1);
    return "%s%s".formatted(transform.apply(first), this.rawValue.substring(1));
  }
}
