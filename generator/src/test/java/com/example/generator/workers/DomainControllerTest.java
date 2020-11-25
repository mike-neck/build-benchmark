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
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import java.util.List;
import java.util.Set;
import javax.lang.model.element.Modifier;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(Provider.class)
@TestFor(DomainController.class)
class DomainControllerTest {

  @Test
  @WithName("fooBar")
  void controller(DomainController controller) {
    assertThat(controller).isNotNull();
  }

  @Test
  @WithName("fooNar")
  void javaNameFooNar(DomainController controller) {
    assertThat(controller.javaName()).isEqualTo("FooNarController");
  }

  @Test
  @WithName("fooBarBaz")
  void javaNameFooBarBaz(DomainController controller) {
    assertThat(controller.javaName()).isEqualTo("FooBarBazController");
  }

  @Test
  @WithName("fooBar")
  void fieldNameFooBar(DomainController controller) {
    assertThat(controller.fieldName()).isEqualTo("fooBarController");
  }

  @Test
  @WithName("fooBarBaz")
  void fieldNameFooBarBaz(DomainController controller) {
    assertThat(controller.fieldName()).isEqualTo("fooBarBazController");
  }

  @TestFactory
  @WithName("nyaCat")
  Iterable<DynamicTest> serviceField(DomainController controller) {
    FieldSpec service = controller.serviceField();
    return List.of(
        dynamicTest("service is not null", () -> assertThat(service).isNotNull()),
        dynamicTest(
            "service's name is service", () -> assertThat(service.name).isEqualTo("nyaCatService")),
        dynamicTest(
            "service's type is NyaCatService",
            () ->
                assertThat(service.type)
                    .isEqualTo(ClassName.get(JavaDefinition.packageName, "NyaCatService"))),
        dynamicTest(
            "service's modifiers are private final",
            () -> assertThat(service.modifiers).contains(Modifier.PRIVATE, Modifier.FINAL)),
        dynamicTest(
            "service's initializer does not exist.",
            () -> assertThat(service.initializer.isEmpty()).isTrue()));
  }

  @TestFactory
  @WithName("fooBar")
  Iterable<DynamicTest> serviceFieldName(DomainController controller) {
    FieldSpec service = controller.serviceField();
    return List.of(
        dynamicTest(
            "service field name is fooBarService",
            () -> assertThat(service.name).isEqualTo("fooBarService")));
  }

  @TestFactory
  @WithName("nyaCat")
  Iterable<DynamicTest> constructor(DomainController controller) {
    MethodSpec constructor = controller.constructor();
    return List.of(
        dynamicTest("constructor is not null", () -> assertThat(constructor).isNotNull()),
        dynamicTest(
            "constructor is constructor", () -> assertThat(constructor.isConstructor()).isTrue()),
        dynamicTest(
            "constructor's modifiers are public only",
            () -> assertThat(constructor.modifiers).isEqualTo(Set.of(Modifier.PUBLIC))),
        dynamicTest(
            "constructor has a service parameter only",
            () ->
                assertThat(constructor.parameters)
                    .hasSize(1)
                    .satisfies(
                        parameterSpec ->
                            assertThat(parameterSpec.type)
                                .describedAs("parameter's type is NyaCatService")
                                .isEqualTo(
                                    ClassName.get(JavaDefinition.packageName, "NyaCatService")),
                        Index.atIndex(0))
                    .satisfies(
                        parameterSpec ->
                            assertThat(parameterSpec.name)
                                .describedAs("parameter's name is nyaCatService")
                                .isEqualTo("nyaCatService"),
                        Index.atIndex(0))),
        dynamicTest(
            "constructor's body is not empty",
            () -> assertThat(constructor.code.isEmpty()).isFalse()),
        dynamicTest(
            "in constructor nyaService is assigned",
            () ->
                assertThat(constructor.code.toString())
                    .contains("this.nyaCatService = nyaCatService")));
  }

  @TestFactory
  @WithName("fooBarBaz")
  Iterable<DynamicTest> constructorBodies(DomainController controller) {
    MethodSpec constructor = controller.constructor();
    return List.of(
        dynamicTest(
            "constructor body has assignment",
            () ->
                assertThat(constructor.code.toString())
                    .contains("this.fooBarBazService = fooBarBazService")));
  }

  @TestFactory
  @WithName("nyaCat")
  Iterable<DynamicTest> getMethod(DomainController controller) {
    MethodSpec getMethod = controller.getMethod();
    return List.of(
        dynamicTest("getMethod is not null", () -> assertThat(getMethod).isNotNull()),
        dynamicTest(
            "getMethod is not constructor", () -> assertThat(getMethod.isConstructor()).isFalse()),
        dynamicTest("getMethod is get", () -> assertThat(getMethod.name).isEqualTo("get")),
        dynamicTest(
            "getMethod's modifiers are public",
            () -> assertThat(getMethod.modifiers).isEqualTo(Set.of(Modifier.PUBLIC))),
        dynamicTest(
            "getMethod has a long parameter",
            () ->
                assertThat(getMethod.parameters)
                    .hasSize(1)
                    .singleElement()
                    .isEqualTo(ParameterSpec.builder(ClassName.LONG, "nyaCatId").build())),
        dynamicTest(
            "getMethod returns Response<NyaCat>",
            () ->
                assertThat(getMethod.returnType)
                    .isEqualTo(
                        ParameterizedTypeName.get(
                            ClassName.get("com.example", "Response"),
                            ClassName.get(JavaDefinition.packageName, "NyaCat")))),
        dynamicTest(
            "getMethod's body is not empty", () -> assertThat(getMethod.code.isEmpty()).isFalse()),
        dynamicTest(
            "NyaCatId instance is created in getMethod's body",
            () ->
                assertThat(getMethod.code.toString())
                    .contains("NyaCatId id = new ", "NyaCatId(nyaCatId)")),
        dynamicTest(
            "Domain instance is got from service.findById(id)",
            () ->
                assertThat(getMethod.code.toString())
                    .contains("NyaCat nyaCat = nyaCatService.findById(id);")),
        dynamicTest(
            "Response.ok will be called in getMethod's body",
            () ->
                assertThat(getMethod.code.toString()).contains("return ", "Response.ok(nyaCat);")));
  }

  @TestFactory
  @WithName("nyaCat")
  Iterable<DynamicTest> createMethod(DomainController controller) {
    return List.of(dynamicTest("createMethod", () -> assumeThat(1).isEqualTo(2)));
  }

  @TestFactory
  @WithName("nyaCat")
  Iterable<DynamicTest> create(DomainController controller) {
    return List.of(dynamicTest("create", () -> assumeThat(1).isEqualTo(2)));
  }
}
