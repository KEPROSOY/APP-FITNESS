package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.MealIngredient
import com.example.data.model.MealType
import org.json.JSONArray
import org.json.JSONObject

class Converters {

  @TypeConverter
  fun fromMealType(type: MealType): String = type.name

  @TypeConverter
  fun toMealType(value: String): MealType = try {
    MealType.valueOf(value)
  } catch (e: Exception) {
    MealType.COMIDA
  }

  @TypeConverter
  fun fromIngredientsList(list: List<MealIngredient>?): String {
    if (list.isNullOrEmpty()) return "[]"
    val array = JSONArray()
    for (item in list) {
      val obj = JSONObject()
      obj.put("id", item.id)
      obj.put("foodName", item.foodName)
      obj.put("grams", item.grams.toDouble())
      obj.put("calories", item.calories)
      obj.put("protein", item.protein.toDouble())
      obj.put("carbs", item.carbs.toDouble())
      obj.put("fat", item.fat.toDouble())
      obj.put("saturatedFat", item.saturatedFat.toDouble())
      obj.put("transFat", item.transFat.toDouble())
      obj.put("emoji", item.emoji)
      array.put(obj)
    }
    return array.toString()
  }

  @TypeConverter
  fun toIngredientsList(json: String?): List<MealIngredient> {
    if (json.isNullOrBlank()) return emptyList()
    val result = mutableListOf<MealIngredient>()
    try {
      val array = JSONArray(json)
      for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        result.add(
          MealIngredient(
            id = obj.optString("id", java.util.UUID.randomUUID().toString()),
            foodName = obj.optString("foodName", "Alimento"),
            grams = obj.optDouble("grams", 100.0).toFloat(),
            calories = obj.optInt("calories", 0),
            protein = obj.optDouble("protein", 0.0).toFloat(),
            carbs = obj.optDouble("carbs", 0.0).toFloat(),
            fat = obj.optDouble("fat", 0.0).toFloat(),
            saturatedFat = obj.optDouble("saturatedFat", 0.0).toFloat(),
            transFat = obj.optDouble("transFat", 0.0).toFloat(),
            emoji = obj.optString("emoji", "🥗")
          )
        )
      }
    } catch (e: Exception) {
      // fallback
    }
    return result
  }

  @TypeConverter
  fun fromAestheticCategory(category: com.example.data.model.AestheticMuscleCategory): String = category.name

  @TypeConverter
  fun toAestheticCategory(value: String): com.example.data.model.AestheticMuscleCategory = try {
    com.example.data.model.AestheticMuscleCategory.valueOf(value)
  } catch (e: Exception) {
    com.example.data.model.AestheticMuscleCategory.CHEST_UPPER_LOWER
  }

  @TypeConverter
  fun fromSetType(type: com.example.data.model.SetType): String = type.name

  @TypeConverter
  fun toSetType(value: String): com.example.data.model.SetType = try {
    com.example.data.model.SetType.valueOf(value)
  } catch (e: Exception) {
    com.example.data.model.SetType.NORMAL
  }
}
