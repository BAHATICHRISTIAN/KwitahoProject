package bi.kwitaho.app.data.db;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class DaoDiagnostic_Impl implements DaoDiagnostic {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<EntiteDiagnostic> __insertAdapterOfEntiteDiagnostic;

  public DaoDiagnostic_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfEntiteDiagnostic = new EntityInsertAdapter<EntiteDiagnostic>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `diagnostics` (`id`,`cheminsImages`,`codeMaladie`,`confiance`,`timestamp`,`demo`,`synchronise`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final EntiteDiagnostic entity) {
        statement.bindLong(1, entity.id);
        if (entity.cheminsImages == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.cheminsImages);
        }
        if (entity.codeMaladie == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.codeMaladie);
        }
        statement.bindDouble(4, entity.confiance);
        statement.bindLong(5, entity.timestamp);
        final int _tmp = entity.demo ? 1 : 0;
        statement.bindLong(6, _tmp);
        final int _tmp_1 = entity.synchronise ? 1 : 0;
        statement.bindLong(7, _tmp_1);
      }
    };
  }

  @Override
  public long inserer(final EntiteDiagnostic entite) {
    return DBUtil.performBlocking(__db, false, true, (_connection) -> {
      return __insertAdapterOfEntiteDiagnostic.insertAndReturnId(_connection, entite);
    });
  }

  @Override
  public LiveData<List<EntiteDiagnostic>> observerTout() {
    final String _sql = "SELECT * FROM diagnostics ORDER BY timestamp DESC";
    return __db.getInvalidationTracker().createLiveData(new String[] {"diagnostics"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCheminsImages = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cheminsImages");
        final int _columnIndexOfCodeMaladie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "codeMaladie");
        final int _columnIndexOfConfiance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "confiance");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final int _columnIndexOfDemo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "demo");
        final int _columnIndexOfSynchronise = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "synchronise");
        final List<EntiteDiagnostic> _result = new ArrayList<EntiteDiagnostic>();
        while (_stmt.step()) {
          final EntiteDiagnostic _item;
          _item = new EntiteDiagnostic();
          _item.id = _stmt.getLong(_columnIndexOfId);
          if (_stmt.isNull(_columnIndexOfCheminsImages)) {
            _item.cheminsImages = null;
          } else {
            _item.cheminsImages = _stmt.getText(_columnIndexOfCheminsImages);
          }
          if (_stmt.isNull(_columnIndexOfCodeMaladie)) {
            _item.codeMaladie = null;
          } else {
            _item.codeMaladie = _stmt.getText(_columnIndexOfCodeMaladie);
          }
          _item.confiance = (float) (_stmt.getDouble(_columnIndexOfConfiance));
          _item.timestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfDemo));
          _item.demo = _tmp != 0;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfSynchronise));
          _item.synchronise = _tmp_1 != 0;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<EntiteDiagnostic> observerParId(final long id) {
    final String _sql = "SELECT * FROM diagnostics WHERE id = ?";
    return __db.getInvalidationTracker().createLiveData(new String[] {"diagnostics"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCheminsImages = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cheminsImages");
        final int _columnIndexOfCodeMaladie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "codeMaladie");
        final int _columnIndexOfConfiance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "confiance");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final int _columnIndexOfDemo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "demo");
        final int _columnIndexOfSynchronise = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "synchronise");
        final EntiteDiagnostic _result;
        if (_stmt.step()) {
          _result = new EntiteDiagnostic();
          _result.id = _stmt.getLong(_columnIndexOfId);
          if (_stmt.isNull(_columnIndexOfCheminsImages)) {
            _result.cheminsImages = null;
          } else {
            _result.cheminsImages = _stmt.getText(_columnIndexOfCheminsImages);
          }
          if (_stmt.isNull(_columnIndexOfCodeMaladie)) {
            _result.codeMaladie = null;
          } else {
            _result.codeMaladie = _stmt.getText(_columnIndexOfCodeMaladie);
          }
          _result.confiance = (float) (_stmt.getDouble(_columnIndexOfConfiance));
          _result.timestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfDemo));
          _result.demo = _tmp != 0;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfSynchronise));
          _result.synchronise = _tmp_1 != 0;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<EntiteDiagnostic> obtenirToutMaintenant() {
    final String _sql = "SELECT * FROM diagnostics";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfCheminsImages = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "cheminsImages");
        final int _columnIndexOfCodeMaladie = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "codeMaladie");
        final int _columnIndexOfConfiance = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "confiance");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final int _columnIndexOfDemo = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "demo");
        final int _columnIndexOfSynchronise = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "synchronise");
        final List<EntiteDiagnostic> _result = new ArrayList<EntiteDiagnostic>();
        while (_stmt.step()) {
          final EntiteDiagnostic _item;
          _item = new EntiteDiagnostic();
          _item.id = _stmt.getLong(_columnIndexOfId);
          if (_stmt.isNull(_columnIndexOfCheminsImages)) {
            _item.cheminsImages = null;
          } else {
            _item.cheminsImages = _stmt.getText(_columnIndexOfCheminsImages);
          }
          if (_stmt.isNull(_columnIndexOfCodeMaladie)) {
            _item.codeMaladie = null;
          } else {
            _item.codeMaladie = _stmt.getText(_columnIndexOfCodeMaladie);
          }
          _item.confiance = (float) (_stmt.getDouble(_columnIndexOfConfiance));
          _item.timestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfDemo));
          _item.demo = _tmp != 0;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfSynchronise));
          _item.synchronise = _tmp_1 != 0;
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void supprimerParId(final long id) {
    final String _sql = "DELETE FROM diagnostics WHERE id = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void toutSupprimer() {
    final String _sql = "DELETE FROM diagnostics";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
