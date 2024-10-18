package com.example.moduleapp.service.impl;

import com.example.common.context.UserPrincipal;
import com.example.moduleapp.data.mapper.AddressMapper;
import com.example.moduleapp.data.request.AddressRequest;
import com.example.moduleapp.model.tables.pojos.Address;
import com.example.moduleapp.repository.impl.AddressRepository;
import com.example.moduleapp.service.AddressService;
import com.example.moduleapp.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final AuthService authService;

    @Override
    public Single<String> create(AddressRequest addressRequest) {
        Address address = addressMapper.toAddress(addressRequest);
        UserPrincipal user = authService.getCurrentUser();
        address.setUserId(user.getUserInfo().getId().longValue());
        return addressRepository.insert(address).map(integer -> "SUCCESS");
    }
}
