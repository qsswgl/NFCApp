package com.nfc.app.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenHelper;
import androidx.room.RoomOpenHelper.Delegate;
import androidx.room.RoomOpenHelper.ValidationResult;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.room.util.TableInfo.Column;
import androidx.room.util.TableInfo.ForeignKey;
import androidx.room.util.TableInfo.Index;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Callback;
import androidx.sqlite.db.SupportSQLiteOpenHelper.Configuration;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NFCDatabase_Impl extends NFCDatabase {
  private volatile NFCRecordDao _nFCRecordDao;

  @Override
  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `nfc_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `nfcId` TEXT NOT NULL, `cardNumber` TEXT NOT NULL, `carNumber` TEXT NOT NULL, `unitName` TEXT NOT NULL, `deviceName` TEXT NOT NULL, `amount` TEXT NOT NULL, `readTime` INTEGER NOT NULL, `content` TEXT NOT NULL, `uploadStatus` INTEGER NOT NULL, `uploadTime` INTEGER NOT NULL, `uploadSuccess` INTEGER NOT NULL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '6fb6a72764d60506145ce117d7db3bca')");
      }

      @Override
      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `nfc_records`");
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onDestructiveMigration(_db);
          }
        }
      }

      @Override
      public void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      @Override
      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      @Override
      public void onPreMigrate(SupportSQLiteDatabase _db) {
        DBUtil.dropFtsSyncTriggers(_db);
      }

      @Override
      public void onPostMigrate(SupportSQLiteDatabase _db) {
      }

      @Override
      public RoomOpenHelper.ValidationResult onValidateSchema(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsNfcRecords = new HashMap<String, TableInfo.Column>(12);
        _columnsNfcRecords.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("nfcId", new TableInfo.Column("nfcId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("cardNumber", new TableInfo.Column("cardNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("carNumber", new TableInfo.Column("carNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("unitName", new TableInfo.Column("unitName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("deviceName", new TableInfo.Column("deviceName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("amount", new TableInfo.Column("amount", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("readTime", new TableInfo.Column("readTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("content", new TableInfo.Column("content", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("uploadStatus", new TableInfo.Column("uploadStatus", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("uploadTime", new TableInfo.Column("uploadTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsNfcRecords.put("uploadSuccess", new TableInfo.Column("uploadSuccess", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysNfcRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesNfcRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoNfcRecords = new TableInfo("nfc_records", _columnsNfcRecords, _foreignKeysNfcRecords, _indicesNfcRecords);
        final TableInfo _existingNfcRecords = TableInfo.read(_db, "nfc_records");
        if (! _infoNfcRecords.equals(_existingNfcRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "nfc_records(com.nfc.app.database.NFCRecord).\n"
                  + " Expected:\n" + _infoNfcRecords + "\n"
                  + " Found:\n" + _existingNfcRecords);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "6fb6a72764d60506145ce117d7db3bca", "6bc33fbf1fc5e5d7474592d53e1ee6fb");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "nfc_records");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `nfc_records`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(NFCRecordDao.class, NFCRecordDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  public List<Migration> getAutoMigrations(
      @NonNull Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecsMap) {
    return Arrays.asList();
  }

  @Override
  public NFCRecordDao nfcRecordDao() {
    if (_nFCRecordDao != null) {
      return _nFCRecordDao;
    } else {
      synchronized(this) {
        if(_nFCRecordDao == null) {
          _nFCRecordDao = new NFCRecordDao_Impl(this);
        }
        return _nFCRecordDao;
      }
    }
  }
}
