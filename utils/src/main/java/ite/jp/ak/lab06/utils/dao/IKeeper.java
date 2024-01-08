package ite.jp.ak.lab06.utils.dao;

import ite.jp.ak.lab06.utils.model.Order;
import ite.jp.ak.lab06.utils.model.Product;
import ite.jp.ak.lab06.utils.model.User;

public interface IKeeper {

    /**
     * Metoda register(role, host, port) pozwalać ma Klientowi i pracownikom (Dostawcy i Sprzedawcy)
     * na zarejestrowanie się w sklepie.
     * W atrybutach tej metody podawane jest, kto jest rejestrowany (np. role = 'deliverer')
     * oraz pod jakim adresem ten ktoś będzie nasłuchiwał na informacje zwrotne (np. host = 'localhost', port = '2000').
     * @param user
     */
    void register(User user);

    /**
     * Metoda uregister(id) pozwalać ma na wyrejestrowanie
     * Klienta lub pracownika (Dostawcy lub Sprzedawcy) o wskazanym id.
     * @param user
     */
    void unregister(User user);

    /**
     * Metoda getOffer(idc) pozwalać ma Klientowi o wskazanym idc
     * na pozyskanie informacji o ofercie sklepu.
     * @param customer
     * @return tablica produktów
     */
    Product[] getOffer(User customer);

    /**
     * Metoda putOrder(idc, data) pozwalać ma Klientowi na złożenie zamówienia na towary.
     * @param customer
     */
    void putOrder(User customer, Product product);

    /**
     * Metoda getOrder(idd) pozwalać ma Dostawcy na pobranie pierwszego nieobsłużonego jeszcze
     * zamówienia Klienta (zakładamy, że Magazyn będzie kolejkował te zamówienia).
     * @param deliver
     * @return zamówienie
     */
    Order getOrder(User deliver);

    /**
     * Metoda returnOrder(data) służyć ma do oddania do magazynu towarów zwróconych przez Klienta.
     * @param product
     */
    void returnOrder(Product product);

    /**
     * Metoda getInfo(id1,id2) pozwalać ma Klientowi lub pracownikowi (Dostawcy lub Sprzedawcy)
     * o id1 na pozyskanie informacji o adresie (hoście i porcie) Klienta lub pracownika (Dostawczy lub Sprzedawcy) o id2.
     * Zakładamy, że zapytanie z id2 = 0 dotyczyć będzie adresu pierwszego "wolnego" sprzedawcy.
     * @param user
     * @param requestedUser
     * @return
     */
    User getInfo(User user, User requestedUser);
}
