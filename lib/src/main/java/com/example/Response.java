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
package com.example;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Response<T> {

  public final int status;
  @Nullable public final T body;
  @NotNull public final Map<String, String> headers;

  private Response(int status, @Nullable T body) {
    this(status, body, Map.of());
  }

  private Response(int status, @Nullable T body, @NotNull Map<String, String> headers) {
    this.status = status;
    this.body = body;
    this.headers = headers;
  }

  @NotNull
  public static <T> Response<T> ok(@NotNull final T body) {
    return new Response<>(200, body);
  }

  @NotNull
  public static Response<Void> created(String location) {
    return new Response<>(201, null, Map.of("Location", location));
  }
}
