package tinderBack;

import java.util.List;

public record Diet (
		int dietId,
		String description,
		Recipe breakfast,
		Recipe lunch,
		Recipe dinner) {}
