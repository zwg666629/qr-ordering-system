<template>
  <div class="dashboard">
    <h2 class="page-title">仪表盘</h2>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">今日订单</div>
              <div class="stat-number">{{ statistics.todayOrders }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #67c23a">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">今日营收</div>
              <div class="stat-number">¥{{ statistics.todayRevenue }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #e6a23c">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">今日顾客</div>
              <div class="stat-number">{{ statistics.todayCustomers }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon><Dish /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">菜品总数</div>
              <div class="stat-number">{{ statistics.totalDishes }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-area">
      <el-col :span="16">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>销售趋势</span>
              <el-radio-group v-model="trendType" size="small">
                <el-radio-button label="week">本周</el-radio-button>
                <el-radio-button label="month">本月</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div id="sales-chart" style="height: 350px"></div>
        </el-card>
      </el-col>
      
      <el-col :span="8">
        <el-card>
          <template #header>
            <span>热销菜品TOP5</span>
          </template>
          <div class="hot-dishes">
            <div 
              class="hot-dish-item" 
              v-for="(dish, index) in hotDishes" 
              :key="dish.id"
            >
              <span class="dish-rank">{{ index + 1 }}</span>
              <span class="dish-name">{{ dish.name }}</span>
              <span class="dish-sales">{{ dish.sales }}份</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 最新订单 -->
    <el-card class="recent-orders">
      <template #header>
        <div class="card-header">
          <span>最新订单</span>
          <el-button type="primary" text @click="$router.push('/order')">
            查看全部
          </el-button>
        </div>
      </template>
      <el-table :data="recentOrders" style="width: 100%">
        <el-table-column prop="orderNumber" label="订单号" width="180" />
        <el-table-column prop="tableNumber" label="餐桌号" width="100" />
        <el-table-column prop="totalAmount" label="金额">
          <template #default="{ row }">
            ¥{{ row.totalAmount }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button 
              type="primary" 
              text 
              @click="$router.push(`/order/${row.id}`)"
            >
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ShoppingCart, Money, User, Dish } from '@element-plus/icons-vue'

// 统计数据
const statistics = reactive({
  todayOrders: 0,
  todayRevenue: 0,
  todayCustomers: 0,
  totalDishes: 0
})

// 趋势类型
const trendType = ref('week')

// 热销菜品
const hotDishes = ref([])

// 最新订单
const recentOrders = ref([])

// 加载统计数据
const loadStatistics = async () => {
  try {
    const response = await fetch('/api/statistics/dashboard')
    if (response.ok) {
      const result = await response.json()
      if (result.code === 200) {
        const data = result.data
        statistics.todayOrders = data.todayOrders || 0
        statistics.todayRevenue = data.todayRevenue || 0
        statistics.todayCustomers = data.todayCustomers || 0
        statistics.totalDishes = data.totalDishes || 0
        hotDishes.value = data.hotDishes || []
        recentOrders.value = data.recentOrders || []
      }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error)
    // 使用默认值
    statistics.todayOrders = 0
    statistics.todayRevenue = 0
    statistics.todayCustomers = 0
    statistics.totalDishes = 0
    hotDishes.value = []
    recentOrders.value = []
  }
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

// 初始化图表
const initCharts = () => {
  const chartDom = document.getElementById('sales-chart')
  const myChart = echarts.init(chartDom)
  
  const option = {
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['订单数', '营业额']
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    },
    yAxis: [
      {
        type: 'value',
        name: '订单数',
        position: 'left'
      },
      {
        type: 'value',
        name: '营业额',
        position: 'right'
      }
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        data: [120, 132, 101, 134, 90, 230, 210],
        smooth: true,
        itemStyle: {
          color: '#409eff'
        }
      },
      {
        name: '营业额',
        type: 'line',
        yAxisIndex: 1,
        data: [2200, 2820, 1910, 2340, 1900, 3300, 3100],
        smooth: true,
        itemStyle: {
          color: '#67c23a'
        }
      }
    ]
  }
  
  myChart.setOption(option)
  
  // 响应式
  window.addEventListener('resize', () => {
    myChart.resize()
  })
}

onMounted(() => {
  loadStatistics()
  initCharts()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #333;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  margin-right: 15px;
}

.stat-content {
  flex: 1;
}

.stat-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 5px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.chart-area {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hot-dishes {
  padding: 10px 0;
}

.hot-dish-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.hot-dish-item:last-child {
  border-bottom: none;
}

.dish-rank {
  width: 30px;
  height: 30px;
  background: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-weight: bold;
}

.dish-name {
  flex: 1;
  color: #333;
}

.dish-sales {
  color: #999;
  font-size: 14px;
}

.recent-orders {
  margin-top: 20px;
}
</style>














