package io.github.factoryfx.factory.datastorage.oracle;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;
import java.util.List;
import java.util.function.Consumer;

public class JdbcUtil {

    public static void writeToBlob(PreparedStatement preparedStatement,
                                   int index,
                                   Consumer<OutputStream> writer,
                                   List<Blob> allocatedBlobs) {
        try {
            Blob blob = preparedStatement.getConnection().createBlob();
            allocatedBlobs.add(blob);
            try (OutputStream out = blob.setBinaryStream(1)) {
                writer.accept(out);
            }
            preparedStatement.setBlob(index, blob);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void freeBlobs(List<Blob> blobs) {
        for (Blob blob : blobs) {
            if (blob != null) {
                try {
                    blob.free();
                } catch (SQLException ignored) {
                }
            }
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
