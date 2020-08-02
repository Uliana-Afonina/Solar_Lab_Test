package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.tinylog.Logger;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;

public class SearchPage {

    int wait = 100000;
    int waitInterval = 50;
    //адрес страницы тестового задания
    public static String url = "https://223.rts-tender.ru/supplier/auction/trade/search.aspx";
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
    public String nextPage = "#next_t_BaseMainContent_MainContent_jqgTrade_toppager";
    //ячейка с таблицы с номером ЕИС
    public String oosNumber = "[aria-describedby=\"BaseMainContent_MainContent_jqgTrade_OosNumber\"]";
    //ячейка таблицы со статусом заявки
    public String state = "[aria-describedby=\"BaseMainContent_MainContent_jqgTrade_LotStateString\"]";
    //текстовое поле "Начальная цена"
    public String price = "[aria-describedby=\"BaseMainContent_MainContent_jqgTrade_StartPrice\"]";
    //всплывающее окно Загрузка
    public String loader = "#load_BaseMainContent_MainContent_jqgTrade";

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
    public void setCheckBox(SelenideElement checkBox) {

        checkBox.waitUntil(Condition.visible, wait, waitInterval).setSelected(true);
        Logger.info("Чек-бокс выбран");

    }

    //установка начальной цены
    public void setStartPrice(double strtPrc) {
        String sP = Double.toString(strtPrc);
        startPrice.waitUntil(Condition.visible, wait, waitInterval).setValue(sP);
        Logger.info("Установлена начальная цена");

    }

    //переключаем на следующую страницу таблицы
    public void clickNextPage(String nxtPg) {
        $(nxtPg).click();
    }

    //ожидание, пока прогрузится страница таблицы
    public void waitLoading(String ldr) {
        $(ldr).waitUntil(not(Condition.visible), wait, waitInterval);
        Logger.info("Ожидаю, пока прогрузится таблица");
    }

}
