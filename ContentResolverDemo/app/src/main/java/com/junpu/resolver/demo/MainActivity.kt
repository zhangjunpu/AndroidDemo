package com.junpu.resolver.demo

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        info?.clear()

        val uri = Uri.parse("content://com.junpu.provider/user")

        val cursor = contentResolver?.query(uri, null, null, null, null)
        cursor?.log()
        cursor?.close()

        val id = "4"
        println("根据Id查找：$id")
        info?.appendln("根据Id查找：$id")
        queryId(id)

        val name = "小张3"
        println("根据Name查找：$name")
        info?.appendln("根据Name查找：$name")
        queryName(name)

        val age = 22
        println("根据Age查找：$age")
        info?.appendln("根据Age查找：$age")
        queryAge(age)

        val sex = "男"
        println("根据Sex查找：$sex")
        info?.appendln("根据Sex查找：$sex")
        querySex(sex)
    }

    private fun Cursor.log() {
        while (moveToNext()) {
            val id = getLong(getColumnIndex("id"))
            val name = getString(getColumnIndex("name"))
            val age = getInt(getColumnIndex("age"))
            val sex = getString(getColumnIndex("sex"))
            val result = "id = $id, name = $name, age = $age, sex = $sex"
            println(result)
            info?.appendln(result)
        }
        println("-------------------------------")
        info?.appendln("-------------------------------")
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
