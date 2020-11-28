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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.example.generator.JavaDefinition;
import com.squareup.javapoet.JavaFile;
import java.nio.file.Path;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

public class JavaDefinitionTest {

  @Test
  public void pathEditorTest() {
    SimpleDef simpleDef = new SimpleDef();
    Function<Path, Path> pathEditor = simpleDef.pathEditor();
    Path path = Path.of("foo", "bar");
    Path result = pathEditor.apply(path);
    assertAll(
        () -> assertThat(result.toString()).contains("foo/"),
        () -> assertThat(result.toString()).contains("bar/"),
        () -> assertThat(result.toString()).contains("com/"),
        () -> assertThat(result.toString()).contains("example/"),
        () -> assertThat(result.toString()).contains("generated/"),
        () -> assertThat(result.toString()).endsWith("SimpleDef.java"));
  }

  private static class SimpleDef implements JavaDefinition {
    @NotNull
    @Override
    public String javaName() {
      return "SimpleDef";
    }

    @Override
    public @NotNull String fieldName() {
      return "simpleDef";
    }

    @NotNull
    @Override
    public JavaFile create() {
      throw new UnsupportedOperationException(
          "JavaFile create() is not supported by this implementation.");
    }
  }
}
