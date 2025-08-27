import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/views/Layout.vue'),
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: 'dashboard',
          name: 'dashboard',
          component: () => import('@/views/Dashboard.vue'),
          meta: { title: '仪表盘' }
        },
        {
          path: 'dish',
          name: 'dish',
          component: () => import('@/views/dish/DishList.vue'),
          meta: { title: '菜品管理' }
        },
        {
          path: 'category',
          name: 'category',
          component: () => import('@/views/dish/CategoryList.vue'),
          meta: { title: '分类管理' }
        },
        {
          path: 'order',
          name: 'order',
          component: () => import('@/views/order/OrderList.vue'),
          meta: { title: '订单管理' }
        },
        {
          path: 'order/:id',
          name: 'orderDetail',
          component: () => import('@/views/order/OrderDetail.vue'),
          meta: { title: '订单详情' }
        },
        {
          path: 'table',
          name: 'table',
          component: () => import('@/views/table/TableList.vue'),
          meta: { title: '餐桌管理' }
        },
        {
          path: 'statistics',
          name: 'statistics',
          component: () => import('@/views/Statistics.vue'),
          meta: { title: '数据统计' }
        },
        {
          path: 'user',
          name: 'user',
          component: () => import('@/views/user/UserList.vue'),
          meta: { title: '用户管理' }
        },
        {
          path: 'file',
          name: 'file',
          component: () => import('@/views/file/FileList.vue'),
          meta: { title: '文件管理' }
        },
        {
          path: 'admin',
          name: 'admin',
          component: () => import('@/views/admin/AdminList.vue'),
          meta: { title: '管理员管理' }
        }
      ]
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  
  if (to.meta.requiresAuth !== false && !userStore.isLoggedIn) {
    next('/login')
  } else if (to.path === '/login' && userStore.isLoggedIn) {
    next('/')
  } else {
    next()
  }
})

export default router














