package com.varc.brewnetapp.shared.request;

import com.varc.brewnetapp.shared.utility.search.SearchCriteria;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Pageable;

@Getter
@ToString
public class Retrieve {
    private final Pageable pageable;
    private final String filter;
    private final String sort;
    private final String startDate;
    private final String endDate;
    private final SearchCriteria criteria;
    private final String searchWord;

    public static Retrieve with(Pageable pageable, String filter, String sort,
                                String startDate, String endDate,
                                SearchCriteria criteria, String searchWord) {
        return new Retrieve(pageable, filter, sort,
                startDate, endDate,
                criteria, searchWord);
    }

    private Retrieve(Pageable pageable, String filter, String sort, String startDate, String endDate, SearchCriteria criteria, String searchWord) {
        this.pageable = pageable;
        this.filter = filter;
        this.sort = sort;
        this.startDate = startDate;
        this.endDate = endDate;
        this.criteria = (criteria == null) ? SearchCriteria.ALL : criteria;
        this.searchWord = searchWord;
    }
}
