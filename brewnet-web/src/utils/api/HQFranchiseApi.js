import BaseApiService from './BaseApiService';

export default class HQFranchiseApi extends BaseApiService {
  constructor() {
    super('/v1/hq/franchise');
  }

  //
  // GET
  //

  // 가맹점 계정 목록 조회
  getFranchiseMembers({ page = 0, pageSize = 15 }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('pageSize', pageSize);

    return this.get(`/member?${searchParams.toString()}`);
  }

  // 가맹점 목록 조회
  getFranchiseList({ page = 0, pageSize = 15, franchiseName = '', citys = [] }) {
    const searchParams = new URLSearchParams();
    searchParams.append('page', page);
    searchParams.append('size', pageSize);

    if (franchiseName) {
      searchParams.append('franchiseName', franchiseName);
    }

    if (citys.length > 0) {
      searchParams.append('citys', citys.join(','));
    }

    return this.get(`?${searchParams.toString()}`);
  }

  getAllFranchiseList({ franchiseName = '', citys = [] }) {
    const searchParams = new URLSearchParams();

    if (franchiseName) {
      searchParams.append('franchiseName', franchiseName);
    }

    if (citys.length > 0) {
      searchParams.append('citys', citys.join(','));
    }

    return this.get(`/excel?${searchParams.toString()}`);
  }

  // 가맹점 상세조회
  getFranchise(franchiseCode) {
    return this.get(`/detail/${franchiseCode}`);
  }
}
