<template>
  <div class="return-form-container">
    <AppAutoComplete
      v-model="selectedOrder"
      :suggestions="orderSuggestions"
      placeholder="주문코드로 검색"
      label="반품 신청할 주문을 선택해주세요."
      @complete-input="onCompleteInputOrderCode"
    />

    <AppTableStyled>
      <thead>
        <tr>
          <th>
            <Checkbox v-model="allCheck" binary size="small" />
          </th>
          <th>상품코드</th>
          <th>상품명</th>
          <th>수량</th>
          <th>단가</th>
          <th>주문금액</th>
        </tr>
      </thead>
      <tbody>
        <template v-if="availableOrderItems.length > 0">
          <tr
            v-for="orderItem in availableOrderItems"
            :key="orderItem.itemCode"
            :class="{ highlight: checkedOrderItemCodeList.includes(orderItem.itemCode) }"
          >
            <td class="align-center">
              <Checkbox
                v-model="checkedOrderItemCodeList"
                name="availableOrderItems"
                :value="orderItem.itemCode"
                size="small"
              />
            </td>
            <td class="align-center">{{ orderItem.itemUniqueCode }}</td>
            <td class="align-center">{{ orderItem.itemName }}</td>
            <td class="align-center">{{ orderItem.quantity.toLocaleString() }}</td>
            <td class="align-right">{{ orderItem.sellingPrice.toLocaleString() }}</td>
            <td class="align-right">{{ orderItem.partSumPrice.toLocaleString() }}</td>
          </tr>
        </template>
        <tr v-else>
          <td colspan="6">반품가능한 상품이 없습니다.</td>
        </tr>
      </tbody>
      <tfoot>
        <tr>
          <th>금액합계</th>
          <td colspan="5" class="align-right">{{ totalPrice.toLocaleString() }}</td>
        </tr>
      </tfoot>
    </AppTableStyled>

    <div>
      <AppRadioList v-model="reason" name="return-reason" label="반품사유" :options="reasonOptions" class="mb-3" />
      <Textarea v-model="explanation" rows="10" cols="100" placeholder="반품 사유를 입력해주세요." size="small" />
    </div>

    <div>
      <AppLabel label="반품 상품 사진을 올려주세요." use-margin-bottom />
      <AppChooseFileList :upload-images="uploadFiles" @change-files="onChangeFiles" @remove-image="onRemove" />
    </div>

    <div class="action-buttons">
      <Button label="취소" variant="outlined" severity="secondary" size="small" @click="$router.back()" />
      <Button label="반품신청" size="small" @click="clickRequestReturn" />
    </div>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppLabel from '@/components/common/AppLabel.vue';
import AppTableStyled from '@/components/common/AppTableStyled.vue';
import AppAutoComplete from '@/components/common/form/AppAutoComplete.vue';
import AppChooseFileList from '@/components/common/form/AppChooseFileList.vue';
import AppRadioList from '@/components/common/form/AppRadioList.vue';
import { useFiles } from '@/hooks/useFiles';
import FCReturnApi from '@/utils/api/FCReturnApi';
import { RETURN_REASON_LIST } from '@/utils/constant';
import { formatKoReturnReason } from '@/utils/format';
import { makeAutocompleteSuggestion, makeRadioOption } from '@/utils/helper';

const { uploadFiles, onRemove, onChangeFiles } = useFiles();
const toast = useToast();
const router = useRouter();
const route = useRoute();

const selectedOrder = ref(null);
const availableOrderItems = ref([]);
const returnAvailableOrderList = ref([]);
const orderSuggestions = computed(() => {
  return returnAvailableOrderList.value.map(e => makeAutocompleteSuggestion(e, e));
});
const reason = ref('');
const reasonOptions = computed(() => {
  return RETURN_REASON_LIST.map(e => makeRadioOption(formatKoReturnReason(e), e, e));
});
const explanation = ref('');

const allCheck = ref(false);
const checkedOrderItemCodeList = ref([]); // 체크된 itemCode 목록이 들어감

const totalPrice = computed(() => {
  return checkedOrderItemCodeList.value.reduce((acc, currentItemCode) => {
    const matchOrder = availableOrderItems.value.find(orderItem => orderItem.itemCode === currentItemCode);
    if (matchOrder) return acc + matchOrder.partSumPrice;
    return acc;
  }, 0);
});

const fcReturnApi = new FCReturnApi();

const onCompleteInputOrderCode = event => {
  // 반품가능한 주문목록 검색
  fcReturnApi.getAvailableReturnList().then(data => {
    returnAvailableOrderList.value = data;
  });
};

const checkForm = () => {
  try {
    if (!selectedOrder.value || !selectedOrder.value.code) throw new Error('반품할 주문을 입력해주세요');
    if (checkedOrderItemCodeList.value.length === 0) throw new Error('반품신청할 상품을 선택해주세요');
    if (!reason.value) throw new Error('반품사유를 선택해주세요');
    if (!explanation.value) throw new Error('반품사유를 상세하게 작성해주세요.');
    if (uploadFiles.value.length === 0) throw new Error('상품 상태를 확인할 이미지를 첨부해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const clickRequestReturn = () => {
  const isPass = checkForm();
  if (!isPass) return;

  fcReturnApi
    .createReturn({
      orderCode: selectedOrder.value.code,
      returningItemCodeList: checkedOrderItemCodeList.value,
      reason: reason.value,
      explanation: explanation.value,
      sumPrice: totalPrice.value, // 체크한 항목만 합계
      imageFiles: uploadFiles.value.map(e => e.file),
    })
    .then(() => {
      toast.add({ severity: 'success', summary: '처리 성공', detail: '반품신청이 등록되었습니다.', life: 3000 });
      router.push({ name: 'fc:home:return:list' });
    });
};

watch(selectedOrder, newSelectedOrder => {
  if (newSelectedOrder && newSelectedOrder.code) {
    const newSelectedOrderCode = newSelectedOrder.code;

    // 반품 가능한 상품 조회
    fcReturnApi.getAvailableOrderItemsList(newSelectedOrderCode).then(data => {
      availableOrderItems.value = data;
    });
  }
});

watch(allCheck, newAllCheck => {
  if (newAllCheck) {
    // 전체 체크상태로 변경
    checkedOrderItemCodeList.value = availableOrderItems.value.map(e => e.itemCode);
  } else {
    // 전체 체크 해제
    checkedOrderItemCodeList.value = [];
  }
});

// URL에 orderCode가 있다면 해당 orderCode로 default 설정
watch(
  () => route.query,
  newQuery => {
    const targetOrderCode = newQuery.orderCode;

    if (targetOrderCode) {
      selectedOrder.value = makeAutocompleteSuggestion(targetOrderCode, targetOrderCode);
    }
  },
  { immediate: true },
);
</script>

<style scoped>
.return-form-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 24px;

  .action-buttons {
    display: flex;
    gap: 16px;
  }
}
</style>
