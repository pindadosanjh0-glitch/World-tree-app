package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.data.AppDatabase
import com.example.data.TreeRepository
import com.example.ui.TreeApp
import com.example.ui.TreeViewModel
import com.example.ui.TreeViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Room DB, Dao, and Repository
        val database = AppDatabase.getDatabase(this)
        val repository = TreeRepository(database.treeDao())

        // Incline ViewModel with Custom Factory
        val viewModel: TreeViewModel by viewModels {
            TreeViewModelFactory(repository)
        }

        setContent {
            MyApplicationTheme {
                TreeApp(viewModel = viewModel)
            }
        }
    }
}
