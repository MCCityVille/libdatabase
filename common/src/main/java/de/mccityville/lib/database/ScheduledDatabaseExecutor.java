package de.mccityville.lib.database;

import io.reactivex.Flowable;
import io.reactivex.Scheduler;
import io.reactivex.Single;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.Objects;

public class ScheduledDatabaseExecutor extends SimpleDatabaseExecutor {

    private final Scheduler scheduler;

    public ScheduledDatabaseExecutor(DataSource dataSource, Scheduler scheduler) {
        super(dataSource);
        this.scheduler = Objects.requireNonNull(scheduler, "scheduler must not be null");
    }

    @Override
    public <T> Single<T> update(String sql, PreparedStatementSetter setter, UpdateResultExtractor<T> extractor) {
        return super.update(sql, setter, extractor)
                .subscribeOn(scheduler);
    }

    @Override
    public Flowable<ResultSet> select(String sql, PreparedStatementSetter setter) {
        return super.select(sql, setter)
                .subscribeOn(scheduler);
    }
}
