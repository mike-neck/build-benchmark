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
package com.example.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.function.Supplier;
import javax.lang.model.element.Modifier;

public class Generator {

  private static Type type(Supplier<? extends String> typeName) {
    return new Type() {
      @Override
      public String toString() {
        return typeName.get();
      }
    };
  }

  public static void main(String[] args) throws IOException {
    Domains domains = Domains.load();
    System.out.println(domains);
    MethodSpec interfaceMethod =
        MethodSpec.methodBuilder("findById")
            .addModifiers(Modifier.ABSTRACT, Modifier.PUBLIC)
            .addParameter(ClassName.get("com.example", "UserId"), "userId", Modifier.FINAL)
            .returns(ClassName.get("com.example", "User"))
            .build();
    TypeSpec userService =
        TypeSpec.interfaceBuilder(ClassName.get("com.example", "UserService"))
            .addMethod(interfaceMethod)
            .addModifiers(Modifier.PUBLIC)
            .build();
    JavaFile userServiceJava = JavaFile.builder("com.example", userService).build();
    userServiceJava.writeTo(System.out);
  }
}
