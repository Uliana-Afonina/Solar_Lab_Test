package helpers;

import org.junit.jupiter.api.BeforeEach;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Util {
    //инициализация переменной для хранения значения текущей стоимости
    public static double priceOfLot = 0.00;
    //создаем объекты для файла с данными и для файла с результатами теста
    public static File fileData = new File("src/main/resources/files/Data.ini");
    public static File fileResult = new File("src/main/resources/files/SumCountLots.txt");


    //Проверка наличия файла Data.ini перед запуском теста
    @BeforeEach
    public void checkFiles() {
        if (!fileData.exists()) {
            Logger.error("Файл Data.ini не найден.");
            System.exit(0);
        }
    }

    //Метод для пребразования цены из строки в вещественное число
    //Этот метод вызывается в методе convertCurrency().
    //в priceText передать String textCurrency = trade.parent().find(searchPage.price).text();
    //В rate передать eur либо usd (выбор делается в методе convertCurrency()
    public static Double convertStringToToDouble(String priceText, String rate) {

        priceOfLot = Double.parseDouble(priceText
                .replace(" ", "")
                .replace(",", ".")
                .replace(rate, ""));

        return priceOfLot;
    }

    //в priceText передать String textCurrency = trade.parent().find(searchPage.price).text();
    //в tradeText передать trade.text() для указания номера в ЕИС лота, цена которого не в рублях

    public static double convertCurrency(String priceText, double usd, double eur, String tradeText) {
// ищем последнее вхождение пробела в строку
        String space = " ";
        int i = priceText.lastIndexOf(space);
// со следующего от пробела знака и до конца строки ищем совпадения EUR, USD, руб.
// вызываем метод convertStringToToDouble() для преобразования строки в вещественное число
// конвертируем валюту, если есть необходимость
        switch (priceText.substring(i + 1)) {
            case "EUR":
                priceOfLot = convertStringToToDouble(priceText, "EUR") * eur;
                Logger.info("Цена #" + tradeText + " была указана в EUR. Пересчитана в руб.");
                return priceOfLot;
            case "USD":
                priceOfLot = convertStringToToDouble(priceText, "USD") * usd;
                Logger.info("Цена #" + tradeText + " была указана в USD. Пересчитана в руб.");
                return priceOfLot;
            case "руб.":
                priceOfLot = convertStringToToDouble(priceText, "руб.");
                return priceOfLot;

        }
//возвращаем вещественное значение цены лота
        return priceOfLot;
    }

    public static void writeResults(String text) throws IOException {

        if (!fileResult.exists()) {
            fileResult.createNewFile();
            Logger.info("Файл успешно создан");
        }
        try (PrintWriter out = new PrintWriter(fileResult)) {
            //Записываем текст в файл
            out.print(text);
        } catch (IOException e) {
            Logger.error("Ошибка при записи результатов в файл");
            throw new RuntimeException(e);
        }
    }

}