import { definePreset } from '@primevue/themes';
import Nora from '@primevue/themes/nora';

const colorSet = 'blue';

const AppPreset = definePreset(Nora, {
  semantic: {
    primary: {
      50: `{${colorSet}.50}`,
      100: `{${colorSet}.100}`,
      200: `{${colorSet}.200}`,
      300: `{${colorSet}.300}`,
      400: `{${colorSet}.400}`,
      500: `{${colorSet}.500}`,
      600: `{${colorSet}.600}`,
      700: `{${colorSet}.700}`,
      800: `{${colorSet}.800}`,
      900: `{${colorSet}.900}`,
      950: `{${colorSet}.950}`,
    },
    surface: {
      0: '#ffffff',
      50: '{zinc.50}',
      100: '{zinc.100}',
      200: '{zinc.200}',
      300: '{zinc.300}',
      400: '{zinc.400}',
      500: '{zinc.500}',
      600: '{zinc.600}',
      700: '{zinc.700}',
      800: '{zinc.800}',
      900: '{zinc.900}',
      950: '{zinc.950}',
    },
    colorScheme: {
      light: {},
      dark: {},
    },
  },
});

export default AppPreset;
