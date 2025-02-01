<template>
  <div class="storage-list-container">
    <SearchArea grid @search="onSearch" @form-reset="onReset">
      <AppSelect
        v-model="criteria.criteria"
        :options="criteriaOptions"
        :initial-value="criteria.criteria"
        label="검색어"
        label-position="left"
        full-width
      />
      <AppInputText v-model="criteria.keyword" placeholder="검색어 입력" class="criteria keyword" />
    </SearchArea>

    <AppTable
      :columns="columns"
      :paginated-data="paginatedStorages"
      :rows-per-page="pageSize"
      :total-elements="totalElements"
      @reload="onReload"
      @change-page="onChangePage"
    />

    <DynamicDialog />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import HQStorageApi from '@/utils/api/HQStorageApi';
import { CRITERIA_STORAGE_LIST, SEARCH_CRITERIA } from '@/utils/constant';
import { formatKoSearchCriteria } from '@/utils/format';
import { makeSelectOption } from '@/utils/helper';

const router = useRouter();

const page = ref(1);
const pageSize = ref(15);
const paginatedStorages = ref([]);
const totalElements = ref(0);

const getInitialCriteria = () => ({
  criteria: SEARCH_CRITERIA.STORAGE_CODE,
  keyword: '',
});
const criteria = ref(getInitialCriteria());
const criteriaOptions = computed(() => {
  return CRITERIA_STORAGE_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const hqStorageApi = new HQStorageApi();

const viewDetail = data => {
  router.push({ name: 'hq:stock:storage:detail', params: { storageCode: data.storageCode } });
};

const columns = [
  {
    field: 'storageCode',
    header: '창고코드',
  },
  {
    field: 'storageName',
    header: '창고명',
  },
  {
    field: 'address',
    header: '주소',
  },
  {
    field: 'contact',
    header: '대표 연락처',
  },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '상세보기',
          clickHandler: viewDetail,
        },
      ],
    },
  },
];

const getStorages = () => {
  hqStorageApi
    .getStorages({
      page: page.value,
      pageSize: pageSize.value,
      storageCode: criteria.value.criteria === SEARCH_CRITERIA.STORAGE_CODE ? criteria.value.keyword : undefined,
      storageName: criteria.value.criteria === SEARCH_CRITERIA.STORAGE_NAME ? criteria.value.keyword : undefined,
    })
    .then(data => {
      paginatedStorages.value = data.data;
      totalElements.value = data.totalCount;
    });
};

const onSearch = () => {
  getStorages();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 1;
  getStorages();
};

const onReload = () => {
  getStorages();
};

const onChangePage = event => {
  page.value = event.page;
  getStorages();
};

onMounted(() => {
  getStorages();
});
</script>

<style scoped>
.storage-list-container {
  .criteria.keyword {
    grid-column: 2 / 4;
  }
}
</style>
