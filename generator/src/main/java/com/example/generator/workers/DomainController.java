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
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import javax.lang.model.element.Modifier;
import org.jetbrains.annotations.NotNull;

public class DomainController implements JavaDefinition {

  private final Name name;
  private final DomainService domainService;

  public DomainController(Name name) {
    this.name = name;
    this.domainService = new DomainService(name);
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
    return null;
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
}
