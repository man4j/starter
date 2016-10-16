package starter.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeConverter;

public class HibernateString2SetConverter implements AttributeConverter<Set<String>, String> {
    public String convertToDatabaseColumn(Set<String> attribute) {
        if (attribute.isEmpty()) return null;
        
        return String.join(",", attribute.toArray(new String[] {}));
    }

    public Set<String> convertToEntityAttribute(String dbData) {
        if (dbData == null) return new HashSet<>();
        
        return new HashSet<>(Arrays.asList(dbData.split(",")));
    }
}
