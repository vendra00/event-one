package com.t1tanic.eventone.model.dto;

public record GeoResolveDto(
        String communityCode, String communityName,
        String provinceCode,  String provinceName,
        String municipalityCode, String municipalityName
) {}
