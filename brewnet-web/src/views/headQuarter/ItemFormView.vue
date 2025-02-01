<template>
  <div class="item-form-container">
    <div class="category-area">
      <AppSelect
        v-model="selectedSubCategory"
        label="카테고리"
        label-position="left"
        :options="categoryOptions"
        :initial-value="selectedSubCategory"
        grouped
      />
    </div>

    <AppInputText v-model="itemName" label="상품명" label-position="left" />
    <AppInputText v-model="itemUniqueCode" label="상품코드" label-position="left" />
    <AppInputNumber
      v-model="sellingPrice"
      label="판매단가"
      :default-value="sellingPrice"
      label-position="left"
      krw-currency
      text-align="right"
    />
    <AppInputNumber
      v-model="purchasePrice"
      :default-value="purchasePrice"
      label="구매단가"
      label-position="left"
      krw-currency
      text-align="right"
    />
    <AppAutoComplete
      v-model="selectedCorrespondent"
      label="취급 거래처"
      :suggestions="correspondentSuggestions"
      label-position="left"
      @complete-input="searchCorrespondent"
    />
    <AppInputNumber v-model="safetyStock" :default-value="safetyStock" label="안전재고값" label-position="left" />
    <AppChooseFile label="상품이미지" label-position="left" @change-file="onChangeFile" />
    <RemovableImage v-if="previewUrl" :image-url="previewUrl" @remove-image="onRemove" />
    <RemovableImage v-else-if="originalImageUrl" :image-url="originalImageUrl" @remove-image="onRemoveOriginalImage" />

    <Button label="저장" size="small" @click="clickSave" />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, onMounted, ref, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppAutoComplete from '@/components/common/form/AppAutoComplete.vue';
import AppChooseFile from '@/components/common/form/AppChooseFile.vue';
import AppInputNumber from '@/components/common/form/AppInputNumber.vue';
import AppInputText from '@/components/common/form/AppInputText.vue';
import AppSelect from '@/components/common/form/AppSelect.vue';
import RemovableImage from '@/components/common/RemovableImage.vue';
import { useFile } from '@/hooks/useFile';
import CategoryApi from '@/utils/api/CategoryApi';
import HQCorrespondentApi from '@/utils/api/HQCorrespondentApi';
import HQItemApi from '@/utils/api/HQItemApi';
import ItemApi from '@/utils/api/ItemApi';
import { makeAutocompleteSuggestion, makeGroupSelectOption, makeSelectOption } from '@/utils/helper';

const route = useRoute();
const { itemCode } = route.params;
const { previewUrl, file: itemImageFile, onChangeFile, onRemove } = useFile();
const toast = useToast();
const router = useRouter();

const editMode = ref(false);
const categories = ref([]);
const categoryOptions = computed(() => {
  return categories.value.map(e =>
    makeGroupSelectOption(
      e.name,
      e.superCategoryCode,
      e.items.map(e2 => makeSelectOption(e2.subCategoryName, e2.subCategoryCode)),
    ),
  );
});
const filteredCorrespondents = ref([]);
const correspondentSuggestions = computed(() => {
  return filteredCorrespondents.value.map(e => makeAutocompleteSuggestion(e.correspondentCode, e.correspondentName));
});

const selectedSubCategory = ref('');
const itemName = ref('');
const itemUniqueCode = ref('');
const sellingPrice = ref(null);
const purchasePrice = ref(null);
const selectedCorrespondent = ref(null);
const safetyStock = ref(null);
const originalImageUrl = ref(''); // 기존 이미지

const hqCorrespondentApi = new HQCorrespondentApi();
const itemApi = new ItemApi();
const hqItemApi = new HQItemApi();
const categoryApi = new CategoryApi();

const searchCorrespondent = event => {
  // 가맹점 검색
  hqCorrespondentApi.getCorrespondents({ correspondentName: event.query || undefined }).then(data => {
    filteredCorrespondents.value = data.data;
  });
};

const checkForm = () => {
  try {
    if (!selectedSubCategory.value) throw new Error('하위 카테고리를 선택해주세요.');
    if (!itemName.value) throw new Error('상품명을 입력해주세요.');
    if (!itemUniqueCode.value) throw new Error('상품코드를 입력해주세요.');
    if (!sellingPrice.value) throw new Error('판매단가를 입력해주세요.');
    if (!purchasePrice.value) throw new Error('구매단가를 입력해주세요.');
    if (!selectedCorrespondent.value) throw new Error('취급거래처를 입력해주세요.');
    if (!safetyStock.value) throw new Error('안전재고값을 입력해주세요.');

    return true;
  } catch (e) {
    toast.add({ severity: 'error', summary: '입력 확인', detail: e.message, life: 3000 });
    return false;
  }
};

const clickSave = async () => {
  const isPass = checkForm();
  if (!isPass) return;

  let successMsg = '';
  if (editMode.value) {
    // 수정
    await hqItemApi.editItem({
      itemCode,
      categoryCode: selectedSubCategory.value,
      name: itemName.value,
      purchasePrice: purchasePrice.value,
      sellingPrice: sellingPrice.value,
      imageUrl: originalImageUrl.value,
      safetyStock: safetyStock.value,
      itemUniqueCode: itemUniqueCode.value,
      correspondentCode: selectedCorrespondent.value.code,
      active: true,
      imageFile: itemImageFile.value,
    });
    successMsg = '상품 정보가 수정되었습니다.';
  } else {
    // 생성
    await hqItemApi.createItem({
      categoryCode: selectedSubCategory.value,
      name: itemName.value,
      purchasePrice: purchasePrice.value,
      sellingPrice: sellingPrice.value,
      safetyStock: safetyStock.value,
      itemUniqueCode: itemUniqueCode.value,
      correspondentCode: selectedCorrespondent.value.code,
      imageFile: itemImageFile.value,
    });
    successMsg = '상품이 등록되었습니다.';
  }

  if (!successMsg) return;

  toast.add({ severity: 'success', summary: '처리 성공', detail: successMsg, life: 3000 });
  router.replace({ name: 'hq:stock:item:list' });
};

const onRemoveOriginalImage = () => {
  originalImageUrl.value = '';
};

watch(
  () => route.params.itemCode,
  newItemCode => {
    if (newItemCode) {
      // 수정모드
      editMode.value = true;

      itemApi.getItem(itemCode).then(data => {
        selectedSubCategory.value = data.categoryCode;
        itemName.value = data.name;
        itemUniqueCode.value = data.itemUniqueCode;
        sellingPrice.value = data.sellingPrice;
        purchasePrice.value = data.purchasePrice;
        selectedCorrespondent.value = makeSelectOption(data.correspondentName, data.correspondentCode);
        safetyStock.value = data.safetyStock;
        previewUrl.value = data.imageUrl;
      });
    } else {
      // 생성모드
      editMode.value = false;

      selectedSubCategory.value = null;
      itemName.value = '';
      itemUniqueCode.value = '';
      sellingPrice.value = null;
      purchasePrice.value = null;
      selectedCorrespondent.value = null;
      safetyStock.value = null;
      previewUrl.value = null;
    }
  },
  { immediate: true },
);

onMounted(async () => {
  // 1. 상위 카테고리 목록 불러오기
  const superCategories = await categoryApi.getSuperCategories();

  // 2. 하위 카테고리 목록 불러오기
  const subCategories = await categoryApi.getCategories();
  superCategories.forEach(superCategory => {
    superCategory.items = subCategories.filter(e => e.superCategoryCode === superCategory.superCategoryCode);
  });

  categories.value = superCategories;
});
</script>

<style scoped>
.item-form-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  width: 400px;

  .category-area {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .item-image {
    width: 250px;
    height: 250px;
    object-fit: cover;
  }
}
</style>
