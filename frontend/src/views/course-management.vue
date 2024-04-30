<template>
  <a-form @submit="handleSearch">
    <a-row :gutter="18">
      <a-col :span="5">
        <a-form-item label="课程名称">
          <a-input v-model:value="searchForm.courseName" />
        </a-form-item>
      </a-col>
      <a-col :span="4">
        <a-form-item label="学分">
          <a-input-number v-model:value="searchForm.courseCredit" />
        </a-form-item>
      </a-col>
      <a-col :span="6">
        <a-form-item label="最早结束时间">
          <a-date-picker placeholder="最早结束时间" v-model:value="searchForm.endTimeStart" />
        </a-form-item>
      </a-col>
      <a-col :span="6">
        <a-form-item label="最晚结束时间">
          <a-date-picker placeholder="最晚结束时间" v-model:value="searchForm.endTimeEnd" />
        </a-form-item>
      </a-col>
      <a-col :span="4">
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
        </a-form-item>
      </a-col>
    </a-row>
  </a-form>

  <a-button class="my-2" type="primary" @click="isAddModalVisible = true"> 添加课程 </a-button>

  <a-table :columns="columns" :dataSource="courses" rowKey="id" :pagination="false">
    <template #action="record">
      <a-button type="link" @click="handleEdit(record)">编辑</a-button>
      <a-button type="link" @click="handleRemove(record)">删除</a-button>
    </template>
    <template #startTime="{ text }">
      {{ convertTimestamp(text) }}
    </template>
    <template #endTime="{ text }">
      {{ convertTimestamp(text) }}
    </template>
    <template #lastOperationTime="{ text }">
      {{ convertTimestamp(text) }}
    </template>
  </a-table>

  <a-pagination
    v-model:current="current"
    v-model:pageSize="pageSize"
    show-size-changer
    :total="500"
    @showSizeChange="onShowSizeChange"
    class="mt-2"
  />

  <a-modal
    v-model:visible="isDeleteModalVisible"
    ok-text="确认"
    cancel-text="取消"
    @ok="handleDeleteOk"
    @cancel="handleDeleteCancel"
  >
    <template #title>删除课程</template>
    <p>确定要删除这个课程吗？</p>
  </a-modal>

  <a-modal
    v-model:visible="isAddModalVisible"
    ok-text="Confirm"
    cancel-text="Cancel"
    @ok="handleAddOk"
    @cancel="handleAddCancel"
  >
    <template #title>添加课程</template>
    <a-form-item label="课程名">
      <a-input v-model:value="addCourseForm.courseName" />
    </a-form-item>
    <a-form-item label="学分">
      <a-input-number v-model:value="addCourseForm.courseCredit" :min="0" />
    </a-form-item>
    <a-form-item label="开始时间">
      <a-date-picker
        class="mx-2"
        v-model:value="addCourseForm.startTime"
        type="date"
        placeholder="开始时间"
      />
    </a-form-item>
    <a-form-item label="结束时间">
      <a-date-picker
        class="mx-2"
        v-model:value="addCourseForm.endTime"
        type="date"
        placeholder="结束时间"
      />
    </a-form-item>
  </a-modal>

  <a-modal
    v-model:visible="isEditModalVisible"
    ok-text="Confirm"
    cancel-text="Cancel"
    @ok="handleEditOk"
    @cancel="handleEditCancel"
  >
    <template #title>编辑课程</template>
    <a-form-item label="课程名">
      <a-input v-model:value="editCourseForm.courseName" />
    </a-form-item>
    <a-form-item label="学分">
      <a-input-number v-model:value="editCourseForm.courseCredit" :min="0" />
    </a-form-item>
    <a-form-item label="开始时间">
      <a-date-picker
        class="mx-2"
        v-model:value="editCourseForm.startTime"
        type="date"
        placeholder="开始时间"
      />
    </a-form-item>
    <a-form-item label="结束时间">
      <a-date-picker
        class="mx-2"
        v-model:value="editCourseForm.endTime"
        type="date"
        placeholder="结束时间"
      />
    </a-form-item>
  </a-modal>
</template>

<script setup lang="ts">
import { Course, isCourse } from '@/interface/course'
import axios from 'axios'
import { Ref, onMounted } from 'vue'
import { ref } from 'vue'
import convertTimestamp from '@/utils/time'
import { Dayjs } from 'dayjs'
import dayjs from 'dayjs'
import { notification } from 'ant-design-vue'
import { watch } from 'vue'

const current = ref(1)
const pageSize = ref(10)

function onShowSizeChange(newCurrent: number, newPageSize: number) {
  current.value = newCurrent
  pageSize.value = newPageSize
}

watch(current, async () => {
  await handleSearch()
})

watch(pageSize, async () => {
  await handleSearch()
})

interface SearchForm {
  courseName: string
  courseCredit: number | null
  endTimeStart: string
  endTimeEnd: string
}

const courses = ref<Array<Course>>([])

const searchForm = ref<SearchForm>({
  courseName: '',
  courseCredit: null,
  endTimeStart: '',
  endTimeEnd: '',
})
const columns = [
  {
    title: '课程名称',
    dataIndex: 'courseName',
    key: 'courseName',
  },
  {
    title: '学分',
    dataIndex: 'courseCredit',
    key: 'courseCredit',
  },
  {
    title: '开始时间',
    dataIndex: 'startTime',
    key: 'startTime',
    slots: { customRender: 'startTime' },
  },
  {
    title: '结束时间',
    dataIndex: 'endTime',
    key: 'endTime',
    slots: { customRender: 'endTime' },
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

async function validateCourseForm(form: CourseForm) {
  if (!form.courseName) {
    notification.error({
      message: '课程名不能为空',
    })
    return false
  }
  if (!form.courseCredit) {
    notification.error({
      message: '学分不能为空',
    })
    return false
  }
  if (form.courseCredit < 0) {
    notification.error({
      message: '学分不能为负数',
    })
    return false
  }
  if (!form.startTime) {
    notification.error({
      message: '开始时间不能为空',
    })
    return false
  }
  if (!form.endTime) {
    notification.error({
      message: '结束时间不能为空',
    })
    return false
  }
  if (form.startTime && form.endTime && form.startTime.isAfter(form.endTime)) {
    notification.error({
      message: '开始时间不能晚于结束时间',
    })
    return false
  }
  return true
}

async function handleSearch() {
  const params: any = {}
  if (searchForm.value.courseName.length > 0) {
    params.courseName = searchForm.value.courseName
  }
  if (searchForm.value.courseCredit) {
    params.courseCredit = searchForm.value.courseCredit
  }
  if (searchForm.value.endTimeStart) {
    let endTimeStart = new Date(searchForm.value.endTimeStart).getTime()
    params.endTimeStart = endTimeStart
  }
  if (searchForm.value.endTimeEnd) {
    let endTimeEnd = new Date(searchForm.value.endTimeEnd).getTime()
    params.endTimeEnd = endTimeEnd
  }
  params.start = current.value * pageSize.value - pageSize.value
  params.end = current.value * pageSize.value
  courses.value = (
    await axios.get('/api/secure/course/search', {
      params: params,
    })
  ).data.filter(isCourse)
}

const courseToDelete = ref(-1)
const isDeleteModalVisible = ref(false)

async function handleDeleteOk() {
  if (courseToDelete.value !== -1) {
    await axios.delete(`/api/secure/course/${courseToDelete.value}`)
    courseToDelete.value = -1
    isDeleteModalVisible.value = false
    await handleSearch()
  }
}

function handleDeleteCancel() {
  isDeleteModalVisible.value = false
}

async function handleRemove(record: Ref<Course>) {
  courseToDelete.value = record.value.id
  isDeleteModalVisible.value = true
}

interface CourseForm {
  courseName: string
  courseCredit: number | null
  startTime: Dayjs | null
  endTime: Dayjs | null
}

function getDefaultCourseForm(): CourseForm {
  return {
    courseName: '',
    courseCredit: null,
    startTime: null,
    endTime: null,
  }
}

const addCourseForm = ref<CourseForm>(getDefaultCourseForm())
const editCourseForm = ref<CourseForm>(getDefaultCourseForm())

const isAddModalVisible = ref(false)

async function handleAddOk() {
  if (!(await validateCourseForm(addCourseForm.value))) {
    return
  }
  if (
    addCourseForm.value.courseName &&
    addCourseForm.value.courseCredit &&
    addCourseForm.value.startTime &&
    addCourseForm.value.endTime
  ) {
    const courseToAdd = {
      courseName: addCourseForm.value.courseName,
      courseCredit: addCourseForm.value.courseCredit,
      startTime: addCourseForm.value.startTime.toDate().getTime().toString(),
      endTime: addCourseForm.value.endTime.toDate().getTime().toString(),
    }
    await axios.post(
      '/api/secure/course',
      {},
      {
        params: courseToAdd,
      },
    )
    isAddModalVisible.value = false
    addCourseForm.value = getDefaultCourseForm()
    await handleSearch()
  }
}

function handleAddCancel() {
  isAddModalVisible.value = false
}

const isEditModalVisible = ref(false)

async function handleEdit(record: Ref<Course>) {
  editId.value = record.value.id
  editCourseForm.value = {
    courseName: record.value.courseName,
    courseCredit: record.value.courseCredit,
    startTime: dayjs(record.value.startTime, 'YYYY-MM-DD'),
    endTime: dayjs(record.value.endTime, 'YYYY-MM-DD'),
  }
  isEditModalVisible.value = true
}

const editId = ref(0)

async function handleEditOk() {
  if (!(await validateCourseForm(editCourseForm.value))) {
    return
  }
  if (
    editCourseForm.value.courseName &&
    editCourseForm.value.courseCredit &&
    editCourseForm.value.startTime &&
    editCourseForm.value.endTime
  ) {
    const courseToEdit = {
      courseName: editCourseForm.value.courseName,
      courseCredit: editCourseForm.value.courseCredit,
      startTime: editCourseForm.value.startTime.toDate().getTime().toString(),
      endTime: editCourseForm.value.endTime.toDate().getTime().toString(),
    }
    await axios.put(
      `/api/secure/course/${editId.value}`,
      {},
      {
        params: courseToEdit,
      },
    )
    isEditModalVisible.value = false
    editCourseForm.value = getDefaultCourseForm()
    editId.value = 0
    await handleSearch()
  }
}

function handleEditCancel() {
  isEditModalVisible.value = false
}

onMounted(async () => {
  await handleSearch()
})
</script>
