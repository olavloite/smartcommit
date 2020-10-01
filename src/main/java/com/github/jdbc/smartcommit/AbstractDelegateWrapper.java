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
