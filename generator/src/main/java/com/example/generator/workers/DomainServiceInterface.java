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
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import javax.lang.model.element.Modifier;
import org.jetbrains.annotations.NotNull;

public class DomainServiceInterface implements DomainService {

  private final Name name;

  public DomainServiceInterface(Name name) {
    this.name = name;
  }

  @Override
  public @NotNull String javaName() {
    return "%sService".formatted(name.typeName());
  }

  @Override
  public @NotNull String fieldName() {
    return "%sService".formatted(name.fieldName());
  }

  @Override
  public @NotNull JavaFile create() {
    return JavaFile.builder(packageName, typeDefinition()).build();
  }

  @Override
  public @NotNull MethodSpec findById() {
    return MethodSpec.methodBuilder("findById")
        .returns(ClassName.get(packageName, name.typeName()))
        .addParameter(
            ClassName.get(packageName, "%sId".formatted(name.typeName())),
            "%sId".formatted(name.fieldName()))
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
        .build();
  }

  @Override
  public @NotNull MethodSpec createNew() {
    return MethodSpec.methodBuilder("createNew")
        .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
        .returns(ClassName.get(packageName, "%sId".formatted(name.typeName())))
        .build();
  }

  @Override
  public @NotNull TypeSpec typeDefinition() {
    return TypeSpec.interfaceBuilder(type())
        .addModifiers(Modifier.PUBLIC)
        .addMethods(List.of(findById(), createNew()))
        .build();
  }
}
