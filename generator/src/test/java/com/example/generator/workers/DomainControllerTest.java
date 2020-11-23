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
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import com.squareup.javapoet.FieldSpec;
import java.util.List;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(Provider.class)
@TestFor(DomainController.class)
class DomainControllerTest {

  @Test
  @WithName("fooBar")
  void controller(DomainController controller) {
    assertThat(controller).isNotNull();
  }

  @Test
  @WithName("fooNar")
  void javaNameFooNar(DomainController controller) {
    assertThat(controller.javaName()).isEqualTo("FooNarController");
  }

  @Test
  @WithName("fooBarBaz")
  void javaNameFooBarBaz(DomainController controller) {
    assertThat(controller.javaName()).isEqualTo("FooBarBazController");
  }

  @Test
  @WithName("fooBar")
  void fieldNameFooBar(DomainController controller) {
    assertThat(controller.fieldName()).isEqualTo("fooBarController");
  }

  @Test
  @WithName("fooBarBaz")
  void fieldNameFooBarBaz(DomainController controller) {
    assertThat(controller.fieldName()).isEqualTo("fooBarBazController");
  }

  @TestFactory
  @WithName("nyaCat")
  Iterable<DynamicTest> serviceField(DomainController controller) {
    FieldSpec service = controller.serviceField();
    return List.of(dynamicTest("service is not null", () -> assertThat(service).isNotNull()));
  }
}
