package com.varc.brewnetapp.domain.storage.command.domain.repository;

import com.varc.brewnetapp.domain.storage.command.domain.aggregate.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Integer> {

    Storage findByStorageCodeAndActiveTrue(int storageCode);
}
