package com.example.moduleapp.service;

import com.example.moduleapp.data.request.AddressRequest;
import com.example.moduleapp.data.response.AddressResponse;
import io.reactivex.rxjava3.core.Single;

import java.util.List;

public interface AddressService {
    Single<String> create(AddressRequest addressRequest);

    Single<List<AddressResponse>> findByUser();

    Single<String> chooseDefault(Integer addressId);
}
