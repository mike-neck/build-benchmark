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

import java.util.Optional;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public enum Interface {
  USE("with") {
    @Override
    public <T> @NotNull Optional<T> ifWithoutInterface(
        @NotNull Supplier<@NotNull ? extends T> object) {
      return Optional.empty();
    }
  },
  NO("without") {
    @Override
    public <T> @NotNull Optional<T> ifWithoutInterface(
        @NotNull Supplier<@NotNull ? extends T> object) {
      return Optional.of(object.get());
    }
  },
  ;

  private final String withInterface;

  Interface(String withInterface) {
    this.withInterface = withInterface;
  }

  public String projectName() {
    return "%s-interface".formatted(withInterface);
  }

  @NotNull
  public abstract <T> Optional<T> ifWithoutInterface(
      @NotNull Supplier<@NotNull ? extends T> object);
}
