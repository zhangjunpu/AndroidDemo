package com.junpu.resolver.demo

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.junpu.resolver.demo.databinding.ActivityMainBinding
import com.junpu.utils.appendLine
import com.junpu.utils.clearText
import com.junpu.viewbinding.bindingOnly

class MainActivity : AppCompatActivity() {

    private val binding by bindingOnly<ActivityMainBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.textInfo.clearText()

        val uri = Uri.parse("content://com.junpu.provider/user")

        val cursor = contentResolver?.query(uri, null, null, null, null)
        cursor?.log()
        cursor?.close()

        val id = "4"
        println("根据Id查找：$id")
        binding.textInfo.appendLine("根据Id查找：$id")
        queryId(id)

        val name = "小张3"
        println("根据Name查找：$name")
        binding.textInfo.appendLine("根据Name查找：$name")
        queryName(name)

        val age = 22
        println("根据Age查找：$age")
        binding.textInfo.appendLine("根据Age查找：$age")
        queryAge(age)

        val sex = "男"
        println("根据Sex查找：$sex")
        binding.textInfo.appendLine("根据Sex查找：$sex")
        querySex(sex)
    }

    private fun Cursor.log() {
        while (moveToNext()) {
            val id = getColumnIndex("id").let {
                if (it != -1) getLong(it) else null
            }
            val name = getColumnIndex("name").let {
                if (it != -1) getString(it) else null
            }
            val age = getColumnIndex("age").let {
                if (it != -1) getInt(it) else null
            }
            val sex = getColumnIndex("sex").let {
                if (it != -1) getString(it) else null
            }
            val result = "id = $id, name = $name, age = $age, sex = $sex"
            println(result)
            binding.textInfo.appendLine(result)
        }
        println("-------------------------------")
        binding.textInfo.appendLine("-------------------------------")
        binding.textInfo.appendLine("-------------------------------")
    }

    private fun queryId(id: String) {
        val uri = Uri.parse("content://com.junpu.provider/user/$id")
        val cursor = contentResolver?.query(uri, null, null, null, null)
        cursor?.log()
        cursor?.close()
    }

    private fun queryName(name: String) {
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

    private fun querySex(sex: String) {
        val uri = Uri.parse("content://com.junpu.provider/user/sex/$sex")
        val cursor = contentResolver?.query(uri, null, null, null, null)
        cursor?.log()
        cursor?.close()
    }

}
