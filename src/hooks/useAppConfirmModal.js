import { useConfirm } from 'primevue';

export function useAppConfirmModal() {
  const confirm = useConfirm();

  const showConfirm = ({ header = '', message = '', acceptLabel = '', danger = false, onAccept = () => {} }) => {
    confirm.require({
      group: 'modal-confirm',
      header,
      message,
      acceptProps: {
        label: acceptLabel,
        severity: danger ? 'danger' : undefined,
      },
      accept: onAccept,
    });
  };

  return {
    showConfirm,
  };
}
