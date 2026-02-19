package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLHelper {
    private static final QueryRunner QUERY_RUNNER = new QueryRunner();

    private SQLHelper() {
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(
                System.getProperty("db.url"),"app","pass");
    }

    @SneakyThrows
    public static DataHelper.VerificationCode getVerificationCode() {
        var codeSQL = "select code from auth_codes order by created desc limit 1";
        try (var conn = connect()) {
            return QUERY_RUNNER.query(
                    conn, codeSQL, new BeanHandler<>(DataHelper.VerificationCode.class));
        }
    }

    @SneakyThrows
    public static String getUserStatus(DataHelper.AuthInfo authInfo) {
        var codeSQL = "select status from users where login = ?";
        try (var conn = connect()) {
            return QUERY_RUNNER.query(
                    conn,
                    codeSQL,
                    new ScalarHandler<String>(),
                    authInfo.getLogin());
        }
    }

    @SneakyThrows
    public static void cleanDB() {
        try (var conn = connect()) {
            QUERY_RUNNER.execute(conn, "delete from auth_codes");
            QUERY_RUNNER.execute(conn, "delete from card_transactions");
            QUERY_RUNNER.execute(conn, "delete from cards");
            QUERY_RUNNER.execute(conn, "delete from users");
        }
    }

    @SneakyThrows
    public static void cleanAuthCodes() {
        try (var conn = connect()) {
            QUERY_RUNNER.execute(conn, "delete from auth_codes");
        }
    }
}
