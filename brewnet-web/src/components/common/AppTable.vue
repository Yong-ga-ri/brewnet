<template>
  <div>
    <DataTable
      ref="dt"
      :value="paginatedData"
      size="small"
      :pt="{
        header: {
          style: {
            'border-top': 'none',
          },
        },
        column: {
          headerCell: {
            style: {
              'background-color': '#FAFAFA',
            },
          },
        },
        tableContainer: {
          style: {
            'border-bottom': '1px solid var(--p-datatable-border-color)',
          },
        },
      }"
      removable-sort
      scrollable
      scroll-height="400px"
      class="app-table"
      @sort="onSort"
    >
      <template #header>
        <div class="table-header">
          <p class="total-elements">총 {{ totalElements || 0 }}건</p>

          <div class="right">
            <AppSelect
              v-if="sorting && sortingOptions.length > 0"
              v-model="sorting"
              :options="sortingOptions"
              :initial-value="sorting"
            />
            <Button
              icon="pi pi-refresh"
              variant="outlined"
              aria-label="새로고침"
              severity="secondary"
              size="small"
              @click="emit('reload')"
            />
            <Button
              v-if="showExcelExport"
              icon="pi pi-download"
              label="엑셀 다운로드"
              size="small"
              severity="success"
              @click="onClickExportToExcel"
            />
            <Button
              v-if="addButton"
              icon="pi pi-plus"
              :label="addButton.label"
              size="small"
              @click="addButton.clickHandler"
            />
          </div>
        </div>
      </template>
      <template #empty>데이터가 없습니다.</template>
      <Column
        v-for="(col, colIndex) of columns"
        :key="`field${colIndex}`"
        :field="col.field"
        :header="col.header"
        :sortable="col.sortable"
        :pt="{
          columnHeaderContent: {
            style: {
              'justify-content': 'center',
            },
          },
          bodyCell: {
            style: {
              'text-align': col.alignment || 'center',
            },
          },
          columnTitle: {
            style: {
              'font-size': '0.9rem',
            },
          },
        }"
        class="app-table-column"
      >
        <!-- 태그로 표시할 경우 -->
        <template v-if="col.template?.tag" #body="{ data }">
          <Tag
            :value="col.render ? col.render(data) : data[col.field]"
            :severity="col.template.tag.getSeverity(data)"
            rounded
          />
        </template>

        <!-- 버튼 리스트를 표시할 경우 -->
        <template v-else-if="col.template?.button" #body="{ data }">
          <div class="buttons">
            <Button
              v-for="button in col.template.button"
              :key="button.getLabel(data)"
              :label="button.getLabel(data)"
              size="small"
              :severity="button.getSeverity ? button.getSeverity(data) : undefined"
              :variant="button.getVariant ? button.getVariant(data) : 'text'"
              :disabled="button.getDisabled ? button.getDisabled(data) : undefined"
              :class="{
                hidden: button.getHidden
                  ? button.getHidden(data) // 1. getHidden 메소드 체크
                  : button.buttonRole
                    ? isButtonForbidden(button.buttonRole) // 2. buttonRole 체크
                    : false,
              }"
              :icon="button.getIcon ? button.getIcon(data) : undefined"
              @click="button.clickHandler ? button.clickHandler(data) : undefined"
            />
          </div>
        </template>

        <!-- 이미지로 표시할 경우 -->
        <template v-else-if="col.template?.image" #body="{ data }">
          <Image
            v-if="col.template.image.getSrc && col.template.image.getSrc(data)"
            :src="col.template.image.getSrc(data)"
            preview
            class="image"
          />
          <div v-else class="empty-image">없음</div>
        </template>

        <!-- 그 외 경우 -->
        <template v-else #body="{ data }">
          <div
            class="simple-text"
            :class="{ danger: col.getHighlightColor && col.getHighlightColor(data) === 'danger' }"
          >
            <!-- render 함수가 있다면 실행 -->
            {{ col.render ? col.render(data) : data[col.field] }}
          </div>
        </template>
      </Column>
    </DataTable>

    <!-- 페이지네이션 -->
    <Paginator
      :rows="rowsPerPage"
      :total-records="totalElements"
      :pt="{
        page: {
          style: {
            borderRadius: '50%',
            minWidth: 0,
            width: '2rem',
            height: '2rem',
          },
        },
      }"
      @page="emit('changePage', $event)"
    ></Paginator>
  </div>
</template>

<script setup>
import { ref } from 'vue';

import { useUserStore } from '@/stores/user';
import { ORDERED_HQ_ROLES, ROLE } from '@/utils/constant';

import AppSelect from './form/AppSelect.vue';

const { paginatedData, columns, rowsPerPage, totalElements, addButton, showExcelExport, sortingOptions } = defineProps({
  paginatedData: {
    type: Array,
    required: true,
  },
  totalElements: {
    type: Number,
    required: true,
  },
  columns: {
    type: Array,
    required: true,
  },
  rowsPerPage: {
    type: Number,
    required: true,
  },
  /**
   * columns: [{
   *   field: string               // 테이블 데이터의 필드명
   *   header: string              // 테이블 컬럼명
   *   sortable: boolean           // 정렬(ASC/DESC) 가능 여부
   *   render: (data: T) => string // render 될 내용 (서버에서 보낸 데이터와 표시할 데이터 형태가 다를 때 사용)
   *   template: {                 // 다른 뷰를 띄울 경우 셋팅
   *     tag: {                    // 태그 뷰를 사용할 경우 셋팅
   *       getSeverity: (data: T) => string // 태그 뷰 색상 설정(메소드의 return 값은 프라임뷰 tag의 severity 값이어야 함)
   *     },
   *     button: {[
   *       {
   *         getLabel: (data: T) => string     // 버튼 label
   *         clickHandler: (data: T) => void   // 버튼 클릭 시 동작할 handler 메소드
   *         getSeverity: (data: T) => string  // 프라임뷰 버튼 severity 값
   *         getVariant: (data: T) => string   // 프라임뷰 버튼 variant 값
   *         getDisabled: (data: T) => boolean // 버튼 disabled 여부
   *         getHidden: (data: T) => boolean   // 버튼 숨기는지?
   *         getIcon: (data: T) => string      // 프라임뷰 아이콘
   *         buttonRole: ROLE                  // 버튼을 누를 수 있는 최소 멤버 권한
   *       }
   *     ]},
   *     image: {
   *       getSrc: (data: T) => string         // 이미지 src
   *     }
   *   }
   *   alignment: string                       // 정렬 ('left', 'center', 'right')
   *   getHighlightColor: (data: T) => string  // 강조할 색상 (가능한 return 값: 'danger')
   * }]
   */

  // required false
  addButton: {
    type: Object,
    required: false,
    default: () => {
      return undefined;
    },
  },
  /**
   * addButton: {
   *   label: string             // 버튼 label
   *   clickHandler: () => void  // 버튼 클릭 핸들러 메소드
   * }
   */
  showExcelExport: {
    type: Boolean,
    required: false,
    default: false,
  },
  sortingOptions: {
    type: Array,
    required: false,
    default: () => [],
  },
});

const emit = defineEmits(['changePage', 'reload', 'exportExcel', 'changeSort']);
const sorting = defineModel('sorting', { type: String, required: false });

const userStore = useUserStore();

const dt = ref();
const exportCSV = () => {
  dt.value.exportCSV();
};

const onClickExportToExcel = () => {
  emit('exportExcel');
};

const onSort = event => {
  emit('changeSort', event.sortField, event.sortOrder);
};

const isButtonForbidden = buttonRole => {
  const currentMemberRole = userStore.memberRole;

  // 배송기사나 가맹점은 권한 계급 없음
  if (currentMemberRole === ROLE.DELIVERY || currentMemberRole === ROLE.FRANCHISE) return false;

  const memberRoleOrder = ORDERED_HQ_ROLES.indexOf(currentMemberRole);
  const buttonRoleOrder = ORDERED_HQ_ROLES.indexOf(buttonRole);

  if (memberRoleOrder === -1) return true; // 알수없는 권한..

  return memberRoleOrder < buttonRoleOrder; // 권한이 더 낮으면 Forbidden
};
</script>

<style scoped>
.app-table {
  .table-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;

    .right {
      display: flex;
      align-items: center;
      gap: 10px;
    }

    .total-elements {
      font-size: 0.85rem;
    }
  }

  .app-table-column {
    & .hidden {
      display: none;
    }

    & .danger {
      color: #f03e3e;
    }
  }

  .buttons {
    display: flex;
    gap: 10px;
    justify-content: center;
  }

  .image {
    width: 50px;
    height: 50px;
    object-fit: cover;
  }

  .empty-image {
    font-size: 14px;
    color: var(--p-surface-400);
  }

  .simple-text {
    font-size: 0.9rem;
  }

  .p-tag {
    font-size: 0.8rem;
  }
}
</style>
