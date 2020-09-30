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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import oracle.jdbc.OracleConnection;

/** Mock class for the Oracle driver. */
public class OracleTestDriver extends oracle.jdbc.OracleDriver
    implements ITestDriver<OracleConnection> {
  static {
    try {
      DriverManager.registerDriver(new OracleTestDriver());
    } catch (SQLException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    return doConnect(url, OracleConnection.class);
  }
}
