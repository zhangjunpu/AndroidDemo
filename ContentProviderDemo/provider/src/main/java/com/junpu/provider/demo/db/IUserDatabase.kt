package com.junpu.provider.demo.db

import android.content.ContentValues
import android.database.Cursor

interface IUserDatabase {
    fun queryAll(): Cursor?
    fun queryById(id: String?): Cursor?
    fun queryByName(name: String?): Cursor?
    fun queryByAge(age: Long): Cursor?
    fun queryBySex(sex: String?): Cursor?
    fun insert(values: ContentValues?): Long
}