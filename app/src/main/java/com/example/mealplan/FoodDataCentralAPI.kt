import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import org.json.JSONObject

class FoodDataFetcher {
    companion object {
        fun fetchFoodData(apiKey: String) {
            val baseUrl = "https://api.nal.usda.gov/fdc/v1"
            val foodUrl = "/foods/list"

            val url = "$baseUrl$foodUrl?api_key=$apiKey"

            Fuel.get(url).responseString { _, _, result ->
                when (result) {
                    is Result.Success -> {
                        val responseData = result.get()
                        val jsonObject = JSONObject(responseData)
                        val foodList = jsonObject.getJSONArray("foods")

                        for (i in 0 until foodList.length()) {
                            val food = foodList.getJSONObject(i)
                            val foodName = food.getString("description")

                            val foodNutrients = food.getJSONArray("foodNutrients")
                            val caloriesNutrient = foodNutrients.getJSONObject(0)
                            val calories = caloriesNutrient.getDouble("value")

                            println("Food: $foodName")
                            println("Calories: $calories")
                            println()
                        }
                    }
                    is Result.Failure -> {
                        val error = result.error.exception
                        // Handle the request failure or error here
                    }
                }
            }
        }
    }
}
