package de.mccityville.lib.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface UpdateResultExtractor<T> {

    UpdateResultExtractor<Integer> AFFECTED_ROWS_INSTANCE = (affectedRows, ps) -> affectedRows;

    T extract(int affectedRows, PreparedStatement ps) throws SQLException;
}
