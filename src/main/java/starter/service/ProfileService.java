package starter.service;

import starter.model.AbstractProfile;

public interface ProfileService {
    AbstractProfile getById(String id);
    
    AbstractProfile getByConfirmUuid(String uuid);

    AbstractProfile update(AbstractProfile profile);

    public AbstractProfile create(String id, String email, String password, boolean confirmed);
}
