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

import java.util.ServiceLoader;
import org.jetbrains.annotations.NotNull;

public interface JavaDefinitionFactory {

  @NotNull
  JavaDefinition create(@NotNull Name name, @NotNull LoggingApp logging, @NotNull Interface inf);

  static Iterable<JavaDefinitionFactory> factories() {
    return ServiceLoader.load(JavaDefinitionFactory.class);
  }
}
