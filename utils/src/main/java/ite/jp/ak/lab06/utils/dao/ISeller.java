package ite.jp.ak.lab06.utils.dao;

import ite.jp.ak.lab06.utils.model.Order;
import ite.jp.ak.lab06.utils.model.Product;
import ite.jp.ak.lab06.utils.model.Response;
import ite.jp.ak.lab06.utils.model.User;

public interface ISeller extends IResponder {

    /**
     * Metoda response(type,data) ma być wykorzystywana przez Magazyniera
     * do przesyłania odpowiedzi na żądania: register(), getOffer(), getInfo()
     * - na podobnej zasadzie, jak w przypadku Dostawcy.
     * @param response
     */
//    @Override
//    void response(Response response);

    /**
     * Metoda acceptOrder(idc,data) pozwalać ma Klientowi na przekazanie Sprzedawcy towarów,
     * które zamierza zakupić bądź zwrócić.
     * Atrybut idc to identyfikator Klienta (np. "4"),
     * natomiast data to lista towarów (np. "towarBrany1,towarBrany2;towarZwracany3").
     * Znajomość idc pozwolić ma Sprzedawcy na przekazanie rachunku do właściwego Klienta.
     * Jeśli na liście towarów są jakieś pozycje do zwrotu, wtedy Sprzedawca zwraca je Magazynierowi.
     * @param order
     */
    void acceptOrder(Order order);
}
