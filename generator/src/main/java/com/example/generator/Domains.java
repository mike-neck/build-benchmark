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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Domains {

  @NotNull
  static Domains load() {
    return load("domains.yml");
  }

  @NotNull
  static Domains load(@NotNull String resourceName) {
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    try (@Nullable InputStream stream = classLoader.getResourceAsStream(resourceName)) {
      Objects.requireNonNull(stream, () -> "resource %s is not existing".formatted(resourceName));
      @NotNull InputStreamReader reader = new InputStreamReader(stream);
      @NotNull ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
      return objectMapper.readValue(reader, Domains.class);
    } catch (IOException e) {
      throw new UncheckedIOException("error while reading %s".formatted(resourceName), e);
    }
  }

  @NotNull private List<String> domains;

  public Domains() {
    this(new ArrayList<>());
  }

  public Domains(@NotNull List<String> domains) {
    this.domains = domains;
  }

  @NotNull
  public List<String> getDomains() {
    return domains;
  }

  public void setDomains(@NotNull List<String> domains) {
    this.domains = domains;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Domains domains1)) return false;
    return domains.equals(domains1.domains);
  }

  @Override
  public int hashCode() {
    return Objects.hash(domains);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Domains.class.getSimpleName() + "[", "]")
        .add("domains=" + domains)
        .toString();
  }
}
