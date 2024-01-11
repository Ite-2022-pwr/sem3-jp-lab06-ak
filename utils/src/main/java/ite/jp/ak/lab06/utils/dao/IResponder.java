package ite.jp.ak.lab06.utils.dao;

import ite.jp.ak.lab06.utils.enums.ResponseType;
import ite.jp.ak.lab06.utils.model.Response;
import ite.jp.ak.lab06.utils.model.User;

public interface IResponder {
    void response(User user, Response response);
}
