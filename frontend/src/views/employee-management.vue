<template>
  <div>
    <a-button class="my-2 mr-2" type="primary" @click="isAddModalVisible = true">
      添加员工
    </a-button>
    <a-button
      class="my-2 mx-2"
      type="primary"
      :disabled="deleteCandidates.length === 0"
      @click="isDeleteMultipleModalVisible = true"
    >
      批量删除
    </a-button>
    <a-form @submit="handleSearch">
      <a-form-item label="姓名">
        <a-input v-model:value="searchForm.name" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="searchForm.userName" />
      </a-form-item>
      <a-form-item label="性别">
        <a-select v-model:value="searchForm.gender">
          <a-select-option value="男性">男性</a-select-option>
          <a-select-option value="女性">女性</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="入职日期范围">
        <a-date-picker
          class="mx-2"
          v-model:value="searchForm.hireDateStart"
          type="date"
          placeholder="开始时间"
        />
        <a-date-picker
          class="mx-2"
          v-model:value="searchForm.hireDateEnd"
          type="date"
          placeholder="结束时间"
        />
      </a-form-item>
      <a-form-item label="职位">
        <a-select v-model:value="searchForm.position">
          <a-select-option v-for="position in validPositions" :key="position" :value="position">
            {{ position }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">搜索</a-button>
      </a-form-item>
    </a-form>
    <a-table :dataSource="employees" :columns="columns" rowKey="id">
      <template #action="record">
        <a-button @click="handleEdit(record)" class="mx-2">编辑 </a-button>
        <a-button @click="handleRemove(record)" class="mx-2">删除</a-button>
      </template>
      <template #lastOperationTime="{ text }">
        {{ convertTimestamp(text) }}
      </template>
      <template #image="{ text }">
        <a-image :src="imgSrc(text)" :width="64" />
      </template>
      <template #checkbox="{ text }">
        <a-checkbox @change="(e: Event) => handleDeleteCandidates(e, text)" />
      </template>
    </a-table>
    <a-modal
      v-model:visible="isAddModalVisible"
      ok-text="添加"
      cancel-text="取消"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
    >
      <template #title>添加员工</template>
      <a-form-item label="姓名">
        <a-input v-model:value="addForm.name" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="addForm.userName" />
      </a-form-item>
      <a-form-item label="性别">
        <a-select v-model:value="addForm.gender">
          <a-select-option value="男性">男性</a-select-option>
          <a-select-option value="女性">女性</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="职位">
        <a-select v-model:value="addForm.position">
          <a-select-option v-for="position in validPositions" :key="position" :value="position">
            {{ position }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="入职日期">
        <a-date-picker v-model:value="addForm.hireDate" type="date" />
      </a-form-item>
      <a-form-item label="部门">
        <a-select v-model:value="addForm.departmentId">
          <a-select-option
            v-for="department in departments"
            :key="department.id"
            :value="department.id"
          >
            {{ department.departmentName }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="头像">
        <a-upload v-model:value="addForm.image" :before-upload="addBeforeUpload">
          <a-button :disabled="addForm.image != null"> <a-icon type="upload" /> 点击上传 </a-button>
        </a-upload>
      </a-form-item>
    </a-modal>
    <a-modal
      v-model:visible="isEditModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
    >
      <template #title>编辑员工</template>
      <a-form-item label="姓名">
        <a-input v-model:value="editForm.name" />
      </a-form-item>
      <a-form-item label="用户名">
        <a-input v-model:value="editForm.userName" />
      </a-form-item>
      <a-form-item label="性别">
        <a-select v-model:value="editForm.gender">
          <a-select-option value="男性">男性</a-select-option>
          <a-select-option value="女性">女性</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="职位">
        <a-select v-model:value="editForm.position">
          <a-select-option v-for="position in validPositions" :key="position" :value="position">
            {{ position }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="入职日期">
        <a-date-picker v-model:value="editForm.hireDate" type="date" />
      </a-form-item>
      <a-form-item label="部门">
        <a-select v-model:value="editForm.departmentId">
          <a-select-option
            v-for="department in departments"
            :key="department.id"
            :value="department.id"
          >
            {{ department.departmentName }}
          </a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="头像">
        <a-upload v-model:value="editForm.image" :before-upload="editBeforeUpload">
          <a-button> <a-icon type="upload" /> 点击上传 </a-button>
        </a-upload>
      </a-form-item>
    </a-modal>
    <a-modal
      v-model:visible="isDeleteModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleDeleteOk"
      @cancel="handleDeleteCancel"
    >
      <template #title>删除员工</template>
      <p>确认要删除员工吗？</p>
    </a-modal>
    <a-modal
      v-model:visible="isDeleteMultipleModalVisible"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleDeleteMultipleOk"
      @cancel="handleDeleteMultipleCancel"
    >
      <template #title>删除员工</template>
      <p>确认要删除员工吗？</p>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, ref } from 'vue'
import convertTimestamp from '@/utils/time'
import axios from 'axios'

const token = useUserStore().token
if (!token || token.length === 0) {
  router.push('/login')
}

const validPositions = ['班主任', '讲师', '学工主管', '教研主管']

const deleteCandidates = ref([] as Array<number>)

const isDeleteMultipleModalVisible = ref(false)

function handleDeleteMultipleCancel() {
  isDeleteMultipleModalVisible.value = false
}

function handleDeleteMultipleOk() {
  deleteMultiple()
  isDeleteMultipleModalVisible.value = false
}

async function deleteMultiple() {
  try {
    await Promise.all(
      deleteCandidates.value.map(async (id) => {
        await axios.delete(`/api/secure/employee/${id}`, {
          headers: {
            Authorization: token,
          },
        })
      }),
    )
    deleteCandidates.value = []
    handleSearch()
  } catch (error) {
    console.error('Failed to delete employees:', error)
  }
}

function handleDeleteCandidates(e: Event, id: number) {
  if ((e as any).target.checked) {
    deleteCandidates.value = [...deleteCandidates.value, id]
  } else {
    deleteCandidates.value = deleteCandidates.value.filter((value) => value !== id)
  }
}

const isDeleteModalVisible = ref(false)
const employeeToDelete = ref(0)
function handleDeleteOk() {
  deleteEmployee()
  isDeleteModalVisible.value = false
}
function handleDeleteCancel() {
  isDeleteModalVisible.value = false
}
function handleRemove(record: Ref<Employee>) {
  employeeToDelete.value = record.value.id
  isDeleteModalVisible.value = true
}
async function deleteEmployee() {
  try {
    await axios.delete(`/api/secure/employee/${employeeToDelete.value}`, {
      headers: {
        Authorization: token,
      },
    })
    handleSearch()
  } catch (error) {
    console.error('Failed to delete employee:', error)
  }
}

interface Employee {
  id: number
  name: string
  userName: string
  gender: string
  image: string
  position: string | null
  hireDate: Date | null
  lastOperationTime: string
  departmentId: number | null
}

// UI part
let departments = ref<
  Array<{
    id: number
    departmentName: string
  }>
>([])

function imgSrc(blob: string) {
  if (!blob) {
    return ''
  }
  return `data:image;base64,${blob}`
}

const searchForm = ref({
  name: '',
  userName: '',
  gender: '',
  hireDateStart: null as Date | null,
  hireDateEnd: null as Date | null,
  position: '',
})

const employees = ref<Employee[]>([])

const columns = [
  { title: '', dataIndex: 'id', key: 'checkbox', slots: { customRender: 'checkbox' } },
  { title: 'ID', dataIndex: 'id', key: 'id' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '用户名', dataIndex: 'userName', key: 'userName' },
  { title: '头像', dataIndex: 'image', key: 'image', slots: { customRender: 'image' } },
  { title: '性别', dataIndex: 'gender', key: 'gender' },
  { title: '职位', dataIndex: 'position', key: 'position' },
  { title: '入职时间', dataIndex: 'hireDate', key: 'hireDate' },
  {
    title: '最后操作时间',
    dataIndex: 'lastOperationTime',
    key: 'lastOperationTime',
    slots: { customRender: 'lastOperationTime' },
  },
  { title: '操作', key: 'action', slots: { customRender: 'action' } },
]

const isAddModalVisible = ref(false)

interface EmployeeForm {
  name: string
  userName: string
  gender: string
  position: string
  hireDate: Dayjs | null
  departmentId: number | null
  image: File | null
}

function getEmployeeDefaultForm(): EmployeeForm {
  return {
    name: '',
    gender: '',
    userName: '',
    position: '',
    hireDate: null as Dayjs | null,
    departmentId: null as number | null,
    image: null as File | null,
  }
}
const addForm = ref(getEmployeeDefaultForm())

import { notification } from 'ant-design-vue'
async function validateEmployeeForm(form: EmployeeForm, excluded: string = '') {
  // 验证表单
  if (
    !form.userName ||
    form.userName.length < 2 ||
    form.userName.length > 20 ||
    !/^[a-zA-Z0-9]+$/.test(form.userName)
  ) {
    notification.error({
      message: '无效的用户名',
      description:
        '用户名必须为2-20个字符，且只能包含数字和字母。当前用户名已存在，请输入新的用户名。',
    })
    return false
  }

  if (
    !(
      await axios.get(`/api/secure/employee/validate/${form.userName}`, {
        params: {
          excluded: excluded,
        },
        headers: {
          Authorization: token,
        },
      })
    ).data
  ) {
    notification.error({
      message: '无效的用户名',
      description: '当前用户名已存在，请输入新的用户名。',
    })
    return false
  }

  if (
    !form.name ||
    form.name.length < 2 ||
    form.name.length > 10 ||
    !/^[\u4e00-\u9fa5]+$/.test(form.name)
  ) {
    notification.error({
      message: '无效的员工姓名',
      description: '员工姓名必须为2-10个字符，且只能包含汉字。',
    })
    return false
  }

  if (!form.gender || (form.gender !== '男性' && form.gender !== '女性')) {
    notification.error({
      message: '无效的性别',
      description: '请选择性别。',
    })
    return false
  }

  if (form.image && form.image.size > 2 * 1024 * 1024) {
    notification.error({
      message: '无效的图像',
      description: '图像大小不能超过2MB。',
    })
    return false
  }

  return true
}

async function handleAddOk() {
  if (!(await validateEmployeeForm(addForm.value))) {
    return
  }
  const formData = new FormData()
  formData.append('name', addForm.value.name)
  formData.append('userName', addForm.value.userName)
  formData.append('gender', addForm.value.gender)
  // optional
  formData.append('position', addForm.value.position)
  if (addForm.value.hireDate) {
    let hireDate = addForm.value.hireDate.toDate().getTime()
    formData.append('hireDate', hireDate.toString())
  }
  if (addForm.value.departmentId) {
    formData.append('departmentId', addForm.value.departmentId.toString())
  } else {
    formData.append('departmentId', '0')
  }
  if (addForm.value.image) {
    formData.append('image', addForm.value.image)
  }
  try {
    await axios.post('/api/secure/employee', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: token,
      },
    })
    addForm.value = getEmployeeDefaultForm()
    handleSearch()
  } catch (error) {
    console.error('Failed to create employee:', error)
  }

  isAddModalVisible.value = false
}

function handleAddCancel() {
  isAddModalVisible.value = false
}

function addBeforeUpload(file: File) {
  addForm.value.image = file
  return false
}

function editBeforeUpload(file: File) {
  editForm.value.image = file
  return false
}

const editForm = ref(getEmployeeDefaultForm())
const isEditModalVisible = ref(false)
const editId = ref(0)

async function handleEditOk() {
  const previousName = employees.value.find((employee) => employee.id === editId.value)?.userName
  if (!(await validateEmployeeForm(editForm.value, previousName))) {
    return
  }
  const formData = new FormData()
  formData.append('name', editForm.value.name)
  formData.append('userName', editForm.value.userName)
  formData.append('gender', editForm.value.gender)
  // optional
  formData.append('position', editForm.value.position)
  if (editForm.value.hireDate) {
    let hireDate = editForm.value.hireDate.toDate().getTime()
    formData.append('hireDate', hireDate.toString())
  }
  if (editForm.value.departmentId) {
    formData.append('departmentId', editForm.value.departmentId.toString())
  } else {
    formData.append('departmentId', '0')
  }
  if (editForm.value.image) {
    formData.append('image', editForm.value.image)
  }
  try {
    await axios.put(`/api/secure/employee/${editId.value}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: token,
      },
    })
    editForm.value = getEmployeeDefaultForm()
    handleSearch()
  } catch (error) {
    console.error('Failed to update employee:', error)
  }

  isEditModalVisible.value = false
}

function handleEditCancel() {
  isEditModalVisible.value = false
}
// API part

async function handleSearch() {
  const params: any = {}
  if (searchForm.value.name.length > 0) {
    params.name = searchForm.value.name
  }
  if (searchForm.value.userName.length > 0) {
    params.userName = searchForm.value.userName
  }
  if (searchForm.value.gender.length > 0) {
    params.gender = searchForm.value.gender
  }
  if (searchForm.value.hireDateStart) {
    let hireDateStart = new Date(searchForm.value.hireDateStart).getTime()
    params.hireDateStart = hireDateStart
  }
  if (searchForm.value.hireDateEnd) {
    let hireDateEnd = new Date(searchForm.value.hireDateEnd).getTime()
    params.hireDateEnd = hireDateEnd
  }
  if (searchForm.value.position) {
    params.position = searchForm.value.position
  }

  try {
    console.error(params)
    const response = await axios.get('/api/secure/employee/search', {
      params: params,
      headers: {
        'Content-Type': 'multipart/form-data',
        Authorization: token,
      },
    })
    employees.value = response.data
  } catch (error) {
    console.error('Failed to search employees:', error)
  }
}

import dayjs, { Dayjs } from 'dayjs'

function handleEdit(record: Ref<Employee>) {
  const r = record.value
  // Handle the edit operation
  editId.value = r.id
  editForm.value = getEmployeeDefaultForm()
  editForm.value.name = r.name
  editForm.value.userName = r.userName
  editForm.value.position = r.position || ''
  editForm.value.hireDate = r.hireDate ? dayjs(r.hireDate, 'YYYY-MM-DD') : null
  editForm.value.gender = r.gender
  editForm.value.departmentId = r.departmentId || null
  isEditModalVisible.value = true
}

import { isDepartment, Department } from '@/interface/department'
import { useUserStore } from '@/store/modules/user'
import { router } from '@/router'
import { Ref } from 'vue'

async function fetchDepartments() {
  const response = await axios.get('/api/secure/department', {
    headers: {
      Authorization: token,
    },
  })
  if (Array.isArray(response.data)) {
    departments.value = response.data.filter(isDepartment).map((department: Department) => {
      return {
        id: department.id,
        departmentName: department.departmentName,
      }
    })
  }
}

onMounted(async () => {
  fetchDepartments()
  handleSearch()
})
</script>
