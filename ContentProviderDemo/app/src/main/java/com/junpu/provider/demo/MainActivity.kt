package com.junpu.provider.demo

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uri = Uri.parse("content://com.junpu.provider/user")

        val cursor = contentResolver?.query(uri, null, null, null, null)
        if (cursor == null || cursor.count <= 0) {
            for (i in 1..5) {
                val value = ContentValues()
                value.put("id", i)
                value.put("name", "小张$i")
                value.put("age", 20 + i)
                value.put("sex", if (i % 2 == 0) "男" else "女")
                contentResolver?.insert(uri, value)
            }
        }
        cursor?.log()
        cursor?.close()

        val id = "4"
        println("根据Id查找：$id")
        queryId(id)

        val name = "小张3"
        println("根据Name查找：$name")
        queryName(name)

        val age = 22
        println("根据Age查找：$age")
        queryAge(age)

        val sex = "女"
        println("根据Sex查找：$sex")
        querySex(sex)

    }

    private fun Cursor.log() {
        while (moveToNext()) {
            val id = getLong(getColumnIndex("id"))
            val name = getString(getColumnIndex("name"))
            val age = getInt(getColumnIndex("age"))
            val sex = getString(getColumnIndex("sex"))
            println("id = $id, name = $name, age = $age, sex = $sex")
        }
        println("-------------------------------")
    }

    private fun queryId(id: String?) {
        id ?: return
        val uri = Uri.parse("content://com.junpu.provider/user/$id")
        val cursor = contentResolver?.query(uri, null, null, null, null)
        cursor?.log()
        cursor?.close()
    }

    private fun queryName(name: String?) {
        name ?: return
        val uri = Uri.parse("content://com.junpu.provider/user/name/$name")
        val cursor = contentResolver?.query(uri, null, null, null, null)
        cursor?.log()
        cursor?.close()
    }

    private fun queryAge(age: Int) {
        val uri = Uri.parse("content://com.junpu.provider/user/name/$age")
        val cursor = contentResolver?.query(uri, null, null, null, null)
        cursor?.log()
        cursor?.close()
    }

    private fun querySex(sex: String?) {
        sex ?: return
        val uri = Uri.parse("content://com.junpu.provider/user/sex/$sex")
        val cursor = contentResolver?.query(uri, null, null, null, null)
        cursor?.log()
        cursor?.close()
    }


}
