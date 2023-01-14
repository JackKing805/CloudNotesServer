package com.cool.cloudnotesserver.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cool.cloudnotesserver.ServerApp
import com.cool.cloudnotesserver.db.dao.AccessRecordDao
import com.cool.cloudnotesserver.db.dao.NoteDao
import com.cool.cloudnotesserver.db.dao.UserDao
import com.cool.cloudnotesserver.db.entity.AccessRecord
import com.cool.cloudnotesserver.db.entity.Note
import com.cool.cloudnotesserver.db.entity.User

@Database(entities = [AccessRecord::class, User::class, Note::class], version = 2, exportSchema = false)
abstract class ServerRoom : RoomDatabase() {
    companion object {
        val instance by lazy {
            Room.databaseBuilder(ServerApp.app, ServerRoom::class.java, "server.db")
                .allowMainThreadQueries()
                .build()
        }
    }

    abstract fun getAccessRecordDao():AccessRecordDao
    abstract fun getUserDao():UserDao
    abstract fun getNoteDao():NoteDao
}