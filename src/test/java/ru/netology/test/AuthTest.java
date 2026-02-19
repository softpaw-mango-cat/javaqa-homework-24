package ru.netology.test;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.page.LoginPage;

public class AuthTest {
    LoginPage loginPage;
    DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();

    @BeforeEach
    void setup() {
        loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
    }

    @AfterEach
    void tearDown() {
        SQLHelper.cleanAuthCodes();
    }

    @AfterAll
    static void tearDownAll() {
        SQLHelper.cleanDB();
    }

    // проверка логина - позитивный кейс с валидным юзером
    @Test
    @DisplayName("Should Login With Existing User")
    void shouldLoginWithExistingUser() {
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = SQLHelper.getVerificationCode();
        verificationPage.validVerify(verificationCode.getCode());
    }

    // проверка логина - негативный кейс с невалидным юзером
    @Test
    @DisplayName("Should Get Error If User Non Existent")
    void shouldGetErrorIfUserNonExistent() {
        var randomInfo = DataHelper.generateRandomUser();
        loginPage.login(randomInfo);
        loginPage.verifyErrorNotification("Ошибка! \nНеверно указан логин или пароль");
    }

    // проверка логина - негативный кейс с валидным юзером и невалидным кодом
    @Test
    @DisplayName("Should Get Error With Existing User And RandomCode")
    void shouldGetErrorWithExistingUserAndRandomCode() {
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.generateRandomCode().getCode();
        verificationPage.verify(verificationCode);
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
    }

    // проверка блокировки при трёхкратном логине с неправильным кодом
    @Test
    @DisplayName("shouldBlockValidUserWithIncorrectCodeThreeTimes")
    void shouldBlockValidUserWithIncorrectCodeThreeTimes() {
        // залогинили пользователя
        var verificationPage = loginPage.validLogin(authInfo);
        // ввод кодов и очистка поля - 3 раза
        verificationPage.verify(DataHelper.generateRandomCode().getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
        verificationPage.clearCodeField();

        verificationPage.verify(DataHelper.generateRandomCode().getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
        verificationPage.clearCodeField();

        verificationPage.verify(DataHelper.generateRandomCode().getCode());
        verificationPage.verifyErrorNotification("Ошибка! \nНеверно указан код! Попробуйте ещё раз.");
        verificationPage.clearCodeField();

        // проверка статуса пользователя
        var actualUserStatus = SQLHelper.getUserStatus(authInfo);
        Assertions.assertEquals("blocked", actualUserStatus);
    }
}
