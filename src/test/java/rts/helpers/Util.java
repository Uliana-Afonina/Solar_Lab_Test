package rts.helpers;

import org.tinylog.Logger;


public class Util {
    //инициализация переменной для хранения значения текущей стоимости
    public static double priceOfLot = 0.00;

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
                priceOfLot *= eur;
                Logger.info("Цена #" + tradeText + " была указана в EUR. Пересчитана в руб.");
                break;
            case "USD":
                priceOfLot = Double.parseDouble(priceText
                        .replace(" ", "")
                        .replace(",", ".")
                        .replace("USD", ""));
                priceOfLot *= usd;
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



