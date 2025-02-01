<template>
  <div v-if="stockDetail" class="return-stock-detail">
    <div class="top-area">
      <Button
        v-if="stockDetail.confirmed === OTHER_DEPT_CHECK_STATUS.UNCONFIRMED"
        label="반품 처리 완료로 변경"
        size="small"
        variant="outlined"
        @click="clickStockConfirmed"
      />
      <Tag
        v-else-if="stockDetail.confirmed === OTHER_DEPT_CHECK_STATUS.CONFIRMED"
        :value="formatKoOtherDeptCheckStatus(stockDetail.confirmed)"
        size="small"
        rounded
        :severity="getReturnStockCheckStatusSeverity(stockDetail.confirmed)"
      />
    </div>
    <div>
      <h3 class="mb-3">반품요청 상세</h3>
      <AppTableStyled full-width>
        <tr>
          <th>반품요청일자</th>
          <td>{{ stockDetail.returningCreatedAt }}</td>
          <th>반품요청지점</th>
          <td>{{ stockDetail.franchiseName }}</td>
          <th>반품담당자</th>
          <td>{{ stockDetail.returningManager }}</td>
        </tr>
        <tr>
          <th>반품사유</th>
          <td colspan="5" class="align-center">{{ formatKoReturnReason(stockDetail.reason) }}</td>
        </tr>
      </AppTableStyled>
    </div>

    <div>
      <h3 class="mb-3">반품처리 상세</h3>
      <AppTableStyled full-width>
        <tr>
          <th>처리완료일자</th>
          <td>{{ stockDetail.createdAt }}</td>
          <th>처리상태</th>
          <td>{{ formatKoReturnStockStatus(stockDetail.status) }}</td>
          <th>재고담당자</th>
          <td>{{ stockDetail.manager }}</td>
        </tr>
        <tr>
          <th>비고사항</th>
          <td colspan="5" class="align-center">{{ stockDetail.comment }}</td>
        </tr>
      </AppTableStyled>
    </div>

    <AppTableStyled>
      <thead>
        <tr>
          <th>상품코드</th>
          <th>상품명</th>
          <th>카테고리</th>
          <th>전체수량/재입고수량</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="returnItem in stockDetail.returningHistoryItemList" :key="returnItem.itemUniqueCode">
          <td class="align-center">{{ returnItem.itemUniqueCode }}</td>
          <td class="align-center">{{ returnItem.itemName }}</td>
          <td class="align-center">{{ returnItem.superCategory }} - {{ returnItem.subCategory }}</td>
          <td class="align-center">
            {{ returnItem.quantity.toLocaleString() }}/{{ returnItem.restockQuantity.toLocaleString() }}
          </td>
        </tr>
      </tbody>
    </AppTableStyled>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';
import { useRoute } from 'vue-router';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQReturnApi from '@/utils/api/HQReturnApi';
import { OTHER_DEPT_CHECK_STATUS } from '@/utils/constant';
import { formatKoOtherDeptCheckStatus, formatKoReturnReason, formatKoReturnStockStatus } from '@/utils/format';
import { getReturnStockCheckStatusSeverity } from '@/utils/helper';

const route = useRoute();
const { detailCode } = route.params;
const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const stockDetail = ref(null);

const hqReturnApi = new HQReturnApi();

const getPageData = () => {
  hqReturnApi.getReturnStockDetail(detailCode).then(data => {
    stockDetail.value = data;
  });
};

const confirmRefund = () => {
  hqReturnApi.confirmStock(detailCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '최종 반품처리 되었습니다.', life: 3000 });
    getPageData();
  });
};

const clickStockConfirmed = () => {
  showConfirm({
    header: '최종 반품 처리',
    message: '최종 반품 처리 완료로 변경하시겠습니까?',
    acceptLabel: '반품 처리 완료',
    onAccept: confirmRefund,
  });
};

onMounted(() => {
  getPageData();
});
</script>

<style scoped>
.return-stock-detail {
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding-top: 32px;

  .top-area {
    display: flex;
    justify-content: flex-end;
    align-items: center;
  }
}
</style>
