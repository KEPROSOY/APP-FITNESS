package com.example.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.MealIngredient
import com.example.data.model.MealType
import com.example.service.AiAnalysisResult
import com.example.data.model.FoodItem
import com.example.data.repository.FoodCatalogRepository
import com.example.ui.theme.MacroCarbs
import com.example.ui.theme.MacroFat
import com.example.ui.theme.MacroProtein

@Composable
fun AiFoodScanScreen(
  isScanning: Boolean,
  scanResult: AiAnalysisResult?,
  onTriggerScan: (Bitmap?, Int) -> Unit,
  onUpdateGrams: (Int, Float) -> Unit,
  onRemoveIngredient: (Int) -> Unit,
  onAddIngredient: (FoodItem, Float) -> Unit = { _, _ -> },
  onConfirmMeal: (MealType) -> Unit,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedMealType by remember { mutableStateOf(MealType.COMIDA) }
  var editingIngredientIndex by remember { mutableStateOf<Int?>(null) }
  var showAddIngredientDialog by remember { mutableStateOf(false) }
  var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
  var showCameraPermissionRationale by remember { mutableStateOf(false) }

  val cameraLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.TakePicturePreview()
  ) { bitmap ->
    if (bitmap != null) {
      capturedBitmap = bitmap
      onTriggerScan(bitmap, 0)
    }
  }

  val galleryLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) {
      try {
        context.contentResolver.openInputStream(uri)?.use { stream ->
          val bmp = BitmapFactory.decodeStream(stream)
          if (bmp != null) {
            capturedBitmap = bmp
            onTriggerScan(bmp, 0)
          }
        }
      } catch (_: Exception) {}
    }
  }

  val cameraPermissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    if (isGranted) {
      cameraLauncher.launch(null)
    } else {
      showCameraPermissionRationale = true
    }
  }

  // Laser scanning animation
  val infiniteTransition = rememberInfiniteTransition(label = "scan_laser")
  val laserProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = LinearEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "laser_y"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    if (scanResult == null) {
      // PANTALLA 5 — VISOR DE CÁMARA & ESCÁNER IA
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Top Back & Title Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 8.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          IconButton(onClick = onBack, modifier = Modifier.testTag("ai_scan_back_button")) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Volver",
              tint = MaterialTheme.colorScheme.onBackground
            )
          }

          Text(
            text = "Escanear alimento",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )

          // AI Badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "SYVRA Vision",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Viewfinder Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .clip(RoundedCornerShape(28.dp))
            .background(Color(0xFF0F172A))
            .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), RoundedCornerShape(28.dp)),
          contentAlignment = Alignment.Center
        ) {
          // Food camera viewfinder or captured camera image
          if (capturedBitmap != null) {
            Image(
              bitmap = capturedBitmap!!.asImageBitmap(),
              contentDescription = "Foto capturada",
              contentScale = ContentScale.Crop,
              modifier = Modifier.fillMaxSize()
            )
          } else {
            // Futuristic Camera HUD viewfinder (no static chicken image)
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    listOf(Color(0xFF0F172A), Color(0xFF020617))
                  )
                ),
              contentAlignment = Alignment.Center
            ) {
              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
              ) {
                Icon(
                  imageVector = Icons.Default.PhotoCamera,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                  modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                  text = "Cámara Lista para Escanear",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Apunta a tu comida o selecciona una foto de tu galería",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFF94A3B8)
                )
              }
            }
          }

          // Semi-transparent overlay
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = if (isScanning) 0.35f else 0.15f))
          )

          // Viewfinder Target Corners
          Box(
            modifier = Modifier
              .size(240.dp)
              .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
          )

          // Animated Scanning Laser Beam
          if (isScanning) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.TopCenter)
                .padding(top = (laserProgress * 300).dp)
                .background(
                  Brush.horizontalGradient(
                    listOf(Color.Transparent, Color(0xFF34D399), Color.White, Color(0xFF34D399), Color.Transparent)
                  )
                )
            )

            // Scanning Overlay Card
            Card(
              shape = RoundedCornerShape(18.dp),
              colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
              modifier = Modifier.padding(24.dp)
            ) {
              Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                CircularProgressIndicator(
                  color = Color(0xFF34D399),
                  modifier = Modifier.size(36.dp),
                  strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                  text = "Analizando alimentos con IA...",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Detectando ingredientes y estimando porciones",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color(0xFF94A3B8),
                  textAlign = TextAlign.Center
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Non-presumptive disclaimer mandated by user prompt
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Cantidades estimadas por IA. Las estimaciones pueden variar según preparación y marca.",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Camera Action Trigger Buttons
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
          horizontalArrangement = Arrangement.SpaceEvenly,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Option 1: Gallery Picker
          OutlinedButton(
            onClick = {
              galleryLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
              )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("button_open_gallery")
          ) {
            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Galería")
          }

          // Main Shutter Button - Real Camera Photo
          Column(
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .size(72.dp)
                .clickable(enabled = !isScanning) {
                  cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
                .testTag("ai_camera_shutter_button")
            ) {
              Box(contentAlignment = Alignment.Center) {
                Surface(
                  shape = CircleShape,
                  color = Color.White,
                  modifier = Modifier.size(56.dp)
                ) {
                  Box(contentAlignment = Alignment.Center) {
                    Icon(
                      imageVector = Icons.Default.PhotoCamera,
                      contentDescription = "Tomar foto con cámara",
                      tint = MaterialTheme.colorScheme.primary,
                      modifier = Modifier.size(30.dp)
                    )
                  }
                }
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Cámara",
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
          }

          // Option 2: Sample Preset (Cycles dynamically: Carne, Pollo, Salmón, Pasta, Huevos)
          var samplePresetIndex by remember { mutableIntStateOf(0) }
          OutlinedButton(
            onClick = {
              val currentIdx = samplePresetIndex
              samplePresetIndex = (samplePresetIndex + 1) % 5
              val sampleBitmap = createFoodPlateBitmap(currentIdx)
              capturedBitmap = sampleBitmap
              onTriggerScan(sampleBitmap, currentIdx)
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.testTag("button_scan_preset_1")
          ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Probar Plato")
          }
        }
      }
    } else {
      // PANTALLA 6 — RESULTADO DEL ANÁLISIS
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .padding(horizontal = 20.dp)
          .testTag("ai_scan_results_view"),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Top Back
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            IconButton(onClick = onBack) {
              Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
              text = "Resultado del análisis",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(
              onClick = {
                // re-scan
                onTriggerScan(null, 0)
              }
            ) {
              Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Reescanear", tint = MaterialTheme.colorScheme.primary)
            }
          }
        }

        // Food Image Preview & Total Calories Banner
        item {
          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column {
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
              ) {
                if (capturedBitmap != null) {
                  Image(
                    bitmap = capturedBitmap!!.asImageBitmap(),
                    contentDescription = scanResult.mealName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                  )
                } else {
                  Box(
                    modifier = Modifier
                      .fillMaxSize()
                      .background(
                        Brush.linearGradient(
                          listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                        )
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = scanResult.detectedIngredients.firstOrNull()?.emoji ?: "🍽️",
                      fontSize = 68.sp
                    )
                  }
                }

                Surface(
                  shape = RoundedCornerShape(12.dp),
                  color = Color.Black.copy(alpha = 0.75f),
                  modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                ) {
                  Text(
                    text = scanResult.mealName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }

              // Macro Summary Banner
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Text(text = "Total estimado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                      text = "${scanResult.totalCalories} kcal",
                      style = MaterialTheme.typography.headlineMedium,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.primary
                    )
                  }

                  Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "Prot", style = MaterialTheme.typography.labelSmall, color = MacroProtein)
                      Text(text = "${scanResult.totalProtein.toInt()}g", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "Carb", style = MaterialTheme.typography.labelSmall, color = MacroCarbs)
                      Text(text = "${scanResult.totalCarbs.toInt()}g", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                      Text(text = "Grasa", style = MaterialTheme.typography.labelSmall, color = MacroFat)
                      Text(text = "${scanResult.totalFat.toInt()}g", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                  }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fat Quality Breakdown Chips
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFF7ED),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text(
                      text = "Grasas Saturadas: %.1fg".format(scanResult.totalSaturatedFat),
                      style = MaterialTheme.typography.labelSmall,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = Color(0xFFEA580C),
                      textAlign = TextAlign.Center,
                      modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                    )
                  }

                  val hasTrans = scanResult.totalTransFat > 0.05f
                  Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (hasTrans) Color(0xFFFEE2E2) else Color(0xFFDCFCE7),
                    modifier = Modifier.weight(1f)
                  ) {
                    Text(
                      text = if (hasTrans) "Trans: %.1fg ⚠️".format(scanResult.totalTransFat) else "Trans: 0g ✨",
                      style = MaterialTheme.typography.labelSmall,
                      fontSize = 11.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = if (hasTrans) Color(0xFFDC2626) else Color(0xFF16A34A),
                      textAlign = TextAlign.Center,
                      modifier = Modifier.padding(vertical = 4.dp, horizontal = 6.dp)
                    )
                  }
                }
              }
            }
          }
        }

        // Meal Type Selector
        item {
          Column {
            Text(
              text = "Guardar como:",
              style = MaterialTheme.typography.labelMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              MealType.entries.forEach { type ->
                val isSelected = type == selectedMealType
                Surface(
                  shape = RoundedCornerShape(14.dp),
                  color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .weight(1f)
                    .clickable { selectedMealType = type }
                ) {
                  Text(
                    text = "${type.emoji} ${type.displayName}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp)
                  )
                }
              }
            }
          }
        }

        // Detected Ingredients Section Header
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Alimentos detectados (${scanResult.detectedIngredients.size})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onBackground
            )

            Text(
              text = "Toca para ajustar",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.primary
            )
          }
        }

        // List of Ingredients
        itemsIndexed(scanResult.detectedIngredients) { index, item ->
          IngredientItemCard(
            item = item,
            onEditClick = { editingIngredientIndex = index },
            onDeleteClick = { onRemoveIngredient(index) }
          )
        }

        // Add Ingredient Separately Button
        item {
          OutlinedButton(
            onClick = { showAddIngredientDialog = true },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("button_add_ingredient_separately")
          ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "+ Agregar ingrediente por separado",
              style = MaterialTheme.typography.labelLarge,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        // Non-presumptive disclaimer notice
        item {
          Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Cantidades estimadas por IA. Puedes ajustar los gramos de cada ingrediente antes de guardar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
              )
            }
          }
        }

        // Primary Action: Confirm to Day
        item {
          Spacer(modifier = Modifier.height(8.dp))
          Button(
            onClick = {
              onConfirmMeal(selectedMealType)
            },
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
              .fillMaxWidth()
              .height(56.dp)
              .testTag("button_add_ai_meal_to_day")
          ) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Agregar a mi día (${selectedMealType.displayName})",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold
            )
          }
        }

        item {
          Spacer(modifier = Modifier.height(24.dp))
        }
      }
    }
  }

  // Edit Ingredient Dialog
  if (editingIngredientIndex != null && scanResult != null) {
    val idx = editingIngredientIndex!!
    if (idx in scanResult.detectedIngredients.indices) {
      val item = scanResult.detectedIngredients[idx]
      EditGramsDialog(
        ingredient = item,
        onDismiss = { editingIngredientIndex = null },
        onConfirm = { newGrams ->
          onUpdateGrams(idx, newGrams)
          editingIngredientIndex = null
        }
      )
    }
  }

  if (showAddIngredientDialog) {
    AddIngredientSeparatelyDialog(
      onDismiss = { showAddIngredientDialog = false },
      onConfirm = { item, grams ->
        onAddIngredient(item, grams)
        showAddIngredientDialog = false
      }
    )
  }

  // Camera Permission Rationale Dialog
  if (showCameraPermissionRationale) {
    androidx.compose.material3.AlertDialog(
      onDismissRequest = { showCameraPermissionRationale = false },
      title = {
        Text("Permiso de Cámara", fontWeight = FontWeight.Bold)
      },
      text = {
        Text("SYVRA requiere acceso a la cámara de tu dispositivo para fotografiar tus platos y escanear calorías y macronutrientes al instante mediante la IA de Gemini. También puedes seleccionar una imagen desde tu galería.")
      },
      confirmButton = {
        Button(
          onClick = {
            showCameraPermissionRationale = false
            cameraLauncher.launch(null)
          }
        ) {
          Text("Permitir")
        }
      },
      dismissButton = {
        OutlinedButton(
          onClick = {
            showCameraPermissionRationale = false
            galleryLauncher.launch(
              PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
          }
        ) {
          Text("Usar Galería")
        }
      }
    )
  }
}

@Composable
private fun IngredientItemCard(
  item: MealIngredient,
  onEditClick: () -> Unit,
  onDeleteClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(text = item.emoji, fontSize = 22.sp)

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.foodName,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${item.grams.toInt()}g  •  ${item.calories} kcal  (P: ${item.protein.toInt()}g, C: ${item.carbs.toInt()}g, G: ${item.fat.toInt()}g)",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          Text(
            text = "Sat: %.1fg".format(item.saturatedFat),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = Color(0xFFEA580C)
          )
          Text(
            text = "•",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = if (item.transFat > 0.05f) "Trans: %.1fg ⚠️".format(item.transFat) else "0g Trans ✨",
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = if (item.transFat > 0.05f) Color(0xFFDC2626) else Color(0xFF16A34A)
          )
        }
      }

      IconButton(onClick = onEditClick) {
        Icon(
          imageVector = Icons.Default.Edit,
          contentDescription = "Editar porción",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(20.dp)
        )
      }

      IconButton(onClick = onDeleteClick) {
        Icon(
          imageVector = Icons.Default.Delete,
          contentDescription = "Eliminar",
          tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}

@Composable
fun EditGramsDialog(
  ingredient: MealIngredient,
  onDismiss: () -> Unit,
  onConfirm: (Float) -> Unit
) {
  var gramsText by remember { mutableStateOf(ingredient.grams.toInt().toString()) }

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
          text = "Ajustar porción",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = ingredient.foodName,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
          value = gramsText,
          onValueChange = { gramsText = it },
          label = { Text("Cantidad en gramos (g)") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_edit_grams")
        )

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
              val g = gramsText.toFloatOrNull() ?: ingredient.grams
              onConfirm(g)
            },
            modifier = Modifier.testTag("button_confirm_edit_grams")
          ) {
            Text("Actualizar")
          }
        }
      }
    }
  }
}

@Composable
fun AddIngredientSeparatelyDialog(
  onDismiss: () -> Unit,
  onConfirm: (FoodItem, Float) -> Unit
) {
  val allFoods = remember { FoodCatalogRepository().getAllFoods() }
  var searchQuery by remember { mutableStateOf("") }
  var selectedFood by remember {
    mutableStateOf(allFoods.firstOrNull { it.name.contains("Pollo", ignoreCase = true) } ?: allFoods.first())
  }
  var gramsText by remember { mutableStateOf("100") }

  val staples = listOf("Pollo", "Pescado", "Arroz", "Huevo", "Aguacate", "Patata", "Avena")
  val filteredFoods = remember(searchQuery) {
    if (searchQuery.isBlank()) allFoods
    else allFoods.filter { it.name.contains(searchQuery, ignoreCase = true) }
  }

  androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("dialog_add_ingredient_separately")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(20.dp)
      ) {
        Text(
          text = "Agregar ingrediente por separado",
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Selecciona el ingrediente y los gramos exactos",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Staples Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          staples.forEach { staple ->
            val match = allFoods.firstOrNull { it.name.contains(staple, ignoreCase = true) }
            val isSelected = match != null && match.id == selectedFood.id
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.clickable {
                if (match != null) {
                  selectedFood = match
                }
              }
            ) {
              Text(
                text = "${match?.emoji ?: "🥗"} $staple",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Selected Food Summary Card
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = selectedFood.emoji, fontSize = 28.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = selectedFood.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = "${selectedFood.caloriesPer100g} kcal por 100g",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Grams Input
        OutlinedTextField(
          value = gramsText,
          onValueChange = { gramsText = it.filter { ch -> ch.isDigit() || ch == '.' } },
          label = { Text("Cantidad en gramos (g)") },
          keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Number
          ),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_ingredient_grams")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Calculated live macros preview
        val factor = (gramsText.toFloatOrNull() ?: 100f) / 100f
        val calcCal = (selectedFood.caloriesPer100g * factor).toInt()
        val calcP = (selectedFood.proteinPer100g * factor).toInt()
        val calcC = (selectedFood.carbsPer100g * factor).toInt()
        val calcF = (selectedFood.fatPer100g * factor).toInt()

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "$calcCal kcal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "P: ${calcP}g", style = MaterialTheme.typography.bodySmall)
            Text(text = "C: ${calcC}g", style = MaterialTheme.typography.bodySmall)
            Text(text = "G: ${calcF}g", style = MaterialTheme.typography.bodySmall)
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
              val g = gramsText.toFloatOrNull() ?: 100f
              onConfirm(selectedFood, g)
            },
            modifier = Modifier.testTag("button_confirm_add_ingredient")
          ) {
            Text("Agregar a la comida")
          }
        }
      }
    }
  }
}

fun createFoodPlateBitmap(dishIndex: Int): Bitmap {
  val width = 640
  val height = 480
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = android.graphics.Canvas(bitmap)
  val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

  val (c1, c2, title, emoji) = when (dishIndex % 5) {
    0 -> listOf(0xFF7F1D1D.toInt(), 0xFF450A0A.toInt(), "Carne de Ternera", "🥩")
    1 -> listOf(0xFFB45309.toInt(), 0xFF78350F.toInt(), "Pechuga de Pollo", "🍗")
    2 -> listOf(0xFF0369A1.toInt(), 0xFF075985.toInt(), "Salmón al Horno", "🐟")
    3 -> listOf(0xFF991B1B.toInt(), 0xFF881337.toInt(), "Pasta con Carne", "🍝")
    else -> listOf(0xFF0D9488.toInt(), 0xFF115E59.toInt(), "Huevos y Aguacate", "🍳")
  }

  paint.shader = android.graphics.LinearGradient(
    0f, 0f, 0f, height.toFloat(),
    c1 as Int, c2 as Int,
    android.graphics.Shader.TileMode.CLAMP
  )
  canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

  paint.shader = null
  paint.textSize = 120f
  paint.textAlign = android.graphics.Paint.Align.CENTER
  canvas.drawText(emoji as String, width / 2f, height / 2f + 20f, paint)

  paint.textSize = 32f
  paint.color = android.graphics.Color.WHITE
  paint.typeface = android.graphics.Typeface.DEFAULT_BOLD
  canvas.drawText(title as String, width / 2f, height - 48f, paint)

  return bitmap
}
