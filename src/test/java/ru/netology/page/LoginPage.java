package ru.netology.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import ru.netology.data.DataHelper;

import static com.codeborne.selenide.Selenide.page;

public class LoginPage {
    @FindBy(css = "[data-test-id=login] input")
    private SelenideElement loginField;
    @FindBy(css = "[data-test-id=password] input")
    private SelenideElement passwordField;
    @FindBy(css = "[data-test-id=action-login]")
    private SelenideElement loginButton;
    @FindBy(css = "[data-test-id=error-notification] .notification__content")
    private SelenideElement errorNotification;

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        login(info.getLogin(), info.getPassword());
        return page(VerificationPage.class);
    }

    public void login(String login, String password) {
        loginField.setValue(login);
        passwordField.setValue(password);
        loginButton.click();
    }

    public void clearFields() {
        loginField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        passwordField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    }

    public void verifyErrorNotification(String expectedText) {
        errorNotification.shouldHave(Condition.exactText(expectedText))
                .shouldBe(Condition.visible);
    }
}
