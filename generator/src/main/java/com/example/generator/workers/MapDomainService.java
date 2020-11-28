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

import com.example.generator.LoggingApp;
import com.example.generator.Name;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.lang.model.element.Modifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapDomainService implements DomainService {

  private final Name name;
  private final Domain domain;
  private final DomainId domainId;
  private final DomainService domainService;
  private final LoggingApp loggingApp;

  public MapDomainService(Name name, LoggingApp loggingApp) {
    this.name = name;
    this.domain = new Domain(name);
    this.domainId = new DomainId(name);
    this.domainService = new DomainServiceInterface(name);
    this.loggingApp = loggingApp;
  }

  @Override
  public @NotNull String javaName() {
    return "Map%sService".formatted(name.typeName());
  }

  @Override
  public @NotNull String fieldName() {
    return "map%sService".formatted(name.typeName());
  }

  @Override
  public @NotNull JavaFile create() {
    TypeSpec typeSpec = typeDefinition();
    return JavaFile.builder(packageName, typeSpec).build();
  }

  @Override
  @NotNull
  public TypeSpec typeDefinition() {
    return TypeSpec.classBuilder(type())
        .superclass(domainService.type())
        .addModifiers(Modifier.PUBLIC)
        .addMethods(List.of(createNew(), findById()))
        .addFields(List.of(loggerField(), mapField(), idGeneratorField()))
        .build();
  }

  FieldSpec loggerField() {
    return FieldSpec.builder(
            Logger.class, "logger", Modifier.STATIC, Modifier.PRIVATE, Modifier.FINAL)
        .initializer(
            "$T.getLogger($T.class)", LoggerFactory.class, ClassName.get(packageName, javaName()))
        .build();
  }

  FieldSpec mapField() {
    ParameterizedTypeName concurrentMapType =
        ParameterizedTypeName.get(
            ClassName.get(ConcurrentMap.class), domainId.type(), domain.type());
    return FieldSpec.builder(concurrentMapType, "map")
        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .build();
  }

  FieldSpec idGeneratorField() {
    return FieldSpec.builder(ClassName.get("com.example", "IdGenerator"), "idGenerator")
        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .build();
  }

  @Override
  @NotNull
  public MethodSpec findById() {
    String methodName = "findById";
    return MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(ClassName.get(packageName, name.typeName()))
        .addParameter(domainId.type(), domainId.fieldName())
        .addStatement(loggingApp.loggingCode(methodName))
        .addStatement(
            "return map.computeIfAbsent(%s, $T::new)".formatted(domainId.fieldName()),
            ClassName.get(packageName, domain.javaName()))
        .build();
  }

  @Override
  @NotNull
  public MethodSpec createNew() {
    String methodName = "createNew";
    return MethodSpec.methodBuilder(methodName)
        .addModifiers(Modifier.PUBLIC)
        .addAnnotation(Override.class)
        .returns(domainId.type())
        .addStatement(loggingApp.loggingCode(methodName))
        .addStatement("$T id = idGenerator.getLong()", ClassName.LONG)
        .addStatement(
            "$T %s = new $T(id)".formatted(domainId.fieldName()), domainId.type(), domainId.type())
        .addStatement(
            "$T %s = map.computeIfAbsent(%s, $T::new)"
                .formatted(domain.fieldName(), domainId.fieldName()),
            domain.type(),
            domain.type())
        .addStatement("return %s.%s".formatted(domain.fieldName(), domainId.fieldName()))
        .build();
  }

  MethodSpec constructor() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(ClassName.get("com.example", "IdGenerator"), "idGenerator")
        .addStatement("this.map = new $T<>()", ConcurrentHashMap.class)
        .addStatement("this.idGenerator = idGenerator")
        .build();
  }
}
