import BaseApiService from './BaseApiService';

export default class HQMemberApi extends BaseApiService {
  constructor() {
    super('/v1/hq/member');
  }

  //
  // GET
  //

  // 멤버 정보 상세 조회 (임직원용)
  getMemberInfo(memberCode) {
    return this.get(`/detail/${memberCode}`);
  }
}
