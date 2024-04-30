import axios from 'axios'
import type { LoginParams, LoginResultModel, GetUserInfoModel } from './model/user.model'
import { useUserStore } from '@/store/modules/user'

export function login(data: LoginParams) {
  return new Promise<LoginResultModel>((resolve) => {
    axios
      .get('/api/user/login', {
        params: {
          username: data.identifier,
          password: data.password,
        },
        timeout: 1000,
      })
      .then((res) => {
        axios.defaults.headers.common['Authorization'] = res.data
        resolve({
          token: res.data,
        })
      })
  })
  // return request<LoginResultModel>('/api/user/login', {
  //   method: 'POST',
  //   data,
  // })
}

export function getUserInfo() {
  return new Promise<GetUserInfoModel>((resolve) => {
    const token = useUserStore().token
    axios
      .get('/api/secure/employee/info', {
        headers: {
          Authorization: token,
        },
      })
      .then((res) => {
        resolve({
          userId: res.data.id ?? 0,
          username: '',
          avatar: res.data.avatar == null ? '' : `data:image;base64,${res.data.avatar}`,
          desc: '',
          realName: '',
          roles: ['admin'],
        })
      })
  })
}
// export function getUserInfo() {
//   return new Promise<GetUserInfoModel>((resolve) => {
//     setTimeout(() => {
//       resolve({
//         userId: 0,
//         username: 'test',
//         realName: '',
//         avatar: '',
//         desc: '',
//         roles: ['admin'],
//       })
//     }, 0)
//   })
// }
