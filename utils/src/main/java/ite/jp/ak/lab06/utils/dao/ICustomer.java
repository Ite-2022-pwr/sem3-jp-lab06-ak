package ite.jp.ak.lab06.utils.dao;

import ite.jp.ak.lab06.utils.model.*;

public interface ICustomer extends IResponder {

    /**
     * Metoda response(type,data) ma być wykorzystywana przez Magazyniera do przesyłania odpowiedzi
     * na żądania: register(), getOffer(), getInfo() - na podobnej zasadzie, jak w przypadku Dostawcy,
     * przy czym Magazynier w odpowiedzi na getOffer przesyła listę wszystkich aktualnie dostępnych towarów
     * (pomijamy stronicowanie).
     * @param response
     */
//    @Override
//    void response(Response response);

    /**
     * Metoda putOrder(idd,data) pozwalać ma Dostawcy na przekazanie Klientowi towarów zgodnie
     * ze złożonym przez niego zamówieniem (które Klient złożył u Magazyniera).
     * Atrybut idd to identyfikator Dostawcy (np. "1", natomiast data to lista towarów (np. "towar1,towar2,towar3").
     * Znajomość idd pozwolić ma Klientowi na zwrócenie towarów za pośrednictwem Dostawcy (jeszcze przed pójściem do kasy).
     * @param order
     */
    void putOrder(Order order);

    /**
     * Metoda returnReceipt(data) pozwalać ma Sprzedawcy na przekazanie rachunku.
     * W tym przypadku atrybut data reprezentować ma ten rachunek.
     * @param receipt
     */
    void returnReceipt(Receipt receipt);
}
