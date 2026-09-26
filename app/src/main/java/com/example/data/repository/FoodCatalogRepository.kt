package com.example.data.repository

import com.example.data.model.FoodItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FoodCatalogRepository {

  private val initialFoods = listOf(
    // ==========================================
    // 1. PROTEÍNAS (Variedad limpia: aves, pescados, vegetales, lácteos)
    // ==========================================
    FoodItem(
      id = "p1",
      name = "Pechuga de pollo a la plancha",
      category = "Proteínas",
      caloriesPer100g = 165,
      proteinPer100g = 31.0f,
      carbsPer100g = 0.0f,
      fatPer100g = 3.6f,
      saturatedFatPer100g = 1.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g",
      emoji = "🍗"
    ),
    FoodItem(
      id = "p2",
      name = "Salmón salvaje del Atlántico",
      category = "Proteínas",
      caloriesPer100g = 208,
      proteinPer100g = 20.4f,
      carbsPer100g = 0.0f,
      fatPer100g = 13.0f,
      saturatedFatPer100g = 2.1f, // Grasa saludable rica en Omega-3
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g",
      emoji = "🍣"
    ),
    FoodItem(
      id = "p3",
      name = "Atún claro al natural en lata",
      category = "Proteínas",
      caloriesPer100g = 116,
      proteinPer100g = 26.0f,
      carbsPer100g = 0.0f,
      fatPer100g = 1.0f,
      saturatedFatPer100g = 0.2f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 100f,
      servingUnit = "g (1 lata)",
      emoji = "🐟"
    ),
    FoodItem(
      id = "p4",
      name = "Filete de merluza blanca al vapor",
      category = "Proteínas",
      caloriesPer100g = 85,
      proteinPer100g = 18.2f,
      carbsPer100g = 0.0f,
      fatPer100g = 0.8f,
      saturatedFatPer100g = 0.2f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g",
      emoji = "🐟"
    ),
    FoodItem(
      id = "p5",
      name = "Huevos enteros camperos",
      category = "Proteínas",
      caloriesPer100g = 155,
      proteinPer100g = 13.0f,
      carbsPer100g = 1.1f,
      fatPer100g = 11.0f,
      saturatedFatPer100g = 3.3f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 60f,
      servingUnit = "g (1 ud)",
      emoji = "🥚"
    ),
    FoodItem(
      id = "p6",
      name = "Claras de huevo pasteurizadas",
      category = "Proteínas",
      caloriesPer100g = 52,
      proteinPer100g = 11.0f,
      carbsPer100g = 0.7f,
      fatPer100g = 0.2f,
      saturatedFatPer100g = 0.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 120f,
      servingUnit = "ml",
      emoji = "🍳"
    ),
    FoodItem(
      id = "p7",
      name = "Tofu firme ecológico",
      category = "Proteínas",
      caloriesPer100g = 83,
      proteinPer100g = 10.5f,
      carbsPer100g = 2.0f,
      fatPer100g = 5.0f,
      saturatedFatPer100g = 0.7f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 125f,
      servingUnit = "g",
      emoji = "🧊"
    ),
    FoodItem(
      id = "p8",
      name = "Lentejas pardinas cocidas",
      category = "Proteínas",
      caloriesPer100g = 116,
      proteinPer100g = 9.0f,
      carbsPer100g = 20.0f,
      fatPer100g = 0.4f,
      saturatedFatPer100g = 0.1f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g (1 taza)",
      emoji = "🍲"
    ),
    FoodItem(
      id = "p9",
      name = "Yogur griego 0% natural",
      category = "Proteínas",
      caloriesPer100g = 59,
      proteinPer100g = 10.3f,
      carbsPer100g = 3.6f,
      fatPer100g = 0.4f,
      saturatedFatPer100g = 0.1f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 125f,
      servingUnit = "g (1 vaso)",
      emoji = "🥛"
    ),
    FoodItem(
      id = "p10",
      name = "Ternera magra corte limpio",
      category = "Proteínas",
      caloriesPer100g = 143,
      proteinPer100g = 22.0f,
      carbsPer100g = 0.0f,
      fatPer100g = 5.8f,
      saturatedFatPer100g = 2.1f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g",
      emoji = "🥩"
    ),

    // ==========================================
    // 2. CARBOHIDRATOS (Energía limpia, fibra y saciedad)
    // ==========================================
    FoodItem(
      id = "c1",
      name = "Avena integral en copos",
      category = "Carbohidratos",
      caloriesPer100g = 389,
      proteinPer100g = 16.9f,
      carbsPer100g = 66.3f,
      fatPer100g = 6.9f,
      saturatedFatPer100g = 1.2f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 50f,
      servingUnit = "g (1 tazón)",
      emoji = "🥣"
    ),
    FoodItem(
      id = "c2",
      name = "Quinoa real cocida",
      category = "Carbohidratos",
      caloriesPer100g = 120,
      proteinPer100g = 4.4f,
      carbsPer100g = 21.3f,
      fatPer100g = 1.9f,
      saturatedFatPer100g = 0.2f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g",
      emoji = "🌾"
    ),
    FoodItem(
      id = "c3",
      name = "Boniato / Batata dulce asada",
      category = "Carbohidratos",
      caloriesPer100g = 90,
      proteinPer100g = 2.0f,
      carbsPer100g = 20.7f,
      fatPer100g = 0.1f,
      saturatedFatPer100g = 0.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g",
      emoji = "🍠"
    ),
    FoodItem(
      id = "c4",
      name = "Arroz integral jazmín",
      category = "Carbohidratos",
      caloriesPer100g = 111,
      proteinPer100g = 2.6f,
      carbsPer100g = 23.0f,
      fatPer100g = 0.9f,
      saturatedFatPer100g = 0.2f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g",
      emoji = "🍚"
    ),
    FoodItem(
      id = "c5",
      name = "Garbanzos cocidos con fibra",
      category = "Carbohidratos",
      caloriesPer100g = 128,
      proteinPer100g = 8.9f,
      carbsPer100g = 27.4f,
      fatPer100g = 2.6f,
      saturatedFatPer100g = 0.3f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g",
      emoji = "🫘"
    ),
    FoodItem(
      id = "c6",
      name = "Pan 100% de centeno integral",
      category = "Carbohidratos",
      caloriesPer100g = 240,
      proteinPer100g = 9.0f,
      carbsPer100g = 43.0f,
      fatPer100g = 2.5f,
      saturatedFatPer100g = 0.4f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 45f,
      servingUnit = "g (1 rebanada)",
      emoji = "🍞"
    ),
    FoodItem(
      id = "c7",
      name = "Plátano maduro potásico",
      category = "Carbohidratos",
      caloriesPer100g = 89,
      proteinPer100g = 1.1f,
      carbsPer100g = 22.8f,
      fatPer100g = 0.3f,
      saturatedFatPer100g = 0.1f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 120f,
      servingUnit = "g (1 pieza)",
      emoji = "🍌"
    ),
    FoodItem(
      id = "c8",
      name = "Arándanos silvestres antioxidantes",
      category = "Carbohidratos",
      caloriesPer100g = 57,
      proteinPer100g = 0.7f,
      carbsPer100g = 14.5f,
      fatPer100g = 0.3f,
      saturatedFatPer100g = 0.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 100f,
      servingUnit = "g (1 tazón)",
      emoji = "🫐"
    ),
    FoodItem(
      id = "c9",
      name = "Manzana fuji crujiente",
      category = "Carbohidratos",
      caloriesPer100g = 52,
      proteinPer100g = 0.3f,
      carbsPer100g = 13.8f,
      fatPer100g = 0.2f,
      saturatedFatPer100g = 0.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 150f,
      servingUnit = "g (1 unidad)",
      emoji = "🍎"
    ),

    // ==========================================
    // 3. GRASAS (Buenas / Saludables / Insaturadas / Omega-3)
    // ==========================================
    FoodItem(
      id = "g1",
      name = "Aguacate Hass fresco",
      category = "Grasas",
      caloriesPer100g = 160,
      proteinPer100g = 2.0f,
      carbsPer100g = 8.5f,
      fatPer100g = 14.7f, // Grasa monoinsaturada cardiosaludable
      saturatedFatPer100g = 2.1f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 75f,
      servingUnit = "g (medio)",
      emoji = "🥑"
    ),
    FoodItem(
      id = "g2",
      name = "Aceite de Oliva Virgen Extra (AOVE)",
      category = "Grasas",
      caloriesPer100g = 884,
      proteinPer100g = 0.0f,
      carbsPer100g = 0.0f,
      fatPer100g = 100.0f, // Ácido oleico monoinsaturado
      saturatedFatPer100g = 14.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 12f,
      servingUnit = "g (1 cda)",
      emoji = "🫒"
    ),
    FoodItem(
      id = "g3",
      name = "Nueces de California peladas",
      category = "Grasas",
      caloriesPer100g = 654,
      proteinPer100g = 15.2f,
      carbsPer100g = 13.7f,
      fatPer100g = 65.2f, // Rica en Omega-3 vegetal (ALA)
      saturatedFatPer100g = 6.1f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 30f,
      servingUnit = "g (puñado)",
      emoji = "🌰"
    ),
    FoodItem(
      id = "g4",
      name = "Almendras crudas naturales",
      category = "Grasas",
      caloriesPer100g = 579,
      proteinPer100g = 21.2f,
      carbsPer100g = 21.6f,
      fatPer100g = 49.9f,
      saturatedFatPer100g = 3.8f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 30f,
      servingUnit = "g (puñado)",
      emoji = "🥜"
    ),
    FoodItem(
      id = "g5",
      name = "Semillas de chía crudas",
      category = "Grasas",
      caloriesPer100g = 486,
      proteinPer100g = 16.5f,
      carbsPer100g = 42.1f,
      fatPer100g = 30.7f, // Alta en Omega-3 y fibra soluble
      saturatedFatPer100g = 3.3f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 15f,
      servingUnit = "g (1 cda)",
      emoji = "🌱"
    ),
    FoodItem(
      id = "g6",
      name = "Mantequilla pura de cacahuete 100%",
      category = "Grasas",
      caloriesPer100g = 588,
      proteinPer100g = 25.0f,
      carbsPer100g = 20.0f,
      fatPer100g = 50.0f,
      saturatedFatPer100g = 7.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 20f,
      servingUnit = "g (1 cda)",
      emoji = "🥜"
    ),
    FoodItem(
      id = "g7",
      name = "Aceitunas verdes de mesa",
      category = "Grasas",
      caloriesPer100g = 145,
      proteinPer100g = 1.0f,
      carbsPer100g = 3.8f,
      fatPer100g = 15.3f,
      saturatedFatPer100g = 2.2f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 40f,
      servingUnit = "g",
      emoji = "🫒"
    ),
    FoodItem(
      id = "g8",
      name = "Semillas de girasol peladas (pipas)",
      category = "Semillas",
      caloriesPer100g = 584,
      proteinPer100g = 20.8f,
      carbsPer100g = 20.0f,
      fatPer100g = 51.5f, // Alta en vitamina E y grasas poliinsaturadas
      saturatedFatPer100g = 4.5f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 25f,
      servingUnit = "g (1 puñado)",
      emoji = "🌻"
    ),
    FoodItem(
      id = "g9",
      name = "Semillas de calabaza (pepitas)",
      category = "Semillas",
      caloriesPer100g = 559,
      proteinPer100g = 30.2f, // Muy rica en proteína vegetal, zinc y magnesio
      carbsPer100g = 10.7f,
      fatPer100g = 49.0f,
      saturatedFatPer100g = 8.7f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 25f,
      servingUnit = "g (1 puñado)",
      emoji = "🎃"
    ),
    FoodItem(
      id = "g10",
      name = "Semillas de lino dorado (linaza molida)",
      category = "Semillas",
      caloriesPer100g = 534,
      proteinPer100g = 18.3f,
      carbsPer100g = 28.9f,
      fatPer100g = 42.2f, // Riquísima en Omega-3 vegetal (ALA) y lignanos
      saturatedFatPer100g = 3.7f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 15f,
      servingUnit = "g (1 cda)",
      emoji = "🌾"
    ),
    FoodItem(
      id = "g11",
      name = "Semillas de sésamo tostado (ajonjolí)",
      category = "Semillas",
      caloriesPer100g = 573,
      proteinPer100g = 17.7f,
      carbsPer100g = 23.4f,
      fatPer100g = 49.7f, // Alta biodisponibilidad de calcio y sesamina
      saturatedFatPer100g = 7.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 15f,
      servingUnit = "g (1 cda)",
      emoji = "⚪"
    ),
    FoodItem(
      id = "g12",
      name = "Semillas de cáñamo peladas (hemp seeds)",
      category = "Semillas",
      caloriesPer100g = 553,
      proteinPer100g = 31.6f, // Proteína vegetal completa con 9 aminoácidos esenciales
      carbsPer100g = 8.7f,
      fatPer100g = 48.8f,
      saturatedFatPer100g = 4.6f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 20f,
      servingUnit = "g (1 cda colmada)",
      emoji = "🌿"
    ),
    FoodItem(
      id = "g13",
      name = "Semillas de amapola naturales",
      category = "Semillas",
      caloriesPer100g = 525,
      proteinPer100g = 18.0f,
      carbsPer100g = 28.1f,
      fatPer100g = 41.6f,
      saturatedFatPer100g = 4.5f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 10f,
      servingUnit = "g (1 cdta)",
      emoji = "⚫"
    ),
    FoodItem(
      id = "g14",
      name = "Mix de 4 semillas (Girasol, Calabaza, Chía y Lino)",
      category = "Semillas",
      caloriesPer100g = 540,
      proteinPer100g = 21.0f,
      carbsPer100g = 22.0f,
      fatPer100g = 44.0f,
      saturatedFatPer100g = 5.0f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 25f,
      servingUnit = "g (puñado)",
      emoji = "🥗"
    ),
    FoodItem(
      id = "g15",
      name = "Semillas de girasol tostadas con cáscara (pipas clásicas)",
      category = "Semillas",
      caloriesPer100g = 582,
      proteinPer100g = 19.3f,
      carbsPer100g = 24.1f,
      fatPer100g = 49.8f,
      saturatedFatPer100g = 5.2f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 30f,
      servingUnit = "g (con cáscara)",
      emoji = "🌻"
    ),
    FoodItem(
      id = "g16",
      name = "Semillas de sésamo negro (ajonjolí negro)",
      category = "Semillas",
      caloriesPer100g = 565,
      proteinPer100g = 17.0f,
      carbsPer100g = 25.7f,
      fatPer100g = 48.0f,
      saturatedFatPer100g = 6.8f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 15f,
      servingUnit = "g (1 cda)",
      emoji = "⚫"
    ),
    FoodItem(
      id = "g17",
      name = "Semillas de quinoa inflada crujiente",
      category = "Semillas",
      caloriesPer100g = 372,
      proteinPer100g = 14.1f,
      carbsPer100g = 64.2f,
      fatPer100g = 6.1f,
      saturatedFatPer100g = 0.7f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 20f,
      servingUnit = "g (cereal topping)",
      emoji = "🌾"
    ),
    FoodItem(
      id = "g18",
      name = "Semillas de sandía tostadas",
      category = "Semillas",
      caloriesPer100g = 557,
      proteinPer100g = 28.3f,
      carbsPer100g = 15.3f,
      fatPer100g = 47.4f,
      saturatedFatPer100g = 9.8f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 20f,
      servingUnit = "g",
      emoji = "🍉"
    ),
    FoodItem(
      id = "g19",
      name = "Semillas de albahaca (sabja seeds)",
      category = "Semillas",
      caloriesPer100g = 440,
      proteinPer100g = 14.8f,
      carbsPer100g = 42.0f,
      fatPer100g = 24.0f,
      saturatedFatPer100g = 2.5f,
      transFatPer100g = 0.0f,
      defaultServingGrams = 10f,
      servingUnit = "g (1 cda para hidratar)",
      emoji = "🌱"
    ),

    // ==========================================
    // 4. GRASAS MALAS / TRANS (Alimentos para control y alerta de salud)
    // ==========================================
    FoodItem(
      id = "m1",
      name = "Croissant hojaldrado con margarina",
      category = "Grasas Malas / Trans",
      caloriesPer100g = 406,
      proteinPer100g = 8.2f,
      carbsPer100g = 45.8f,
      fatPer100g = 21.0f,
      saturatedFatPer100g = 12.5f, // Grasa Saturada Elevada
      transFatPer100g = 2.8f,     // ⚠️ ALERTA TRANS: Grasas hidrogenadas
      defaultServingGrams = 60f,
      servingUnit = "g (1 ud)",
      emoji = "🥐"
    ),
    FoodItem(
      id = "m2",
      name = "Galletas rellenas ultraprocesadas",
      category = "Grasas Malas / Trans",
      caloriesPer100g = 485,
      proteinPer100g = 5.3f,
      carbsPer100g = 68.0f,
      fatPer100g = 21.5f,
      saturatedFatPer100g = 10.8f,
      transFatPer100g = 3.2f,     // ⚠️ ALERTA TRANS
      defaultServingGrams = 40f,
      servingUnit = "g (4 uds)",
      emoji = "🍪"
    ),
    FoodItem(
      id = "m3",
      name = "Patatas fritas de bolsa comerciales",
      category = "Grasas Malas / Trans",
      caloriesPer100g = 536,
      proteinPer100g = 7.0f,
      carbsPer100g = 53.0f,
      fatPer100g = 34.0f,
      saturatedFatPer100g = 8.5f,
      transFatPer100g = 1.5f,     // ⚠️ ALERTA TRANS: Aceites fritos recalentados
      defaultServingGrams = 45f,
      servingUnit = "g (1 bolsa peq)",
      emoji = "🍟"
    ),
    FoodItem(
      id = "m4",
      name = "Rosquilla / Donut glaseado industrial",
      category = "Grasas Malas / Trans",
      caloriesPer100g = 452,
      proteinPer100g = 4.9f,
      carbsPer100g = 51.0f,
      fatPer100g = 25.0f,
      saturatedFatPer100g = 13.0f,
      transFatPer100g = 2.4f,     // ⚠️ ALERTA TRANS
      defaultServingGrams = 65f,
      servingUnit = "g (1 unidad)",
      emoji = "🍩"
    ),
    FoodItem(
      id = "m5",
      name = "Bacon / Panceta frita crujiente",
      category = "Grasas Malas / Trans",
      caloriesPer100g = 541,
      proteinPer100g = 37.0f,
      carbsPer100g = 1.4f,
      fatPer100g = 42.0f,
      saturatedFatPer100g = 15.2f, // ⚠️ Muy alta en grasa saturada
      transFatPer100g = 0.4f,
      defaultServingGrams = 30f,
      servingUnit = "g (2 lonchas)",
      emoji = "🥓"
    ),
    FoodItem(
      id = "m6",
      name = "Hamburguesa fast-food doble queso",
      category = "Grasas Malas / Trans",
      caloriesPer100g = 295,
      proteinPer100g = 17.0f,
      carbsPer100g = 24.0f,
      fatPer100g = 18.0f,
      saturatedFatPer100g = 9.5f,
      transFatPer100g = 1.2f,     // ⚠️ ALERTA TRANS
      defaultServingGrams = 200f,
      servingUnit = "g (1 burger)",
      emoji = "🍔"
    )
  )

  private val _catalog = MutableStateFlow(initialFoods)
  val catalog = _catalog.asStateFlow()

  val categories = listOf(
    "Todos",
    "Proteínas",
    "Carbohidratos",
    "Grasas",
    "Semillas",
    "Grasas Malas / Trans"
  )

  val predefinedRecipes: List<com.example.data.model.Recipe> = listOf(
    com.example.data.model.Recipe(
      id = "rec_1",
      title = "Plato Fitness de Pollo, Arroz y Brócoli",
      description = "Combinación clásica hiperproteica ideal para definición o mantenimiento muscular limpio.",
      category = "Fitness",
      mealType = com.example.data.model.MealType.COMIDA,
      emoji = "🍗",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Pechuga de pollo a la plancha",
          grams = 150f,
          calories = 248,
          protein = 46.5f,
          carbs = 0.0f,
          fat = 5.4f,
          saturatedFat = 1.5f,
          transFat = 0.0f,
          emoji = "🍗"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Arroz blanco cocido",
          grams = 150f,
          calories = 195,
          protein = 4.0f,
          carbs = 42.3f,
          fat = 0.5f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🍚"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Brócoli al vapor",
          grams = 100f,
          calories = 35,
          protein = 2.4f,
          carbs = 7.2f,
          fat = 0.4f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🥦"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aceite de oliva virgen extra",
          grams = 5f,
          calories = 44,
          protein = 0.0f,
          carbs = 0.0f,
          fat = 5.0f,
          saturatedFat = 0.7f,
          transFat = 0.0f,
          emoji = "🫒"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_2",
      title = "Salmón Salvaje con Patata y Aguacate",
      description = "Carga de Omega-3 cardiosaludable, carbohidratos saciantes y grasas monoinsaturadas.",
      category = "Almuerzos",
      mealType = com.example.data.model.MealType.COMIDA,
      emoji = "🍣",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Salmón salvaje del Atlántico",
          grams = 150f,
          calories = 312,
          protein = 30.6f,
          carbs = 0.0f,
          fat = 19.5f,
          saturatedFat = 3.1f,
          transFat = 0.0f,
          emoji = "🍣"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Patata cocida",
          grams = 180f,
          calories = 157,
          protein = 3.4f,
          carbs = 36.2f,
          fat = 0.2f,
          saturatedFat = 0.0f,
          transFat = 0.0f,
          emoji = "🥔"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aguacate Hass",
          grams = 50f,
          calories = 80,
          protein = 1.0f,
          carbs = 4.2f,
          fat = 7.3f,
          saturatedFat = 1.0f,
          transFat = 0.0f,
          emoji = "🥑"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_3",
      title = "Bowl Energético de Avena con Plátano",
      description = "Desayuno con carbohidratos de absorción lenta, fibra prebiótica y magnesio.",
      category = "Desayunos",
      mealType = com.example.data.model.MealType.DESAYUNO,
      emoji = "🥣",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Avena integral en copos",
          grams = 60f,
          calories = 233,
          protein = 10.1f,
          carbs = 39.8f,
          fat = 4.1f,
          saturatedFat = 0.7f,
          transFat = 0.0f,
          emoji = "🥣"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Plátano maduro",
          grams = 100f,
          calories = 89,
          protein = 1.1f,
          carbs = 22.8f,
          fat = 0.3f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🍌"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Almendras naturales",
          grams = 15f,
          calories = 87,
          protein = 3.2f,
          carbs = 3.2f,
          fat = 7.5f,
          saturatedFat = 0.6f,
          transFat = 0.0f,
          emoji = "🥜"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_4",
      title = "Desayuno Proteico de Huevos y Pan Integral",
      description = "Huevos camperos ricos en colina con tostadas de trigo entero y aguacate.",
      category = "Desayunos",
      mealType = com.example.data.model.MealType.DESAYUNO,
      emoji = "🍳",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Huevos camperos (2 uds)",
          grams = 120f,
          calories = 186,
          protein = 15.6f,
          carbs = 1.3f,
          fat = 13.2f,
          saturatedFat = 4.0f,
          transFat = 0.0f,
          emoji = "🥚"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Pan 100% integral",
          grams = 70f,
          calories = 173,
          protein = 9.1f,
          carbs = 28.7f,
          fat = 2.4f,
          saturatedFat = 0.5f,
          transFat = 0.0f,
          emoji = "🍞"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aguacate Hass",
          grams = 30f,
          calories = 48,
          protein = 0.6f,
          carbs = 2.5f,
          fat = 4.4f,
          saturatedFat = 0.6f,
          transFat = 0.0f,
          emoji = "🥑"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_5",
      title = "Poke Bowl de Atún con Arroz y Aguacate",
      description = "Plato rápido fresco y alto en proteína de asimilación rápida para después de entrenar.",
      category = "Fitness",
      mealType = com.example.data.model.MealType.COMIDA,
      emoji = "🐟",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Atún claro al natural en lata",
          grams = 120f,
          calories = 139,
          protein = 31.2f,
          carbs = 0.0f,
          fat = 1.2f,
          saturatedFat = 0.2f,
          transFat = 0.0f,
          emoji = "🐟"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Arroz blanco cocido",
          grams = 150f,
          calories = 195,
          protein = 4.0f,
          carbs = 42.3f,
          fat = 0.5f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🍚"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aguacate Hass",
          grams = 50f,
          calories = 80,
          protein = 1.0f,
          carbs = 4.2f,
          fat = 7.3f,
          saturatedFat = 1.0f,
          transFat = 0.0f,
          emoji = "🥑"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_6",
      title = "Ensalada Mediterránea de Lentejas",
      description = "Proteína vegetal, hierro y fibra con queso fresco y aceite de oliva virgen.",
      category = "Almuerzos",
      mealType = com.example.data.model.MealType.COMIDA,
      emoji = "🥗",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Lentejas pardinas cocidas",
          grams = 160f,
          calories = 185,
          protein = 14.4f,
          carbs = 32.0f,
          fat = 0.6f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🍲"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Tomate fresco",
          grams = 100f,
          calories = 18,
          protein = 0.9f,
          carbs = 3.9f,
          fat = 0.2f,
          saturatedFat = 0.0f,
          transFat = 0.0f,
          emoji = "🍅"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aceite de oliva virgen extra",
          grams = 8f,
          calories = 71,
          protein = 0.0f,
          carbs = 0.0f,
          fat = 8.0f,
          saturatedFat = 1.1f,
          transFat = 0.0f,
          emoji = "🫒"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_7",
      title = "Yogur Griego 0% con Fruta y Nueces",
      description = "Snack o postre proteico sin grasas añadidas con probióticos naturales.",
      category = "Snacks",
      mealType = com.example.data.model.MealType.SNACK,
      emoji = "🥛",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Yogur griego 0% natural",
          grams = 200f,
          calories = 118,
          protein = 20.6f,
          carbs = 7.2f,
          fat = 0.8f,
          saturatedFat = 0.2f,
          transFat = 0.0f,
          emoji = "🥛"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Nueces peladas",
          grams = 15f,
          calories = 98,
          protein = 2.3f,
          carbs = 2.0f,
          fat = 9.8f,
          saturatedFat = 0.9f,
          transFat = 0.0f,
          emoji = "🥜"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_8",
      title = "Cena Ligera de Merluza con Patatas",
      description = "Cena digestiva, baja en grasas saturadas, perfecta para conciliar el descanso.",
      category = "Cenas Rápidas",
      mealType = com.example.data.model.MealType.CENA,
      emoji = "🍲",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Filete de merluza blanca al vapor",
          grams = 180f,
          calories = 153,
          protein = 32.8f,
          carbs = 0.0f,
          fat = 1.4f,
          saturatedFat = 0.3f,
          transFat = 0.0f,
          emoji = "🐟"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Patata cocida",
          grams = 150f,
          calories = 130,
          protein = 2.8f,
          carbs = 30.1f,
          fat = 0.1f,
          saturatedFat = 0.0f,
          transFat = 0.0f,
          emoji = "🥔"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aceite de oliva virgen extra",
          grams = 5f,
          calories = 44,
          protein = 0.0f,
          carbs = 0.0f,
          fat = 5.0f,
          saturatedFat = 0.7f,
          transFat = 0.0f,
          emoji = "🫒"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_9",
      title = "Bowl de Ternera Magra con Arroz Jazmín y Brócoli",
      description = "Plato completo para ganancia muscular, rico en hierro hemínico, zinc y carbohidratos limpios.",
      category = "Almuerzos",
      mealType = com.example.data.model.MealType.COMIDA,
      emoji = "🥩",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Ternera magra picada o en tiras",
          grams = 150f,
          calories = 195,
          protein = 31.5f,
          carbs = 0.0f,
          fat = 7.5f,
          saturatedFat = 2.8f,
          transFat = 0.0f,
          emoji = "🥩"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Arroz blanco cocido",
          grams = 180f,
          calories = 234,
          protein = 4.5f,
          carbs = 50.4f,
          fat = 0.6f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🍚"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Brócoli fresco al vapor",
          grams = 100f,
          calories = 34,
          protein = 2.8f,
          carbs = 6.6f,
          fat = 0.4f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🥦"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aceite de oliva virgen extra",
          grams = 5f,
          calories = 44,
          protein = 0.0f,
          carbs = 0.0f,
          fat = 5.0f,
          saturatedFat = 0.7f,
          transFat = 0.0f,
          emoji = "🫒"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_10",
      title = "Pechuga de Pavo con Batata Asada y Espárragos",
      description = "Cena ligera de digestión rápida con betacarotenos y proteína magra hipocalórica.",
      category = "Cenas Rápidas",
      mealType = com.example.data.model.MealType.CENA,
      emoji = "🍗",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Pechuga de pollo a la plancha",
          grams = 160f,
          calories = 176,
          protein = 38.4f,
          carbs = 0.0f,
          fat = 2.4f,
          saturatedFat = 0.6f,
          transFat = 0.0f,
          emoji = "🍗"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Patata cocida",
          grams = 150f,
          calories = 129,
          protein = 2.4f,
          carbs = 30.0f,
          fat = 0.2f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🥔"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aceite de oliva virgen extra",
          grams = 5f,
          calories = 44,
          protein = 0.0f,
          carbs = 0.0f,
          fat = 5.0f,
          saturatedFat = 0.7f,
          transFat = 0.0f,
          emoji = "🫒"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_11",
      title = "Porridge de Avena con Proteína, Arándanos y Chía",
      description = "Desayuno saciante de liberación lenta con fibra soluble beta-glucano y antioxidantes.",
      category = "Desayunos",
      mealType = com.example.data.model.MealType.DESAYUNO,
      emoji = "🥣",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Copos de avena integral",
          grams = 50f,
          calories = 189,
          protein = 6.8f,
          carbs = 33.1f,
          fat = 3.4f,
          saturatedFat = 0.6f,
          transFat = 0.0f,
          emoji = "🥣"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Yogur griego 0% natural",
          grams = 150f,
          calories = 89,
          protein = 15.5f,
          carbs = 5.4f,
          fat = 0.6f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🥛"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Arándanos frescos",
          grams = 60f,
          calories = 34,
          protein = 0.4f,
          carbs = 8.7f,
          fat = 0.2f,
          saturatedFat = 0.0f,
          transFat = 0.0f,
          emoji = "🫐"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_12",
      title = "Tacos Saludables de Pollo con Guacamole Fresco",
      description = "Plato mexicano adaptado, alto en proteínas y grasas monoinsaturadas cardiosaludables.",
      category = "Cenas Rápidas",
      mealType = com.example.data.model.MealType.CENA,
      emoji = "🌮",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Pechuga de pollo a la plancha",
          grams = 140f,
          calories = 231,
          protein = 43.4f,
          carbs = 0.0f,
          fat = 5.0f,
          saturatedFat = 1.4f,
          transFat = 0.0f,
          emoji = "🍗"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aguacate Hass",
          grams = 50f,
          calories = 80,
          protein = 1.0f,
          carbs = 4.2f,
          fat = 7.3f,
          saturatedFat = 1.0f,
          transFat = 0.0f,
          emoji = "🥑"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Tomate fresco",
          grams = 80f,
          calories = 14,
          protein = 0.7f,
          carbs = 3.1f,
          fat = 0.2f,
          saturatedFat = 0.0f,
          transFat = 0.0f,
          emoji = "🍅"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_13",
      title = "Revuelto Proteico con Tostada de Pan Integral",
      description = "Combinación perfecta de clara y yema para un perfil completo de aminoácidos y colina.",
      category = "Desayunos",
      mealType = com.example.data.model.MealType.DESAYUNO,
      emoji = "🍳",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Claras de huevo pasteurizadas",
          grams = 150f,
          calories = 65,
          protein = 14.0f,
          carbs = 0.9f,
          fat = 0.2f,
          saturatedFat = 0.0f,
          transFat = 0.0f,
          emoji = "🍳"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Huevos enteros camperos",
          grams = 60f,
          calories = 93,
          protein = 7.8f,
          carbs = 0.7f,
          fat = 6.6f,
          saturatedFat = 2.0f,
          transFat = 0.0f,
          emoji = "🥚"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Pan 100% integral",
          grams = 60f,
          calories = 148,
          protein = 7.8f,
          carbs = 24.6f,
          fat = 2.0f,
          saturatedFat = 0.4f,
          transFat = 0.0f,
          emoji = "🍞"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_14",
      title = "Tartar Fresco de Salmón con Aguacate",
      description = "Plato gourmet rico en ácidos grasos poliinsaturados Omega-3 EPA y DHA.",
      category = "Cenas Rápidas",
      mealType = com.example.data.model.MealType.CENA,
      emoji = "🍣",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Salmón salvaje del Atlántico",
          grams = 140f,
          calories = 291,
          protein = 28.5f,
          carbs = 0.0f,
          fat = 18.2f,
          saturatedFat = 2.9f,
          transFat = 0.0f,
          emoji = "🍣"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Aguacate Hass",
          grams = 40f,
          calories = 64,
          protein = 0.8f,
          carbs = 3.4f,
          fat = 5.9f,
          saturatedFat = 0.8f,
          transFat = 0.0f,
          emoji = "🥑"
        )
      )
    ),
    com.example.data.model.Recipe(
      id = "rec_15",
      title = "Batido Energético de Plátano y Crema de Cacahuete",
      description = "Snack pre o post entreno para reponer glucógeno y potasio con energía sostenida.",
      category = "Snacks",
      mealType = com.example.data.model.MealType.SNACK,
      emoji = "🍌",
      ingredients = listOf(
        com.example.data.model.MealIngredient(
          foodName = "Plátano maduro",
          grams = 120f,
          calories = 107,
          protein = 1.3f,
          carbs = 27.4f,
          fat = 0.4f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🍌"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Crema de cacahuete 100% natural",
          grams = 20f,
          calories = 118,
          protein = 5.2f,
          carbs = 4.0f,
          fat = 10.0f,
          saturatedFat = 1.6f,
          transFat = 0.0f,
          emoji = "🥜"
        ),
        com.example.data.model.MealIngredient(
          foodName = "Yogur griego 0% natural",
          grams = 150f,
          calories = 89,
          protein = 15.5f,
          carbs = 5.4f,
          fat = 0.6f,
          saturatedFat = 0.1f,
          transFat = 0.0f,
          emoji = "🥛"
        )
      )
    )
  )

  fun getAllFoods(): List<FoodItem> = _catalog.value

  fun searchFoods(query: String, category: String = "Todos"): List<FoodItem> {
    val q = query.trim().lowercase()
    return _catalog.value.filter { item ->
      val matchesCategory = (category == "Todos" || item.category.equals(category, ignoreCase = true))
      val matchesQuery = q.isEmpty() || item.name.lowercase().contains(q)
      matchesCategory && matchesQuery
    }
  }

  fun getFoodById(id: String): FoodItem? {
    return _catalog.value.find { it.id == id }
  }

  fun getRecipes(mealType: com.example.data.model.MealType? = null): List<com.example.data.model.Recipe> {
    return if (mealType != null) {
      predefinedRecipes.filter { it.mealType == mealType || it.mealType == com.example.data.model.MealType.COMIDA }
    } else {
      predefinedRecipes
    }
  }
}
