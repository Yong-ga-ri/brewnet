import XLSX from 'xlsx';

export default class ExcelManager {
  #rows = [];
  #orderedFields = [];
  #sheetName = null;
  #headerNames = null;
  #cellWidths = null;

  constructor(rows, orderedFields) {
    if (!rows) throw new Error('엑셀 데이터가 필요합니다.');
    if (!orderedFields) throw new Error('엑셀 헤더명이 필요합니다.');
    if (rows.length === 0) throw new Error('출력할 데이터가 없습니다.');

    // orderedField에 있는 property만 가져오도록 (화면에 안 보이는 값은 엑셀 출력에 포함되지 않도록)
    this.#rows = rows.map(obj =>
      orderedFields.reduce((filteredObj, prop) => {
        if (obj.hasOwnProperty(prop)) {
          filteredObj[prop] = obj[prop];
        }
        return filteredObj;
      }, {}),
    );

    this.#orderedFields = orderedFields;
  }

  setSheetName(sheetName) {
    this.#sheetName = sheetName;
  }

  /**
   *
   * @param {*} headerNames // ['Header1', 'Header2', 'Name', 'Birthday', ...]
   */
  setHeaderNames(headerNames) {
    this.#headerNames = headerNames;
  }

  /**
   *
   * @param {*} widths // [{ field: (rows의 field명), width: number }, ...]
   */
  setCellWidths(widths) {
    this.#cellWidths = widths;
  }

  export(fileName = '') {
    const worksheet = XLSX.utils.json_to_sheet(this.#rows, { header: this.#orderedFields }); // 컬럼 순서 설정
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, this.#sheetName || undefined);

    // 헤더명 변경
    if (this.#headerNames) {
      XLSX.utils.sheet_add_aoa(worksheet, [this.#headerNames], { origin: 'A1' });
    }

    // cell width 설정
    if (this.#cellWidths) {
      worksheet['!cols'] = this.#orderedFields.map(orderedField => {
        const foundCellWidthInfo = this.#cellWidths.find(cellWidth => cellWidth.field === orderedField);
        if (foundCellWidthInfo) {
          return { wch: foundCellWidthInfo.width };
        }

        return { wch: 10 }; // 설정안된 나머지 cell width는 10
      });
    }

    XLSX.writeFile(workbook, `${fileName || 'download'}.xlsx`, { compression: true }); // export
  }
}
