package com.t1tanic.eventone.model.dto;

public record GeoLocationDto(String countryCode, String countryName,
                             String communityCode, String communityName,
                             String provinceCode, String provinceName,
                             String municipalityCode, String municipalityName,
                             String locality, String postalCode) {}
