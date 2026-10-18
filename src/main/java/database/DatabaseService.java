package database;

import java.sql.DriverManager;
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

import tinderBack.FoodTag;
import tinderBack.Ingredient;
import tinderBack.Recipe;

import java.net.URI;
import java.sql.Connection;

/**
 * Сервис работы с базой данных
 */
public class DatabaseService {
	/**
	 * Тип целого числа
	 */
	private static final String DB_INT = "INTEGER";
	
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
			HAVING SUM(rt.tag_id IN (?)) = ? 
				AND SUM(rt.tag_id IN (?)) = 0 
				AND SUM(ri.ingredient_id IN (?)) = ?
				AND SUM(ri.ingredient_id IN (?)) = 0 
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
				,calories REAL NOT NULL
				,difficulty INTEGER NOT NULL
				,cooking_time INTEGER NOT NULL
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
				,CONSTRAINT fk_recipe_id FOREIGN KEY(recipe_id) REFERENCES recipes(id)
				,CONSTRAINT fk_tag_id FOREIGN KEY(tag_id) REFERENCES tags(id)
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
				,CONSTRAINT fk_ingredient_id FOREIGN KEY(ingredient_id) REFERENCES ingredients(id)
				,CONSTRAINT fk_recipe_id FOREIGN KEY(recipe_id) REFERENCES recipes(id)
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
	 * Адрес базы данных
	 */
	private final URI m_dbUri;
	
	/**
	 * Имя пользователя
	 */
	private final String m_user;
	
	/**
	 * Пароль пользователя
	 */
	private final String m_pword;
	
	/**
	 * Соединение с базой данных
	 */
	private volatile Connection m_dbConnection;
	
	/**
	 * Фабрика для создания RowSet
	 */
	private final RowSetFactory factory;
	
	/**
	 * Создать сервис работы с базой данных
	 * @param databaseUrl URI базы данных
	 * @param user имя пользователя
	 * @param password пароль
	 * @throws DBException если не удалось создать фабрику для RowSet
	 * @see RowSetProvider
	 */
	public DatabaseService(URI databaseUri, String user, String password) throws DBException {
		m_dbUri = databaseUri;
		m_user = user;
		m_pword = password;
		m_dbConnection = null;
		try {
			factory = RowSetProvider.newFactory();
		} catch (SQLException e) {
			throw new DBException("Couldn't create RowSetFactory: " + e.getMessage());
		}
	}
	
	/**
	 * Подключиться к базе данных
	 * @param resetDatabase нужно ли очистить базу данных при подключении
	 * @throws DBException если не удалось подключиться к базе данных
	 */
	public void openConnection(boolean resetDatabase) throws DBException {
		try {
			if (m_dbConnection == null) {
				synchronized (m_dbConnection) {
					if (m_dbConnection == null) {
						m_dbConnection = DriverManager.getConnection("jdbc:sqlite:" + m_dbUri, m_user, m_pword);
					}
				}
			} else {
				throw new DBException("Connection already exists");
			}			
		} catch (SQLException e) {
			throw new DBException("Cannot connect to database: " + e.getMessage());
		}
		if (resetDatabase) {
			resetDatabase();
		}
	}
	
	/**
	 * Закрыть соединение с базой данных
	 * @throws DBException если произошла ошибка при отключении
	 */
	public void closeConnection() throws DBException {
		try {
			if (m_dbConnection != null) {
				synchronized (m_dbConnection) {
					if (m_dbConnection != null) {
						m_dbConnection.close();
					}
				}
			} else {
				throw new DBException("Connection already closed");
			}			
		} catch (SQLException e) {
			throw new DBException("Cannot connect to database: " + e.getMessage());
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
						getIngredientsByRecipe(recipeId),
						getTagsByRecipe(recipeId),
						"image_path", //TODO откуда брать адрес изображения?
						res.getInt("cooking_time"),
						res.getInt("rating"),
						res.getDouble("calories"),
						res.getInt("difficulty"));
			}
		} catch (SQLException e) {
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
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (Statement stmt = m_dbConnection.createStatement()) {
					try {
						m_dbConnection.setAutoCommit(false);
						stmt.execute(QUERY_RESET_DB);
						m_dbConnection.commit();
					} finally {
						m_dbConnection.rollback();
						m_dbConnection.setAutoCommit(true);
					}
				} catch (SQLException e) {
					throw new DBException("Couldn't reset database: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
	}
	
	/**
	 * Получить рецепт из базы данных
	 */
	private CachedRowSet queryRecFromDB(int recipeId) throws DBException {
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement(QUERY_GET_RECIPE)) {
					stmt.setInt(1, recipeId);
					CachedRowSet res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
					return res;
				} catch (SQLException e) {
					throw new DBException("Couldn't get recipe: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
	}
	
	/**
	 * Получить ингредиенты рецепта из базы данных
	 */
	private CachedRowSet queryIngsFromDBByRec(int recipeId) throws DBException {
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement(QUERY_GET_RECIPE_INGREDIENTS)) {
					stmt.setInt(1, recipeId);
					CachedRowSet res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
					return res;
				} catch (SQLException e) {
					throw new DBException("Couldn't get ingredients: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
	}
	
	/**
	 * Получить теги рецепта из базы данных
	 */
	private CachedRowSet queryTagsFromDBByRec(int recipeId) throws DBException {
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement(QUERY_GET_RECIPE_TAGS)) {
					stmt.setInt(1, recipeId);
					CachedRowSet res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
					return res;
				} catch (SQLException e) {
					throw new DBException("Couldn't get ingredients: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
	}
	
	/**
	 * Запросить рецепты из базы данных по условиям
	 */
	private CachedRowSet queryRecsFromDBUndConds(int count, int offset,
			Set<Integer> tagsInc, Set<Integer> tagsExc,
			Set<Integer> ingsInc, Set<Integer> ingsExc) throws DBException {
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement(QUERY_GET_RECIPES_UNDER_CONDITIONS)) {
					stmt.setArray(1, m_dbConnection.createArrayOf(DB_INT, tagsInc.toArray()));
					stmt.setInt(2, tagsInc.size());
					stmt.setArray(3, m_dbConnection.createArrayOf(DB_INT, tagsExc.toArray()));
					stmt.setArray(4, m_dbConnection.createArrayOf(DB_INT, ingsInc.toArray()));
					stmt.setInt(5, ingsInc.size());
					stmt.setArray(6, m_dbConnection.createArrayOf(DB_INT, ingsExc.toArray()));
					stmt.setInt(7, count);
					stmt.setInt(8, offset);
					CachedRowSet res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
					return res;
				} catch (SQLException e) {
					throw new DBException("Couldn't get recipes: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
	}
	
	/**
	 * Получить все ингредиенты из базы данных
	 */
	private CachedRowSet queryAllIngsFromDB() throws DBException {
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement(QUERY_GET_ALL_INGREDIENTS)) {
					CachedRowSet res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
					return res;
				} catch (SQLException e) {
					throw new DBException("Couldn't get recipe: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
	}
	
	/**
	 * Получить все теги из базы данных
	 */
	private CachedRowSet queryAllTagsFromDB() throws DBException {
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement(QUERY_GET_ALL_TAGS)) {
					CachedRowSet res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
					return res;
				} catch (SQLException e) {
					throw new DBException("Couldn't get recipe: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
	}
}