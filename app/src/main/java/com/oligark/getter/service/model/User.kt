package com.oligark.getter.service.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by pmvb on 17-09-25.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: Int,
    val age: Int,
    val gender: String,
    val name: String,
    val password: String,
    val email: String
)