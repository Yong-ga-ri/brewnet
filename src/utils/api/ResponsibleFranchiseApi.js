import BaseApiService from './BaseApiService';

export default class ResponsibleFranchiseApi extends BaseApiService {
  constructor() {
    super('/v1/responsible/franchise');
  }

  //
  // POST
  //
  createFranchise({ franchiseName, address, detailAddress, contact, businessNumber, name }) {
    return this.post('', {
      franchiseName,
      address,
      detailAddress,
      contact,
      businessNumber,
      name,
    });
  }

  //
  // PUT
  //
  editFranchise({ franchiseCode, franchiseName, address, detailAddress, contact, businessNumber, name }) {
    return this.put('', {
      franchiseCode,
      franchiseName,
      address,
      detailAddress,
      contact,
      businessNumber,
      name,
    });
  }

  //
  // DELETE
  //
  deleteFranchise(franchiseCode) {
    return this.delete('', {
      franchiseCode,
    });
  }
}
