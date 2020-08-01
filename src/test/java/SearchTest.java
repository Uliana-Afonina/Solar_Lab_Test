import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;
import rts.helpers.Util;
import rts.pages.SearchPage;

import java.io.*;
import java.math.BigDecimal;
import java.util.Properties;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;
import static java.math.BigDecimal.ROUND_DOWN;


public class SearchTest extends Util {

    SearchPage searchPage = new SearchPage();

    // Util util = new Util();
    int wait = 100000;
    int waitInterval = 50;

    @Test

    public void countSums() throws IOException {

        Selenide.open(SearchPage.url);
        Logger.info("Открыт браузер");

        searchPage.checkBox223FL.waitUntil(Condition.visible, wait, waitInterval).setSelected(true);//чек 223-ФЗ
        Logger.info("Чек-бокс '223-ФЗ' выбран");
        searchPage.checkBoxCP.waitUntil(Condition.visible, wait, waitInterval).setSelected(true);//чек Коммерческая закупка
        Logger.info("Чек-бокс 'Коммерческая закупка' выбран");
        searchPage.startPrice.waitUntil(Condition.visible, wait, waitInterval).setValue("0");//установка начальной цены
        Logger.info("Установлена начальная цена от 0");

        double usd = 0.0;
        double eur = 0.0;

        //создаем объект Properties и загружаем в него данные из файла.
        Properties properties = new Properties();
        properties.load(new FileReader(fileData));

        //получаем значения свойств из объекта Properties
        String BEGIN_OF_NOTICE = properties.getProperty("BEGIN_OF_NOTICE"); //начальная дата публикации извещения
        String END_OF_NOTICE = properties.getProperty("END_OF_NOTICE"); //конечная дата конца публикации извещения
        String USD = properties.getProperty("USD"); //курс доллара к рублю
        String EUR = properties.getProperty("EUR"); //курс евро к рублю
        usd = Double.parseDouble(USD);
        eur = Double.parseDouble(EUR);

        //устанавливаем диапазон дат публикаций извезений
        searchPage.dateFrom.waitUntil(Condition.visible, wait, waitInterval).setValue(BEGIN_OF_NOTICE);
        searchPage.dateTo.waitUntil(Condition.visible, wait, waitInterval).setValue(END_OF_NOTICE);
        Logger.info("Даты начала и конца извещения извлечены из файла Data.ini");

        searchPage.buttonSearch.waitUntil(Condition.visible, wait, waitInterval).click();//нажатие кнопки Поиск
        Logger.info("Нажата кнопка 'Поиск'");
        //ждем, пока прогрузится таблица (должно исчезнуть окно Загрузка...)
        Logger.info("Ожидаю, пока прогрузится таблица");
        $(searchPage.loader).waitUntil(not(Condition.visible), wait, waitInterval);

        double sum = 0.0;
        int count = 0;
        double currentSum = 0;
        //до тех пор, пока кнопка перехода на следующую страницу таблицы кликабельная,
        //считаем количество и сумму лотов
        do {
            if ($(searchPage.loader).isDisplayed()) {
                $(searchPage.loader).waitUntil(not(Condition.visible), wait, waitInterval);
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
            $(searchPage.nextPage).click();
        } while (!$(searchPage.nextPage).has(Condition.cssClass("ui-state-disabled")));
        Logger.info("Достигли конца таблицы");
        //переводим общую сумму закупок из экспоненциальной в "читабельную" форму
        BigDecimal allSum = new BigDecimal(sum).setScale(2, ROUND_DOWN);

        String text = "Количество лотов: " + count + "\nСумма лотов: " + allSum + " руб.";
        Logger.info("Количество лотов: " + count + "\nСумма лотов: " + allSum + " руб.");
        //создаем новый экземпляр файла ,в который запишем результаты выполнения теста, по заданному пути
        File file = new File("src/test/java/rts/files/SumCountLots.txt");
        try {
            //проверяем, что файл существует. Если нет, то создаем его
            if (!file.exists()) {
                file.createNewFile();
                Logger.info("Файл успешно создан");
            }

            try (PrintWriter out = new PrintWriter(file)) {
                //Записываем текст в файл
                out.print(text);
            }
            //закрываем файл
        } catch (IOException e) {
            Logger.error("Ошибка при записи результатов в файл");
            throw new RuntimeException(e);
        }

    }
}
