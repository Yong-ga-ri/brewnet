import { useLoadingStore } from '@/stores/loading';
import { useUserStore } from '@/stores/user';

import DOMEvent from '../domEvent';

export default class BaseApiService {
  #baseUrl = `${import.meta.env.VITE_SERVER_URL}/api`;
  #resource;
  #userStore;
  #loadingStore;

  constructor(resource) {
    if (!resource) throw new Error('Resource is not provided');
    this.#resource = resource;

    this.#userStore = useUserStore();
    this.#loadingStore = useLoadingStore();
  }

  async #callApi(endpoint, fetchOptions) {
    try {
      this.#loadingStore.setLoading(true);

      const requestUrl = `${this.#baseUrl}${this.#resource}${endpoint}`;
      const requestHeaders = new Headers();

      // 토큰이 있다면 헤더에 담아 요청
      const token = this.#userStore.accessToken;
      if (token) {
        requestHeaders.append('Authorization', `${token}`);
      }

      // form data가 아니면 Content-Type 추가
      // -> Error: Multipart: Boundary not found 에러 대응
      if (fetchOptions?.body && !(fetchOptions.body instanceof FormData)) {
        requestHeaders.append('Content-Type', 'application/json');
      }

      // 요청 시작
      const response = await fetch(requestUrl, {
        ...fetchOptions,
        headers: requestHeaders,
      });

      if (response.ok) {
        // Promise resolved and HTTP status is successful

        // 응답 헤더에 토큰이 담겨져 왔다면 저장
        const newAccessToken = response.headers.get('authorization');
        if (newAccessToken) {
          this.#userStore.saveTempAccessToken(newAccessToken);
        }
        const newRefreshToken = response.headers.get('refresh-token');
        if (newRefreshToken) {
          this.#userStore.saveTempRefreshToken(newRefreshToken);
        }
        const responseData = await response.json();

        // logging
        const parsedUrl = new URL(requestUrl);
        console.group(`Request: ${fetchOptions.method} ${parsedUrl.pathname}`);
        console.group('QueryParams');
        const printQuery = {};
        parsedUrl.searchParams.forEach((value, key) => {
          printQuery[key] = value;
        });
        console.table(printQuery);
        console.groupEnd();
        console.group('Response Data');
        console.table({ status: responseData.status, message: responseData.message });
        console.dir(responseData.result, { depth: null });
        console.groupEnd();
        console.groupEnd();

        return responseData.result;
      } else {
        // Promise resolved but HTTP status failed

        // 서버에서 보내주는 에러 메시지 뽑기
        const errorData = await response.json();
        const errMsg = errorData.message;

        throw new Error(errMsg);
      }
    } catch (error) {
      // Promise rejected

      // GET method에서는 에러메시지 표시 X
      if (fetchOptions?.method !== 'GET') {
        DOMEvent.dispatchApiError(error.message);
      }

      throw error; // throw 함으로써 사용부의 catch에 걸림
    } finally {
      this.#loadingStore.setLoading(false);
    }
  }

  async get(endpoint) {
    const fetchOptions = {
      method: 'GET',
    };

    return this.#callApi(endpoint, fetchOptions);
  }

  async post(endpoint, data) {
    let requestBody = JSON.stringify(data);
    if (data instanceof FormData) {
      requestBody = data; // formData는 stringify 처리 X
    }

    const fetchOptions = {
      method: 'POST',
      body: requestBody,
    };

    return this.#callApi(endpoint, fetchOptions);
  }

  async put(endpoint, data) {
    let requestBody = JSON.stringify(data);
    if (data instanceof FormData) {
      requestBody = data; // formData는 stringify 처리 X
    }

    const fetchOptions = {
      method: 'PUT',
      body: requestBody,
    };

    return this.#callApi(endpoint, fetchOptions);
  }

  async patch(endpoint, data) {
    let requestBody = JSON.stringify(data);
    if (data instanceof FormData) {
      requestBody = data; // formData는 stringify 처리 X
    }

    const fetchOptions = {
      method: 'PATCH',
      body: requestBody,
    };

    return this.#callApi(endpoint, fetchOptions);
  }

  async delete(endpoint, data) {
    let requestBody = JSON.stringify(data);
    const fetchOptions = {
      method: 'DELETE',
      body: requestBody,
    };

    return this.#callApi(endpoint, fetchOptions);
  }

  makeBlobJson(dto) {
    return new Blob([JSON.stringify(dto)], { type: 'application/json' });
  }
}
