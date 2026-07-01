<template>
  <el-container class="shell">
    <el-aside width="232px" class="aside">
      <div class="logo"><b>律</b><div><strong>律约</strong><small>LEGAL SERVICE</small></div></div>
      <div class="role-label">工作台 · {{ roleName }}</div>
      <el-menu router :default-active="$route.path" class="menu">
        <el-menu-item v-for="item in menu" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon><span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
      <div class="aside-foot"><span>服务状态</span><b><i /> 系统运行中</b></div>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div><span class="breadcrumb">法律服务咨询预约系统</span><b>/</b><span>{{ currentTitle }}</span></div>
        <el-dropdown>
          <div class="user"><div class="avatar">{{ realName[0] }}</div><span><b>{{ realName }}</b><small>{{ roleName }}</small></span><el-icon><ArrowDown /></el-icon></div>
          <template #dropdown><el-dropdown-menu><el-dropdown-item @click="logout">退出登录</el-dropdown-item></el-dropdown-menu></template>
        </el-dropdown>
      </el-header>
      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
const route = useRoute(), router = useRouter()
const role = localStorage.getItem('role')
const realName = localStorage.getItem('realName') || '用户'
const roleName = { admin: '平台管理员', lawyer: '执业律师', customer: '咨询客户' }[role]
const all = [
  { path: '/dashboard', label: '数据概览', icon: 'DataLine', roles: ['admin'] },
  { path: '/categories', label: '咨询类别', icon: 'Collection', roles: ['admin'] },
  { path: '/lawyers', label: role === 'customer' ? '律师名录' : '律师管理', icon: 'UserFilled', roles: ['admin','customer'] },
  { path: '/appointments', label: role === 'customer' ? '我的预约' : '预约管理', icon: 'Calendar', roles: ['admin','lawyer','customer'] },
  { path: '/schedules', label: '空闲时间', icon: 'Clock', roles: ['admin','lawyer'] },
  { path: '/orders', label: role === 'customer' ? '我的账单' : '收费订单', icon: 'Tickets', roles: ['admin','lawyer','customer'] },
  { path: '/profile-apply', label: role === 'lawyer' ? '资料修改' : '资料审核', icon: 'EditPen', roles: ['admin','lawyer'] }
]
const menu = all.filter(x => x.roles.includes(role))
const currentTitle = computed(() => all.find(x => x.path === route.path)?.label || '工作台')
function logout() { localStorage.clear(); router.push('/login') }
</script>

<style scoped>
.shell { min-height: 100vh; }
.aside { position: fixed; inset: 0 auto 0 0; z-index: 2; display: flex; flex-direction: column; background: var(--forest); color: white; }
.shell > .el-container { margin-left: 232px; }
.logo { display: flex; gap: 13px; align-items: center; height: 86px; padding: 0 24px; border-bottom: 1px solid rgba(255,255,255,.08); }
.logo > b { width: 40px; height: 40px; display: grid; place-items: center; border: 1px solid rgba(255,255,255,.45); font: 22px Georgia; }
.logo strong,.logo small { display: block; }.logo strong { font: 21px Georgia,"STSong",serif; }.logo small { margin-top: 3px; color: #8ca49e; font-size: 8px; letter-spacing: .15em; }
.role-label { padding: 24px 25px 9px; color: #728d86; font-size: 10px; letter-spacing: .12em; }
.menu { flex: 1; border: 0; background: transparent; --el-menu-text-color: #a9bbb6; --el-menu-hover-bg-color: rgba(255,255,255,.055); --el-menu-active-color: white; }
.menu .el-menu-item { margin: 4px 12px; border-radius: 8px; }.menu .el-menu-item.is-active { background: rgba(197,154,84,.17); box-shadow: inset 3px 0 var(--gold); }
.aside-foot { margin: 18px; padding: 14px; background: rgba(255,255,255,.04); border-radius: 8px; font-size: 10px; color: #789089; }
.aside-foot b { display: block; margin-top: 7px; color: #bdd0cb; font-size: 11px; }.aside-foot i { display:inline-block;width:6px;height:6px;border-radius:50%;background:#65bf92;margin-right:5px; }
.header { position: sticky; top: 0; z-index: 1; height: 70px; display:flex; align-items:center; justify-content:space-between; padding:0 32px; background:rgba(255,254,250,.93); border-bottom:1px solid #e7ece9; backdrop-filter:blur(10px); }
.header > div { display:flex; gap:10px; color:#94a09c; font-size:12px; }.header .breadcrumb { color:var(--forest); font-weight:600; }
.user { display:flex;align-items:center;gap:10px;cursor:pointer; }.avatar { width:36px;height:36px;display:grid;place-items:center;border-radius:50%;background:var(--mint);color:var(--forest);font-weight:700; }
.user span b,.user span small{display:block}.user span b{font-size:12px;color:#344a44}.user span small{font-size:10px;color:#9aa4a1;margin-top:2px}
.main { padding: 28px 32px 42px; overflow: visible; }
</style>
