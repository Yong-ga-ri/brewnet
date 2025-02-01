import { createPinia } from 'pinia';
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate';
import PrimeVue from 'primevue/config';
import ConfirmationService from 'primevue/confirmationservice';
import ToastService from 'primevue/toastservice';
import Tooltip from 'primevue/tooltip';
import DialogService from 'primevue/dialogservice';
import { createApp } from 'vue';

import App from './App.vue';

import AppPreset from '@/assets/AppPreset';
import router from '@/router/router';

import './assets/main.css';
import 'primeicons/primeicons.css';
import koreanLocale from './assets/KoreanLocale';

const app = createApp(App);

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

app.use(pinia);
app.use(router);

// Prime Vue 설정
app.use(PrimeVue, {
  // Default theme configuration
  theme: {
    preset: AppPreset,
  },
  locale: koreanLocale
});
app.use(ConfirmationService);
app.directive('tooltip', Tooltip);
app.use(ToastService);
app.use(DialogService);

app.mount('#app');
