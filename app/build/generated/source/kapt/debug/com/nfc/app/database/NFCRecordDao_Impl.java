package com.nfc.app.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class NFCRecordDao_Impl implements NFCRecordDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<NFCRecord> __insertionAdapterOfNFCRecord;

  private final EntityDeletionOrUpdateAdapter<NFCRecord> __deletionAdapterOfNFCRecord;

  private final EntityDeletionOrUpdateAdapter<NFCRecord> __updateAdapterOfNFCRecord;

  private final SharedSQLiteStatement __preparedStmtOfDeleteUploadedRecords;

  public NFCRecordDao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfNFCRecord = new EntityInsertionAdapter<NFCRecord>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR ABORT INTO `nfc_records` (`id`,`nfcId`,`cardNumber`,`carNumber`,`unitName`,`deviceName`,`amount`,`readTime`,`content`,`uploadStatus`,`uploadTime`,`uploadSuccess`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NFCRecord value) {
        stmt.bindLong(1, value.getId());
        if (value.getNfcId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getNfcId());
        }
        if (value.getCardNumber() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getCardNumber());
        }
        if (value.getCarNumber() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getCarNumber());
        }
        if (value.getUnitName() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUnitName());
        }
        if (value.getDeviceName() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getDeviceName());
        }
        if (value.getAmount() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getAmount());
        }
        stmt.bindLong(8, value.getReadTime());
        if (value.getContent() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getContent());
        }
        final int _tmp = value.getUploadStatus() ? 1 : 0;
        stmt.bindLong(10, _tmp);
        stmt.bindLong(11, value.getUploadTime());
        final int _tmp_1 = value.getUploadSuccess() ? 1 : 0;
        stmt.bindLong(12, _tmp_1);
      }
    };
    this.__deletionAdapterOfNFCRecord = new EntityDeletionOrUpdateAdapter<NFCRecord>(__db) {
      @Override
      public String createQuery() {
        return "DELETE FROM `nfc_records` WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NFCRecord value) {
        stmt.bindLong(1, value.getId());
      }
    };
    this.__updateAdapterOfNFCRecord = new EntityDeletionOrUpdateAdapter<NFCRecord>(__db) {
      @Override
      public String createQuery() {
        return "UPDATE OR ABORT `nfc_records` SET `id` = ?,`nfcId` = ?,`cardNumber` = ?,`carNumber` = ?,`unitName` = ?,`deviceName` = ?,`amount` = ?,`readTime` = ?,`content` = ?,`uploadStatus` = ?,`uploadTime` = ?,`uploadSuccess` = ? WHERE `id` = ?";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, NFCRecord value) {
        stmt.bindLong(1, value.getId());
        if (value.getNfcId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getNfcId());
        }
        if (value.getCardNumber() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getCardNumber());
        }
        if (value.getCarNumber() == null) {
          stmt.bindNull(4);
        } else {
          stmt.bindString(4, value.getCarNumber());
        }
        if (value.getUnitName() == null) {
          stmt.bindNull(5);
        } else {
          stmt.bindString(5, value.getUnitName());
        }
        if (value.getDeviceName() == null) {
          stmt.bindNull(6);
        } else {
          stmt.bindString(6, value.getDeviceName());
        }
        if (value.getAmount() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getAmount());
        }
        stmt.bindLong(8, value.getReadTime());
        if (value.getContent() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getContent());
        }
        final int _tmp = value.getUploadStatus() ? 1 : 0;
        stmt.bindLong(10, _tmp);
        stmt.bindLong(11, value.getUploadTime());
        final int _tmp_1 = value.getUploadSuccess() ? 1 : 0;
        stmt.bindLong(12, _tmp_1);
        stmt.bindLong(13, value.getId());
      }
    };
    this.__preparedStmtOfDeleteUploadedRecords = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM nfc_records WHERE uploadStatus = 1";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final NFCRecord record, final Continuation<? super Long> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          long _result = __insertionAdapterOfNFCRecord.insertAndReturnId(record);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object delete(final NFCRecord record, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfNFCRecord.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object update(final NFCRecord record, final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfNFCRecord.handle(record);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteUploadedRecords(final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteUploadedRecords.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteUploadedRecords.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object getAllRecords(final Continuation<? super List<NFCRecord>> continuation) {
    final String _sql = "SELECT * FROM nfc_records ORDER BY readTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NFCRecord>>() {
      @Override
      public List<NFCRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNfcId = CursorUtil.getColumnIndexOrThrow(_cursor, "nfcId");
          final int _cursorIndexOfCardNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cardNumber");
          final int _cursorIndexOfCarNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "carNumber");
          final int _cursorIndexOfUnitName = CursorUtil.getColumnIndexOrThrow(_cursor, "unitName");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfReadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "readTime");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadStatus");
          final int _cursorIndexOfUploadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadTime");
          final int _cursorIndexOfUploadSuccess = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadSuccess");
          final List<NFCRecord> _result = new ArrayList<NFCRecord>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final NFCRecord _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNfcId;
            if (_cursor.isNull(_cursorIndexOfNfcId)) {
              _tmpNfcId = null;
            } else {
              _tmpNfcId = _cursor.getString(_cursorIndexOfNfcId);
            }
            final String _tmpCardNumber;
            if (_cursor.isNull(_cursorIndexOfCardNumber)) {
              _tmpCardNumber = null;
            } else {
              _tmpCardNumber = _cursor.getString(_cursorIndexOfCardNumber);
            }
            final String _tmpCarNumber;
            if (_cursor.isNull(_cursorIndexOfCarNumber)) {
              _tmpCarNumber = null;
            } else {
              _tmpCarNumber = _cursor.getString(_cursorIndexOfCarNumber);
            }
            final String _tmpUnitName;
            if (_cursor.isNull(_cursorIndexOfUnitName)) {
              _tmpUnitName = null;
            } else {
              _tmpUnitName = _cursor.getString(_cursorIndexOfUnitName);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final String _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getString(_cursorIndexOfAmount);
            }
            final long _tmpReadTime;
            _tmpReadTime = _cursor.getLong(_cursorIndexOfReadTime);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final boolean _tmpUploadStatus;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUploadStatus);
            _tmpUploadStatus = _tmp != 0;
            final long _tmpUploadTime;
            _tmpUploadTime = _cursor.getLong(_cursorIndexOfUploadTime);
            final boolean _tmpUploadSuccess;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfUploadSuccess);
            _tmpUploadSuccess = _tmp_1 != 0;
            _item = new NFCRecord(_tmpId,_tmpNfcId,_tmpCardNumber,_tmpCarNumber,_tmpUnitName,_tmpDeviceName,_tmpAmount,_tmpReadTime,_tmpContent,_tmpUploadStatus,_tmpUploadTime,_tmpUploadSuccess);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getRecordById(final int id, final Continuation<? super NFCRecord> continuation) {
    final String _sql = "SELECT * FROM nfc_records WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NFCRecord>() {
      @Override
      public NFCRecord call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNfcId = CursorUtil.getColumnIndexOrThrow(_cursor, "nfcId");
          final int _cursorIndexOfCardNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cardNumber");
          final int _cursorIndexOfCarNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "carNumber");
          final int _cursorIndexOfUnitName = CursorUtil.getColumnIndexOrThrow(_cursor, "unitName");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfReadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "readTime");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadStatus");
          final int _cursorIndexOfUploadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadTime");
          final int _cursorIndexOfUploadSuccess = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadSuccess");
          final NFCRecord _result;
          if(_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNfcId;
            if (_cursor.isNull(_cursorIndexOfNfcId)) {
              _tmpNfcId = null;
            } else {
              _tmpNfcId = _cursor.getString(_cursorIndexOfNfcId);
            }
            final String _tmpCardNumber;
            if (_cursor.isNull(_cursorIndexOfCardNumber)) {
              _tmpCardNumber = null;
            } else {
              _tmpCardNumber = _cursor.getString(_cursorIndexOfCardNumber);
            }
            final String _tmpCarNumber;
            if (_cursor.isNull(_cursorIndexOfCarNumber)) {
              _tmpCarNumber = null;
            } else {
              _tmpCarNumber = _cursor.getString(_cursorIndexOfCarNumber);
            }
            final String _tmpUnitName;
            if (_cursor.isNull(_cursorIndexOfUnitName)) {
              _tmpUnitName = null;
            } else {
              _tmpUnitName = _cursor.getString(_cursorIndexOfUnitName);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final String _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getString(_cursorIndexOfAmount);
            }
            final long _tmpReadTime;
            _tmpReadTime = _cursor.getLong(_cursorIndexOfReadTime);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final boolean _tmpUploadStatus;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUploadStatus);
            _tmpUploadStatus = _tmp != 0;
            final long _tmpUploadTime;
            _tmpUploadTime = _cursor.getLong(_cursorIndexOfUploadTime);
            final boolean _tmpUploadSuccess;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfUploadSuccess);
            _tmpUploadSuccess = _tmp_1 != 0;
            _result = new NFCRecord(_tmpId,_tmpNfcId,_tmpCardNumber,_tmpCarNumber,_tmpUnitName,_tmpDeviceName,_tmpAmount,_tmpReadTime,_tmpContent,_tmpUploadStatus,_tmpUploadTime,_tmpUploadSuccess);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getRecordByNFCId(final String nfcId,
      final Continuation<? super List<NFCRecord>> continuation) {
    final String _sql = "SELECT * FROM nfc_records WHERE nfcId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (nfcId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, nfcId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NFCRecord>>() {
      @Override
      public List<NFCRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNfcId = CursorUtil.getColumnIndexOrThrow(_cursor, "nfcId");
          final int _cursorIndexOfCardNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cardNumber");
          final int _cursorIndexOfCarNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "carNumber");
          final int _cursorIndexOfUnitName = CursorUtil.getColumnIndexOrThrow(_cursor, "unitName");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfReadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "readTime");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadStatus");
          final int _cursorIndexOfUploadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadTime");
          final int _cursorIndexOfUploadSuccess = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadSuccess");
          final List<NFCRecord> _result = new ArrayList<NFCRecord>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final NFCRecord _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNfcId;
            if (_cursor.isNull(_cursorIndexOfNfcId)) {
              _tmpNfcId = null;
            } else {
              _tmpNfcId = _cursor.getString(_cursorIndexOfNfcId);
            }
            final String _tmpCardNumber;
            if (_cursor.isNull(_cursorIndexOfCardNumber)) {
              _tmpCardNumber = null;
            } else {
              _tmpCardNumber = _cursor.getString(_cursorIndexOfCardNumber);
            }
            final String _tmpCarNumber;
            if (_cursor.isNull(_cursorIndexOfCarNumber)) {
              _tmpCarNumber = null;
            } else {
              _tmpCarNumber = _cursor.getString(_cursorIndexOfCarNumber);
            }
            final String _tmpUnitName;
            if (_cursor.isNull(_cursorIndexOfUnitName)) {
              _tmpUnitName = null;
            } else {
              _tmpUnitName = _cursor.getString(_cursorIndexOfUnitName);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final String _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getString(_cursorIndexOfAmount);
            }
            final long _tmpReadTime;
            _tmpReadTime = _cursor.getLong(_cursorIndexOfReadTime);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final boolean _tmpUploadStatus;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUploadStatus);
            _tmpUploadStatus = _tmp != 0;
            final long _tmpUploadTime;
            _tmpUploadTime = _cursor.getLong(_cursorIndexOfUploadTime);
            final boolean _tmpUploadSuccess;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfUploadSuccess);
            _tmpUploadSuccess = _tmp_1 != 0;
            _item = new NFCRecord(_tmpId,_tmpNfcId,_tmpCardNumber,_tmpCarNumber,_tmpUnitName,_tmpDeviceName,_tmpAmount,_tmpReadTime,_tmpContent,_tmpUploadStatus,_tmpUploadTime,_tmpUploadSuccess);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getLastRecordByCardNumber(final String cardNumber,
      final Continuation<? super NFCRecord> continuation) {
    final String _sql = "SELECT * FROM nfc_records WHERE cardNumber = ? ORDER BY readTime DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (cardNumber == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, cardNumber);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<NFCRecord>() {
      @Override
      public NFCRecord call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNfcId = CursorUtil.getColumnIndexOrThrow(_cursor, "nfcId");
          final int _cursorIndexOfCardNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cardNumber");
          final int _cursorIndexOfCarNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "carNumber");
          final int _cursorIndexOfUnitName = CursorUtil.getColumnIndexOrThrow(_cursor, "unitName");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfReadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "readTime");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadStatus");
          final int _cursorIndexOfUploadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadTime");
          final int _cursorIndexOfUploadSuccess = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadSuccess");
          final NFCRecord _result;
          if(_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNfcId;
            if (_cursor.isNull(_cursorIndexOfNfcId)) {
              _tmpNfcId = null;
            } else {
              _tmpNfcId = _cursor.getString(_cursorIndexOfNfcId);
            }
            final String _tmpCardNumber;
            if (_cursor.isNull(_cursorIndexOfCardNumber)) {
              _tmpCardNumber = null;
            } else {
              _tmpCardNumber = _cursor.getString(_cursorIndexOfCardNumber);
            }
            final String _tmpCarNumber;
            if (_cursor.isNull(_cursorIndexOfCarNumber)) {
              _tmpCarNumber = null;
            } else {
              _tmpCarNumber = _cursor.getString(_cursorIndexOfCarNumber);
            }
            final String _tmpUnitName;
            if (_cursor.isNull(_cursorIndexOfUnitName)) {
              _tmpUnitName = null;
            } else {
              _tmpUnitName = _cursor.getString(_cursorIndexOfUnitName);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final String _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getString(_cursorIndexOfAmount);
            }
            final long _tmpReadTime;
            _tmpReadTime = _cursor.getLong(_cursorIndexOfReadTime);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final boolean _tmpUploadStatus;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUploadStatus);
            _tmpUploadStatus = _tmp != 0;
            final long _tmpUploadTime;
            _tmpUploadTime = _cursor.getLong(_cursorIndexOfUploadTime);
            final boolean _tmpUploadSuccess;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfUploadSuccess);
            _tmpUploadSuccess = _tmp_1 != 0;
            _result = new NFCRecord(_tmpId,_tmpNfcId,_tmpCardNumber,_tmpCarNumber,_tmpUnitName,_tmpDeviceName,_tmpAmount,_tmpReadTime,_tmpContent,_tmpUploadStatus,_tmpUploadTime,_tmpUploadSuccess);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getRecordsByCardNumber(final String cardNumber,
      final Continuation<? super List<NFCRecord>> continuation) {
    final String _sql = "SELECT * FROM nfc_records WHERE cardNumber = ? ORDER BY readTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (cardNumber == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, cardNumber);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NFCRecord>>() {
      @Override
      public List<NFCRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNfcId = CursorUtil.getColumnIndexOrThrow(_cursor, "nfcId");
          final int _cursorIndexOfCardNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cardNumber");
          final int _cursorIndexOfCarNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "carNumber");
          final int _cursorIndexOfUnitName = CursorUtil.getColumnIndexOrThrow(_cursor, "unitName");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfReadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "readTime");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadStatus");
          final int _cursorIndexOfUploadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadTime");
          final int _cursorIndexOfUploadSuccess = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadSuccess");
          final List<NFCRecord> _result = new ArrayList<NFCRecord>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final NFCRecord _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNfcId;
            if (_cursor.isNull(_cursorIndexOfNfcId)) {
              _tmpNfcId = null;
            } else {
              _tmpNfcId = _cursor.getString(_cursorIndexOfNfcId);
            }
            final String _tmpCardNumber;
            if (_cursor.isNull(_cursorIndexOfCardNumber)) {
              _tmpCardNumber = null;
            } else {
              _tmpCardNumber = _cursor.getString(_cursorIndexOfCardNumber);
            }
            final String _tmpCarNumber;
            if (_cursor.isNull(_cursorIndexOfCarNumber)) {
              _tmpCarNumber = null;
            } else {
              _tmpCarNumber = _cursor.getString(_cursorIndexOfCarNumber);
            }
            final String _tmpUnitName;
            if (_cursor.isNull(_cursorIndexOfUnitName)) {
              _tmpUnitName = null;
            } else {
              _tmpUnitName = _cursor.getString(_cursorIndexOfUnitName);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final String _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getString(_cursorIndexOfAmount);
            }
            final long _tmpReadTime;
            _tmpReadTime = _cursor.getLong(_cursorIndexOfReadTime);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final boolean _tmpUploadStatus;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUploadStatus);
            _tmpUploadStatus = _tmp != 0;
            final long _tmpUploadTime;
            _tmpUploadTime = _cursor.getLong(_cursorIndexOfUploadTime);
            final boolean _tmpUploadSuccess;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfUploadSuccess);
            _tmpUploadSuccess = _tmp_1 != 0;
            _item = new NFCRecord(_tmpId,_tmpNfcId,_tmpCardNumber,_tmpCarNumber,_tmpUnitName,_tmpDeviceName,_tmpAmount,_tmpReadTime,_tmpContent,_tmpUploadStatus,_tmpUploadTime,_tmpUploadSuccess);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getUnuploadedRecords(final Continuation<? super List<NFCRecord>> continuation) {
    final String _sql = "SELECT * FROM nfc_records WHERE uploadStatus = 0 ORDER BY readTime ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<NFCRecord>>() {
      @Override
      public List<NFCRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfNfcId = CursorUtil.getColumnIndexOrThrow(_cursor, "nfcId");
          final int _cursorIndexOfCardNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "cardNumber");
          final int _cursorIndexOfCarNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "carNumber");
          final int _cursorIndexOfUnitName = CursorUtil.getColumnIndexOrThrow(_cursor, "unitName");
          final int _cursorIndexOfDeviceName = CursorUtil.getColumnIndexOrThrow(_cursor, "deviceName");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfReadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "readTime");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadStatus");
          final int _cursorIndexOfUploadTime = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadTime");
          final int _cursorIndexOfUploadSuccess = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadSuccess");
          final List<NFCRecord> _result = new ArrayList<NFCRecord>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final NFCRecord _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpNfcId;
            if (_cursor.isNull(_cursorIndexOfNfcId)) {
              _tmpNfcId = null;
            } else {
              _tmpNfcId = _cursor.getString(_cursorIndexOfNfcId);
            }
            final String _tmpCardNumber;
            if (_cursor.isNull(_cursorIndexOfCardNumber)) {
              _tmpCardNumber = null;
            } else {
              _tmpCardNumber = _cursor.getString(_cursorIndexOfCardNumber);
            }
            final String _tmpCarNumber;
            if (_cursor.isNull(_cursorIndexOfCarNumber)) {
              _tmpCarNumber = null;
            } else {
              _tmpCarNumber = _cursor.getString(_cursorIndexOfCarNumber);
            }
            final String _tmpUnitName;
            if (_cursor.isNull(_cursorIndexOfUnitName)) {
              _tmpUnitName = null;
            } else {
              _tmpUnitName = _cursor.getString(_cursorIndexOfUnitName);
            }
            final String _tmpDeviceName;
            if (_cursor.isNull(_cursorIndexOfDeviceName)) {
              _tmpDeviceName = null;
            } else {
              _tmpDeviceName = _cursor.getString(_cursorIndexOfDeviceName);
            }
            final String _tmpAmount;
            if (_cursor.isNull(_cursorIndexOfAmount)) {
              _tmpAmount = null;
            } else {
              _tmpAmount = _cursor.getString(_cursorIndexOfAmount);
            }
            final long _tmpReadTime;
            _tmpReadTime = _cursor.getLong(_cursorIndexOfReadTime);
            final String _tmpContent;
            if (_cursor.isNull(_cursorIndexOfContent)) {
              _tmpContent = null;
            } else {
              _tmpContent = _cursor.getString(_cursorIndexOfContent);
            }
            final boolean _tmpUploadStatus;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfUploadStatus);
            _tmpUploadStatus = _tmp != 0;
            final long _tmpUploadTime;
            _tmpUploadTime = _cursor.getLong(_cursorIndexOfUploadTime);
            final boolean _tmpUploadSuccess;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfUploadSuccess);
            _tmpUploadSuccess = _tmp_1 != 0;
            _item = new NFCRecord(_tmpId,_tmpNfcId,_tmpCardNumber,_tmpCarNumber,_tmpUnitName,_tmpDeviceName,_tmpAmount,_tmpReadTime,_tmpContent,_tmpUploadStatus,_tmpUploadTime,_tmpUploadSuccess);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  @Override
  public Object getRecordCount(final Continuation<? super Integer> continuation) {
    final String _sql = "SELECT COUNT(*) FROM nfc_records";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if(_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, continuation);
  }

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
