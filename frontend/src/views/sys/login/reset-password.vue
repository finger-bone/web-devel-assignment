<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Form, FormItem, Input, InputPassword, Button } from 'ant-design-vue'
import type { FormProps } from 'ant-design-vue'
import axios from 'axios'
import { router } from '@/router'
import { useUserStore } from '@/store/modules/user'
import { notification } from 'ant-design-vue'
const token = useUserStore().token
if (!token || token.length === 0) {
  router.push('/login')
}
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`

function removeCredentials() {
  axios.defaults.headers.common['Authorization'] = ''
  useUserStore().logout()
  router.push('/login')
}

axios
  .get('/api/secure/user/ping')
  .then((res) => {
    if (res.data != 'pong') {
      removeCredentials()
    }
  })
  .catch((_) => {
    removeCredentials()
  })

const formRef = ref()
const loading = ref(false)

interface ResetPasswordParams {
  oldPassword: string
  password: string
  confirmPassword: string
}

const formData = reactive<ResetPasswordParams>({
  oldPassword: '',
  password: '',
  confirmPassword: '',
})

const handleReset: FormProps['onFinish'] = async (values: ResetPasswordParams) => {
  loading.value = true

  if (values.password !== values.confirmPassword) {
    notification.error({
      message: '错误',
      description: '新密码和确认密码不匹配',
    })
    loading.value = false
    return
  }

  try {
    const verifyResponse = await axios.get('/api/secure/user/verify', {
      params: {
        password: values.oldPassword,
      },
    })

    if (verifyResponse.data) {
      const resetResponse = await axios.put(
        '/api/secure/user/reset',
        {},
        {
          params: {
            password: values.oldPassword,
            newPassword: values.password,
          },
        },
      )

      if (resetResponse.status === 200) {
        // Handle successful password reset here
        notification.success({
          message: '成功',
          description: '密码重置成功',
        })
        axios.defaults.headers.common['Authorization'] = ''
        useUserStore().logout()
      } else {
        // Handle unsuccessful password reset here
        notification.error({
          message: '错误',
          description: '密码重置失败',
        })
      }
    } else {
      // Handle incorrect old password here
      notification.error({
        message: '错误',
        description: '旧密码不正确',
      })
    }
  } catch (error) {
    // Handle error here
    notification.error({
      message: '错误',
      description: '重置密码时出错',
    })
  }

  loading.value = false
}
</script>

<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-800">
    <div class="bg-white p-6 rounded shadow-md w-96">
      <!-- Change w-80 to w-96 or any other width as per your requirement -->
      <h2 class="text-2xl mb-4 text-center">重置密码</h2>
      <Form :model="formData" ref="formRef" @finish="handleReset">
        <FormItem name="oldPassword" class="mb-4">
          <label class="block text-gray-700">旧密码</label>
          <Input size="large" v-model:value="formData.oldPassword" class="w-full" />
        </FormItem>
        <FormItem name="password" class="mb-4">
          <label class="block text-gray-700">新密码</label>
          <InputPassword
            size="large"
            visibilityToggle
            v-model:value="formData.password"
            class="w-full"
          />
        </FormItem>
        <FormItem name="confirmPassword" class="mb-4">
          <label class="block text-gray-700">确认新密码：</label>
          <InputPassword
            size="large"
            visibilityToggle
            v-model:value="formData.confirmPassword"
            class="w-full"
          />
        </FormItem>

        <FormItem>
          <Button type="primary" html-type="submit" size="large" block :loading="loading">
            重置密码
          </Button>
        </FormItem>
      </Form>
    </div>
  </div>
</template>
