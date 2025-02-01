<template>
  <div class="correspondent-list-container">
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
      :paginated-data="paginatedCorrespondents"
      :rows-per-page="pageSize"
      :total-elements="totalElements"
      show-excel-export
      @reload="onReload"
      @change-page="onChangePage"
      @export-excel="onExportToExcel"
    />

    <DynamicDialog />
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { useToast } from 'primevue';
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import XLSX from 'xlsx';

import AppTable from '@/components/common/AppTable.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useModal } from '@/hooks/useModal';
import HQCorrespondentApi from '@/utils/api/HQCorrespondentApi';
import { CRITERIA_CORRESPONDENT_LIST, SEARCH_CRITERIA } from '@/utils/constant';
import ExcelManager from '@/utils/ExcelManager';
import { formatKoSearchCriteria } from '@/utils/format';
import { makeSelectOption } from '@/utils/helper';

const CorrespondentItemsModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/CorrespondentItemsModalBody.vue'),
);

const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const { openModal } = useModal();
const toast = useToast();

const page = ref(1);
const pageSize = ref(15);
const paginatedCorrespondents = ref([]);
const totalElements = ref(0);

const getInitialCriteria = () => ({
  criteria: SEARCH_CRITERIA.CORRESPONDENT_NAME,
  keyword: '',
});
const criteria = ref(getInitialCriteria());
const criteriaOptions = computed(() => {
  return CRITERIA_CORRESPONDENT_LIST.map(e => makeSelectOption(formatKoSearchCriteria(e), e));
});

const hqCorrespondentApi = new HQCorrespondentApi();

const clickEdit = data => {
  router.push({ name: 'hq:partner:correspondent:edit', params: { correspondentCode: data.correspondentCode } });
};

const viewItems = data => {
  openModal({
    component: CorrespondentItemsModalBody,
    header: `[${data.correspondentName}] 상품 조회`,
    data: {
      correspondentCode: data.correspondentCode,
    },
  });
};

const onRemove = targetCorrespondentCode => {
  hqCorrespondentApi.deleteCorrespondent(targetCorrespondentCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '거래처가 삭제되었습니다.', life: 3000 });

    // data reload
    getCorrespondents();
  });
};

const clickRemove = data => {
  showConfirm({
    header: '거래처 삭제',
    message: `[${data.correspondentName}] 거래처를 삭제하시겠습니까?`,
    acceptLabel: '네, 삭제합니다.',
    danger: true,
    onAccept: () => onRemove(data.correspondentCode),
  });
};

const columns = [
  {
    field: 'correspondentCode',
    header: '거래처코드',
  },
  {
    field: 'correspondentName',
    header: '거래처명',
  },
  {
    field: 'managerName',
    header: '담당자명',
  },
  {
    field: 'address',
    header: '주소',
  },
  {
    field: 'contact',
    header: '연락처',
  },
  {
    field: 'email',
    header: '이메일',
  },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '정보수정',
          clickHandler: clickEdit,
        },
        {
          getLabel: () => '상품조회',
          clickHandler: viewItems,
        },
        {
          getLabel: () => '삭제',
          clickHandler: clickRemove,
        },
      ],
    },
  },
];

const getCorrespondents = () => {
  hqCorrespondentApi
    .getCorrespondents({
      page: page.value,
      pageSize: pageSize.value,
      correspondentCode:
        criteria.value.criteria === SEARCH_CRITERIA.CORRESPONDENT_CODE ? criteria.value.keyword : undefined,
      correspondentName:
        criteria.value.criteria === SEARCH_CRITERIA.CORRESPONDENT_NAME ? criteria.value.keyword : undefined,
    })
    .then(data => {
      paginatedCorrespondents.value = data.data.map(e => ({
        correspondentCode: e.correspondentCode,
        correspondentName: e.correspondentName,
        managerName: e.managerName,
        address: `${e.address} ${e.detailAddress}`, // address는 하나로 표시하기 위해
        contact: e.contact,
        email: e.email,
      }));
      totalElements.value = data.totalCount;
    });
};

const onSearch = () => {
  getCorrespondents();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  page.value = 1;
  getCorrespondents();
};

const onReload = () => {
  getCorrespondents();
};

const onChangePage = event => {
  page.value = event.page + 1;
  getCorrespondents();
};

const onExportToExcel = () => {
  hqCorrespondentApi
    .getAllCorrespondentList({
      correspondentCode:
        criteria.value.criteria === SEARCH_CRITERIA.CORRESPONDENT_CODE ? criteria.value.keyword : undefined,
      correspondentName:
        criteria.value.criteria === SEARCH_CRITERIA.CORRESPONDENT_NAME ? criteria.value.keyword : undefined,
    })
    .then(rows => {
      const excelRows = rows.map(e => ({
        correspondentCode: e.correspondentCode,
        correspondentName: e.correspondentName,
        managerName: e.managerName,
        address: `${e.address} ${e.detailAddress}`, // address는 하나로 표시하기 위해
        contact: e.contact,
        email: e.email,
      }));

      const orderedFields = columns.filter(e => e.field).map(e => e.field);
      const headerNames = columns.filter(e => e.field).map(e => e.header);

      const excelManager = new ExcelManager(excelRows, orderedFields);
      excelManager.setHeaderNames(headerNames);
      excelManager.setSheetName('거래처목록');
      const maxAddressWidth = excelRows.reduce((acc, r) => Math.max(acc, r.address.length), 10); // 가장 긴 주소의 길이를 cell width로 설정
      excelManager.setCellWidths([{ field: 'address', width: maxAddressWidth }]);

      excelManager.export(`거래처목록${dayjs().format('YYMMDD')}`);
    });
};

onMounted(() => {
  getCorrespondents();
});
</script>

<style scoped>
.correspondent-list-container {
  .criteria.keyword {
    grid-column: 2 / 4;
  }
}
</style>
