package de.mccityville.lib.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface PreparedStatementSetter {

    PreparedStatementSetter NO_OP = ps -> {};

    void set(PreparedStatement ps) throws SQLException;
}
