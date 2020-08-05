package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.tinylog.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static helpers.Util.fileData;

public class SearchPage {

    int wait = 100000;
    int waitInterval = 50;
    //адрес страницы тестового задания
    public static final String URL = "https://223.rts-tender.ru/supplier/auction/trade/search.aspx";
    //чек-бокс 223-ФЗ
    public SelenideElement checkBox223FL = $("#BaseMainContent_MainContent_chkPurchaseType_0");
    //чек-бокс Коммерческая Закупка
    public SelenideElement checkBoxCP = $("#BaseMainContent_MainContent_chkPurchaseType_1");
    //поле Начальная Цена
    public SelenideElement startPrice = $("#BaseMainContent_MainContent_txtStartPrice_txtRangeFrom");
    //кнопка Поиск
    public SelenideElement buttonSearch = $("#BaseMainContent_MainContent_btnSearch");
    //поле ввода даты Начала извещения
    public SelenideElement dateFrom = $("#BaseMainContent_MainContent_txtPublicationDate_txtDateFrom");
    //поле ввода даты Конца извещения
    public SelenideElement dateTo = $("#BaseMainContent_MainContent_txtPublicationDate_txtDateTo");
    //кнопка, перелистывающая таблицу
    public final SelenideElement NEXT_PAGE = $x("//*[@id='next_t_BaseMainContent_MainContent_jqgTrade_toppager']");
    //ячейка с таблицы с номером ЕИС
    public final String OOS_NUMBER = ".//*[@aria-describedby='BaseMainContent_MainContent_jqgTrade_OosNumber']";           //"[aria-describedby=\"BaseMainContent_MainContent_jqgTrade_OosNumber\"]";
    //ячейка таблицы со статусом заявки
    public final String STATE = ".//*[@aria-describedby='BaseMainContent_MainContent_jqgTrade_LotStateString']";
    //текстовое поле "Начальная цена"
    public final String PRICE = ".//*[@aria-describedby='BaseMainContent_MainContent_jqgTrade_StartPrice']";
    //всплывающее окно Загрузка
    public final SelenideElement LOADER = $x(".//*[@id='load_BaseMainContent_MainContent_jqgTrade']");

    //установка даты начала публикации извещение
    public void setDataBegin(String data) {
        dateFrom.waitUntil(Condition.visible, wait, waitInterval).setValue(data);
        Logger.info("Дата начала публикации извещения извлечена из файла Data.ini");
    }

    //установка даты конца публикации извещение
    public void setDataEnd(String data) {
        dateTo.waitUntil(Condition.visible, wait, waitInterval).setValue(data);
        Logger.info("Дата конца публикации извещения извлечена из файла Data.ini");
    }

    //нажатие кнопки Поиск
    public void clickSearchButton() {
        buttonSearch.waitUntil(Condition.visible, wait, waitInterval).click();
        Logger.info("Нажата кнопка 'Поиск'");
    }

    //выбор чек-боксов
    public void setCheckBox223FL() {
        checkBox223FL.waitUntil(Condition.visible, wait, waitInterval).setSelected(true);
        Logger.info("Чек-бокс '223-ФЗ' выбран ");
    }

    public void setCheckBoxCP() {
        checkBoxCP.waitUntil(Condition.visible, wait, waitInterval).setSelected(true);
        Logger.info("Чек-бокс 'Коммерческая закупка' выбран");
    }

    //установка начальной цены
    public void setStartPrice(double strtPrc) {
        startPrice.waitUntil(Condition.visible, wait, waitInterval).setValue(Double.toString(strtPrc));
        Logger.info("Установлена начальная цена");
    }

    //переключаем на следующую страницу таблицы
    public void clickNextPage() {
        NEXT_PAGE.click();
    }

    //ожидание, пока прогрузится страница таблицы
    public void waitLoading() {
        if (LOADER.isDisplayed()) {
        LOADER.waitUntil(not(Condition.visible), wait, waitInterval);
        Logger.info("Ожидаю, пока прогрузится таблица");
        }
    }

    //считываем данные о дате публикации извещений и данные о курсах валют
    public String readData(String key) throws IOException {
        //создаем объект Properties и загружаем в него данные из файла.
        //получаем значения свойств из объекта Properties
        Properties properties = new Properties();
        properties.load(new FileReader(fileData));
        return properties.getProperty(key);
    }

    //возвращаем цену лота
    public String getPrice(SelenideElement trade) {
        //инициализируем строковую переменную для хранения цены лота
        return trade.parent().$x(PRICE).getText();
    }

}