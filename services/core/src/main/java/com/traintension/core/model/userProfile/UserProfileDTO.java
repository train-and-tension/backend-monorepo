package com.traintension.core.model.userProfile;

import com.traintension.core.model.userProfile.annotation.ValidTimezone;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Kullanici profili DTO'lari")
public class UserProfileDTO {

    @Builder
    @Schema(description = "Kullanici profili yanit modeli")
    public record Response(
            @Schema(description = "Kullanici profili UUID'si") UUID id,
            @Schema(description = "Ad") String firstName,
            @Schema(description = "Soyad") String lastName,
            @Schema(description = "Kullanici adi (8-20 karakter, benzersiz)") String username,
            @Schema(description = "Profil fotografi URL'i") String profilePicURL,
            @Schema(description = "Saat dilimi (IANA format)", example = "Europe/Istanbul") String timezone,
            @Schema(description = "Olusturulma tarihi") OffsetDateTime createdAt,
            @Schema(description = "Version numarasi") Long version
    ) {}

    @Schema(description = "Saat dilimi degisikligi yaniti")
    public record TimezoneResponse(
            @Schema(description = "Guncellenen saat dilimi", example = "Europe/Istanbul") String timezone
    ) {}

    @Schema(description = "Kullanici adi degisikligi yaniti")
    public record UsernameResponse(
            @Schema(description = "Guncellenen kullanici adi") String username
    ) {}

    @Schema(description = "Kullanici adi degistirme istegi")
    public record ChangeUsernameRequest(
            @NotBlank @Size(min = 8, max = 20)
            @Schema(description = "Yeni kullanici adi (8-20 karakter)", example = "fitnessuser1") String username
    ) {}

    @Schema(description = "Saat dilimi degistirme istegi")
    public record ChangeTimezoneRequest(
            @NotNull @ValidTimezone
            @Schema(description = "Yeni saat dilimi (IANA format)", example = "Europe/Istanbul") String timezone
    ) {}

    @Schema(description = "Profil guncelleme istegi. Sadece gonderilen alanlar guncellenir.")
    public record UpdateProfileRequest(
            @Schema(description = "Yeni ad (opsiyonel)", example = "Ahmet") String firstName,
            @Schema(description = "Yeni soyad (opsiyonel)", example = "Yilmaz") String lastName,
            @Schema(description = "Yeni profil fotografi URL'i (opsiyonel)") String profilePicUrl
    ) {}

}
