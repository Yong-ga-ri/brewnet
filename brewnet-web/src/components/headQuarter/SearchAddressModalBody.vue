<template>
  <AppModalBody
    action-button-label="선택 완료"
    class="search-address-modal-body-container"
    @click-action-button="choose"
  >
    <AppRadioList v-model="selectedSearchType" name="address-search-type" :options="searchTypes" class="mb-3" />
    <form class="form-search-address" @submit.prevent="searchAddress">
      <AppInputText
        v-model="searchKeyword"
        :placeholder="selectedSearchType === 'byAddress' ? '예: 보라매로 87' : '예: OO카페 서울역점'"
        full-width
      />
      <Button type="submit" label="조회" icon="pi pi-search" size="small" :loading="isLoading" />
    </form>

    <div class="result-area">
      <div class="top">
        <p>검색결과</p>
        <p>총 {{ options.length }}건</p>
      </div>
      <Listbox v-if="options.length > 0" v-model="selectedAddress" :options="options">
        <template #option="slotProps">
          <div class="result-item">
            <h3>{{ slotProps.option.title }}</h3>
            <small>
              {{ slotProps.option.desc }}
            </small>
          </div>
        </template>
      </Listbox>
      <p v-else class="empty">검색 결과가 없습니다.</p>
    </div>
  </AppModalBody>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, inject, ref, watch } from 'vue';

import { makeRadioOption } from '@/utils/helper';

import AppModalBody from '../common/AppModalBody.vue';
import AppInputText from '../common/form/AppInputText.vue';
import AppRadioList from '../common/form/AppRadioList.vue';

const emit = defineEmits(['callbackEvent', 'cancel']);

const dialogRef = inject('dialogRef');
const toast = useToast();

const searchKeyword = ref('');
const selectedAddress = ref(null);
const searchResults = ref([]);
const options = computed(() => {
  // 일반 주소검색, 키워드로 주소검색 둘 다 호환
  return searchResults.value.map(e => ({
    title: e.place_name || e.address_name,
    x: e.x,
    y: e.y,
    desc: e.place_name
      ? e.address_name
      : `[도로명]${e.road_address?.address_name || ''}\n[지번] ${e.address?.address_name || ''}`,
    address: e.address_name,
  }));
});
const searchTypes = ref([
  makeRadioOption('지번/도로명으로 검색', 'byAddress', 'byAddress'),
  makeRadioOption('장소명으로 검색', 'byPlaceName', 'byPlaceName'),
]);
const selectedSearchType = ref('byAddress');
const isLoading = ref(false);

const choose = () => {
  if (!selectedAddress.value || !selectedAddress.value.address) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: '주소검색을 통해 주소를 선택해주세요.', life: 3000 });
    return;
  }

  dialogRef.value.close({
    selectedAddress: selectedAddress.value.address,
  });
};

const searchAddress = async () => {
  const endpoint =
    selectedSearchType.value === 'byAddress'
      ? `https://dapi.kakao.com/v2/local/search/address`
      : 'https://dapi.kakao.com/v2/local/search/keyword';

  isLoading.value = true;
  const response = await fetch(`${endpoint}?query=${searchKeyword.value}`, {
    headers: {
      Authorization: `KakaoAK ${import.meta.env.VITE_KAKAO_REST_API_KEY}`,
    },
  });

  if (!response.ok) {
    throw new Error(`API error: ${response.statusText}`);
  }

  isLoading.value = false;
  const { documents } = await response.json();
  searchResults.value = documents;
};

watch(selectedSearchType, () => {
  searchKeyword.value = '';
});
</script>

<style scoped>
.search-address-modal-body-container {
  .form-search-address {
    display: flex;
    align-items: center;
    margin-bottom: 32px;
    gap: 10px;
  }

  .result-area {
    .top {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }

    .empty {
      border: 1px solid var(--p-surface-300);
      border-radius: 5px;
      padding: 16px;
    }

    .result-item {
      display: flex;
      flex-direction: column;

      small {
        white-space: pre-wrap;
      }
    }
  }
}
</style>
