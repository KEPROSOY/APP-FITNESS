package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.navigation.NutriAppRoot
import com.example.ui.theme.SyvraTheme
import com.example.viewmodel.NutriViewModel

class MainActivity : FragmentActivity() {

  private val viewModel: NutriViewModel by viewModels()

  @androidx.compose.material3.ExperimentalMaterial3Api
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val userProfile by viewModel.userProfile.collectAsState()
      val systemInDark = isSystemInDarkTheme()
      val isDarkTheme = if (userProfile.followSystemTheme) systemInDark else userProfile.isDarkTheme

      SyvraTheme(darkTheme = isDarkTheme) {
        Surface(modifier = Modifier.fillMaxSize()) {
          NutriAppRoot(viewModel = viewModel)
        }
      }
    }
  }
}

// Kept for screenshot test backwards compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  Text(text = "Hello $name!", modifier = modifier)
}
