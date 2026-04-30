package com.social.vitadrop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.social.vitadrop.presentation.navigation.NavGraph
import com.social.vitadrop.ui.theme.VitaDropTheme

class MainActivity : ComponentActivity() {
   // val db = AppDatabase.getDatabase(applicationContext)
    //val dao = db.studentDao()
   // val repository = StudentRepository(dao)
   // val viewModel = StudentViewModel(repository)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VitaDropTheme {
                //viewModel.insertStudent("Omkar", "Android Development")
               // viewModel.students
               NavGraph()
            }
        }
    }
}

