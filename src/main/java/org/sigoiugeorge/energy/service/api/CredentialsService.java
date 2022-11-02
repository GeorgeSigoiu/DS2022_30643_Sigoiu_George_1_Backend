package org.sigoiugeorge.energy.service.api;

import org.sigoiugeorge.energy.model.Credentials;

public interface CredentialsService extends CrudOperationsService<Credentials>  {
    Boolean usernameIsUnique(String username);
}
