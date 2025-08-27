<template>
  <div class="table-list">
    <div class="page-header">
      <h2>餐桌管理</h2>
      <div class="header-actions">
        <el-button type="success" @click="handleBatchCreate">
          <el-icon><Plus /></el-icon>
          批量创建
        </el-button>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增餐桌
        </el-button>
      </div>
    </div>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="餐桌号">
          <el-input v-model="searchForm.tableNumber" placeholder="请输入餐桌号" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value="" />
            <el-option label="空闲" :value="1" />
            <el-option label="占用" :value="2" />
            <el-option label="停用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 表格 -->
    <el-card>
      <el-table :data="tableList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="tableNumber" label="餐桌号" width="120" />
        <el-table-column prop="seats" label="座位数" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="qrCode" label="二维码" width="100">
          <template #default="{ row }">
            <el-button 
              v-if="row.qrCode" 
              type="primary" 
              text 
              @click="handleViewQrCode(row)"
            >
              查看
            </el-button>
            <el-button 
              v-else 
              type="info" 
              text 
              @click="handleGenerateQrCode(row)"
            >
              生成
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleEdit(row)">编辑</el-button>
            <el-button 
              type="warning" 
              text 
              @click="handleUpdateStatus(row, row.status === 1 ? 0 : 1)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button type="danger" text @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <el-pagination
        v-model:current-page="pageInfo.current"
        v-model:page-size="pageInfo.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="pageInfo.total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px"
      />
    </el-card>
    
    <!-- 新增/编辑对话框 -->
    <el-dialog 
      v-model="dialogVisible" 
      :title="dialogTitle"
      width="500px"
    >
      <el-form 
        ref="formRef"
        :model="tableForm" 
        :rules="tableRules"
        label-width="100px"
      >
        <el-form-item label="餐桌号" prop="tableNumber">
          <el-input v-model="tableForm.tableNumber" placeholder="请输入餐桌号" />
        </el-form-item>
        <el-form-item label="座位数" prop="seats">
          <el-input-number 
            v-model="tableForm.seats" 
            :min="1" 
            :max="20"
            placeholder="请输入座位数"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="tableForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 批量创建对话框 -->
    <el-dialog 
      v-model="batchDialogVisible" 
      title="批量创建餐桌"
      width="500px"
    >
      <el-form 
        ref="batchFormRef"
        :model="batchForm" 
        :rules="batchRules"
        label-width="120px"
      >
        <el-form-item label="起始餐桌号" prop="startNumber">
          <el-input-number 
            v-model="batchForm.startNumber" 
            :min="1" 
            placeholder="请输入起始餐桌号"
          />
        </el-form-item>
        <el-form-item label="创建数量" prop="count">
          <el-input-number 
            v-model="batchForm.count" 
            :min="1" 
            :max="100"
            placeholder="请输入创建数量"
          />
        </el-form-item>
        <el-form-item label="座位数" prop="seats">
          <el-input-number 
            v-model="batchForm.seats" 
            :min="1" 
            :max="20"
            placeholder="请输入座位数"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBatchSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 二维码查看对话框 -->
    <el-dialog 
      v-model="qrCodeDialogVisible" 
      title="餐桌二维码"
      width="400px"
    >
      <div class="qr-code-container">
        <div class="qr-code-info">
          <h3>餐桌 {{ currentTable?.tableNumber }}</h3>
          <p>座位数：{{ currentTable?.seats }}人</p>
        </div>
        <div class="qr-code-image">
          <img v-if="currentTable?.qrCode" :src="currentTable.qrCode" alt="二维码" />
          <div v-else class="no-qr-code">暂无二维码</div>
        </div>
        <div class="qr-code-actions">
          <el-button type="primary" @click="handleDownloadQrCode">下载二维码</el-button>
          <el-button @click="handleRegenerateQrCode">重新生成</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { tableApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  tableNumber: '',
  status: ''
})

// 分页信息
const pageInfo = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 餐桌列表
const tableList = ref([])
const loading = ref(false)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增餐桌')
const formRef = ref()

// 餐桌表单
const tableForm = reactive({
  id: null,
  tableNumber: '',
  seats: 4,
  status: 1
})

// 表单验证规则
const tableRules = {
  tableNumber: [
    { required: true, message: '请输入餐桌号', trigger: 'blur' }
  ],
  seats: [
    { required: true, message: '请输入座位数', trigger: 'blur' }
  ]
}

// 批量创建对话框
const batchDialogVisible = ref(false)
const batchFormRef = ref()

// 批量创建表单
const batchForm = reactive({
  startNumber: 1,
  count: 5,
  seats: 4
})

// 批量创建验证规则
const batchRules = {
  startNumber: [
    { required: true, message: '请输入起始餐桌号', trigger: 'blur' }
  ],
  count: [
    { required: true, message: '请输入创建数量', trigger: 'blur' }
  ],
  seats: [
    { required: true, message: '请输入座位数', trigger: 'blur' }
  ]
}

// 二维码对话框
const qrCodeDialogVisible = ref(false)
const currentTable = ref(null)

// 获取餐桌列表
const getTableList = async () => {
  loading.value = true
  try {
    const data = await tableApi.getTableList()
    tableList.value = data
    pageInfo.total = data.length
  } catch (error) {
    ElMessage.error('获取餐桌列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pageInfo.current = 1
  getTableList()
}

// 重置
const handleReset = () => {
  searchForm.tableNumber = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增餐桌'
  Object.assign(tableForm, {
    id: null,
    tableNumber: '',
    seats: 4,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑餐桌'
  Object.assign(tableForm, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除餐桌"${row.tableNumber}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await tableApi.deleteTable(row.id)
      ElMessage.success('删除成功')
      getTableList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 更新状态
const handleUpdateStatus = async (row, status) => {
  try {
    await tableApi.updateTableStatus(row.id, status)
    ElMessage.success(status === 1 ? '启用成功' : '停用成功')
    getTableList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (tableForm.id) {
      await tableApi.updateTable(tableForm.id, tableForm)
      ElMessage.success('更新成功')
    } else {
      await tableApi.createTable(tableForm)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    getTableList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 批量创建
const handleBatchCreate = () => {
  batchDialogVisible.value = true
}

// 批量创建提交
const handleBatchSubmit = async () => {
  const valid = await batchFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    await tableApi.batchCreateTables(batchForm)
    ElMessage.success('批量创建成功')
    batchDialogVisible.value = false
    getTableList()
  } catch (error) {
    ElMessage.error('批量创建失败')
  }
}

// 查看二维码
const handleViewQrCode = (row) => {
  currentTable.value = row
  qrCodeDialogVisible.value = true
}

// 生成二维码
const handleGenerateQrCode = async (row) => {
  try {
    const data = await tableApi.getTableQrCode(row.id)
    ElMessage.success('二维码生成成功')
    getTableList()
  } catch (error) {
    ElMessage.error('二维码生成失败')
  }
}

// 下载二维码
const handleDownloadQrCode = () => {
  if (!currentTable.value?.qrCode) return
  
  const link = document.createElement('a')
  link.href = currentTable.value.qrCode
  link.download = `table-${currentTable.value.tableNumber}-qrcode.png`
  link.click()
}

// 重新生成二维码
const handleRegenerateQrCode = async () => {
  try {
    await tableApi.getTableQrCode(currentTable.value.id)
    ElMessage.success('二维码重新生成成功')
    getTableList()
    qrCodeDialogVisible.value = false
  } catch (error) {
    ElMessage.error('二维码生成失败')
  }
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'danger',
    1: 'success',
    2: 'warning'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    0: '停用',
    1: '空闲',
    2: '占用'
  }
  return texts[status] || '未知'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

// 分页改变
const handleSizeChange = () => {
  getTableList()
}

const handleCurrentChange = () => {
  getTableList()
}

onMounted(() => {
  getTableList()
})
</script>

<style scoped>
.table-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.search-card {
  margin-bottom: 20px;
}

.qr-code-container {
  text-align: center;
  padding: 20px;
}

.qr-code-info {
  margin-bottom: 20px;
}

.qr-code-info h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.qr-code-info p {
  margin: 0;
  color: #666;
}

.qr-code-image {
  margin-bottom: 20px;
}

.qr-code-image img {
  width: 200px;
  height: 200px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.no-qr-code {
  width: 200px;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #999;
  margin: 0 auto;
}

.qr-code-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
}
</style>













