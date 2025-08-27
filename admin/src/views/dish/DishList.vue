<template>
  <div class="dish-list">
    <div class="page-header">
      <h2>菜品管理</h2>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增菜品
      </el-button>
    </div>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="菜品名称">
          <el-input v-model="searchForm.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类">
            <el-option label="全部" value="" />
            <el-option 
              v-for="category in categories" 
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value="" />
            <el-option label="在售" :value="1" />
            <el-option label="停售" :value="0" />
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
      <el-table :data="dishList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="image" label="图片" width="100">
          <template #default="{ row }">
            <el-image 
              :src="row.image || '/placeholder.jpg'" 
              style="width: 60px; height: 60px"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="菜品名称" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleEdit(row)">编辑</el-button>
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
      width="600px"
    >
      <el-form 
        ref="formRef"
        :model="dishForm" 
        :rules="dishRules"
        label-width="100px"
      >
        <el-form-item label="菜品名称" prop="name">
          <el-input v-model="dishForm.name" placeholder="请输入菜品名称" />
        </el-form-item>
        <el-form-item label="菜品分类" prop="categoryId">
          <el-select v-model="dishForm.categoryId" placeholder="请选择分类">
            <el-option 
              v-for="category in categories" 
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="菜品价格" prop="price">
          <el-input-number 
            v-model="dishForm.price" 
            :min="0" 
            :precision="2"
            placeholder="请输入价格"
          />
        </el-form-item>
        <el-form-item label="菜品图片">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
          >
            <img v-if="dishForm.image" :src="dishForm.image" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="菜品描述">
          <el-input 
            v-model="dishForm.description" 
            type="textarea"
            :rows="3"
            placeholder="请输入菜品描述"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="dishForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dishForm.status">
            <el-radio :label="1">在售</el-radio>
            <el-radio :label="0">停售</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { dishApi } from '@/api'

// 搜索表单
const searchForm = reactive({
  name: '',
  categoryId: '',
  status: ''
})

// 分页信息
const pageInfo = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 菜品列表
const dishList = ref([])
const loading = ref(false)

// 分类列表
const categories = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜品')
const formRef = ref()

// 菜品表单
const dishForm = reactive({
  id: null,
  name: '',
  categoryId: '',
  price: 0,
  image: '',
  description: '',
  sort: 0,
  status: 1
})

// 表单验证规则
const dishRules = {
  name: [
    { required: true, message: '请输入菜品名称', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择菜品分类', trigger: 'change' }
  ],
  price: [
    { required: true, message: '请输入菜品价格', trigger: 'blur' }
  ]
}

// 获取菜品列表
const getDishList = async () => {
  loading.value = true
  try {
    const params = {
      categoryId: searchForm.categoryId || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined,
      name: searchForm.name || undefined
    }
    const data = await dishApi.getDishList(params)
    
    // 添加分类名称
    dishList.value = data.map(dish => {
      const category = categories.value.find(cat => cat.id === dish.categoryId)
      return {
        ...dish,
        categoryName: category ? category.name : '未知分类'
      }
    })
    
    pageInfo.total = data.length
  } catch (error) {
    ElMessage.error('获取菜品列表失败')
  } finally {
    loading.value = false
  }
}

// 获取分类列表
const getCategoryList = async () => {
  try {
    categories.value = await dishApi.getCategories()
  } catch (error) {
    ElMessage.error('获取分类列表失败')
  }
}

// 搜索
const handleSearch = () => {
  pageInfo.current = 1
  getDishList()
}

// 重置
const handleReset = () => {
  searchForm.name = ''
  searchForm.categoryId = ''
  searchForm.status = ''
  handleSearch()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增菜品'
  Object.assign(dishForm, {
    id: null,
    name: '',
    categoryId: '',
    price: 0,
    image: '',
    description: '',
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  dialogTitle.value = '编辑菜品'
  Object.assign(dishForm, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm(
    `确定要删除菜品"${row.name}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await dishApi.deleteDish(row.id)
      ElMessage.success('删除成功')
      getDishList()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  })
}

// 状态改变
const handleStatusChange = async (row) => {
  try {
    await dishApi.updateDish(row.id, { status: row.status })
    ElMessage.success(row.status === 1 ? '已上架' : '已下架')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('操作失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (dishForm.id) {
      await dishApi.updateDish(dishForm.id, dishForm)
      ElMessage.success('更新成功')
    } else {
      await dishApi.createDish(dishForm)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    getDishList()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 上传成功
const handleUploadSuccess = (response) => {
  dishForm.image = response.data.url
}

// 分页改变
const handleSizeChange = () => {
  getDishList()
}

const handleCurrentChange = () => {
  getDishList()
}

onMounted(() => {
  getCategoryList()
  getDishList()
})
</script>

<style scoped>
.dish-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.avatar-uploader .avatar {
  width: 100px;
  height: 100px;
  display: block;
  object-fit: cover;
}

.avatar-uploader :deep(.el-upload) {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader :deep(.el-upload:hover) {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}
</style>

