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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.Objects;
import javax.lang.model.element.Modifier;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@TestFor(DomainServiceInterface.class)
@ExtendWith(Provider.class)
class DomainServiceTest {

  @WithName("fooBar")
  @Test
  void javaName(DomainServiceInterface domainService) {
    assertThat(domainService.javaName()).isEqualTo("FooBarService");
  }

  @WithName("fooBarBaz")
  @Test
  void javaName2(DomainServiceInterface domainService) {
    assertThat(domainService.javaName()).isEqualTo("FooBarBazService");
  }

  @WithName("fooBar")
  @Test
  void fieldName(DomainServiceInterface domainService) {
    assertThat(domainService.fieldName()).isEqualTo("fooBarService");
  }

  @WithName("fooBarBaz")
  @Test
  void fieldName2(DomainServiceInterface domainService) {
    assertThat(domainService.fieldName()).isEqualTo("fooBarBazService");
  }

  @WithName("fooBarBaz")
  @Test
  void findByIdMethod(DomainServiceInterface domainService) {
    MethodSpec findById = domainService.findById();
    assertAll(
        () -> assertThat(findById).isNotNull(),
        () -> assertThat(findById.name).isEqualTo("findById"),
        () -> assertThat(findById.modifiers).contains(Modifier.ABSTRACT, Modifier.PUBLIC),
        () ->
            assertThat(findById.parameters)
                .hasSize(1)
                .has(new Condition<>(Objects::nonNull, "not null"), Index.atIndex(0))
                .has(
                    new Condition<>(
                        parameterSpec -> parameterSpec.name.equals("fooBarBazId"),
                        "parameter name(fooBarBazId)"),
                    Index.atIndex(0))
                .has(
                    new Condition<>(
                        parameterSpec ->
                            parameterSpec.type.equals(
                                ClassName.get(JavaDefinition.packageName, "FooBarBazId")),
                        "parameter type[FooBarBazId]"),
                    Index.atIndex(0)),
        () ->
            assertThat(findById.returnType)
                .isNotNull()
                .isNotEqualTo(TypeName.VOID)
                .isEqualTo(ClassName.get(JavaDefinition.packageName, "FooBarBaz")));
  }

  @WithName("fooBar")
  @Test
  void createNew(DomainServiceInterface domainService) {
    MethodSpec createNew = domainService.createNew();
    assertAll(
        () -> assertThat(createNew).isNotNull(),
        () -> assertThat(createNew.name).isEqualTo("createNew"),
        () -> assertThat(createNew.modifiers).contains(Modifier.PUBLIC, Modifier.ABSTRACT),
        () -> assertThat(createNew.parameters).hasSize(0),
        () ->
            assertThat(createNew.returnType)
                .isNotEqualTo(TypeName.VOID)
                .isEqualTo(ClassName.get(JavaDefinition.packageName, "FooBarId")));
  }

  @WithName("fooBar")
  @Test
  void typeDef(DomainServiceInterface domainService) {
    TypeSpec service = domainService.typeDefinition();
    assertAll(
        () -> assertThat(service).isNotNull(),
        () -> assertThat(service.kind).isEqualTo(TypeSpec.Kind.INTERFACE),
        () -> assertThat(service.name).isEqualTo("FooBarService"),
        () -> assertThat(service.modifiers).contains(Modifier.PUBLIC),
        () ->
            assertThat(service.methodSpecs)
                .hasSize(2)
                .extracting(methodSpec -> methodSpec.name)
                .contains("createNew", "findById"));
  }

  @WithName("barBaz")
  @Test
  void create(DomainServiceInterface domainService) {
    JavaFile javaFile = domainService.create();
    assertAll(
        () -> assertThat(javaFile).isNotNull(),
        () -> {
          StringBuilder builder = new StringBuilder();
          javaFile.writeTo(builder);
          assertThat(builder)
              .contains(
                  "interface BarBazService",
                  "BarBazId createNew();",
                  "BarBaz findById(BarBazId barBazId);");
        });
  }
}
