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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

public interface JavaDefinition {

  @NotNull String packageName = "com.example.generated";

  @NotNull
  String javaName();

  @NotNull
  default ClassName type() {
    return ClassName.get(packageName, javaName());
  }

  @NotNull
  JavaFile create();

  default void writeTo(@NotNull Path rootPath) throws IOException {
    JavaFile javaFile = create();
    Function<Path, Path> toJavaPath = pathEditor();
    javaFile.writeTo(toJavaPath.apply(rootPath));
  }

  @NotNull
  default Function<Path, Path> pathEditor() {
    String[] packages = packageName.split("\\.");
    Function<Path, Path> function =
        Arrays.stream(packages)
            .<Function<Path, Path>>map(pname -> path -> path.resolve(pname))
            .reduce(Function.identity(), Function::andThen);
    return function.andThen(path -> path.resolve("%s.java".formatted(javaName())));
  }
}
