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
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractBaseStatement<T extends Statement> extends AbstractDelegateWrapper<T> {
  private static final Logger log = Logger.getLogger(AbstractBaseStatement.class.getName());
  final SmartCommitConnection connection;

  AbstractBaseStatement(SmartCommitConnection connection, T delegate) {
    super(delegate);
    this.connection = connection;
  }

  void turnOffAutocommitIfDml(String sql) throws SQLException {
    if (connection.getDelegateAutoCommit() && StatementParser.isUpdateOrDdl(sql)) {
      log.log(Level.FINEST, "Turning off autocommit on {0}", connection);
      connection.setDelegateAutoCommit(false);
    }
  }

  void turnOffAutocommit() throws SQLException {
    if (connection.getDelegateAutoCommit()) {
      log.log(Level.FINEST, "Turning off autocommit on {0}", connection);
      connection.setDelegateAutoCommit(false);
    }
  }
}
