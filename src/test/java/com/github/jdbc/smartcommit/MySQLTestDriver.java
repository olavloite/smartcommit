package com.github.jdbc.smartcommit;

import com.mysql.cj.MysqlConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Mock class for the MySQL driver. */
public class MySQLTestDriver extends com.mysql.cj.jdbc.Driver
    implements ITestDriver<MysqlConnection> {

  static {
    try {
      DriverManager.registerDriver(new MySQLTestDriver());
    } catch (SQLException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  public MySQLTestDriver() throws SQLException {}

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    return doConnect(url, MysqlConnection.class);
  }
}
