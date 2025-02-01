<template>
  <div>
    <div class="add-button-wrapper">
      <Button label="카테고리 추가" icon="pi pi-plus" size="small" @click="clickAddCategory" />
    </div>

    <ItemCategoryGroup
      v-for="categoryGroup in categoryGroupList"
      :key="categoryGroup.superCategoryCode"
      :category-group="categoryGroup"
      class="mb-8"
      @save-category="onSave"
      @remove-category="onRemove"
      @save-super-category="onSaveSuper"
      @remove-super-category="onRemoveSuper"
    />

    <DynamicDialog />
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { computed, defineAsyncComponent, onMounted, ref } from 'vue';

import ItemCategoryGroup from '@/components/headQuarter/ItemCategoryGroup.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import { useModal } from '@/hooks/useModal';
import CategoryApi from '@/utils/api/CategoryApi';
import { makeSelectOption } from '@/utils/helper';

const AddItemCategoryModal = defineAsyncComponent(
  () => import('@/components/headQuarter/AddItemCategoryModalBody.vue'),
);

const { showConfirm } = useAppConfirmModal();
const toast = useToast();
const { openModal } = useModal();

const categoryGroupList = ref([]);
const superCategoryList = ref([]);
const superCategoryOptions = computed(() => {
  return superCategoryList.value.map(e => makeSelectOption(e.name, e.superCategoryCode));
});

const categoryApi = new CategoryApi();

const getCategories = () => {
  categoryApi.getCategories().then(foundSubCategories => {
    // super 카테고리 하위로 재정렬
    const arrangedCategories = []; // [{ superCategoryCode: number, superCategoryName: string, subCategories: []}];

    // 1. super 카테고리에 sub 카테고리 맵핑
    superCategoryList.value.forEach(superCategory => {
      arrangedCategories.push({
        superCategoryCode: superCategory.superCategoryCode,
        superCategoryName: superCategory.name,
        subCategories: foundSubCategories.filter(
          subCategory => subCategory.superCategoryCode === superCategory.superCategoryCode,
        ),
      });
    });

    categoryGroupList.value = arrangedCategories;
  });
};

const getPageData = async () => {
  const foundSuperCategories = await categoryApi.getSuperCategories();
  superCategoryList.value = foundSuperCategories;

  getCategories();
};

const onSave = (targetCategoryCode, newName) => {
  categoryApi.editSubCategory(targetCategoryCode, newName).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '카테고리명이 수정되었습니다.', life: 3000 });
    getPageData();
  });
};

const onSaveSuper = (targetCategoryCode, newName) => {
  categoryApi.editSuperCategory(targetCategoryCode, newName).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '카테고리명이 수정되었습니다.', life: 3000 });
    getPageData();
  });
};

const removeCategory = targetCategoryCode => {
  categoryApi.removeSubCategory(targetCategoryCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '카테고리가 삭제되었습니다.', life: 3000 });
    getPageData();
  });
};
const onRemove = (targetCategoryCode, targetCategoryName) => {
  showConfirm({
    header: '상품 카테고리 삭제',
    message: `[${targetCategoryName}] 카테고리를 삭제하시겠습니까?`,
    danger: true,
    acceptLabel: '네, 삭제합니다.',
    onAccept: () => removeCategory(targetCategoryCode),
  });
};

const removeSuperCategory = targetCategoryCode => {
  categoryApi.removeSuperCategory(targetCategoryCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '카테고리가 삭제되었습니다.', life: 3000 });
    getPageData();
  });
};
const onRemoveSuper = (targetCategoryCode, targetCategoryName) => {
  showConfirm({
    header: '상품 카테고리 삭제',
    message: `[${targetCategoryName}] 카테고리를 삭제하시겠습니까?`,
    danger: true,
    acceptLabel: '네, 삭제합니다.',
    onAccept: () => removeSuperCategory(targetCategoryCode),
  });
};

const clickAddCategory = () => {
  openModal({
    component: AddItemCategoryModal,
    header: '상품 카테고리 생성',
    data: {
      superCategoryOptions,
    },
    onClose: opt => {
      const callbackParams = opt.data;
      if (!callbackParams) return;

      // 카테고리 생성
      const selectedSuperCategory = callbackParams.selectedSuperCategory;
      const addingCategoryName = callbackParams.addingCategoryName;
      categoryApi.createCategory(selectedSuperCategory, addingCategoryName).then(() => {
        toast.add({ severity: 'success', summary: '처리 성공', detail: '새 카테고리가 등록되었습니다.', life: 3000 });

        // reload
        getPageData();
      });
    },
  });
};

onMounted(() => {
  getPageData();
});
</script>

<style scoped>
.add-button-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 32px;
}
</style>
