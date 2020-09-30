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

import com.microsoft.sqlserver.jdbc.ISQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/** Mock class for the SQL Server driver. */
public class SqlServerTestDriver implements ITestDriver<ISQLServerConnection> {

  static {
    try {
      DriverManager.registerDriver(new SqlServerTestDriver());
    } catch (SQLException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  // Must use a delegate as SQLServerDriver is final
  private final SQLServerDriver delegate = new SQLServerDriver();

  public SqlServerTestDriver() throws SQLException {}

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    return doConnect(url, ISQLServerConnection.class);
  }

  public int hashCode() {
    return delegate.hashCode();
  }

  public boolean equals(Object obj) {
    return delegate.equals(obj);
  }

  public final String toString() {
    return delegate.toString();
  }

  public boolean acceptsURL(String url) throws SQLServerException {
    return delegate.acceptsURL(url);
  }

  public DriverPropertyInfo[] getPropertyInfo(String Url, Properties Info)
      throws SQLServerException {
    return delegate.getPropertyInfo(Url, Info);
  }

  public int getMajorVersion() {
    return delegate.getMajorVersion();
  }

  public int getMinorVersion() {
    return delegate.getMinorVersion();
  }

  public Logger getParentLogger() {
    return delegate.getParentLogger();
  }

  public boolean jdbcCompliant() {
    return delegate.jdbcCompliant();
  }
}
