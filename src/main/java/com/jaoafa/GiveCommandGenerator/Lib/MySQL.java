package com.jaoafa.GiveCommandGenerator.Lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.jaoafa.GiveCommandGenerator.GiveCommandGenerator;
import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

/**
 * Connects to and uses a MySQL database
 *
 * @author -_Husky_-
 * @author tips48
 */
public class MySQL extends Database {
	private final String user;
	private final String database;
	private final String password;
	private final String port;
	private final String hostname;

	/**
	 * Creates a new MySQL instance
	 *
	 * @param hostname
	 *            Name of the host
	 * @param port
	 *            Port number
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public MySQL(String hostname, String port, String username,
			String password) {
		this(hostname, port, null, username, password);
	}

	/**
	 * Creates a new MySQL instance for a specific database
	 *
	 * @param hostname
	 *            Name of the host
	 * @param port
	 *            Port number
	 * @param database
	 *            Database name
	 * @param username
	 *            Username
	 * @param password
	 *            Password
	 */
	public MySQL(String hostname, String port, String database,
			String username, String password) {
		this.hostname = hostname;
		this.port = port;
		this.database = database;
		this.user = username;
		this.password = password;
	}

	@Override
	public Connection openConnection() throws SQLException,
			ClassNotFoundException {
		if (checkConnection()) {
			return connection;
		}

		String connectionURL = "jdbc:mysql://"
				+ this.hostname + ":" + this.port;
		if (database != null) {
			connectionURL = connectionURL + "/" + this.database + "?autoReconnect=true&useUnicode=true&characterEncoding=utf8";
		}

		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection(connectionURL,
				this.user, this.password);
		return connection;
	}

	/**
	 * Statementをチェックします。
	 * @deprecated 使用の仕方によってはSQLインジェクションの被害にあう可能性があります。PreparedStatementを使用するべきです。
	 * @param statement チェックするStatement
	 * @return 更新の必要があれば新しいStatement。必要がなければ引数と同じStatement
	 */
	@Deprecated
	public static Statement check(Statement statement){
		try {
			statement.executeQuery("SELECT * FROM chetab LIMIT 1");
			return statement;
		} catch (CommunicationsException e){
			MySQL MySQL = new MySQL(GiveCommandGenerator.sqlserver, "3306", "jaoafa", GiveCommandGenerator.sqluser, GiveCommandGenerator.sqlpassword);
			try {
				GiveCommandGenerator.c = MySQL.openConnection();
				statement = GiveCommandGenerator.c.createStatement();
				return statement;
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO 自動生成された catch ブロック
				e1.printStackTrace();
				return null;
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 新しいStatementを返します。
	 * @deprecated 使用の仕方によってはSQLインジェクションの被害にあう可能性があります。PreparedStatementを使用するべきです。
	 * @return 新しいStatement
	 * @throws SQLException 新しいStatementの取得中にSQLExceptionが発生した場合
	 * @throws ClassNotFoundException 新しいStatementの取得中にClassNotFoundExceptionが発生した場合
	 */
	@Deprecated
	public static Statement getNewStatement() throws SQLException, ClassNotFoundException{
		Statement statement;
		try {
			statement = GiveCommandGenerator.c.createStatement();
		} catch (NullPointerException e) {
			MySQL MySQL = new MySQL(GiveCommandGenerator.sqlserver, "3306", "jaoafa", GiveCommandGenerator.sqluser, GiveCommandGenerator.sqlpassword);
			try {
				GiveCommandGenerator.c = MySQL.openConnection();
				statement = GiveCommandGenerator.c.createStatement();
			} catch (ClassNotFoundException | SQLException e1) {
				//GiveCommandGeneratorPremise.BugReporter(e);
				throw e1;
			}
		} catch (SQLException e) {
			//GiveCommandGeneratorPremise.BugReporter(e);
			throw e;
		}

		statement = MySQL.check(statement);
		return statement;
	}

	/**
	 * 新しいPreparedStatementを返します。
	 * @return 新しいPreparedStatement
	 * @throws SQLException 新しいPreparedStatementの取得中にSQLExceptionが発生した場合
	 * @throws ClassNotFoundException 新しいPreparedStatementの取得中にClassNotFoundExceptionが発生した場合
	 */
	public static PreparedStatement getNewPreparedStatement(String sql) throws SQLException, ClassNotFoundException{
		PreparedStatement statement;
		try {
			if(((System.currentTimeMillis() / 1000L) - GiveCommandGenerator.ConnectionCreate) >= 18000){
				// com.mysql.jdbc.exceptions.jdbc4.CommunicationsExceptionの発生を防ぐため、最後にコネクションを作成したときから5時間以上経っていればコネクションを作り直す。
				MySQL MySQL = new MySQL(GiveCommandGenerator.sqlserver, "3306", "jaoafa", GiveCommandGenerator.sqluser, GiveCommandGenerator.sqlpassword);
				try {
					GiveCommandGenerator.c = MySQL.openConnection();
					GiveCommandGenerator.ConnectionCreate = System.currentTimeMillis() / 1000L;
				} catch (ClassNotFoundException | SQLException e) {
					//GiveCommandGeneratorPremise.BugReporter(e);
					throw e;
				}
			}
			statement = GiveCommandGenerator.c.prepareStatement(sql);
		} catch (NullPointerException e) {
			MySQL MySQL = new MySQL(GiveCommandGenerator.sqlserver, "3306", "jaoafa", GiveCommandGenerator.sqluser, GiveCommandGenerator.sqlpassword);
			try {
				GiveCommandGenerator.c = MySQL.openConnection();
				GiveCommandGenerator.ConnectionCreate = System.currentTimeMillis() / 1000L;
				statement = GiveCommandGenerator.c.prepareStatement(sql);
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO 自動生成された catch ブロック
				//GiveCommandGeneratorPremise.BugReporter(e);
				throw e1;
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			//GiveCommandGenerator.(e);
			throw e;
		}
		return statement;
	}
	/**
	 * 新しいPreparedStatementを返します。
	 * @return 新しいPreparedStatement
	 * @throws SQLException 新しいPreparedStatementの取得中にSQLExceptionが発生した場合
	 * @throws ClassNotFoundException 新しいPreparedStatementの取得中にClassNotFoundExceptionが発生した場合
	 */
	public static PreparedStatement getNewPreparedStatement(String sql, int autoGeneratedKeys) throws SQLException, ClassNotFoundException{
		PreparedStatement statement;
		try {
			if(((System.currentTimeMillis() / 1000L) - GiveCommandGenerator.ConnectionCreate) >= 18000){
				// com.mysql.jdbc.exceptions.jdbc4.CommunicationsExceptionの発生を防ぐため、最後にコネクションを作成したときから5時間以上経っていればコネクションを作り直す。
				MySQL MySQL = new MySQL(GiveCommandGenerator.sqlserver, "3306", "jaoafa", GiveCommandGenerator.sqluser, GiveCommandGenerator.sqlpassword);
				try {
					GiveCommandGenerator.c = MySQL.openConnection();
					GiveCommandGenerator.ConnectionCreate = System.currentTimeMillis() / 1000L;
				} catch (ClassNotFoundException | SQLException e) {
					//GiveCommandGeneratorPremise.BugReporter(e);
					throw e;
				}
			}
			statement = GiveCommandGenerator.c.prepareStatement(sql, autoGeneratedKeys);
		} catch (NullPointerException e) {
			MySQL MySQL = new MySQL(GiveCommandGenerator.sqlserver, "3306", "jaoafa", GiveCommandGenerator.sqluser, GiveCommandGenerator.sqlpassword);
			try {
				GiveCommandGenerator.c = MySQL.openConnection();
				GiveCommandGenerator.ConnectionCreate = System.currentTimeMillis() / 1000L;
				statement = GiveCommandGenerator.c.prepareStatement(sql, autoGeneratedKeys);
			} catch (ClassNotFoundException | SQLException e1) {
				// TODO 自動生成された catch ブロック
				//GiveCommandGeneratorPremise.BugReporter(e);
				throw e1;
			}
		} catch (SQLException e) {
			// TODO 自動生成された catch ブロック
			//GiveCommandGenerator.(e);
			throw e;
		}
		return statement;
	}
}