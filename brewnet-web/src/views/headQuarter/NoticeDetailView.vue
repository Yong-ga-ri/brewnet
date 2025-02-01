<template>
  <div v-if="noticeDetail" class="notice-detail-container">
    <div class="top">
      <div class="left">
        <h2 class="title mb-4">{{ noticeDetail.title }}</h2>
        <small class="small">{{ noticeDetail.memberName }} / {{ noticeDetail.createdAt }}</small>
      </div>

      <SpeedDial
        :model="items"
        direction="left"
        :button-props="{ rounded: true }"
        :tooltip-options="{ position: 'top' }"
      >
        <template #button="{ toggleCallback }">
          <Button size="small" icon="pi pi-ellipsis-v" @click="toggleCallback" />
        </template>
      </SpeedDial>
    </div>

    <div class="bottom">
      <p class="content">{{ noticeDetail.content }}</p>
      <AppImageList :images="noticeDetail.imageList" vertical />
    </div>
  </div>
</template>

<script setup>
import { useToast } from 'primevue';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';

import AppImageList from '@/components/common/AppImageList.vue';
import { useAppConfirmModal } from '@/hooks/useAppConfirmModal';
import HQNoticeApi from '@/utils/api/HQNoticeApi';
import MasterNoticeApi from '@/utils/api/MasterNoticeApi';

const route = useRoute();
const { noticeCode } = route.params;
const router = useRouter();
const { showConfirm } = useAppConfirmModal();
const toast = useToast();

const noticeDetail = ref(null);

const hqNoticeApi = new HQNoticeApi();
const masterNoticeApi = new MasterNoticeApi();

const getNotice = () => {
  hqNoticeApi.getNotice(noticeCode).then(data => {
    noticeDetail.value = data;
  });
};

const onDelete = () => {
  masterNoticeApi.deleteNotice(noticeCode).then(() => {
    toast.add({ severity: 'success', summary: '처리 성공', detail: '공지사항 글이 삭제되었습니다.', life: 3000 });
    router.replace({ name: 'hq:board:notice:list' });
  });
};
const clickDelete = () => {
  showConfirm({
    header: '글 삭제',
    message: '공지사항 글을 삭제하시겠습니까?',
    acceptLabel: '글 삭제',
    danger: true,
    onAccept: onDelete,
  });
};

const clickEdit = () => {
  router.push({ name: 'hq:board:notice:edit', params: { noticeCode: route.params.noticeCode } });
};

const items = ref([
  {
    label: '수정',
    icon: 'pi pi-pen-to-square',
    command: clickEdit,
  },
  {
    label: '삭제',
    icon: 'pi pi-trash',
    command: clickDelete,
  },
]);

onMounted(() => {
  getNotice();
});
</script>

<style scoped>
.notice-detail-container {
  .top {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    background-color: var(--p-primary-50);

    .left {
      .small {
        color: var(--p-surface-600);
      }
    }
  }

  .top,
  .bottom {
    padding: 20px;
  }

  .top {
    margin-bottom: 16px;
  }

  .bottom {
    display: flex;
    align-items: flex-start;
    gap: 16px;
    border-radius: 5px;
    border: 1px solid var(--p-surface-300);

    .content {
      flex-grow: 1;
      white-space: pre-wrap;
    }
  }
}
</style>
