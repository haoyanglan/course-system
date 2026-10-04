import { createApp } from 'vue'
import App from './App.vue'
import {
  ElAvatar, ElBadge, ElButton, ElCard, ElCarousel, ElCarouselItem, ElCol,
  ElDatePicker, ElDialog, ElDivider, ElDrawer, ElDropdown, ElDropdownItem,
  ElDropdownMenu, ElEmpty, ElForm, ElFormItem, ElImage, ElInput,
  ElInputNumber, ElLink, ElLoading, ElMenu, ElMenuItem, ElOption, ElPagination,
  ElRadio, ElRadioGroup, ElRow, ElSelect, ElTable, ElTableColumn, ElTag
} from 'element-plus'
import 'element-plus/dist/index.css'

const app = createApp(App)
app.use(ElLoading)
for (const component of [
  ElAvatar, ElBadge, ElButton, ElCard, ElCarousel, ElCarouselItem, ElCol,
  ElDatePicker, ElDialog, ElDivider, ElDrawer, ElDropdown, ElDropdownItem,
  ElDropdownMenu, ElEmpty, ElForm, ElFormItem, ElImage, ElInput,
  ElInputNumber, ElLink, ElMenu, ElMenuItem, ElOption, ElPagination,
  ElRadio, ElRadioGroup, ElRow, ElSelect, ElTable, ElTableColumn, ElTag
]) app.use(component)
app.mount('#app')
