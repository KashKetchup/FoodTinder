package database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	 * @throws DBException если произошла ошибка при выполнении SQL запроса
	 * @see Recipe
	 */
	public Recipe getRecipe(int recipe_id) throws DBException {
		CachedRowSet res = null;
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement("SELECT * FROM recipes WHERE id = ?;")) {
					stmt.setInt(1, recipe_id);
					res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
				} catch (SQLException e) {
					throw new DBException("Couldn't get recipe: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
		if (res != null) {
			//TODO добавить возврат рецепта
		}
		return null;
	}
	
	/**
	 * Получить список рецептов, соответствующих набору тегов
	 * @param tagsId идентификаторы тегов
	 * @return неизменяемый список экземпляров Recipe, если их нет, то пустой список
	 * @throws DBException если произошла ошибка при выполнении SQL запроса
	 * @see Recipe
	 */
	public List<Recipe> getRecipesByTags(int[] tagsIds) throws DBException {
		CachedRowSet res = null;
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement("SELECT r.* FROM recipes AS r JOIN recipes_tags AS rt ON r.id = rt.recipe_id WHERE rt.tag_id IN (?) GROUP BY r.id HAVING COUNT(*) = ?;")) {
					stmt.setArray(1, m_dbConnection.createArrayOf("INTEGER", Arrays.stream(tagsIds).boxed().toArray()));
					stmt.setLong(2, Arrays.stream(tagsIds).distinct().count());
					res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
				} catch (SQLException e) {
					throw new DBException("Couldn't get recipes: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
		try {
			res.close(); //TODO delete
			//TODO вывод рецептов
		} catch (SQLException e) {
			throw new DBException("Error during data reading: " + e.getMessage());
		}
		return List.of();
	}
	
	/**
	 * Получить список ингредиентов, использующихся в рецепте
	 * @param recipeId идентификатор рецепта
	 * @return неизменяемый список экземпляров Ingredient, если их нет, то пустой список
	 * @throws DBException если произошла ошибка при выполнении SQL запроса
	 * @see Ingredient
	 */
	public List<Ingredient> getIngredientsByRecipe(int recipeId) throws DBException {
		CachedRowSet res = null;
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement("SELECT i.*, ri.amount FROM ingredients AS i JOIN recipes_ingredients AS ri ON i.id = ri.ingredient_id WHERE ri.recipe_id = ?;")) {
					stmt.setInt(1, recipeId);
					res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
				} catch (SQLException e) {
					throw new DBException("Couldn't get ingredients: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
		try {
			List<Ingredient> ingredients = new ArrayList<Ingredient>();
			while (res.next()) {
				ingredients.add(new Ingredient(res.getInt("id"), res.getString("name")));
			}
			return List.copyOf(ingredients);
		} catch (SQLException e) {
			throw new DBException("Error during data reading: " + e.getMessage());
		}
	}
	
	/**
	 * Получить списки ингредиентов, использующихся в рецептах
	 * @param recipesIds идентификаторы рецептов
	 * @return неизменяемая карта, ключи - идентификаторы рецептов, значения - неизменяемые списки экземпляров Ingredient, если их нет, то пустая карта
	 * @throws DBException если произошла ошибка при выполнении SQL запроса
	 * @see Ingredient
	 */
	public Map<Integer, List<Ingredient>> getIngredientsByRecipes(int[] recipesIds) throws DBException {
		CachedRowSet res = null;
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement("SELECT i.*, ri.recipe_id, ri.amount FROM ingredients AS i JOIN recipes_ingredients AS ri ON i.id = ri.ingredient_id WHERE ri.recipe_id IN (?);")) {
					stmt.setArray(1, m_dbConnection.createArrayOf("INTEGER", Arrays.stream(recipesIds).boxed().toArray()));
					res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
				} catch (SQLException e) {
					throw new DBException("Couldn't get ingredients: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
		try {
			Map<Integer, List<Ingredient>> ingredients = new HashMap<Integer, List<Ingredient>>();
			while (res.next()) {
				int recipeId = res.getInt("recipe_id");
				List<Ingredient> curList = ingredients.get(recipeId);
				if (curList == null) {
					curList = new ArrayList<Ingredient>();
					ingredients.put(recipeId, curList);
				}
				curList.add(new Ingredient(res.getInt("id"), res.getString("name")));
			}
			for (Map.Entry<Integer, List<Ingredient>> recIngr : ingredients.entrySet()) {
				ingredients.put(recIngr.getKey(), List.copyOf(recIngr.getValue()));
			}
			return Map.copyOf(ingredients);
		} catch (SQLException e) {
			throw new DBException("Error during data reading: " + e.getMessage());
		}
	}
	
	/**
	 * Получить список тегов, которыми отмечен рецепт
	 * @param recipeId идентификатор рецепта
	 * @return неизменяемый список экземпляров FoodTag, если их нет, то пустой список
	 * @throws DBException если произошла ошибка при выполнении SQL запроса
	 * @see FoodTag
	 */
	public List<FoodTag> getTagsByRecipe(int recipeId) throws DBException {
		CachedRowSet res = null;
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement("SELECT t.* FROM tags AS t JOIN recipes_tags AS rt ON t.id = rt.tag_id WHERE rt.recipe_id = ?;")) {
					stmt.setInt(1, recipeId);
					res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
				} catch (SQLException e) {
					throw new DBException("Couldn't get ingredients: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
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
	 * Получить списки тегов, которыми отмечены рецепты
	 * @param recipesIds идентификаторы рецептов
	 * @return неизменяемая карта, ключи - идентификаторы рецептов, значения - неизменяемые списки экземпляров FoodTag, если их нет, то пустая карта
	 * @throws DBException если произошла ошибка при выполнении SQL запроса
	 * @see Ingredient
	 */
	public Map<Integer, List<FoodTag>> getTagsByRecipes(int[] recipesIds) throws DBException {
		CachedRowSet res = null;
		synchronized (m_dbConnection) {
			if (m_dbConnection != null) {
				try (PreparedStatement stmt = m_dbConnection.prepareStatement("SELECT t.*, rt.recipe_id FROM tags AS t JOIN recipes_tags AS rt ON t.id = rt.ingredient_id WHERE rt.recipe_id IN (?);")) {
					stmt.setArray(1, m_dbConnection.createArrayOf("INTEGER", Arrays.stream(recipesIds).boxed().toArray()));
					res = factory.createCachedRowSet();
					res.populate(stmt.executeQuery());
				} catch (SQLException e) {
					throw new DBException("Couldn't get ingredients: " + e.getMessage());
				}
			} else {
				throw new DBException("Connection is closed");
			}
		}
		try {
			Map<Integer, List<FoodTag>> tags = new HashMap<Integer, List<FoodTag>>();
			while (res.next()) {
				int recipeId = res.getInt("recipe_id");
				List<FoodTag> curList = tags.get(recipeId);
				if (curList == null) {
					curList = new ArrayList<FoodTag>();
					tags.put(recipeId, curList);
				}
				curList.add(new FoodTag(res.getInt("id"), res.getString("name")));
			}
			for (Map.Entry<Integer, List<FoodTag>> recTag : tags.entrySet()) {
				tags.put(recTag.getKey(), List.copyOf(recTag.getValue()));
			}
			return Map.copyOf(tags);
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
						stmt.execute(
								"DROP TABLE IF EXISTS tags;" + 
								"DROP TABLE IF EXISTS ingredients;" +
								"DROP TABLE IF EXISTS recipes;" +
								"DROP TABLE IF EXISTS recipes_tags;" +
								"DROP TABLE IF EXISTS recipes_ingredients;" +
								"CREATE TABLE tags (id INTEGER NOT NULL UNIQUE, name TEXT NOT NULL UNIQUE, PRIMARY KEY(id AUTOINCREMENT))" +
								"CREATE TABLE ingredients (id INTEGER NOT NULL UNIQUE, name TEXT NOT NULL UNIQUE, base_cost REAL NOT NULL, PRIMARY KEY(id AUTOINCREMENT))" +
								"CREATE TABLE recipes (id INTEGER NOT NULL UNIQUE, name TEXT NOT NULL, description TEXT, instructions TEXT, calories REAL NOT NULL, difficulty INTEGER NOT NULL, cooking_time INTEGER NOT NULL, rating INTEGER NOT NULL DEFAULT 3, PRIMARY KEY(id AUTOINCREMENT))" +
								"CREATE TABLE recipes_tags (id INTEGER NOT NULL UNIQUE, recipe_id INTEGER NOT NULL, tag_id INTEGER NOT NULL, PRIMARY KEY(id AUTOINCREMENT), CONSTRAINT fk_recipe_id FOREIGN KEY(recipe_id) REFERENCES recipes(id), CONSTRAINT fk_tag_id FOREIGN KEY(tag_id) REFERENCES tags(id))" +
								"CREATE TABLE recipes_ingredients (id INTEGER NOT NULL UNIQUE, recipe_id INTEGER NOT NULL, ingredient_id INTEGER NOT NULL, amount INTEGER NOT NULL, PRIMARY KEY(id AUTOINCREMENT), CONSTRAINT fk_ingredient_id FOREIGN KEY(ingredient_id) REFERENCES ingredients(id), CONSTRAINT fk_recipe_id FOREIGN KEY(recipe_id) REFERENCES recipes(id))");
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
}