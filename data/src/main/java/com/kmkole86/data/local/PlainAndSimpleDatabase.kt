package com.kmkole86.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kmkole86.data.local.dao.MovieDao
import com.kmkole86.data.local.model.MovieLocal
import com.kmkole86.data.local.model.PageLocal

@Database(
    version = 1,
    entities = [
        PageLocal::class,
        MovieLocal::class
    ]
)

abstract class PlainAndSimpleDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME: String = "simple_database"
    }

    abstract fun moviesDao(): MovieDao
}