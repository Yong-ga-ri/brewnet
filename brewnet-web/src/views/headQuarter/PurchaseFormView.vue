<template>
  <div class="purchase-form-container">
    <Button label="발주 등록" class="save-button" size="small" @click="clickSave" />

    <div class="top-area">
      <AppAutoComplete
        v-model="selectedSupplier"
        label="거래처 선택"
        :suggestions="supplierSuggestions"
        placeholder="거래처명으로 검색"
        full-width
        @complete-input="onCompleteInputSupplier"
      />
      <AppAutoComplete
        v-model="selectedStorage"
        label="입고창고 선택"
        :suggestions="storageSuggestions"
        placeholder="창고명 검색"
        full-width
        @complete-input="onCompleteInputStorage"
      />
      <AppInputText :model-value="dayjs().format('YYYY-MM-DD')" label="발주일자" disabled />
      <AppInputText :model-value="userStore.username" disabled label="담당자" />
    </div>

    <div class="approval-area">
      <h4 class="mb-4">결재 설정</h4>
      <div class="approval-line mb-3">
        <AppInputText model-value="발주 결재라인" label="결재자 선택" disabled />
        <AppAutoComplete
          v-model="selectedApprovalUser"
          :suggestions="approvalUserSuggestions"
          full-width
          placeholder="임직원명으로 검색"
          @complete-input="onCompleteInputApprovalUser"
        />
      </div>

      <AppInputText v-model="comment" label="첨언" placeholder="비고사항 등 입력" />
    </div>

    <template v-if="selectedSupplier">
      <div class="table-area">
        <CorrespondentItemTable
          v-if="selectedSupplier"
          :correspondent-code="selectedSupplier.code"
          :selected-items="selectedItems"
          @choose="chooseItem"
          @remove="removeItem"
        />
        <AppTableStyled>
          <thead>
            <tr>
              <th v-for="header in tableHeader" :key="header">{{ header }}</th>
            </tr>
          </thead>
          <tbody>
            <template v-if="selectedItems.length > 0">
              <tr v-for="item in selectedItems" :key="item.itemCode">
                <td class="align-center">
                  <Button icon="pi pi-trash" variant="text" rounded severity="secondary" @click="removeItem(item)" />
                </td>
                <td class="align-center">{{ item.itemUniqueCode }}</td>
                <td class="align-center">{{ item.itemName }}</td>
                <td>
                  <AppInputText v-model="item.quantity" number-mode text-align="right" />
                </td>
                <td class="align-right">{{ item.purchasePrice.toLocaleString() }}</td>
                <td class="align-right">{{ calculateSum(item.purchasePrice, item.quantity).toLocaleString() }}</td>
                <td class="align-right">
                  {{ calculateTax(calculateSum(item.purchasePrice, item.quantity)).toLocaleString() }}
                </td>
              </tr>
            </template>
            <template v-else>
              <tr>
                <td colspan="7" class="empty-td">발주 상품을 선택해주세요.</td>
              </tr>
            </template>
          </tbody>
          <tfoot>
            <tr>
              <td>총 합계</td>
              <td colspan="4" class="align-center">{{ total.toLocaleString() }}</td>
              <td class="align-right">{{ totalSupplyValue.toLocaleString() }}</td>
              <td class="align-right">{{ totalTaxValue.toLocaleString() }}</td>
            </tr>
          </tfoot>
        </AppTableStyled>
      </div>
    </template>
    <template v-else>
      <EmptyContent text="거래처를 선택해주세요." />
    </template>
  </div>
</template>

<script setup>
import dayjs from 'dayjs';
import { useToast } from 'primevue';
import { computed, ref, watch } from 'vue';
import { useRouter } from 'vue-router';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import EmptyContent from '@/components/common/EmptyContent.vue';
import AppAutoComplete from '@/components/common/form/AppAutoComplete.vue';
import AppInputNumber from '@/components/common/form/AppInputNumber.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import CorrespondentItemTable from '@/components/headQuarter/CorrespondentItemTable.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useUserStore } from '@/stores/user';
import HQCorrespondentApi from '@/utils/api/HQCorrespondentApi';
import HQDocumentApi from '@/utils/api/HQDocumentApi';
import HQPurchaseApi from '@/utils/api/HQPurchaseApi';
import HQStorageApi from '@/utils/api/HQStorageApi';
import { DRAFT_KIND } from '@/utils/constant';
import { makeAutocompleteSuggestion } from '@/utils/helper';

const userStore = useUserStore();
const toast = useToast();
const router = useRouter();
const { showConfirm } = useAppConfirmModal();

const selectedSupplier = ref(null);
const filteredSuppliers = ref([]);
const supplierSuggestions = computed(() => {
  return filteredSuppliers.value.map(e => makeAutocompleteSuggestion(e.correspondentCode, e.correspondentName));
});

const selectedStorage = ref(null);
const filteredStorages = ref([]);
const storageSuggestions = computed(() => {
  return filteredStorages.value.map(e => makeAutocompleteSuggestion(e.storageCode, e.storageName));
});

const selectedApprovalUser = ref(null);
const approverCandidates = ref([]);
const approvalUserSuggestions = computed(() => {
  return approverCandidates.value.map(e => makeAutocompleteSuggestion(e.approverCode, e.approverName));
});

const tableHeader = ref(['', '상품코드', '상품명', '수량', '단가', '공급가액', '부가세']);
const selectedItems = ref([]); // 선택된 상품(코드)들
const totalSupplyValue = ref(0);
const totalTaxValue = ref(0);
const total = computed(() => totalSupplyValue.value + totalTaxValue.value);
const comment = ref('');

const hqPurchaseApi = new HQPurchaseApi();
const hqStorageApi = new HQStorageApi();
const hqCorrespondentApi = new HQCorrespondentApi();
const hqDocumentApi = new HQDocumentApi();

const onCompleteInputSupplier = event => {
  // 거래처명으로 검색
  hqCorrespondentApi.getCorrespondents({ correspondentName: event.query }).then(data => {
    filteredSuppliers.value = data.data;
  });
};

const onCompleteInputStorage = event => {
  // 창고명으로 검색
  hqStorageApi.getStorages({ storageName: event.query }).then(data => {
    filteredStorages.value = data.data;
  });
};

const onCompleteInputApprovalUser = event => {
  // 발주 결재라인의 결재자 후보들 검색
  hqDocumentApi.getApproverCandidates(DRAFT_KIND.PURCHASE).then(data => {
    approverCandidates.value = data;
  });
};

const calculateSum = (price, quantity) => {
  if (!price || !quantity) return 0;
  return price * quantity;
};

const calculateTax = sum => {
  return sum * 0.1;
};

const chooseItem = data => {
  selectedItems.value = selectedItems.value.concat([{ ...data, quantity: null }]);
};

const removeItem = data => {
  selectedItems.value = selectedItems.value.filter(e => e.itemCode !== data.itemCode);
};

const checkForm = () => {
  try {
    if (!selectedSupplier.value) throw new Error('거래처를 선택해주세요.');
    if (!selectedStorage.value) throw new Error('입고창고를 선택해주세요.');
    if (selectedItems.value.length === 0) throw new Error('발주 상품을 입력해주세요.');
    if (totalSupplyValue.value === 0) throw new Error('수량을 입력해주세요.');
    if (!selectedApprovalUser.value) throw new Error('결재자를 선택해주세요');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const onPurchaseSave = () => {
  hqPurchaseApi
    .createPurchase({
      comment: comment.value,
      correspondentCode: selectedSupplier.value.code,
      storageCode: selectedStorage.value.code,
      approverCode: selectedApprovalUser.value.code,
      items: selectedItems.value.map(e => ({ itemCode: e.itemCode, quantity: e.quantity })),
    })
    .then(newPurchaseCode => {
      toast.add({
        severity: 'success',
        summary: '처리 성공',
        detail: '발주에 대한 결재요청이 등록되었습니다.',
        life: 3000,
      });

      // 상세 페이지로 이동
      router.replace({ name: 'hq:purchase:detail', params: { purchaseCode: newPurchaseCode } });
    });
};

const clickSave = () => {
  const isPass = checkForm();
  if (!isPass) return;
  showConfirm({
    header: '최종 확인',
    message: '입력된 내용으로 발주 등록이 진행됩니다.',
    acceptLabel: '발주 등록',
    onAccept: onPurchaseSave,
  });
};

// items가 변하면(items 안의 quantity가 변하면) 총합 계산
watch(
  selectedItems,
  newItems => {
    // 공급가액 총액 계산
    totalSupplyValue.value = newItems
      .map(e => calculateSum(e.purchasePrice, e.quantity))
      .reduce((prev, current) => prev + current, 0);

    // 부가세 총액 계산
    totalTaxValue.value = newItems
      .map(e => calculateTax(calculateSum(e.purchasePrice, e.quantity)))
      .reduce((prev, current) => prev + current, 0);
  },
  { deep: true }, // items 내용이 변하면 watch 되도록 설정
);
</script>

<style scoped>
.purchase-form-container {
  display: flex;
  flex-direction: column;
  gap: 10px;

  .top-area {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
  }

  .table-area {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    align-items: flex-start;

    .empty-td {
      text-align: center;
    }
  }

  .approval-area {
    background-color: var(--p-primary-50);
    padding: 20px;

    .approval-line {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
      align-items: flex-end;
    }
  }

  .save-button {
    align-self: flex-end;
  }
}
</style>
