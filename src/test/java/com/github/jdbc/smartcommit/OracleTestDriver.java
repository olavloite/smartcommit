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
