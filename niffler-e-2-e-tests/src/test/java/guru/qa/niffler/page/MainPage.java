package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {
    //Elements
    private final ElementsCollection spendingTable = $(".spendings-table tbody").$$("tr");
    private final SelenideElement addSpendingSection = $(".main-content__section-add-spending"),
            categoryInput = addSpendingSection.$("div[id^='react-select-3-input']"),
            amountInput = addSpendingSection.$("input[name='amount']"),
            descriptionInput = addSpendingSection.$("input[name='description']"),
            calendarInput = addSpendingSection.$(".react-datepicker__input-container"),
            submitSpendingButton = addSpendingSection.$("button[type='submit']"),
            submitDeleteSelected = $(".spendings__bulk-actions button"),
            submitMainPage = $("a[href*='main']"),
            submitFriends = $("a[href*='friends']"),
            submitProfile = $("a[href*='profile']"),
            submitLogOut = $("[data-tooltip-id=logout]"),
            submitAllPeople = $("a[href*='people']"),
            avatar = $(".header__avatar");

    //Actions
    public SelenideElement findSpendingRowByDescription(String description) {
        return spendingTable.find(text(description)).scrollIntoView(true);
    }

    public void avatarToVisible() {
        avatar.shouldBe(visible);
    }

    public MainPage chooseSpending(SelenideElement sectionHistory) {
        sectionHistory.$$("td").first().click();
        return this;
    }

    public MainPage deleteSpending() {
        submitDeleteSelected.click();
        return this;
    }

    public void checkCountOfSpendings(int expectedSize) {
        spendingTable.shouldHave(size(expectedSize));
    }

    public MainPage clickSubmitMainPage() {
        submitMainPage.click();
        return this;
    }

    public MainPage clickSubmitFriends() {
        submitFriends.click();
        return this;
    }

    public MainPage clickSubmitAllPeople() {
        submitAllPeople.click();
        return this;
    }

    public MainPage clickSubmitProfile() {
        submitProfile.click();
        return this;
    }

    public MainPage clickLogOut() {
        submitLogOut.click();
        return this;
    }

}
