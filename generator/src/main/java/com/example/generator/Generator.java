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

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.jetbrains.annotations.NotNull;

public class Generator {

  @NotNull private final Interface inf;
  @NotNull private final JavaDefinition javaDefinition;

  Generator(@NotNull Interface inf, @NotNull JavaDefinition javaDefinition) {
    this.inf = inf;
    this.javaDefinition = javaDefinition;
  }

  Path filePath() {
    return Path.of(inf.projectName(), "src", "main", "java");
  }

  String write() {
    Path root = filePath();
    try {
      javaDefinition.writeTo(root);
      return "wrote %s".formatted(javaDefinition);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private static LoggingApp loggingApp(String[] args) {
    return Arrays.stream(args)
        .map(LoggingApp::fromStringOptionally)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElse(LoggingApp.NO_LOGGING);
  }

  private static Interface inf(String[] args) {
    return Arrays.stream(args)
        .map(Interface::fromString)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .findFirst()
        .orElse(Interface.USE);
  }

  private static Iterable<Generator> newGenerators(
      @NotNull Interface inf, @NotNull LoggingApp logging, @NotNull Domains domains) {
    return StreamSupport.stream(domains.spliterator(), false)
        .flatMap(
            name ->
                StreamSupport.stream(JavaDefinitionFactory.factories().spliterator(), false)
                    .map(factory -> factory.create(name, logging, inf)))
        .map(def -> new Generator(inf, def))
        .collect(Collectors.toUnmodifiableList());
  }

  public static void main(String[] args) {
    LoggingApp logging = loggingApp(args);
    Interface inf = inf(args);
    GeneratorLog generatorLog = GeneratorLog.fromEnvironment();

    Domains domains = Domains.load();
    Iterable<Generator> generators = newGenerators(inf, logging, domains);
    generatorLog.iterate(generators, Generator::write);
  }
}
