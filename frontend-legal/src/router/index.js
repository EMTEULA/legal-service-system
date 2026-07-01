import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Layout from '../views/Layout.vue'
import Dashboard from '../views/Dashboard.vue'
import Category from '../views/Category.vue'
import Lawyer from '../views/Lawyer.vue'
import Appointment from '../views/Appointment.vue'
import Schedule from '../views/Schedule.vue'
import Orders from '../views/Orders.vue'
import ProfileApply from '../views/ProfileApply.vue'

const routes = [
  { path: '/login', component: Login },
  {
    path: '/', component: Layout, redirect: '/appointments',
    children: [
      { path: 'dashboard', component: Dashboard, meta: { roles: ['admin'] } },
      { path: 'categories', component: Category, meta: { roles: ['admin'] } },
      { path: 'lawyers', component: Lawyer, meta: { roles: ['admin', 'customer'] } },
      { path: 'appointments', component: Appointment, meta: { roles: ['admin', 'lawyer', 'customer'] } },
      { path: 'schedules', component: Schedule, meta: { roles: ['admin', 'lawyer'] } },
      { path: 'orders', component: Orders, meta: { roles: ['admin', 'lawyer', 'customer'] } },
      { path: 'profile-apply', component: ProfileApply, meta: { roles: ['admin', 'lawyer'] } }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })
router.beforeEach(to => {
  const role = localStorage.getItem('role')
  if (to.path !== '/login' && !localStorage.getItem('token')) return '/login'
  if (to.path === '/login' && localStorage.getItem('token')) return role === 'admin' ? '/dashboard' : '/appointments'
  if (to.meta.roles && !to.meta.roles.includes(role)) return '/appointments'
})
export default router
