package database;

/**
 * Исключение при работе с базой данных
 */
public class DBException extends Exception {
	/**
	 * Создать исключение при работе с базой данных
	 * @param message сообщение в исключении
	 */
	public DBException(String message) {
		super(message);
	}
}
