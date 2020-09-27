package com.github.jdbc.smartcommit;

import java.sql.SQLException;
import java.sql.Wrapper;

abstract class AbstractDelegateWrapper<D extends Wrapper> implements Wrapper {
  final D delegate;

  AbstractDelegateWrapper(D delegate) {
    this.delegate = delegate;
  }

  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (getClass().equals(iface)) {
      return (T) this;
    }
    return delegate.unwrap(iface);
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    if (getClass().equals(iface)) {
      return true;
    }
    return delegate.isWrapperFor(iface);
  }
}
