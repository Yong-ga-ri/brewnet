import BaseApiService from './BaseApiService';

export default class HQDocumentApi extends BaseApiService {
  constructor() {
    super('/v1/hq/document');
  }

  //
  // GET
  //

  // 결재라인 조회
  getApprovalLines() {
    return this.get('/approval');
  }

  // 결재자 후보 목록 조회
  getApproverCandidates(draftKind) {
    return this.get(`/approvers?approvalLine=${draftKind}`);
  }
}
