# Smart Commit JDBC Driver

Generic JDBC driver wrapper that can be used with most JDBC drivers to implement 'Smart Commit'.

When should I use it: If your application uses the Open-Session-In-View (anti-)pattern and you want to reduce the number of read locks required by your application. Especially applications that do a significant large amount of read-only transactions compared to read/write transaction can benefit from this.

See [the samples project](https://github.com/olavloite/smartcommit-samples) for samples on how to use this driver with the database of your choice.

## Usage

Add the following dependency to your project:

```xml
<dependency>
  <groupId>com.github.olavloite</groupId>
  <artifactId>smartcommit-jdbc</artifactId>
  <version>1.0</version>
</dependency>
```

Adjust your JDBC connection URL and driver class name to use the Smart Commit driver as a wrapper around your underlying database. The following example is for a PostgreSQL database:

```
url=jdbc:smartcommit:postgresql://localhost/my-database
driver-class-name=com.github.jdbc.smartcommit.SmartCommitDriver
```

See the [Smart Commit JDBC Driver Samples](https://github.com/olavloite/smartcommit-samples) for more examples on how to use this driver.

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

Smart Commit can be used when:
* Your application does not need a higher isolation level than READ_COMMITTED. READ_COMMITTED is the default isolation in many databases, such as PostgreSQL, Microsoft SQL Server, and Oracle.
* Your application does need write operations to be executed in transactions.

## What are the Benefits?

* (Web) Applications that execute requests using generic business logic often do not know whether a request in the end will execute any write operations, or if it will only data from the database. Smart Commit ensures that read-only requests are executed in autocommit and will therefore not hold on to any read locks any longer than absolutely necessary.
* (Web) Applications that execute transactions that execute mainly read operations at the beginning of a transaction and write operations at the end, will require a lot less (read) locks.
* Transactions are less likely to deadlock and/or to be aborted, as the application will require both less transactions and shorter transactions. This effect is especially noticeable in cloud databases such as Google Cloud Spanner.
* PostgreSQL specific: If you use PostgreSQL in combination with PgBouncer in [Transaction pooling mode](https://www.pgbouncer.org/features.html), you will be able to achieve a much higher reuse of the backend PostgreSQL connections, as there will be a lot less (long-running) transactions.

## What are the Drawbacks?

* Read operations that are executed before write operations in a transaction, are executed outside of the transaction. That effectively means that those read operations will always have transaction isolation level READ_COMMITTED, regardless of the isolation level that has been set for the JDBC connection.

Note that many relational databases already use READ_COMMITTED as the default isolation level, which means that unless you have specifically chosen a different isolation level, the isolation level of those read operations will still be the same as without the Smart Commit driver. The following databases use READ_COMMITTED by default (at the time of writing):
* PostgreSQL
* Microsoft SQL Server
* Oracle

Note that MySQL uses REPEATABLE_READ its default isolation level.

## How Does it Work?

The Smart Commit JDBC Driver is a simple wrapper around any other JDBC driver, so you can use it with any JDBC driver available. The only things that you need to change are:
1. Add the Smart Commit JDBC Driver as a dependency to your project.
2. Change your current JDBC connection URL so that it includes the `smartcommit:` prefix.

The `smartcommit:` prefix is added to the JDBC connection URL right after the initial `jdbc:` part, and is then followed by the actual connection URL of your actual JDBC connection URL.


## Examples

The below list of examples is not intended to be exhaustive. The Smart Commit JDBC Driver can be used in combination with any JDBC driver.

This driver has no affiliation with any of the example drivers or their vendors named in this document. Any support questions regarding the Smart Commit JDBC Driver should be filed as an issue with this repository, and not with the JDBC driver that is being used in combination with the Smart Commit JDBC driver.


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

# H2
jdbc:smartcommit:h2:mem:test

```