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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLType;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

class SmartCommitCallableStatement<T extends CallableStatement>
    extends SmartCommitPreparedStatement<T> implements CallableStatement {
  SmartCommitCallableStatement(SmartCommitConnection connection, T delegate, String sql) {
    super(connection, delegate, sql);
  }

  public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType);
  }

  public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType, scale);
  }

  public boolean wasNull() throws SQLException {
    return delegate.wasNull();
  }

  public String getString(int parameterIndex) throws SQLException {
    return delegate.getString(parameterIndex);
  }

  public boolean getBoolean(int parameterIndex) throws SQLException {
    return delegate.getBoolean(parameterIndex);
  }

  public byte getByte(int parameterIndex) throws SQLException {
    return delegate.getByte(parameterIndex);
  }

  public short getShort(int parameterIndex) throws SQLException {
    return delegate.getShort(parameterIndex);
  }

  public int getInt(int parameterIndex) throws SQLException {
    return delegate.getInt(parameterIndex);
  }

  public long getLong(int parameterIndex) throws SQLException {
    return delegate.getLong(parameterIndex);
  }

  public float getFloat(int parameterIndex) throws SQLException {
    return delegate.getFloat(parameterIndex);
  }

  public double getDouble(int parameterIndex) throws SQLException {
    return delegate.getDouble(parameterIndex);
  }

  @SuppressWarnings("deprecation")
  public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
    return delegate.getBigDecimal(parameterIndex, scale);
  }

  public byte[] getBytes(int parameterIndex) throws SQLException {
    return delegate.getBytes(parameterIndex);
  }

  public Date getDate(int parameterIndex) throws SQLException {
    return delegate.getDate(parameterIndex);
  }

  public Time getTime(int parameterIndex) throws SQLException {
    return delegate.getTime(parameterIndex);
  }

  public Timestamp getTimestamp(int parameterIndex) throws SQLException {
    return delegate.getTimestamp(parameterIndex);
  }

  public Object getObject(int parameterIndex) throws SQLException {
    return delegate.getObject(parameterIndex);
  }

  public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
    return delegate.getBigDecimal(parameterIndex);
  }

  public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
    return delegate.getObject(parameterIndex, map);
  }

  public Ref getRef(int parameterIndex) throws SQLException {
    return delegate.getRef(parameterIndex);
  }

  public Blob getBlob(int parameterIndex) throws SQLException {
    return delegate.getBlob(parameterIndex);
  }

  public Clob getClob(int parameterIndex) throws SQLException {
    return delegate.getClob(parameterIndex);
  }

  public Array getArray(int parameterIndex) throws SQLException {
    return delegate.getArray(parameterIndex);
  }

  public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
    return delegate.getDate(parameterIndex, cal);
  }

  public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
    return delegate.getTime(parameterIndex, cal);
  }

  public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
    return delegate.getTimestamp(parameterIndex, cal);
  }

  public void registerOutParameter(int parameterIndex, int sqlType, String typeName)
      throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType, typeName);
  }

  public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType);
  }

  public void registerOutParameter(String parameterName, int sqlType, int scale)
      throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType, scale);
  }

  public void registerOutParameter(String parameterName, int sqlType, String typeName)
      throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType, typeName);
  }

  public URL getURL(int parameterIndex) throws SQLException {
    return delegate.getURL(parameterIndex);
  }

  public void setURL(String parameterName, URL val) throws SQLException {
    delegate.setURL(parameterName, val);
  }

  public void setNull(String parameterName, int sqlType) throws SQLException {
    delegate.setNull(parameterName, sqlType);
  }

  public void setBoolean(String parameterName, boolean x) throws SQLException {
    delegate.setBoolean(parameterName, x);
  }

  public void setByte(String parameterName, byte x) throws SQLException {
    delegate.setByte(parameterName, x);
  }

  public void setShort(String parameterName, short x) throws SQLException {
    delegate.setShort(parameterName, x);
  }

  public void setInt(String parameterName, int x) throws SQLException {
    delegate.setInt(parameterName, x);
  }

  public void setLong(String parameterName, long x) throws SQLException {
    delegate.setLong(parameterName, x);
  }

  public void setFloat(String parameterName, float x) throws SQLException {
    delegate.setFloat(parameterName, x);
  }

  public void setDouble(String parameterName, double x) throws SQLException {
    delegate.setDouble(parameterName, x);
  }

  public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
    delegate.setBigDecimal(parameterName, x);
  }

  public void setString(String parameterName, String x) throws SQLException {
    delegate.setString(parameterName, x);
  }

  public void setBytes(String parameterName, byte[] x) throws SQLException {
    delegate.setBytes(parameterName, x);
  }

  public void setDate(String parameterName, Date x) throws SQLException {
    delegate.setDate(parameterName, x);
  }

  public void setTime(String parameterName, Time x) throws SQLException {
    delegate.setTime(parameterName, x);
  }

  public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
    delegate.setTimestamp(parameterName, x);
  }

  public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
    delegate.setAsciiStream(parameterName, x, length);
  }

  public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
    delegate.setBinaryStream(parameterName, x, length);
  }

  public void setObject(String parameterName, Object x, int targetSqlType, int scale)
      throws SQLException {
    delegate.setObject(parameterName, x, targetSqlType, scale);
  }

  public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
    delegate.setObject(parameterName, x, targetSqlType);
  }

  public void setObject(String parameterName, Object x) throws SQLException {
    delegate.setObject(parameterName, x);
  }

  public void setCharacterStream(String parameterName, Reader reader, int length)
      throws SQLException {
    delegate.setCharacterStream(parameterName, reader, length);
  }

  public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
    delegate.setDate(parameterName, x, cal);
  }

  public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
    delegate.setTime(parameterName, x, cal);
  }

  public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
    delegate.setTimestamp(parameterName, x, cal);
  }

  public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
    delegate.setNull(parameterName, sqlType, typeName);
  }

  public String getString(String parameterName) throws SQLException {
    return delegate.getString(parameterName);
  }

  public boolean getBoolean(String parameterName) throws SQLException {
    return delegate.getBoolean(parameterName);
  }

  public byte getByte(String parameterName) throws SQLException {
    return delegate.getByte(parameterName);
  }

  public short getShort(String parameterName) throws SQLException {
    return delegate.getShort(parameterName);
  }

  public int getInt(String parameterName) throws SQLException {
    return delegate.getInt(parameterName);
  }

  public long getLong(String parameterName) throws SQLException {
    return delegate.getLong(parameterName);
  }

  public float getFloat(String parameterName) throws SQLException {
    return delegate.getFloat(parameterName);
  }

  public double getDouble(String parameterName) throws SQLException {
    return delegate.getDouble(parameterName);
  }

  public byte[] getBytes(String parameterName) throws SQLException {
    return delegate.getBytes(parameterName);
  }

  public Date getDate(String parameterName) throws SQLException {
    return delegate.getDate(parameterName);
  }

  public Time getTime(String parameterName) throws SQLException {
    return delegate.getTime(parameterName);
  }

  public Timestamp getTimestamp(String parameterName) throws SQLException {
    return delegate.getTimestamp(parameterName);
  }

  public Object getObject(String parameterName) throws SQLException {
    return delegate.getObject(parameterName);
  }

  public BigDecimal getBigDecimal(String parameterName) throws SQLException {
    return delegate.getBigDecimal(parameterName);
  }

  public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
    return delegate.getObject(parameterName, map);
  }

  public Ref getRef(String parameterName) throws SQLException {
    return delegate.getRef(parameterName);
  }

  public Blob getBlob(String parameterName) throws SQLException {
    return delegate.getBlob(parameterName);
  }

  public Clob getClob(String parameterName) throws SQLException {
    return delegate.getClob(parameterName);
  }

  public Array getArray(String parameterName) throws SQLException {
    return delegate.getArray(parameterName);
  }

  public Date getDate(String parameterName, Calendar cal) throws SQLException {
    return delegate.getDate(parameterName, cal);
  }

  public Time getTime(String parameterName, Calendar cal) throws SQLException {
    return delegate.getTime(parameterName, cal);
  }

  public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
    return delegate.getTimestamp(parameterName, cal);
  }

  public URL getURL(String parameterName) throws SQLException {
    return delegate.getURL(parameterName);
  }

  public RowId getRowId(int parameterIndex) throws SQLException {
    return delegate.getRowId(parameterIndex);
  }

  public RowId getRowId(String parameterName) throws SQLException {
    return delegate.getRowId(parameterName);
  }

  public void setRowId(String parameterName, RowId x) throws SQLException {
    delegate.setRowId(parameterName, x);
  }

  public void setNString(String parameterName, String value) throws SQLException {
    delegate.setNString(parameterName, value);
  }

  public void setNCharacterStream(String parameterName, Reader value, long length)
      throws SQLException {
    delegate.setNCharacterStream(parameterName, value, length);
  }

  public void setNClob(String parameterName, NClob value) throws SQLException {
    delegate.setNClob(parameterName, value);
  }

  public void setClob(String parameterName, Reader reader, long length) throws SQLException {
    delegate.setClob(parameterName, reader, length);
  }

  public void setBlob(String parameterName, InputStream inputStream, long length)
      throws SQLException {
    delegate.setBlob(parameterName, inputStream, length);
  }

  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
    delegate.setNClob(parameterName, reader, length);
  }

  public NClob getNClob(int parameterIndex) throws SQLException {
    return delegate.getNClob(parameterIndex);
  }

  public NClob getNClob(String parameterName) throws SQLException {
    return delegate.getNClob(parameterName);
  }

  public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
    delegate.setSQLXML(parameterName, xmlObject);
  }

  public SQLXML getSQLXML(int parameterIndex) throws SQLException {
    return delegate.getSQLXML(parameterIndex);
  }

  public SQLXML getSQLXML(String parameterName) throws SQLException {
    return delegate.getSQLXML(parameterName);
  }

  public String getNString(int parameterIndex) throws SQLException {
    return delegate.getNString(parameterIndex);
  }

  public String getNString(String parameterName) throws SQLException {
    return delegate.getNString(parameterName);
  }

  public Reader getNCharacterStream(int parameterIndex) throws SQLException {
    return delegate.getNCharacterStream(parameterIndex);
  }

  public Reader getNCharacterStream(String parameterName) throws SQLException {
    return delegate.getNCharacterStream(parameterName);
  }

  public Reader getCharacterStream(int parameterIndex) throws SQLException {
    return delegate.getCharacterStream(parameterIndex);
  }

  public Reader getCharacterStream(String parameterName) throws SQLException {
    return delegate.getCharacterStream(parameterName);
  }

  public void setBlob(String parameterName, Blob x) throws SQLException {
    delegate.setBlob(parameterName, x);
  }

  public void setClob(String parameterName, Clob x) throws SQLException {
    delegate.setClob(parameterName, x);
  }

  public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
    delegate.setAsciiStream(parameterName, x, length);
  }

  public void setBinaryStream(String parameterName, InputStream x, long length)
      throws SQLException {
    delegate.setBinaryStream(parameterName, x, length);
  }

  public void setCharacterStream(String parameterName, Reader reader, long length)
      throws SQLException {
    delegate.setCharacterStream(parameterName, reader, length);
  }

  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
    delegate.setAsciiStream(parameterName, x);
  }

  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
    delegate.setBinaryStream(parameterName, x);
  }

  public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
    delegate.setCharacterStream(parameterName, reader);
  }

  public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
    delegate.setNCharacterStream(parameterName, value);
  }

  public void setClob(String parameterName, Reader reader) throws SQLException {
    delegate.setClob(parameterName, reader);
  }

  public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
    delegate.setBlob(parameterName, inputStream);
  }

  public void setNClob(String parameterName, Reader reader) throws SQLException {
    delegate.setNClob(parameterName, reader);
  }

  public <O> O getObject(int parameterIndex, Class<O> type) throws SQLException {
    return delegate.getObject(parameterIndex, type);
  }

  public <O> O getObject(String parameterName, Class<O> type) throws SQLException {
    return delegate.getObject(parameterName, type);
  }

  public void setObject(String parameterName, Object x, SQLType targetSqlType, int scaleOrLength)
      throws SQLException {
    delegate.setObject(parameterName, x, targetSqlType, scaleOrLength);
  }

  public void setObject(String parameterName, Object x, SQLType targetSqlType) throws SQLException {
    delegate.setObject(parameterName, x, targetSqlType);
  }

  public void registerOutParameter(int parameterIndex, SQLType sqlType) throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType);
  }

  public void registerOutParameter(int parameterIndex, SQLType sqlType, int scale)
      throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType, scale);
  }

  public void registerOutParameter(int parameterIndex, SQLType sqlType, String typeName)
      throws SQLException {
    delegate.registerOutParameter(parameterIndex, sqlType, typeName);
  }

  public void registerOutParameter(String parameterName, SQLType sqlType) throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType);
  }

  public void registerOutParameter(String parameterName, SQLType sqlType, int scale)
      throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType, scale);
  }

  public void registerOutParameter(String parameterName, SQLType sqlType, String typeName)
      throws SQLException {
    delegate.registerOutParameter(parameterName, sqlType, typeName);
  }
}
