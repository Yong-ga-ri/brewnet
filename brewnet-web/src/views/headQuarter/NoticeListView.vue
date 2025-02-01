<template>
  <div>
    <SearchArea grid @search="onSearch" @form-reset="onReset">
      <AppInputText v-model="criteria.titleKeyword" label="제목" />
      <AppInputText v-model="criteria.authorKeyword" label="작성자" />
    </SearchArea>

    <AppTable
      :columns="columns"
      :paginated-data="paginatedNotices"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      @change-page="onChangePage"
      @reload="reload"
      @change-sort="onChangeSort"
    />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQNoticeApi from '@/utils/api/HQNoticeApi';
import MasterNoticeApi from '@/utils/api/MasterNoticeApi';
import { ROLE, SORTING_OPTION } from '@/utils/constant';

const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const page = ref(0);
const paginatedNotices = ref([]);
const pageSize = ref(15);
const getInitialCriteria = () => ({
  titleKeyword: '',
  authorKeyword: '',
});
const totalElements = ref(0);
const criteria = ref(getInitialCriteria());
const sorting = ref(SORTING_OPTION.DATE_DESC);

const hqNoticeApi = new HQNoticeApi();
const masterNoticeApi = new MasterNoticeApi();

const getNoticeList = () => {
  hqNoticeApi
    .getNoticeList({
      page: page.value,
      pageSize: pageSize.value,
      sort: sorting.value,
      title: criteria.value.titleKeyword,
      writerName: criteria.value.authorKeyword,
    })
    .then(data => {
      paginatedNotices.value = data.content;
      totalElements.value = data.totalElements;
    })
    .catch(e => {
      paginatedNotices.value = [];
      totalElements.value = 0;
    });
};

const clickDetail = data => {
  // 공지사항 상세보기
  router.push({ name: 'hq:board:notice:detail', params: { noticeCode: data.noticeCode } });
};

const clickEdit = data => {
  router.push({ name: 'hq:board:notice:edit', params: { noticeCode: data.noticeCode } });
};

const onDelete = targetNoticeCode => {
  masterNoticeApi.deleteNotice(targetNoticeCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '공지사항 글이 삭제되었습니다.', life: 3000 });

    // 데이터 새로고침
    getNoticeList();
  });
};
const clickDelete = data => {
  showConfirm({
    header: '글 삭제',
    message: '공지사항 글을 삭제하시겠습니까?',
    acceptLabel: '글 삭제',
    danger: true,
    onAccept: () => onDelete(data.noticeCode),
  });
};

const columns = [
  { field: 'noticeCode', header: '공지사항 코드' },
  { field: 'title', header: '제목' },
  { field: 'memberName', header: '작성자' },
  { field: 'createdAt', header: '작성일시', sortable: true },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '상세보기',
          clickHandler: clickDetail,
        },
        {
          getLabel: () => '수정',
          clickHandler: clickEdit,
          buttonRole: ROLE.MASTER,
        },
        {
          getLabel: () => '삭제',
          clickHandler: clickDelete,
          buttonRole: ROLE.MASTER,
        },
      ],
    },
  },
];

const onChangePage = event => {
  page.value = event.page;
  getNoticeList();
};

const reload = () => {
  getNoticeList();
};

const onSearch = () => {
  getNoticeList();
};

const onReset = () => {
  criteria.value = getInitialCriteria();
  sorting.value = SORTING_OPTION.DATE_DESC;
  page.value = 0;
  getNoticeList();
};

const onChangeSort = (sortField, sortOrder) => {
  if (!sortField || !sortOrder) return;

  if (sortField === 'createdAt') {
    if (sortOrder === 1) {
      sorting.value = SORTING_OPTION.DATE_ASC;
    } else if (sortOrder === -1) {
      sorting.value = SORTING_OPTION.DATE_DESC;
    }
  }

  getNoticeList();
};

onMounted(() => {
  getNoticeList();
});
</script>

<style scoped></style>
