<template>
    <a-form @submit="handleTeacherSearch">
      <a-row :gutter="12">
        <a-col :span="8">
          <a-form-item label="教师姓名">
            <a-input v-model:value="teacherSearchName" />
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
        <a-row :gutter="16">
            <a-col :span="8">
                <a-form-item label="查看此教师时间表">
                    <a-select allowClear :filterOption="filterOption" show-search v-model:value="showScheduleTeacherId">
                        <a-select-option v-for="each in teachers" :key="each.name" :value="each.id">
                            {{ each.name }}
                        </a-select-option>
                    </a-select>
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="从">
                    <a-date-picker v-model:value="courseTimeRangeStart" placeholder="开始时间"/>
                </a-form-item>
            </a-col>
            <a-col :span="4">
                <a-form-item label="到">
                    <a-date-picker v-model:value="courseTimeRangeEnd" placeholder="结束时间"/>
                </a-form-item>
            </a-col>
        </a-row>
    </a-form>
    <div class="w-full h-[50vh]">
        <VueECharts :option="timeScheduleOption" />
    </div>
</template>

<script lang="ts" setup>
import * as echarts from 'echarts'
echarts;
import VueECharts from 'vue-echarts'
import { isEmployee, Employee } from '@/interface/employee';
import axios from 'axios';
import { filterOption } from '@/utils/filterOption';
import { Course, isCourse } from '@/interface/course';
import { watch } from 'vue';
import dayjs, { Dayjs } from 'dayjs';
import { onMounted, ref } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'
import { computed } from 'vue';

use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent])


const teacherSearchName = ref('');
const teachers = ref<Array<{
    name: string;
    id: number
}>>([]);
const showScheduleTeacherId = ref<number | null>(null);
const courseTimeRangeStart = ref<Dayjs | null>(null);
const courseTimeRangeEnd = ref<Dayjs | null>(null);

async function handleTeacherSearch() {
    teachers.value = (
    await axios.get('/api/secure/employee/search', {
      params: {
        name: teacherSearchName.value,
      },
    })
  ).data
    .filter(isEmployee)
    .map((each: Employee) => {
      return {
        id: each.id,
        name: each.name,
      }
    })
}

onMounted(async () => {
    await handleTeacherSearch();
});

const courses = ref<Array<Course>>([]);

async function getTeacherSchedule() {
    if (showScheduleTeacherId.value === null || showScheduleTeacherId.value === undefined) {
        return;
    }
    courses.value = (
        await axios.get(`/api/secure/course/assignment/teacher/${showScheduleTeacherId.value}`)
    ).data
    .filter(isCourse)
    .filter((course: Course) => {
        return courseTimeRangeStart.value === null || courseTimeRangeStart.value.isBefore(course.endTime)
    })
    .filter((course: Course) => {
        return courseTimeRangeEnd.value === null || courseTimeRangeEnd.value.isAfter(course.startTime)
    })
}

watch(showScheduleTeacherId, async () => {
    await getTeacherSchedule();
})

watch(courseTimeRangeStart, async () => {
    await getTeacherSchedule();
})

watch(courseTimeRangeEnd, async () => {
    await getTeacherSchedule();
})

const courseData = computed(() => {
    return courses.value.map((course) => {
        return {
            name: course.courseName,
            value: [
                dayjs(course.startTime).toDate().getTime(), 
                dayjs(course.endTime).toDate().getTime()
            ]
        }
    });
})

const timeScheduleOption = computed(() => {
    // 将课程数据转换为 ECharts Gantt 图可以接受的格式
    const seriesData = courseData.value.map((course, index) => {
        let startTime = course.value[0];  // original start time
        if (courseTimeRangeStart.value && startTime < courseTimeRangeStart.value.toDate().getTime()) {
            startTime = courseTimeRangeStart.value.toDate().getTime();  // use courseTimeRangeStart if the course starts earlier
        }
        return {
            name: course.name,
            value: [
                index,  // yAxis index
                startTime,  // start time
                course.value[1]  // end time
            ]
        }
    });

    // 设置图表的选项
    return {
        tooltip: {
            formatter: function (params: any) {
                return params.marker + params.name + ': ' + dayjs(params.value[1]).format('YYYY-MM-DD') + ' ~ ' + dayjs(params.value[2]).format('YYYY-MM-DD');
            }
        },
        xAxis: {
            type: 'time',
            min: courseTimeRangeStart.value?.toDate().getTime(),
            max: courseTimeRangeEnd.value?.toDate().getTime(),
            axisLabel: {
                formatter: function (value: any) {
                    // value is a timestamp in milliseconds
                    return dayjs(value).format('YYYY/MM/DD');
                },
                splitNumber: 4
            },
        },
        yAxis: {
            type: 'category',
            data: courseData.value.map(course => course.name)
        },
        series: [{
            type: 'custom',
            renderItem: (_: any, api: any) => {
                const categoryIndex = api.value(0);
                const start = api.coord([api.value(1), categoryIndex]);
                const end = api.coord([api.value(2), categoryIndex]);
                const height = api.size([0, 1])[1] * 0.6;

                return {
                    type: 'rect',
                    shape: {
                        x: start[0],
                        y: start[1] - height / 2,
                        width: end[0] - start[0],
                        height: height
                    },
                    style: api.style()
                };
            },
            encode: {
                x: [1, 2],
                y: 0
            },
            data: seriesData
        }]
    };
});
</script>