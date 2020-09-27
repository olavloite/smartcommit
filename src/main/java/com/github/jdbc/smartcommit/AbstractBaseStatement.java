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
    if (connection.getDelegateAutoCommit() && StatementParser.isDml(sql)) {
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
