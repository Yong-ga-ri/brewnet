<template>
  <div>
    <SearchArea grid @search="onSearch" @form-reset="onReset">
      <AppInputText v-model="criteria.franchiseNameKeyword" label="지점명" />
      <AppAutoComplete
        v-model="criteria.addressKeyword"
        label="시/도"
        :suggestions="addressSuggestions"
        full-width
        @complete-input="onChangeAddressKeyword"
      />
    </SearchArea>

    <AppTable
      :paginated-data="paginatedFranchises"
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      show-excel-export
      @change-page="onChangePage"
      @reload="reload"
      @export-excel="onExportExcel"
    />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppAutoComplete from '@/components/common/form/AppAutoComplete.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQFranchiseApi from '@/utils/api/HQFranchiseApi';
import ResponsibleFranchiseApi from '@/utils/api/ResponsibleFranchiseApi';
import { ROLE } from '@/utils/constant';
import ExcelManager from '@/utils/ExcelManager';
import { makeAutocompleteSuggestion } from '@/utils/helper';

const { showConfirm } = useAppConfirmModal();
const router = useRouter();
const toast = useToast();

const page = ref(0);
const paginatedFranchises = ref([]);
const pageSize = ref(15);
const totalElements = ref(0);
const getInitialCriteria = () => ({
  franchiseNameKeyword: '',
  addressKeyword: null,
});
const criteria = ref(getInitialCriteria());
const allAddress = [
  '서울',
  '부산',
  '인천',
  '대구',
  '대전',
  '광주',
  '울산',
  '세종특별자치시',
  '경기',
  '충북',
  '충남',
  '전남',
  '경북',
  '경남',
  '강원특별자치도',
  '전북특별자치도',
  '제주특별자치도',
].map(e => makeAutocompleteSuggestion(e, e));
const addressSuggestions = ref([]);

const hqFranchiseApi = new HQFranchiseApi();
const responsibleFranchiseApi = new ResponsibleFranchiseApi();

const clickEdit = data => {
  router.push({ name: 'hq:partner:franchise:edit', params: { franchiseCode: data.franchiseCode } });
};

const getFranchiseList = () => {
  hqFranchiseApi
    .getFranchiseList({
      page: page.value,
      pageSize: pageSize.value,
      franchiseName: criteria.value.franchiseNameKeyword,
      citys: criteria.value.addressKeyword ? [criteria.value.addressKeyword.label] : undefined,
    })
    .then(data => {
      paginatedFranchises.value = data.content.map(e => ({ ...e, address: `${e.address} ${e.detailAddress || ''}` })); // 주소+상세주소
      totalElements.value = data.totalElements;
    })
    .catch(e => {
      // 조건에 맞는 가맹점이 없는 경우
      paginatedFranchises.value = [];
      totalElements.value = 0;
    });
};

const onRemove = targetFranchiseCode => {
  responsibleFranchiseApi.deleteFranchise(targetFranchiseCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '가맹점이 삭제되었습니다.', life: 3000 });

    // data reload
    getFranchiseList();
  });
};

const clickRemove = data => {
  showConfirm({
    header: '가맹점 삭제',
    message: `[${data.franchiseName}] 가맹점을 삭제하시겠습니까?`,
    acceptLabel: '네, 삭제합니다.',
    danger: true,
    onAccept: () => onRemove(data.franchiseCode),
  });
};

const columns = [
  { field: 'franchiseCode', header: '가맹점코드' },
  { field: 'franchiseName', header: '지점명', sortable: true },
  { field: 'address', header: '주소' },
  { field: 'contact', header: '연락처' },
  { field: 'businessNumber', header: '사업자등록번호' },
  { field: 'name', header: '대표자명' },
  {
    field: '',
    header: '',
    template: {
      button: [
        { getLabel: () => '정보수정', clickHandler: clickEdit, buttonRole: ROLE.RESPONSIBLE_ADMIN },
        { getLabel: () => '삭제', clickHandler: clickRemove, buttonRole: ROLE.RESPONSIBLE_ADMIN },
      ],
    },
  },
];

const onChangePage = event => {
  page.value = event.page;
  getFranchiseList();
};

const reload = () => {
  getFranchiseList();
};

const onChangeAddressKeyword = event => {
  if (!event.query) {
    addressSuggestions.value = allAddress;
    return;
  }

  addressSuggestions.value = allAddress.filter(e => e.includes(event.query));
};

const onSearch = () => {
  getFranchiseList();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 0;
  getFranchiseList();
};

const onExportExcel = () => {
  hqFranchiseApi
    .getAllFranchiseList({
      franchiseName: criteria.value.franchiseNameKeyword,
      citys: criteria.value.addressKeyword ? [criteria.value.addressKeyword.label] : undefined,
    })
    .then(rows => {
      const orderedFields = columns.filter(e => e.field).map(e => e.field); // 엑셀 컬럼 순서
      const headerNames = columns.filter(e => e.field).map(e => e.header); // 헤더명

      const excelManager = new ExcelManager(rows, orderedFields);
      excelManager.setHeaderNames(headerNames);
      excelManager.export(`가맹점목록${dayjs().format('YYMMDD')}`);
    })
    .catch(e => {
      toast.add({ severity: 'error', summary: '처리 실패', detail: e.message, life: 3000 });
    });
};

onMounted(() => {
  getFranchiseList();
});
</script>

<style scoped></style>
