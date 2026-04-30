package com.social.vitadrop.data.local.dao



import androidx.room.*
import com.social.vitadrop.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert
    suspend fun insertStudent(student: StudentEntity)

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Delete
    suspend fun deleteStudent(student: StudentEntity)
}