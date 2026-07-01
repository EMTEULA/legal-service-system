<template>
  <div class="page-head"><div><h1>咨询类别</h1><p>维护法律服务范围与默认小时收费</p></div><el-button type="primary" icon="Plus" @click="open()">新增类别</el-button></div>
  <div class="panel"><div class="toolbar"><el-input v-model="query.name" placeholder="搜索类别名称" clearable style="width:240px" @keyup.enter="load" /><el-select v-model="query.status" placeholder="全部状态" clearable style="width:130px"><el-option label="启用" :value="1"/><el-option label="停用" :value="0"/></el-select><el-button icon="Search" @click="load">查询</el-button></div>
    <div class="table-wrap"><el-table :data="rows"><el-table-column prop="name" label="类别名称" width="160"/><el-table-column prop="description" label="服务说明" min-width="300"/><el-table-column label="参考收费" width="140"><template #default="{row}"><span class="money">¥ {{ row.basePrice }}/小时</span></template></el-table-column><el-table-column label="状态" width="100"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'" effect="plain">{{row.status===1?'启用':'停用'}}</el-tag></template></el-table-column><el-table-column label="操作" width="150"><template #default="{row}"><el-button link type="primary" @click="open(row)">编辑</el-button><el-button link type="danger" @click="remove(row)">删除</el-button></template></el-table-column></el-table>
      <div class="pagination"><el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" layout="total, prev, pager, next" :total="total" @current-change="load"/></div></div>
  </div>
  <el-dialog v-model="visible" :title="form.id?'编辑咨询类别':'新增咨询类别'" width="520px"><el-form :model="form" label-width="90px"><el-form-item label="类别名称"><el-input v-model="form.name"/></el-form-item><el-form-item label="服务说明"><el-input v-model="form.description" type="textarea" :rows="3"/></el-form-item><el-form-item label="小时收费"><el-input-number v-model="form.basePrice" :min="0" :step="50"/><span class="muted">　元/小时</span></el-form-item><el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :value="1">启用</el-radio><el-radio :value="0">停用</el-radio></el-radio-group></el-form-item></el-form><template #footer><el-button @click="visible=false">取消</el-button><el-button type="primary" @click="save">保存</el-button></template></el-dialog>
</template>
<script setup>
import { onMounted, reactive, ref } from 'vue';import { ElMessage,ElMessageBox } from 'element-plus';import { categoryApi } from '../api'
const rows=ref([]),total=ref(0),visible=ref(false),query=reactive({pageNum:1,pageSize:10,name:'',status:null}),form=reactive({})
async function load(){const r=await categoryApi.list(query);rows.value=r.data.list;total.value=r.data.total}
function open(row={}){Object.assign(form,{id:null,name:'',description:'',basePrice:0,status:1},row);visible.value=true}
async function save(){if(!form.name)return ElMessage.warning('请输入类别名称');await (form.id?categoryApi.update(form):categoryApi.add(form));ElMessage.success('保存成功');visible.value=false;load()}
async function remove(row){await ElMessageBox.confirm(`确定删除“${row.name}”？`,'删除确认',{type:'warning'});await categoryApi.remove(row.id);ElMessage.success('已删除');load()}onMounted(load)
</script>
