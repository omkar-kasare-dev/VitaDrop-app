package com.social.vitadrop.presentation.viewmodel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.social.vitadrop.data.local.entity.StudentEntity
import com.social.vitadrop.data.repository.StudentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(private val repository: StudentRepository) : ViewModel() {

    val students = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun insertStudent(name: String, course: String) {
        viewModelScope.launch {
            repository.insert(StudentEntity(name = name, course = course))
        }
    }
}