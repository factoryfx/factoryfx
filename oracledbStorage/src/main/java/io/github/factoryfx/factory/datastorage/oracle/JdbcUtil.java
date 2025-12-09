package io.github.factoryfx.factory.datastorage.oracle;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.*;

public class JdbcUtil {
    public static void writeStringToBlob(String value, PreparedStatement preparedStatement, int index) {
        try {
            final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            preparedStatement.setBinaryStream(index, new ByteArrayInputStream(bytes), bytes.length);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readStringFromBlob(ResultSet resultSet, String columnLabel) {
        try {
            Blob factoryBlob = resultSet.getBlob(columnLabel);
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

            String sql = """
                    SELECT COUNT(*) AS cnt
                    FROM all_tab_columns
                    WHERE table_name = 'USER_LOBS'
                      AND column_name IN ('COMPRESSION', 'SECUREFILE')""";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                return rs.next() && rs.getInt("cnt") >= 2;
            }
        } catch (SQLException e) {
            return false;
        }
    }
}
