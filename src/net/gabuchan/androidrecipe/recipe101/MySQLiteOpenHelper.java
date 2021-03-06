package net.gabuchan.androidrecipe.recipe101;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    // データベースのバージョン
    private static final int DB_VERSION = 2;
    // データベース名
    private static final String DB_NAME = "recipe.db";

    public MySQLiteOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // DBが新規作成された時に呼び出される。
    @Override
    public void onCreate(SQLiteDatabase db) {
        // テーブル作成
        db.execSQL("CREATE TABLE IF NOT EXISTS people"
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "  name TEXT,"
                + "  age INTEGER);");

        // 初期データを投入
        ContentValues values = new ContentValues();
        for (int i = 0; i < 30; i++) {
            values.put("name", "gabu" + i);
            values.put("age", i);
            db.insert("people", null, values);
        }
    }

    // DBのバージョンとコンストラクタで指定したバージョンが違う時に
    // 呼び出される。
    @Override
    public void onUpgrade(SQLiteDatabase db,
                          int oldVersion,
                          int newVersion) {
        // バージョンを比較
        if (oldVersion == 1 && newVersion == 2) {
            db.execSQL("ALTER TABLE people ADD COLUMN email TEXT;");
        }
    }
}