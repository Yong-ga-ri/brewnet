<template>
  <div>
    <AppTable
      :paginated-data="paginatedFranchiseMembers"
      :columns="columns"
      :total-elements="totalElements"
      :rows-per-page="pageSize"
      @change-page="onChangePage"
      @reload="reload"
    />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { ref, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';

import AppTable from '@/components/common/AppTable.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQFranchiseApi from '@/utils/api/HQFranchiseApi';
import MemberApi from '@/utils/api/MemberApi';

const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const page = ref(0);
const paginatedFranchiseMembers = ref([]);
const totalElements = ref(0);
const pageSize = ref(15);

const hqFranchiseApi = new HQFranchiseApi();
const memberApi = new MemberApi();

function onClickEdit(data) {
  router.push({ name: 'hq:partner:franchise-account:edit', params: { memberCode: data.memberCode } });
}

const getFranchiseMembers = () => {
  hqFranchiseApi.getFranchiseMembers({ page: page.value, pageSize: pageSize.value }).then(data => {
    totalElements.value = data.totalElements;
    paginatedFranchiseMembers.value = data.content;
  });
};

const reload = () => {
  getFranchiseMembers();
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
    header: '가맹점 계정 삭제',
    message: `[${data.franchiseName}] 계정을 삭제하시겠습니까?`,
    acceptLabel: '네, 삭제합니다.',
    danger: true,
    onAccept: () => onAcceptRemove(data.loginId),
  });
}

const columns = [
  { field: 'memberCode', header: '계정코드' },
  { field: 'loginId', header: '아이디' },
  { field: 'memberName', header: '점주명' },
  { field: 'email', header: '이메일' },
  { field: 'contact', header: '휴대폰번호' },
  { field: 'franchiseName', header: '지점명' },
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

onMounted(() => {
  getFranchiseMembers();
});

// 페이지 변경되면 API 호출
watch(page, () => {
  getFranchiseMembers();
});
</script>

<style scoped></style>
