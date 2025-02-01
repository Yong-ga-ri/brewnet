<template>
  <div class="storage-detail-container">
    <template v-if="storageDetail">
      <div class="top-buttons">
        <Button label="재고 확인" icon="pi pi-external-link" size="small" @click="goToStockList" />
        <Button size="small" variant="outlined" severity="secondary" label="수정" @click="clickEdit" />
        <Button size="small" variant="outlined" severity="danger" label="삭제" @click="clickRemove" />
      </div>

      <AppTableStyled full-width>
        <tr>
          <th>창고코드</th>
          <td>{{ storageDetail.storageCode }}</td>
          <th>창고명</th>
          <td>{{ storageDetail.storageName }}</td>
          <th>등록일시</th>
          <td>{{ storageDetail.createdAt }}</td>
        </tr>
        <tr>
          <th>가동상태</th>
          <td><Tag rounded :value="storageDetail.active ? '정상' : '비가동'" /></td>
          <th>연락처</th>
          <td colspan="3">{{ storageDetail.contact }}</td>
        </tr>
        <tr>
          <th>주소</th>
          <td colspan="5">{{ storageDetail.address }}</td>
        </tr>
      </AppTableStyled>
    </template>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppTableStyled from '@/components/common/AppTableStyled.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQStorageApi from '@/utils/api/HQStorageApi';

const toast = useToast();
const route = useRoute();
const router = useRouter();
const { showConfirm } = useAppConfirmModal();

const { storageCode } = route.params;
const storageDetail = ref(null);

const hqStorageApi = new HQStorageApi();

const getStorage = () => {
  hqStorageApi.getStorage(storageCode).then(data => {
    storageDetail.value = data;
  });
};

const goToStockList = () => {
  router.push({
    name: 'hq:stock:storage-stock',
    params: { storageCode: storageCode },
  });
};

const clickEdit = () => {
  router.push({ name: 'hq:stock:storage:edit', params: { storageCode: route.params.storageCode } });
};

const onRemove = () => {
  hqStorageApi.deleteStorage(storageCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '창고가 삭제되었습니다.', life: 3000 });

    // data reload
    router.replace({ name: 'hq:stock:storage:list' });
  });
};

const clickRemove = () => {
  showConfirm({
    header: '창고 삭제',
    message: '창고를 삭제하시겠습니까?',
    acceptLabel: '네, 삭제합니다.',
    danger: true,
    onAccept: onRemove,
  });
};

onMounted(() => {
  getStorage();
});
</script>

<style scoped>
.storage-detail-container {
  .top-buttons {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin-bottom: 20px;
    gap: 10px;
  }
}
</style>
