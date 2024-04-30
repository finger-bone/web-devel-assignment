<template>
  <div class="w-full h-full grid grid-cols-2">
    <div class="p-2 m-2">
      <v-chart :option="genderOption" />
    </div>
    <div class="p-2 m-2">
      <v-chart :option="educationOption" />
    </div>
  </div>
</template>

<script lang="ts" setup>
import axios from 'axios'
import responseToDataOptions from '@/utils/statistics'
import { onMounted, ref } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent } from 'echarts/components'

use([CanvasRenderer, PieChart, TitleComponent, TooltipComponent, LegendComponent])

const educationOption = ref<any>({
  title: {
    text: '学生最高学历统计',
    left: 'center',
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)',
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    data: [],
  },
  series: [
    {
      name: '最高学历',
      type: 'pie',
      radius: ['30%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2,
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)',
        },
      },
      data: [],
    },
  ],
})
const genderOption = ref<any>({
  title: {
    text: '学生性别统计',
    left: 'center',
  },
  tooltip: {
    trigger: 'item',
    formatter: '{a} <br/>{b} : {c} ({d}%)',
  },
  legend: {
    orient: 'vertical',
    left: 'left',
    data: [],
  },
  series: [
    {
      name: '性别',
      type: 'pie',
      radius: ['30%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2,
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: 'rgba(0, 0, 0, 0.5)',
        },
      },
      data: [],
    },
  ],
})

onMounted(async () => {
  const educationResponse = await axios.get('/api/secure/student/statistics/education', {})
  const educationData = responseToDataOptions(educationResponse)
  educationOption.value.legend.data = educationData.map((item) => item.name)
  educationOption.value.series[0].data = educationData
  const genderResponse = await axios.get('/api/secure/student/statistics/gender', {})
  const genderData = responseToDataOptions(genderResponse)
  genderOption.value.legend.data = genderData.map((item) => item.name)
  genderOption.value.series[0].data = genderData
})
</script>
