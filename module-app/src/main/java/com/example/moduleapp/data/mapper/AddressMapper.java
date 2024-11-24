package com.example.moduleapp.data.mapper;

import com.example.moduleapp.data.request.AddressRequest;
import com.example.moduleapp.data.response.AddressResponse;
import com.example.moduleapp.data.response.OrderResponse;
import com.example.moduleapp.model.tables.pojos.Address;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AddressMapper extends ConvertMapper {
    Address toAddress(AddressRequest addressRequest);

    AddressResponse toAddressResponse(Address address);

    List<AddressResponse> toAddressResponses(List<Address> addresses);

    OrderResponse.AddressResponse toAddressOrderResponse(Address address);

    List<OrderResponse.AddressResponse> toAddressOrderResponses(List<Address> addresses);
}
