<template>
  <div class="login-shell">
    <section class="brand">
      <div class="brand-mark">律</div>
      <div class="brand-copy">
        <span>LEGAL SERVICE</span>
        <h1>让每一次咨询，<br>都有清晰的下一步。</h1>
        <p>连接客户与专业律师，从预约、审核到咨询结算，全流程可信管理。</p>
      </div>
      <div class="quote">专业不是制造距离，<br>而是让复杂的问题变得可以行动。</div>
    </section>
    <section class="login-card">
      <div class="card-title">
        <span>{{ registerMode ? 'CREATE ACCOUNT' : 'WELCOME BACK' }}</span>
        <h2>{{ registerMode ? '客户注册' : '登录律约' }}</h2>
        <p>{{ registerMode ? '注册后即可预约合适的专业律师' : '请使用系统账号继续' }}</p>
      </div>
      <el-form :model="form" label-position="top" @keyup.enter="submit">
        <el-form-item v-if="registerMode" label="姓名">
          <el-input v-model="form.name" placeholder="请输入真实姓名" size="large" />
        </el-form-item>
        <el-form-item v-if="registerMode" label="联系电话">
          <el-input v-model="form.phone" placeholder="请输入联系电话" size="large" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large" prefix-icon="User" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" size="large" prefix-icon="Lock" />
        </el-form-item>
        <el-button class="submit" type="primary" size="large" :loading="loading" @click="submit">
          {{ registerMode ? '注册账号' : '进入系统' }}
        </el-button>
      </el-form>
      <button class="switch" @click="registerMode = !registerMode">
        {{ registerMode ? '已有账号？返回登录' : '没有账号？注册客户账号' }}
      </button>
      <div v-if="!registerMode" class="demo">演示：admin / zhanglawyer / wangming　密码均为 123456</div>
    </section>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { authApi } from '../api'

const router = useRouter()
const loading = ref(false)
const registerMode = ref(false)
const form = reactive({ name: '', phone: '', username: 'admin', password: '123456' })

async function submit() {
  if (!form.username || !form.password) return ElMessage.warning('请输入用户名和密码')
  loading.value = true
  try {
    if (registerMode.value) {
      if (!form.name || !form.phone) return ElMessage.warning('请填写姓名和联系电话')
      await authApi.register(form)
      ElMessage.success('注册成功，请登录')
      registerMode.value = false
    } else {
      const { data } = await authApi.login(form)
      Object.entries(data).forEach(([key, value]) => localStorage.setItem(key, value))
      router.push(data.role === 'admin' ? '/dashboard' : '/appointments')
    }
  } finally { loading.value = false }
}
</script>

<style scoped>
.login-shell { min-height: 100vh; display: grid; grid-template-columns: minmax(0, 1.25fr) minmax(500px, .75fr); background: var(--paper); }
.brand { min-width: 0; position: relative; overflow: hidden; display: flex; flex-direction: column; justify-content: space-between; padding: 54px 58px; color: white; background: var(--forest); }
.brand::before { content: ""; position: absolute; inset: -10%; background: radial-gradient(circle at 72% 28%, rgba(197,154,84,.2), transparent 22%), repeating-linear-gradient(120deg, transparent 0 60px, rgba(255,255,255,.018) 61px 62px); }
.brand-mark { position: relative; width: 52px; height: 52px; display: grid; place-items: center; border: 1px solid rgba(255,255,255,.4); font: 26px Georgia, serif; }
.brand-copy { position: relative; max-width: 650px; }
.brand-copy span, .card-title span { color: var(--gold); font-size: 11px; letter-spacing: .28em; font-weight: 700; }
.brand-copy h1 { font: 500 48px/1.32 Georgia, "STSong", serif; letter-spacing: .03em; margin: 20px 0 26px; }
.brand-copy p { max-width: 520px; line-height: 1.9; color: #c7d5d1; }
.quote { position: relative; color: #94aaa4; border-left: 2px solid var(--gold); padding-left: 18px; font: 15px/1.8 Georgia, serif; }
.login-card { min-width: 0; width: min(430px, calc(100% - 48px)); margin: auto; padding: 34px; }
.card-title { margin-bottom: 30px; }
.card-title h2 { font: 500 34px Georgia, "STSong", serif; color: var(--forest); margin: 12px 0 8px; }
.card-title p { color: #87928f; font-size: 13px; }
.submit { width: 100%; height: 48px; margin-top: 8px; letter-spacing: .1em; }
.switch { width: 100%; margin: 22px 0 0; border: 0; background: transparent; color: var(--jade); cursor: pointer; }
.demo { margin-top: 30px; padding: 12px; font-size: 11px; text-align: center; color: #8c9894; background: #f3f6f4; border-radius: 8px; }
</style>
