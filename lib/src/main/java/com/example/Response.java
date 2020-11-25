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

import org.jetbrains.annotations.NotNull;

public class Response<T> {

  public final int status;
  @NotNull public final T body;

  public Response(int status, @NotNull T body) {
    this.status = status;
    this.body = body;
  }

  @NotNull
  public static <T> Response<T> ok(@NotNull final T body) {
    return new Response<>(200, body);
  }
}
