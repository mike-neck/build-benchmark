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

import com.example.generator.Name;
import com.squareup.javapoet.JavaFile;
import org.junit.jupiter.api.Test;

public class DomainIdTest {

  @Test
  public void createTest() {
    DomainId domainId = new DomainId(new Name("foo"));
    assertAll(
        () -> assertThat(domainId.javaName()).isEqualTo("FooId"),
        () -> {
          JavaFile javaFile = domainId.create();
          StringBuilder builder = new StringBuilder();
          javaFile.writeTo(builder);
          assertThat(builder.toString())
              .contains(
                  "public final class FooId",
                  "public FooId(long value) {",
                  "package com.example.generated;",
                  "public boolean equals(Object other) {",
                  "if (this == other) return true;",
                  "if (!(other instanceof FooId)) return false;",
                  "FooId id = (FooId) other;",
                  "return value == id.value;",
                  "public int hashCode() {",
                  "return Objects.hash(value);",
                  "public String toString() {",
                  "return \"%s[%d]\".formatted(\"FooId\", value);");
        });
  }
}
