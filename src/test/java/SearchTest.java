import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.Test;
import org.tinylog.Logger;
import rts.pages.SearchPage;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Properties;

import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Selenide.$;
import static java.math.BigDecimal.ROUND_DOWN;

public class SearchTest {
    SearchPage searchPage = new SearchPage();

    @Test
    public void countSums() throws IOException {
        Selenide.open("https://223.rts-tender.ru/supplier/auction/trade/search.aspx");
        Logger.info("Открыт браузер");


        searchPage.checkBox223FL.waitUntil(Condition.visible, 10000, 50).setSelected(true);//чек 223-ФЗ
        Logger.info("Чек-бокс '223-ФЗ' выбран");
        searchPage.checkBoxCP.waitUntil(Condition.visible, 10000, 50).setSelected(true);//чек Коммерческая закупка
        Logger.info("Чек-бокс 'Коммерческая закупка' выбран");
        searchPage.startPrice.waitUntil(Condition.visible, 10000, 50).setValue("0");//установка начальной цены
        Logger.info("Установлена начальная цена от 0");
        //проверка исключения (вдруг файл не существует)

        double usd = 0.0;
        double eur = 0.0;
        try {
            File myFile = new File("src/test/java/rts/files/Data.ini");
            Logger.info("Файл Data.ini успешно открыт.");

            //создаем объект Properties и загружаем в него данные из файла.
            Properties properties = new Properties();
            properties.load(new FileReader(myFile));

            //получаем значения свойств из объекта Properties
            String BEGIN_OF_NOTICE = properties.getProperty("BEGIN_OF_NOTICE"); //начальная дата публикации извещения
            String END_OF_NOTICE = properties.getProperty("END_OF_NOTICE"); //конечная дата конца публикации извещения
            String USD = properties.getProperty("USD"); //курс доллара к рублю
            String EUR = properties.getProperty("EUR"); //курс евро к рублю
            usd = Double.parseDouble(USD);
            eur = Double.parseDouble(EUR);

            //устанавливаем диапазон дат публикаций извезений
            searchPage.dateFrom.waitUntil(Condition.visible, 10000, 50).setValue(BEGIN_OF_NOTICE);
            searchPage.dateTo.waitUntil(Condition.visible, 10000, 50).setValue(END_OF_NOTICE);
            Logger.info("Даты начала и конца извещения извлечены из файла Data.ini");

        } catch (IOException e) {
            Logger.error("Файл Data.ini не найден");
            e.printStackTrace();
        }

        searchPage.buttonSearch.waitUntil(Condition.visible, 10000, 50).click();//нажатие кнопки Поиск
        Logger.info("Нажата кнопка 'Поиск'");
        //ждем, пока прогрузится таблица (должно исчезнуть окно Загрузка...)
        Logger.info("Ожидаю, пока прогрузится таблица");
        if ($(searchPage.loader).isDisplayed()) {
            $(searchPage.loader).waitUntil(not(Condition.visible), 10000, 50);
        }

        double sum = 0.0;
        int kol = 0;
        double currentSum = 0;
        //до тех пор, пока кнопка перехода на следующую страницу таблицы кликабельная,
        //считаем количество и сумму лотов
        do {
            if ($(searchPage.loader).isDisplayed()) {
                $(searchPage.loader).waitUntil(not(Condition.visible), 10000, 50);
            }
            //поиск по коллекции. Выбираем те строки, где присутствует номер ЕИС
            ElementsCollection trades = Selenide.$$(searchPage.oosNumber);
            trades = trades.filterBy(not(Condition.empty));
            for (SelenideElement trade : trades) {
                //Выбираем те значения, статус которых не отменен
                if (!trade.parent().find(searchPage.state).text().equals("Отменена")) {

                    //переводим строку с ценой в числовой формат, откидываем нечисленные символы
                    //проверяем, в какой валюте указана цена. Переводим в рубли, если есть необходимость
                    //курс евро и доллара подтягивается из файла Data.ini
                    if (trade.parent().find(searchPage.price).text().matches("(.*)EUR(.*)")) {
                        currentSum = Double.parseDouble(
                                trade.parent().find(searchPage.price).text()
                                        .replace(" ", "")
                                        .replace(",", ".")
                                        .replace("EUR", "")
                        );
                        currentSum = currentSum * eur;
                        Logger.info("Цена #" + trade.text() + " была указана в EUR. Пересчитана в руб.");

                    } else if (trade.parent().find(searchPage.price).text().matches("(.*)USD(.*)")) {
                        currentSum = Double.parseDouble(
                                trade.parent().find(searchPage.price).text()
                                        .replace(" ", "")
                                        .replace(",", ".")
                                        .replace("USD", "")
                        );
                        currentSum = currentSum * usd;
                        Logger.info("Цена #" + trade.text() + " была указана в USD. Пересчитаны в руб.");
                    } else if (trade.parent().find(searchPage.price).text().matches("(.*)руб(.*)")) {
                        currentSum = Double.parseDouble(
                                trade.parent().find(searchPage.price).text()
                                        .replace(" руб.", "")
                                        .replace(" ", "")
                                        .replace(",", ".")
                        );
                    }

                    //переводим числа из экспоненциального формата в "читабельный"
                    BigDecimal bd = new BigDecimal(currentSum).setScale(2, ROUND_DOWN);

                    // System.out.println("Trade #" + trade.text() + " Sum:" + bd);
                    sum += currentSum;
                    kol++;
                    Logger.info(kol + ". Цена #" + trade.text() + " = " + bd + " руб.");

                }
            }
            $(searchPage.nextPage).click();
        } while (!$(searchPage.nextPage).has(Condition.cssClass("ui-state-disabled")));
        Logger.info("Достигли конца таблицы");
        //переводим общую сумму закупок из экспоненциальной в "читабельную" форму
        BigDecimal allSum = new BigDecimal(sum).setScale(2, ROUND_DOWN);

        String text = "Количество лотов: " + kol + "\nСумма лотов: " + allSum + " руб.";
        Logger.info("Количество лотов: " + kol + "\nСумма лотов: " + allSum + " руб.");
        //создаем новый экземпляр файла по заданному пути
        File file = new File("src/test/java/rts/files/SumKol.txt");
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
