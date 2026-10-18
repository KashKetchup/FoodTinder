package tinderBack;

import java.util.Set;

public class FullRecipe extends Recipe {
	private String m_instructions;
	public FullRecipe(int id,
	        String name, 
	        String description,
	        Set<Ingredient> ingredients, 
	        Set<FoodTag> tags, 
	        String imagePath,
	        int cookingTime,
	        int rate,
	        float calories,
	        int difficulty,
	        String instructions) {
		super(id, name, description, ingredients, tags, imagePath, cookingTime, rate, calories, difficulty);
		m_instructions = instructions;
	}
    public String getIntructions() {
    	return m_instructions;
    }
}