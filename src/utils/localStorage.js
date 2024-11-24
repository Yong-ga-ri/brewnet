export default class LocalStorageUtil {
  #sendCompletePurchase = 'brewnet:purchase:send-completed'; // purchase code를 배열 형태로 저장
  #loginId = 'brewnet:auth:saved-id';
  #completeInStock = 'brewnet:purchase:complete-in-stock'; // 입고 확인된 발주 목록 (code값 저장)

  #getSendCompletePurchase() {
    const foundItems = localStorage.getItem(this.#sendCompletePurchase);
    if (foundItems) return JSON.parse(foundItems);
    return [];
  }

  #getCompleteInStock() {
    const foundItems = localStorage.getItem(this.#completeInStock);
    if (foundItems) return JSON.parse(foundItems);
    return [];
  }

  getLoginId() {
    return localStorage.getItem(this.#loginId);
  }
  #setLoginId(id) {
    localStorage.setItem(this.#loginId, id);
  }
  #removeLoginId() {
    localStorage.removeItem(this.#loginId);
  }

  //
  // 회계부서로 구매품의서 전송
  //

  // 회계부서로 발주내역 전송 완료했을 때
  saveSendCompletePurchase(purchaseCode) {
    let saveValue = [Number(purchaseCode)];
    const foundItems = this.#getSendCompletePurchase();

    if (foundItems.length > 0) {
      // 기존에 이미 저장된게 있다면 가져오기
      saveValue = saveValue.concat(foundItems);
    }

    localStorage.setItem(this.#sendCompletePurchase, JSON.stringify(saveValue));
  }

  // 회계부서로 전송된 발주 건인지?
  isSendCompletePurchase(purchaseCode) {
    const foundItems = this.#getSendCompletePurchase();
    return foundItems.includes(purchaseCode);
  }

  //
  // 입고 확인 완료된 발주 건
  //

  // 입고 처리 진행
  saveCompleteInStock(purchaseCode) {
    let saveValue = [Number(purchaseCode)];
    const foundItems = this.#getCompleteInStock();

    if (foundItems.length > 0) {
      // 기존에 이미 저장된게 있다면 가져오기
      saveValue = saveValue.concat(foundItems);
    }

    localStorage.setItem(this.#completeInStock, JSON.stringify(saveValue));
  }

  // 입고 이미 처리된 발주건인지?
  isCompleteInStock(purchaseCode) {
    const foundItems = this.#getCompleteInStock();
    return foundItems.includes(purchaseCode);
  }

  // 로그인 아이디 기억할건지?
  handleRememberLoginId(isRemember, loginId) {
    if (isRemember) {
      this.#setLoginId(loginId);
    } else {
      this.#removeLoginId();
    }
  }
}
