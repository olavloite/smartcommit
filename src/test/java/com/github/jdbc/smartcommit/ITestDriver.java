/*
 * Copyright 2020 Knut Olav Løite
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jdbc.smartcommit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public interface ITestDriver<T> extends Driver {

  default Connection doConnect(String url, Class<T> realConnectionClass)
      throws SQLException {
    if (acceptsURL(url)) {
      Connection connection = mock(Connection.class);
      when(connection.isWrapperFor(realConnectionClass)).thenReturn(true);
      return connection;
    }
    return null;
  }
}
