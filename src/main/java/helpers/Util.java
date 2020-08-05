package helpers;

import org.junit.jupiter.api.BeforeEach;
import org.tinylog.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Util {
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

    public static Double convertStringToToDouble(String priceText) {
        return Double.parseDouble(priceText.substring(0, priceText.lastIndexOf(" "))
                .replace(" ", "")
                .replace(",", "."));
    }

    //в priceText передать String textCurrency = trade.parent().find(searchPage.price).text();
    //в tradeText передать trade.text() для указания номера в ЕИС лота, цена которого не в рублях
    public static double convertCurrency(String priceText, double usd, double eur, String tradeText) {
        double priceOfLot = 0.00;
// ищем последнее вхождение пробела в строку
// со следующего от пробела знака и до конца строки ищем совпадения EUR, USD, руб.
// вызываем метод convertStringToToDouble() для преобразования строки в вещественное число
// конвертируем валюту, если есть необходимость
        switch (priceText.substring(priceText.lastIndexOf(" ") + 1)) {
            case "EUR":
                Logger.info("Цена #" + tradeText + " была указана в EUR. Пересчитана в руб.");
                return convertStringToToDouble(priceText) * eur;
            case "USD":
                Logger.info("Цена #" + tradeText + " была указана в USD. Пересчитана в руб.");
                return convertStringToToDouble(priceText) * usd;
            case "руб.":
                return convertStringToToDouble(priceText);
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