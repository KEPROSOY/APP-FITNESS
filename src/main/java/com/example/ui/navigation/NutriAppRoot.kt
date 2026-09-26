package com.example.ui.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.data.model.MealType
import com.example.ui.screens.AiFoodScanScreen
import com.example.ui.screens.FoodSearchScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.WelcomeScreen
import com.example.viewmodel.NutriViewModel

enum class RootScreen {
  WELCOME,
  ONBOARDING,
  MAIN,
  AI_SCAN,
  FOOD_SEARCH
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun NutriAppRoot(
  viewModel: NutriViewModel
) {
  val userProfile by viewModel.userProfile.collectAsState()
  val isAiScanning by viewModel.isAiScanning.collectAsState()
  val aiScanResult by viewModel.aiScanResult.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val searchResults by viewModel.searchResults.collectAsState()

  var currentScreen by remember {
    mutableStateOf(
      if (userProfile.isLoggedIn && userProfile.isOnboardingCompleted) RootScreen.MAIN else RootScreen.WELCOME
    )
  }

  // Restore and keep session active if user is logged in
  LaunchedEffect(userProfile.isLoggedIn, userProfile.isOnboardingCompleted) {
    if (userProfile.isLoggedIn && userProfile.isOnboardingCompleted) {
      if (currentScreen == RootScreen.WELCOME || currentScreen == RootScreen.ONBOARDING) {
        currentScreen = RootScreen.MAIN
      }
    }
  }

  var targetMealTypeForSearch by remember { mutableStateOf(MealType.COMIDA) }

  Crossfade(targetState = currentScreen, label = "root_navigation") { screen ->
    when (screen) {
      RootScreen.WELCOME -> {
        WelcomeScreen(
          onStartClick = { currentScreen = RootScreen.ONBOARDING },
          onLoginSuccess = { email, provider ->
            viewModel.login(email, provider)
            currentScreen = RootScreen.MAIN
          },
          onFirebaseEmailAuth = { email, pass, isRegister, onResult ->
            if (isRegister) {
              viewModel.registerWithFirebase(email, pass, onResult)
            } else {
              viewModel.loginWithFirebase(email, pass, onResult)
            }
          },
          onAnonymousLogin = { onResult ->
            viewModel.loginAnonymouslyWithFirebase(onResult)
          },
          onGoogleLogin = { idToken, email, displayName, onResult ->
            viewModel.loginWithGoogle(idToken, email, displayName, onResult)
          }
        )
      }

      RootScreen.ONBOARDING -> {
        OnboardingScreen(
          onComplete = { name, age, gender, heightCm, weightKg, activity, goal ->
            viewModel.completeOnboarding(name, age, gender, heightCm, weightKg, activity, goal)
            currentScreen = RootScreen.MAIN
          },
          onBack = { currentScreen = RootScreen.WELCOME }
        )
      }

      RootScreen.MAIN -> {
        MainScaffold(
          viewModel = viewModel,
          onNavigateToAiScan = { currentScreen = RootScreen.AI_SCAN },
          onNavigateToFoodSearch = { mealType ->
            targetMealTypeForSearch = mealType
            viewModel.onSearchQueryChanged("")
            currentScreen = RootScreen.FOOD_SEARCH
          },
          onLogout = {
            viewModel.logout()
            currentScreen = RootScreen.WELCOME
          }
        )
      }

      RootScreen.AI_SCAN -> {
        AiFoodScanScreen(
          isScanning = isAiScanning,
          scanResult = aiScanResult,
          onTriggerScan = { bitmap, preset ->
            viewModel.scanFoodImage(bitmap, preset)
          },
          onUpdateGrams = { index, grams ->
            viewModel.updateIngredientGrams(index, grams)
          },
          onRemoveIngredient = { index ->
            viewModel.removeIngredient(index)
          },
          onAddIngredient = { item, grams ->
            viewModel.addIngredientToScan(item, grams)
          },
          onConfirmMeal = { mealType ->
            viewModel.confirmAiMealToDay(mealType)
            currentScreen = RootScreen.MAIN
          },
          onBack = {
            viewModel.aiScanResult.value = null
            currentScreen = RootScreen.MAIN
          }
        )
      }

      RootScreen.FOOD_SEARCH -> {
        FoodSearchScreen(
          targetMealType = targetMealTypeForSearch,
          searchQuery = searchQuery,
          selectedCategory = selectedCategory,
          categories = listOf("Todos", "Proteínas", "Carbohidratos", "Grasas", "Frutas", "Verduras", "Bebidas"),
          searchResults = searchResults,
          recipes = viewModel.allRecipes,
          onSearchQueryChanged = { q -> viewModel.onSearchQueryChanged(q) },
          onCategorySelected = { c -> viewModel.onCategorySelected(c) },
          onAddFoodToMeal = { mealType, ingredient ->
            viewModel.logMeal(mealType, listOf(ingredient))
          },
          onAddRecipeToMeal = { mealType, recipe ->
            viewModel.logRecipe(mealType, recipe)
          },
          onBack = { currentScreen = RootScreen.MAIN }
        )
      }
    }
  }
}
