package com.traintension.core.common.annotations.uniqueIdsValidation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueIdsValidator.class) // Doğrulama mantığını bu sınıfa devrediyoruz
public @interface UniqueIds {

    // Varsayılan hata mesajımız (Senin özel Exception sınıfına bu mesaj gidecek)
    String message() default "Duplicate records with the same ID are not allowed. The client must handle deduplication (squashing)";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
