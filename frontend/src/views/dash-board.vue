<template>
  <h1>
    欢迎来到后台管理系统!
  </h1>
</template>

<script lang="ts" setup>
import { router } from '@/router'
import { useUserStore } from '@/store/modules/user'

const token = useUserStore().token
if (!token || token.length === 0) {
  router.push('/login')
}

</script>
