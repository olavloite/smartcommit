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
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.junit.BeforeClass;
import org.junit.Test;

public class SmartCommitConnectionTest {
  private static final String CREATE_TABLE =
      "CREATE TABLE NUMBERS (NUM BIGINT PRIMARY KEY, NAME VARCHAR)";

  @BeforeClass
  public static void setup() throws Exception {
    Class.forName("com.github.jdbc.smartcommit.SmartCommitDriver");
    Class.forName("org.h2.Driver");
  }

  static SmartCommitConnection createConnection() throws SQLException {
    SmartCommitConnection res = DriverManager.getConnection("jdbc:smartcommit:h2:mem:test")
        .unwrap(SmartCommitConnection.class);
    res.createStatement().execute(CREATE_TABLE);
    res.setAutoCommit(false);
    return res;
  }

  @Test
  public void testDefaultSmartCommit() throws SQLException {
    try (SmartCommitConnection connection = createConnection()) {
      assertThat(connection.getAutoCommit()).isFalse();
      assertThat(connection.getSmartCommit()).isTrue();
    }
  }

  @Test
  public void testSwitchAutoCommit() throws SQLException {
    try (SmartCommitConnection connection = createConnection()) {
      // Switch autoCommit on.
      connection.setAutoCommit(true);
      assertThat(connection.getAutoCommit()).isTrue();
      assertThat(connection.getDelegateAutoCommit()).isTrue();

      // Switch autoCommit off with smartCommit on.
      connection.setAutoCommit(false);
      assertThat(connection.getAutoCommit()).isFalse();
      assertThat(connection.getDelegateAutoCommit()).isTrue();

      connection.setSmartCommit(false);

      // Switch autoCommit on with smartCommit off.
      connection.setAutoCommit(true);
      assertThat(connection.getAutoCommit()).isTrue();
      assertThat(connection.getDelegateAutoCommit()).isTrue();

      // Switch autoCommit off with smartCommit off.
      connection.setAutoCommit(false);
      assertThat(connection.getAutoCommit()).isFalse();
      assertThat(connection.getDelegateAutoCommit()).isFalse();
    }
  }

  @Test
  public void testStatementExecute_Insert() throws SQLException {
    try (SmartCommitConnection connection = createConnection()) {
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      connection.createStatement().execute("INSERT INTO NUMBERS (NUM, NAME) VALUES (1, 'One')");
      assertThat(connection.getDelegateAutoCommit()).isFalse();
      connection.commit();
      assertThat(connection.getDelegateAutoCommit()).isTrue();
    }
  }

  @Test
  public void testStatementExecute_Select() throws SQLException {
    try (SmartCommitConnection connection = createConnection()) {
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      try (Statement statement = connection.createStatement()) {
        statement.execute("SELECT * FROM NUMBERS");
      }
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      connection.commit();
      assertThat(connection.getDelegateAutoCommit()).isTrue();
    }
  }

  @Test
  public void testStatementExecuteQuery() throws SQLException {
    try (SmartCommitConnection connection = createConnection()) {
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      try (ResultSet rs = connection.createStatement().executeQuery("SELECT * FROM NUMBERS")) {
        while (rs.next()) {
        }
      }
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      connection.commit();
      assertThat(connection.getDelegateAutoCommit()).isTrue();
    }
  }

  @Test
  public void testStatementExecuteUpdate() throws SQLException {
    try (SmartCommitConnection connection = createConnection()) {
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      connection.createStatement()
          .executeUpdate("INSERT INTO NUMBERS (NUM, NAME) VALUES (1, 'One')");
      assertThat(connection.getDelegateAutoCommit()).isFalse();
      connection.commit();
      assertThat(connection.getDelegateAutoCommit()).isTrue();
    }
  }

  @Test
  public void testPreparedStatementExecuteQuery() throws SQLException {
    try (SmartCommitConnection connection = createConnection()) {
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      try (PreparedStatement ps = connection.prepareStatement("SELECT * FROM NUMBERS")) {
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
          }
        }
      }
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      connection.commit();
      assertThat(connection.getDelegateAutoCommit()).isTrue();
    }
  }

  @Test
  public void testPreparedStatementExecuteUpdate() throws SQLException {
    try (SmartCommitConnection connection = createConnection()) {
      assertThat(connection.getDelegateAutoCommit()).isTrue();
      try (PreparedStatement ps =
          connection.prepareStatement("INSERT INTO NUMBERS (NUM, NAME) VALUES (?, ?)")) {
        ps.setLong(1, 1L);
        ps.setString(2, "One");
        ps.executeUpdate();
      }
      assertThat(connection.getDelegateAutoCommit()).isFalse();
      connection.commit();
      assertThat(connection.getDelegateAutoCommit()).isTrue();
    }
  }

}
