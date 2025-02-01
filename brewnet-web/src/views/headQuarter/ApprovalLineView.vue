<template>
  <div class="approval-line-container">
    <AppTableStyled full-width>
      <tr v-for="approvalLine in approvalLines" :key="approvalLine.kind">
        <th>{{ formatKoDraftKind(approvalLine.kind) }} 결재라인</th>
        <td>
          <p>결재자(직위): {{ formatKoEmployeePosition(approvalLine.positionName) }}</p>
        </td>
        <td v-if="isHQAuthenticated(userStore.memberRole, ROLE.MASTER)">
          <Button label="수정" size="small" @click="clickEdit(approvalLine)" />
        </td>
      </tr>
    </AppTableStyled>

    <DynamicDialog />
  </div>
</template>

<script setup>
import { defineAsyncComponent, onMounted, ref } from 'vue';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import { useModal } from '@/hooks/useModal';
import { useUserStore } from '@/stores/user';
import HQDocumentApi from '@/utils/api/HQDocumentApi';
import { ROLE } from '@/utils/constant';
import { formatKoDraftKind, formatKoEmployeePosition } from '@/utils/format';
import { isHQAuthenticated } from '@/utils/helper';

const EditApprovalLineModalBody = defineAsyncComponent(
  () => import('@/components/headQuarter/EditApprovalLineModalBody.vue'),
);

const { openModal } = useModal();
const userStore = useUserStore();

const approvalLines = ref([]);

const hqDocumentApi = new HQDocumentApi();

const getApprovalLines = () => {
  hqDocumentApi.getApprovalLines().then(data => {
    approvalLines.value = data;
  });
};

const clickEdit = target => {
  openModal({
    component: EditApprovalLineModalBody,
    header: `${formatKoDraftKind(target.kind)} 결재라인 수정`,
    data: {
      targetApprovalLine: target,
    },
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      if (callbackParams.reload) {
        getApprovalLines();
      }
    },
  });
};

onMounted(() => {
  // 결재라인 가져오기
  getApprovalLines();
});
</script>

<style scoped>
.approval-line-container {
  table {
    margin-top: 20px;

    tr > td:nth-child(3) {
      text-align: center;
    }
  }
}
</style>
