<template>
  <div>
    <a-form @submit="() => {
      current = 1;
      handleSearch();
    }">
      <a-row :gutter="16">
        <a-col :span="6">
          <a-form-item label="班级名称">
            <a-input v-model:value="searchForm.name" placeholder="输入班级名称" />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="最早结束时间">
            <a-date-picker
              v-model:value="searchForm.endTimeStart"
              type="datetime"
              placeholder="结课时间范围"
            />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item label="结束时间">
            <a-date-picker
              v-model:value="searchForm.endTimeEnd"
              type="datetime"
              placeholder="结课时间范围"
            />
          </a-form-item>
        </a-col>
        <a-col :span="6">
          <a-form-item>
            <a-button type="primary" html-type="submit">搜索</a-button>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-button type="primary" class="my-2" @click="isAddModalVisible = true">添加班级</a-button>
    <a-table :pagination="false" :dataSource="classes" :columns="columns" rowKey="id">
      <template #action="record">
        <a-button type="link" @click="handleEdit(record)">编辑</a-button>
        <a-button type="link" @click="handleRemove(record)">删除</a-button>
      </template>
      <template #startTime="{ text }">
        {{ convertTimestampDate(text) }}
      </template>
      <template #endTime="{ text }">
        {{ convertTimestampDate(text) }}
      </template>
      <template #headTeacherId="{ text }">
        {{ teachers.find((teacher) => teacher.id === text)?.name }}
      </template>
      <template #lastOperationTime="{ text }">
        {{ convertTimestampDateTime(text) }}
      </template>
    </a-table>
    <a-pagination
      show-quick-jumper
      v-model:current="current"
      v-model:pageSize="pageSize"
      show-size-changer
      :total="500"
      @showSizeChange="onShowSizeChange"
      class="mt-2"
    />
    <a-modal
      v-model:visible="isAddModalVisible"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
    >
      <template #title>添加班级</template>
      <a-form-item label="班级名称">
        <a-input v-model:value="addForm.className" />
      </a-form-item>
      <a-form-item label="教室（可选）">
        <a-input v-model:value="addForm.classroom" />
      </a-form-item>
      <a-form-item label="开始时间">
        <a-date-picker v-model:value="addForm.startTime" type="datetime" />
      </a-form-item>
      <a-form-item label="结束时间">
        <a-date-picker v-model:value="addForm.endTime" type="datetime" />
      </a-form-item>
      <a-form-item label="班主任">
        <a-select
          allowClear
          :filterOption="filterOption"
          show-search
          v-model:value="addForm.headTeacherId"
        >
          <a-select-option v-for="teacher in teachers" :key="teacher.name" :value="teacher.id">
            {{ teacher.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-modal>
    <a-modal
      v-model:visible="isDeleteModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleDeleteOk"
      @cancel="handleDeleteCancel"
    >
      <template #title>删除班级</template>
      <p>确定要删除班级吗？</p>
    </a-modal>
    <a-modal
      v-model:visible="isEditModalVisible"
      ok-text="保存"
      cancel-text="取消"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
    >
      <template #title>编辑班级</template>
      <a-form-item label="班级名称">
        <a-input
          v-model:value="editForm.className"
          placeholder="请输入班级名称，如：2024第01期10班"
        />
      </a-form-item>
      <a-form-item label="教室">
        <a-input v-model:value="editForm.classroom" placeholder="请填写班级教室" />
      </a-form-item>
      <a-form-item label="开始时间">
        <a-date-picker
          v-model:value="editForm.startTime"
          type="datetime"
          placeholder="选择开课时间"
        />
      </a-form-item>
      <a-form-item label="结束时间">
        <a-date-picker
          v-model:value="editForm.endTime"
          type="datetime"
          placeholder="选择结课时间"
        />
      </a-form-item>
      <a-form-item label="班主任">
        <a-select
          allowClear
          :filterOption="filterOption"
          show-search
          v-model:value="editForm.headTeacherId"
          placeholder="请选择"
        >
          <a-select-option v-for="teacher in teachers" :key="teacher.name" :value="teacher.id">
            {{ teacher.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { isClass, Class } from '@/interface/class'
import { Employee, isEmployee } from '@/interface/employee'
import { convertTimestampDate } from '@/utils/time'
import notification from 'ant-design-vue/es/notification'
import axios from 'axios'
import dayjs, { Dayjs } from 'dayjs'
import { Ref, watch } from 'vue'
import { onMounted } from 'vue'
import { ref } from 'vue'
import { filterOption } from '@/utils/filterOption'
import { convertTimestampDateTime } from '@/utils/time'
const isDeleteModalVisible = ref(false)
function handleDeleteCancel() {
  isDeleteModalVisible.value = false
}
const classToDelete = ref(-1)
async function handleDeleteOk() {
  await axios.delete(`/api/secure/class/${classToDelete.value}`, {})
  classToDelete.value = -1
  isDeleteModalVisible.value = false
  await handleSearch()
}

function handleEditCancel() {
  isEditModalVisible.value = false
}

const current = ref(1)
const pageSize = ref(10)

function onShowSizeChange(newCurrent: number, newPageSize: number) {
  current.value = newCurrent
  pageSize.value = newPageSize
  handleSearch()
}

watch(current, () => {
  handleSearch()
})

watch(pageSize, () => {
  handleSearch()
})

const columns = [
  { title: '班级编号', dataIndex: 'id', key: 'id' },
  { title: '班级名称', dataIndex: 'className', key: 'className' },
  { title: '教室', dataIndex: 'classroom', key: 'classroom' },
  {
    title: '开始时间',
    dataIndex: 'startTime',
    key: 'startTime',
    slots: { customRender: 'startTime' },
  },
  { title: '结束时间', dataIndex: 'endTime', key: 'endTime', slots: { customRender: 'endTime' } },
  {
    title: '班主任',
    dataIndex: 'headTeacherId',
    key: 'headTeacherId',
    slots: { customRender: 'headTeacherId' },
  },
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

const classes = ref<Class[]>([])
const searchForm = ref<{
  name: string
  endTimeStart: Dayjs
  endTimeEnd: Dayjs
}>({
  name: '',
  endTimeStart: null as any,
  endTimeEnd: null as any,
})

async function handleSearch() {
  const params: any = {}
  if (searchForm.value.name.length > 0) {
    params.className = searchForm.value.name
  }
  if (searchForm.value.endTimeStart !== null) {
    params.endTimeStart = searchForm.value.endTimeStart.toDate().getTime()
  }
  if (searchForm.value.endTimeEnd !== null) {
    params.endTimeEnd = searchForm.value.endTimeEnd.toDate().getTime()
  }
  params.start = current.value * pageSize.value - pageSize.value
  params.end = current.value * pageSize.value

  classes.value = (
    await axios.get('/api/secure/class/search', {
      params: params,
    })
  ).data.filter(isClass)
}

const isAddModalVisible = ref(false)

interface ClassForm {
  className: string
  classroom: string
  startTime: Dayjs | null
  endTime: Dayjs | null
  headTeacherId: number | null
}

function getDefaultClassForm(): ClassForm {
  return {
    className: '',
    classroom: '',
    startTime: null,
    endTime: null,
    headTeacherId: null,
  }
}

const addForm = ref(getDefaultClassForm())

const teachers = ref<
  Array<{
    name: string
    id: number
  }>
>([])

async function validateClassForm(form: ClassForm, excluded: string = '') {
  // Validate class name
  if (
    !form.className ||
    form.className.length < 4 ||
    form.className.length > 30 ||
    !/^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(form.className)
  ) {
    notification.error({
      message: '班级名称非法',
      description: '班级名称必须为4-30个字符，且只能包含中文、数字和字母。',
    })
    return false
  }

  // Validate if class name is unique
  if (
    !(
      await axios.get(`/api/secure/class/valid/${form.className}`, {
        params: {
          excluded: excluded,
        },
      })
    ).data
  ) {
    notification.error({
      message: '班级名称非法',
      description: '班级已存在，请输入一个新的名称。',
    })
    return false
  }

  // Validate classroom
  if (
    (form.classroom.length > 20 ||
      !/^[\u4e00-\u9fa5a-zA-Z0-9]*$/.test(form.classroom))
  ) {
    notification.error({
      message: '教室无效',
      description: '教室名称必须为1-20个字符，且只能包含中文、数字和字母。',
    })
    return false
  }

  // Validate start time
  if (!form.startTime) {
    notification.error({
      message: '开始时间无效',
      description: '请选择一个开始时间。',
    })
    return false
  }

  // Validate end time
  if (!form.endTime) {
    notification.error({
      message: '结束时间无效',
      description: '请选择一个结束时间。',
    })
    return false
  }

  if (form.startTime.toDate().getTime() >= form.endTime.toDate().getTime()) {
    notification.error({
      message: '结束时间无效',
      description: '结束时间必须晚于开始时间。',
    })
    return false
  }

  // Validate head teacher
  if (!form.headTeacherId) {
    notification.error({
      message: '班主任无效',
      description: '请选择一个班主任。',
    })
    return false
  }

  return true
}

async function getEmployees() {
  try {
    const response = await axios.get('/api/secure/employee/search', {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    teachers.value = response.data
      .filter(isEmployee)
      .filter((employee: Employee) => {
        return employee.position == '班主任'
      })
      .map((employee: Employee) => {
        return {
          name: employee.name,
          id: employee.id,
        }
      })
  } catch (error) {
    console.error('Failed to search employees:', error)
  }
}

async function handleAddCancel() {
  isAddModalVisible.value = false
}

async function handleAddOk() {
  if (!(await validateClassForm(addForm.value))) {
    return
  }
  const formData = new FormData()
  formData.append('className', addForm.value.className)
  if (addForm.value.classroom) {
    formData.append('classroom', addForm.value.classroom)
  }
  if (addForm.value.startTime) {
    let startTime = addForm.value.startTime.toDate().getTime()
    formData.append('startTime', startTime.toString())
  }
  if (addForm.value.endTime) {
    let endTime = addForm.value.endTime.toDate().getTime()
    formData.append('endTime', endTime.toString())
  }
  if (addForm.value.headTeacherId) {
    formData.append('headTeacherId', addForm.value.headTeacherId.toString())
  }
  try {
    await axios.post('/api/secure/class', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    addForm.value = getDefaultClassForm()
    handleSearch()
  } catch (error) {
    console.error('Failed to create class:', error)
  }

  isAddModalVisible.value = false
}

const editForm = ref(getDefaultClassForm())
const isEditModalVisible = ref(false)

const classToEditID = ref<number>(0)

async function handleEdit(record: Ref<Class>) {
  editForm.value = {
    className: record.value.className,
    classroom: record.value.classroom,
    startTime: dayjs(record.value.startTime, 'YYYY-MM-DD'),
    endTime: dayjs(record.value.endTime, 'YYYY-MM-DD'),
    headTeacherId: record.value.headTeacherId,
  }
  classToEditID.value = record.value.id
  isEditModalVisible.value = true
}

function handleRemove(record: Ref<Class>) {
  console.info('record', record)
  classToDelete.value = record.value.id
  isDeleteModalVisible.value = true
}

async function handleEditOk() {
  if (!(await validateClassForm(editForm.value, editForm.value.className))) {
    return
  }
  const params = {
    className: editForm.value.className,
    classroom: editForm.value.classroom,
    startTime: editForm.value.startTime!.toDate().getTime(),
    endTime: editForm.value.endTime!.toDate().getTime(),
    headTeacherId: editForm.value.headTeacherId,
  }
  try {
    await axios.put(
      `/api/secure/class/${classToEditID.value}`,
      {},
      {
        params: params,
      },
    )
    editForm.value = getDefaultClassForm()
    classToEditID.value = 0
    isEditModalVisible.value = false
    handleSearch()
  } catch (error) {
    console.error('Failed to update class:', error)
  }
}

onMounted(async () => {
  handleSearch()
  getEmployees()
})
</script>
