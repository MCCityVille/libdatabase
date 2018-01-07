package de.mccityville.lib.database;

import de.mccityville.lib.database.DatabaseExecutor;
import de.mccityville.lib.database.ScheduledDatabaseExecutor;
import io.reactivex.schedulers.Schedulers;
import org.h2.jdbcx.JdbcDataSource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class ScheduledDatabaseExecutorTest {

    private static DatabaseExecutor executor;

    @BeforeClass
    public static void prepare() throws Throwable {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        executor = new ScheduledDatabaseExecutor(dataSource, Schedulers.io());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("CREATE TABLE `test`(`key` VARCHAR, `value` VARCHAR)")) {
            ps.executeUpdate();
        }
    }

    @Test
    public void test() throws Throwable {
        int count = executor.update("INSERT INTO `test`(`key`, `value`) VALUES ('test', 'hello world')").blockingGet();
        Assert.assertEquals(1, count);
        String str = executor.selectRows("SELECT `value` FROM `test` WHERE `key` = ?", ps -> ps.setString(1, "test"))
                .map(rs -> rs.getString("value"))
                .blockingFirst();
        Assert.assertEquals("hello world", str);
    }
}
