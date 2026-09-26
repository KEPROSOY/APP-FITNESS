package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ActivityLevel
import com.example.data.model.ChatMessage
import com.example.data.model.DailySummary
import com.example.data.model.FoodItem
import com.example.data.model.GoalType
import com.example.data.model.MealIngredient
import com.example.data.model.MealRecord
import kotlinx.coroutines.flow.first
import com.example.data.model.MealType
import com.example.data.model.UserProfile
import com.example.data.model.WeightEntry
import com.example.data.model.AestheticMuscleCategory
import com.example.data.model.AestheticVolumeDistribution
import com.example.data.model.DailyGymSummary
import com.example.data.model.ExerciseSet
import com.example.data.model.PersonalRecord
import com.example.data.model.SetType
import com.example.data.model.WorkoutExercise
import com.example.data.model.WorkoutSession
import com.example.data.repository.FoodCatalogRepository
import com.example.data.repository.NutritionRepository
import com.example.data.repository.WorkoutRepository
import com.example.fitness.FitnessEngine
import com.example.service.AiAnalysisResult
import com.example.service.AiNutritionService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NutriViewModel(application: Application) : AndroidViewModel(application) {

  private val db = AppDatabase.getDatabase(application)
  private val repository = NutritionRepository(db)
  private val workoutRepo = WorkoutRepository(db.workoutDao(), db.personalRecordDao(), db.userProfileDao())
  private val foodCatalogRepo = FoodCatalogRepository()
  private val aiService = AiNutritionService()
  val authService = com.example.service.FirebaseAuthService(application)

  val currentFirebaseUser = authService.currentUser
  val cloudSyncStatus = authService.syncStatus
  val isCloudSyncing = authService.isSyncing

  val currentDateIso = MutableStateFlow(NutritionRepository.getTodayIso())

  val userProfile: StateFlow<UserProfile> = repository.userProfileFlow
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = UserProfile()
    )

  val todayMeals: StateFlow<List<MealRecord>> = repository.getMealsForDate(NutritionRepository.getTodayIso())
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = emptyList()
    )

  val todayGymSummary: StateFlow<DailyGymSummary> = workoutRepo.getDailyGymSummary(NutritionRepository.getTodayIso())
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = DailyGymSummary(NutritionRepository.getTodayIso(), 0, 0, 0f, 0, emptyList())
    )

  val allWorkoutSessions: StateFlow<List<WorkoutSession>> = workoutRepo.allSessionsFlow
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = emptyList()
    )

  val personalRecords: StateFlow<List<PersonalRecord>> = workoutRepo.personalRecordsFlow
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = emptyList()
    )

  val dailySummary: StateFlow<DailySummary> = combine(
    userProfile,
    todayMeals,
    currentDateIso,
    todayGymSummary
  ) { profile, meals, dateIso, gymSummary ->
    val totalCals = meals.sumOf { it.totalCalories }
    val totalP = meals.sumOf { it.totalProtein.toDouble() }.toFloat()
    val totalC = meals.sumOf { it.totalCarbs.toDouble() }.toFloat()
    val totalF = meals.sumOf { it.totalFat.toDouble() }.toFloat()
    val totalSat = meals.sumOf { it.totalSaturatedFat.toDouble() }.toFloat()
    val totalTrans = meals.sumOf { it.totalTransFat.toDouble() }.toFloat()

    DailySummary(
      dateIso = dateIso,
      totalCaloriesConsumed = totalCals,
      targetCalories = profile.dailyCalories,
      totalProtein = totalP,
      targetProtein = profile.proteinGoalGrams,
      totalCarbs = totalC,
      targetCarbs = profile.carbsGoalGrams,
      totalFat = totalF,
      targetFat = profile.fatGoalGrams,
      totalSaturatedFat = totalSat,
      totalTransFat = totalTrans,
      meals = meals,
      workoutCaloriesBurned = gymSummary.totalCaloriesBurned
    )
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.Eagerly,
    initialValue = DailySummary(
      dateIso = NutritionRepository.getTodayIso(),
      totalCaloriesConsumed = 1158,
      targetCalories = 2200,
      totalProtein = 78.3f,
      targetProtein = 140,
      totalCarbs = 142.8f,
      targetCarbs = 240,
      totalFat = 32.6f,
      targetFat = 65,
      totalSaturatedFat = 5.5f,
      totalTransFat = 0.0f,
      meals = emptyList(),
      workoutCaloriesBurned = 0
    )
  )

  val weightEntries: StateFlow<List<WeightEntry>> = repository.weightsFlow
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Eagerly,
      initialValue = emptyList()
    )

  // Food Search state
  val searchQuery = MutableStateFlow("")
  val selectedCategory = MutableStateFlow("Todos")
  val searchResults = MutableStateFlow<List<FoodItem>>(emptyList())

  // AI Vision Scanner state
  val isAiScanning = MutableStateFlow(false)
  val aiScanResult = MutableStateFlow<AiAnalysisResult?>(null)
  val capturedMealBitmap = MutableStateFlow<Bitmap?>(null)

  // Symmetry AI Body Scan state
  val isSymmetryScanning = MutableStateFlow(false)
  val symmetryScanResult = MutableStateFlow<com.example.data.model.SymmetryResult?>(null)

  init {
    viewModelScope.launch(Dispatchers.Default) {
      searchResults.value = foodCatalogRepo.searchFoods("", "Todos")
    }
    viewModelScope.launch(Dispatchers.IO) {
      val profile = repository.userProfileFlow.first()
      val today = NutritionRepository.getTodayIso()
      val lastActive = profile.lastActiveDateIso

      val updatedProfile = if (lastActive != today) {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val streak = try {
            if (lastActive != null) {
                val lastDate = format.parse(lastActive)
                val todayDate = format.parse(today)
                if (lastDate != null && todayDate != null) {
                    val diffDays = (todayDate.time - lastDate.time) / (1000 * 60 * 60 * 24)
                    if (diffDays == 1L) profile.streakCount + 1 else 1
                } else {
                    1
                }
            } else {
                1
            }
        } catch (e: Exception) { 1 }
        profile.copy(lastActiveDateIso = today, streakCount = streak)
      } else {
        profile
      }
      if (profile != updatedProfile) {
        repository.saveUserProfile(updatedProfile)
      }
      repository.cleanupOldMeals()
    }
    repository.checkAndSeedInitialData(viewModelScope)
  }

  // AI Chat Assistant state
  private val _chatMessages = MutableStateFlow(
    listOf(
      ChatMessage(
        text = "¡Hola! Soy SYVRA, tu asistente de nutrición y fitness inteligente. Puedo recomendarte qué comer hoy según tus macros restantes, darte recetas o resolver dudas. ¿Cómo puedo ayudarte?",
        isUser = false
      )
    )
  )
  val chatMessages = _chatMessages.asStateFlow()
  val isChatTyping = MutableStateFlow(false)

  val allRecipes: List<com.example.data.model.Recipe>
    get() = foodCatalogRepo.getRecipes()

  fun logRecipe(mealType: MealType, recipe: com.example.data.model.Recipe) {
    logMeal(mealType, recipe.ingredients)
  }

  // --- Onboarding & Profile ---
  fun completeOnboarding(
    name: String,
    age: Int,
    gender: String,
    heightCm: Float,
    weightKg: Float,
    activityLevel: ActivityLevel,
    goal: GoalType
  ) {
    val estimates = NutritionRepository.calculateNutrition(
      age = age,
      gender = gender,
      heightCm = heightCm,
      weightKg = weightKg,
      activityLevel = activityLevel,
      goal = goal
    )

    val updatedProfile = userProfile.value.copy(
      name = name.ifBlank { "Usuario" },
      age = age,
      gender = gender,
      heightCm = heightCm,
      weightKg = weightKg,
      targetWeightKg = if (goal == GoalType.PERDER_PESO) weightKg - 4f else if (goal == GoalType.GANAR_PESO) weightKg + 4f else weightKg,
      activityLevel = activityLevel,
      goal = goal,
      dailyCalories = estimates.dailyCalories,
      proteinGoalGrams = estimates.proteinGrams,
      carbsGoalGrams = estimates.carbsGrams,
      fatGoalGrams = estimates.fatGrams,
      isOnboardingCompleted = true,
      isLoggedIn = true
    )

    viewModelScope.launch {
      repository.saveUserProfile(updatedProfile)
      repository.insertWeight(weightKg, NutritionRepository.getTodayIso())
    }
  }

  fun login(email: String, provider: String = "EMAIL", name: String = "Alex") {
    val current = userProfile.value
    val updated = current.copy(
      email = email,
      authProvider = provider,
      name = if (current.name.isBlank() || current.name == "Alex") name else current.name,
      isLoggedIn = true,
      isOnboardingCompleted = true
    )
    viewModelScope.launch {
      repository.saveUserProfile(updated)
      syncAllDataToCloud()
    }
  }

  fun loginWithFirebase(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
    viewModelScope.launch {
      val res = authService.signInWithEmail(email, pass)
      res.fold(
        onSuccess = { user ->
          val cloudProfile = authService.fetchCloudProfile()
          val current = userProfile.value
          val profileToUse = (cloudProfile ?: current).copy(
            email = user.email ?: email,
            authProvider = "FIREBASE",
            isLoggedIn = true,
            isOnboardingCompleted = true
          )
          repository.saveUserProfile(profileToUse)
          syncAllDataToCloud()
          onResult(true, null)
        },
        onFailure = { err ->
          val friendlyMsg = when {
            err.message?.contains("password", ignoreCase = true) == true -> "Contraseña incorrecta. Verifica tus datos."
            err.message?.contains("no user", ignoreCase = true) == true -> "No existe cuenta con este correo. Puedes crear una nueva."
            err.message?.contains("network", ignoreCase = true) == true -> "Error de red o sin conexión. Modo local activado."
            else -> err.localizedMessage ?: "Error de autenticación"
          }
          onResult(false, friendlyMsg)
        }
      )
    }
  }

  fun registerWithFirebase(email: String, pass: String, onResult: (Boolean, String?) -> Unit) {
    viewModelScope.launch {
      val res = authService.signUpWithEmail(email, pass)
      res.fold(
        onSuccess = { user ->
          val current = userProfile.value
          val profileToUse = current.copy(
            email = user.email ?: email,
            authProvider = "FIREBASE",
            isLoggedIn = true,
            isOnboardingCompleted = true
          )
          repository.saveUserProfile(profileToUse)
          syncAllDataToCloud()
          onResult(true, null)
        },
        onFailure = { err ->
          val friendlyMsg = when {
            err.message?.contains("already in use", ignoreCase = true) == true -> "Este correo ya está registrado. Inicia sesión en su lugar."
            err.message?.contains("weak-password", ignoreCase = true) == true -> "La contraseña debe tener al menos 6 caracteres."
            else -> err.localizedMessage ?: "Error al crear la cuenta"
          }
          onResult(false, friendlyMsg)
        }
      )
    }
  }

  fun loginAnonymouslyWithFirebase(onResult: (Boolean, String?) -> Unit) {
    viewModelScope.launch {
      val res = authService.signInAnonymously()
      res.fold(
        onSuccess = { user ->
          val current = userProfile.value
          val profileToUse = current.copy(
            email = "invitado@syvra.app",
            authProvider = "INVITADO_FIREBASE",
            isLoggedIn = true,
            isOnboardingCompleted = true
          )
          repository.saveUserProfile(profileToUse)
          syncAllDataToCloud()
          onResult(true, null)
        },
        onFailure = { err ->
          login("invitado@syvra.app", "INVITADO")
          onResult(true, null)
        }
      )
    }
  }

  fun loginWithGoogle(
    idToken: String?,
    email: String?,
    displayName: String?,
    onResult: (Boolean, String?) -> Unit
  ) {
    viewModelScope.launch {
      val res = authService.signInWithGoogleToken(idToken, email, displayName)
      res.fold(
        onSuccess = { user ->
          val cloudProfile = authService.fetchCloudProfile()
          val current = userProfile.value
          val profileToUse = (cloudProfile ?: current).copy(
            name = user.displayName ?: displayName ?: current.name,
            email = user.email ?: email ?: current.email,
            authProvider = "GOOGLE_FIREBASE",
            isLoggedIn = true,
            isOnboardingCompleted = true
          )
          repository.saveUserProfile(profileToUse)
          syncAllDataToCloud()
          onResult(true, null)
        },
        onFailure = { err ->
          val current = userProfile.value
          val fallbackUser = current.copy(
            name = displayName ?: current.name,
            email = email ?: "usuario.google@nutriai.app",
            authProvider = "GOOGLE",
            isLoggedIn = true,
            isOnboardingCompleted = true
          )
          repository.saveUserProfile(fallbackUser)
          onResult(true, null)
        }
      )
    }
  }

  fun syncAllDataToCloud() {
    viewModelScope.launch {
      try {
        val profile = userProfile.value
        val workouts = allWorkoutSessions.value
        val meals = todayMeals.value
        val weights = weightEntries.value
        authService.syncUserDataToCloud(profile, workouts, meals, weights)
      } catch (e: Exception) {
        // Safe offline fallback
      }
    }
  }

  fun logout() {
    authService.signOut()
    val current = userProfile.value
    val updated = current.copy(
      isLoggedIn = false
    )
    viewModelScope.launch {
      repository.saveUserProfile(updated)
    }
  }

  fun updateAvatar(uriString: String) {
    val current = userProfile.value
    val updated = current.copy(avatarUri = uriString)
    viewModelScope.launch {
      repository.saveUserProfile(updated)
    }
  }

  fun updateProfile(profile: UserProfile) {
    viewModelScope.launch {
      repository.saveUserProfile(profile)
      syncAllDataToCloud()
    }
  }

  fun toggleTheme() {
    val current = userProfile.value
    updateProfile(current.copy(isDarkTheme = !current.isDarkTheme, followSystemTheme = false))
  }

  fun setFollowSystemTheme(follow: Boolean) {
    val current = userProfile.value
    updateProfile(current.copy(followSystemTheme = follow))
  }

  // --- Meals Logging ---
  fun logMeal(
    mealType: MealType,
    ingredients: List<MealIngredient>,
    imageUrl: String? = null
  ) {
    val timeFormatted = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())

    // Validate and sanitize nutritional accuracy (Calories = 4*P + 4*C + 9*F)
    val accurateIngredients = ingredients.map { ing ->
      val p = (Math.round(ing.protein * 10f)) / 10f
      val c = (Math.round(ing.carbs * 10f)) / 10f
      val f = (Math.round(ing.fat * 10f)) / 10f
      val sat = (Math.round(ing.saturatedFat.coerceAtMost(f) * 10f)) / 10f
      val trans = (Math.round(ing.transFat.coerceAtMost((f - sat).coerceAtLeast(0f)) * 10f)) / 10f
      val calcCals = Math.round(p * 4f + c * 4f + f * 9f).toInt()
      val finalCals = if (ing.calories > 0 && Math.abs(ing.calories - calcCals) <= 15) ing.calories else calcCals

      ing.copy(
        calories = finalCals,
        protein = p,
        carbs = c,
        fat = f,
        saturatedFat = sat,
        transFat = trans
      )
    }

    val totalCals = accurateIngredients.sumOf { it.calories }
    val totalP = accurateIngredients.sumOf { it.protein.toDouble() }.toFloat()
    val totalC = accurateIngredients.sumOf { it.carbs.toDouble() }.toFloat()
    val totalF = accurateIngredients.sumOf { it.fat.toDouble() }.toFloat()
    val totalSat = accurateIngredients.sumOf { it.saturatedFat.toDouble() }.toFloat()
    val totalTrans = accurateIngredients.sumOf { it.transFat.toDouble() }.toFloat()

    val meal = MealRecord(
      dateIso = NutritionRepository.getTodayIso(),
      mealType = mealType,
      timeFormatted = timeFormatted,
      totalCalories = totalCals,
      totalProtein = totalP,
      totalCarbs = totalC,
      totalFat = totalF,
      totalSaturatedFat = totalSat,
      totalTransFat = totalTrans,
      imageUrl = imageUrl,
      ingredients = accurateIngredients
    )

    viewModelScope.launch {
      repository.cleanupOldMeals()
      // Check if there is already a meal registered for this mealType today
      val existingMeal = todayMeals.value.find { it.mealType == mealType }
      if (existingMeal != null) {
        val mergedIngredients = existingMeal.ingredients + accurateIngredients
        val mergedCals = mergedIngredients.sumOf { it.calories }
        val mergedP = mergedIngredients.sumOf { it.protein.toDouble() }.toFloat()
        val mergedC = mergedIngredients.sumOf { it.carbs.toDouble() }.toFloat()
        val mergedF = mergedIngredients.sumOf { it.fat.toDouble() }.toFloat()
        val mergedSat = mergedIngredients.sumOf { it.saturatedFat.toDouble() }.toFloat()
        val mergedTrans = mergedIngredients.sumOf { it.transFat.toDouble() }.toFloat()

        val updatedMeal = existingMeal.copy(
          timeFormatted = timeFormatted,
          totalCalories = mergedCals,
          totalProtein = mergedP,
          totalCarbs = mergedC,
          totalFat = mergedF,
          totalSaturatedFat = mergedSat,
          totalTransFat = mergedTrans,
          imageUrl = imageUrl ?: existingMeal.imageUrl,
          ingredients = mergedIngredients
        )
        repository.insertMeal(updatedMeal)
      } else {
        repository.insertMeal(meal)
      }
    }
  }

  fun deleteMeal(mealId: Long) {
    viewModelScope.launch {
      repository.deleteMeal(mealId)
    }
  }

  fun removeIngredientFromMeal(mealId: Long, ingredientIndex: Int) {
    viewModelScope.launch {
      val meal = todayMeals.value.find { it.id == mealId } ?: return@launch
      if (ingredientIndex !in meal.ingredients.indices) return@launch

      val remainingIngredients = meal.ingredients.toMutableList().apply {
        removeAt(ingredientIndex)
      }

      if (remainingIngredients.isEmpty()) {
        repository.deleteMeal(mealId)
      } else {
        val mergedCals = remainingIngredients.sumOf { it.calories }
        val mergedP = remainingIngredients.sumOf { it.protein.toDouble() }.toFloat()
        val mergedC = remainingIngredients.sumOf { it.carbs.toDouble() }.toFloat()
        val mergedF = remainingIngredients.sumOf { it.fat.toDouble() }.toFloat()
        val mergedSat = remainingIngredients.sumOf { it.saturatedFat.toDouble() }.toFloat()
        val mergedTrans = remainingIngredients.sumOf { it.transFat.toDouble() }.toFloat()

        val updatedMeal = meal.copy(
          totalCalories = mergedCals,
          totalProtein = mergedP,
          totalCarbs = mergedC,
          totalFat = mergedF,
          totalSaturatedFat = mergedSat,
          totalTransFat = mergedTrans,
          ingredients = remainingIngredients
        )
        repository.insertMeal(updatedMeal)
      }
    }
  }

  // --- Food Search ---
  fun onSearchQueryChanged(query: String) {
    searchQuery.value = query
    viewModelScope.launch(Dispatchers.Default) {
      val results = foodCatalogRepo.searchFoods(query, selectedCategory.value)
      searchResults.value = results
    }
  }

  fun onCategorySelected(category: String) {
    selectedCategory.value = category
    viewModelScope.launch(Dispatchers.Default) {
      val results = foodCatalogRepo.searchFoods(searchQuery.value, category)
      searchResults.value = results
    }
  }

  // --- AI Food Scanner ---
  fun scanFoodImage(bitmap: Bitmap?, presetIndex: Int = 0) {
    capturedMealBitmap.value = bitmap
    isAiScanning.value = true
    viewModelScope.launch {
      val result = aiService.analyzeMealImage(bitmap, presetIndex)
      aiScanResult.value = result
      isAiScanning.value = false
    }
  }

  // --- AI Symmetry Body Scanner ---
  fun scanPhysiqueImage(bitmap: Bitmap?) {
    isSymmetryScanning.value = true
    viewModelScope.launch {
      val result = aiService.analyzeSymmetry(bitmap)
      symmetryScanResult.value = result
      isSymmetryScanning.value = false
    }
  }
  
  fun clearSymmetryScan() {
      symmetryScanResult.value = null
  }

  fun updateIngredientGrams(index: Int, newGrams: Float) {
    val current = aiScanResult.value ?: return
    if (index !in current.detectedIngredients.indices) return

    val oldItem = current.detectedIngredients[index]
    val factor = if (oldItem.grams > 0) newGrams / oldItem.grams else 1f
    val updatedItem = oldItem.copy(
      grams = newGrams,
      calories = (oldItem.calories * factor).toInt(),
      protein = oldItem.protein * factor,
      carbs = oldItem.carbs * factor,
      fat = oldItem.fat * factor,
      saturatedFat = oldItem.saturatedFat * factor,
      transFat = oldItem.transFat * factor
    )

    val updatedList = current.detectedIngredients.toMutableList().apply {
      set(index, updatedItem)
    }

    aiScanResult.value = current.copy(
      detectedIngredients = updatedList,
      totalCalories = updatedList.sumOf { it.calories },
      totalProtein = updatedList.sumOf { it.protein.toDouble() }.toFloat(),
      totalCarbs = updatedList.sumOf { it.carbs.toDouble() }.toFloat(),
      totalFat = updatedList.sumOf { it.fat.toDouble() }.toFloat(),
      totalSaturatedFat = updatedList.sumOf { it.saturatedFat.toDouble() }.toFloat(),
      totalTransFat = updatedList.sumOf { it.transFat.toDouble() }.toFloat()
    )
  }

  fun removeIngredient(index: Int) {
    val current = aiScanResult.value ?: return
    if (index !in current.detectedIngredients.indices) return

    val updatedList = current.detectedIngredients.toMutableList().apply {
      removeAt(index)
    }

    aiScanResult.value = current.copy(
      detectedIngredients = updatedList,
      totalCalories = updatedList.sumOf { it.calories },
      totalProtein = updatedList.sumOf { it.protein.toDouble() }.toFloat(),
      totalCarbs = updatedList.sumOf { it.carbs.toDouble() }.toFloat(),
      totalFat = updatedList.sumOf { it.fat.toDouble() }.toFloat(),
      totalSaturatedFat = updatedList.sumOf { it.saturatedFat.toDouble() }.toFloat(),
      totalTransFat = updatedList.sumOf { it.transFat.toDouble() }.toFloat()
    )
  }

  fun addIngredientToScan(item: FoodItem, grams: Float = 100f) {
    val current = aiScanResult.value ?: return
    val factor = grams / 100f
    val newItem = MealIngredient(
      foodName = item.name,
      grams = grams,
      calories = (item.caloriesPer100g * factor).toInt(),
      protein = item.proteinPer100g * factor,
      carbs = item.carbsPer100g * factor,
      fat = item.fatPer100g * factor,
      saturatedFat = item.saturatedFatPer100g * factor,
      transFat = item.transFatPer100g * factor,
      emoji = item.emoji
    )
    val updatedList = current.detectedIngredients + newItem

    aiScanResult.value = current.copy(
      detectedIngredients = updatedList,
      totalCalories = updatedList.sumOf { it.calories },
      totalProtein = updatedList.sumOf { it.protein.toDouble() }.toFloat(),
      totalCarbs = updatedList.sumOf { it.carbs.toDouble() }.toFloat(),
      totalFat = updatedList.sumOf { it.fat.toDouble() }.toFloat(),
      totalSaturatedFat = updatedList.sumOf { it.saturatedFat.toDouble() }.toFloat(),
      totalTransFat = updatedList.sumOf { it.transFat.toDouble() }.toFloat()
    )
  }

  fun confirmAiMealToDay(mealType: MealType) {
    val current = aiScanResult.value ?: return
    if (current.detectedIngredients.isEmpty()) return

    var savedImageUrl: String? = null
    capturedMealBitmap.value?.let { bmp ->
      try {
        val file = java.io.File(getApplication<android.app.Application>().filesDir, "meal_${System.currentTimeMillis()}.jpg")
        val out = java.io.FileOutputStream(file)
        bmp.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, out)
        out.flush()
        out.close()
        savedImageUrl = file.absolutePath
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }

    logMeal(
      mealType = mealType,
      ingredients = current.detectedIngredients,
      imageUrl = savedImageUrl
    )
    aiScanResult.value = null
    capturedMealBitmap.value = null
  }

  // --- Weight Tracking ---
  fun logWeight(weightKg: Float) {
    viewModelScope.launch {
      val today = NutritionRepository.getTodayIso()
      repository.insertWeight(weightKg, today)
      val current = userProfile.value
      repository.saveUserProfile(current.copy(weightKg = weightKg))
      syncAllDataToCloud()
    }
  }

  // --- AI Nutrition Chat ---
  fun sendChatMessage(text: String) {
    if (text.isBlank()) return
    val userMsg = ChatMessage(text = text.trim(), isUser = true)
    _chatMessages.value = _chatMessages.value + userMsg

    isChatTyping.value = true
    viewModelScope.launch {
      val replyText = aiService.answerNutritionChat(
        userMessage = text,
        userProfile = userProfile.value,
        summary = dailySummary.value
      )
      _chatMessages.value = _chatMessages.value + ChatMessage(text = replyText, isUser = false)
      isChatTyping.value = false
    }
  }

  fun resetAll() {
    viewModelScope.launch {
      repository.resetAllData()
      repository.checkAndSeedInitialData(viewModelScope)
    }
  }

  // --- Gym & Aesthetic Workout Operations ---
  fun saveWorkout(session: WorkoutSession) {
    viewModelScope.launch {
      workoutRepo.saveWorkoutSession(session)
    }
  }

  fun deleteWorkout(sessionId: Long) {
    viewModelScope.launch {
      workoutRepo.deleteSession(sessionId)
    }
  }

  fun estimate1Rm(weightKg: Float, reps: Int): Float {
    return FitnessEngine.estimate1Rm(weightKg, reps)
  }

  fun estimateTargetLoad(oneRm: Float, targetReps: Int): Float {
    return FitnessEngine.estimateTargetLoad(oneRm, targetReps)
  }

  fun getAestheticDistribution(sessions: List<WorkoutSession>): List<AestheticVolumeDistribution> {
    val allExercises = sessions.flatMap { it.exercises }
    return FitnessEngine.calculateSymmetryDistribution(allExercises)
  }

  fun seedSampleWorkoutIfEmpty() {
    viewModelScope.launch {
      val sessions = allWorkoutSessions.value
      if (sessions.isEmpty()) {
        val today = NutritionRepository.getTodayIso()
        val sampleSession = WorkoutSession(
          dateIso = today,
          title = "Sesión V-Taper: Hombros & Pectoral",
          durationMinutes = 65,
          notes = "Enfoque en deltoides lateral y press inclinado con sobrecarga progresiva",
          exercises = listOf(
            WorkoutExercise(
              exerciseName = "Press Militar con Barra",
              category = AestheticMuscleCategory.V_TAPER_DELTS,
              notes = "RPE 8.5 en la última serie",
              sets = listOf(
                ExerciseSet(setNumber = 1, weightKg = 40f, reps = 12, completed = true),
                ExerciseSet(setNumber = 2, weightKg = 50f, reps = 8, completed = true),
                ExerciseSet(setNumber = 3, weightKg = 55f, reps = 6, completed = true)
              )
            ),
            WorkoutExercise(
              exerciseName = "Elevaciones Laterales con Mancuerna",
              category = AestheticMuscleCategory.V_TAPER_DELTS,
              notes = "Control estricto en la fase excéntrica (3 seg)",
              sets = listOf(
                ExerciseSet(setNumber = 1, weightKg = 12f, reps = 15, completed = true),
                ExerciseSet(setNumber = 2, weightKg = 14f, reps = 12, completed = true),
                ExerciseSet(setNumber = 3, weightKg = 14f, reps = 10, completed = true)
              )
            ),
            WorkoutExercise(
              exerciseName = "Press Inclinado con Mancuernas",
              category = AestheticMuscleCategory.CHEST_UPPER_LOWER,
              notes = "Banco a 30 grados para pectoral clavicular",
              sets = listOf(
                ExerciseSet(setNumber = 1, weightKg = 24f, reps = 10, completed = true),
                ExerciseSet(setNumber = 2, weightKg = 28f, reps = 8, completed = true),
                ExerciseSet(setNumber = 3, weightKg = 30f, reps = 6, completed = true)
              )
            ),
            WorkoutExercise(
              exerciseName = "Jalón al Pecho Agarre Neutro",
              category = AestheticMuscleCategory.V_TAPER_LATS,
              notes = "Apertura dorsal máxima",
              sets = listOf(
                ExerciseSet(setNumber = 1, weightKg = 55f, reps = 12, completed = true),
                ExerciseSet(setNumber = 2, weightKg = 65f, reps = 10, completed = true),
                ExerciseSet(setNumber = 3, weightKg = 70f, reps = 8, completed = true)
              )
            )
          )
        )
        workoutRepo.saveWorkoutSession(sampleSession)
      }
    }
  }
}
