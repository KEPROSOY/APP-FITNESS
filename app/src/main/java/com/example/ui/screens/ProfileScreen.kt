package com.example.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.GoalType
import com.example.data.model.UserProfile
import com.example.data.model.calculateBmi
import com.example.ui.theme.MacroCarbs
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroProtein

@Composable
fun ProfileScreen(
  userProfile: UserProfile,
  onUpdateProfile: (UserProfile) -> Unit,
  onToggleTheme: () -> Unit,
  onResetData: () -> Unit,
  onLogout: () -> Unit = {},
  cloudSyncStatus: String = "Sincronizado",
  isCloudSyncing: Boolean = false,
  onForceSync: () -> Unit = {},
  modifier: Modifier = Modifier
) {
  var showEditProfileDialog by remember { mutableStateOf(false) }
  var showEditGoalsDialog by remember { mutableStateOf(false) }
  val context = androidx.compose.ui.platform.LocalContext.current

  val photoPickerLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = java.io.File(context.filesDir, "avatar_${System.currentTimeMillis()}.jpg")
        val outputStream = java.io.FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        onUpdateProfile(userProfile.copy(avatarUri = file.absolutePath))
      } catch (e: Exception) {
        e.printStackTrace()
        onUpdateProfile(userProfile.copy(avatarUri = uri.toString()))
      }
    }
  }

  val bmiResult = calculateBmi(userProfile.weightKg, userProfile.heightCm)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .testTag("profile_screen_scroll"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Avatar & Name with Profile Photo Selection
    item {
      Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Avatar with Camera Tap Overlay
          Box(
            modifier = Modifier
              .size(72.dp)
              .clickable {
                photoPickerLauncher.launch(
                  PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
              }
              .testTag("avatar_change_button"),
            contentAlignment = Alignment.BottomEnd
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primaryContainer,
              border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
              modifier = Modifier.size(72.dp)
            ) {
              if (!userProfile.avatarUri.isNullOrBlank()) {
                AsyncImage(
                  model = userProfile.avatarUri,
                  contentDescription = "Foto de perfil",
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
                )
              } else {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                  )
                }
              }
            }

            // Camera Badge
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary,
              border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.surface),
              modifier = Modifier.size(24.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.CameraAlt,
                  contentDescription = "Cambiar foto",
                  tint = Color.White,
                  modifier = Modifier.size(13.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = userProfile.name,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )
            if (!userProfile.email.isNullOrBlank()) {
              Text(
                text = userProfile.email ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = userProfile.goal.label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          IconButton(
            onClick = { showEditProfileDialog = true },
            modifier = Modifier.testTag("edit_profile_button")
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Editar",
              tint = MaterialTheme.colorScheme.primary
            )
          }
        }
      }
    }

    // IMC (Índice de Masa Corporal) Diagnostics Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
          containerColor = Color(bmiResult.colorHex).copy(alpha = 0.12f)
        ),
        border = BorderStroke(1.2.dp, Color(bmiResult.colorHex).copy(alpha = 0.4f)),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("card_profile_bmi")
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.MonitorWeight,
                contentDescription = null,
                tint = Color(bmiResult.colorHex),
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Índice de Masa Corporal (IMC)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = Color(bmiResult.colorHex)
            ) {
              Text(
                text = bmiResult.category,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            verticalAlignment = Alignment.Bottom
          ) {
            Text(
              text = bmiResult.bmi.toString(),
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = Color(bmiResult.colorHex)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "kg/m²",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = bmiResult.detail,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 20.sp
          )
        }
      }
    }

    // Physical Metrics Summary Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "Datos personales",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            MetricBadge(label = "Edad", value = "${userProfile.age} años")
            MetricBadge(label = "Sexo", value = userProfile.gender)
            MetricBadge(label = "Altura", value = "${userProfile.heightCm.toInt()} cm")
            MetricBadge(label = "Peso", value = "%.1f kg".format(userProfile.weightKg))
          }
        }
      }
    }

    // Daily Goals Summary Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Metas diarias",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface
            )

            TextButton(onClick = { showEditGoalsDialog = true }) {
              Text("Ajustar metas")
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "Calorías", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
              Text(text = "%,d kcal".format(userProfile.dailyCalories), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Column {
              Text(text = "Proteína", style = MaterialTheme.typography.labelSmall, color = MacroProtein)
              Text(text = "${userProfile.proteinGoalGrams} g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Column {
              Text(text = "Carbos", style = MaterialTheme.typography.labelSmall, color = MacroCarbs)
              Text(text = "${userProfile.carbsGoalGrams} g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Column {
              Text(text = "Grasas", style = MaterialTheme.typography.labelSmall, color = MacroFat)
              Text(text = "${userProfile.fatGoalGrams} g", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Settings Section
    item {
      Text(
        text = "Preferencias",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
      )
    }

    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
          // Follow System Theme Switch
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.BrightnessMedium, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(text = "Color del sistema", style = MaterialTheme.typography.bodyLarge)
                Text(
                  text = "Negro si el sistema está en oscuro, claro si está en claro",
                  style = MaterialTheme.typography.bodySmall,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
            Switch(
              checked = userProfile.followSystemTheme,
              onCheckedChange = { onUpdateProfile(userProfile.copy(followSystemTheme = it)) },
              modifier = Modifier.testTag("switch_system_theme")
            )
          }

          // Dark Mode Toggle (Manual Override)
          if (!userProfile.followSystemTheme) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Tema oscuro forzado", style = MaterialTheme.typography.bodyLarge)
              }
              Switch(
                checked = userProfile.isDarkTheme,
                onCheckedChange = { onToggleTheme() },
                modifier = Modifier.testTag("switch_dark_theme")
              )
            }
          }

          // Notifications Toggle
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(12.dp))
              Text(text = "Recordatorios de comida", style = MaterialTheme.typography.bodyLarge)
            }
            Switch(
              checked = userProfile.notificationsEnabled,
              onCheckedChange = { onUpdateProfile(userProfile.copy(notificationsEnabled = it)) },
              modifier = Modifier.testTag("switch_notifications")
            )
          }
        }
      }
    }

    // Session Management & Logout
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Cuenta de usuario (Firebase Auth)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = if (!userProfile.email.isNullOrBlank()) "Sesión: ${userProfile.email} (${userProfile.authProvider})"
                else "Sesión local activa",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Firebase Cloud status row
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "Estado de persistencia en la nube",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = cloudSyncStatus,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.primary
                )
              }

              Button(
                onClick = onForceSync,
                enabled = !isCloudSyncing,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
              ) {
                Text(if (isCloudSyncing) "Sincronizando..." else "Sincronizar")
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.errorContainer,
              contentColor = MaterialTheme.colorScheme.onErrorContainer
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("button_logout")
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.Logout,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Cerrar sesión de Firebase", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Privacy and Reset
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Privacidad y almacenamiento local", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Tus registros de nutrición se almacenan en la base de datos local SQLite/Room del dispositivo.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = onResetData,
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("button_reset_data")
          ) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Restablecer datos de prueba", color = MaterialTheme.colorScheme.error)
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(72.dp))
    }
  }

  // Edit Personal Profile Dialog
  if (showEditProfileDialog) {
    EditProfileDialog(
      userProfile = userProfile,
      onDismiss = { showEditProfileDialog = false },
      onConfirm = { updated ->
        onUpdateProfile(updated)
        showEditProfileDialog = false
      }
    )
  }

  // Edit Goals Dialog
  if (showEditGoalsDialog) {
    EditGoalsDialog(
      userProfile = userProfile,
      onDismiss = { showEditGoalsDialog = false },
      onConfirm = { updated ->
        onUpdateProfile(updated)
        showEditGoalsDialog = false
      }
    )
  }
}

@Composable
private fun MetricBadge(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    Spacer(modifier = Modifier.height(2.dp))
    Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
  }
}

@Composable
fun EditProfileDialog(
  userProfile: UserProfile,
  onDismiss: () -> Unit,
  onConfirm: (UserProfile) -> Unit
) {
  var name by remember { mutableStateOf(userProfile.name) }
  var age by remember { mutableStateOf(userProfile.age.toString()) }
  var height by remember { mutableStateOf(userProfile.heightCm.toInt().toString()) }
  var weight by remember { mutableStateOf(userProfile.weightKg.toString()) }

  androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        Text(text = "Editar perfil", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nombre") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Edad") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = height,
            onValueChange = { height = it },
            label = { Text("Altura (cm)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = weight,
          onValueChange = { weight = it },
          label = { Text("Peso actual (kg)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) { Text("Cancelar") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val updated = userProfile.copy(
                name = name.ifBlank { userProfile.name },
                age = age.toIntOrNull() ?: userProfile.age,
                heightCm = height.toFloatOrNull() ?: userProfile.heightCm,
                weightKg = weight.toFloatOrNull() ?: userProfile.weightKg
              )
              onConfirm(updated)
            }
          ) {
            Text("Guardar")
          }
        }
      }
    }
  }
}

@Composable
fun EditGoalsDialog(
  userProfile: UserProfile,
  onDismiss: () -> Unit,
  onConfirm: (UserProfile) -> Unit
) {
  var calories by remember { mutableStateOf(userProfile.dailyCalories.toString()) }
  var protein by remember { mutableStateOf(userProfile.proteinGoalGrams.toString()) }
  var carbs by remember { mutableStateOf(userProfile.carbsGoalGrams.toString()) }
  var fat by remember { mutableStateOf(userProfile.fatGoalGrams.toString()) }

  androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(24.dp)
      ) {
        Text(text = "Ajustar metas diarias", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = calories,
          onValueChange = { calories = it },
          label = { Text("Calorías diarias (kcal)") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = protein,
            onValueChange = { protein = it },
            label = { Text("Prot (g)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = carbs,
            onValueChange = { carbs = it },
            label = { Text("Carb (g)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = fat,
            onValueChange = { fat = it },
            label = { Text("Grasa (g)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = onDismiss) { Text("Cancelar") }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val updated = userProfile.copy(
                dailyCalories = calories.toIntOrNull() ?: userProfile.dailyCalories,
                proteinGoalGrams = protein.toIntOrNull() ?: userProfile.proteinGoalGrams,
                carbsGoalGrams = carbs.toIntOrNull() ?: userProfile.carbsGoalGrams,
                fatGoalGrams = fat.toIntOrNull() ?: userProfile.fatGoalGrams
              )
              onConfirm(updated)
            }
          ) {
            Text("Guardar metas")
          }
        }
      }
    }
  }
}
