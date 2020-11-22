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

import com.squareup.javapoet.CodeBlock;
import java.util.Arrays;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public enum LoggingApp {
  LOGGING("logging") {
    @NotNull
    @Override
    public CodeBlock loggingCode(@NotNull String methodName) {
      return CodeBlock.of("logger.info($S, $S)", "logging at {}", methodName);
    }
  },
  NO_LOGGING("no-logging") {
    @NotNull
    @Override
    public CodeBlock loggingCode(@NotNull String methodName) {
      return CodeBlock.builder().build();
    }
  },
  ;

  @NotNull
  public abstract CodeBlock loggingCode(@NotNull String methodName);

  private final String logging;

  LoggingApp(String logging) {
    this.logging = logging;
  }

  static Optional<LoggingApp> fromStringOptionally(String value) {
    return Arrays.stream(values()).filter(it -> it.logging.equals(value)).findFirst();
  }

  static LoggingApp fromString(String value) {
    return fromStringOptionally(value).orElse(NO_LOGGING);
  }
}
