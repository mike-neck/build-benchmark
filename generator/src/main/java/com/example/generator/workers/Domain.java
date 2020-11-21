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
import com.squareup.javapoet.TypeSpec;
import javax.lang.model.element.Modifier;
import org.jetbrains.annotations.NotNull;

public final class Domain implements JavaDefinition {

  private final Name name;
  final DomainId domainId;

  public Domain(Name name) {
    this.name = name;
    this.domainId = new DomainId(name);
  }

  @Override
  public @NotNull String javaName() {
    return name.typeName();
  }

  @Override
  public @NotNull String fieldName() {
    return name.fieldName();
  }

  @Override
  public @NotNull JavaFile create() {
    ClassName className = type();
    TypeSpec typeSpec =
        TypeSpec.classBuilder(className)
            .addModifiers(Modifier.FINAL, Modifier.PUBLIC)
            .addField(field())
            .addMethod(constructor())
            .build();
    return JavaFile.builder(packageName, typeSpec).build();
  }

  @NotNull
  MethodSpec constructor() {
    return MethodSpec.constructorBuilder()
        .addParameter(domainId.type(), domainId.fieldName())
        .addStatement("this.%s = %s".formatted(domainId.fieldName(), domainId.fieldName()))
        .build();
  }

  @NotNull
  FieldSpec field() {
    return FieldSpec.builder(domainId.type(), domainId.fieldName(), Modifier.PUBLIC, Modifier.FINAL)
        .build();
  }
}
