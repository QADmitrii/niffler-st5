package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.TestUser;
import guru.qa.niffler.jupiter.extension.DbCreateUserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


@ExtendWith(DbCreateUserExtension.class)
public class LoginTest {
    LoginPage loginPage = new LoginPage();
    WelcomePage welcomePage = new WelcomePage();
    MainPage mainPage = new MainPage();

    @Test
    @TestUser
    void login(UserJson userJson) {
        Selenide.open("http://127.0.0.1:3000/");
        welcomePage.waitPageLoaded().doLoginRedirect();
        loginPage.fillLoginPass(userJson.username(), userJson.testData().password())
                .clickSubmit();
        mainPage.avatarToVisible();
        System.out.println("Логин юзера " + userJson.username() + "Пароль " + userJson.testData().password());
    }
}
