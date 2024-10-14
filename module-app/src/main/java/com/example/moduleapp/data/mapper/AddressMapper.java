package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.AddressRequest;
import com.example.moduleapp.data.response.AddressResponse;
import com.example.moduleapp.model.tables.pojos.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper extends ConvertMapper {
    Address toAddress(AddressRequest addressRequest);

    AddressResponse toAddressResponse(Address address);


}
