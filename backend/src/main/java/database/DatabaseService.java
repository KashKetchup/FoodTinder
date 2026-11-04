package database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import tinderBack.FoodTag;
import tinderBack.Ingredient;
import tinderBack.Recipe;

import java.sql.Connection;

/**
 * Сервис работы с базой данных
 */
public class DatabaseService {	
	/**
	 * Запрос получения рецепта
	 */
	private static final String QUERY_GET_RECIPE = 
			"""
			SELECT * 
			FROM recipes 
			WHERE id = ?;
			""";
	
	/**
	 * Получить ингредиенты рецепта
	 */
	private static final String QUERY_GET_RECIPE_INGREDIENTS = 
			"""
			SELECT i.*
				,ri.amount 
			FROM ingredients i 
				JOIN recipes_ingredients ri ON i.id = ri.ingredient_id 
			WHERE ri.recipe_id = ?;
			""";
	
	/**
	 * Получить теги рецепта
	 */
	private static final String QUERY_GET_RECIPE_TAGS = 
			"""
			SELECT t.* 
			FROM tags t 
				JOIN recipes_tags rt ON t.id = rt.tag_id 
			WHERE rt.recipe_id = ?;
			""";
	
	/**
	 * Запрос получения рецептов по условиям
	 */
	private static final String QUERY_GET_RECIPES_UNDER_CONDITIONS = 
			"""
			SELECT r.* 
			FROM recipes r 
				INNER JOIN recipes_tags rt ON r.id = rt.recipe_id 
				INNER JOIN recipes_ingredients ri ON r.id = ri.recipe_id 
			GROUP BY r.id 
			HAVING COUNT(DISTINCT IIF(rt.tag_id IN (%s), rt.tag_id, NULL)) = ? 
				AND COUNT(DISTINCT IIF(rt.tag_id IN (%s), rt.tag_id, NULL)) = 0 
				AND COUNT(DISTINCT IIF(ri.ingredient_id IN (%s), ri.ingredient_id, NULL)) = ?
				AND COUNT(DISTINCT IIF(ri.ingredient_id IN (%s), ri.ingredient_id, NULL)) = 0 
			ORDER BY r.id ASC
			LIMIT ? OFFSET ?;
			""";
	
	/**
	 * Запрос получения всех ингредиентов
	 */
	private static final String QUERY_GET_ALL_INGREDIENTS = 
			"""
			SELECT * 
			FROM ingredients
			ORDER BY name ASC;
			""";
	
	/**
	 * Запрос получения всех тегов
	 */
	private static final String QUERY_GET_ALL_TAGS = 
			"""
			SELECT * 
			FROM tags
			ORDER BY name ASC;
			""";
	
	/**
	 * Запрос создания таблицы tags
	 */
	private final static String QUERY_CREATE_TAGS = 
			"""
			CREATE TABLE tags (
				id INTEGER NOT NULL UNIQUE
				,name TEXT NOT NULL UNIQUE
				,PRIMARY KEY(id AUTOINCREMENT)
			);
			""";
	
	/**
	 * Запрос создания таблицы ingredients
	 */
	private final static String QUERY_CREATE_INGREDIENTS = 
			"""
			CREATE TABLE ingredients (
				id INTEGER NOT NULL UNIQUE
				,name TEXT NOT NULL UNIQUE
				,base_cost REAL NOT NULL
				,PRIMARY KEY(id AUTOINCREMENT)
			);
			""";
	
	/**
	 * Запрос создания таблицы recipes
	 */
	private final static String QUERY_CREATE_RECIPES = 
			"""
			CREATE TABLE recipes (
				id INTEGER NOT NULL UNIQUE
				,name TEXT NOT NULL
				,description TEXT
				,instructions TEXT
				,calories REAL NOT NULL DEFAULT 0.0
				,difficulty INTEGER NOT NULL DEFAULT 0
				,cooking_time INTEGER NOT NULL DEFAULT 0
				,rating INTEGER NOT NULL DEFAULT 3
				,PRIMARY KEY(id AUTOINCREMENT)
			);
			""";
	
	/**
	 * Запрос создания таблицы recipes_tags
	 */
	private final static String QUERY_CREATE_RECIPES_TAGS = 
			"""
			CREATE TABLE recipes_tags (
				id INTEGER NOT NULL UNIQUE
				,recipe_id INTEGER NOT NULL
				,tag_id INTEGER NOT NULL
				,PRIMARY KEY(id AUTOINCREMENT)
				,CONSTRAINT fk_rt_recipes FOREIGN KEY(recipe_id) REFERENCES recipes(id)
				,CONSTRAINT fk_rt_tags FOREIGN KEY(tag_id) REFERENCES tags(id)
			);
			""";
	
	/**
	 * Запрос создания таблицы recipes_ingredients
	 */
	private final static String QUERY_CREATE_RECIPES_INGREDIENTS = 
			"""
			CREATE TABLE recipes_ingredients (
				id INTEGER NOT NULL UNIQUE
				,recipe_id INTEGER NOT NULL
				,ingredient_id INTEGER NOT NULL
				,amount INTEGER NOT NULL
				,PRIMARY KEY(id AUTOINCREMENT)
				,CONSTRAINT fk_ri_ingredients FOREIGN KEY(ingredient_id) REFERENCES ingredients(id)
				,CONSTRAINT fk_ri_recipes FOREIGN KEY(recipe_id) REFERENCES recipes(id)
			);
			""";
	
	/**
	 * Запрос пересоздания базы данных
	 */
	private final static String QUERY_RESET_DB = 
			"""
			DROP TABLE IF EXISTS tags;
			DROP TABLE IF EXISTS ingredients;
			DROP TABLE IF EXISTS recipes;
			DROP TABLE IF EXISTS recipes_tags;
			DROP TABLE IF EXISTS recipes_ingredients;
			"""
			+ QUERY_CREATE_TAGS
			+ QUERY_CREATE_INGREDIENTS
			+ QUERY_CREATE_RECIPES
			+ QUERY_CREATE_RECIPES_TAGS
			+ QUERY_CREATE_RECIPES_INGREDIENTS;
	
	/**
	 * Пул соединений с базой данных
	 */
	private final DataSource m_dataSource;
	
	/**
	 * Фабрика для создания RowSet
	 */
	private final RowSetFactory m_factory;
	
	/**
	 * Создать сервис работы с базой данных
	 * @param databaseUrl URI базы данных
	 * @param user имя пользователя
	 * @param password пароль
	 * @throws DBException если не удалось создать фабрику для RowSet
	 * @see RowSetProvider
	 */
	public DatabaseService(String databaseUrl, String user, String password) throws DBException {
		PoolProperties p = new PoolProperties();
		p.setUrl("jdbc:sqlite:" + databaseUrl);
		p.setDriverClassName("org.sqlite.JDBC");
		p.setUsername(user);
		p.setPassword(password);
		m_dataSource = new DataSource();
		m_dataSource.setPoolProperties(p);
		try {
			m_factory = RowSetProvider.newFactory();
		} catch (SQLException e) {
			throw new DBException("Couldn't create RowSetFactory: " + e.getMessage());
		}
	}
	
	/**
	 * Получить рецепт по идентификатору
	 * @param recipe_id идентификатор рецепта
	 * @return экземпляр Recipe, если рецепт есть, иначе null
	 * @throws DBException если произошла ошибка при работе с базой данных
	 * @see Recipe
	 */
	public Recipe getRecipe(int recipeId) throws DBException {
		CachedRowSet res = queryRecFromDB(recipeId);
		try {
			if (res.next()) {
				return new Recipe(
						res.getInt("id"),
						res.getString("name"),
						res.getString("description"),
						res.getString("instructions"),
						getIngredientsByRecipe(recipeId),
						getTagsByRecipe(recipeId),
						"image_path", //TODO откуда брать адрес изображения?
						res.getInt("cooking_time"),
						res.getInt("rating"),
						res.getDouble("calories"),
						res.getInt("difficulty"));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			throw new DBException("Error during data reading: " + e.getMessage());
		}
		return null;
	}
	
	/**
	 * Получить список ингредиентов, использующихся в рецепте
	 * @param recipeId идентификатор рецепта
	 * @return неизменяемый список экземпляров Ingredient, если их нет, то пустой список
	 * @throws DBException если произошла ошибка при работе с базой данных
	 * @see Ingredient
	 */
	public Set<Ingredient> getIngredientsByRecipe(int recipeId) throws DBException {
		CachedRowSet res = queryIngsFromDBByRec(recipeId);
		try {
			Set<Ingredient> ingredients = new HashSet<Ingredient>();
			while (res.next()) {
				ingredients.add(new Ingredient(res.getInt("id"), res.getString("name")));
			}
			return Set.copyOf(ingredients);
		} catch (SQLException e) {
			throw new DBException("Error during data reading: " + e.getMessage());
		}
	}

	/**
	 * Получить список тегов, которыми отмечен рецепт
	 * @param recipeId идентификатор рецепта
	 * @return неизменяемый список экземпляров FoodTag, если их нет, то пустой список
	 * @throws DBException если произошла ошибка при работе с базой данных
	 * @see FoodTag
	 */
	public Set<FoodTag> getTagsByRecipe(int recipeId) throws DBException {
		CachedRowSet res = queryTagsFromDBByRec(recipeId);
		try {
			Set<FoodTag> tags = new HashSet<FoodTag>();
			while (res.next()) {
				tags.add(new FoodTag(res.getInt("id"), res.getString("name")));
			}
			return Set.copyOf(tags);
		} catch (SQLException e) {
			throw new DBException("Error during data reading: " + e.getMessage());
		}
	}

	/**
	 * Получить список рецептов, соответствующих условиям, со смещением от начала
	 * @param count максимальное количество
	 * @param offset смещение относительно начала
	 * @param tagsIdsInclude идентификаторы тегов, которыми должны обладать рецепты
	 * @param tagsIdsExclude идентификаторы тегов, которыми не должны обладать рецепты
	 * @param ingredientsIdsInclude идентификаторы ингредиентов, которые должны быть в рецептах
	 * @param ingredientsIdsExclude идентификаторы ингредиентов, которых не должно быть в рецептах
	 * @return неизменяемый список экземпляров Recipe, если их нет, то пустой список
	 * @throws DBException если произошла ошибка при работе с базой данных
	 * @see Recipe
	 */
	public List<Recipe> getRecipesByTagsWithOffset(int count, int offset,
			Set<Integer> tagsIdsInclude, Set<Integer> tagsIdsExclude,
			Set<Integer> ingredientsIdsInclude, Set<Integer> ingredientsIdsExclude) throws DBException {
		CachedRowSet res = queryRecsFromDBUndConds(count, offset, 
				tagsIdsInclude, tagsIdsExclude, 
				ingredientsIdsInclude, ingredientsIdsExclude);
		try {
			List<Recipe> recipes = new ArrayList<Recipe>();
			while (res.next()) {
				int recipeId = res.getInt("id");
				recipes.add(new Recipe(
						recipeId,
						res.getString("name"),
						res.getString("description"),
						res.getString("instructions"),
						getIngredientsByRecipe(recipeId),
						getTagsByRecipe(recipeId),
						"image_path", //TODO откуда брать адрес изображения?
						res.getInt("cooking_time"),
						res.getInt("rating"),
						res.getDouble("calories"),
						res.getInt("difficulty")));
			}
			return List.copyOf(recipes);
		} catch (SQLException e) {
			throw new DBException("Error during data reading: " + e.getMessage());
		}
	}
	
	/**
	 * Получить список рецептов, соответствующих условиям
	 * @param count максимальное количество
	 * @param tagsIdsInclude идентификаторы тегов, которыми должны обладать рецепты
	 * @param tagsIdsExclude идентификаторы тегов, которыми не должны обладать рецепты
	 * @param ingredientsIdsInclude идентификаторы ингредиентов, которые должны быть в рецептах
	 * @param ingredientsIdsExclude идентификаторы ингредиентов, которых не должно быть в рецептах
	 * @return неизменяемый список экземпляров Recipe, если их нет, то пустой список
	 * @throws DBException если произошла ошибка при работе с базой данных
	 * @see Recipe
	 */
	public List<Recipe> getRecipesByTags(int count,
			Set<Integer> tagsIdsInclude, Set<Integer> tagsIdsExclude,
			Set<Integer> ingredientsIdsInclude, Set<Integer> ingredientsIdsExclude) throws DBException {
		return getRecipesByTagsWithOffset(count, 0, 
				tagsIdsInclude, tagsIdsExclude, 
				ingredientsIdsInclude, ingredientsIdsExclude);
	}
	
	/**
	 * Получить список всех ингредиентов, отсортированных в лексикографическом порядке
	 * @return неизменяемый список экземпляров Ingredient, никогда не возвращает null
	 * @throws DBException если произошла ошибка при работе с базой данных
	 * @see Ingredient
	 */
	public List<Ingredient> getAllIngredients() throws DBException {
		CachedRowSet res = queryAllIngsFromDB();
		try {
			List<Ingredient> tags = new ArrayList<Ingredient>();
			while (res.next()) {
				tags.add(new Ingredient(res.getInt("id"), res.getString("name")));
			}
			return List.copyOf(tags);
		} catch (SQLException e) {
			throw new DBException("Error during data reading: " + e.getMessage());
		}
	}
	
	/**
	 * Получить список всех тегов, отсортированных в лексикографическом порядке
	 * @return неизменяемый список экземпляров FoodTag, никогда не возвращает null
	 * @throws DBException если произошла ошибка при работе с базой данных
	 * @see FoodTag
	 */
	public List<FoodTag> getAllTags() throws DBException {
		CachedRowSet res = queryAllTagsFromDB();
		try {
			List<FoodTag> tags = new ArrayList<FoodTag>();
			while (res.next()) {
				tags.add(new FoodTag(res.getInt("id"), res.getString("name")));
			}
			return List.copyOf(tags);
		} catch (SQLException e) {
			throw new DBException("Error during data reading: " + e.getMessage());
		}
	}
	
	/**
	 * Очистить базу данных
	 * @throws DBException если произошла ошибка при сбросе
	 */
	private void resetDatabase() throws DBException {
		try (Connection conn = m_dataSource.getConnection();
				Statement stmt = conn.createStatement()) {
			try {
				conn.setAutoCommit(false);
				stmt.execute(QUERY_RESET_DB);
				conn.commit();
			} finally {
				conn.rollback();
				conn.setAutoCommit(true);
			}
		} catch (SQLException e) {
			throw new DBException("Couldn't reset database: " + e.getMessage());
		}
	}
	
	/**
	 * Получить рецепт из базы данных
	 */
	private CachedRowSet queryRecFromDB(int recipeId) throws DBException {
		try (Connection conn = m_dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(QUERY_GET_RECIPE)) {
			stmt.setInt(1, recipeId);
			CachedRowSet res = m_factory.createCachedRowSet();
			res.populate(stmt.executeQuery());
			return res;
		} catch (SQLException e) {
			throw new DBException("Couldn't get recipe: " + e.getMessage());
		}
	}
	
	/**
	 * Получить ингредиенты рецепта из базы данных
	 */
	private CachedRowSet queryIngsFromDBByRec(int recipeId) throws DBException {
		try (Connection conn = m_dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(QUERY_GET_RECIPE_INGREDIENTS)) {
			stmt.setInt(1, recipeId);
			CachedRowSet res = m_factory.createCachedRowSet();
			res.populate(stmt.executeQuery());
			return res;
		} catch (SQLException e) {
			throw new DBException("Couldn't get ingredients: " + e.getMessage());
		}
	}
	
	/**
	 * Получить теги рецепта из базы данных
	 */
	private CachedRowSet queryTagsFromDBByRec(int recipeId) throws DBException {
		try (Connection conn = m_dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(QUERY_GET_RECIPE_TAGS)) {
			stmt.setInt(1, recipeId);
			CachedRowSet res = m_factory.createCachedRowSet();
			res.populate(stmt.executeQuery());
			return res;
		} catch (SQLException e) {
			throw new DBException("Couldn't get ingredients: " + e.getMessage());
		}
	}
	
	/**
	 * Запросить рецепты из базы данных по условиям
	 */
	private CachedRowSet queryRecsFromDBUndConds(int count, int offset,
			Set<Integer> tagsInc, Set<Integer> tagsExc,
			Set<Integer> ingsInc, Set<Integer> ingsExc) throws DBException {
		String queryString = QUERY_GET_RECIPES_UNDER_CONDITIONS.formatted(
				StringUtils.repeat("?", ", ", tagsInc.size()), 
				StringUtils.repeat("?", ", ", tagsExc.size()), 
				StringUtils.repeat("?", ", ", ingsInc.size()), 
				StringUtils.repeat("?", ", ", ingsExc.size()));
		try (Connection conn = m_dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(queryString)) {
			return prepRecStmt(count, offset, tagsInc, tagsExc, ingsInc, ingsExc, stmt);
		} catch (SQLException e) {
			throw new DBException("Couldn't get recipes: " + e.getMessage());
		}
	}
	
	/**
	 * Выполнить запрос с предварительной вставкой данных и вернуть результат
	 */
	private CachedRowSet prepRecStmt(int count, int offset,
			Set<Integer> tagsInc, Set<Integer> tagsExc,
			Set<Integer> ingsInc, Set<Integer> ingsExc,
			PreparedStatement stmt) throws SQLException {
		int index = 1;
		for (int number : tagsInc) {
			stmt.setInt(index++, number);
		}
		stmt.setInt(index++, tagsInc.size());
		for (int number : tagsExc) {
			stmt.setInt(index++, number);
		}
		for (int number : ingsInc) {
			stmt.setInt(index++, number);
		};
		stmt.setInt(index++, ingsInc.size());
		for (int number : ingsExc) {
			stmt.setInt(index++, number);
		}
		stmt.setInt(index++, count);
		stmt.setInt(index, offset);
		CachedRowSet res = m_factory.createCachedRowSet();
		res.populate(stmt.executeQuery());
		return res;
	}
	
	/**
	 * Получить все ингредиенты из базы данных
	 */
	private CachedRowSet queryAllIngsFromDB() throws DBException {
		try (Connection conn = m_dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(QUERY_GET_ALL_INGREDIENTS)) {
			CachedRowSet res = m_factory.createCachedRowSet();
			res.populate(stmt.executeQuery());
			return res;
		} catch (SQLException e) {
			throw new DBException("Couldn't get recipe: " + e.getMessage());
		}
	}
	
	/**
	 * Получить все теги из базы данных
	 */
	private CachedRowSet queryAllTagsFromDB() throws DBException {
		try (Connection conn = m_dataSource.getConnection();
				PreparedStatement stmt = conn.prepareStatement(QUERY_GET_ALL_TAGS)) {
			CachedRowSet res = m_factory.createCachedRowSet();
			res.populate(stmt.executeQuery());
			return res;
		} catch (SQLException e) {
			throw new DBException("Couldn't get recipe: " + e.getMessage());
		}
	}
}