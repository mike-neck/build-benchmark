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

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;

public class IdGeneratorFactory {

  private final AtomicReference<IdGenerator> ref = new AtomicReference<>();

  public IdGenerator idGenerator() {
    return ref.updateAndGet(
        (pre) -> {
          if (pre != null) {
            return pre;
          }
          return ServiceLoader.load(IdGenerator.class)
              .findFirst()
              .orElseThrow(
                  () -> new IllegalStateException("An implementation of IdGenerator not found"));
        });
  }
}
