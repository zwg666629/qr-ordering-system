<template>
  <div class="order-confirm">
    <!-- 导航栏 -->
    <van-nav-bar 
      :title="pageTitle"
      left-text="返回" 
      left-arrow 
      @click-left="$router.go(-1)"
    />
    
    <!-- 餐桌信息提示 -->
    <div v-if="tableNumber && isFromQrCode" class="table-info">
      <van-notice-bar 
        :text="`已为您选择 ${tableNumber} 号餐桌`"
        left-icon="checked"
        background="#f0f9ff"
        color="#059669"
      />
    </div>
    
    <!-- 餐桌选择 -->
    <div class="table-section">
      <van-cell-group>
        <van-field
          v-model="tableNumber"
          label="餐桌号"
          placeholder="请输入餐桌号"
          :error-message="tableError"
          :readonly="isFromQrCode"
          @blur="validateTable"
        />
      </van-cell-group>
    </div>
    
    <!-- 订单商品 -->
    <div class="order-items">
      <h3 class="section-title">订单商品</h3>
      <div class="item-list">
        <div 
          class="order-item" 
          v-for="item in cartStore.items" 
          :key="item.id"
        >
          <img 
            :src="item.image || '/src/assets/dish-placeholder.svg'" 
            :alt="item.name"
            class="item-image"
          />
          <div class="item-info">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-price">¥{{ item.price }} x {{ item.quantity }}</div>
          </div>
          <div class="item-subtotal">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
        </div>
      </div>
    </div>
    
    <!-- 备注 -->
    <div class="remark-section">
      <van-cell-group>
        <van-field
          v-model="remark"
          label="备注"
          type="textarea"
          placeholder="请输入备注信息（选填）"
          rows="2"
          autosize
        />
      </van-cell-group>
    </div>
    
    <!-- 订单金额 -->
    <div class="amount-section">
      <van-cell-group>
        <van-cell title="商品金额" :value="`¥${cartStore.totalAmount.toFixed(2)}`" />
        <van-cell title="配送费" value="¥0.00" />
        <van-cell 
          title="合计" 
          :value="`¥${cartStore.totalAmount.toFixed(2)}`" 
          class="total-amount"
        />
      </van-cell-group>
    </div>
    
    <!-- 支付方式 -->
    <div class="payment-section">
      <h3 class="section-title">支付方式</h3>
      <van-radio-group v-model="paymentMethod">
        <van-cell-group>
          <van-cell clickable @click="paymentMethod = 1">
            <template #title>
              <div class="payment-option">
                <van-icon name="wechat-pay" color="#09BB07" size="20" />
                <span>微信支付</span>
              </div>
            </template>
            <template #right-icon>
              <van-radio name="1" />
            </template>
          </van-cell>
          <van-cell clickable @click="paymentMethod = 2">
            <template #title>
              <div class="payment-option">
                <van-icon name="alipay" color="#1677FF" size="20" />
                <span>支付宝</span>
              </div>
            </template>
            <template #right-icon>
              <van-radio name="2" />
            </template>
          </van-cell>
        </van-cell-group>
      </van-radio-group>
    </div>
    
    <!-- 底部提交栏 -->
    <div class="submit-bar">
      <div class="submit-info">
        <span class="label">合计：</span>
        <span class="amount">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
      </div>
      <van-button 
        type="primary" 
        size="large" 
        class="submit-btn"
        :loading="submitting"
        @click="submitOrder"
      >
        提交订单
      </van-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'
import { orderApi, tableApi } from '@/api'
import { showToast, showDialog } from 'vant'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()
const userStore = useUserStore()

// 表单数据
const tableNumber = ref('')
const tableId = ref(null)
const tableError = ref('')
const remark = ref('')
const paymentMethod = ref('1')
const submitting = ref(false)
const isFromQrCode = ref(false)

// 页面标题
const pageTitle = computed(() => {
  return tableNumber.value ? `餐桌${tableNumber.value} - 确认订单` : '确认订单'
})

// 初始化餐桌信息
onMounted(async () => {
  // 从URL参数获取餐桌号
  const tableParam = route.query.table
  if (tableParam) {
    tableNumber.value = tableParam
    isFromQrCode.value = true
    await validateTable()
  } else {
    // 尝试从sessionStorage恢复
    const savedTable = sessionStorage.getItem('currentTable')
    if (savedTable) {
      tableNumber.value = savedTable
      isFromQrCode.value = true
      await validateTable()
    }
  }
})

// 验证餐桌号
const validateTable = async () => {
  if (!tableNumber.value) {
    tableError.value = '请输入餐桌号'
    return false
  }
  
  try {
    const table = await tableApi.getTableByNumber(tableNumber.value)
    if (table) {
      tableId.value = table.id
      tableError.value = ''
      return true
    } else {
      tableError.value = '餐桌号不存在'
      return false
    }
  } catch (error) {
    tableError.value = '餐桌号验证失败'
    return false
  }
}

// 提交订单
const submitOrder = async () => {
  // 验证购物车
  if (cartStore.items.length === 0) {
    showToast('购物车为空')
    return
  }
  
  // 验证餐桌号
  const tableValid = await validateTable()
  if (!tableValid) {
    showToast('请输入正确的餐桌号')
    return
  }
  
  // 确认提交
  try {
    await showDialog({
      title: '确认下单',
      message: `餐桌号：${tableNumber.value}\n总金额：¥${cartStore.totalAmount.toFixed(2)}`,
      showCancelButton: true,
      confirmButtonText: '确认',
      cancelButtonText: '取消'
    })
  } catch {
    return // 用户取消
  }
  
  submitting.value = true
  
  try {
    // 构建订单数据
    const orderData = {
      tableId: tableId.value,
      tableNumber: tableNumber.value,
      userId: userStore.isLoggedIn ? userStore.userInfo.userId : null, // 添加用户ID
      totalAmount: cartStore.totalAmount,
      remark: remark.value,
      items: cartStore.items.map(item => ({
        dishId: item.id,
        dishName: item.name,
        dishImage: item.image,
        dishPrice: item.price,
        quantity: item.quantity,
        subtotal: item.price * item.quantity
      }))
    }
    
    // 创建订单
    const order = await orderApi.createOrder(orderData)
    
    // 清空购物车
    cartStore.clearCart()
    
    // 根据支付方式处理
    if (paymentMethod.value === '1' || paymentMethod.value === '2') {
      // 模拟支付（实际应调用支付接口）
      showToast('订单创建成功，正在跳转支付...')
      
      // 这里应该跳转到实际的支付页面
      // 现在先模拟支付成功
      setTimeout(async () => {
        try {
          await orderApi.payOrder(order.id, parseInt(paymentMethod.value))
          showToast('支付成功')
          // 支付成功后跳转，标记来源为支付完成
          router.push({
            path: `/order-detail/${order.id}`,
            query: { from: 'payment' }
          })
        } catch (error) {
          showToast('支付失败')
          // 即使支付失败也标记来源
          router.push({
            path: `/order-detail/${order.id}`,
            query: { from: 'payment' }
          })
        }
      }, 1500)
    } else {
      // 直接下单（不支付）跳转到订单详情
      router.push({
        path: `/order-detail/${order.id}`,
        query: { from: 'order-confirm' }
      })
    }
  } catch (error) {
    console.error('提交订单失败:', error)
    showToast(error.message || '提交订单失败')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.order-confirm {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80px;
}

.table-info {
  margin-bottom: 10px;
}

.section-title {
  padding: 15px;
  font-size: 14px;
  color: #666;
}

.table-section {
  margin-top: 10px;
}

.order-items {
  background: white;
  margin-top: 10px;
}

.item-list {
  padding: 0 15px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
  margin-right: 12px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 14px;
  color: #333;
  margin-bottom: 5px;
}

.item-price {
  font-size: 12px;
  color: #999;
}

.item-subtotal {
  font-size: 14px;
  color: #ff6b35;
  font-weight: bold;
}

.remark-section {
  margin-top: 10px;
}

.amount-section {
  margin-top: 10px;
}

.total-amount {
  font-weight: bold;
}

.total-amount :deep(.van-cell__value) {
  color: #ff6b35;
  font-size: 16px;
}

.payment-section {
  background: white;
  margin-top: 10px;
}

.payment-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 10px 15px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
}

.submit-info {
  display: flex;
  align-items: center;
}

.submit-info .label {
  font-size: 14px;
  color: #666;
}

.submit-info .amount {
  font-size: 18px;
  color: #ff6b35;
  font-weight: bold;
  margin-left: 5px;
}

.submit-btn {
  min-width: 120px;
}
</style>


