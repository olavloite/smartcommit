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

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmartCommitConnection extends AbstractDelegateWrapper<Connection> implements Connection {
  private static final Logger log = Logger.getLogger(SmartCommitConnection.class.getName());
  
  /** Flag for turning on/off smartCommit for the connection. This is enabled by default, but will only have effect as long as autoCommit=false for this connection. */
  private boolean smartCommit = true;
  
  /** Flag for turning on/off autoCommit for the connection. Setting autoCommit=true will automatically cause the smartCommit setting to have no effect until autoCommit is switched back off. */
  private boolean autoCommit;

  SmartCommitConnection(Connection delegate) throws SQLException {
    super(delegate);
    this.autoCommit = delegate.getAutoCommit();
  }

  public Statement createStatement() throws SQLException {
    return new SmartCommitStatement<>(this, delegate.createStatement());
  }

  public PreparedStatement prepareStatement(String sql) throws SQLException {
    return new SmartCommitPreparedStatement<>(this, delegate.prepareStatement(sql), sql);
  }

  public CallableStatement prepareCall(String sql) throws SQLException {
    return new SmartCommitCallableStatement<>(this, delegate.prepareCall(sql), sql);
  }

  public String nativeSQL(String sql) throws SQLException {
    return delegate.nativeSQL(sql);
  }

  /** Returns true if the underlying connection is currently running in autocommit mode. */
  public boolean getDelegateAutoCommit() throws SQLException {
    return delegate.getAutoCommit();
  }

  void setDelegateAutoCommit(boolean autoCommit) throws SQLException {
    delegate.setAutoCommit(autoCommit);
  }

  public void setAutoCommit(boolean autoCommit) throws SQLException {
    if (this.autoCommit == autoCommit) {
      // no change needed.
      return;
    }
    
    if (autoCommit) {
      // Turning on autoCommit.
      // First try to change the underlying connection.
      delegate.setAutoCommit(true);
      this.autoCommit = true;
    } else {
      // Turning off autoCommit.
      // Set the underlying connection based on the smartCommit setting.
      delegate.setAutoCommit(this.smartCommit);
      this.autoCommit = false;
    }
  }

  public boolean getAutoCommit() throws SQLException {
    return autoCommit;
  }
  
  public void setSmartCommit(boolean smartCommit) throws SQLException {
    if (this.smartCommit == smartCommit) {
      return;
    }
    
    if (smartCommit) {
      delegate.setAutoCommit(true);
      this.smartCommit = true;
    } else {
      if (!autoCommit && delegate.getAutoCommit()) {
        delegate.setAutoCommit(false);
      }
      this.smartCommit = false;
    }
  }
  
  public boolean getSmartCommit() {
    return smartCommit;
  }

  public void commit() throws SQLException {
    if (!smartCommit) {
      delegate.commit();
      return;
    }
    if (autoCommit) {
      throw new SQLException("Cannot commit when in autocommit");
    }
    if (delegate.getAutoCommit()) {
      log.log(Level.FINEST, "Connection {0} in autocommit, skipping commit", this);
    } else {
      log.log(Level.FINEST, "Committing on connection {0}", this);
      delegate.commit();
    }
    setDelegateAutoCommit(true);
  }

  public void rollback() throws SQLException {
    if (!smartCommit) {
      delegate.rollback();
      return;
    }
    if (autoCommit) {
      throw new SQLException("Cannot rollback when in autocommit");
    }
    if (delegate.getAutoCommit()) {
      log.log(Level.FINEST, "Connection {0} in autocommit, skipping rollback", this);
    } else {
      log.log(Level.FINEST, "Rollback on connection {0}", this);
      delegate.rollback();
    }
    setDelegateAutoCommit(true);
  }

  public void close() throws SQLException {
    delegate.close();
  }

  public boolean isClosed() throws SQLException {
    return delegate.isClosed();
  }

  public DatabaseMetaData getMetaData() throws SQLException {
    return new SmartCommitDatabaseMetaData(this, delegate.getMetaData());
  }

  public void setReadOnly(boolean readOnly) throws SQLException {
    delegate.setReadOnly(readOnly);
  }

  public boolean isReadOnly() throws SQLException {
    return delegate.isReadOnly();
  }

  public void setCatalog(String catalog) throws SQLException {
    delegate.setCatalog(catalog);
  }

  public String getCatalog() throws SQLException {
    return delegate.getCatalog();
  }

  public void setTransactionIsolation(int level) throws SQLException {
    delegate.setTransactionIsolation(level);
  }

  public int getTransactionIsolation() throws SQLException {
    return delegate.getTransactionIsolation();
  }

  public SQLWarning getWarnings() throws SQLException {
    return delegate.getWarnings();
  }

  public void clearWarnings() throws SQLException {
    delegate.clearWarnings();
  }

  public Statement createStatement(int resultSetType, int resultSetConcurrency)
      throws SQLException {
    return new SmartCommitStatement<>(
        this, delegate.createStatement(resultSetType, resultSetConcurrency));
  }

  public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
      throws SQLException {
    return new SmartCommitPreparedStatement<>(
        this, delegate.prepareStatement(sql, resultSetType, resultSetConcurrency), sql);
  }

  public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
      throws SQLException {
    return new SmartCommitCallableStatement<>(
        this, delegate.prepareCall(sql, resultSetType, resultSetConcurrency), sql);
  }

  public Map<String, Class<?>> getTypeMap() throws SQLException {
    return delegate.getTypeMap();
  }

  public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
    delegate.setTypeMap(map);
  }

  public void setHoldability(int holdability) throws SQLException {
    delegate.setHoldability(holdability);
  }

  public int getHoldability() throws SQLException {
    return delegate.getHoldability();
  }

  public Savepoint setSavepoint() throws SQLException {
    if (autoCommit) {
      throw new SQLException("Cannot set savepoint when in autocommit");
    }
    if (getDelegateAutoCommit()) {
      setDelegateAutoCommit(false);
    }
    return delegate.setSavepoint();
  }

  public Savepoint setSavepoint(String name) throws SQLException {
    if (autoCommit) {
      throw new SQLException("Cannot set savepoint when in autocommit");
    }
    if (getDelegateAutoCommit()) {
      setDelegateAutoCommit(false);
    }
    return delegate.setSavepoint(name);
  }

  public void rollback(Savepoint savepoint) throws SQLException {
    if (autoCommit) {
      throw new SQLException("Cannot rollback savepoint when in autocommit");
    }
    if (delegate.getAutoCommit()) {
      log.log(Level.FINEST, "Connection {0} in autocommit, skipping rollback savepoint", this);
    } else {
      log.log(Level.FINEST, "Rollback savepoint {0}", savepoint);
      delegate.rollback(savepoint);
    }
  }

  public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    if (delegate.getAutoCommit()) {
      log.log(Level.FINEST, "Connection {0} in autocommit, skipping release savepoint", this);
    } else {
      log.log(Level.FINEST, "Release savepoint {0}", savepoint);
      delegate.releaseSavepoint(savepoint);
    }
  }

  public Statement createStatement(
      int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    return new SmartCommitStatement<>(
        this, delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
  }

  public PreparedStatement prepareStatement(
      String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
      throws SQLException {
    return new SmartCommitPreparedStatement<>(
        this,
        delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability),
        sql);
  }

  public CallableStatement prepareCall(
      String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
      throws SQLException {
    return new SmartCommitCallableStatement<>(
        this,
        delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability),
        sql);
  }

  public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    return new SmartCommitPreparedStatement<>(
        this, delegate.prepareStatement(sql, autoGeneratedKeys), sql);
  }

  public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    return new SmartCommitPreparedStatement<>(
        this, delegate.prepareStatement(sql, columnIndexes), sql);
  }

  public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    return new SmartCommitPreparedStatement<>(
        this, delegate.prepareStatement(sql, columnNames), sql);
  }

  public Clob createClob() throws SQLException {
    return delegate.createClob();
  }

  public Blob createBlob() throws SQLException {
    return delegate.createBlob();
  }

  public NClob createNClob() throws SQLException {
    return delegate.createNClob();
  }

  public SQLXML createSQLXML() throws SQLException {
    return delegate.createSQLXML();
  }

  public boolean isValid(int timeout) throws SQLException {
    return delegate.isValid(timeout);
  }

  public void setClientInfo(String name, String value) throws SQLClientInfoException {
    delegate.setClientInfo(name, value);
  }

  public void setClientInfo(Properties properties) throws SQLClientInfoException {
    delegate.setClientInfo(properties);
  }

  public String getClientInfo(String name) throws SQLException {
    return delegate.getClientInfo(name);
  }

  public Properties getClientInfo() throws SQLException {
    return delegate.getClientInfo();
  }

  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
    return delegate.createArrayOf(typeName, elements);
  }

  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
    return delegate.createStruct(typeName, attributes);
  }

  public void setSchema(String schema) throws SQLException {
    delegate.setSchema(schema);
  }

  public String getSchema() throws SQLException {
    return delegate.getSchema();
  }

  public void abort(Executor executor) throws SQLException {
    delegate.abort(executor);
  }

  public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
    delegate.setNetworkTimeout(executor, milliseconds);
  }

  public int getNetworkTimeout() throws SQLException {
    return delegate.getNetworkTimeout();
  }
}
