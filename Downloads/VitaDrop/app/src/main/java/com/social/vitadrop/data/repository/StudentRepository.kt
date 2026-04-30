package com.social.vitadrop.data.repository


import com.social.vitadrop.data.local.dao.StudentDao
import com.social.vitadrop.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

class StudentRepository(private val studentDao: StudentDao) {

    suspend fun insert(student: StudentEntity) {
        studentDao.insertStudent(student)
    }

    fun getAllStudents(): Flow<List<StudentEntity>> {
        return studentDao.getAllStudents()
    }
}