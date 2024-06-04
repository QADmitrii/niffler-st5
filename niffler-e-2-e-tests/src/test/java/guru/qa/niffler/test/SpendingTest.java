package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.annotation.GenerateCategory;
import guru.qa.niffler.jupiter.annotation.GenerateSpend;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.CategoryHttpExtension;
import guru.qa.niffler.jupiter.extension.GenerateCategoryExtension;
import guru.qa.niffler.jupiter.extension.GenerateSpendExtension;
import guru.qa.niffler.jupiter.extension.SpendHttpExtension;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.WelcomePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@WebTest
@ExtendWith({GenerateCategoryExtension.class, GenerateSpendExtension.class})
public class SpendingTest {
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    WelcomePage welcomePage = new WelcomePage();

    static {
        Configuration.browserSize = "2560x1080";
    }

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage.waitPageLoaded().doLoginRedirect();
        loginPage.fillLoginPass("gena", "12345")
                .clickSubmit();
    }

    @GenerateCategory(
            username = "gena",
            category = "Обучение2"
    )
    @GenerateSpend(
            username = "gena",
            description = "QA.GURU Advanced 5",
            amount = 65000.00,
            currency = CurrencyValues.RUB,
            category = "Обучение2")
    @Test
    void spendingShouldBeDeletedAfterTableAction(SpendJson spendJson) {

        mainPage.chooseSpending(mainPage.findSpendingRowByDescription(spendJson.description()))
                .deleteSpending()
                .checkCountOfSpendings(0);
    }
}
