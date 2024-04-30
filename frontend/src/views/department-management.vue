<template>
  <div>
    <a-button class="my-2" type="primary" @click="isAddModalVisible = true"> 添加部门 </a-button>
    <a-table :pagination="false" :dataSource="departments" :columns="columns" rowKey="id">
      <template #action="record">
        <a-button type="link" @click="handleEdit(record)">编辑</a-button>
        <a-button type="link" @click="handleRemove(record)">删除</a-button>
      </template>
      <template #lastOperationTime="{ text }">
        {{ convertTimestamp(text) }}
      </template>
    </a-table>
    <a-modal
      v-model:visible="isEditModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
    >
      <template #title>编辑部门</template>
      <a-input v-model:value="editName" placeholder="请输入部门名称" />
    </a-modal>
    <a-modal
      v-model:visible="isAddModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
    >
      <template #title>添加部门</template>
      <a-input v-model:value="addName" placeholder="请输入部门名称" />
    </a-modal>
    <a-modal
      v-model:visible="isDeleteModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleDeleteOk"
      @cancel="handleDeleteCancel"
    >
      <template #title>删除部门</template>
      <p>确定要删除这个部门吗？</p>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { isDepartment, Department } from '@/interface/department'
import { router } from '@/router'
import { useUserStore } from '@/store/modules/user'

const token = useUserStore().token
if (!token || token.length === 0) {
  router.push('/login')
}

// UI Part
import convertTimestamp from '@/utils/time'
import { notification } from 'ant-design-vue';

const isEditModalVisible = ref(false)
const isAddModalVisible = ref(false)
const isDeleteModalVisible = ref(false)
const editName = ref('')
const addName = ref('')
let currentDepartment: Department | null = null
const columns = [
  { title: '部门ID', dataIndex: 'id', key: 'id' },
  { title: '部门名称', dataIndex: 'departmentName', key: 'departmentName' },
  {
    title: '最后操作时间',
    dataIndex: 'lastOperationTime',
    key: 'lastOperationTime',
    slots: { customRender: 'lastOperationTime' },
  },
  {
    title: '操作',
    key: 'action',
    slots: { customRender: 'action' },
  },
]

async function handleEditOk() {
  if (currentDepartment && editName.value) {
    if(!(await validateDepartmentName(editName.value, currentDepartment.departmentName))) {
      return;
    }
    await editDepartment(currentDepartment, editName.value)
    isEditModalVisible.value = false
  }
}

function handleEditCancel() {
  isEditModalVisible.value = false
}

async function handleAddOk() {
  if(!(await validateDepartmentName(addName.value))) {
    return;
  }
  if (addName.value) {
    await addDepartment(addName.value)
    isAddModalVisible.value = false
  }
}

function handleAddCancel() {
  isAddModalVisible.value = false
}

async function handleDeleteOk() {
  if (currentDepartment) {
    await deleteDepartment(currentDepartment)
    isDeleteModalVisible.value = false
  }
}

function handleDeleteCancel() {
  isDeleteModalVisible.value = false
}

function handleEdit(record: unknown) {
  record = (record as any).value
  if (!isDepartment(record)) {
    return
  }
  currentDepartment = record
  editName.value = record.departmentName
  isEditModalVisible.value = true
}

function handleRemove(record: unknown) {
  record = (record as any).value
  if (!isDepartment(record)) {
    return
  }
  currentDepartment = record
  isDeleteModalVisible.value = true
}

// API Part
import axios from 'axios'
import { onMounted, ref } from 'vue'

const departments = ref<Department[]>([])

async function fetchDepartments() {
  console.error(token)
  const response = await axios.get('/api/secure/department', {})
  if (Array.isArray(response.data)) {
    departments.value = response.data.filter(isDepartment)
  }
}

async function deleteDepartment(department: Department) {
  try {
    await axios.delete(`/api/secure/department/${department.id}`, {})
    await fetchDepartments()
  } catch (error) {
    console.error('Failed to delete department:', error)
  }
}

async function editDepartment(department: Department, newDepartmentName: string) {
  try {
    if (newDepartmentName) {
      await axios.put(
        `/api/secure/department/${department.id}`,
        {},
        {
          params: {
            departmentName: newDepartmentName,
          },
        },
      )
      await fetchDepartments()
    }
  } catch (error) {
    console.error('Failed to edit department:', error)
  }
}

async function validateDepartmentName(departmentName: string, excluded: string = "") {
  if(departmentName.length < 2 || departmentName.length > 10) {
    notification.error({
      message: '部门名称长度应在2-10之间',
    });
    return false;
  }
  if(
    !((await axios.get(
      `/api/secure/department/valid/${departmentName}`,
      {
        params: {
          excluded: excluded,
        }
      }
    )).data)
  ) {
    notification.error({
      message: '部门名称已存在',
    });
    return false;
  }

  return true;
}

async function addDepartment(departmentName: string) {
  if(!(await validateDepartmentName(departmentName))) {
    return;
  }
  try {
    if (departmentName) {
      await axios.post(
        `/api/secure/department`,
        {},
        {
          params: {
            departmentName: departmentName,
          },
        },
      )
      addName.value = ''
      await fetchDepartments()
    }
  } catch (error) {
    console.error('Failed to add department:', error)
  }
}

onMounted(async () => {
  fetchDepartments()
})
</script>
