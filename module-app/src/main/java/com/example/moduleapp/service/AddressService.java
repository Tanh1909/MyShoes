package com.example.moduleapp.service;

import com.example.moduleapp.data.request.AddressRequest;
import io.reactivex.rxjava3.core.Single;

public interface AddressService {
    Single<String> create(AddressRequest addressRequest);
}
