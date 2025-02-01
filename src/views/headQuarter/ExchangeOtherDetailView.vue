<template>
  <div v-if="exchangeOtherDetail" class="exchange-stock-detail">
    <div class="top-area">
      <Button
        v-if="exchangeOtherDetail.confirmed === OTHER_DEPT_CHECK_STATUS.UNCONFIRMED"
        label="교환 처리 완료로 변경"
        size="small"
        variant="outlined"
        @click="clickExchangeConfirmed"
      />
      <Tag
        v-else-if="exchangeOtherDetail.confirmed === OTHER_DEPT_CHECK_STATUS.CONFIRMED"
        :value="formatKoOtherDeptCheckStatus(exchangeOtherDetail.confirmed)"
        size="small"
        rounded
        :severity="getExchangeStatusSeverity(exchangeOtherDetail.confirmed)"
      />
    </div>
    <div>
      <h3 class="mb-3">교환요청 상세</h3>
      <AppTableStyled full-width>
        <tr>
          <th>교환요청일자</th>
          <td>{{ exchangeOtherDetail.exchangeCreatedAt }}</td>
          <th>교환요청지점</th>
          <td>{{ exchangeOtherDetail.franchiseName }}</td>
          <th>교환담당자</th>
          <td>{{ exchangeOtherDetail.exchangeManager }}</td>
        </tr>
        <tr>
          <th>교환사유</th>
          <td colspan="5" class="align-center">{{ formatKoExchangeReason(exchangeOtherDetail.reason) }}</td>
        </tr>
      </AppTableStyled>
    </div>

    <div>
      <h3 class="mb-3">교환처리 상세</h3>
      <AppTableStyled full-width>
        <tr>
          <th>처리완료일자</th>
          <td>{{ exchangeOtherDetail.createdAt }}</td>
          <th>처리상태</th>
          <td>{{ formatKoExchangeOtherStatus(exchangeOtherDetail.status) }}</td>
          <th>재고담당자</th>
          <td>{{ exchangeOtherDetail.manager }}</td>
        </tr>
        <tr>
          <th>비고사항</th>
          <td colspan="5" class="align-center">{{ exchangeOtherDetail.comment }}</td>
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
        <tr v-for="exchangeItem in exchangeOtherDetail.exchangeHistoryItemList" :key="exchangeItem.itemUniqueCode">
          <td class="align-center">{{ exchangeItem.itemUniqueCode }}</td>
          <td class="align-center">{{ exchangeItem.itemName }}</td>
          <td class="align-center">{{ exchangeItem.superCategory }} - {{ exchangeItem.subCategory }}</td>
          <td class="align-center">
            {{ exchangeItem.quantity.toLocaleString() }}/{{ exchangeItem.restockQuantity.toLocaleString() }}
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
import HQExchangeApi from '@/utils/api/HQExchangeApi';
import { OTHER_DEPT_CHECK_STATUS } from '@/utils/constant';
import { formatKoOtherDeptCheckStatus, formatKoExchangeReason, formatKoExchangeOtherStatus } from '@/utils/format';
import { getExchangeStatusSeverity } from '@/utils/helper';

const route = useRoute();
const { exchangeStockHistoryCode } = route.params;
const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const exchangeOtherDetail = ref(null);

const hqExchangeApi = new HQExchangeApi();

const getPageData = () => {
  console.log(exchangeStockHistoryCode);
  hqExchangeApi.getExchangeOtherDetail(exchangeStockHistoryCode).then(data => {
    exchangeOtherDetail.value = data;
  });
};

const confirmExchange = () => {
  hqExchangeApi.completeExchange(exchangeStockHistoryCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '최종 교환처리 되었습니다.', life: 3000 });
    getPageData();
  });
};

const clickExchangeConfirmed = () => {
  showConfirm({
    header: '최종 교환 처리',
    message: '최종 교환 처리 완료로 변경하시겠습니까?',
    acceptLabel: '교환 처리 완료',
    onAccept: confirmExchange,
  });
};

onMounted(() => {
  getPageData();
});
</script>

<style scoped>
.exchange-stock-detail {
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
