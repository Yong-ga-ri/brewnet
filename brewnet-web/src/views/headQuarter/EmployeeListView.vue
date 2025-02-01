<template>
  <div>
    <!-- 검색 area -->
    <SearchArea grid @search="onSearch" @form-reset="reset">
      <AppInputText id="input_name_keyword" v-model="criteria.username" label="임직원명" />
    </SearchArea>

    <AppTable
      :paginated-data="paginatedEmployees"
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      @change-page="onChangePage"
      @reload="reload"
    />

    <DynamicDialog />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { ref, onMounted, watch, defineAsyncComponent } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import SearchArea from '@/components/common/SearchArea.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useModal } from '@/hooks/useModal';
import MemberApi from '@/utils/api/MemberApi';
import { ROLE } from '@/utils/constant';
import { formatKoEmployeePosition, formatKoMemberRole } from '@/utils/format';

const EditMemberRole = defineAsyncComponent(() => import('@/components/headQuarter/EditMemberRoleModalBody.vue'));

const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const { openModal } = useModal();
const toast = useToast();

const getInitialCriteria = () => ({ username: '' });
const criteria = ref(getInitialCriteria());
const paginatedEmployees = ref([]);
const totalElements = ref(0);
const page = ref(0);
const pageSize = ref(15);

const memberApi = new MemberApi();

const getEmployees = () => {
  memberApi
    .getMembers({ page: page.value, pageSize: pageSize.value, memberName: criteria.value.username })
    .then(data => {
      totalElements.value = data.totalElements;
      paginatedEmployees.value = data.content;
    });
};

function onClickEdit(data) {
  router.push({ name: 'hq:settings:employee:edit', params: { memberCode: data.memberCode } });
}

function onClickEditRole(data) {
  openModal({
    component: EditMemberRole,
    header: '권한 설정',
    data: { member: data },
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      if (callbackParams.reload) {
        getEmployees();
      }
    },
  });
}

const reload = () => {
  getEmployees();
};

function onAcceptRemove(targetMemberLoginId) {
  memberApi.deactivateMember(targetMemberLoginId).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '계정이 비활성화되었습니다.', life: 3000 });

    // 데이터 refresh
    reload();
  });
}

function onClickRemove(data) {
  showConfirm({
    header: '임직원 삭제',
    message: `[${data.name}] 임직원을 삭제하시겠습니까?`,
    acceptLabel: '네, 삭제합니다.',
    danger: true,
    onAccept: () => onAcceptRemove(data.id),
  });
}

const columns = [
  { field: 'memberCode', header: '임직원코드' },
  { field: 'name', header: '임직원명', sortable: true },
  { field: 'id', header: '아이디' },
  { field: 'email', header: '이메일' },
  { field: 'contact', header: '휴대폰번호' },
  { field: 'positionName', header: '직위', render: data => formatKoEmployeePosition(data.positionName) },
  {
    field: 'role',
    header: '권한',
    render: data => formatKoMemberRole(data.role),
  },
  {
    field: '',
    header: '',
    template: {
      button: [
        {
          getLabel: () => '정보수정',
          clickHandler: onClickEdit,
        },
        {
          getLabel: () => '권한설정',
          clickHandler: onClickEditRole,
          buttonRole: ROLE.MASTER,
        },
        {
          getLabel: () => '삭제',
          clickHandler: onClickRemove,
        },
      ],
    },
  },
];

const onChangePage = event => {
  page.value = event.page;
};

const onSearch = () => {
  getEmployees();
};

const reset = () => {
  criteria.value = getInitialCriteria();
  getEmployees();
};

onMounted(() => {
  getEmployees();
});

// 페이지 변경되면 API 호출
watch(page, () => {
  getEmployees();
});
</script>

<style scoped></style>
