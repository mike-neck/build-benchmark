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
package com.example.generator.workers;

import com.example.generator.Interface;
import com.example.generator.LoggingApp;
import com.example.generator.Name;
import com.example.generator.TypedJavaDefinitionFactory;
import org.jetbrains.annotations.NotNull;

public class DomainIdFactory implements TypedJavaDefinitionFactory<DomainId> {
  @Override
  public @NotNull DomainId create(
      @NotNull Name name, @NotNull LoggingApp logging, @NotNull Interface inf) {
    return new DomainId(name);
  }
}
