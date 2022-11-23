package org.sigoiugeorge.energy.service.api;

import org.sigoiugeorge.energy.model.User;

public interface UserService extends CrudOperationsService<User> {

    User getUser(String username);

}
