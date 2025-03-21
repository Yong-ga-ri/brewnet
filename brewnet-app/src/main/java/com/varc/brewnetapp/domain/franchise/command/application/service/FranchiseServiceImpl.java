package com.varc.brewnetapp.domain.franchise.command.application.service;

import com.varc.brewnetapp.domain.auth.query.service.AuthService;
import com.varc.brewnetapp.domain.franchise.command.application.dto.CreateFranchiseRequestDTO;
import com.varc.brewnetapp.domain.franchise.command.application.dto.DeleteFranchiseRequestDTO;
import com.varc.brewnetapp.domain.franchise.command.application.dto.UpdateFranchiseRequestDTO;
import com.varc.brewnetapp.domain.franchise.command.domain.aggregate.entity.Franchise;
import com.varc.brewnetapp.domain.franchise.command.domain.repository.FranchiseRepository;
import com.varc.brewnetapp.security.service.RefreshTokenService;
import com.varc.brewnetapp.shared.exception.EmptyDataException;
import com.varc.brewnetapp.shared.exception.InvalidDataException;
import com.varc.brewnetapp.shared.utility.TelNumberUtil;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "commandFranchiseService")
public class FranchiseServiceImpl implements FranchiseService {

    private final FranchiseRepository franchiseRepository;
    private final ModelMapper modelMapper;

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public FranchiseServiceImpl(FranchiseRepository franchiseRepository, ModelMapper modelMapper, AuthService authService, RefreshTokenService refreshTokenService) {
        this.franchiseRepository = franchiseRepository;
        this.modelMapper = modelMapper;
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    @Transactional
    public void createFranchise(CreateFranchiseRequestDTO createFranchiseRequestDTO) {
        String[] parts = createFranchiseRequestDTO.getAddress().split("\\s+", 2); // 첫 번째 띄어쓰기 기준으로 나누기

        Franchise franchise = modelMapper.map(createFranchiseRequestDTO, Franchise.class);
        franchise.setCreatedAt(LocalDateTime.now());
        franchise.setCity(parts[0]);
        franchise.setActive(true);
        franchise.setContact(TelNumberUtil.formatTelNumber(createFranchiseRequestDTO.getContact()));

        try {
            franchiseRepository.save(franchise);
        } catch (Exception e) {
            throw new InvalidDataException("가맹점 정보 저장에 실패했습니다");
        }

    }

    @Override
    @Transactional
    public void updateFranchise(UpdateFranchiseRequestDTO updateFranchiseRequestDTO) {
        Franchise franchise = franchiseRepository.findById(updateFranchiseRequestDTO.getFranchiseCode())
            .orElseThrow(() -> new EmptyDataException("가맹점 정보가 없습니다"));

        if(updateFranchiseRequestDTO.getFranchiseName() != null)
            franchise.setFranchiseName(updateFranchiseRequestDTO.getFranchiseName());

        if (updateFranchiseRequestDTO.getAddress() != null){
            String[] parts = updateFranchiseRequestDTO.getAddress().split("\\s+", 2); // 첫 번째 띄어쓰기 기준으로 나누기
            franchise.setAddress(updateFranchiseRequestDTO.getAddress());
            franchise.setCity(parts[0]);
        }

        if(updateFranchiseRequestDTO.getContact() != null)
            franchise.setContact(TelNumberUtil.formatTelNumber(updateFranchiseRequestDTO.getContact()));

        if(updateFranchiseRequestDTO.getName() != null)
            franchise.setName(updateFranchiseRequestDTO.getName());

        if(updateFranchiseRequestDTO.getDetailAddress() != null)
            franchise.setDetailAddress(updateFranchiseRequestDTO.getDetailAddress());

        if(updateFranchiseRequestDTO.getBusinessNumber() != null)
            franchise.setBusinessNumber(updateFranchiseRequestDTO.getBusinessNumber());

        try {
            franchiseRepository.save(franchise);
        } catch (Exception e) {
            throw new InvalidDataException("가맹점 정보 수정에 실패했습니다");
        }
    }

    @Override
    @Transactional
    public void deleteFranchise(DeleteFranchiseRequestDTO deleteFranchiseRequestDTO) {
        int franchiseCode = deleteFranchiseRequestDTO.getFranchiseCode();
        Franchise franchise = franchiseRepository.findById(franchiseCode)
            .orElseThrow(() -> new EmptyDataException("가맹점 정보가 없습니다"));

        franchise.setActive(false);

        franchiseRepository.save(franchise);
        authService.getFranchiseMemberLoginId(franchiseCode)
                .forEach(refreshTokenService::deleteRefreshToken);
    }
}


