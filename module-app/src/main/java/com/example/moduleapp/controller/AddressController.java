package com.example.moduleapp.controller;

import com.example.common.data.response.ApiResponse;
import com.example.moduleapp.data.request.AddressRequest;
import com.example.moduleapp.data.response.AddressResponse;
import com.example.moduleapp.service.AddressService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Single<ApiResponse<String>> create(@RequestBody AddressRequest addressRequest) {
        return addressService.create(addressRequest).map(ApiResponse::success);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse<List<AddressResponse>>> getAll() {
        return addressService.findByUser().map(ApiResponse::success);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Single<ApiResponse<String>> chooseDefault(@PathVariable Integer id) {
        return addressService.chooseDefault(id).map(ApiResponse::success);
    }
}
