<template>
  <div class="w-full h-full grid grid-cols-2">
    <div>
      <v-chart :option="genderOption"/>
    </div>
    <div>
      <v-chart :option="positionOption"/>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { router } from '@/router';
import { useUserStore } from '@/store/modules/user';
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { use } from "echarts/core";
import { CanvasRenderer } from "echarts/renderers";
import { PieChart } from "echarts/charts";
import responseToDataOptions from '@/utils/statistics';
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent
} from "echarts/components";

use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent
]);

const token = useUserStore().token
if (!token || token.length === 0) {
  router.push('/login')
}


const genderOption = ref<any>({
  title: {
    text: "员工性别统计",
    left: "center"
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: "vertical",
    left: "left",
    data: []
  },
  series: [
    {
      name: "性别",
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)"
        }
      },
    },
  ]
});

const positionOption = ref<any>({
  title: {
    text: "员工职位统计",
    left: "center"
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: "vertical",
    left: "left",
    data: []
  },
  series: [
    {
      name: "职位",
      type: 'pie',
      radius: ['40%', '70%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)"
        }
      },
      data: []
    },
  ]
});

onMounted(async () => {
  const genderResponse = await axios.get("/api/secure/employee/statistics/gender", {
    headers: {
      Authorization: token
    }
  });
  const genderData = responseToDataOptions(genderResponse);
  genderOption.value.legend.data = genderData.map(item => item.name);
  genderOption.value.series[0].data = genderData;
  const positionResponse = await axios.get("/api/secure/employee/statistics/position", {
    headers: {
      Authorization: token
    }
  });
  const positionData = responseToDataOptions(positionResponse);
  positionOption.value.legend.data = positionData.map(item => item.name);
  positionOption.value.series[0].data = positionData;
})

</script>