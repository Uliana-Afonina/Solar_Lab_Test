package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.tinylog.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;
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
    public final String NEXT_PAGE = "#next_t_BaseMainContent_MainContent_jqgTrade_toppager";
    //ячейка с таблицы с номером ЕИС
    public final String OOS_NUMBER = "[aria-describedby=\"BaseMainContent_MainContent_jqgTrade_OosNumber\"]";
    //ячейка таблицы со статусом заявки
    public final String STATE = "[aria-describedby=\"BaseMainContent_MainContent_jqgTrade_LotStateString\"]";
    //текстовое поле "Начальная цена"
    public final String PRICE = "[aria-describedby=\"BaseMainContent_MainContent_jqgTrade_StartPrice\"]";
    //всплывающее окно Загрузка
    public final String LOADER = "#load_BaseMainContent_MainContent_jqgTrade";

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
    public void clickNextPage(String NEXT_PAGE) {
        $(NEXT_PAGE).click();
    }

    //ожидание, пока прогрузится страница таблицы
    public void waitLoading(String LOADER) {
        $(LOADER).waitUntil(not(Condition.visible), wait, waitInterval);
        Logger.info("Ожидаю, пока прогрузится таблица");
    }

    //считываем данные о дате публикации извещений и данные о курсах валют
    public String readData(String key) throws IOException {
        //создаем объект Properties и загружаем в него данные из файла.
        //получаем значения свойств из объекта Properties
        Properties properties = new Properties();
        properties.load(new FileReader(fileData));
        return properties.getProperty(key);
    }

    //инициализируем строковую переменную для хранения цены лота
    public String textCurrency;

    //ищем элемент, отвечающий за цену лота
    public String findPrice(SelenideElement trade) {
        //Выбираем те значения, статус которых не отменен
        if (!trade.parent().find(STATE).text().equals("Отменена")) {
            textCurrency = trade.parent().find(PRICE).text();
        }
        return textCurrency;
    }

}
