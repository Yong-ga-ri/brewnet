package com.varc.brewnetapp.domain.storage.command.application.service;

import com.varc.brewnetapp.domain.storage.command.application.dto.ChangeStockRequestDTO;
import com.varc.brewnetapp.domain.storage.command.application.dto.StorageDeleteRequestDTO;
import com.varc.brewnetapp.domain.storage.command.application.dto.StorageRequestDTO;
import com.varc.brewnetapp.domain.storage.command.domain.aggregate.Stock;

public interface StorageService {

    void createStorage(String loginId, StorageRequestDTO newStorage);

    void editStorage(String loginId, int storageCode, StorageRequestDTO editedStorage);

    void deleteStorage(String loginId, StorageDeleteRequestDTO deleteRequest);

    void changeStock(String loginId, ChangeStockRequestDTO changeRequest);

    Stock setStockReadyToDepart(int storageCode,
                                int itemCode,
                                int quantity
    );
}
