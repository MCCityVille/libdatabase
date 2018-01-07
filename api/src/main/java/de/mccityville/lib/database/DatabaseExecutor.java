package de.mccityville.lib.database;

import io.reactivex.Flowable;
import io.reactivex.Single;
import org.reactivestreams.Publisher;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseExecutor {

    DataSource getDataSource();

    default Single<Integer> update(String sql) {
        return update(sql, UpdateResultExtractor.AFFECTED_ROWS_INSTANCE);
    }

    default Single<Integer> update(String sql, PreparedStatementSetter setter) {
        return update(sql, setter, UpdateResultExtractor.AFFECTED_ROWS_INSTANCE);
    }

    default <T> Single<T> update(String sql, UpdateResultExtractor<T> extractor) {
        return update(sql, PreparedStatementSetter.NO_OP, extractor);
    }

    <T> Single<T> update(String sql, PreparedStatementSetter setter, UpdateResultExtractor<T> extractor);

    default <T> Flowable<ResultSet> select(String sql) {
        return select(sql, PreparedStatementSetter.NO_OP);
    }

    Flowable<ResultSet> select(String sql, PreparedStatementSetter setter);

    default Flowable<ResultSet> selectRows(String sql) {
        return selectRows(sql, PreparedStatementSetter.NO_OP);
    }

    default Flowable<ResultSet> selectRows(String sql, PreparedStatementSetter setter) {
        return select(sql, setter)
                .flatMap(rs -> ((Publisher<ResultSet>) subscriber -> {
                    try {
                        while (rs.next())
                            subscriber.onNext(rs);
                    } catch (SQLException e) {
                        subscriber.onError(e);
                    } finally {
                        subscriber.onComplete();
                    }
                }));
    }
}
