package com.traintension.core.model.sync;

import com.traintension.common.utils.user.RequiredRole;
import com.traintension.common.utils.user.UserContext;
import com.traintension.core.common.config.ApiConstants;
import com.traintension.core.model.sync.SyncDTO.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(ApiConstants.SYNC)
@RequiredArgsConstructor
@RequiredRole("USER")
@Tag(name = "Sync", description = "Kullanici ve sistem verilerini global version cursor'u ile senkronize eder.")
public class SyncController {
    private final SyncService syncService;

    @GetMapping
    @Operation(
            summary = "Version cursor ile sync cek",
            description = """
                    Kullaniciya ait personal kayitlari, sistem/global kayitlari ve silinme gecmisini
                    global version sirasi ile tek bir event listesi olarak dondurur.
                    Ilk sync icin afterVersion=0 kullanilir.
                    """
    )
    @ApiResponse(responseCode = "200", description = "Sync event sayfasi donduruldu")
    public ResponseEntity<Response> sync(
            @Parameter(description = "Exclusive cursor. Sadece bu version'dan buyuk kayitlar doner.", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) Long afterVersion,
            @Parameter(description = "Sayfa boyutu. Maksimum 150.", example = "150")
            @RequestParam(defaultValue = "150") @Min(1) @Max(150) Integer limit
    ) {
        return ResponseEntity.ok(syncService.sync(UserContext.getId(), afterVersion, limit));
    }
}
