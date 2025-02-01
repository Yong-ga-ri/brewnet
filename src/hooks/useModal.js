import { useDialog } from 'primevue/usedialog';

export function useModal() {
  const dialog = useDialog();

  const openModal = ({ component, header, data, onClose }) => {
    dialog.open(component, {
      props: {
        header,
        style: {
          minWidth: '50vw',
          maxWidth: '80vw',
        },
        breakpoints: {
          '960px': '75vw',
          '640px': '90vw',
        },
        modal: true,
      },
      data,
      onClose,
    });
  };

  return { openModal };
}
