package com.junpu.provider.demo.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.junpu.provider.demo.provider.ProviderConstant.ProviderColumns.KEY_AGE
import com.junpu.provider.demo.provider.ProviderConstant.ProviderColumns.KEY_ID
import com.junpu.provider.demo.provider.ProviderConstant.ProviderColumns.KEY_NAME
import com.junpu.provider.demo.provider.ProviderConstant.ProviderColumns.KEY_SEX
import com.junpu.provider.demo.provider.ProviderConstant.ProviderColumns.TABLE_NAME

class UserDatabase(context: Context?) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION),
    IUserDatabase {

    companion object {
        private const val DB_NAME = "Junpu_Provider_DB"
        private const val DB_VERSION = 1
    }

    private var isIgnoreUpgrade = true // 是否忽略升级

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME($KEY_ID TEXT PRIMARY KEY NOT NULL, $KEY_NAME TEXT, $KEY_SEX TEXT, $KEY_AGE INTEGER )")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (!isIgnoreUpgrade) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }

    override fun queryAll(): Cursor? {
        return readableDatabase?.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    override fun queryById(id: String?): Cursor? {
        id ?: return null
        return readableDatabase?.rawQuery("SELECT * FROM $TABLE_NAME WHERE $KEY_ID='$id'", null)
    }

    override fun queryByName(name: String?): Cursor? {
        name ?: return null
        return readableDatabase?.rawQuery("SELECT * FROM $TABLE_NAME WHERE $KEY_NAME='$name'", null)
    }

    override fun queryByAge(age: Long): Cursor? {
        return readableDatabase?.rawQuery("SELECT * FROM $TABLE_NAME WHERE $KEY_AGE=$age", null)
    }

    override fun queryBySex(sex: String?): Cursor? {
        sex ?: return null
        return readableDatabase?.rawQuery("SELECT * FROM $TABLE_NAME WHERE $KEY_SEX='$sex'", null)
    }

    override fun insert(values: ContentValues?): Long {
        values ?: -1
        return writableDatabase?.insert(TABLE_NAME, null, values) ?: -1
    }
}