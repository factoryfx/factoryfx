package io.github.factoryfx.factory.datastorage.oracle;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.jackson.OutputStyle;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

public class JdbcUtil {
    public static void writeObjectToBlob(PreparedStatement preparedStatement,
                                         int index,
                                         SimpleObjectMapper objectMapper,
                                         Object value,
                                         OutputStyle outputStyle) {
        try {
            Blob blob = preparedStatement.getConnection().createBlob();
            try (OutputStream out = blob.setBinaryStream(1)) {
                objectMapper.writeValue(out, value, outputStyle);
            }
            preparedStatement.setBlob(index, blob);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonNode readTreeFromBlob(ResultSet resultSet, String columnLabel, SimpleObjectMapper objectMapper) {
        try (InputStream blobStream = resultSet.getBinaryStream(columnLabel)) {
            return objectMapper.readTree(blobStream);
        } catch (SQLException | IOException e) {
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
