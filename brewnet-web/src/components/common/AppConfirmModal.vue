<template>
  <ConfirmDialog group="modal-confirm" class="modal-confirm">
    <template #container="{ message, acceptCallback, rejectCallback }">
      <div class="container">
        <div class="question-area">
          <i class="pi pi-question"></i>
        </div>

        <div class="message-area">
          <h3>{{ message.header }}</h3>
          <p>{{ message.message }}</p>
        </div>

        <div class="btn-group">
          <Button label="취소" outlined size="small" severity="secondary" rounded @click="rejectCallback"></Button>
          <Button
            :label="message.acceptProps.label"
            size="small"
            :severity="message.acceptProps.severity"
            rounded
            @click="acceptCallback"
          ></Button>
        </div>
      </div>
    </template>
  </ConfirmDialog>
</template>

<script setup></script>

<style scoped>
.modal-confirm {
  .container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 60px 32px 20px 32px;

    .question-area {
      width: 80px;
      height: 80px;
      border: 1px solid var(--p-content-border-color);
      display: flex;
      align-items: center;
      justify-content: center;
      position: absolute;
      top: -40px;
      background-color: var(--p-surface-100);
      border-radius: 50%;

      i {
        font-size: 35px;
      }
    }

    .message-area {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;
      margin-bottom: 32px;

      p {
        white-space: pre-wrap;
        text-align: center;
      }
    }

    .btn-group {
      display: flex;
      justify-content: center;
      gap: 10px;
    }
  }
}
</style>
