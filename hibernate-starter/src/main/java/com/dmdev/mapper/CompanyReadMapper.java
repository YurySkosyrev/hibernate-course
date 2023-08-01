package com.dmdev.mapper;

import com.dmdev.dto.CompanyReadDto;
import com.dmdev.entity.Company;

public class CompanyReadMapper implements Mapper <Company, CompanyReadDto>{
    @Override
    public CompanyReadDto mapFrom(Company object) {
        return new CompanyReadDto(object.getId(),
                object.getName(),
                object.getLocales());
    }
}
