<template>
  <div class="admin-management">
    <!-- 页面标题和统计卡片 -->
    <div class="page-header">
      <h2>管理员管理</h2>
      <div class="stats-cards">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.totalAdmins || 0 }}</div>
            <div class="stats-label">总管理员</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.activeAdmins || 0 }}</div>
            <div class="stats-label">正常状态</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.superAdmins || 0 }}</div>
            <div class="stats-label">超级管理员</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.normalAdmins || 0 }}</div>
            <div class="stats-label">普通管理员</div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <div class="search-form">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索用户名、姓名、手机号或邮箱"
          style="width: 300px;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="searchForm.role" placeholder="角色" clearable style="width: 120px;">
          <el-option label="超级管理员" :value="1" />
          <el-option label="普通管理员" :value="2" />
        </el-select>

        <el-select v-model="searchForm.status" placeholder="状态" clearable style="width: 120px;">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch" :loading="loading">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">重置</el-button>
        
        <div class="right-actions">
          <el-button type="primary" @click="showCreateDialog" :icon="Plus">
            新增管理员
          </el-button>
          <el-button @click="loadStatistics" :loading="statsLoading" :icon="Refresh">
            刷新统计
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 管理员列表 -->
    <el-card class="table-card">
      <el-table
        :data="adminList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120">
          <template #default="{ row }">
            {{ row.realName || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" width="130">
          <template #default="{ row }">
            {{ row.phone || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column prop="email" label="邮箱" width="180" show-overflow-tooltip>
          <template #default="{ row }">
            {{ row.email || '未设置' }}
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="row.role === 1 ? 'danger' : 'primary'">
              {{ row.role === 1 ? '超级管理员' : '普通管理员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginTime" label="最后登录" width="160">
          <template #default="{ row }">
            {{ row.lastLoginTime || '从未登录' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewAdminDetail(row)">
              详情
            </el-button>
            <el-button type="warning" size="small" @click="showEditDialog(row)">
              编辑
            </el-button>
            <el-button 
              :type="row.status === 1 ? 'danger' : 'success'"
              size="small"
              @click="toggleAdminStatus(row)"
              :loading="row.statusLoading"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button 
              type="info" 
              size="small" 
              @click="resetPassword(row)"
              :loading="row.passwordLoading"
            >
              重置密码
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

    <!-- 新增/编辑管理员对话框 -->
    <el-dialog 
      v-model="formVisible" 
      :title="isEdit ? '编辑管理员' : '新增管理员'" 
      width="500px"
    >
      <el-form 
        ref="formRef" 
        :model="adminForm" 
        :rules="formRules" 
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username">
          <el-input 
            v-model="adminForm.username" 
            placeholder="请输入用户名"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input 
            v-model="adminForm.password" 
            type="password" 
            :placeholder="isEdit ? '留空表示不修改密码' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="adminForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="adminForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="adminForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="adminForm.role" placeholder="请选择角色" style="width: 100%;">
            <el-option label="超级管理员" :value="1" />
            <el-option label="普通管理员" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="adminForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="formVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="formLoading">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 管理员详情对话框 -->
    <el-dialog v-model="detailVisible" title="管理员详情" width="600px">
      <div v-if="currentAdmin" class="admin-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="管理员ID">{{ currentAdmin.id }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ currentAdmin.username }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ currentAdmin.realName || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ currentAdmin.phone || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱" span="2">{{ currentAdmin.email || '未设置' }}</el-descriptions-item>
          <el-descriptions-item label="角色">
            <el-tag :type="currentAdmin.role === 1 ? 'danger' : 'primary'">
              {{ currentAdmin.role === 1 ? '超级管理员' : '普通管理员' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="currentAdmin.status === 1 ? 'success' : 'danger'">
              {{ currentAdmin.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="最后登录时间">{{ currentAdmin.lastLoginTime || '从未登录' }}</el-descriptions-item>
          <el-descriptions-item label="最后登录IP">{{ currentAdmin.lastLoginIp || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ currentAdmin.createTime }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ currentAdmin.updateTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Refresh } from '@element-plus/icons-vue'
import { adminManagementApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  role: null,
  status: null
})

// 管理员列表
const adminList = ref([])
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

// 表单相关
const formVisible = ref(false)
const formLoading = ref(false)
const formRef = ref()
const isEdit = ref(false)
const adminForm = reactive({
  id: null,
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  role: 2,
  status: 1
})

// 表单验证规则
const formRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 管理员详情
const detailVisible = ref(false)
const currentAdmin = ref(null)

// 加载管理员列表
const loadAdminList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      role: searchForm.role,
      status: searchForm.status
    }
    
    const response = await adminManagementApi.getAdminList(params)
    adminList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载管理员列表失败:', error)
    ElMessage.error('加载管理员列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    statsLoading.value = true
    const response = await adminManagementApi.getAdminStatistics()
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
  loadAdminList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.role = null
  searchForm.status = null
  handleSearch()
}

// 分页处理
const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  loadAdminList()
}

const handlePageChange = (newPage) => {
  pagination.page = newPage
  loadAdminList()
}

// 显示新增对话框
const showCreateDialog = () => {
  isEdit.value = false
  resetForm()
  formVisible.value = true
}

// 显示编辑对话框
const showEditDialog = (admin) => {
  isEdit.value = true
  adminForm.id = admin.id
  adminForm.username = admin.username
  adminForm.password = ''
  adminForm.realName = admin.realName || ''
  adminForm.phone = admin.phone || ''
  adminForm.email = admin.email || ''
  adminForm.role = admin.role
  adminForm.status = admin.status
  formVisible.value = true
}

// 重置表单
const resetForm = () => {
  adminForm.id = null
  adminForm.username = ''
  adminForm.password = ''
  adminForm.realName = ''
  adminForm.phone = ''
  adminForm.email = ''
  adminForm.role = 2
  adminForm.status = 1
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

// 提交表单
const submitForm = async () => {
  try {
    await formRef.value.validate()
    formLoading.value = true
    
    if (isEdit.value) {
      await adminManagementApi.updateAdmin(adminForm.id, { ...adminForm })
      ElMessage.success('更新管理员成功')
    } else {
      await adminManagementApi.createAdmin({ ...adminForm })
      ElMessage.success('创建管理员成功')
    }
    
    formVisible.value = false
    loadAdminList()
    loadStatistics()
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error('操作失败')
  } finally {
    formLoading.value = false
  }
}

// 切换管理员状态
const toggleAdminStatus = async (admin) => {
  try {
    const action = admin.status === 1 ? '禁用' : '启用'
    await ElMessageBox.confirm(
      `确认要${action}管理员 "${admin.username}" 吗？`,
      '确认操作',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    admin.statusLoading = true
    const newStatus = admin.status === 1 ? 0 : 1
    await adminManagementApi.updateAdminStatus(admin.id, newStatus)
    
    admin.status = newStatus
    ElMessage.success(`${action}成功`)
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('切换管理员状态失败:', error)
      ElMessage.error('操作失败')
    }
  } finally {
    admin.statusLoading = false
  }
}

// 重置密码
const resetPassword = async (admin) => {
  try {
    await ElMessageBox.confirm(
      `确认要重置管理员 "${admin.username}" 的密码吗？`,
      '确认重置密码',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    admin.passwordLoading = true
    const response = await adminManagementApi.resetAdminPassword(admin.id)
    
    ElMessage({
      message: response.message || '密码重置成功',
      type: 'success',
      duration: 5000
    })
  } catch (error) {
    if (error !== 'cancel') {
      console.error('重置密码失败:', error)
      ElMessage.error('重置密码失败')
    }
  } finally {
    admin.passwordLoading = false
  }
}

// 查看管理员详情
const viewAdminDetail = async (admin) => {
  try {
    const response = await adminManagementApi.getAdminDetail(admin.id)
    currentAdmin.value = response
    detailVisible.value = true
  } catch (error) {
    console.error('获取管理员详情失败:', error)
    ElMessage.error('获取管理员详情失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  loadAdminList()
  loadStatistics()
})
</script>

<style scoped>
.admin-management {
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

.right-actions {
  margin-left: auto;
  display: flex;
  gap: 12px;
}

.table-card {
  background: #fff;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}

.dialog-footer {
  text-align: right;
}
</style>