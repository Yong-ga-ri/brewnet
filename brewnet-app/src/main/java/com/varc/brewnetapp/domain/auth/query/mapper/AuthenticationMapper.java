package com.varc.brewnetapp.domain.auth.query.mapper;

import com.varc.brewnetapp.domain.auth.query.dto.MemberInfoDTO;
import com.varc.brewnetapp.domain.auth.query.vo.MemberVO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AuthenticationMapper {
    MemberVO selectMemberByIdWithAuthorities(String loginId);

    List<String> selectAuths();

    Integer selectFranchiseCodeByMemberCode(@Param("memberCode") int memberCode);

    MemberInfoDTO selectFranchiseMemberInfoBy(@Param("loginId") String loginId);
}
