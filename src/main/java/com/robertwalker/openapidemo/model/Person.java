package com.robertwalker.openapidemo.model;

import lombok.Builder;
import lombok.Data;
import lombok.With;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Data
@Builder
@With
public class Person {
    private UUID id;
    private String firstName;
    private String middleName;
    private String lastName;

    public String getFullName() {
        List<String> components = List.of(trimToEmpty(firstName), trimToEmpty(middleName), trimToEmpty(lastName));
        List<String> compactComponents = components.stream().filter(StringUtils::isNotBlank).toList();
        return join(compactComponents, ' ');
    }
}
