package rts.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;


public class SearchPage {
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
}




