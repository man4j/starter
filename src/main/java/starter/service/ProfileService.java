package starter.service;

import starter.model.AbstractProfile;

public interface ProfileService {
    AbstractProfile getByEmail(String email);
    
    AbstractProfile getByConfirmUuid(String uuid);

    AbstractProfile update(AbstractProfile profile);

    public AbstractProfile create(String email, String password, boolean confirmed);
}
