package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import com.example.data.model.FoodItem
import com.example.data.model.MealIngredient
import com.example.data.model.MealType
import com.example.data.model.Recipe

@Composable
fun FoodSearchScreen(
  targetMealType: MealType,
  searchQuery: String,
  selectedCategory: String,
  categories: List<String>,
  searchResults: List<FoodItem>,
  recipes: List<Recipe> = emptyList(),
  onSearchQueryChanged: (String) -> Unit,
  onCategorySelected: (String) -> Unit,
  onAddFoodToMeal: (MealType, MealIngredient) -> Unit,
  onAddRecipeToMeal: (MealType, Recipe) -> Unit = { _, _ -> },
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  var currentTab by remember { mutableStateOf("INGREDIENTS") } // "INGREDIENTS" or "RECIPES"
  var itemToAddWithGrams by remember { mutableStateOf<FoodItem?>(null) }
  val categoryScrollState = rememberScrollState()
  val quickIngredientsScrollState = rememberScrollState()

  // Quick ingredient shortcuts for requested staples and seeds
  val quickShortcuts = listOf(
    "Girasol" to "🌻",
    "Calabaza" to "🎃",
    "Chía" to "🌱",
    "Lino" to "🌾",
    "Sésamo" to "⚪",
    "Cáñamo" to "🌿",
    "Pipas" to "🌻",
    "Amapola" to "⚫",
    "Quinoa" to "🌾",
    "Pollo" to "🍗",
    "Pescado" to "🐟",
    "Arroz" to "🍚",
    "Huevo" to "🥚",
    "Aguacate" to "🥑",
    "Avena" to "🥣",
    "Patata" to "🥔",
    "Ternera" to "🥩"
  )

  val filteredRecipes = remember(recipes, searchQuery) {
    val q = searchQuery.trim().lowercase()
    if (q.isEmpty()) {
      recipes
    } else {
      recipes.filter { r ->
        r.title.lowercase().contains(q) ||
        r.category.lowercase().contains(q) ||
        r.ingredients.any { it.foodName.lowercase().contains(q) }
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .statusBarsPadding()
      .navigationBarsPadding()
  ) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp)
    ) {
      // Top Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 12.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack, modifier = Modifier.testTag("food_search_back_button")) {
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = "Alimentos y Recetas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "Para: ${targetMealType.displayName}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Mode Switcher: Ingredientes vs Recetas
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 8.dp)
          .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
          .padding(4.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (currentTab == "INGREDIENTS") MaterialTheme.colorScheme.primary else Color.Transparent,
          modifier = Modifier
            .weight(1f)
            .clickable { currentTab = "INGREDIENTS" }
            .testTag("tab_search_ingredients")
        ) {
          Text(
            text = "🥑 Ingredientes",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (currentTab == "INGREDIENTS") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = if (currentTab == "RECIPES") MaterialTheme.colorScheme.primary else Color.Transparent,
          modifier = Modifier
            .weight(1f)
            .clickable { currentTab = "RECIPES" }
            .testTag("tab_search_recipes")
        ) {
          Text(
            text = "📖 Recetas (${filteredRecipes.size})",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (currentTab == "RECIPES") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = {
          Text(
            if (currentTab == "INGREDIENTS") "Buscar ingrediente (ej. pollo, pescado, arroz...)"
            else "Buscar receta (ej. bowl ternera, salmón, avena...)"
          )
        },
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChanged("") }) {
              Icon(imageVector = Icons.Default.Clear, contentDescription = "Borrar")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("food_search_input_field")
      )

      Spacer(modifier = Modifier.height(10.dp))

      if (currentTab == "INGREDIENTS") {
        // Quick Shortcuts for Requested Staples (Pollo, Pescado, Arroz, Huevos, etc.)
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(quickIngredientsScrollState),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          quickShortcuts.forEach { (name, emoji) ->
            val isSelected = searchQuery.equals(name, ignoreCase = true)
            Surface(
              shape = RoundedCornerShape(14.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
              ),
              modifier = Modifier.clickable {
                if (isSelected) onSearchQueryChanged("") else onSearchQueryChanged(name)
              }
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(text = emoji, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = name,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.SemiBold,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Horizontal Category Chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(categoryScrollState),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          categories.forEach { cat ->
            val isSelected = cat == selectedCategory
            Surface(
              shape = RoundedCornerShape(16.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier
                .clickable { onCategorySelected(cat) }
                .testTag("search_category_$cat")
            ) {
              Text(
                text = cat,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Results Count
        Text(
          text = "${searchResults.size} ingredientes disponibles",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // List of Foods
        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .testTag("food_search_results_list"),
          contentPadding = PaddingValues(bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          items(searchResults, key = { it.id }) { item ->
            FoodCatalogCard(
              item = item,
              onAddClick = {
                itemToAddWithGrams = item
              }
            )
          }
        }
      } else {
        // RECIPES TAB
        Text(
          text = "${filteredRecipes.size} recetas saludables con ingredientes separados",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .testTag("recipes_list"),
          contentPadding = PaddingValues(bottom = 24.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          items(filteredRecipes, key = { it.id }) { recipe ->
            RecipeCard(
              recipe = recipe,
              onAddRecipe = {
                onAddRecipeToMeal(targetMealType, recipe)
                onBack()
              },
              onAddIndividualIngredient = { ing ->
                onAddFoodToMeal(targetMealType, ing)
                onBack()
              }
            )
          }
        }
      }
    }
  }

  // Portion Selector Dialog
  if (itemToAddWithGrams != null) {
    val food = itemToAddWithGrams!!
    SelectPortionDialog(
      food = food,
      onDismiss = { itemToAddWithGrams = null },
      onConfirm = { grams ->
        val factor = grams / 100f
        val ing = MealIngredient(
          foodName = food.name,
          grams = grams,
          calories = (food.caloriesPer100g * factor).toInt(),
          protein = food.proteinPer100g * factor,
          carbs = food.carbsPer100g * factor,
          fat = food.fatPer100g * factor,
          saturatedFat = food.saturatedFatPer100g * factor,
          transFat = food.transFatPer100g * factor,
          emoji = food.emoji
        )
        onAddFoodToMeal(targetMealType, ing)
        itemToAddWithGrams = null
        onBack()
      }
    )
  }
}

@Composable
private fun FoodCatalogCard(
  item: FoodItem,
  onAddClick: () -> Unit
) {
  Card(
    shape = RoundedCornerShape(18.dp),
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
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(44.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          Text(text = item.emoji, fontSize = 20.sp)
        }
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "${item.caloriesPer100g} kcal / 100g  •  P: ${item.proteinPer100g}g  C: ${item.carbsPer100g}g  G: ${item.fatPer100g}g",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(3.dp))
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFFFF7ED)
          ) {
            Text(
              text = "Sat: ${item.saturatedFatPer100g}g",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = Color(0xFFEA580C),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (item.transFatPer100g > 0.05f) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
          ) {
            Text(
              text = if (item.transFatPer100g > 0.05f) "Trans: ${item.transFatPer100g}g ⚠️" else "0g Trans ✨",
              style = MaterialTheme.typography.labelSmall,
              fontSize = 10.sp,
              fontWeight = FontWeight.SemiBold,
              color = if (item.transFatPer100g > 0.05f) Color(0xFFDC2626) else Color(0xFF16A34A),
              modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
            )
          }
        }
      }

      Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier
          .size(36.dp)
          .clickable(onClick = onAddClick)
          .testTag("add_catalog_item_${item.id}")
      ) {
        Box(contentAlignment = Alignment.Center) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }
      }
    }
  }
}

@Composable
private fun SelectPortionDialog(
  food: FoodItem,
  onDismiss: () -> Unit,
  onConfirm: (Float) -> Unit
) {
  var gramsText by remember { mutableStateOf(food.defaultServingGrams.toInt().toString()) }

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
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = food.emoji, fontSize = 26.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = food.name,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
          value = gramsText,
          onValueChange = { gramsText = it },
          label = { Text("Porción consumida (gramos)") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("input_portion_grams")
        )

        Spacer(modifier = Modifier.height(14.dp))

        val g = gramsText.toFloatOrNull() ?: 100f
        val factor = g / 100f
        val calcCals = (food.caloriesPer100g * factor).toInt()
        val calcP = food.proteinPer100g * factor
        val calcC = food.carbsPer100g * factor
        val calcF = food.fatPer100g * factor

        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "$calcCals kcal", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "P: ${calcP.toInt()}g", style = MaterialTheme.typography.bodySmall)
            Text(text = "C: ${calcC.toInt()}g", style = MaterialTheme.typography.bodySmall)
            Text(text = "G: ${calcF.toInt()}g", style = MaterialTheme.typography.bodySmall)
          }
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
              onConfirm(g)
            },
            modifier = Modifier.testTag("confirm_portion_button")
          ) {
            Text("Agregar a la comida")
          }
        }
      }
    }
  }
}

@Composable
private fun RecipeCard(
  recipe: Recipe,
  onAddRecipe: () -> Unit,
  onAddIndividualIngredient: (MealIngredient) -> Unit
) {
  var isExpanded by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
      ) {
        Surface(
          shape = RoundedCornerShape(14.dp),
          color = MaterialTheme.colorScheme.primaryContainer,
          modifier = Modifier.size(48.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(text = recipe.emoji, fontSize = 24.sp)
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = recipe.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${recipe.category} • ${recipe.totalCalories} kcal",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = recipe.description,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Macros Bar
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = "P: ${recipe.totalProtein.toInt()}g",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = com.example.ui.theme.MacroProtein
          )
          Text(
            text = "C: ${recipe.totalCarbs.toInt()}g",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = com.example.ui.theme.MacroCarbs
          )
          Text(
            text = "G: ${recipe.totalFat.toInt()}g",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = com.example.ui.theme.MacroFat
          )
          Text(
            text = "Sat: %.1fg".format(recipe.totalSaturatedFat),
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFEA580C)
          )
          Text(
            text = if (recipe.totalTransFat > 0.05f) "Trans: %.1fg ⚠️".format(recipe.totalTransFat) else "0g Trans ✨",
            style = MaterialTheme.typography.labelSmall,
            color = if (recipe.totalTransFat > 0.05f) Color(0xFFDC2626) else Color(0xFF16A34A)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Toggle ingredients breakdown
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clickable { isExpanded = !isExpanded },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (isExpanded) "Ocultar ingredientes (${recipe.ingredients.size})" else "Ver ingredientes por separado (${recipe.ingredients.size})",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.SemiBold,
          color = MaterialTheme.colorScheme.primary
        )
        Text(
          text = if (isExpanded) "▲" else "▼",
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.primary
        )
      }

      if (isExpanded) {
        Spacer(modifier = Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          recipe.ingredients.forEach { ing ->
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                  Text(text = ing.emoji, fontSize = 16.sp)
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = ing.foodName,
                      style = MaterialTheme.typography.bodySmall,
                      fontWeight = FontWeight.Medium
                    )
                    Text(
                      text = "${ing.grams.toInt()}g • ${ing.calories} kcal • P: ${ing.protein.toInt()}g C: ${ing.carbs.toInt()}g G: ${ing.fat.toInt()}g",
                      style = MaterialTheme.typography.labelSmall,
                      fontSize = 10.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }
                }

                androidx.compose.material3.TextButton(
                  onClick = { onAddIndividualIngredient(ing) },
                  contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                ) {
                  Text(text = "+ Agregar solo este", style = MaterialTheme.typography.labelSmall)
                }
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Button(
        onClick = onAddRecipe,
        shape = RoundedCornerShape(14.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.fillMaxWidth()
      ) {
        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "Agregar receta completa",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

