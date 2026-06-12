package com.traintension.core.model.workoutDay.annotations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class UniqueOrderNumbersValidator implements ConstraintValidator<UniqueOrderNumbers, List<? extends OrderedRequest>> {

    @Override
    public boolean isValid(List<? extends OrderedRequest> requests, ConstraintValidatorContext context) {
        // Liste boşsa validasyon başarılıdır
        if (requests == null || requests.isEmpty()) {
            return true;
        }

        // 1. Adım: Sadece null olmayan (set edilecek olan) orderNumber değerlerini topla
        List<Integer> providedOrders = requests.stream()
                .map(OrderedRequest::orderNumber)
                .filter(java.util.Objects::nonNull)
                .toList();

        // 2. Adım: Eğer hepsi null ise (hiçbirine set işlemi yapılmayacaksa) geçerlidir
        if (providedOrders.isEmpty()) {
            return true;
        }

        // 3. Adım: Girilen sayıların benzersizliğini kontrol et
        long uniqueCount = providedOrders.stream()
                .distinct()
                .count();

        // Eğer girilen değerlerin sayısı ile benzersiz değerlerin sayısı eşitse validasyon geçer.
        return uniqueCount == providedOrders.size();
    }
}