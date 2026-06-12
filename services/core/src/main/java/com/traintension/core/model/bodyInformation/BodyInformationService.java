package com.traintension.core.model.bodyInformation;

import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.model.bodyInformation.BodyInformationDTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.traintension.core.generated.Tables.BODY_INFORMATION;
import static com.traintension.core.generated.Tables.MEASUREMENT_HISTORY;

@Service
@Slf4j
@RequiredArgsConstructor
public class BodyInformationService {

    private final DSLContext dsl;

    @Transactional
    public Response create(CreatePersonalRequest request, UUID userId) {
        Response response = dsl.insertInto(BODY_INFORMATION)
                .set(BodyInformationMapper.fromDTO(request, userId))
                .returning()
                .fetchOne(BodyInformationMapper::fromRecord);

        dsl.insertInto(MEASUREMENT_HISTORY)
                .set(BodyInformationMapper.toMeasurementHistory(request, userId))
                .execute();

        return response;
    }

    @Transactional
    public Response updateMeasurements(UpdateMeasurementsRequest request, UUID userId) {
        Response response = dsl.update(BODY_INFORMATION)
                .set(BodyInformationMapper.fromDTO(request))
                .where(BODY_INFORMATION.ID.eq(request.id()))
                .and(BODY_INFORMATION.USER_PROFILE_ID.eq(userId))
                .returning()
                .fetchOptional(BodyInformationMapper::fromRecord)
                .orElseThrow(NotFoundException::new);

        dsl.insertInto(MEASUREMENT_HISTORY)
                .set(BodyInformationMapper.toMeasurementHistory(request, userId))
                .execute();

        return response;
    }

    @Transactional
    public Response updateProfile(UpdateProfileRequest request, UUID userId) {
        return dsl.update(BODY_INFORMATION)
                .set(BodyInformationMapper.fromDTO(request))
                .where(BODY_INFORMATION.ID.eq(request.id()))
                .and(BODY_INFORMATION.USER_PROFILE_ID.eq(userId))
                .returning()
                .fetchOptional(BodyInformationMapper::fromRecord)
                .orElseThrow(NotFoundException::new);
    }
}
