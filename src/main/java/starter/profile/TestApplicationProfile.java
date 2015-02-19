package starter.profile;

public class TestApplicationProfile implements ApplicationProfile {
    @Override
    public int getMessagesCacheInterval() {
        return 0;
    }

    @Override
    public boolean isTemplateCacheEnabled() {
        return false;
    }
}
