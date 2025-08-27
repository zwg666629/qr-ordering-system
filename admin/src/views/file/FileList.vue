<template>
  <div class="file-management">
    <!-- 页面标题和统计卡片 -->
    <div class="page-header">
      <h2>文件管理</h2>
      <div class="stats-cards">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.totalFiles || 0 }}</div>
            <div class="stats-label">总文件数</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ formatFileSize(statistics.totalSize || 0) }}</div>
            <div class="stats-label">总大小</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.avatarCount || 0 }}</div>
            <div class="stats-label">头像文件</div>
          </div>
        </el-card>
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-number">{{ statistics.dishCount || 0 }}</div>
            <div class="stats-label">菜品图片</div>
          </div>
        </el-card>
      </div>
    </div>

    <!-- 搜索和操作栏 -->
    <el-card class="search-card">
      <div class="search-form">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索文件名或文件类型"
          style="width: 300px;"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        
        <el-select v-model="searchForm.category" placeholder="文件分类" clearable style="width: 120px;">
          <el-option label="头像" value="avatar" />
          <el-option label="菜品图片" value="dish" />
          <el-option label="通用文件" value="general" />
        </el-select>
        
        <el-button type="primary" @click="handleSearch" :loading="loading">
          <el-icon><Search /></el-icon>
          搜索
        </el-button>
        <el-button @click="resetSearch">重置</el-button>
        
        <div class="right-actions">
          <el-button type="primary" @click="uploadVisible = true" :icon="Upload">
            上传文件
          </el-button>
          <el-button 
            type="danger" 
            @click="batchDelete" 
            :disabled="selectedFiles.length === 0"
            :icon="Delete"
          >
            批量删除
          </el-button>
          <el-button @click="loadStatistics" :loading="statsLoading" :icon="Refresh">
            刷新统计
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 文件列表 -->
    <el-card class="table-card">
      <el-table
        :data="fileList"
        v-loading="loading"
        stripe
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="originalName" label="文件名" width="200" show-overflow-tooltip />
        <el-table-column prop="fileType" label="类型" width="80" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag :type="getCategoryType(row.category)" size="small">
              {{ getCategoryLabel(row.category) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="大小" width="100">
          <template #default="{ row }">
            {{ formatFileSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="mimeType" label="MIME类型" width="150" show-overflow-tooltip />
        <el-table-column label="预览" width="100" align="center">
          <template #default="{ row }">
            <el-image
              v-if="isImage(row.mimeType)"
              :src="row.fileUrl"
              style="width: 50px; height: 50px;"
              fit="cover"
              :preview-src-list="[row.fileUrl]"
              preview-teleported
            />
            <el-icon v-else :size="30" color="#909399">
              <Document />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="uploaderType" label="上传者" width="100">
          <template #default="{ row }">
            <el-tag :type="row.uploaderType === 1 ? 'success' : 'primary'" size="small">
              {{ row.uploaderType === 1 ? '用户' : '管理员' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewFileDetail(row)">
              详情
            </el-button>
            <el-button 
              type="success" 
              size="small" 
              @click="downloadFile(row)" 
              :loading="row.downloading"
              :icon="Download"
            >
              下载
            </el-button>
            <el-button 
              type="danger" 
              size="small" 
              @click="deleteFile(row)"
              :loading="row.deleting"
              :icon="Delete"
            >
              删除
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

    <!-- 文件上传对话框 -->
    <el-dialog v-model="uploadVisible" title="上传文件" width="500px">
      <el-form :model="uploadForm" label-width="80px">
        <el-form-item label="文件分类">
          <el-select v-model="uploadForm.category" placeholder="请选择分类">
            <el-option label="头像" value="avatar" />
            <el-option label="菜品图片" value="dish" />
            <el-option label="通用文件" value="general" />
          </el-select>
        </el-form-item>
        <el-form-item label="选择文件">
          <el-upload
            ref="uploadRef"
            :auto-upload="false"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
            :file-list="uploadForm.fileList"
            :limit="5"
            drag
            multiple
          >
            <el-icon class="el-icon--upload"><Upload /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">
                支持多文件上传，单个文件不超过10MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadVisible = false">取消</el-button>
          <el-button 
            type="primary" 
            @click="submitUpload" 
            :loading="uploading"
            :disabled="uploadForm.fileList.length === 0"
          >
            确认上传
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 文件详情对话框 -->
    <el-dialog 
      v-model="detailVisible" 
      title="文件详情" 
      width="600px"
      :close-on-click-modal="false"
      custom-class="file-detail-dialog"
    >
      <div v-if="currentFile" class="file-detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="文件ID">{{ currentFile.id }}</el-descriptions-item>
          <el-descriptions-item label="原始文件名" class="filename-item">
            <span class="filename-text" :title="currentFile.originalName">
              {{ currentFile.originalName }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="存储文件名" class="filename-item">
            <span class="filename-text" :title="currentFile.fileName">
              {{ currentFile.fileName }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="文件大小">{{ formatFileSize(currentFile.fileSize) }}</el-descriptions-item>
          <el-descriptions-item label="文件类型">{{ currentFile.fileType }}</el-descriptions-item>
          <el-descriptions-item label="MIME类型" class="mime-type-item">
            <span class="mime-type-text" :title="currentFile.mimeType">
              {{ currentFile.mimeType }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="文件分类">
            <el-tag :type="getCategoryType(currentFile.category)">
              {{ getCategoryLabel(currentFile.category) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="上传者类型">
            <el-tag :type="currentFile.uploaderType === 1 ? 'success' : 'primary'">
              {{ currentFile.uploaderType === 1 ? '用户' : '管理员' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="上传时间" span="2">{{ currentFile.createTime }}</el-descriptions-item>
          <el-descriptions-item label="文件路径" span="2" class="file-url-item">
            <div class="file-url-container">
              <el-input 
                :value="currentFile.fileUrl" 
                readonly 
                type="textarea" 
                :rows="2"
                resize="none"
                class="file-url-input"
              />
              <div class="file-url-actions">
                <el-button 
                  size="small" 
                  type="primary" 
                  @click="copyFileUrl(currentFile.fileUrl)"
                >
                  复制
                </el-button>
                <el-button 
                  size="small" 
                  type="success" 
                  @click="openFileUrl(currentFile.fileUrl)"
                >
                  打开
                </el-button>
              </div>
            </div>
          </el-descriptions-item>
        </el-descriptions>
        <div v-if="isImage(currentFile.mimeType)" class="preview-section">
          <div class="preview-label">预览：</div>
          <div class="image-container">
            <el-image 
              :src="currentFile.fileUrl" 
              style="width: 100%; height: auto;" 
              fit="contain"
              :preview-src-list="[currentFile.fileUrl]"
              preview-teleported
              loading="lazy"
            />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Upload, Delete, Download, Document, Refresh } from '@element-plus/icons-vue'
import { fileManagementApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  keyword: '',
  category: ''
})

// 文件列表
const fileList = ref([])
const loading = ref(false)
const statsLoading = ref(false)
const selectedFiles = ref([])

// 分页
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

// 统计信息
const statistics = ref({})

// 上传相关
const uploadVisible = ref(false)
const uploading = ref(false)
const uploadForm = reactive({
  category: 'general',
  fileList: []
})

// 文件详情
const detailVisible = ref(false)
const currentFile = ref(null)

// 加载文件列表
const loadFileList = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.page,
      size: pagination.size,
      keyword: searchForm.keyword || undefined,
      category: searchForm.category || undefined
    }
    
    const response = await fileManagementApi.getFileList(params)
    fileList.value = response.records || []
    pagination.total = response.total || 0
  } catch (error) {
    console.error('加载文件列表失败:', error)
    ElMessage.error('加载文件列表失败')
  } finally {
    loading.value = false
  }
}

// 加载统计信息
const loadStatistics = async () => {
  try {
    statsLoading.value = true
    const response = await fileManagementApi.getFileStatistics()
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
  loadFileList()
}

// 重置搜索
const resetSearch = () => {
  searchForm.keyword = ''
  searchForm.category = ''
  handleSearch()
}

// 分页处理
const handleSizeChange = (newSize) => {
  pagination.size = newSize
  pagination.page = 1
  loadFileList()
}

const handlePageChange = (newPage) => {
  pagination.page = newPage
  loadFileList()
}

// 选择文件
const handleSelectionChange = (selection) => {
  selectedFiles.value = selection
}

// 文件上传处理
const handleFileChange = (file, fileList) => {
  uploadForm.fileList = fileList
}

const handleFileRemove = (file, fileList) => {
  uploadForm.fileList = fileList
}

// 提交上传
const submitUpload = async () => {
  try {
    uploading.value = true
    
    for (const fileItem of uploadForm.fileList) {
      if (fileItem.raw) {
        await fileManagementApi.uploadFile(fileItem.raw, uploadForm.category)
      }
    }
    
    ElMessage.success('文件上传成功')
    uploadVisible.value = false
    uploadForm.fileList = []
    uploadForm.category = 'general'
    
    // 刷新列表和统计
    loadFileList()
    loadStatistics()
  } catch (error) {
    console.error('文件上传失败:', error)
    ElMessage.error('文件上传失败')
  } finally {
    uploading.value = false
  }
}

// 删除文件
const deleteFile = async (file) => {
  try {
    await ElMessageBox.confirm(
      `确认删除文件 "${file.originalName}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    file.deleting = true
    await fileManagementApi.deleteFile(file.id)
    
    ElMessage.success('删除成功')
    loadFileList()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除文件失败:', error)
      ElMessage.error('删除失败')
    }
  } finally {
    file.deleting = false
  }
}

// 批量删除
const batchDelete = async () => {
  try {
    await ElMessageBox.confirm(
      `确认删除选中的 ${selectedFiles.value.length} 个文件吗？`,
      '确认批量删除',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )
    
    const fileIds = selectedFiles.value.map(file => file.id)
    await fileManagementApi.batchDeleteFiles(fileIds)
    
    ElMessage.success('批量删除成功')
    loadFileList()
    loadStatistics()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除失败:', error)
      ElMessage.error('批量删除失败')
    }
  }
}

// 查看文件详情
const viewFileDetail = async (file) => {
  try {
    const response = await fileManagementApi.getFileDetail(file.id)
    currentFile.value = response
    detailVisible.value = true
  } catch (error) {
    console.error('获取文件详情失败:', error)
    ElMessage.error('获取文件详情失败')
  }
}

// 复制文件URL
const copyFileUrl = async (url) => {
  try {
    await navigator.clipboard.writeText(url)
    ElMessage.success('文件路径已复制到剪贴板')
  } catch (error) {
    // 降级方案：使用document.execCommand
    const textArea = document.createElement('textarea')
    textArea.value = url
    document.body.appendChild(textArea)
    textArea.select()
    try {
      document.execCommand('copy')
      ElMessage.success('文件路径已复制到剪贴板')
    } catch (err) {
      ElMessage.error('复制失败，请手动复制')
    }
    document.body.removeChild(textArea)
  }
}

// 打开文件URL
const openFileUrl = (url) => {
  window.open(url, '_blank')
}

// 下载文件
const downloadFile = async (file) => {
  try {
    // 设置下载状态
    file.downloading = true
    
    const response = await fileManagementApi.downloadFile(file.id)
    
    // 检查响应是否为blob
    if (!(response instanceof Blob)) {
      console.error('响应不是Blob类型:', response)
      ElMessage.error('文件下载响应格式错误')
      return
    }
    
    // 检查blob大小
    if (response.size === 0) {
      console.error('文件内容为空')
      ElMessage.error('文件内容为空，无法下载')
      return
    }
    
    // 创建下载链接
    const url = window.URL.createObjectURL(response)
    const link = document.createElement('a')
    link.href = url
    link.download = file.originalName || `文件_${file.id}`
    
    // 触发下载
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    // 释放对象URL
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载文件失败，详细信息:', {
      error: error.message,
      response: error.response,
      status: error.response?.status,
      statusText: error.response?.statusText,
      data: error.response?.data
    })
    
    let errorMessage = '下载文件失败'
    if (error.response?.status === 404) {
      errorMessage = '文件不存在或已被删除'
    } else if (error.response?.status === 500) {
      errorMessage = '服务器内部错误，请联系管理员'
    } else if (error.response?.status === 403) {
      errorMessage = '没有权限下载此文件'
    } else if (error.code === 'NETWORK_ERROR') {
      errorMessage = '网络连接错误，请检查网络'
    }
    
    ElMessage.error(errorMessage)
  } finally {
    // 清除下载状态
    file.downloading = false
  }
}

// 工具函数
const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const isImage = (mimeType) => {
  return mimeType && mimeType.startsWith('image/')
}

const getCategoryType = (category) => {
  const types = {
    avatar: 'success',
    dish: 'warning',
    general: 'info'
  }
  return types[category] || 'info'
}

const getCategoryLabel = (category) => {
  const labels = {
    avatar: '头像',
    dish: '菜品图片',
    general: '通用文件'
  }
  return labels[category] || '未知'
}

// 页面加载时获取数据
onMounted(() => {
  loadFileList()
  loadStatistics()
})
</script>

<style scoped>
.file-management {
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
  font-size: 28px;
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

/* 文件详情对话框样式 */
.file-detail-dialog :deep(.el-dialog) {
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.file-detail-dialog :deep(.el-dialog__header) {
  flex-shrink: 0;
}

.file-detail-dialog :deep(.el-dialog__body) {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  max-height: 70vh;
}

.file-detail-dialog :deep(.el-dialog__footer) {
  flex-shrink: 0;
}

/* 备选方案：全局样式 */
.el-dialog.file-detail-dialog {
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.el-dialog.file-detail-dialog .el-dialog__body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  max-height: 70vh;
}

.file-detail-content {
  min-height: 0;
}

/* 文件名显示优化 */
.filename-item .filename-text {
  display: inline-block;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: top;
}

.filename-item .filename-text:hover {
  overflow: visible;
  white-space: normal;
  word-break: break-all;
}

/* MIME类型显示优化 */
.mime-type-item .mime-type-text {
  display: inline-block;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  vertical-align: top;
}

.mime-type-item .mime-type-text:hover {
  overflow: visible;
  white-space: normal;
  word-break: break-all;
}

/* 文件URL容器样式 */
.file-url-container {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.file-url-input {
  width: 100%;
}

.file-url-input .el-textarea__inner {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 12px;
  line-height: 1.4;
  background-color: #f8f9fa;
  border: 1px solid #e1e3e4;
  word-break: break-all;
  resize: none;
}

.file-url-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

/* 预览区域样式 */
.preview-section {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
  text-align: center;
}

.preview-label {
  font-weight: bold;
  color: #303133;
  margin-bottom: 12px;
  text-align: left;
}

.image-container {
  width: 100%;
  min-height: 200px;
  border-radius: 8px;
  border: 1px solid #ebeef5;
  background-color: #fafbfc;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 10px;
  box-sizing: border-box;
}

.image-container .el-image {
  max-width: 100%;
  width: auto;
  height: auto;
  object-fit: contain;
}

/* 描述列表优化 */
.el-descriptions {
  --el-descriptions-item-bordered-label-background: #fafbfc;
}

.el-descriptions :deep(.el-descriptions__cell) {
  vertical-align: top;
}

.el-descriptions :deep(.el-descriptions__content) {
  word-break: break-all;
  line-height: 1.5;
}

/* 上传相关样式 */
.el-upload {
  width: 100%;
}

.dialog-footer {
  text-align: right;
}

/* 滚动条样式 */
.file-detail-content::-webkit-scrollbar {
  width: 6px;
}

.file-detail-content::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.file-detail-content::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.file-detail-content::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 确保对话框内容区域可滚动 */
.el-dialog__body {
  position: relative;
}

.file-detail-content {
  position: relative;
  overflow-y: auto;
  max-height: 75vh;
  padding-right: 10px;
}
</style>