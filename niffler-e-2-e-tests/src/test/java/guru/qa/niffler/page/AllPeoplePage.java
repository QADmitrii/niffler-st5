package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class AllPeoplePage {
    private final ElementsCollection peopleAbstractTable = $(".abstract-table tbody").$$("tr");
    private final SelenideElement peopleContentTable = $(".header");

    public AllPeoplePage waitPageLoaded() {
        peopleContentTable.shouldBe(Condition.visible);
        return this;
    }

    private SelenideElement findUser(String username) {
        return peopleAbstractTable.find(((text(username))));
    }

    public void checkPendingInvitation(String username) {
        findUser(username).lastChild().$(".abstract-table__buttons").shouldHave(text("Pending invitation"));

    }

    public void checkInvitationRecieved(String username) {
        findUser(username).lastChild().$(".abstract-table__buttons div").shouldHave(attribute("data-tooltip-id", "submit-invitation"));

    }

    public void checkFriends(String username) {
        findUser(username).lastChild().$(".abstract-table__buttons").shouldHave(text("You are friends"));
        
    }
}
