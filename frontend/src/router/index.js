import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/Home.vue'),
      meta: { keepAlive: true }
    },
    {
      path: '/menu',
      name: 'menu',
      component: () => import('@/views/Menu.vue'),
      meta: { keepAlive: true }
    },
    {
      path: '/cart',
      name: 'cart',
      component: () => import('@/views/Cart.vue')
    },
    {
      path: '/order',
      name: 'order',
      component: () => import('@/views/Order.vue')
    },
    {
      path: '/order-confirm',
      name: 'order-confirm',
      component: () => import('@/views/OrderConfirm.vue')
    },
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/profile',
    name: 'profile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: true }
  },
    {
      path: '/order-detail/:id',
      name: 'order-detail',
      component: () => import('@/views/OrderDetail.vue')
    }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 检查是否需要登录
  if (to.meta.requiresAuth && !token) {
    // 需要登录但未登录，跳转到登录页
    next({
      path: '/login',
      query: { redirect: to.fullPath }
    })
  } else {
    next()
  }
})

export default router