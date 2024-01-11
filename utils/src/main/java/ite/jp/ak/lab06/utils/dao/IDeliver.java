package ite.jp.ak.lab06.utils.dao;

import ite.jp.ak.lab06.utils.model.Order;
import ite.jp.ak.lab06.utils.model.Product;
import ite.jp.ak.lab06.utils.model.Response;

public interface IDeliver extends IResponder {

    /**
     * Metoda response(type,data) pozwalać ma Magazynierowi na przesłanie odpowiedzi
     * do którejś z wywołanych wcześniej przez Dostawcę metod: register(), getOrder(), getInfo().
     * Pierwszy atrybut tej metody określa typ odpowiedzi, natomiast data reprezentuje treść odpowiedzi.
     * Przykładowo, wywołanie metody response() z type="registerResponse" oraz data="10
     * może oznaczać, że Magazynier zareagował na metodę register(),
     * przyznając Dostawcy identyfikator idd=10.
     * Wywołanie metody response() z type="getOrderResponse" oraz data="3,towar1,towar2"
     * może oznaczać, że Klient o identyfikatorze idc=10 zamówił towar1 oraz towar2.
     * Wywołanie metody response() z type="getInfoResponse", data="localhost,4000" może oznaczać,
     * że adres Klienta, o którego zapytał Dostawca, to host=localhost, port=400.
     * @param response
     */
//    @Override
//    void response(Response response);

    /**
     * Metoda returnOrder(data) pozwalać ma Klientowi zwrócić towary, które wcześniej otrzymał.
     * Towary te zapisane są w atrybucie data (np. data="towar1").
     * Lista zwracanych atrybutów musi mieć część wspólną z listą towarów dostarczonych Klientowi przez Dostawcę.
     * @param order
     */
    void returnOrder(Order order);
}
