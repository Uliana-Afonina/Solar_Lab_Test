import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import helpers.Util;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;
import pages.SearchPage;

import java.io.*;
import java.math.BigDecimal;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;
import static java.math.BigDecimal.ROUND_DOWN;


public class SearchTest extends Util {

    SearchPage searchPage = new SearchPage();

    @Test

    public void countSums() throws IOException {

        Selenide.open(SearchPage.url);
        Logger.info("Открыт браузер");
        //чек-бокс 223-ФЗ
        searchPage.setCheckBox(searchPage.checkBox223FL);
        //чек-бокс Коммерческая закупка
        searchPage.setCheckBox(searchPage.checkBoxCP);
        //установка начальной цены
        searchPage.setStartPrice(0);
        //начальная дата публикации извещения
        String BEGIN_OF_NOTICE = searchPage.readData("BEGIN_OF_NOTICE");
        //конечная дата конца публикации извещения
        String END_OF_NOTICE = searchPage.readData("END_OF_NOTICE");
        //курс доллара к рублю
        String USD = searchPage.readData("USD");
        //курс евро к рублю
        String EUR = searchPage.readData("EUR");
        //преобразовывем курсы валют из строки в вещественное число
        double usd = Double.parseDouble(USD);
        double eur = Double.parseDouble(EUR);

        //устанавливаем диапазон дат публикаций извезений
        searchPage.setDataBegin(BEGIN_OF_NOTICE);
        searchPage.setDataEnd(END_OF_NOTICE);

        //нажатие кнопки Поиск
        searchPage.clickSearchButton();

        //ждем, пока прогрузится таблица (должно исчезнуть окно Загрузка...)
        searchPage.waitLoading(searchPage.loader);

        double sum = 0.0;
        int count = 0;
        double currentSum;
        //до тех пор, пока кнопка перехода на следующую страницу таблицы кликабельная,
        //считаем количество и сумму лотов
        do {
            if ($(searchPage.loader).isDisplayed()) {
                searchPage.waitLoading(searchPage.loader);
            }
            //поиск по коллекции. Выбираем те строки, где присутствует номер ЕИС
            ElementsCollection trades = Selenide.$$(searchPage.oosNumber).filterBy(not(Condition.empty));

            for (SelenideElement trade : trades) {
                //Выбираем те значения, статус которых не отменен
                if (!trade.parent().find(searchPage.state).text().equals("Отменена")) {

                    //переводим строку с ценой в числовой формат, откидываем нечисленные символы
                    //проверяем, в какой валюте указана цена. Переводим в рубли, если есть необходимость
                    //курс евро и доллара подтягивается из файла Data.ini

                    String textCurrency = trade.parent().find(searchPage.price).text();
                    //преобразовываем цену из текстового формата в числовой
                    currentSum = Util.convertStringToDouble(textCurrency, usd, eur, trade.text());

                    //переводим числа из экспоненциального формата в "читабельный"
                    BigDecimal bd = new BigDecimal(currentSum).setScale(2, ROUND_DOWN);
                    sum += currentSum;
                    count++;
                    Logger.info(count + ". Цена #" + trade.text() + " = " + bd + " руб.");

                }
            }
            //переходим на следующую страницу таблицы
            searchPage.clickNextPage(searchPage.nextPage);
        } while (!$(searchPage.nextPage).has(Condition.cssClass("ui-state-disabled")));
        Logger.info("Достигли конца таблицы");
        //переводим общую сумму закупок из экспоненциальной в "читабельную" форму
        BigDecimal allSum = new BigDecimal(sum).setScale(2, ROUND_DOWN);

        String text = "Количество лотов: " + count + "\nСумма лотов: " + allSum + " руб.";
        Logger.info("Количество лотов: " + count + "\nСумма лотов: " + allSum + " руб.");
        //записываем результат выполнения теста в файл SumCountLots.txt
        Util.writeResults(text);

    }
}
