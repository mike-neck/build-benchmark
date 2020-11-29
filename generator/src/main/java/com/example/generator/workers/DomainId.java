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

import com.example.generator.JavaDefinition;
import com.example.generator.Name;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import java.util.List;
import java.util.Objects;
import javax.lang.model.element.Modifier;
import org.jetbrains.annotations.NotNull;

public final class DomainId implements JavaDefinition {

  private final Name name;

  public DomainId(Name name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return javaName();
  }

  @NotNull
  @Override
  public String javaName() {
    return "%sId".formatted(name.typeName());
  }

  @Override
  public @NotNull String fieldName() {
    return "%sId".formatted(name.fieldName());
  }

  @NotNull
  @Override
  public JavaFile create() {
    ClassName selfType = type();
    FieldSpec valueField =
        FieldSpec.builder(TypeName.LONG, "value", Modifier.FINAL, Modifier.PUBLIC).build();
    MethodSpec constructorBody =
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PUBLIC)
            .addParameter(TypeName.LONG, "value")
            .addStatement("this.value = value")
            .build();
    MethodSpec equalsBody =
        MethodSpec.methodBuilder("equals")
            .addAnnotation(Override.class)
            .addParameter(Object.class, "other")
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.BOOLEAN)
            // language=groovy
            .addStatement("if (this == other) return true")
            // language=groovy
            .addStatement("if (!(other instanceof $T)) return false", selfType)
            // language=groovy
            .addStatement("$T id = ($T) other", selfType, selfType)
            // language=groovy
            .addStatement("return value == id.value")
            .build();
    MethodSpec hashCodeBody =
        MethodSpec.methodBuilder("hashCode")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.INT)
            // language=groovy
            .addStatement("return $T.hash(value)", Objects.class)
            .build();
    MethodSpec toStringBody =
        MethodSpec.methodBuilder("toString")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class)
            .addStatement("return $S.formatted($S, value)", "%s[%d]", javaName())
            .build();
    TypeSpec typeSpec =
        TypeSpec.classBuilder(selfType)
            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
            .addField(valueField)
            .addMethods(List.of(constructorBody, equalsBody, hashCodeBody, toStringBody))
            .build();
    return JavaFile.builder(packageName, typeSpec).build();
  }
}
