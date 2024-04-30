<script lang="ts" setup>
import MainSider from './MainSider.vue'
import MainHeader from './MainHeader.vue'
import { Layout, LayoutContent } from 'ant-design-vue'
import { router } from '@/router'
import { useUserStore } from '@/store/modules/user'
import axios from 'axios'
const token = useUserStore().token
if (!token || token.length === 0) {
  router.push('/login')
}
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
</script>
<template>
  <Layout class="main-layout-container">
    <MainSider />
    <Layout>
      <MainHeader />
      <LayoutContent class="main-layout-content">
        <router-view />
      </LayoutContent>
    </Layout>
  </Layout>
</template>
<style lang="less" scoped>
.main-layout-container {
  min-height: 100vh;

  .main-layout-content {
    margin: 24px;
    padding: 24px;
    background: #fff;
    min-height: 280px;
  }
}
</style>
