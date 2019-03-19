package io.github.factoryfx.factory.datastorage.oracle;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
}
