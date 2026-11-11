package bi.kwitaho.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class BaseDeDonnees_Impl extends BaseDeDonnees {
  private volatile DaoDiagnostic _daoDiagnostic;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(1, "a9d34023121e87a0219582b62684221a", "f96a01f5bb931a8e5f72c7102e8bbd3c") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `diagnostics` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cheminsImages` TEXT NOT NULL, `codeMaladie` TEXT NOT NULL, `confiance` REAL NOT NULL, `timestamp` INTEGER NOT NULL, `demo` INTEGER NOT NULL, `synchronise` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'a9d34023121e87a0219582b62684221a')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `diagnostics`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsDiagnostics = new HashMap<String, TableInfo.Column>(7);
        _columnsDiagnostics.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnostics.put("cheminsImages", new TableInfo.Column("cheminsImages", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnostics.put("codeMaladie", new TableInfo.Column("codeMaladie", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnostics.put("confiance", new TableInfo.Column("confiance", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnostics.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnostics.put("demo", new TableInfo.Column("demo", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDiagnostics.put("synchronise", new TableInfo.Column("synchronise", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysDiagnostics = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesDiagnostics = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDiagnostics = new TableInfo("diagnostics", _columnsDiagnostics, _foreignKeysDiagnostics, _indicesDiagnostics);
        final TableInfo _existingDiagnostics = TableInfo.read(connection, "diagnostics");
        if (!_infoDiagnostics.equals(_existingDiagnostics)) {
          return new RoomOpenDelegate.ValidationResult(false, "diagnostics(bi.kwitaho.app.data.db.EntiteDiagnostic).\n"
                  + " Expected:\n" + _infoDiagnostics + "\n"
                  + " Found:\n" + _existingDiagnostics);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "diagnostics");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "diagnostics");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DaoDiagnostic.class, DaoDiagnostic_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DaoDiagnostic daoDiagnostic() {
    if (_daoDiagnostic != null) {
      return _daoDiagnostic;
    } else {
      synchronized(this) {
        if(_daoDiagnostic == null) {
          _daoDiagnostic = new DaoDiagnostic_Impl(this);
        }
        return _daoDiagnostic;
      }
    }
  }
}
