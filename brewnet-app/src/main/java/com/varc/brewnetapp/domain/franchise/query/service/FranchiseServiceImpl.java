package com.varc.brewnetapp.domain.franchise.query.service;

import com.varc.brewnetapp.domain.franchise.query.dto.FranchiseDTO;
import com.varc.brewnetapp.domain.franchise.query.dto.FranchiseMemberDTO;
import com.varc.brewnetapp.domain.franchise.query.mapper.FranchiseMapper;
import com.varc.brewnetapp.exception.EmptyDataException;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service(value = "queryFranchiseService")
public class FranchiseServiceImpl implements FranchiseService {

    private final FranchiseMapper franchiseMapper;

    public FranchiseServiceImpl(FranchiseMapper franchiseMapper) {
        this.franchiseMapper = franchiseMapper;
    }

    @Override
    @Transactional
    public FranchiseDTO findFranchise(Integer franchiseCode) {

        FranchiseDTO franchiseDTO = franchiseMapper.selectFranchise(franchiseCode)
            .orElseThrow(() -> new EmptyDataException("가맹점 정보가 없습니다"));

        return franchiseDTO;
    }

    @Override
    @Transactional
    public Page<FranchiseDTO> findFranchiseList(Pageable page, String franchiseName,
        List<String> citys, String sort) {

        long pageSize = page.getPageSize();
        long pageNumber = page.getPageNumber();
        long offset = pageNumber * pageSize;

        List<FranchiseDTO> franchiseList = franchiseMapper.selectFranchiseList(offset, pageSize, franchiseName, citys, sort);

        if (franchiseList.isEmpty() || franchiseList.size() <= 0)
            throw new EmptyDataException("조회하려는 가맹점 정보가 없습니다");

        // 동적 쿼리 사용해서 필터링 및 검색어 있으면 전체 total element 값이 달라짐
        int count = franchiseMapper.selectFranchiseWhereFranchiseNameAndCitysCnt(franchiseName, citys);


        return new PageImpl<>(franchiseList, page, count);
    }

    @Override
    @Transactional
    public Page<FranchiseMemberDTO> findFranchiseMemberList(Pageable page, String franchiseName,
        List<String> citys, String sort) {
        long pageSize = page.getPageSize();
        long pageNumber = page.getPageNumber();
        long offset = pageNumber * pageSize;

        List<FranchiseMemberDTO> franchiseMemberList = franchiseMapper
            .selectFranchiseMemberList(offset, pageSize, franchiseName, citys, sort);

        if (franchiseMemberList.isEmpty() || franchiseMemberList.size() < 0)
            throw new EmptyDataException("조회하려는 가맹점 회원 정보가 없습니다");

        // 동적 쿼리 사용해서 필터링 및 검색어 있으면 전체 total element 값이 달라짐
        int count = franchiseMapper.selectFranchiseMemberWhereFranchiseNameAndCitysCnt(franchiseName, citys);


        return new PageImpl<>(franchiseMemberList, page, count);
    }

    @Override
    @Transactional
    public List<FranchiseDTO> findFranchiseListExcel(String franchiseName, List<String> citys,
        String sort) {
        List<FranchiseDTO> franchiseList = franchiseMapper.selectFranchiseListExcel(franchiseName, citys, sort);

        if (franchiseList.isEmpty() || franchiseList.size() <= 0)
            throw new EmptyDataException("조회하려는 가맹점 정보가 없습니다");

        return franchiseList;
    }
}
