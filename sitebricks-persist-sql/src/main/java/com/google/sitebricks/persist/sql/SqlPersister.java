package com.google.sitebricks.persist.sql;

import com.google.sitebricks.persist.EntityStore;
import com.google.sitebricks.persist.Persister;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

import java.sql.SQLException;

/**
 * @author dhanji@gmail.com (Dhanji R. Prasanna)
 */
class SqlPersister extends Persister {
  private final BoneCPConfig config;
  private BoneCP pool;

  public SqlPersister(BoneCPConfig config) {
    this.config = config;
  }

  @Override
  public synchronized void start() {
    try {
      pool = new BoneCP(config);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public synchronized void shutdown() {
    pool.shutdown();
  }

  @Override
  protected EntityStore beginWork() {
    return new SqlEntityStore(pool);
  }

  @Override
  protected void endWork(EntityStore store, boolean commit) {
    try {
      if (commit)
        ((Sql) store.delegate()).connection().commit();
      else
        ((Sql) store.delegate()).connection().rollback();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected EntityStore.EntityTransaction beginTransaction() {
    return new EntityStore.EntityTransaction() {
      @Override
      public void commit() {
//        entityStore.complete(true);
      }

      @Override
      public void rollback() {
//        entityStore.complete(false);
      }
    };
  }
}
