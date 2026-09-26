package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MealIngredient
import com.example.data.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealBottomSheet(
  initialMealType: MealType = MealType.COMIDA,
  onDismiss: () -> Unit,
  onOptionSelected: (action: String, mealType: MealType) -> Unit,
  onManualLog: (mealType: MealType, ingredient: MealIngredient) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var selectedType by remember { mutableStateOf(initialMealType) }
  var showManualDialog by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .padding(bottom = 32.dp)
        .navigationBarsPadding()
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Registrar comida",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDismiss) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Cerrar",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Meal Type Selector Chips
      Text(
        text = "Momento del día",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        MealType.entries.forEach { type ->
          val isSelected = type == selectedType
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
              .weight(1f)
              .clickable { selectedType = type }
              .testTag("meal_type_chip_${type.name.lowercase()}")
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(vertical = 10.dp)
            ) {
              Text(text = type.emoji, fontSize = 20.sp)
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = type.displayName,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(24.dp))

      Text(
        text = "¿Cómo deseas registrar?",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
      )
      Spacer(modifier = Modifier.height(12.dp))

      // 3 Main Options
      AddOptionCard(
        title = "Analizar con IA",
        description = "Toma una foto de tu plato y la IA estimará calorías y porciones",
        icon = Icons.Default.CameraAlt,
        iconTint = Color(0xFF10B981),
        tag = "option_ai_scan",
        onClick = {
          onDismiss()
          onOptionSelected("AI_SCAN", selectedType)
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      AddOptionCard(
        title = "Buscar alimento",
        description = "Busca en la base de datos de nutrición por categoría",
        icon = Icons.Default.Search,
        iconTint = Color(0xFF38BDF8),
        tag = "option_search_food",
        onClick = {
          onDismiss()
          onOptionSelected("SEARCH", selectedType)
        }
      )

      Spacer(modifier = Modifier.height(10.dp))

      AddOptionCard(
        title = "Registrar manualmente",
        description = "Ingresa directamente los gramos, calorías y macros",
        icon = Icons.Default.Edit,
        iconTint = Color(0xFFF59E0B),
        tag = "option_manual_entry",
        onClick = {
          showManualDialog = true
        }
      )
    }
  }

  if (showManualDialog) {
    ManualEntryDialog(
      mealType = selectedType,
      onDismiss = { showManualDialog = false },
      onConfirm = { ingredient ->
        showManualDialog = false
        onManualLog(selectedType, ingredient)
        onDismiss()
      }
    )
  }
}

@Composable
private fun AddOptionCard(
  title: String,
  description: String,
  icon: ImageVector,
  iconTint: Color,
  tag: String,
  onClick: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag(tag),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Surface(
        shape = CircleShape,
        color = iconTint.copy(alpha = 0.15f),
        modifier = Modifier.size(48.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp)
          )
        }
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }
    }
  }
}

@Composable
fun ManualEntryDialog(
  mealType: MealType,
  onDismiss: () -> Unit,
  onConfirm: (MealIngredient) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var grams by remember { mutableStateOf("150") }
  var calories by remember { mutableStateOf("250") }
  var protein by remember { mutableStateOf("25") }
  var carbs by remember { mutableStateOf("15") }
  var fat by remember { mutableStateOf("8") }
  var satFat by remember { mutableStateOf("1.5") }
  var transFat by remember { mutableStateOf("0.0") }

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
        Text(
          text = "Registro manual (${mealType.displayName})",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nombre del alimento") },
          placeholder = { Text("Ej. Ensalada de quinoa") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_manual_food_name")
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = grams,
            onValueChange = { grams = it },
            label = { Text("Gramos (g)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = calories,
            onValueChange = { calories = it },
            label = { Text("Calorías (kcal)") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
            label = { Text("Grasa total") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = satFat,
            onValueChange = { satFat = it },
            label = { Text("Sat (g)") },
            placeholder = { Text("Saturadas") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = transFat,
            onValueChange = { transFat = it },
            label = { Text("Trans (g)") },
            placeholder = { Text("Trans") },
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          androidx.compose.material3.TextButton(onClick = onDismiss) {
            Text("Cancelar")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = {
              val g = grams.toFloatOrNull() ?: 100f
              val cal = calories.toIntOrNull() ?: 150
              val p = protein.toFloatOrNull() ?: 10f
              val c = carbs.toFloatOrNull() ?: 10f
              val f = fat.toFloatOrNull() ?: 5f
              val sf = satFat.toFloatOrNull() ?: (f * 0.2f)
              val tf = transFat.toFloatOrNull() ?: 0f
              val foodTitle = name.ifBlank { "Comida casera" }

              onConfirm(
                MealIngredient(
                  foodName = foodTitle,
                  grams = g,
                  calories = cal,
                  protein = p,
                  carbs = c,
                  fat = f,
                  saturatedFat = sf,
                  transFat = tf,
                  emoji = "🍲"
                )
              )
            },
            modifier = Modifier.testTag("confirm_manual_entry_button")
          ) {
            Text("Guardar comida")
          }
        }
      }
    }
  }
}
