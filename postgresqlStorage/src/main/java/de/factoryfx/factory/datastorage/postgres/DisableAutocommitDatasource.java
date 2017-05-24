package de.factoryfx.factory.datastorage.postgres;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

public class DisableAutocommitDatasource implements DataSource {

    public DisableAutocommitDatasource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = dataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    public Connection getConnection(String username, String password) throws SQLException {
        Connection connection = dataSource.getConnection(username, password);
        connection.setAutoCommit(false);
        return connection;
    }

    public PrintWriter getLogWriter() throws SQLException {
        return dataSource.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        dataSource.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        dataSource.setLoginTimeout(seconds);
    }

    public int getLoginTimeout() throws SQLException {
        return dataSource.getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return dataSource.getParentLogger();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        T unwrapped = null;
        if (iface.isAssignableFrom(dataSource.getClass())) {
            unwrapped = (T)dataSource;
            if (dataSource.isWrapperFor(iface))
                unwrapped = (T)dataSource.unwrap(iface);
        } else {
            unwrapped = dataSource.unwrap(iface);
        }
        return unwrapped;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(dataSource.getClass()) || dataSource.isWrapperFor(iface);
    }

    private final DataSource dataSource;

}
