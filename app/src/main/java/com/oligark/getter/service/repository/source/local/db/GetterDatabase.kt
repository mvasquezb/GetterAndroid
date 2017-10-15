package com.oligark.getter.service.repository.source.local.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.oligark.getter.service.model.Business
import com.oligark.getter.service.model.Offer
import com.oligark.getter.service.model.Store
import com.oligark.getter.service.model.User

/**
 * Created by pmvb on 17-09-25.
 */
/**
 * Getter database class
 * Potential for Dependency Injection
 */
@Database(entities = arrayOf(
        User::class,
        Business::class,
        Store::class,
        Offer::class
), version = 4)
@TypeConverters(Converters::class)
abstract class GetterDatabase : RoomDatabase() {
    companion object {
        @JvmStatic private val DB_NAME = "GetterDb.db"

        @JvmStatic private var INSTANCE: GetterDatabase? = null
        @JvmStatic private val lock = Any()

        @JvmStatic
        fun getInstance(context: Context): GetterDatabase {
            synchronized(lock) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            GetterDatabase::class.java,
                            DB_NAME
                    ).fallbackToDestructiveMigration().build()
                }
                return INSTANCE!!
            }
        }
    }

    abstract fun storeDao(): StoreDao
    abstract fun offerDao(): OfferDao
}