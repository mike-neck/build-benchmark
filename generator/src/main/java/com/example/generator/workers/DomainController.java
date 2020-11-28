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

import com.example.generator.Name;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.jetbrains.annotations.NotNull;

public class DomainController implements JavaDefinition {

  private final Name name;
  private final Domain domain;
  private final DomainId domainId;
  private final DomainService domainService;

  public DomainController(Name name) {
    this.name = name;
    this.domain = new Domain(name);
    this.domainId = new DomainId(name);
    this.domainService = new DomainServiceInterface(name);
  }

  @Override
  public @NotNull String javaName() {
    return "%sController".formatted(name.typeName());
  }

  @Override
  public @NotNull String fieldName() {
    return "%sController".formatted(name.fieldName());
  }

  @Override
  public @NotNull JavaFile create() {
    return JavaFile.builder(packageName, typeSpec()).build();
  }

  @NotNull
  TypeSpec typeSpec() {
    return TypeSpec.classBuilder(ClassName.get(packageName, javaName()))
        .addModifiers(Modifier.PUBLIC)
        .addField(serviceField())
        .addMethods(List.of(constructor(), getMethod(), createMethod()))
        .build();
  }

  public FieldSpec serviceField() {
    return FieldSpec.builder(domainService.type(), domainService.fieldName())
        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
        .build();
  }

  public MethodSpec constructor() {
    return MethodSpec.constructorBuilder()
        .addModifiers(Modifier.PUBLIC)
        .addParameter(domainService.type(), domainService.fieldName())
        .addStatement(
            "this.%s = %s".formatted(domainService.fieldName(), domainService.fieldName()))
        .build();
  }

  private static final ClassName responseType = ClassName.get("com.example", "Response");

  public MethodSpec getMethod() {
    return MethodSpec.methodBuilder("get")
        .addModifiers(Modifier.PUBLIC)
        .addParameter(long.class, domainId.fieldName())
        .returns(ParameterizedTypeName.get(responseType, domain.type()))
        .addStatement(
            "$T id = new $T(%s)".formatted(domainId.fieldName()), domainId.type(), domainId.type())
        .addStatement(
            "$T %s = %s.findById(id)".formatted(domain.fieldName(), domainService.fieldName()),
            domain.type())
        .addStatement("return $T.ok(%s)".formatted(domain.fieldName()), responseType)
        .build();
  }

  public MethodSpec createMethod() {
    return MethodSpec.methodBuilder("create")
        .returns(ParameterizedTypeName.get(responseType, ClassName.get(Void.class)))
        .addModifiers(Modifier.PUBLIC)
        .addStatement(
            "$T %s = %s.createNew()".formatted(domainId.fieldName(), domainService.fieldName()),
            domainId.type())
        .addStatement(
            "$T location = $S.formatted(%s.value)".formatted(domainId.fieldName()),
            String.class,
            "https://example.com/" + domain.fieldName() + "/%d")
        .addStatement("return $T.created(location)", responseType)
        .build();
  }
}
