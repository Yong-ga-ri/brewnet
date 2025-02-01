import html2canvas from 'html2canvas';
import { jsPDF } from 'jspdf';

export default class PrintPDFManager {
  exportPDF(targetId, filename) {
    html2canvas(document.querySelector(targetId), { allowTaint: false, useCORS: true }).then(canvas => {
      // 캔버스를 이미지로 변환 (A4에 맞게)
      let imgData = canvas.toDataURL('image/jpeg');
      let imgWidth = 210; // 이미지 가로 길이(mm)
      let pageHeight = imgWidth * 1.414; // 출력 페이지 세로 길이
      let imgHeight = (canvas.height * imgWidth) / canvas.width;
      let heightLeft = imgHeight;
      let margin = 0; // 출력 페이지 여백설정
      let doc = new jsPDF('p', 'mm', 'a4');
      let position = 0;

      window.scrollTo(0, 0);

      //첫 페이지 출력
      doc.addImage(imgData, 'JPEG', margin, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      //한 페이지 이상일 경우 루프 돌면서 출력
      while (heightLeft >= 20) {
        position = heightLeft - imgHeight;
        position = position - 20;
        doc.addPage();
        doc.addImage(imgData, 'JPEG', margin, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      // PDF를 바로 다운로드
      doc.save(`${filename}.pdf` || 'sample.pdf');
    });
  }
}
