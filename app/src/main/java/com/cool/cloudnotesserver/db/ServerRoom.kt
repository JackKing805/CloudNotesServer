package com.cool.cloudnotesserver.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cool.cloudnotesserver.ServerApp
import com.cool.cloudnotesserver.db.dao.AccessRecordDao
import com.cool.cloudnotesserver.db.entity.AccessRecord

@Database(entities = [AccessRecord::class], version = 1, exportSchema = false)
abstract class ServerRoom : RoomDatabase() {
    companion object {
        val instance by lazy {
            Room.databaseBuilder(ServerApp.app, ServerRoom::class.java, "server.db")
                .allowMainThreadQueries()
                .build()
        }
    }

    abstract fun getAccessRecordDao():AccessRecordDao
}