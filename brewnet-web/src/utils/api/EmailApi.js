import BaseApiService from './BaseApiService';

export default class EmailApi extends BaseApiService {
  constructor() {
    super('/v1/email');
  }

  //
  // POST
  //

  // 이메일 인증 전송
  sendVerifyCodeByEmail(loginId, email) {
    return this.post('/send-email', {
      loginId,
      email,
    });
  }

  // 이메일 인증 검증
  checkVerifyCodeByEmail(email, code) {
    return this.post('/confirm-email', {
      email,
      code,
    });
  }
}
