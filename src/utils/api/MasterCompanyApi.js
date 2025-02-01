import BaseApiService from './BaseApiService';

export default class MasterCompanyApi extends BaseApiService {
  constructor() {
    super('/v1/master/company');
  }

  //
  // POST
  //

  // 회사 정보 생성
  createCompanyInfo({
    name,
    businessNumber,
    corporateNumber,
    ceoName,
    address,
    typeOfBusiness,
    contact,
    dateOfEstablishment,
  }) {
    return this.post('', {
      name,
      businessNumber,
      corporateNumber,
      ceoName,
      address,
      typeOfBusiness,
      contact,
      dateOfEstablishment,
    });
  }

  //
  // PUT
  //

  // 회사 정보 수정
  editCompanyInfo({
    name,
    businessNumber,
    corporateNumber,
    ceoName,
    address,
    typeOfBusiness,
    contact,
    dateOfEstablishment,
  }) {
    return this.put('', {
      name,
      businessNumber,
      corporateNumber,
      ceoName,
      address,
      typeOfBusiness,
      contact,
      dateOfEstablishment,
    });
  }

  // 법인인감 수정
  editCompanySeal(file) {
    const formData = new FormData();
    formData.append('sealImage', file);

    return this.put('/seal', formData);
  }
}
