<template>
  <div>
    <a-button class="my-2" type="primary" @click="isAddModalVisible = true">添加学生</a-button>
    <a-button
      class="my-2 mx-2"
      type="primary"
      :disabled="deleteCandidates.length === 0"
      @click="isDeleteMultipleModalVisible = true"
    >
      批量删除
    </a-button>
    <a-form @submit="() => {
      current = 1;
      handleSearch();
    }">
      <a-row :gutter="16">
        <a-col :span="4">
          <a-form-item label="姓名">
            <a-input v-model:value="searchForm.name" />
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item label="学号">
            <a-input v-model:value="searchForm.studentNumber" />
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item label="班级">
            <a-input v-model:value="searchForm.className" />
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item label="最高学历">
            <a-select
              allowClear
              :filterOption="filterOption"
              show-search
              v-model:value="searchForm.highestEducation"
            >
              <a-select-option value="初中">初中</a-select-option>
              <a-select-option value="高中">高中</a-select-option>
              <a-select-option value="大专">大专</a-select-option>
              <a-select-option value="本科">本科</a-select-option>
              <a-select-option value="硕士">硕士</a-select-option>
              <a-select-option value="博士">博士</a-select-option>
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="4">
          <a-form-item>
            <a-button type="primary" @click="() => {
              current = 1;
              handleSearch();
            }">搜索</a-button>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <a-table :pagination="false" :dataSource="students" :columns="columns" rowKey="id">
      <template #checkbox="{ text }">
        <a-checkbox @change="(e: Event) => handleDeleteCandidates(e, text)" />
      </template>
      <template #lastOperationTime="{ text }">
        {{ convertTimestampDateTime(text) }}
      </template>
      <template #class="{ text }">
        {{ classes.find((c) => c.id === text)?.name }}
      </template>
      <template #action="{ record }">
        <a-button type="link" @click="editStudent(record)">编辑</a-button>
        <a-button type="link" @click="deleteStudent(record)">删除</a-button>
        <a-button type="link" @click="editDisciplinaryStudent(record)">违纪</a-button>
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
      ok-text="确认"
      cancel-text="取消"
      @ok="handleAddOk"
      @cancel="handleAddCancel"
    >
      <template #title>添加学员</template>
      <a-form-item label="姓名">
        <a-input v-model:value="addStudentForm.name" />
      </a-form-item>
      <a-form-item label="学号">
        <a-input v-model:value="addStudentForm.studentNumber" />
      </a-form-item>
      <a-form-item label="班级">
        <a-select
          allowClear
          :filterOption="filterOption"
          show-search
          v-model:value="addStudentForm.classId"
        >
          <a-select-option v-for="c in classes" :key="c.name" :value="c.id">{{
            c.name
          }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="性别">
        <a-select
          allowClear
          :filterOption="filterOption"
          show-search
          v-model:value="addStudentForm.gender"
        >
          <a-select-option value="男性">男性</a-select-option>
          <a-select-option value="女性">女性</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="电话号码">
        <a-input v-model:value="addStudentForm.phoneNumber" />
      </a-form-item>
      <a-form-item label="最高学历">
        <a-select
          allowClear
          :filterOption="filterOption"
          show-search
          v-model:value="addStudentForm.highestEducation"
        >
          <a-select-option value="初中">初中</a-select-option>
          <a-select-option value="高中">高中</a-select-option>
          <a-select-option value="大专">大专</a-select-option>
          <a-select-option value="本科">本科</a-select-option>
          <a-select-option value="硕士">硕士</a-select-option>
          <a-select-option value="博士">博士</a-select-option>
        </a-select>
      </a-form-item>
    </a-modal>
    <a-modal
      v-model:visible="isEditModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleEditOk"
      @cancel="handleEditCancel"
    >
      <template #title>编辑学员信息</template>
      <a-form-item label="姓名">
        <a-input v-model:value="editStudentForm.name" />
      </a-form-item>
      <a-form-item label="学号">
        <a-input v-model:value="editStudentForm.studentNumber" />
      </a-form-item>
      <a-form-item label="班级">
        <a-select
          allowClear
          :filterOption="filterOption"
          show-search
          v-model:value="editStudentForm.classId"
        >
          <a-select-option v-for="c in classes" :key="c.name" :value="c.id">{{
            c.name
          }}</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="性别">
        <a-select
          allowClear
          :filterOption="filterOption"
          show-search
          v-model:value="editStudentForm.gender"
        >
          <a-select-option value="男性">男性</a-select-option>
          <a-select-option value="女性">女性</a-select-option>
        </a-select>
      </a-form-item>
      <a-form-item label="电话号码">
        <a-input v-model:value="editStudentForm.phoneNumber" />
      </a-form-item>
      <a-form-item label="最高学历">
        <a-select
          allowClear
          :filterOption="filterOption"
          show-search
          v-model:value="editStudentForm.highestEducation"
        >
          <a-select-option value="初中">初中</a-select-option>
          <a-select-option value="高中">高中</a-select-option>
          <a-select-option value="大专">大专</a-select-option>
          <a-select-option value="本科">本科</a-select-option>
          <a-select-option value="硕士">硕士</a-select-option>
          <a-select-option value="博士">博士</a-select-option>
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
      <template #title>删除学生</template>
      <p>你确定要删除该学生吗？</p>
    </a-modal>
    <a-modal
      v-model:visible="isDisciplinaryModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleEditDisciplinaryOk"
      @cancel="handleEditDisciplinaryCancel"
    >
      <template #title>违纪处理</template>
      <a-form-item label="违纪扣分">
        <a-input-number v-model:value="disciplinaryPoints" :min="0" />
      </a-form-item>
    </a-modal>
    <a-modal
      v-model:visible="isDeleteMultipleModalVisible"
      ok-text="确认"
      cancel-text="取消"
      @ok="handleDeleteMultipleOk"
      @cancel="handleDeleteMultipleCancel"
    >
      <template #title>批量删除学生</template>
      <p>确定要批量删除学生吗？</p>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { convertTimestampDateTime } from '@/utils/time'
import { type Student } from '@/interface/student'
import { Class, isClass } from '@/interface/class'
import { filterOption } from '@/utils/filterOption'
import { notification } from 'ant-design-vue'

const deleteCandidates = ref<Array<number>>([])

function handleDeleteCandidates(e: Event, id: number) {
  if ((e as any).target.checked) {
    deleteCandidates.value = [...deleteCandidates.value, id]
  } else {
    deleteCandidates.value = deleteCandidates.value.filter((value) => value !== id)
  }
}

// UI Part
const current = ref(1)
const pageSize = ref(10)
function onShowSizeChange(newCurrent: number, newPageSize: number) {
  current.value = newCurrent
  pageSize.value = newPageSize
}

watch(current, () => {
  handleSearch()
})

watch(pageSize, () => {
  handleSearch()
})

const isAddModalVisible = ref(false)
const columns = [
  { title: '', dataIndex: 'id', key: 'checkbox', slots: { customRender: 'checkbox' } },
  { title: '学号', dataIndex: 'id', key: 'id' },
  { title: '姓名', dataIndex: 'name', key: 'name' },
  { title: '学号', dataIndex: 'studentNumber', key: 'studentNumber' },
  { title: '班级', dataIndex: 'classId', key: 'classId', slots: { customRender: 'class' } },
  { title: '性别', dataIndex: 'gender', key: 'gender' },
  { title: '电话号码', dataIndex: 'phoneNumber', key: 'phoneNumber' },
  { title: '最高学历', dataIndex: 'highestEducation', key: 'highestEducation' },
  { title: '违纪次数', dataIndex: 'disciplinaryActions', key: 'disciplinaryActions' },
  { title: '违纪扣分', dataIndex: 'disciplinaryPoints', key: 'disciplinaryPoints' },
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

const classes = ref<
  Array<{
    id: string
    name: string
  }>
>([])

async function fetchClasses() {
  classes.value = (await axios.get('/api/secure/class/search', {})).data
    .filter(isClass)
    .map((c: Class) => {
      return {
        id: c.id,
        name: c.className,
      }
    })
}

const students = ref<Array<Student>>([])

interface SearchForm {
  name: string
  studentNumber: string
  className: string
  highestEducation: string
}

const searchForm = ref<SearchForm>({
  name: '',
  studentNumber: '',
  className: '',
  highestEducation: '',
})

async function handleSearch() {
  const params: any = {}
  if (searchForm.value.name) {
    params.name = searchForm.value.name
  }
  if (searchForm.value.studentNumber) {
    params.studentNumber = searchForm.value.studentNumber
  }
  if (searchForm.value.className) {
    params.className = searchForm.value.className
  }
  if (searchForm.value.highestEducation) {
    params.highestEducation = searchForm.value.highestEducation
  }
  params.start = current.value * pageSize.value - pageSize.value
  params.end = current.value * pageSize.value

  const response = await axios.get('/api/secure/student/search', {
    params: params,
  })
  students.value = response.data
}

interface StudentForm {
  name: string
  studentNumber: string
  classId: number | null
  gender: string
  phoneNumber: string
  highestEducation: string
}

function getStudentDefaultForm(): StudentForm {
  return {
    name: '',
    studentNumber: '',
    classId: null,
    gender: '',
    phoneNumber: '',
    highestEducation: '',
  }
}

const addStudentForm = ref(getStudentDefaultForm())
const editStudentForm = ref(getStudentDefaultForm())

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
        await axios.delete(`/api/secure/student/${id}`, {})
      }),
    )
    deleteCandidates.value = []
    handleSearch()
  } catch (error) {
    console.error('Failed to delete employees:', error)
  }
}

async function validateStudentForm(
  form: StudentForm,
  excludedNumber: string = '',
  excludedPhone: string = '',
) {
  if (
    !form.name ||
    form.name.length < 2 ||
    form.name.length > 10 ||
    !/^[\u4e00-\u9fa5a-zA-Z0-9]+$/.test(form.name)
  ) {
    notification.error({
      message: '学员姓名无效',
      description: '学员姓名必须为2-10个字符，只能包含中文、英文和数字。',
    })
    return false
  }

  if (
    !form.studentNumber ||
    form.studentNumber.length !== 10 ||
    !/^[a-zA-Z0-9]+$/.test(form.studentNumber)
  ) {
    notification.error({
      message: '学号无效',
      description: '学号必须为10个字符，只能包含英文和数字。',
    })
    return false
  }

  if (excludedNumber.length > 0) {
    if (
      !(
        await axios.get(`/api/secure/student/valid/number/${form.studentNumber}`, {
          params: { excluded: excludedNumber },
        })
      ).data
    ) {
      notification.error({
        message: '学号无效',
        description: '该学号已存在，请输入新的学号。',
      })
      return false
    }
  } else {
    if (!(await axios.get(`/api/secure/student/valid/number/${form.studentNumber}`, {})).data) {
      notification.error({
        message: '学号无效',
        description: '该学号已存在，请输入新的学号。',
      })
      return false
    }
  }

  if (!form.gender || (form.gender !== '男性' && form.gender !== '女性')) {
    notification.error({
      message: '性别无效',
      description: '请选择一个有效的性别。',
    })
    return false
  }

  if (!form.phoneNumber || form.phoneNumber.length !== 11 || !/^\d{11}$/.test(form.phoneNumber)) {
    notification.error({
      message: '电话号码无效',
      description: '电话号码必须为11位数字。',
    })
    return false
  }

  if (excludedPhone.length > 0) {
    if (
      !(
        await axios.get(`/api/secure/student/valid/phone/${form.phoneNumber}`, {
          params: { excluded: excludedPhone },
        })
      ).data
    ) {
      notification.error({
        message: '电话号码无效',
        description: '该电话号码已存在，请输入新的电话号码。',
      })
      return false
    }
  } else {
    if (!(await axios.get(`/api/secure/student/valid/phone/${form.phoneNumber}`, {})).data) {
      notification.error({
        message: '电话号码无效',
        description: '该电话号码已存在，请输入新的电话号码。',
      })
      return false
    }
  }

  if (
    form.highestEducation &&
    !['初中', '高中', '大专', '本科', '硕士', '博士'].includes(form.highestEducation)
  ) {
    notification.error({
      message: '最高学历无效',
      description: '请选择一个有效的最高学历。',
    })
    return false
  }

  if (!form.classId) {
    notification.error({
      message: '班级无效',
      description: '请选择一个有效的班级。',
    })
    return false
  }

  return true
}

async function addStudent() {
  if (!(await validateStudentForm(addStudentForm.value))) {
    return
  }
  const form = new FormData()
  form.append('name', addStudentForm.value.name)
  form.append('studentNumber', addStudentForm.value.studentNumber)
  form.append('classId', addStudentForm.value.classId!.toString())
  form.append('gender', addStudentForm.value.gender)
  form.append('phoneNumber', addStudentForm.value.phoneNumber)
  form.append('highestEducation', addStudentForm.value.highestEducation)
  await axios.post('/api/secure/student', form, {})
  isAddModalVisible.value = false
  addStudentForm.value = getStudentDefaultForm()
  handleSearch()
}

async function handleAddOk() {
  await addStudent()
}

function handleAddCancel() {
  isAddModalVisible.value = false
}

const isEditModalVisible = ref(false)
const editStudentId = ref(-1)

async function editStudent(student: Student) {
  editStudentForm.value = {
    name: student.name,
    studentNumber: student.studentNumber,
    classId: student.classId,
    gender: student.gender,
    phoneNumber: student.phoneNumber,
    highestEducation: student.highestEducation,
  }
  editStudentId.value = student.id
  isEditModalVisible.value = true
}

function handleEditCancel() {
  isEditModalVisible.value = false
}

async function handleEditOk() {
  const previousStudent = students.value.find((s) => s.id === editStudentId.value)
  if (
    !(await validateStudentForm(
      editStudentForm.value,
      previousStudent?.studentNumber,
      previousStudent?.phoneNumber,
    ))
  ) {
    return
  }
  const form = new FormData()
  form.append('name', editStudentForm.value.name)
  form.append('studentNumber', editStudentForm.value.studentNumber)
  form.append('classId', editStudentForm.value.classId!.toString())
  form.append('gender', editStudentForm.value.gender)
  form.append('phoneNumber', editStudentForm.value.phoneNumber)
  form.append('highestEducation', editStudentForm.value.highestEducation)

  await axios.put(`/api/secure/student/${editStudentId.value}`, form, {})
  isEditModalVisible.value = false
  handleSearch()
}

const isDeleteModalVisible = ref(false)

function deleteStudent(student: Student) {
  editStudentId.value = student.id
  isDeleteModalVisible.value = true
}

async function handleDeleteOk() {
  await axios.delete(`/api/secure/student/${editStudentId.value}`, {})
  isDeleteModalVisible.value = false
  handleSearch()
}

function handleDeleteCancel() {
  isDeleteModalVisible.value = false
}

const isDisciplinaryModalVisible = ref(false)
const disciplinaryStudentId = ref(-1)
const disciplinaryPoints = ref(0)

function editDisciplinaryStudent(student: Student) {
  disciplinaryStudentId.value = student.id
  isDisciplinaryModalVisible.value = true
}

async function handleEditDisciplinaryOk() {
  await axios.put(
    `/api/secure/student/disciplinary/${disciplinaryStudentId.value}`,
    {},
    {
      params: {
        deltaDisciplinaryActions: 1,
        deltaDisciplinaryPoints: disciplinaryPoints.value,
      },
    },
  )
  isDisciplinaryModalVisible.value = false
  disciplinaryPoints.value = 0
  handleSearch()
}

function handleEditDisciplinaryCancel() {
  isDisciplinaryModalVisible.value = false
}

const isDeleteMultipleModalVisible = ref(false)

onMounted(async () => {
  await fetchClasses()
  handleSearch()
})
</script>
