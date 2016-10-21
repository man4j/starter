package starter.profile;

public interface ApplicationProfile {
    int getMessagesCacheInterval();
    
    boolean isTemplateCacheEnabled();
    
    String getDbUrl();
    
    String getDbUser();
    
    String getDbPassword();
    
    boolean isShowSql();
}
