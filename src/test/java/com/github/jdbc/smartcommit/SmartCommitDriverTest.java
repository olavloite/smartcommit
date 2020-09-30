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

import static com.google.common.truth.Truth.assertThat;
import com.google.cloud.spanner.jdbc.CloudSpannerJdbcConnection;
import com.microsoft.sqlserver.jdbc.ISQLServerConnection;
import com.mysql.cj.MysqlConnection;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import org.junit.BeforeClass;
import org.junit.Test;
import org.postgresql.PGConnection;
import oracle.jdbc.OracleConnection;

public class SmartCommitDriverTest {

  @BeforeClass
  public static void registerDrivers()
      throws ClassNotFoundException, SQLException, NoSuchMethodException, SecurityException,
          IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    Class.forName(SmartCommitDriver.class.getName());
    Class.forName(PostgreSQLTestDriver.class.getName());
    Class.forName(CloudSpannerTestDriver.class.getName());
    Class.forName(MySQLTestDriver.class.getName());
    Class.forName(SqlServerTestDriver.class.getName());
    Class.forName(OracleTestDriver.class.getName());

    // Deregister real drivers to ensure the test drivers will create the connections.
    Enumeration<Driver> drivers = DriverManager.getDrivers();
    while (drivers.hasMoreElements()) {
      Driver driver = drivers.nextElement();
      if (!(driver instanceof SmartCommitDriver || driver instanceof ITestDriver)) {
        DriverManager.deregisterDriver(driver);
      }
    }
  }

  @Test
  public void testConnectPostgreSQL() throws SQLException {
    try (Connection con =
        DriverManager.getConnection("jdbc:smartcommit:postgresql://localhost/foo")) {
      assertThat(con).isInstanceOf(SmartCommitConnection.class);
      assertThat(con.isWrapperFor(PGConnection.class)).isTrue();
    }
  }

  @Test
  public void testConnectCloudSpanner() throws SQLException {
    try (Connection con =
        DriverManager.getConnection(
            "jdbc:smartcommit:cloudspanner:/projects/p/instances/i/databases/d")) {
      assertThat(con).isInstanceOf(SmartCommitConnection.class);
      assertThat(con.isWrapperFor(CloudSpannerJdbcConnection.class)).isTrue();
    }
  }

  @Test
  public void testConnectMySQL() throws SQLException {
    try (Connection con =
        DriverManager.getConnection("jdbc:smartcommit:mysql://localhost:3306/foo")) {
      assertThat(con).isInstanceOf(SmartCommitConnection.class);
      assertThat(con.isWrapperFor(MysqlConnection.class)).isTrue();
    }
  }

  @Test
  public void testConnectSQLServer() throws SQLException {
    try (Connection con =
        DriverManager.getConnection(
            "jdbc:smartcommit:sqlserver://localhost;databaseName=AdventureWorks;integratedSecurity=true;")) {
      assertThat(con).isInstanceOf(SmartCommitConnection.class);
      assertThat(con.isWrapperFor(ISQLServerConnection.class)).isTrue();
    }
  }

  @Test
  public void testConnectOracleThin() throws SQLException {
    try (Connection con = DriverManager.getConnection("jdbc:smartcommit:oracle:thin:@myhost:1521:orcl")) {
      assertThat(con).isInstanceOf(SmartCommitConnection.class);
      assertThat(con.isWrapperFor(OracleConnection.class)).isTrue();
    }
  }

  @Test
  public void testConnectOracleOci() throws SQLException {
    try (Connection con = DriverManager.getConnection("jdbc:smartcommit:oracle:oci8:scott/tiger@myhost")) {
      assertThat(con).isInstanceOf(SmartCommitConnection.class);
      assertThat(con.isWrapperFor(OracleConnection.class)).isTrue();
    }
  }
}
