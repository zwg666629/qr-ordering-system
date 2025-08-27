<template>
  <div class="order-detail-page">
    <div class="page-header">
      <el-button @click="goBack" text>
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
      <h2>订单详情</h2>
      <div></div>
    </div>
    
    <div v-if="orderDetail" class="detail-content">
      <!-- 基本信息 -->
      <el-card class="info-card" shadow="never">
        <template #header>
          <div class="card-header">
            <span>基本信息</span>
            <el-tag :type="getStatusType(orderDetail.status)" size="large">
              {{ getStatusText(orderDetail.status) }}
            </el-tag>
          </div>
        </template>
        
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">订单号：</span>
              <span class="value">{{ orderDetail.orderNumber }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">餐桌号：</span>
              <span class="value">{{ orderDetail.tableNumber }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">订单金额：</span>
              <span class="value price">¥{{ orderDetail.totalAmount }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">支付方式：</span>
              <span class="value">{{ getPaymentMethodText(orderDetail.paymentMethod) }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">下单时间：</span>
              <span class="value">{{ formatTime(orderDetail.createTime) }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">支付时间：</span>
              <span class="value">{{ formatTime(orderDetail.paymentTime) }}</span>
            </div>
          </el-col>
          <el-col :span="24" v-if="orderDetail.remark">
            <div class="info-item">
              <span class="label">备注：</span>
              <span class="value">{{ orderDetail.remark }}</span>
            </div>
          </el-col>
        </el-row>
      </el-card>
      
      <!-- 商品信息 -->
      <el-card class="items-card" shadow="never">
        <template #header>
          <span>商品信息</span>
        </template>
        
        <el-table :data="orderDetail.items" border>
          <el-table-column prop="dishName" label="菜品名称" />
          <el-table-column prop="dishPrice" label="单价" width="120">
            <template #default="{ row }">
              ¥{{ row.dishPrice }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" />
          <el-table-column prop="subtotal" label="小计" width="120">
            <template #default="{ row }">
              <span class="price">¥{{ row.subtotal }}</span>
            </template>
          </el-table-column>
        </el-table>
        
        <div class="total-section">
          <div class="total-item">
            <span class="label">商品总数：</span>
            <span class="value">{{ getTotalQuantity() }}件</span>
          </div>
          <div class="total-item">
            <span class="label">订单总额：</span>
            <span class="value price total-price">¥{{ orderDetail.totalAmount }}</span>
          </div>
        </div>
      </el-card>
      
      <!-- 操作按钮 -->
      <el-card class="actions-card" shadow="never">
        <template #header>
          <span>订单操作</span>
        </template>
        
        <div class="actions">
          <el-button 
            v-if="orderDetail.status === 2" 
            type="primary" 
            @click="handleProcessOrder"
          >
            开始制作
          </el-button>
          <el-button 
            v-if="orderDetail.status === 3" 
            type="success" 
            @click="handleCompleteOrder"
          >
            完成订单
          </el-button>
          <el-button 
            v-if="orderDetail.status === 1" 
            type="danger" 
            @click="handleCancelOrder"
          >
            取消订单
          </el-button>
          <el-button @click="printOrder">
            <el-icon><Printer /></el-icon>
            打印订单
          </el-button>
        </div>
      </el-card>
    </div>
    
    <div v-else class="loading-container">
      <el-skeleton :rows="8" animated />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '@/api'

const route = useRoute()
const router = useRouter()

const orderDetail = ref(null)

// 获取订单详情
const getOrderDetail = async () => {
  try {
    const orderId = route.params.id
    orderDetail.value = await orderApi.getOrderDetail(orderId)
  } catch (error) {
    ElMessage.error('获取订单详情失败')
    goBack()
  }
}

// 返回
const goBack = () => {
  router.back()
}

// 开始制作
const handleProcessOrder = () => {
  ElMessageBox.confirm(
    `确定开始制作订单"${orderDetail.value.orderNumber}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  ).then(async () => {
    try {
      await orderApi.updateOrderStatus(orderDetail.value.id, 3)
      ElMessage.success('订单已开始制作')
      getOrderDetail()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

// 完成订单
const handleCompleteOrder = () => {
  ElMessageBox.confirm(
    `确定完成订单"${orderDetail.value.orderNumber}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'success'
    }
  ).then(async () => {
    try {
      await orderApi.completeOrder(orderDetail.value.id)
      ElMessage.success('订单已完成')
      getOrderDetail()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

// 取消订单
const handleCancelOrder = () => {
  ElMessageBox.confirm(
    `确定取消订单"${orderDetail.value.orderNumber}"吗？`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await orderApi.updateOrderStatus(orderDetail.value.id, 5)
      ElMessage.success('订单已取消')
      getOrderDetail()
    } catch (error) {
      ElMessage.error('操作失败')
    }
  })
}

// 打印订单
const printOrder = () => {
  ElMessage.info('打印功能开发中...')
}

// 计算总数量
const getTotalQuantity = () => {
  if (!orderDetail.value?.items) return 0
  return orderDetail.value.items.reduce((sum, item) => sum + item.quantity, 0)
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

onMounted(() => {
  getOrderDetail()
})
</script>

<style scoped>
.order-detail-page {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-card {
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}

.info-item .label {
  color: #666;
  font-weight: 500;
  min-width: 80px;
}

.info-item .value {
  color: #333;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.items-card {
  margin-bottom: 20px;
}

.total-section {
  margin-top: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 4px;
  display: flex;
  justify-content: flex-end;
  gap: 40px;
}

.total-item {
  display: flex;
  align-items: center;
}

.total-item .label {
  color: #666;
  margin-right: 10px;
}

.total-price {
  font-size: 18px;
}

.actions-card {
  margin-bottom: 20px;
}

.actions {
  display: flex;
  gap: 12px;
}

.loading-container {
  padding: 40px;
}
</style>













