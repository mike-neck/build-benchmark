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
    JavaDefinitionInitializer initializer =
        loggingParam
            .map(param -> loggingTypeInitializer(withName, param))
            .orElseGet(() -> new NormalType(withName));
    return initializer.initializeInstanceOf(testFor);
  }

  interface JavaDefinitionInitializer {
    JavaDefinition initializeInstanceOf(TestFor testFor);
  }

  static JavaDefinitionInitializer loggingTypeInitializer(
      WithName withName, LoggingParam loggingParam) {
    return new LoggingType(withName, loggingParam);
  }

  static class NormalType implements JavaDefinitionInitializer {

    final WithName withName;

    NormalType(WithName withName) {
      this.withName = withName;
    }

    @Override
    public JavaDefinition initializeInstanceOf(TestFor testFor) {
      Class<? extends JavaDefinition> klass = testFor.value();
      try {
        Constructor<? extends JavaDefinition> constructor = klass.getConstructor(Name.class);
        Name name = new Name(withName.value());
        constructor.setAccessible(true);
        return constructor.newInstance(name);
      } catch (NoSuchMethodException
          | IllegalAccessException
          | InstantiationException
          | InvocationTargetException e) {
        throw new IllegalStateException(
            "failed to initialize target class[%s]".formatted(klass.getSimpleName()), e);
      }
    }
  }

  static class LoggingType implements JavaDefinitionInitializer {

    final WithName withName;
    final LoggingParam loggingParam;

    LoggingType(WithName withName, LoggingParam loggingParam) {
      this.withName = withName;
      this.loggingParam = loggingParam;
    }

    @Override
    public JavaDefinition initializeInstanceOf(TestFor testFor) {
      Class<? extends JavaDefinition> klass = testFor.value();
      try {
        Constructor<? extends JavaDefinition> constructor =
            klass.getConstructor(Name.class, LoggingApp.class);
        Name name = new Name(withName.value());
        constructor.setAccessible(true);
        return constructor.newInstance(name, loggingParam.value());
      } catch (NoSuchMethodException
          | IllegalAccessException
          | InstantiationException
          | InvocationTargetException e) {
        throw new IllegalStateException(
            "failed to initialize target class[%s]".formatted(klass.getSimpleName()), e);
      }
    }
  }
}
