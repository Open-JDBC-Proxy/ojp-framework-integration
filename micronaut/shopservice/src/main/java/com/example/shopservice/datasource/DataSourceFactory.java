package com.example.shopservice.datasource;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Factory
public class DataSourceFactory {
    @Singleton
    @Named("default")
    public DataSource dataSource(
            @Value("${datasources.default.url}") String url,
            @Value("${datasources.default.username}") String user,
            @Value("${datasources.default.password}") String password,
            @Value("${datasources.default.driver-class-name}") String driver
    ) throws ClassNotFoundException {
        Class.forName(driver);//Guarantees that the OJP driver is registered with the DriverManager.

        DataSource ds = new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return DriverManager.getConnection(url, user, password);
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return DriverManager.getConnection(url, username, password);
            }

            // The following methods can be left as default or throw UnsupportedOperationException
            @Override
            public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }
            @Override
            public boolean isWrapperFor(Class<?> iface) { throw new UnsupportedOperationException(); }
            @Override
            public java.io.PrintWriter getLogWriter() { throw new UnsupportedOperationException(); }
            @Override
            public void setLogWriter(java.io.PrintWriter out) { throw new UnsupportedOperationException(); }
            @Override
            public void setLoginTimeout(int seconds) { throw new UnsupportedOperationException(); }
            @Override
            public int getLoginTimeout() { throw new UnsupportedOperationException(); }
            @Override
            public java.util.logging.Logger getParentLogger() { throw new UnsupportedOperationException(); }
        };

        return ds;
    }
}
