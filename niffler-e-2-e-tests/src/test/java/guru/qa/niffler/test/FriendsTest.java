package guru.qa.niffler.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UserQueueExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.annotation.User.Selector.*;

@WebTest
@ExtendWith(UserQueueExtension.class)
public class FriendsTest {
    LoginPage loginPage = new LoginPage();
    MainPage mainPage = new MainPage();
    WelcomePage welcomePage = new WelcomePage();
    AllPeoplePage allPeoplePage = new AllPeoplePage();

    static {
        Configuration.browserSize = "2560x1080";
    }

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/main");
        welcomePage.waitPageLoaded().doLoginRedirect();
        loginPage.fillLoginPass("ibutov", "12345")
                .clickSubmit();
    }

    @Test
    @DisplayName("Пользователь друг для dima и получил инвайт от duck")
    void recieveInviteTest(@User(selector = INVITATION_RECEIVED) UserJson userForTest,
                           @User(selector = WITH_FRIENDS) UserJson userFromTest) {
        allPeoplePage.waitPageLoaded();
        mainPage.clickSubmitAllPeople();
        allPeoplePage.isFriends(userFromTest.username());
        allPeoplePage.isInvitationRecieved(userForTest.username());
    }

    @Test
    @DisplayName("Пользователь друг для dima и отправил инвайт barsik")
    void sentInviteTest(@User(selector = INVITATION_SENT) UserJson userForTest,
                        @User(selector = WITH_FRIENDS) UserJson userFromTest) {
        allPeoplePage.waitPageLoaded();
        mainPage.clickSubmitAllPeople();
        allPeoplePage.isFriends(userFromTest.username());
        allPeoplePage.isPendingInvitation(userForTest.username());
    }

    @Test
    @DisplayName("Пользователь друг для dima")
    void friendsTest(@User(selector = WITH_FRIENDS) UserJson userForTest) {
        allPeoplePage.waitPageLoaded();
        mainPage.clickSubmitAllPeople();
        allPeoplePage.isFriends(userForTest.username());
    }
}
