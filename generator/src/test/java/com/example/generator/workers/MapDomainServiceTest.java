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
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.example.generator.LoggingApp;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import javax.lang.model.element.Modifier;
import org.assertj.core.api.Condition;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;

@ExtendWith(Provider.class)
@TestFor(MapDomainService.class)
class MapDomainServiceTest {

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.NO_LOGGING)
  void initialize(MapDomainService service) {
    assertThat(service).isNotNull();
  }

  @Test
  @WithName("foo")
  @LoggingParam(LoggingApp.LOGGING)
  void initializeWithLogging(MapDomainService service) {
    assertThat(service).isNotNull();
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.NO_LOGGING)
  void javaName(MapDomainService service) {
    assertThat(service.javaName()).isEqualTo("MapFooBarService");
  }

  @Test
  @WithName("foo")
  @LoggingParam(LoggingApp.LOGGING)
  void javaNameWithLogging(MapDomainService service) {
    assertThat(service.javaName()).isEqualTo("MapFooService");
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.NO_LOGGING)
  void fieldName(MapDomainService service) {
    assertThat(service.fieldName()).isEqualTo("mapFooBarService");
  }

  @Test
  @WithName("foo")
  @LoggingParam(LoggingApp.LOGGING)
  void fieldNameWithLogging(MapDomainService service) {
    assertThat(service.fieldName()).isEqualTo("mapFooService");
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.NO_LOGGING)
  void staticField(MapDomainService service) {
    FieldSpec logger = service.loggerField();
    assertAll(
        () -> assertThat(logger).isNotNull(),
        () -> assertThat(logger.name).isEqualTo("logger"),
        () -> assertThat(logger.type).isEqualTo(TypeName.get(Logger.class)),
        () ->
            assertThat(logger.modifiers)
                .contains(Modifier.PRIVATE, Modifier.FINAL, Modifier.STATIC),
        () ->
            assertThat(logger.initializer)
                .satisfies(codeBlock -> assertThat(codeBlock.isEmpty()).isFalse())
                .satisfies(
                    codeBlock ->
                        assertThat(codeBlock.toString())
                            .contains("LoggerFactory.getLogger(", "MapFooBarService.class")));
  }

  @Test
  @WithName("fooBarBaz")
  @LoggingParam(LoggingApp.LOGGING)
  void staticFieldWithLogging(MapDomainService service) {
    FieldSpec logger = service.loggerField();
    assertAll(
        () -> assertThat(logger).isNotNull(),
        () -> assertThat(logger.name).isEqualTo("logger"),
        () -> assertThat(logger.type).isEqualTo(TypeName.get(Logger.class)));
  }

  @TestFactory
  @WithName("fooBar")
  @LoggingParam(LoggingApp.NO_LOGGING)
  Iterable<DynamicTest> mapField(MapDomainService service) {
    FieldSpec map = service.mapField();
    return List.of(
        dynamicTest("field map exists", () -> assertThat(map).isNotNull()),
        dynamicTest(
            "map is final private",
            () -> assertThat(map.modifiers).containsAll(List.of(Modifier.PRIVATE, Modifier.FINAL))),
        dynamicTest(
            "map's type is ConcurrentMap<FooBarId, FooBar>",
            () ->
                assertThat(map.type)
                    .isEqualTo(
                        ParameterizedTypeName.get(
                            ClassName.get(ConcurrentMap.class),
                            ClassName.get(JavaDefinition.packageName, "FooBarId"),
                            ClassName.get(JavaDefinition.packageName, "FooBar")))),
        dynamicTest(
            "map initializer is in constructor",
            () -> assertThat(map.initializer.isEmpty()).isTrue()),
        dynamicTest("map's field name is map", () -> assertThat(map.name).isEqualTo("map")));
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.NO_LOGGING)
  void findByIdWithoutLogging(MapDomainService service) {
    MethodSpec findById = service.findById();
    assertAll(
        () -> assertThat(findById).isNotNull(),
        () -> assertThat(findById.name).isEqualTo("findById"),
        () -> assertThat(findById.modifiers).contains(Modifier.PUBLIC),
        () ->
            assertThat(findById.annotations)
                .contains(AnnotationSpec.builder(Override.class).build()),
        () ->
            assertThat(findById.returnType)
                .isNotNull()
                .isNotEqualTo(TypeName.VOID)
                .isEqualTo(ClassName.get(JavaDefinition.packageName, "FooBar")),
        () ->
            assertThat(findById.parameters)
                .hasSize(1)
                .has(new Condition<>(Objects::nonNull, "not null"), Index.atIndex(0))
                .has(
                    new Condition<>(
                        parameterSpec -> parameterSpec.name.equals("fooBarId"),
                        "parameter name(fooBarBazId)"),
                    Index.atIndex(0))
                .has(
                    new Condition<>(
                        parameterSpec ->
                            parameterSpec.type.equals(
                                ClassName.get(JavaDefinition.packageName, "FooBarId")),
                        "parameter type[FooBarId]"),
                    Index.atIndex(0)),
        () ->
            assertThat(findById.code)
                .satisfies(
                    codeBlock ->
                        assertThat(codeBlock.isEmpty())
                            .describedAs("codeBlock is not empty")
                            .isFalse())
                .satisfies(
                    codeBlock ->
                        assertThat(codeBlock.toString())
                            .contains("FooBar::new", "computeIfAbsent(fooBarId"))
                .satisfies(
                    codeBlock -> assertThat(codeBlock.toString()).doesNotContain("logger.info")));
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.LOGGING)
  void findById(MapDomainService service) {
    MethodSpec findById = service.findById();
    assertAll(
        () -> assertThat(findById).isNotNull(),
        () -> assertThat(findById.name).isEqualTo("findById"),
        () -> assertThat(findById.modifiers).contains(Modifier.PUBLIC),
        () ->
            assertThat(findById.returnType)
                .isNotNull()
                .isNotEqualTo(TypeName.VOID)
                .isEqualTo(ClassName.get(JavaDefinition.packageName, "FooBar")),
        () -> assertThat(findById.parameters).hasSize(1),
        () ->
            assertThat(findById.code)
                .satisfies(
                    codeBlock ->
                        assertThat(codeBlock.isEmpty())
                            .describedAs("codeBlock is not empty")
                            .isFalse())
                .satisfies(
                    codeBlock ->
                        assertThat(codeBlock.toString())
                            .contains("FooBar::new", "computeIfAbsent(fooBarId", "logger.info")));
  }

  @TestFactory
  @WithName("fooBar")
  @LoggingParam(LoggingApp.NO_LOGGING)
  Iterable<DynamicTest> createNewWithoutLogging(MapDomainService service) {
    MethodSpec createNew = service.createNew();
    return List.of(
        dynamicTest("createNew is not null", () -> assertThat(createNew).isNotNull()),
        dynamicTest(
            "createNew's parameter is void", () -> assertThat(createNew.parameters).isEmpty()),
        dynamicTest(
            "createNew is public",
            () -> assertThat(createNew.modifiers).isEqualTo(Set.of(Modifier.PUBLIC))),
        dynamicTest(
            "createNew has Override annotation",
            () ->
                assertThat(createNew.annotations)
                    .hasSize(1)
                    .satisfies(
                        annotationSpec ->
                            assertThat(annotationSpec.type)
                                .isEqualTo(ClassName.get(Override.class)),
                        Index.atIndex(0))),
        dynamicTest(
            "createNew's return type is FooBarId",
            () ->
                assertThat(createNew.returnType)
                    .isNotEqualTo(ClassName.VOID)
                    .isEqualTo(ClassName.get(JavaDefinition.packageName, "FooBarId"))),
        dynamicTest(
            "createNew's code block is not empty",
            () -> assertThat(createNew.code.isEmpty()).isFalse()),
        dynamicTest(
            "createNew has return statement",
            () -> assertThat(createNew.code.toString()).contains("return fooBar.fooBarId")),
        dynamicTest(
            "createNew's body has map access and domain assignment",
            () ->
                assertThat(createNew.code.toString())
                    .contains("FooBar fooBar = map.computeIfAbsent(fooBarId", "FooBar::new")),
        dynamicTest(
            "createNew's body has an initialization of domainId",
            () ->
                assertThat(createNew.code.toString())
                    .contains("FooBarId fooBarId = new ", "FooBarId(id)")),
        dynamicTest(
            "createNew's body has no logger access",
            () -> assertThat(createNew.code.toString()).doesNotContain("logger")),
        dynamicTest(
            "createNew's body has generation of id",
            () ->
                assertThat(createNew.code.toString()).contains("long id = idGenerator.getLong()")));
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.LOGGING)
  void createNew(MapDomainService service) {
    MethodSpec createNew = service.createNew();
    assertThat(createNew.code.toString()).contains("logger");
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.LOGGING)
  void idGenerator(MapDomainService service) {
    FieldSpec idGenerator = service.idGeneratorField();
    assertAll(
        () -> assertThat(idGenerator).isNotNull(),
        () -> assertThat(idGenerator.name).isEqualTo("idGenerator"),
        () -> assertThat(idGenerator.type).isEqualTo(ClassName.get("com.example", "IdGenerator")),
        () -> assertThat(idGenerator.modifiers).contains(Modifier.FINAL, Modifier.PRIVATE));
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.LOGGING)
  void constructor(MapDomainService service) {
    MethodSpec constructor = service.constructor();
    assertAll(
        () -> assertThat(constructor).isNotNull(),
        () -> assertThat(constructor.isConstructor()).isTrue(),
        () -> assertThat(constructor.modifiers).contains(Modifier.PUBLIC),
        () ->
            assertThat(constructor.parameters)
                .hasSize(1)
                .contains(
                    ParameterSpec.builder(
                            ClassName.get("com.example", "IdGenerator"), "idGenerator")
                        .build(),
                    Index.atIndex(0)),
        () -> assertThat(constructor.code.isEmpty()).isFalse(),
        () ->
            assertThat(constructor.code.toString())
                .contains("ConcurrentHashMap<>", "this.map = new "),
        () -> assertThat(constructor.code.toString()).contains("this.idGenerator = idGenerator"));
  }

  @Test
  @WithName("fooBar")
  @LoggingParam(LoggingApp.LOGGING)
  void create(MapDomainService service) {
    JavaFile javaFile = service.create();
    TypeSpec typeSpec = javaFile.typeSpec;
    assertAll(
        () -> assertThat(javaFile).isNotNull(),
        () -> assertThat(typeSpec.name).isEqualTo("MapFooBarService"),
        () ->
            assertThat(typeSpec.superclass)
                .isEqualTo(ClassName.get(JavaDefinition.packageName, "FooBarService")),
        () -> assertThat(javaFile.packageName).isEqualTo(JavaDefinition.packageName),
        () -> assertThat(typeSpec.modifiers).contains(Modifier.PUBLIC),
        () -> assertThat(typeSpec.kind).isEqualTo(TypeSpec.Kind.CLASS),
        () ->
            assertThat(typeSpec.methodSpecs)
                .hasSize(2)
                .extracting(methodSpec -> methodSpec.name)
                .contains("createNew", "findById"),
        () ->
            assertThat(typeSpec.fieldSpecs)
                .hasSize(3)
                .extracting(fieldSpec -> fieldSpec.name)
                .contains("map", "logger", "idGenerator"),
        () -> {
          StringBuilder builder = new StringBuilder();
          javaFile.writeTo(builder);
          assertThat(builder.toString()).isNotEmpty().isNotBlank();
        });
  }
}
