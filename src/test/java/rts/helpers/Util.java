package rts.helpers;

import org.junit.jupiter.api.BeforeEach;
import org.tinylog.Logger;

import java.io.File;


public class Util {
    //инициализация переменной для хранения значения текущей стоимости
    public static double priceOfLot = 0.00;

    public static File fileData = new File("src/test/java/rts/files/Data.ini");


    //Проверка наличия файла Data.ini перед запуском теста
    @BeforeEach
    public void checkFiles() {
        if (!fileData.exists()) {
            Logger.error("Файл Data.ini не найден.");
            System.exit(0);
        }
    }
    //********Метод конвертации валюты*********
    //Этот метод вызывается в методе convertStringToDouble().
    //В prOfLt передать priceOfLot
    //В rate передать eur либо usd (выбор делается в методе convertStringToDouble()

    private static Double convertCurrency(double prOfLt, double rate) {

        prOfLt *= rate;
        return prOfLt;

    }

    //********Метод для пробразования строки-цены в число-цену*********

    //в priceText передать String textCurrency = trade.parent().find(searchPage.price).text();
    //в tradeText передать trade.text() для указания номера в ЕИС лота, цена которого не в рублях

    public static Double convertStringToDouble(String priceText, double usd, double eur, String tradeText) {
// ищем последнее вхождение пробела в строку
        String space = " ";
        int i = priceText.lastIndexOf(space);
// со следующего от пробела знака и до конца строки ищем совпадения EUR, USD, руб.
// преобразуем строку в вещественное число
        switch (priceText.substring(i + 1)) {
            case "EUR":
                priceOfLot = Double.parseDouble(priceText
                        .replace(" ", "")
                        .replace(",", ".")
                        .replace("EUR", ""));
                priceOfLot = convertCurrency(priceOfLot, eur);
                Logger.info("Цена #" + tradeText + " была указана в EUR. Пересчитана в руб.");
                break;
            case "USD":
                priceOfLot = Double.parseDouble(priceText
                        .replace(" ", "")
                        .replace(",", ".")
                        .replace("USD", ""));
                priceOfLot = convertCurrency(priceOfLot, usd);
                Logger.info("Цена #" + tradeText + " была указана в USD. Пересчитана в руб.");
                break;
            case "руб.":
                priceOfLot = Double.parseDouble(priceText
                        .replace(" ", "")
                        .replace(",", ".")
                        .replace("руб.", ""));
                break;

        }
//возвращаем вещественное значение цены лота
        return priceOfLot;
    }


}



