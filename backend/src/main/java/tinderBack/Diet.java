package tinderBack;

import java.util.List;

public record Diet (
		int dietId,
		String description,
		List<List<Recipe>> recipes) {}
