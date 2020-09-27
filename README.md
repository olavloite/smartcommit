# Smart Commit JDBC Driver

Generic JDBC driver wrapper that can be used with most JDBC drivers to implement 'Smart Commit'.

When should I use it: If your application uses the Open-Session-In-View (anti-)pattern and you want to reduce the number of read locks required by your application. Especially applications that do a significant large amount of read-only transactions compared to read/write transaction can benefit from this.

## What is Smart Commit?

The JDBC standard requires all drivers to implement autocommit and transactional behavior for a connection:
* When a connection is in autocommit, all statements that are executed on the connection are treated as a single small transaction that cannot be rollbacked.
* When a connection is NOT in autocommit, all statements are automatically added to the current transaction, and applied to the database when the client application calls `Connection#commit()` or discarded when it calls `Connection#rollback()`.

Smart Commit combines autocommit and transactional behavior for a connection like this:
* The connection starts by default in autocommit mode and as long as the client application is only executing queries, the connection will remain in autocommit mode.
* When the connection receives a write statement (INSERT/UPDATE/DELETE) it will automatically turn off autocommit mode and start a transaction, and all subsequent statements will be added to the current transaction.
* When a client application calls `Connection#commit()` or `Connection#rollback()`, the current transaction (if any) will be committed/rollbacked and the connection will return to autocommit mode.

Smart Commit mode therefore reduces the amount of (read) locks that must be acquired for a transaction. It however also means that any query that is executed before a write operation in a transaction will effectively be executed outside of this transaction. This is not a problem for applications that use READ_COMMITTED serialization level. If your application is using a higher serialization level (REPEATABLE_READ or SERIALIZABLE), you will loose this serialization level for all reads that happen before the first write.

## When Should I Use It?

The Smart Commit JDBC Driver can reduce the number of read locks that are needed for applications that use the Open-Session-In-View (anti-)pattern. Open-Session-In-View (or Open EntityManager in View) is enabled by default in Spring Boot: https://docs.spring.io/spring-boot/docs/2.3.4.RELEASE/reference/html/spring-boot-features.html#boot-features-jpa-in-web-environment

## How Does it Work?

The Smart Commit JDBC Driver is a simple wrapper around any other JDBC driver, so you can use it with any JDBC driver available. The only things that you need to change are:
1. Add the Smart Commit JDBC Driver as a dependency to your project.
2. Change your current JDBC connection URL so that it includes the `smartcommit:` prefix.

The `smartcommit:` prefix is added to the JDBC connection URL right after the initial `jdbc:` part, and is then followed by the actual connection URL of your actual JDBC connection URL.

Examples:

```
# PostgreSQL
jdbc:smartcommit:postgresql://localhost/foo

# MySQL
jdbc:smartcommit:mysql://localhost:3306/foo

# Google Cloud Spanner
jdbc:smartcommit:cloudspanner:/projects/p/instances/i/databases/d

# Microsoft SQL Server
jdbc:smartcommit:sqlserver://localhost;databaseName=AdventureWorks;integratedSecurity=true;

# Oracle thin JDBC driver
jdbc:smartcommit:oracle:thin:@myhost:1521:orcl

# Oracle OCI JDBC driver
jdbc:smartcommit:oracle:oci8:scott/tiger@myhost

```