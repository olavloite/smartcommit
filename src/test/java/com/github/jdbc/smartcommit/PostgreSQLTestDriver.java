package com.github.jdbc.smartcommit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.postgresql.PGConnection;

/** Mock class for the PostgreSQL driver. */
public class PostgreSQLTestDriver extends org.postgresql.Driver implements ITestDriver<PGConnection> {
  static {
    try {
      DriverManager.registerDriver(new PostgreSQLTestDriver());
    } catch (SQLException e) {
      throw new ExceptionInInitializerError(e);
    }
  }
  
  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    return doConnect(url, PGConnection.class);
  }
}
