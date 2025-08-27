<template>
  <div class="cart">
    <!-- 导航栏 -->
    <van-nav-bar 
      :title="pageTitle"
      left-text="返回" 
      left-arrow 
      @click-left="$router.go(-1)"
    />
    
    <!-- 餐桌信息提示 -->
    <div v-if="tableNumber" class="table-info">
      <van-notice-bar 
        :text="`${tableNumber}号餐桌 - 购物车`"
        left-icon="shop-o"
        background="#f0f9ff"
        color="#1e40af"
      />
    </div>
    
    <!-- 购物车内容 -->
    <div v-if="cartStore.items.length > 0" class="cart-content">
      <!-- 商品列表 -->
      <div class="cart-items">
        <div 
          class="cart-item" 
          v-for="item in cartStore.items" 
          :key="item.id"
        >
          <img 
            :src="item.image || '/src/assets/dish-placeholder.svg'" 
            :alt="item.name" 
            class="item-image"
          />
          <div class="item-info">
            <h3>{{ item.name }}</h3>
            <div class="item-footer">
              <span class="price">¥{{ item.price }}</span>
              <div class="quantity-control">
                <van-button 
                  icon="minus" 
                  size="small" 
                  @click="cartStore.removeFromCart(item.id)"
                />
                <span class="quantity">{{ item.quantity }}</span>
                <van-button 
                  icon="plus" 
                  type="primary" 
                  size="small" 
                  @click="cartStore.addToCart(item)"
                />
              </div>
            </div>
          </div>
          <div class="subtotal">
            ¥{{ (item.price * item.quantity).toFixed(2) }}
          </div>
        </div>
      </div>
      
      <!-- 总计 -->
      <div class="cart-summary">
        <div class="summary-row">
          <span>商品总计：</span>
          <span class="total-amount">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
        </div>
      </div>
      
      <!-- 底部操作栏 -->
      <div class="cart-bottom">
        <div class="total-info">
          <span class="total-count">共{{ cartStore.totalCount }}件</span>
          <span class="total-price">¥{{ cartStore.totalAmount.toFixed(2) }}</span>
        </div>
        <van-button 
          type="primary" 
          size="large" 
          class="checkout-btn"
          @click="checkout"
        >
          去结算
        </van-button>
      </div>
    </div>
    
    <!-- 空购物车 -->
    <div v-else class="empty-cart">
      <van-empty description="购物车空空如也">
        <van-button 
          type="primary" 
          size="small" 
          @click="$router.push('/menu')"
        >
          去点餐
        </van-button>
      </van-empty>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { showDialog } from 'vant'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const tableNumber = ref('')

// 页面标题
const pageTitle = computed(() => {
  return tableNumber.value ? `餐桌${tableNumber.value} - 购物车` : '购物车'
})

// 结算
const checkout = () => {
  // 跳转到订单确认页面，带上餐桌参数
  if (tableNumber.value) {
    router.push({
      path: '/order-confirm',
      query: { table: tableNumber.value }
    })
  } else {
    router.push('/order-confirm')
  }
}

onMounted(() => {
  // 获取URL中的餐桌号参数
  const tableParam = route.query.table
  if (tableParam) {
    tableNumber.value = tableParam
  } else {
    // 尝试从sessionStorage恢复
    const savedTable = sessionStorage.getItem('currentTable')
    if (savedTable) {
      tableNumber.value = savedTable
    }
  }
})
</script>

<style scoped>
.cart {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.table-info {
  margin-bottom: 10px;
}

.cart-content {
  padding-bottom: 80px;
}

.cart-items {
  padding: 10px;
}

.cart-item {
  display: flex;
  background: white;
  border-radius: 10px;
  padding: 15px;
  margin-bottom: 10px;
  align-items: center;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
  margin-right: 15px;
}

.item-info {
  flex: 1;
}

.item-info h3 {
  font-size: 16px;
  color: #333;
  margin-bottom: 15px;
}

.item-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.price {
  color: #ff6b35;
  font-size: 16px;
  font-weight: bold;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.quantity {
  font-size: 16px;
  font-weight: bold;
  min-width: 20px;
  text-align: center;
}

.subtotal {
  font-size: 16px;
  font-weight: bold;
  color: #ff6b35;
  margin-left: 15px;
}

.cart-summary {
  background: white;
  margin: 10px;
  padding: 15px;
  border-radius: 10px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
}

.total-amount {
  color: #ff6b35;
  font-weight: bold;
  font-size: 18px;
}

.cart-bottom {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 15px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.total-info {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.total-count {
  font-size: 14px;
  color: #666;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  color: #ff6b35;
}

.checkout-btn {
  min-width: 120px;
}

.empty-cart {
  padding-top: 100px;
}
</style>