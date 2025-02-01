<template>
  <table class="draft-approval-line">
    <tr>
      <th>기안자</th>
      <th v-for="approvalLine in approvalLines" :key="approvalLine.approverCode">결재자</th>
    </tr>
    <tr>
      <td>{{ draftManagerName }}</td>
      <td v-for="approvalLine in approvalLines" :key="approvalLine.approverName">
        {{ approvalLine.approved === APPROVER_APPROVED_STATUS.APPROVED ? approvalLine.approverName : '' }}
      </td>
    </tr>
  </table>
</template>

<script setup>
import { APPROVER_APPROVED_STATUS } from '@/utils/constant';

const { approvalLines, draftManagerName } = defineProps({
  draftManagerName: {
    type: [String, null],
    required: true,
  },
  approvalLines: {
    type: Array,
    required: true,
  },
  /**
   * approvalLines: [{
   *   approverCode: number,     // 결재자의 멤버코드
   *   positionName: string,     // 결재자의 직위 (enum)
   *   approverName: string,     // 결재자명
   *   approved: string,         // 결재자의 approved 상태
   *   comment: string,          // 결재자의 comment
   *   createdAt: string,        // 결재자의 결재일시
   * }]
   */
});
</script>

<style scoped>
.draft-approval-line {
  th {
    border: 1px solid black;
    padding: 3px 0;
    font-size: 13px;
    width: 70px;
  }
  td {
    border: 1px solid black;
    font-size: 13px;
    text-align: center;
  }

  & > tr:nth-child(2) {
    height: 50px;
  }
}
</style>
