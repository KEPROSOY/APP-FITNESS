import '../models/models.dart';

class NutritionService {
  Future<List<MealRecord>> fetchTodayMeals() async {
    return [];
  }

  double calculateCalories({
    required double weightKg,
    required double heightCm,
    required int age,
    required String gender,
    required String activityLevel,
  }) {
    // Fórmula Mifflin-St Jeor
    double bmr = (10 * weightKg) + (6.25 * heightCm) - (5 * age);
    if (gender == 'Masculino') {
      bmr += 5;
    } else {
      bmr -= 161;
    }
    return bmr * 1.55;
  }
}
