package com.github.jdbc.smartcommit;

import com.google.cloud.spanner.jdbc.CloudSpannerJdbcConnection;
import com.google.cloud.spanner.jdbc.JdbcDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Mock class for the Cloud Spanner driver. */
public class CloudSpannerTestDriver extends JdbcDriver
    implements ITestDriver<CloudSpannerJdbcConnection> {
  static {
    try {
      DriverManager.registerDriver(new CloudSpannerTestDriver());
    } catch (SQLException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    return doConnect(url, CloudSpannerJdbcConnection.class);
  }
}
