import dayjs from 'dayjs';

import BaseApiService from './BaseApiService';

export default class MemberApi extends BaseApiService {
  constructor() {
    super('/v1/member');
  }

  //
  // GET
  //

  // 본사 직원목록 조회
  getMembers({ page = 0, pageSize = 15, memberName = '' }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('pageSize', pageSize);
    if (memberName) {
      searchParams.append('search', memberName);
    }

    return this.get(`?${searchParams.toString()}`);
  }

  // 나의 정보 조회
  getMyInfo() {
    return this.get('/detail');
  }

  // 나의 결재목록 조회(내가 결재자인 경우)
  getMyApprovals({
    page = 0,
    pageSize = 15,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    approval,
    dateSort,
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).add(1, 'day').format('YYYY-MM-DD')); // +1 day

    if (approval) {
      searchParams.append('approval', approval);
    }

    if (dateSort) {
      searchParams.append('dateSort', dateSort);
    }

    return this.get(`/my-approval?${searchParams.toString()}`);
  }

  // 나의 기안서목록 조회
  getMyDrafts({
    page = 0,
    pageSize = 15,
    startDate = dayjs().subtract(1, 'year').format('YYYY-MM-DD'),
    endDate = dayjs().format('YYYY-MM-DD'),
    approval,
    dateSort,
  }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);
    searchParams.append('startDate', dayjs(startDate).format('YYYY-MM-DD'));
    searchParams.append('endDate', dayjs(endDate).add(1, 'day').format('YYYY-MM-DD')); // +1 day

    if (approval) {
      searchParams.append('approval', approval);
    }

    if (dateSort) {
      searchParams.append('dateSort', dateSort);
    }

    return this.get(`/my-draft?${searchParams.toString()}`);
  }

  //
  // POST
  //

  // 비밀번호로 본인확인
  checkAuthByPassword(password) {
    return this.post('/my-pw', { password });
  }

  //
  // PUT
  //

  // 내 비밀번호 변경 (로그인 한 유저)
  changePassword(password, checkNum) {
    return this.put('/my-pw', { password, checkNum });
  }

  // 내 서명 변경
  changeMySignature(imageFile, checkNum) {
    const formData = new FormData();
    formData.append('signatureImage', imageFile);
    formData.append('checkNumDTO', this.makeBlobJson({ checkNum }));

    return this.put('/my-signature', formData);
  }

  // 멤버 정보 수정 (마스터가 수정)
  changeMemberInfo({ memberCode, password, name, email, contact, positionName, franchiseCode }) {
    return this.put(`/${memberCode}`, { password, name, email, contact, positionName, franchiseCode });
  }

  //
  // DELETE
  //

  // 내 서명 삭제
  deleteMySignature(checkNum) {
    return this.delete('/my-signature', { checkNum });
  }

  // 계정 비활성화
  deactivateMember(targetMemberLoginId) {
    return this.delete('', { loginId: targetMemberLoginId });
  }
}
