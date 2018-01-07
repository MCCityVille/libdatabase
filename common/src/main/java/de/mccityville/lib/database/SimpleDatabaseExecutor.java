package de.mccityville.lib.database;

import de.mccityville.lib.database.DatabaseExecutor;
import de.mccityville.lib.database.PreparedStatementSetter;
import de.mccityville.lib.database.UpdateResultExtractor;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class SimpleDatabaseExecutor implements DatabaseExecutor {

    @NonNull private final DataSource dataSource;

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public <T> Single<T> update(String sql, PreparedStatementSetter setter, UpdateResultExtractor<T> extractor) {
        return Single.fromCallable(() -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                setter.set(ps);
                int updated = ps.executeUpdate();
                return extractor.extract(updated, ps);
            }
        });
    }

    @Override
    public Flowable<ResultSet> select(String sql, PreparedStatementSetter setter) {
        return Flowable.create(emitter -> {
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(sql)) {
                setter.set(ps);
                try (ResultSet rs = ps.executeQuery()) {
                    emitter.onNext(rs);
                }
            } catch (SQLException e) {
                emitter.onError(e);
            } finally {
                emitter.onComplete();
            }
        }, BackpressureStrategy.BUFFER);
    }
}
