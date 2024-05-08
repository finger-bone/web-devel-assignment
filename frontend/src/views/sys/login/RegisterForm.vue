<script lang="ts" setup>
import { reactive, ref, unref, computed } from 'vue'
import LoginFormTitle from './LoginFormTitle.vue'
import { Form, Input, Button, notification } from 'ant-design-vue'
import type { FormProps } from 'ant-design-vue'
import { useLoginState, useFormRules, LoginStateEnum } from './useLogin.ts'
import { useI18n } from 'vue-i18n'
import type { RegisterParams } from '@/api/model/user.model.ts'
import axios from 'axios'

const FormItem = Form.Item
const InputPassword = Input.Password
const { t } = useI18n()
const { handleBackLogin, getLoginState } = useLoginState()

const formRef = ref()
const loading = ref(false)

const formData = reactive<RegisterParams>({
  identifier: '',
  password: '',
  confirmPassword: '',
})

const { getFormRules } = useFormRules(formData)

const getShow = computed(() => unref(getLoginState) === LoginStateEnum.REGISTER)

const handleRegister: FormProps['onFinish'] = async (values: RegisterParams) => {
  loading.value = true
  if (values.password !== values.confirmPassword) {
    notification.error({
      message: '错误',
      description: '密码和确认密码必须相同',
    })
    loading.value = false
    return
  }
  try {
    const res = await axios.post(
      '/api/user/register',
      {},
      {
        params: {
          username: values.identifier,
          password: values.password,
        },
      },
    )
    if (res.data) {
      notification.success({
        message: '成功',
        description: '用户已注册成功',
      })
      handleBackLogin()
    } else {
      notification.error({
        message: '错误',
        description: '用户名已存在',
      })
    }
  } catch (error) {
    notification.error({
      message: '错误',
      description: '未知错误',
    })
  }
}
</script>
<template>
  <div v-if="getShow">
    <LoginFormTitle class="enter-x" />
    <Form
      class="p-4 enter-x"
      :model="formData"
      :rules="getFormRules"
      ref="formRef"
      @finish="handleRegister"
    >
      <FormItem name="identifier" class="enter-x">
        <Input
          class="fix-auto-fill"
          size="large"
          v-model:value="formData.identifier"
          :placeholder="t('sys.login.userName')"
        />
      </FormItem>
      <FormItem name="password" class="enter-x">
        <InputPassword
          size="large"
          v-model:value="formData.password"
          :placeholder="t('sys.login.password')"
        />
      </FormItem>
      <FormItem name="confirmPassword" class="enter-x">
        <InputPassword
          size="large"
          visibilityToggle
          v-model:value="formData.confirmPassword"
          :placeholder="t('sys.login.confirmPassword')"
        />
      </FormItem>

      <Button
        type="primary"
        html-type="submit"
        class="enter-x"
        size="large"
        block
        :loading="loading"
      >
        {{ t('sys.login.registerButton') }}
      </Button>
      <Button size="large" block class="mt-4 enter-x" @click="handleBackLogin">
        {{ t('sys.login.backSignIn') }}
      </Button>
    </Form>
  </div>
</template>
