class UserProfile {
  final int id;
  final String name;
  final int age;
  final String gender;
  final double heightCm;
  final double weightKg;
  final double targetWeightKg;
  final String goal;
  final int dailyCalories;
  final int proteinGoalGrams;
  final int carbsGoalGrams;
  final int fatGoalGrams;
  final bool isOnboardingCompleted;

  UserProfile({
    this.id = 1,
    this.name = 'Alex',
    this.age = 26,
    this.gender = 'Masculino',
    this.heightCm = 175.0,
    this.weightKg = 68.5,
    this.targetWeightKg = 65.0,
    this.goal = 'Perder peso',
    this.dailyCalories = 2200,
    this.proteinGoalGrams = 140,
    this.carbsGoalGrams = 240,
    this.fatGoalGrams = 65,
    this.isOnboardingCompleted = false,
  });
}

class MealIngredient {
  final String id;
  final String foodName;
  final double grams;
  final int calories;
  final double protein;
  final double carbs;
  final double fat;
  final String emoji;

  MealIngredient({
    required this.id,
    required this.foodName,
    required this.grams,
    required this.calories,
    required this.protein,
    required this.carbs,
    required this.fat,
    this.emoji = '🥗',
  });
}

class MealRecord {
  final int id;
  final String dateIso;
  final String mealType; // Desayuno, Comida, Cena, Snack
  final String timeFormatted;
  final int totalCalories;
  final double totalProtein;
  final double totalCarbs;
  final double totalFat;
  final List<MealIngredient> ingredients;

  MealRecord({
    this.id = 0,
    required this.dateIso,
    required this.mealType,
    required this.timeFormatted,
    required this.totalCalories,
    required this.totalProtein,
    required this.totalCarbs,
    required this.totalFat,
    this.ingredients = const [],
  });
}
