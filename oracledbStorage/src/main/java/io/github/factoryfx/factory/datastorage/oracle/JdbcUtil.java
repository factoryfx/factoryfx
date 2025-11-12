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

            String sql1 = """
                    SELECT COUNT(*) AS cnt
                    FROM all_tab_columns
                    WHERE table_name = 'USER_LOBS'
                      AND column_name IN ('COMPRESSION', 'SECUREFILE')""";

            String sql2 = """
                    SELECT 1 
                    FROM user_lobs 
                    WHERE table_name = 'FACTORY_HISTORY' and column_name = 'FACTORY' and securefile = 'YES'""";

            try (Statement stmt = connection.createStatement();
                 ResultSet rs = stmt.executeQuery(sql1)) {

                if (rs.next() && rs.getInt("cnt") >= 2) {
                    try (Statement stmt2 = connection.createStatement();
                         ResultSet rs2 = stmt2.executeQuery(sql2)) {
                        return rs2.next();
                    }
                }
            }

        } catch (SQLException ignored) {
        }
        return false;
    }
}
