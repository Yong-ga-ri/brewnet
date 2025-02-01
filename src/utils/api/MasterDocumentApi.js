import BaseApiService from './BaseApiService';

export default class MasterDocumentApi extends BaseApiService {
  constructor() {
    super('/v1/master/document');
  }

  //
  // POST
  //

  // 결재라인 설정
  setApprovalLine({ positionName, kind, seq }) {
    const requestBody = {
      positionName,
      kind,
      seq,
    };
    return this.post('/approver', requestBody);
  }
}
