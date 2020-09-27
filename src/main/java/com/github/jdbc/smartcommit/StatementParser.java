/*
 * Copyright 2019 Google LLC
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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

class StatementParser {
  /** Singleton instance of {@link StatementParser}. */
  public static final StatementParser INSTANCE = new StatementParser();

  /** The type of statement that has been recognized by the parser. */
  enum StatementType {
    DDL,
    QUERY,
    UPDATE,
    UNKNOWN;
  }

  /** A statement that has been parsed */
  static class ParsedStatement {
    private final StatementType type;
    private final String sqlWithoutComments;

    private static ParsedStatement ddl(String sqlWithoutComments) {
      return new ParsedStatement(StatementType.DDL, sqlWithoutComments);
    }

    private static ParsedStatement query(String sqlWithoutComments) {
      return new ParsedStatement(StatementType.QUERY, sqlWithoutComments);
    }

    private static ParsedStatement update(String sqlWithoutComments) {
      return new ParsedStatement(StatementType.UPDATE, sqlWithoutComments);
    }

    private static ParsedStatement unknown(String sqlWithoutComments) {
      return new ParsedStatement(StatementType.UNKNOWN, sqlWithoutComments);
    }

    private ParsedStatement(StatementType type, String sqlWithoutComments) {
      this.type = type;
      this.sqlWithoutComments = sqlWithoutComments;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.type, this.sqlWithoutComments);
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof ParsedStatement)) {
        return false;
      }
      ParsedStatement o = (ParsedStatement) other;
      return Objects.equals(this.type, o.type)
          && Objects.equals(this.sqlWithoutComments, o.sqlWithoutComments);
    }

    StatementType getType() {
      return type;
    }

    boolean isQuery() {
      switch (type) {
        case QUERY:
          return true;
        case UPDATE:
        case DDL:
        case UNKNOWN:
        default:
      }
      return false;
    }

    boolean isUpdate() {
      switch (type) {
        case UPDATE:
          return true;
        case QUERY:
        case DDL:
        case UNKNOWN:
        default:
      }
      return false;
    }

    boolean isDdl() {
      switch (type) {
        case DDL:
          return true;
        case UPDATE:
        case QUERY:
        case UNKNOWN:
        default:
      }
      return false;
    }

    String getSqlWithoutComments() {
      return sqlWithoutComments;
    }
  }

  private static final Set<String> ddlStatements =
      new HashSet<>(Arrays.asList("CREATE", "DROP", "ALTER"));
  private static final Set<String> selectStatements =
      new HashSet<>(Arrays.asList("SELECT", "WITH"));
  private static final Set<String> dmlStatements =
      new HashSet<>(Arrays.asList("INSERT", "UPDATE", "DELETE"));

  /** Private constructor for singleton instance. */
  private StatementParser() {}

  /**
   * Parses the given statement and categorizes it as one of the possible {@link StatementType}s.
   * The validity of the statement is not checked, unless it is a client-side statement.
   *
   * @param statement The statement to parse.
   * @return the parsed and categorized statement.
   * @throws SQLException
   */
  ParsedStatement parse(String sqlWithComments) throws SQLException {
    String sql = removeCommentsAndTrim(sqlWithComments);
    if (isQuery(sql)) {
      return ParsedStatement.query(sql);
    } else if (isUpdateStatement(sql)) {
      return ParsedStatement.update(sql);
    } else if (isDdlStatement(sql)) {
      return ParsedStatement.ddl(sql);
    }
    return ParsedStatement.unknown(sql);
  }

  /**
   * Checks whether the given statement is (probably) a DDL statement. The method does not check the
   * validity of the statement, only if it is a DDL statement based on the first word in the
   * statement.
   *
   * @param sql The statement to check (without any comments).
   * @return <code>true</code> if the statement is a DDL statement (i.e. starts with 'CREATE',
   *     'ALTER' or 'DROP').
   */
  public boolean isDdlStatement(String sql) {
    return statementStartsWith(sql, ddlStatements);
  }

  /**
   * Checks whether the given statement is (probably) a SELECT query. The method does not check the
   * validity of the statement, only if it is a SELECT statement based on the first word in the
   * statement.
   *
   * @param sql The statement to check (without any comments).
   * @return <code>true</code> if the statement is a SELECT statement (i.e. starts with 'SELECT').
   * @throws SQLException
   */
  public boolean isQuery(String sql) throws SQLException {
    // Skip any query hints at the beginning of the query.
    if (sql.startsWith("@")) {
      sql = removeStatementHint(sql);
    }
    return statementStartsWith(sql, selectStatements);
  }

  /**
   * Checks whether the given statement is (probably) an update statement. The method does not check
   * the validity of the statement, only if it is an update statement based on the first word in the
   * statement.
   *
   * @param sql The statement to check (without any comments).
   * @return <code>true</code> if the statement is a DML update statement (i.e. starts with
   *     'INSERT', 'UPDATE' or 'DELETE').
   */
  public boolean isUpdateStatement(String sql) {
    return statementStartsWith(sql, dmlStatements);
  }

  private boolean statementStartsWith(String sql, Iterable<String> checkStatements) {
    String[] tokens = sql.split("\\s+", 2);
    if (tokens.length > 0) {
      for (String check : checkStatements) {
        if (tokens[0].equalsIgnoreCase(check)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Removes comments from and trims the given sql statement. Spanner supports three types of
   * comments:
   *
   * <ul>
   *   <li>Single line comments starting with '--'
   *   <li>Single line comments starting with '#'
   *   <li>Multi line comments between '/&#42;' and '&#42;/'
   * </ul>
   *
   * Reference: https://cloud.google.com/spanner/docs/lexical#comments
   *
   * @param sql The sql statement to remove comments from and to trim.
   * @return the sql statement without the comments and leading and trailing spaces.
   */
  static String removeCommentsAndTrim(String sql) throws SQLException {
    final char SINGLE_QUOTE = '\'';
    final char DOUBLE_QUOTE = '"';
    final char BACKTICK_QUOTE = '`';
    final char HYPHEN = '-';
    final char DASH = '#';
    final char SLASH = '/';
    final char ASTERIKS = '*';
    boolean isInQuoted = false;
    boolean isInSingleLineComment = false;
    boolean isInMultiLineComment = false;
    char startQuote = 0;
    boolean lastCharWasEscapeChar = false;
    boolean isTripleQuoted = false;
    StringBuilder res = new StringBuilder(sql.length());
    int index = 0;
    while (index < sql.length()) {
      char c = sql.charAt(index);
      if (isInQuoted) {
        if ((c == '\n' || c == '\r') && !isTripleQuoted) {
          throw new SQLException("SQL statement contains an unclosed literal: " + sql);
        } else if (c == startQuote) {
          if (lastCharWasEscapeChar) {
            lastCharWasEscapeChar = false;
          } else if (isTripleQuoted) {
            if (sql.length() > index + 2
                && sql.charAt(index + 1) == startQuote
                && sql.charAt(index + 2) == startQuote) {
              isInQuoted = false;
              startQuote = 0;
              isTripleQuoted = false;
              res.append(c).append(c);
              index += 2;
            }
          } else {
            isInQuoted = false;
            startQuote = 0;
          }
        } else if (c == '\\') {
          lastCharWasEscapeChar = true;
        } else {
          lastCharWasEscapeChar = false;
        }
        res.append(c);
      } else {
        // We are not in a quoted string.
        if (isInSingleLineComment) {
          if (c == '\n') {
            isInSingleLineComment = false;
            // Include the line feed in the result.
            res.append(c);
          }
        } else if (isInMultiLineComment) {
          if (sql.length() > index + 1 && c == ASTERIKS && sql.charAt(index + 1) == SLASH) {
            isInMultiLineComment = false;
            index++;
          }
        } else {
          if (c == DASH
              || (sql.length() > index + 1 && c == HYPHEN && sql.charAt(index + 1) == HYPHEN)) {
            // This is a single line comment.
            isInSingleLineComment = true;
          } else if (sql.length() > index + 1 && c == SLASH && sql.charAt(index + 1) == ASTERIKS) {
            isInMultiLineComment = true;
            index++;
          } else {
            if (c == SINGLE_QUOTE || c == DOUBLE_QUOTE || c == BACKTICK_QUOTE) {
              isInQuoted = true;
              startQuote = c;
              // Check whether it is a triple-quote.
              if (sql.length() > index + 2
                  && sql.charAt(index + 1) == startQuote
                  && sql.charAt(index + 2) == startQuote) {
                isTripleQuoted = true;
                res.append(c).append(c);
                index += 2;
              }
            }
            res.append(c);
          }
        }
      }
      index++;
    }
    if (isInQuoted) {
      throw new SQLException("SQL statement contains an unclosed literal: " + sql);
    }
    if (res.length() > 0 && res.charAt(res.length() - 1) == ';') {
      res.deleteCharAt(res.length() - 1);
    }
    return res.toString().trim();
  }

  /**
   * Removes any statement hints at the beginning of the statement.
   *
   * @throws SQLException
   */
  static String removeStatementHint(String sql) throws SQLException {
    // Valid statement hints at the beginning of a query statement can only contain a fixed set of
    // possible values. Although it is possible to add a @{FORCE_INDEX=...} as a statement hint, the
    // only allowed value is _BASE_TABLE. This means that we can safely assume that the statement
    // hint will not contain any special characters, for example a closing curly brace or one of the
    // keywords SELECT, UPDATE, DELETE, WITH, and that we can keep the check simple by just
    // searching for the first occurrence of a keyword that should be preceded by a closing curly
    // brace at the end of the statement hint.
    int startStatementHintIndex = sql.indexOf('{');
    // Statement hints are only allowed for queries.
    int startQueryIndex = -1;
    String upperCaseSql = sql.toUpperCase();
    for (String keyword : selectStatements) {
      startQueryIndex = upperCaseSql.indexOf(keyword);
      if (startQueryIndex > -1) break;
    }
    if (startQueryIndex > -1) {
      int endStatementHintIndex = sql.substring(0, startQueryIndex).lastIndexOf('}');
      if (startStatementHintIndex == -1 || startStatementHintIndex > endStatementHintIndex) {
        // Looks like an invalid statement hint. Just ignore at this point and let the caller handle
        // the invalid query.
        return sql;
      }
      return removeCommentsAndTrim(sql.substring(endStatementHintIndex + 1));
    }
    // Seems invalid, just return the original statement.
    return sql;
  }

  static boolean isDml(String sql) {
    try {
      return INSTANCE.parse(sql).isUpdate();
    } catch (Throwable t) {
      return false;
    }
  }
}
