package de.factoryfx.factory.datastorage.oracle;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JdbcUtil {
    public static void writeStringToBlob(String value, PreparedStatement preparedStatement, int index){
        try {
            preparedStatement.setBinaryStream(index, new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)), value.length());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
