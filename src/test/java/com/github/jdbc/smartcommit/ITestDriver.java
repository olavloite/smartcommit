package com.github.jdbc.smartcommit;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;

public interface ITestDriver<T> extends Driver {

  default Connection doConnect(String url, Class<T> realConnectionClass)
      throws SQLException {
    if (acceptsURL(url)) {
      Connection connection = mock(Connection.class);
      when(connection.isWrapperFor(realConnectionClass)).thenReturn(true);
      return connection;
    }
    return null;
  }
}
