package com.oligark.getter.service.repository.source.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.oligark.getter.service.model.Business
import com.oligark.getter.service.model.User

/**
 * Created by pmvb on 17-09-25.
 */
@Dao
interface UserDao {

    @Query("select * from users")
    fun getUsers(): List<User>

    @Query("select * from users where id = :userId")
    fun getUser(userId: Int): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<User>)
}