<template>
  <div class="layout-container">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="isCollapse ? '64px' : '200px'" class="sidebar">
        <div class="logo">
          <span v-if="!isCollapse">扫码点餐管理系统</span>
          <span v-else>餐</span>
        </div>
        
        <!-- 收藏的菜单 -->
        <div v-if="favoriteMenus.length > 0 && !isCollapse" class="favorite-section">
          <div class="section-title">
            <el-icon><Star /></el-icon>
            <span>常用功能</span>
            <div class="section-tip" v-if="favoriteMenus.length > 1">拖拽排序</div>
          </div>
          <div ref="favoriteMenuRef" class="favorite-menu">
            <div 
              v-for="menu in favoriteMenus" 
              :key="menu.path"
              class="favorite-item"
              :class="{ 'is-active': activeMenu === menu.path }"
              :data-path="menu.path"
            >
              <el-icon class="drag-handle" title="拖拽排序">
                <Menu />
              </el-icon>
              <div class="favorite-item-content" @click="router.push(menu.path)">
                <el-icon>
                  <component :is="menu.icon" />
                </el-icon>
                <span>{{ menu.title }}</span>
              </div>
              <el-icon 
                class="favorite-remove-icon" 
                @click.stop="toggleFavorite(menu)"
                title="取消收藏"
              >
                <StarFilled />
              </el-icon>
            </div>
          </div>
        </div>

        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          router
          class="sidebar-menu"
        >
          <el-menu-item index="/dashboard" class="menu-item-with-star">
            <el-icon><DataAnalysis /></el-icon>
            <span>仪表盘</span>
            <el-icon 
              class="star-icon" 
              :class="{ 'favorited': isFavorite('/dashboard') }"
              @click.stop="toggleFavorite({ path: '/dashboard', title: '仪表盘', icon: 'DataAnalysis' })"
            >
              <StarFilled v-if="isFavorite('/dashboard')" />
              <Star v-else />
            </el-icon>
          </el-menu-item>
          
          <el-sub-menu index="dish-management">
            <template #title>
              <el-icon><Dish /></el-icon>
              <span>菜品管理</span>
            </template>
            <el-menu-item index="/dish" class="menu-item-with-star">
              菜品列表
              <el-icon 
                class="star-icon submenu-star" 
                :class="{ 'favorited': isFavorite('/dish') }"
                @click.stop="toggleFavorite({ path: '/dish', title: '菜品列表', icon: 'Dish' })"
              >
                <StarFilled v-if="isFavorite('/dish')" />
                <Star v-else />
              </el-icon>
            </el-menu-item>
            <el-menu-item index="/category" class="menu-item-with-star">
              分类管理
              <el-icon 
                class="star-icon submenu-star" 
                :class="{ 'favorited': isFavorite('/category') }"
                @click.stop="toggleFavorite({ path: '/category', title: '分类管理', icon: 'Grid' })"
              >
                <StarFilled v-if="isFavorite('/category')" />
                <Star v-else />
              </el-icon>
            </el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="/order" class="menu-item-with-star">
            <el-icon><List /></el-icon>
            <span>订单管理</span>
            <el-icon 
              class="star-icon" 
              :class="{ 'favorited': isFavorite('/order') }"
              @click.stop="toggleFavorite({ path: '/order', title: '订单管理', icon: 'List' })"
            >
              <StarFilled v-if="isFavorite('/order')" />
              <Star v-else />
            </el-icon>
          </el-menu-item>
          
          <el-menu-item index="/table" class="menu-item-with-star">
            <el-icon><Grid /></el-icon>
            <span>餐桌管理</span>
            <el-icon 
              class="star-icon" 
              :class="{ 'favorited': isFavorite('/table') }"
              @click.stop="toggleFavorite({ path: '/table', title: '餐桌管理', icon: 'Grid' })"
            >
              <StarFilled v-if="isFavorite('/table')" />
              <Star v-else />
            </el-icon>
          </el-menu-item>
          
          <el-menu-item index="/statistics" class="menu-item-with-star">
            <el-icon><TrendCharts /></el-icon>
            <span>数据统计</span>
            <el-icon 
              class="star-icon" 
              :class="{ 'favorited': isFavorite('/statistics') }"
              @click.stop="toggleFavorite({ path: '/statistics', title: '数据统计', icon: 'TrendCharts' })"
            >
              <StarFilled v-if="isFavorite('/statistics')" />
              <Star v-else />
            </el-icon>
          </el-menu-item>
          
          <el-menu-item index="/user" class="menu-item-with-star">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
            <el-icon 
              class="star-icon" 
              :class="{ 'favorited': isFavorite('/user') }"
              @click.stop="toggleFavorite({ path: '/user', title: '用户管理', icon: 'User' })"
            >
              <StarFilled v-if="isFavorite('/user')" />
              <Star v-else />
            </el-icon>
          </el-menu-item>
          
          <el-menu-item index="/file" class="menu-item-with-star">
            <el-icon><Folder /></el-icon>
            <span>文件管理</span>
            <el-icon 
              class="star-icon" 
              :class="{ 'favorited': isFavorite('/file') }"
              @click.stop="toggleFavorite({ path: '/file', title: '文件管理', icon: 'Folder' })"
            >
              <StarFilled v-if="isFavorite('/file')" />
              <Star v-else />
            </el-icon>
          </el-menu-item>
          
          <el-menu-item index="/admin" class="menu-item-with-star">
            <el-icon><UserFilled /></el-icon>
            <span>管理员管理</span>
            <el-icon 
              class="star-icon" 
              :class="{ 'favorited': isFavorite('/admin') }"
              @click.stop="toggleFavorite({ path: '/admin', title: '管理员管理', icon: 'UserFilled' })"
            >
              <StarFilled v-if="isFavorite('/admin')" />
              <Star v-else />
            </el-icon>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <!-- 主体区域 -->
      <el-container>
        <!-- 顶部导航 -->
        <el-header class="header">
          <div class="header-left">
            <el-icon 
              class="collapse-btn" 
              @click="isCollapse = !isCollapse"
            >
              <component :is="isCollapse ? 'Expand' : 'Fold'" />
            </el-icon>
            
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-dropdown>
              <span class="user-info">
                <el-icon><UserFilled /></el-icon>
                {{ userStore.userInfo?.realName || userStore.userInfo?.username || '管理员' }}
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleLogout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <!-- 主要内容 -->
        <el-main class="main-content">
          <router-view v-slot="{ Component }">
            <transition name="fade" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox, ElMessage } from 'element-plus'
import Sortable from 'sortablejs'
import { 
  House, 
  Bowl, 
  List, 
  ShoppingCart, 
  Expand, 
  Fold, 
  UserFilled, 
  SwitchButton,
  TrendCharts,
  DataAnalysis,
  Dish,
  Grid,
  User,
  Folder,
  Star,
  StarFilled,
  Menu
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)
const favoriteMenus = ref([])
const favoriteMenuRef = ref(null)
let sortableInstance = null

const activeMenu = computed(() => route.path)
const currentTitle = computed(() => route.meta.title)

// 本地存储键名
const FAVORITE_MENUS_KEY = 'admin_favorite_menus'

// 初始化收藏菜单
const initFavoriteMenus = () => {
  const stored = localStorage.getItem(FAVORITE_MENUS_KEY)
  if (stored) {
    try {
      favoriteMenus.value = JSON.parse(stored)
    } catch (error) {
      console.error('解析收藏菜单失败:', error)
      favoriteMenus.value = []
    }
  }
}

// 保存收藏菜单到本地存储
const saveFavoriteMenus = () => {
  localStorage.setItem(FAVORITE_MENUS_KEY, JSON.stringify(favoriteMenus.value))
}

// 检查是否已收藏
const isFavorite = (path) => {
  return favoriteMenus.value.some(menu => menu.path === path)
}

// 切换收藏状态
const toggleFavorite = (menuItem) => {
  const index = favoriteMenus.value.findIndex(menu => menu.path === menuItem.path)
  
  if (index > -1) {
    // 取消收藏
    favoriteMenus.value.splice(index, 1)
    ElMessage({
      message: `已取消收藏 "${menuItem.title}"`,
      type: 'info',
      duration: 2000,
      showClose: true
    })
  } else {
    // 添加到收藏
    favoriteMenus.value.push(menuItem)
    ElMessage({
      message: `已收藏 "${menuItem.title}" 到常用功能`,
      type: 'success',
      duration: 2000,
      showClose: true
    })
  }
  
  saveFavoriteMenus()
  // 重新初始化拖拽排序
  nextTick(() => {
    initSortable()
  })
}

// 初始化拖拽排序
const initSortable = () => {
  if (!favoriteMenuRef.value || favoriteMenus.value.length <= 1) return
  
  // 销毁之前的实例
  if (sortableInstance) {
    sortableInstance.destroy()
  }
  
  sortableInstance = Sortable.create(favoriteMenuRef.value, {
    handle: '.drag-handle',
    animation: 150,
    ghostClass: 'sortable-ghost',
    chosenClass: 'sortable-chosen',
    dragClass: 'sortable-drag',
    onEnd: (evt) => {
      const { oldIndex, newIndex } = evt
      if (oldIndex !== newIndex) {
        // 更新数组顺序
        const item = favoriteMenus.value.splice(oldIndex, 1)[0]
        favoriteMenus.value.splice(newIndex, 0, item)
        
        // 保存到本地存储
        saveFavoriteMenus()
        
        ElMessage({
          message: '排序已更新',
          type: 'success',
          duration: 1500
        })
      }
    }
  })
}

const handleLogout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  })
}

onMounted(() => {
  initFavoriteMenus()
  nextTick(() => {
    initSortable()
  })
})

// 监听收藏菜单变化，重新初始化拖拽
watch(() => favoriteMenus.value.length, () => {
  nextTick(() => {
    initSortable()
  })
}, { flush: 'post' })
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.el-container {
  height: 100%;
}

/* 侧边栏 */
.sidebar {
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 18px;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo img {
  width: 32px;
  height: 32px;
  margin-right: 10px;
}

.sidebar-menu {
  border-right: none;
  background-color: #304156;
}

.sidebar-menu :deep(.el-menu-item),
.sidebar-menu :deep(.el-sub-menu__title) {
  color: #bfcbd9;
}

.sidebar-menu :deep(.el-menu-item:hover),
.sidebar-menu :deep(.el-sub-menu__title:hover) {
  background-color: #263445 !important;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: white;
}

/* 收藏区域样式 */
.favorite-section {
  padding: 15px 10px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.section-title {
  display: flex;
  align-items: center;
  color: #bfcbd9;
  font-size: 12px;
  margin-bottom: 10px;
  padding: 0 10px;
}

.section-title .el-icon {
  margin-right: 6px;
  font-size: 14px;
}

.section-tip {
  margin-left: auto;
  color: #999;
  font-size: 10px;
  background: rgba(255, 255, 255, 0.1);
  padding: 2px 6px;
  border-radius: 8px;
}

.favorite-menu {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.favorite-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  color: #bfcbd9;
  border-radius: 4px;
  transition: all 0.3s;
  font-size: 13px;
}

.favorite-item:hover {
  background-color: #263445;
  color: white;
}

.favorite-item.is-active {
  background-color: #409eff;
  color: white;
}

.favorite-item-content {
  display: flex;
  align-items: center;
  flex: 1;
  cursor: pointer;
}

.favorite-item-content .el-icon {
  margin-right: 8px;
  font-size: 16px;
}

.favorite-remove-icon {
  color: #f39c12;
  cursor: pointer;
  font-size: 12px;
  opacity: 0.8;
  transition: all 0.3s;
}

.favorite-remove-icon:hover {
  opacity: 1;
  transform: scale(1.1);
  color: #e67e22;
}

/* 拖拽手柄样式 */
.drag-handle {
  cursor: grab;
  color: #666;
  font-size: 14px;
  margin-right: 6px;
  opacity: 0.6;
  transition: all 0.3s;
  flex-shrink: 0;
}

.drag-handle:hover {
  opacity: 1;
  color: #409eff;
}

.drag-handle:active {
  cursor: grabbing;
}

/* 拖拽状态样式 */
.sortable-ghost {
  opacity: 0.5;
  background-color: #409eff !important;
  color: white !important;
}

.sortable-chosen {
  transform: scale(1.02);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.sortable-drag {
  transform: rotate(3deg);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
}

/* 菜单项星标样式 */
.menu-item-with-star {
  position: relative;
}

.star-icon {
  position: absolute;
  right: 15px;
  font-size: 14px;
  color: #666;
  opacity: 0;
  transition: all 0.3s;
  cursor: pointer;
}

.submenu-star {
  right: 10px;
}

.menu-item-with-star:hover .star-icon {
  opacity: 1;
}

.star-icon:hover {
  color: #f39c12;
  transform: scale(1.1);
}

.star-icon.favorited {
  opacity: 1;
  color: #f39c12;
}

/* 调整菜单项的padding来为星标留出空间 */
.sidebar-menu :deep(.el-menu-item) {
  padding-right: 40px;
}

.sidebar-menu :deep(.el-sub-menu .el-menu-item) {
  padding-right: 35px;
}

/* 顶部导航 */
.header {
  background-color: white;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 20px;
  transition: color 0.3s;
}

.collapse-btn:hover {
  color: #409eff;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #606266;
}

.user-info .el-icon {
  margin-right: 5px;
}

/* 主要内容 */
.main-content {
  background-color: #f0f2f5;
  padding: 20px;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>

