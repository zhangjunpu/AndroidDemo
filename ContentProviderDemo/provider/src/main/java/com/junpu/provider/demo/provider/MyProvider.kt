package com.junpu.provider.demo.provider

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.junpu.provider.demo.db.IUserDatabase
import com.junpu.provider.demo.db.UserDatabase

/**
 * 自定义ContentProvider
 * @author junpu
 * @time 2019/3/6
 */
class MyProvider : ContentProvider() {

    companion object {
        private const val USER = 100
        private const val USER_ID = 101
        private const val USER_NAME = 102
        private const val USER_AGE = 103
        private const val USER_SEX = 104
    }

    private var db: IUserDatabase? = null
    private var uriMatcher: UriMatcher? = null

    init {
        uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
        // 这个排列顺序很讲究，同一列的以"字符串 > # > *"为原则，否则match解析会出问题，原因请看源码
        uriMatcher?.addURI(ProviderConstant.AUTHORIDY, "user", USER)
        uriMatcher?.addURI(ProviderConstant.AUTHORIDY, "user/name/*", USER_NAME)
        uriMatcher?.addURI(ProviderConstant.AUTHORIDY, "user/age/#", USER_AGE)
        uriMatcher?.addURI(ProviderConstant.AUTHORIDY, "user/sex/*", USER_SEX)
        uriMatcher?.addURI(ProviderConstant.AUTHORIDY, "user/*", USER_ID)
    }

    override fun onCreate(): Boolean {
        db = UserDatabase(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        println("uri = $uri")
        return when (uriMatcher?.match(uri)) {
            USER -> db?.queryAll()
            USER_ID -> db?.queryById(uri.lastPathSegment)
            USER_NAME -> db?.queryByName(uri.lastPathSegment)
            USER_AGE -> db?.queryByAge(ContentUris.parseId(uri))
            USER_SEX -> db?.queryBySex(uri.lastPathSegment)
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (uriMatcher?.match(uri) == USER) {
            val id = db?.insert(values) ?: -1
            if (id > 0) {
                context?.contentResolver?.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
        }
        return null
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher?.match(uri)) {
            USER -> "vnd.android.cursor.dir/vnd.junpu.provider.user"
            USER_ID -> "vnd.android.cursor.item/vnd.junpu.provider.user.id"
            USER_NAME -> "vnd.android.cursor.dir/vnd.junpu.provider.user.name"
            USER_AGE -> "vnd.android.cursor.dir/vnd.junpu.provider.user.age"
            USER_SEX -> "vnd.android.cursor.dir/vnd.junpu.provider.user.sex"
            else -> null
        }
    }

    fun buildUri(): Uri {
        return Uri.parse("content://" + ProviderConstant.AUTHORIDY).buildUpon()
            .appendPath(ProviderConstant.ProviderColumns.TABLE_NAME).build()
    }

    /**
     * 获取Uri的倒数第二个path字段
     */
    private val Uri.secondLastSegments: String?
        get() {
            val index = pathSegments.lastIndex - 1
            return if (index < 0) null else pathSegments[index]
        }

}