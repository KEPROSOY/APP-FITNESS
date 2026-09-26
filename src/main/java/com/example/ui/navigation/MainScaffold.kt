package com.example.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealType
import com.example.ui.components.AddMealBottomSheet
import com.example.ui.components.LiquidMintGlow
import com.example.ui.screens.AiChatScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ProgressScreen
import com.example.viewmodel.NutriViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.GymViewModel

enum class MainTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val tag: String) {
  INICIO("Inicio", Icons.Filled.Home, Icons.Outlined.Home, "tab_inicio"),
  PROGRESO("Progreso", Icons.AutoMirrored.Filled.ShowChart, Icons.AutoMirrored.Outlined.ShowChart, "tab_progreso"),
  FISICO("Symmetry", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, "tab_fisico"),
  IA("IA", Icons.Filled.AutoAwesome, Icons.Outlined.AutoAwesome, "tab_ia"),
  PERFIL("Perfil", Icons.Filled.Person, Icons.Outlined.Person, "tab_perfil")
}

@Composable
fun MainScaffold(
  viewModel: NutriViewModel,
  gymViewModel: GymViewModel = viewModel(),
  onNavigateToAiScan: () -> Unit,
  onNavigateToFoodSearch: (MealType) -> Unit,
  onLogout: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var selectedTab by remember { mutableStateOf(MainTab.INICIO) }
  var showAddMealSheet by remember { mutableStateOf(false) }
  var targetMealTypeForSheet by remember { mutableStateOf(MealType.COMIDA) }

  val userProfile by viewModel.userProfile.collectAsState()
  val dailySummary by viewModel.dailySummary.collectAsState()
  val weightEntries by viewModel.weightEntries.collectAsState()
  val chatMessages by viewModel.chatMessages.collectAsState()
  val isChatTyping by viewModel.isChatTyping.collectAsState()
  val allWorkoutSessions by viewModel.allWorkoutSessions.collectAsState()
  val todayGymSummary by viewModel.todayGymSummary.collectAsState()
  val personalRecords by viewModel.personalRecords.collectAsState()
  val cloudSyncStatus by viewModel.cloudSyncStatus.collectAsState()
  val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
  val systemInDark = isSystemInDarkTheme()
  val isDark = if (userProfile.followSystemTheme) systemInDark else userProfile.isDarkTheme

  Scaffold(
    modifier = modifier.fillMaxSize(),
    bottomBar = {
      // Floating Minimalist Luxury Glass Dock
      Box(
        modifier = Modifier
          .navigationBarsPadding()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          .shadow(
            elevation = if (isDark) 16.dp else 10.dp,
            shape = RoundedCornerShape(28.dp),
            spotColor = if (isDark) Color(0x99000000) else Color(0x180F172A),
            ambientColor = if (isDark) Color(0x3338BDF8) else Color(0x0A0F172A)
          )
          .clip(RoundedCornerShape(28.dp))
          .background(
            if (isDark) {
              Brush.verticalGradient(
                listOf(
                  Color(0xFF0C1019).copy(alpha = 0.96f),
                  Color(0xFF030508).copy(alpha = 0.98f)
                )
              )
            } else {
              Brush.verticalGradient(
                listOf(
                  Color(0xFFFFFFFF).copy(alpha = 0.98f),
                  Color(0xFFF8FAFC).copy(alpha = 0.95f)
                )
              )
            }
          )
          .border(
            BorderStroke(
              1.dp,
              if (isDark) {
                Brush.verticalGradient(
                  listOf(
                    Color(0xFF38BDF8).copy(alpha = 0.35f),
                    Color.White.copy(alpha = 0.12f),
                    Color(0xFF1E293B).copy(alpha = 0.60f)
                  )
                )
              } else {
                Brush.verticalGradient(
                  listOf(
                    Color.White.copy(alpha = 0.95f),
                    Color(0xFFE2E8F0).copy(alpha = 0.80f),
                    Color(0xFFCBD5E1).copy(alpha = 0.35f)
                  )
                )
              }
            ),
            RoundedCornerShape(28.dp)
          )
      ) {
        NavigationBar(
          containerColor = Color.Transparent,
          tonalElevation = 0.dp
        ) {
          MainTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            NavigationBarItem(
              selected = isSelected,
              onClick = { selectedTab = tab },
              icon = {
                Icon(
                  imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                  contentDescription = tab.title,
                  modifier = Modifier.size(23.dp)
                )
              },
              label = {
                Text(
                  text = tab.title,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 11.sp
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                selectedTextColor = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                indicatorColor = if (isDark) Color(0xFF38BDF8).copy(alpha = 0.16f) else Color(0xFFE0F2FE),
                unselectedIconColor = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8),
                unselectedTextColor = if (isDark) Color(0xFF64748B) else Color(0xFF94A3B8)
              ),
              modifier = Modifier.testTag(tab.tag)
            )
          }
        }
      }
    },
    floatingActionButton = {
      if (selectedTab != MainTab.IA) {
        // Luxury Floating Action Button
        Box(
          modifier = Modifier
            .padding(bottom = 8.dp)
            .shadow(
              elevation = 12.dp,
              shape = CircleShape,
              spotColor = if (isDark) Color(0xFF38BDF8).copy(alpha = 0.60f) else Color(0x330284C7)
            )
            .clip(CircleShape)
            .background(
              if (isDark) {
                Brush.linearGradient(
                  listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                )
              } else {
                Brush.linearGradient(
                  listOf(Color(0xFF0284C7), Color(0xFF0369A1))
                )
              }
            )
            .border(
              BorderStroke(
                1.5.dp,
                Brush.linearGradient(
                  listOf(
                    Color.White.copy(alpha = if (isDark) 0.60f else 0.85f),
                    Color.White.copy(alpha = 0.15f),
                    Color.White.copy(alpha = 0.45f)
                  )
                )
              ),
              CircleShape
            )
        ) {
          FloatingActionButton(
            onClick = {
              targetMealTypeForSheet = MealType.COMIDA
              showAddMealSheet = true
            },
            containerColor = Color.Transparent,
            contentColor = Color.White,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(
              defaultElevation = 0.dp,
              pressedElevation = 2.dp
            ),
            modifier = Modifier.testTag("main_fab_add_meal")
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Agregar comida",
              modifier = Modifier.size(28.dp)
            )
          }
        }
      }
    },
    floatingActionButtonPosition = FabPosition.End
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        MainTab.INICIO -> {
          HomeScreen(
            userProfile = userProfile,
            dailySummary = dailySummary,
            onAddMealClick = { mealType ->
              targetMealTypeForSheet = mealType
              showAddMealSheet = true
            },
            onAiScanClick = onNavigateToAiScan,
            onDeleteMealClick = { mealId -> viewModel.deleteMeal(mealId) },
            onRemoveIngredientFromMeal = { mealId, index -> viewModel.removeIngredientFromMeal(mealId, index) },
            onToggleTheme = { viewModel.toggleTheme() },
            onLogout = onLogout
          )
        }
        MainTab.PROGRESO -> {
          ProgressScreen(
            userProfile = userProfile,
            weightEntries = weightEntries,
            onLogWeight = { w -> viewModel.logWeight(w) },
            dailySummary = dailySummary,
            workoutSessions = allWorkoutSessions,
            cloudSyncStatus = cloudSyncStatus,
            isCloudSyncing = isCloudSyncing,
            onForceSyncClick = { viewModel.syncAllDataToCloud() }
          )
        }
        MainTab.FISICO -> {
          val isSymmetryScanning by viewModel.isSymmetryScanning.collectAsState()
          val symmetryResult by viewModel.symmetryScanResult.collectAsState()
          com.example.ui.screens.SymmetryScreen(
            gymViewModel = gymViewModel,
            isScanning = isSymmetryScanning,
            scanResult = symmetryResult,
            onTriggerScan = { bitmap -> viewModel.scanPhysiqueImage(bitmap) },
            onClearScan = { viewModel.clearSymmetryScan() },
            isDarkTheme = isDark
          )
        }
        MainTab.IA -> {
          AiChatScreen(
            userProfile = userProfile,
            dailySummary = dailySummary,
            chatMessages = chatMessages,
            isTyping = isChatTyping,
            onSendMessage = { text -> viewModel.sendChatMessage(text) }
          )
        }
        MainTab.PERFIL -> {
          ProfileScreen(
            userProfile = userProfile,
            onUpdateProfile = { p -> viewModel.updateProfile(p) },
            onToggleTheme = { viewModel.toggleTheme() },
            onResetData = { viewModel.resetAll() },
            onLogout = onLogout,
            cloudSyncStatus = cloudSyncStatus,
            isCloudSyncing = isCloudSyncing,
            onForceSync = { viewModel.syncAllDataToCloud() }
          )
        }
      }
    }
  }

  // Add Meal Modal Bottom Sheet
  if (showAddMealSheet) {
    AddMealBottomSheet(
      initialMealType = targetMealTypeForSheet,
      onDismiss = { showAddMealSheet = false },
      onOptionSelected = { action, mealType ->
        if (action == "AI_SCAN") {
          onNavigateToAiScan()
        } else if (action == "SEARCH") {
          onNavigateToFoodSearch(mealType)
        }
      },
      onManualLog = { mealType, ingredient ->
        viewModel.logMeal(mealType, listOf(ingredient))
      }
    )
  }
}
