package com.traintension.core.model.muscle;

import com.traintension.core.generated.tables.records.MuscleRecord;
import com.traintension.core.model.muscle.MuscleDTO.*;

import java.util.List;


public class MuscleMapper {
    public static Response fromRecord(MuscleRecord record) {
        return Response.builder()
                .id(record.getId())
                .name(record.getName())
                .description(record.getDescription())
                .mediaUrl(record.getMediaUrl())
                .createdAt(record.getCreatedAt())
                .version(record.getVersion())
                .build();
    }

    public static List<MuscleRecord> fromDTO(CreateGlobalsRequest request) {
        return request.items().stream()
                .map(r -> {
                    MuscleRecord record = new MuscleRecord();
                    record.setName(r.name());
                    record.setDescription(r.description());
                    record.setMediaUrl(r.mediaUrl());
                    return record;
                }).toList();
    }

    public static List<MuscleRecord> fromDTO(UpdateGlobalsRequest request) {
        return request.items().stream()
                .map(r -> {
                    MuscleRecord record = new MuscleRecord();
                    record.setId(r.id());
                    if (r.name() != null) record.setName(r.name());
                    if (r.description() != null) record.setDescription(r.description());
                    if (r.mediaUrl() != null) record.setMediaUrl(r.mediaUrl());
                    return record;
                }).toList();
    }
}
