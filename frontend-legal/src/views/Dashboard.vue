<template>
  <div class="page-head"><div><h1>业务数据概览</h1><p>有效收费订单与预约服务的实时汇总</p></div><el-date-picker v-model="dates" type="daterange" value-format="YYYY-MM-DD" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" @change="load" /></div>
  <div class="metrics">
    <div v-for="item in metrics" :key="item.label" class="metric panel"><div :class="item.tone"><el-icon><component :is="item.icon" /></el-icon></div><span><small>{{ item.label }}</small><b>{{ item.value }}</b><em>{{ item.note }}</em></span></div>
  </div>
  <div class="charts">
    <div class="panel chart-card"><h3>律师服务表现</h3><div ref="barEl" class="chart" /></div>
    <div class="panel chart-card"><h3>咨询类别分布</h3><div ref="pieEl" class="chart" /></div>
    <div class="panel chart-card wide"><h3>收入趋势</h3><div ref="lineEl" class="chart small" /></div>
  </div>
</template>
<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { statsApi } from '../api'
const dates=ref([]), data=ref({summary:{},lawyers:[],categories:[],trend:[]})
const barEl=ref(),pieEl=ref(),lineEl=ref(); let charts=[]
const metrics=computed(()=>[
  {label:'累计有效收入',value:`¥ ${Number(data.value.summary?.totalIncome||0).toLocaleString()}`,note:'审核通过及已收费',icon:'Wallet',tone:'gold'},
  {label:'预约总量',value:data.value.summary?.appointmentCount||0,note:`今日新增 ${data.value.summary?.todayAppointmentCount||0}`,icon:'Calendar',tone:'green'},
  {label:'订单总量',value:data.value.summary?.orderCount||0,note:`本月 ${data.value.summary?.monthOrderCount||0} 单`,icon:'Tickets',tone:'blue'}
])
async function load(){const r=await statsApi.all({startDate:dates.value?.[0],endDate:dates.value?.[1]});data.value=r.data;await nextTick();render()}
function render(){charts.forEach(c=>c.dispose());charts=[
  echarts.init(barEl.value),echarts.init(pieEl.value),echarts.init(lineEl.value)]
  charts[0].setOption({grid:{left:40,right:20,bottom:35,top:35},tooltip:{},xAxis:{type:'category',data:data.value.lawyers.map(x=>x.lawyerName),axisLine:{lineStyle:{color:'#dce5e1'}}},yAxis:{type:'value',splitLine:{lineStyle:{color:'#edf1ef'}}},series:[{name:'预约量',type:'bar',barWidth:24,data:data.value.lawyers.map(x=>x.appointmentCount),itemStyle:{color:'#1d6f5f',borderRadius:[5,5,0,0]}}]})
  charts[1].setOption({tooltip:{trigger:'item'},legend:{bottom:0},series:[{type:'pie',radius:['44%','70%'],center:['50%','44%'],itemStyle:{borderColor:'#fff',borderWidth:3},data:data.value.categories.map(x=>({name:x.categoryName,value:x.appointmentCount}))}]})
  charts[2].setOption({grid:{left:55,right:25,bottom:30,top:25},tooltip:{trigger:'axis'},xAxis:{type:'category',data:data.value.trend.map(x=>x.date)},yAxis:{type:'value',splitLine:{lineStyle:{color:'#edf1ef'}}},series:[{type:'line',smooth:true,symbolSize:7,data:data.value.trend.map(x=>x.amount),lineStyle:{color:'#c59a54',width:3},itemStyle:{color:'#c59a54'},areaStyle:{color:{type:'linear',x:0,y:0,x2:0,y2:1,colorStops:[{offset:0,color:'rgba(197,154,84,.25)'},{offset:1,color:'rgba(197,154,84,.01)'}]}}}]})
}
onMounted(()=>{load();addEventListener('resize',()=>charts.forEach(c=>c.resize()))});onBeforeUnmount(()=>charts.forEach(c=>c.dispose()))
</script>
<style scoped>
.metrics{display:grid;grid-template-columns:repeat(3,1fr);gap:18px;margin-bottom:18px}.metric{display:flex;align-items:center;gap:16px;padding:22px}.metric>div{width:48px;height:48px;display:grid;place-items:center;border-radius:12px;font-size:20px}.gold{background:#f5ead7;color:#a47529}.green{background:#dfeee8;color:#1d6f5f}.blue{background:#e0e9ef;color:#476b7b}.metric span{display:flex;flex-direction:column}.metric small{color:#899692}.metric b{font:600 27px Georgia;margin:5px 0;color:var(--forest)}.metric em{font-style:normal;font-size:10px;color:#9ba5a1}.charts{display:grid;grid-template-columns:1.15fr .85fr;gap:18px}.chart-card{padding:20px}.chart-card h3{margin:0 0 4px;font:600 15px Georgia;color:var(--forest)}.chart{height:300px}.wide{grid-column:1/-1}.chart.small{height:230px}
</style>
