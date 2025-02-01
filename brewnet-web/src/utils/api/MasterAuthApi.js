import BaseApiService from './BaseApiService';

export default class MasterAuhApi extends BaseApiService {
  constructor() {
    super('/v1/master/auth');
  }

  //
  // POST
  //

  // 회원 권한 설정
  setMemberRole(loginId, authName) {
    return this.post('/member', {
      loginId,
      authName,
    });
  }
}
