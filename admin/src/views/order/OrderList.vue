<template>
  <div class="order-list">
    <div class="page-header">
      <h2>订单管理</h2>
    </div>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单号">
          <el-input v-model="searchForm.orderNumber" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="餐桌号">
          <el-input v-model="searchForm.tableNumber" placeholder="请输入餐桌号" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态">
            <el-option label="全部" value="" />
            <el-option label="待支付" :value="1" />
            <el-option label="已支付" :value="2" />
            <el-option label="制作中" :value="3" />
            <el-option label="已完成" :value="4" />
            <el-option label="已取消" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ stats.todayOrders }}</div>
            <div class="stat-label">今日订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">¥{{ stats.todayRevenue }}</div>
            <div class="stat-label">今日营收</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ stats.pendingOrders }}</div>
            <div class="stat-label">待处理订单</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <div class="stat-value">{{ stats.completedOrders }}</div>
            <div class="stat-label">已完成订单</div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 表格 -->
    <el-card>
      <el-table :data="orderList" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        <el-table-column prop="tableNumber" label="餐桌号" width="100" />
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            <span class="price">¥{{ row.totalAmount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="订单状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100">
          <template #default="{ row }">
            {{ getPaymentMethodText(row.paymentMethod) }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text @click="handleViewDetail(row)">
              查看详情
            </el-button>
            <el-button 
              v-if="row.status === 2" 
              type="success" 
              text 
              @click="handleProcessOrder(row)"
            >
              开始制作
            </el-button>
            <el-button 
              v-if="row.status === 3" 
              type="warning" 
              text 
              @click="handleCompleteOrder(row)"
            >
              完成订单
            </el-button>
            <el-button 
              v-if="row.status === 1" 
              type="danger" 
              text 
              @click="handleCancelOrder(row)"
            >
              取消订单
            </el-button>
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
    
    <!-- 订单详情对话框 -->
    <el-dialog 
      v-model="detailDialogVisible" 
      title="订单详情"
      width="800px"
    >
      <div v-if="orderDetail" class="order-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单号">{{ orderDetail.orderNumber }}</el-descriptions-item>
          <el-descriptions-item label="餐桌号">{{ orderDetail.tableNumber }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="getStatusType(orderDetail.status)">
              {{ getStatusText(orderDetail.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span class="price">¥{{ orderDetail.totalAmount }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="支付方式">
            {{ getPaymentMethodText(orderDetail.paymentMethod) }}
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">
            {{ formatTime(orderDetail.createTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="支付时间">
            {{ formatTime(orderDetail.paymentTime) }}
          </el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">
            {{ orderDetail.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>
        
        <h3 style="margin: 20px 0 10px 0">订单商品</h3>
        <el-table :data="orderDetail.items" border>
          <el-table-column prop="dishName" label="菜品名称" />
          <el-table-column prop="dishPrice" label="单价" width="100">
            <template #default="{ row }">
              ¥{{ row.dishPrice }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="80" />
          <el-table-column prop="subtotal" label="小计" width="100">
            <template #default="{ row }">
              ¥{{ row.subtotal }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { orderApi } from '@/api'

const router = useRouter()

// 搜索表单
const searchForm = reactive({
  orderNumber: '',
  tableNumber: '',
  status: '',
  dateRange: []
})

// 分页信息
const pageInfo = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 订单列表
const orderList = ref([])
const loading = ref(false)

// 统计数据
const stats = reactive({
  todayOrders: 0,
  todayRevenue: 0,
  pendingOrders: 0,
  completedOrders: 0
})

// 订单详情对话框
const detailDialogVisible = ref(false)
const orderDetail = ref(null)

// 获取订单列表
const getOrderList = async () => {
  loading.value = true
  try {
    const params = {
      orderNumber: searchForm.orderNumber || undefined,
      tableNumber: searchForm.tableNumber || undefined,
      status: searchForm.status !== '' ? searchForm.status : undefined,
      startDate: searchForm.dateRange?.[0],
      endDate: searchForm.dateRange?.[1]
    }
    
    // 过滤掉空值
    Object.keys(params).forEach(key => {
      if (params[key] === undefined) {
        delete params[key]
      }
    })
    
    const data = await orderApi.getOrderList(params)
    orderList.value = data
    pageInfo.total = data.length
    
    // 计算统计数据
    calculateStats(data)
  } catch (error) {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

// 计算统计数据
const calculateStats = (orders) => {
  const today = new Date().toDateString()
  
  stats.todayOrders = orders.filter(order => 
    new Date(order.createTime).toDateString() === today
  ).length
  
  stats.todayRevenue = orders
    .filter(order => 
      new Date(order.createTime).toDateString() === today && 
      [2, 3, 4].includes(order.status)
    )
    .reduce((sum, order) => sum + order.totalAmount, 0)
  
  stats.pendingOrders = orders.filter(order => [1, 2, 3].includes(order.status)).length
  stats.completedOrders = orders.filter(order => order.status === 4).length
}

// 搜索
const handleSearch = () => {
  pageInfo.current = 1
  getOrderList()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    orderNumber: '',
    tableNumber: '',
    status: '',
    dateRange: []
  })
  handleSearch()
}

// 查看详情
const handleViewDetail = async (row) => {
  try {
    orderDetail.value = await orderApi.getOrderDetail(row.id)
    detailDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取订单详情失败')
  }
}

// 开始制作
const handleProcessOrder = (row) => {
  ElMessageBox.confirm(
    `确定开始制作订单"${row.orderNumber}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      await orderApi.updateOrderStatus(row.id, 3)
      ElMessage.success('订单已开始制作')
      getOrderList()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

// 完成订单
const handleCompleteOrder = (row) => {
  ElMessageBox.confirm(
    `确定完成订单"${row.orderNumber}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(async () => {
    try {
      await orderApi.completeOrder(row.id)
      ElMessage.success('订单已完成')
      getOrderList()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

// 取消订单
const handleCancelOrder = (row) => {
  ElMessageBox.confirm(
    `确定取消订单"${row.orderNumber}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await orderApi.updateOrderStatus(row.id, 5)
      ElMessage.success('订单已取消')
      getOrderList()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    1: 'warning',
    2: 'primary',
    3: 'info',
    4: 'success',
    5: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = {
    1: '待支付',
    2: '已支付',
    3: '制作中',
    4: '已完成',
    5: '已取消'
  }
  return texts[status] || '未知'
}

// 获取支付方式文本
const getPaymentMethodText = (method) => {
  if (!method) return '-'
  const texts = {
    1: '微信支付',
    2: '支付宝'
  }
  return texts[method] || '其他'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '-'
  return new Date(time).toLocaleString()
}

// 分页改变
const handleSizeChange = () => {
  getOrderList()
}

const handleCurrentChange = () => {
  getOrderList()
}

onMounted(() => {
  getOrderList()
})
</script>

<style scoped>
.order-list {
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

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-item {
  padding: 20px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 8px;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.order-detail {
  padding: 20px 0;
}
</style>













