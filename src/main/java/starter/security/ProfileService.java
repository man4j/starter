package starter.security;

import starter.model.AbstractProfile;

public interface ProfileService {
    AbstractProfile getById(String id);
    
    AbstractProfile getByConfirmUuid(String uuid);

    AbstractProfile saveOrUpdate(AbstractProfile profile);

    public AbstractProfile createProfile(String id, String email, String password, boolean confirmed);
}
