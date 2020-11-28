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

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class Generator {

  public static void main(String[] args) {
    System.out.println(
        Arrays.stream(args)
            .map(LoggingApp::fromStringOptionally)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(log -> log.loggingCode("main").toString())
            .collect(Collectors.joining(",")));
    Domains domains = Domains.load();
    System.out.println(domains);
  }
}
