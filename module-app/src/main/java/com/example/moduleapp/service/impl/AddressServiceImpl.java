package com.example.moduleapp.service.impl;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.context.UserPrincipal;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.AppErrorCode;
import com.example.moduleapp.data.mapper.AddressMapper;
import com.example.moduleapp.data.request.AddressRequest;
import com.example.moduleapp.data.response.AddressResponse;
import com.example.moduleapp.model.tables.pojos.Address;
import com.example.moduleapp.repository.impl.AddressRepository;
import com.example.moduleapp.service.AddressService;
import com.example.moduleapp.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        long userId = user.getUserInfo().getId().longValue();
        address.setUserId(userId);
        if (addressRequest.getIsDefault()) {
            return addressRepository.findDefaultAddressByUserId(userId)
                    .flatMap(addressOptional -> {
                        if (addressOptional.isPresent()) {
                            Address addressOldDefault = addressOptional.get();
                            addressOldDefault.setIsDefault(Byte.valueOf("0"));
                            List<Address> updateList = new ArrayList<>();
                            updateList.add(addressOldDefault);
                            updateList.add(address);
                            return addressRepository.insertUpdateOnDuplicateKey(updateList)
                                    .map(integers -> "SUCCESS");
                        } else {
                            return addressRepository.insert(address)
                                    .map(integers -> "SUCCESS");
                        }
                    });
        } else {
            return addressRepository.insert(address).map(integers -> "SUCCESS");
        }
    }

    @Override
    public Single<List<AddressResponse>> findByUser() {
        UserPrincipal user = authService.getCurrentUser();
        long userId = user.getUserInfo().getId().longValue();
        return addressRepository.findByUserId(userId)
                .map(addressMapper::toAddressResponses);
    }

    @Override
    public Single<String> chooseDefault(Integer addressId) {
        UserPrincipal user = authService.getCurrentUser();
        long userId = user.getUserInfo().getId().longValue();
        return Single.zip(
                addressRepository.findDefaultAddressByUserId(userId),
                addressRepository.findById(addressId),
                (addressOldOptional, addressOptional) -> {
                    if (addressOptional.isEmpty()) {
                        throw new AppException(ErrorCodeBase.NOT_FOUND, "ADDRESS ID");
                    }
                    Address address = addressOptional.get();
                    if (!address.getUserId().equals(userId)) {
                        throw new AppException(AppErrorCode.USER_NOT_HAS_THIS_ADDRESS);
                    }
                    address.setIsDefault(Byte.valueOf("1"));
                    if (addressOldOptional.isPresent()) {
                        Address addressOldDefault = addressOldOptional.get();
                        addressOldDefault.setIsDefault(Byte.valueOf("0"));
                        List<Address> updateList = new ArrayList<>();
                        updateList.add(addressOldDefault);
                        updateList.add(address);
                        return addressRepository.insertUpdateOnDuplicateKey(updateList)
                                .map(integers -> "SUCCESS");
                    } else {
                        return addressRepository.update(addressId, address)
                                .map(integer -> "SUCCESS");
                    }
                }
        ).flatMap(singleValue -> singleValue);
    }
}
