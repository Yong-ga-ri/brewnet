package com.varc.brewnetapp.domain.exchange.query.service;

import com.varc.brewnetapp.common.domain.exchange.ExchangeStatus;
import com.varc.brewnetapp.domain.exchange.query.aggregate.vo.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ExchangeService {
    Page<ExchangeListVO> findExchangeList(Pageable page);

    List<ExchangeListVO> findExcelExchangeList(String searchFilter, String searchWord, String startDate, String endDate, boolean getConfirmed);

    Page<ExchangeListVO> findRequestedExchangeList(Pageable page);

    Page<ExchangeListVO> searchExchangeList(String searchFilter, String searchWord, String startDate, String endDate, boolean getConfirmed, Pageable page);

    ExchangeDetailVO findExchangeDetailBy(Integer exchangeCode);

    Page<ExchangeHistoryVO> findExchangeHistoryList(String searchFilter, String searchWord, String startDate, String endDate, Pageable page);

//    Page<ExchangeHistoryVO> searchExchangeHistoryList(String searchFilter, String searchWord, String startDate, String endDate, Pageable page);

    ExchangeHistoryDetailVO findExchangeHistoryDetailBy(Integer exchangeStockHistoryCode);

    Page<FranExchangeListVO> findFranExchangeList(String loginId, Pageable page);

    Page<FranExchangeListVO> searchFranExchangeList(String loginId, String searchFilter, String searchWord, String startDate, String endDate, Pageable page);

    FranExchangeDetailVO findFranExchangeDetailBy(String loginId, int exchangeCode);

    List<FranExchangeStatusVO> findFranExchangeStatusBy(String loginId, int exchangeCode);

//    Workbook exportExchangeExcel();

    ExchangeStatus findExchangeLatestStatus(int exchangeCode);

    List<ExchangeApproverVO> findExchangeApprover(int exchangeCode);

    boolean isValidExchangeByFranchise(String loginId, int exchangeCode);

    boolean isValidOrderByFranchise(String loginId, int orderCode);

    List<Integer> findFranAvailableExchangeBy(String loginId);

    List<FranExchangeItemVO> findFranAvailableExchangeItemBy(String loginId, int orderCode);
}
