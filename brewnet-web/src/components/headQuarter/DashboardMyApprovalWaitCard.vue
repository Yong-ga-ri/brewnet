<template>
  <Card class="my-approval-wait-card">
    <template #title>
      <div class="my-approval-wait-card-title">
        <h4>결재대기 목록 ({{ totalElements }}건)</h4>
        <Button label="전체보기" as="router-link" :to="{ name: 'hq:my:approval' }" size="small" severity="secondary" />
      </div>
    </template>
    <template #content>
      <DashboardTable>
        <thead>
          <tr>
            <th>기안서구분</th>
            <th>기안서코드</th>
            <th>제목</th>
            <th>기안일자</th>
            <th>기안자</th>
            <th>상태</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="paginatedDraftList.length === 0">
            <td colspan="7">{{ errMsg }}</td>
          </tr>
          <template v-else>
            <tr v-for="draft in paginatedDraftList" :key="`${draft.kind}${draft.code}`">
              <td>{{ formatKoDraftKind(draft.kind) }}</td>
              <td>{{ draft.code }}</td>
              <td class="ellipsis">{{ draft.title }}</td>
              <td>{{ dayjs(draft.date).format('YYYY-MM-DD') }}</td>
              <td>{{ draft.drafterName }}</td>
              <td>{{ formatKoApprovalStatus(draft.status) }}</td>
              <td>
                <Button
                  label="기안서보기"
                  size="small"
                  variant="text"
                  class="action-button"
                  @click="clickGoDetail(draft)"
                />
              </td>
            </tr>
          </template>
        </tbody>
      </DashboardTable>
    </template>
  </Card>
</template>

<script setup>
import dayjs from 'dayjs';
import { onMounted, ref } from 'vue';

import { useDraftNavigation } from '@/hooks/useDraftNavigation';
import HQStatisticsApi from '@/utils/api/HQStatisticsApi';
import { formatKoApprovalStatus, formatKoDraftKind } from '@/utils/format';

import DashboardTable from './DashboardTable.vue';

const { clickGoDetail } = useDraftNavigation();

const paginatedDraftList = ref([]);
const totalElements = ref(0);
const errMsg = ref('');

const hqStatisticsApi = new HQStatisticsApi();

onMounted(() => {
  hqStatisticsApi
    .getDraftListOnWaitingMyApproval()
    .then(data => {
      paginatedDraftList.value = data.content;
      totalElements.value = data.totalElements;
    })
    .catch(e => {
      errMsg.value = e.message;
    });
});
</script>

<style scoped>
.my-approval-wait-card {
  box-shadow: var(--p-primary-100) 0px 2px 8px 0px;

  .my-approval-wait-card-title {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
