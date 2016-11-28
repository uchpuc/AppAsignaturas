package app.example.mx.asignaturas;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by richux on 29/10/16.
 */

public class AsignaturasDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "asignaturas.db";
    private static final String TABLE_NAME = "asignatura";
    public static final String COL_ASIGNATURA = "asignatura";
    public static final String COL_PROFESOR = "profesor";
    public static final String COL_SEMESTRE = "semestre";
    public static final String COL_HORAS = "horas";

    public AsignaturasDatabase(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String createQuery =
                "CREATE TABLE " + TABLE_NAME +
                        " (_id INTEGER PRIMARY KEY, "
                + COL_ASIGNATURA + " TEXT NOT NULL COLLATE UNICODE, "
                + COL_PROFESOR + " TEXT NOT NULL, "
                + COL_SEMESTRE + " TEXT NOT NULL, "
                + COL_HORAS + " TEXT NOT NULL)";

        db.execSQL(createQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String upgradeQuery = "DROP TABLE IF EXIST "+ TABLE_NAME;
        db.execSQL(upgradeQuery);
    }

    public static long insertaAsignatura (Context context, String asignatura, String profesor, String semestre, String horas){

        SQLiteOpenHelper dbOpenHelper = new AsignaturasDatabase(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

        ContentValues valorAsignatura = new ContentValues();
        valorAsignatura.put(COL_ASIGNATURA, asignatura);
        valorAsignatura.put(COL_PROFESOR, profesor);
        valorAsignatura.put(COL_SEMESTRE, semestre);
        valorAsignatura.put(COL_HORAS, horas);

        long result = -1L;
        try {
            result = database.insert(TABLE_NAME, null, valorAsignatura);
            if (result != -1L){

                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
                Intent intentFilter = new Intent(AsignaturasLoader.ACTION_RELOAD_TABLE);
                broadcastManager.sendBroadcast(intentFilter);
            }
        } finally {
            dbOpenHelper.close();
        }
        return result;
    }

    public static Cursor devuelveTodos (Context context){
        SQLiteOpenHelper dbOpenHelper = new AsignaturasDatabase(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();

        return  database.query(
                TABLE_NAME,
                new String[]{COL_ASIGNATURA, COL_PROFESOR, COL_SEMESTRE, COL_HORAS,BaseColumns._ID},
                null, null, null, null,
                COL_ASIGNATURA+" ASC");
    }

    public static Cursor devuelveConId(Context context, long identificador){
        SQLiteOpenHelper dbOpenHelper = new AsignaturasDatabase(context);
        SQLiteDatabase database = dbOpenHelper.getReadableDatabase();

        return database.query(
                TABLE_NAME,
                new String[]{COL_ASIGNATURA, COL_PROFESOR, COL_SEMESTRE, COL_HORAS, BaseColumns._ID},
                BaseColumns._ID + " = ?",
                new String[]{String.valueOf(identificador)},
                null,
                null,
                COL_ASIGNATURA+" ASC");
    }

    public static int eliminaConId(Context context, long asignaturaId){

        SQLiteOpenHelper dbOpenHelper = new AsignaturasDatabase(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

        int resultado = database.delete(
                TABLE_NAME,
                BaseColumns._ID + " =?",
                new String[]{String.valueOf(asignaturaId)});

        if (resultado != 0){

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
            Intent intentFilter = new Intent(AsignaturasLoader.ACTION_RELOAD_TABLE);
            broadcastManager.sendBroadcast(intentFilter);
        }

        dbOpenHelper.close();
        return resultado;

    }

    public static int actualizaAsignatura (Context context, String asignatura, String profesor, String semestre, String horas, long asignaturaId){

        SQLiteOpenHelper dbOpenHelper = new AsignaturasDatabase(context);
        SQLiteDatabase database = dbOpenHelper.getWritableDatabase();

        ContentValues valorAsignatura = new ContentValues();
        valorAsignatura.put(COL_ASIGNATURA, asignatura);
        valorAsignatura.put(COL_PROFESOR, profesor);
        valorAsignatura.put(COL_SEMESTRE, semestre);
        valorAsignatura.put(COL_HORAS, horas);

        int result = database.update(
                TABLE_NAME,
                valorAsignatura,
                BaseColumns._ID + " =?",
                new String[]{String.valueOf(asignaturaId)});

        if (result != 0){

            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
            Intent intentFilter = new Intent(AsignaturasLoader.ACTION_RELOAD_TABLE);
            broadcastManager.sendBroadcast(intentFilter);
        }

        dbOpenHelper.close();

        return result;
    }


















}
