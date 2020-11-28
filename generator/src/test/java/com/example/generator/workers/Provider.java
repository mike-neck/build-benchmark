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

import com.example.generator.Interface;
import com.example.generator.JavaDefinition;
import com.example.generator.LoggingApp;
import com.example.generator.Name;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class Provider implements ParameterResolver {

  @Override
  public boolean supportsParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Optional<TestFor> opTestFor =
        extensionContext
            .getTestClass()
            .flatMap(klass -> Optional.ofNullable(klass.getAnnotation(TestFor.class)));
    if (opTestFor.isEmpty()) {
      return false;
    }
    TestFor testFor = opTestFor.get();
    Optional<WithName> withName =
        extensionContext
            .getTestMethod()
            .flatMap(method -> Optional.ofNullable(method.getAnnotation(WithName.class)));
    if (withName.isEmpty()) {
      return false;
    }
    return parameterContext.getParameter().getType().equals(testFor.value());
  }

  @Override
  public Object resolveParameter(
      ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    TestFor testFor =
        extensionContext
            .getTestClass()
            .flatMap(klass -> Optional.ofNullable(klass.getAnnotation(TestFor.class)))
            .orElseThrow(() -> new IllegalStateException("TestFor is not annotated to class"));
    Optional<Method> testMethod = extensionContext.getTestMethod();
    WithName withName =
        testMethod
            .flatMap(method -> Optional.ofNullable(method.getAnnotation(WithName.class)))
            .orElseThrow(() -> new IllegalStateException("WithName is not annotated to method"));
    Optional<LoggingParam> loggingParam =
        testMethod.flatMap(method -> Optional.ofNullable(method.getAnnotation(LoggingParam.class)));
    Optional<WithInterface> withInterface =
        testMethod.flatMap(
            method -> Optional.ofNullable(method.getAnnotation(WithInterface.class)));
    JavaDefinitionInitializer initializer =
        loggingParam
            .map(param -> loggingTypeInitializer(withName, param))
            .or(() -> withInterface.map(wi -> withInterfaceInitializer(withName, wi)))
            .orElseGet(() -> normalInitializer(withName));
    return initializer.initializeInstanceOf(testFor);
  }

  interface JavaDefinitionInitializer {
    JavaDefinition initializeInstanceOf(TestFor testFor);
  }

  interface JavaDefinitionConstructor {
    Constructor<? extends JavaDefinition> getConstructor(Class<? extends JavaDefinition> klass)
        throws NoSuchMethodException, SecurityException;
  }

  interface NewInstance {
    JavaDefinition createNewInstance(Constructor<? extends JavaDefinition> constructor)
        throws InstantiationException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException;
  }

  static class Init implements JavaDefinitionInitializer {

    private final JavaDefinitionConstructor javaDefinitionConstructor;
    private final NewInstance newInstance;

    Init(JavaDefinitionConstructor javaDefinitionConstructor, NewInstance newInstance) {
      this.javaDefinitionConstructor = javaDefinitionConstructor;
      this.newInstance = newInstance;
    }

    @Override
    public JavaDefinition initializeInstanceOf(TestFor testFor) {
      Class<? extends JavaDefinition> klass = testFor.value();
      try {
        Constructor<? extends JavaDefinition> constructor =
            javaDefinitionConstructor.getConstructor(klass);
        constructor.setAccessible(true);
        return newInstance.createNewInstance(constructor);
      } catch (NoSuchMethodException
          | InvocationTargetException
          | InstantiationException
          | IllegalAccessException e) {
        throw new IllegalStateException(
            "failed to initialize target class[%s]".formatted(klass.getSimpleName()), e);
      }
    }
  }

  static JavaDefinitionInitializer loggingTypeInitializer(
      WithName withName, LoggingParam loggingParam) {
    return new Init(
        klass -> klass.getConstructor(Name.class, LoggingApp.class),
        constructor -> constructor.newInstance(new Name(withName.value()), loggingParam.value()));
  }

  static JavaDefinitionInitializer withInterfaceInitializer(
      WithName withName, WithInterface withInterface) {
    return new Init(
        klass -> klass.getConstructor(Name.class, Interface.class),
        constructor -> constructor.newInstance(new Name(withName.value()), withInterface.value()));
  }

  static JavaDefinitionInitializer normalInitializer(WithName withName) {
    return new Init(
        klass -> klass.getConstructor(Name.class),
        constructor -> constructor.newInstance(new Name(withName.value())));
  }
}
