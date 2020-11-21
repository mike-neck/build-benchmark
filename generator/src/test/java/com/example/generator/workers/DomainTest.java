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

import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import javax.lang.model.element.Modifier;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(Provider.class)
@TestFor(Domain.class)
class DomainTest {

  @WithName("fooBarBaz")
  @Test
  void javaName(Domain domain) {
    assertThat(domain.javaName()).isEqualTo("FooBarBaz");
  }

  @WithName("fooBarBaz")
  @Test
  void fieldName(Domain domain) {
    assertThat(domain.fieldName()).isEqualTo("fooBarBaz");
  }

  @WithName("fooBarBaz")
  @Test
  void fields(Domain domain) {
    FieldSpec fieldSpec = domain.field();
    assertAll(
        () -> assertThat(fieldSpec.modifiers).contains(Modifier.FINAL, Modifier.PUBLIC),
        () -> assertThat(fieldSpec.type).isEqualTo(domain.domainId.type()),
        () -> assertThat(fieldSpec.name).isEqualTo("fooBarBazId"));
  }

  @Test
  @WithName("fooBarBaz")
  void constructor(Domain domain) {
    MethodSpec constructor = domain.constructor();
    assertAll(
        () -> assertThat(constructor.isConstructor()).isTrue(),
        () -> assertThat(constructor.parameters).hasSize(1),
        () ->
            assertThat(constructor.parameters)
                .satisfies(
                    parameterSpec -> assertThat(parameterSpec.name).isEqualTo("fooBarBazId"),
                    Index.atIndex(0))
                .satisfies(
                    parameterSpec ->
                        assertThat(parameterSpec.type).isEqualTo(domain.domainId.type()),
                    Index.atIndex(0)),
        () -> assertThat(constructor.code.toString()).contains("this.fooBarBazId = fooBarBazId;"));
  }

  @WithName("fooBarBaz")
  @Test
  void create(Domain domain) {
    JavaFile javaFile = domain.create();
    assertAll(
        () -> assertThat(javaFile.typeSpec.name).isEqualTo(domain.javaName()),
        () -> assertThat(javaFile.typeSpec.modifiers).contains(Modifier.FINAL, Modifier.PUBLIC),
        () -> assertThat(javaFile.typeSpec.fieldSpecs).hasSize(1),
        () -> assertThat(javaFile.packageName).isEqualTo(JavaDefinition.packageName),
        () ->
            assertThat(javaFile.typeSpec.methodSpecs)
                .hasSize(1)
                .allSatisfy(methodSpec -> assertThat(methodSpec.isConstructor()).isTrue())
                .allSatisfy(methodSpec -> assertThat(methodSpec.parameters).hasSize(1)),
        () -> {
          StringBuilder builder = new StringBuilder();
          javaFile.writeTo(builder);
          assertThat(builder.toString())
              .contains(
                  "public final class FooBarBaz",
                  "public final FooBarBazId fooBarBazId;",
                  "FooBarBaz(FooBarBazId fooBarBazId) {");
        });
  }
}
