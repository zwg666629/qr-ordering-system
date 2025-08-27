<template>
  <div class="user-management">
    <!-- 页面标题和统计卡片 -->
    <div class="page-header">
      <h2>用户管理</h2>
      <div class="stats-cards">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.totalUsers || 0 }}</div>
            <div class="stats-label">总用户数</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.activeUsers || 0 }}</div>
            <div class="stats-label">正常用户</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.inactiveUsers || 0 }}</div>
            <div class="stats-label">禁用用户</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.todayNewUsers || 0 }}</div>
            <div class="stats-label">今日新增</div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <el-card class="search-card">
      <div class="search-form">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索用户手机号或昵称"
          style="width: 300px;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="searchForm.status" placeholder="用户状态" clearable style="width: 120px;">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch" :loading="loading">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">重置</el-button>
        <el-button @click="loadStatistics" :loading="statsLoading">
          <el-icon><Refresh /></el-icon>
          刷新统计
        </el-button>
      </div>
    </el-card>

    <!-- 用户列表 -->
    <el-card class="table-card">
      <el-table
        :data="userList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="nickname" label="昵称" width="120">
          <template #default="{ row }">
            {{ row.nickname || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column prop="avatar" label="头像" width="80" align="center">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              <el-icon><User /></el-icon>
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="gender" label="性别" width="60" align="center">
          <template #default="{ row }">
            <span v-if="row.gender === 1">男</span>
            <span v-else-if="row.gender === 2">女</span>
            <span v-else>未设置</span>
          </template>
        </el-table-column>
        <el-table-column prop="birthday" label="生日" width="120" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160" />
        <el-table-column prop="createTime" label="注册时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewUserDetail(row)">
              查看详情
            </el-button>
            <el-button 
              :type="row.status === 1 ? 'danger' : 'success'"
              size="small"
              @click="toggleUserStatus(row)"
              :loading="row.loading"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>

    <!-- 用户详情弹框 -->
    <el-dialog v-model="detailVisible" title="用户详情" width="600px">
      <div v-if="currentUser" class="user-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="用户ID">{{ currentUser.id }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentUser.phone }}</el-descriptions-item>
          <el-descriptions-item label="昵称">{{ currentUser.nickname || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="性别">
            <span v-if="currentUser.gender === 1">男</span>
            <span v-else-if="currentUser.gender === 2">女</span>
            <span v-else>未设置</span>
          </el-descriptions-item>
          <el-descriptions-item label="生日">{{ currentUser.birthday || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentUser.status === 1 ? 'success' : 'danger'">
              {{ currentUser.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后登录时间">{{ currentUser.lastLoginTime || '从未登录' }}</el-descriptions-item>
          <el-descriptions-item label="最后登录IP">{{ currentUser.lastLoginIp || '无' }}</el-descriptions-item>
          <el-descriptions-item label="注册时间">{{ currentUser.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentUser.updateTime }}</el-descriptions-item>
        </el-descriptions>
        <div v-if="currentUser.avatar" class="avatar-section">
          <div class="avatar-label">头像：</div>
          <el-image :src="currentUser.avatar" style="width: 100px; height: 100px;" fit="cover" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, User, Refresh } from '@element-plus/icons-vue'
import { userManagementApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  status: null
})

// 用户列表
const userList = ref([])
const loading = ref(false)
const statsLoading = ref(false)

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 统计信息
const statistics = ref({})

// 用户详情
const detailVisible = ref(false)
const currentUser = ref(null)

// 加载用户列表
const loadUserList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      status: searchForm.status
    }
    
    const response = await userManagementApi.getUserList(params)
    userList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载用户列表失败:', error)
    ElMessage.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    statsLoading.value = true
    const response = await userManagementApi.getUserStatistics()
    statistics.value = response
  } catch (error) {
    console.error('加载统计信息失败:', error)
    ElMessage.error('加载统计信息失败')
  } finally {
    statsLoading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.page = 1
  loadUserList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.status = null
  handleSearch()
}

// 分页大小变化
const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  loadUserList()
}

// 页码变化
const handlePageChange = (newPage) => {
  pagination.page = newPage
  loadUserList()
}

// 切换用户状态
const toggleUserStatus = async (user) => {
  try {
    const action = user.status === 1 ? '禁用' : '启用'
    await ElMessageBox.confirm(
      `确认要${action}用户 "${user.nickname || user.phone}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    user.loading = true
    const newStatus = user.status === 1 ? 0 : 1
    await userManagementApi.updateUserStatus(user.id, newStatus)
    
    user.status = newStatus
    ElMessage.success(`${action}成功`)
    
    // 刷新统计信息
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换用户状态失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    user.loading = false
  }
}

// 查看用户详情
const viewUserDetail = async (user) => {
  try {
    const response = await userManagementApi.getUserDetail(user.id)
    currentUser.value = response
    detailVisible.value = true
  } catch (error) {
    console.error('获取用户详情失败:', error)
    ElMessage.error('获取用户详情失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadUserList()
  loadStatistics()
})
</script>

<style scoped>
.user-management {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0 0 20px 0;
  color: #303133;
}

.stats-cards {
  display: flex;
  gap: 20px;
}

.stats-card {
  flex: 1;
}

.stats-content {
  text-align: center;
}

.stats-number {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  line-height: 1;
}

.stats-label {
  margin-top: 8px;
  color: #606266;
  font-size: 14px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  display: flex;
  gap: 16px;
  align-items: center;
}

.table-card {
  background: #fff;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.user-detail .avatar-section {
  margin-top: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-label {
  font-weight: bold;
  color: #303133;
}
</style>