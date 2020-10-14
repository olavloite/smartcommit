/*
 * Copyright 2020 Knut Olav Løite
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.github.jdbc.smartcommit;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class SmartCommitDriver implements Driver {
  private static final String DRIVER_PREFIX = "jdbc:smartcommit:";
  private static final int DRIVER_MAJOR_VERSION = 1;
  private static final int DRIVER_MINOR_VERSION = 0;
  private static final Logger PARENT_LOGGER = Logger.getLogger("com.github.jdbc.smartcommit");

  private static final Map<String, String> WELL_KNOWN_DRIVERS = new HashMap<>();

  static {
    WELL_KNOWN_DRIVERS.put("jdbc:h2:", "org.h2.Driver");
    WELL_KNOWN_DRIVERS.put("jdbc:postgresql:", "org.postgresql.Driver");
    WELL_KNOWN_DRIVERS.put("jdbc:mysql:", "com.mysql.cj.jdbc.Driver");
    WELL_KNOWN_DRIVERS.put("jdbc:cloudspanner:", "com.google.cloud.spanner.jdbc.JdbcDriver");
    WELL_KNOWN_DRIVERS.put("jdbc:sqlserver:", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    WELL_KNOWN_DRIVERS.put("jdbc:oracle:", "oracle.jdbc.OracleDriver");
  }

  static class DelegateInfo {
    private final Driver driver;
    private final String url;

    DelegateInfo(Driver driver, String url) {
      this.driver = driver;
      this.url = url;
    }
  }

  private static final AtomicReference<SmartCommitDriver> registeredDriver = new AtomicReference<>();

  static {
    try {
      register();
    } catch (SQLException e) {
      java.sql.DriverManager.println("Registering driver failed: " + e.getMessage());
    }
  }

  static void register() throws SQLException {
    SmartCommitDriver driver = new SmartCommitDriver();
    if (registeredDriver.compareAndSet(null, driver)) {
      DriverManager.registerDriver(driver);
    }
  }

  /**
   * According to JDBC specification, this driver is registered against {@link DriverManager} when
   * the class is loaded. To avoid leaks, this method allow unregistering the driver so that the
   * class can be gc'ed if necessary.
   *
   * @throws SQLException if deregistering the driver fails
   */
  static void deregister() throws SQLException {
    DriverManager.deregisterDriver(registeredDriver.getAndSet(null));
  }

  public SmartCommitDriver() {}

  private DelegateInfo getDelegateInfo(String url) throws SQLException {
    if (acceptsURL(url)) {
      String delegateUrl = "jdbc:" + url.substring(DRIVER_PREFIX.length());
      tryRegisterDriver(delegateUrl);
      Driver driver = DriverManager.getDriver(delegateUrl);
      if (driver != null) {
        return new DelegateInfo(driver, delegateUrl);
      }
    }
    return null;
  }

  private void tryRegisterDriver(String url) throws SQLException {
    // Iterate over all drivers than can be found and try to dynamically load that driver.
    Iterator<Driver> iterator = ServiceLoader.load(Driver.class).iterator();
    while (iterator.hasNext()) {
      try {
        Driver driver = iterator.next();
        if (driver.acceptsURL(url)) {
          // Driver found.
          return;
        }
      } catch (Throwable t) {
        // ignore and try the next one.
      }
    }
    // No driver found during dynamic loading. Check if it is a well-known driver.
    int secondColon = url.indexOf(':', "jdbc:".length());
    if (secondColon == -1) {
      return;
    }
    String type = url.substring(0, secondColon + 1);
    try {
      String className = WELL_KNOWN_DRIVERS.get(type);
      if (className != null) {
        Class.forName(className);
      }
    } catch (ClassNotFoundException e) {
      // ignore and let the error propagate to the client application.
    }
  }

  public Connection connect(String url, Properties info) throws SQLException {
    DelegateInfo delegateInfo = getDelegateInfo(url);
    if (delegateInfo != null) {
      Connection delegateConnection = delegateInfo.driver.connect(delegateInfo.url, info);
      if (delegateConnection != null) {
        return new SmartCommitConnection(delegateConnection);
      }
      throw new SQLException(
          String.format("Could not open a delegate connection for URL %s", delegateInfo.url));
    }
    return null;
  }

  public boolean acceptsURL(String url) throws SQLException {
    return url.startsWith(DRIVER_PREFIX);
  }

  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    DelegateInfo delegate = getDelegateInfo(url);
    if (delegate != null) {
      return delegate.driver.getPropertyInfo(delegate.url, info);
    }
    return new DriverPropertyInfo[0];
  }

  public int getMajorVersion() {
    return DRIVER_MAJOR_VERSION;
  }

  public int getMinorVersion() {
    return DRIVER_MINOR_VERSION;
  }

  public boolean jdbcCompliant() {
    return true;
  }

  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return PARENT_LOGGER;
  }
}
