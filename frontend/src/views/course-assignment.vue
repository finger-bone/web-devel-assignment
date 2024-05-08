<template>
  <a-form @submit="() => {
    current = 1
    handleSearch();
  }">
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

  <a-table :columns="columns" :dataSource="courses" rowKey="id" :pagination="false">
    <template #startTime="{ text }">
      {{ convertTimestampDate(text) }}
    </template>
    <template #endTime="{ text }">
      {{ convertTimestampDate(text) }}
    </template>
    <template #teachers="{ text }">
      <div v-for="each in courseIdToTeachers.get(text)">
        <div class="flex flex-row items-center">
          <span>{{ each.name }}</span>
          <a-button type="link" @click="handleDeleteTeacher(each.id, text)">取消分配</a-button>
        </div>
      </div>
      <div class="flex flex-row items-center">
        <a-button type="link" @click="handleAddTeacher(text)">添加</a-button>
      </div>
    </template>
    <template #classes="{ text }">
      <div v-for="each in courseIdToClasses.get(text)">
        <div class="flex flex-row items-center">
          <span>{{ each.className }}</span>
          <a-button type="link" @click="handleDeleteClass(each.id, text)">取消分配</a-button>
        </div>
      </div>
      <div class="flex flex-row items-center">
        <a-button type="link" @click="handleAddClass(text)">添加</a-button>
      </div>
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
    v-model:visible="isDeleteTeacherModalVisible"
    ok-text="确认"
    cancel-text="取消"
    @ok="handleDeleteTeacherOk"
    @cancel="handleDeleteTeacherCancel"
  >
    <template #title>取消分配</template>
    <p>确定要取消分配该教师吗？</p>
  </a-modal>

  <a-modal
    v-model:visible="isAddTeacherModalVisible"
    ok-text="确认"
    cancel-text="取消"
    @ok="handleAddTeacherOk"
    @cancel="handleAddTeacherCancel"
  >
    <template #title>分配教师</template>
    <a-form @submit="handleAddTeacherSearch">
      <a-row :gutter="12">
        <a-col :span="18">
          <a-form-item label="教师姓名">
            <a-input v-model:value="addTeacherSearchName" />
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item>
            <a-button type="primary" html-type="submit">搜索</a-button>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-form>
      <a-form-item label="选择教师">
        <a-select allowClear :filterOption="filterOption" show-search v-model:value="addTeacherId">
          <a-select-option v-for="each in addTeachers" :key="each.name" :value="each.id">
            {{ each.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>

  <a-modal
    v-model:visible="isAddClassModalVisible"
    ok-text="确认"
    cancel-text="取消"
    @ok="handleAddClassOk"
    @cancel="handleAddClassCancel"
  >
    <template #title>分配班级</template>
    <a-form @submit="handleAddClassSearch">
      <a-row :gutter="12">
        <a-col :span="18">
          <a-form-item label="班级名称">
            <a-input v-model:value="addClassSearchName" />
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item>
            <a-button type="primary" html-type="submit">搜索</a-button>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-form>
      <a-form-item label="选择班级">
        <a-select allowClear :filterOption="filterOption" show-search v-model:value="addClassId">
          <a-select-option v-for="each in addClasses" :key="each.name" :value="each.id">
            {{ each.name }}
          </a-select-option>
        </a-select>
      </a-form-item>
    </a-form>
  </a-modal>

  <!-- Remove Class Modal -->
  <a-modal
    v-model:visible="isDeleteClassModalVisible"
    ok-text="确认"
    cancel-text="取消"
    @ok="handleDeleteClassOk"
    @cancel="handleDeleteClassCancel"
  >
    <template #title>取消分配</template>
    <p>确定要取消分配该班级吗？</p>
  </a-modal>
</template>

<script setup lang="ts">
import { Course, isCourse } from '@/interface/course'
import axios from 'axios'
import { onMounted } from 'vue'
import { ref } from 'vue'
import { filterOption } from '@/utils/filterOption'
import { convertTimestampDate } from '@/utils/time'
import { watch } from 'vue'
import { Class, isClass } from '@/interface/class'
import { Employee, isEmployee } from '@/interface/employee'

const current = ref(1)
const pageSize = ref(10)

function handleAddTeacher(text: number) {
  courseIdToAddTeacher.value = text
  isAddTeacherModalVisible.value = true
  handleAddTeacherSearch()
}

function handleAddClass(text: number) {
  courseIdToAddClass.value = text
  isAddClassModalVisible.value = true
  handleAddClassSearch()
}

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
    title: '已分配教师',
    key: 'id',
    dataIndex: 'id',
    slots: { customRender: 'teachers' },
  },
  {
    title: '已分配班级',
    key: 'id',
    dataIndex: 'id',
    slots: { customRender: 'classes' },
  },
]

const courseIdToClasses = ref(new Map<number, Array<Class>>())
const courseIdToTeachers = ref(new Map<number, Array<Employee>>())

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
  courseIdToClasses.value = new Map<number, Array<Class>>()
  courses.value.forEach((course: Course) => {
    axios.get(`/api/secure/course/${course.id}/class`).then((res) => {
      courseIdToClasses.value.set(course.id, res.data)
    })
  })
  courseIdToTeachers.value = new Map<number, Array<Employee>>()
  courses.value.forEach((course: Course) => {
    axios.get(`/api/secure/course/${course.id}/teacher`).then((res) => {
      courseIdToTeachers.value.set(course.id, res.data)
    })
  })
}

const isDeleteTeacherModalVisible = ref(false)

const courseIdToDeleteTeacher = ref<number>(0)
const deleteTeacherId = ref<number>(0)
function handleDeleteTeacher(id: number, courseId: number) {
  courseIdToDeleteTeacher.value = courseId
  deleteTeacherId.value = id
  isDeleteTeacherModalVisible.value = true
}
async function handleDeleteTeacherOk() {
  await axios.delete(
    `/api/secure/course/${courseIdToDeleteTeacher.value}/teacher/${deleteTeacherId.value}`,
  )
  isDeleteTeacherModalVisible.value = false
  await handleSearch()
}
function handleDeleteTeacherCancel() {
  isDeleteTeacherModalVisible.value = false
}

const addTeacherSearchName = ref<string>('')
const courseIdToAddTeacher = ref<number>(0)
const addTeacherId = ref<number | null>(null)
const isAddTeacherModalVisible = ref(false)
const addTeachers = ref<
  Array<{
    id: number
    name: string
  }>
>([])

function handleAddTeacherCancel() {
  isAddTeacherModalVisible.value = false
}

async function handleAddTeacherSearch() {
  addTeachers.value = (
    await axios.get('/api/secure/employee/search', {
      params: {
        name: addTeacherSearchName.value,
      },
    })
  ).data
    .filter(isEmployee)
    .filter((each: Employee) => {
      return !courseIdToTeachers.value
        .get(courseIdToAddTeacher.value)
        ?.map((each: Employee) => each.id)
        .includes(each.id)
    })
    .map((each: Employee) => {
      return {
        id: each.id,
        name: each.name,
      }
    })
}

async function handleAddTeacherOk() {
  await axios.post(`/api/secure/course/${courseIdToAddTeacher.value}/teacher/${addTeacherId.value}`)
  isAddTeacherModalVisible.value = false
  addTeacherSearchName.value = ''
  addTeacherId.value = null
  await handleSearch()
}

const isAddClassModalVisible = ref(false)
const addClassSearchName = ref<string>('')
const courseIdToAddClass = ref<number>(0)
const addClassId = ref<number | null>(null)
const addClasses = ref<
  Array<{
    id: number
    name: string
  }>
>([])

// Remove Class Modal
const isDeleteClassModalVisible = ref(false)
const courseIdToDeleteClass = ref<number>(0)
const deleteClassId = ref<number>(0)

// Handle Add Class
async function handleAddClassOk() {
  await axios.post(`/api/secure/course/${courseIdToAddClass.value}/class/${addClassId.value}`)
  isAddClassModalVisible.value = false
  addClassSearchName.value = ''
  addClassId.value = null
  await handleSearch()
}

function handleAddClassCancel() {
  isAddClassModalVisible.value = false
}

async function handleAddClassSearch() {
  addClasses.value = (
    await axios.get('/api/secure/class/search', {
      params: {
        name: addClassSearchName.value,
      },
    })
  ).data
    .filter(isClass)
    .filter((each: Class) => {
      return !courseIdToClasses.value
        .get(courseIdToAddClass.value)
        ?.map((each: Class) => each.id)
        .includes(each.id)
    })
    .map((each: Class) => {
      return {
        id: each.id,
        name: each.className,
      }
    })
}

// Handle Remove Class
function handleDeleteClass(id: number, courseId: number) {
  courseIdToDeleteClass.value = courseId
  deleteClassId.value = id
  isDeleteClassModalVisible.value = true
}

async function handleDeleteClassOk() {
  await axios.delete(
    `/api/secure/course/${courseIdToDeleteClass.value}/class/${deleteClassId.value}`,
  )
  isDeleteClassModalVisible.value = false
  await handleSearch()
}

function handleDeleteClassCancel() {
  isDeleteClassModalVisible.value = false
}

onMounted(async () => {
  await handleSearch()
})
</script>
