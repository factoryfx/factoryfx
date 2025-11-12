package io.github.factoryfx.factory.datastorage.oracle;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class JdbcUtil {
    public static void writeStringToBlob(String value, PreparedStatement preparedStatement, int index){
        try {
            final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            preparedStatement.setBinaryStream(index, new ByteArrayInputStream(bytes), bytes.length);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readStringFromBlob(ResultSet resultSet, String columnLabel){
        try {
            Blob factoryBlob  = resultSet.getBlob(columnLabel);
            return new String(factoryBlob.getBytes(1L, (int) factoryBlob.length()), StandardCharsets.UTF_8);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isOracleWithCompressionSupport(Connection connection) {
        try {
            if (!connection.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle")) {
                return false;
            }

            String sql = "SELECT 1 FROM all_tab_columns WHERE table_name = 'USER_LOBS' AND column_name = 'COMPRESSION'";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                return rs.next();
            }

        } catch (SQLException e) {
            return false;
        }
    }
}
