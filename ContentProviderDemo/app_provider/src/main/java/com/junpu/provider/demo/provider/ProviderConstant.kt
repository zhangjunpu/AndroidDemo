package com.junpu.provider.demo.provider

import android.provider.BaseColumns

object ProviderConstant {

    const val AUTHORIDY = "com.junpu.provider"

    object ProviderColumns : BaseColumns {
        const val TABLE_NAME = "user"
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_SEX = "sex"
        const val KEY_AGE = "age"
    }
}