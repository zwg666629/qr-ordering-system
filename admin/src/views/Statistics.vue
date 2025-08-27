<template>
  <div class="statistics">
    <h2 class="page-title">数据统计</h2>
    
    <!-- 时间选择器 -->
    <el-card class="time-selector">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD"
            value-format="YYYY-MM-DD"
            @change="loadStatistics"
          />
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="loadStatistics">
            <el-icon><Search /></el-icon>
            查询
          </el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <!-- 统计概览 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #409eff">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">总订单数</div>
              <div class="stat-number">{{ statistics.totalOrders }}</div>
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
              <div class="stat-title">总营收</div>
              <div class="stat-number">¥{{ statistics.totalRevenue }}</div>
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
              <div class="stat-title">总顾客数</div>
              <div class="stat-number">{{ statistics.totalCustomers }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card>
          <div class="stat-card">
            <div class="stat-icon" style="background: #f56c6c">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-title">平均客单价</div>
              <div class="stat-number">¥{{ statistics.avgOrderAmount }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 图表区域 -->
    <el-row :gutter="20" class="chart-area">
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>营收趋势</span>
          </template>
          <div id="revenue-chart" style="height: 400px"></div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <span>订单状态分布</span>
          </template>
          <div id="status-chart" style="height: 400px"></div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 详细数据表格 -->
    <el-card class="data-table">
      <template #header>
        <span>详细数据</span>
      </template>
      <el-table :data="detailData" style="width: 100%">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column prop="orders" label="订单数" width="100" />
        <el-table-column prop="revenue" label="营收" width="120">
          <template #default="{ row }">
            ¥{{ row.revenue }}
          </template>
        </el-table-column>
        <el-table-column prop="customers" label="顾客数" width="100" />
        <el-table-column prop="avgAmount" label="平均客单价" width="120">
          <template #default="{ row }">
            ¥{{ row.avgAmount }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ShoppingCart, Money, User, TrendCharts, Search } from '@element-plus/icons-vue'

// 日期范围
const dateRange = ref([])

// 统计数据
const statistics = reactive({
  totalOrders: 0,
  totalRevenue: 0,
  totalCustomers: 0,
  avgOrderAmount: 0
})

// 详细数据
const detailData = ref([])

// 加载统计数据
const loadStatistics = async () => {
  try {
    // 这里可以调用真实的统计API
    // 暂时使用模拟数据
    statistics.totalOrders = 245
    statistics.totalRevenue = 12890.50
    statistics.totalCustomers = 180
    statistics.avgOrderAmount = 71.62
    
    // 模拟详细数据
    detailData.value = [
      { date: '2024-08-20', orders: 45, revenue: 2340.50, customers: 32, avgAmount: 73.14 },
      { date: '2024-08-21', orders: 52, revenue: 2680.00, customers: 38, avgAmount: 70.53 },
      { date: '2024-08-22', orders: 38, revenue: 1890.30, customers: 28, avgAmount: 67.51 },
      { date: '2024-08-23', orders: 61, revenue: 3234.80, customers: 45, avgAmount: 71.88 },
      { date: '2024-08-24', orders: 49, revenue: 2744.90, customers: 37, avgAmount: 74.19 }
    ]
    
    // 更新图表
    initCharts()
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 初始化图表
const initCharts = () => {
  // 营收趋势图
  const revenueChart = echarts.init(document.getElementById('revenue-chart'))
  const revenueOption = {
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: detailData.value.map(item => item.date)
    },
    yAxis: {
      type: 'value',
      name: '营收（元）'
    },
    series: [{
      name: '营收',
      type: 'line',
      data: detailData.value.map(item => item.revenue),
      smooth: true,
      itemStyle: {
        color: '#409eff'
      }
    }]
  }
  revenueChart.setOption(revenueOption)
  
  // 订单状态分布图
  const statusChart = echarts.init(document.getElementById('status-chart'))
  const statusOption = {
    tooltip: {
      trigger: 'item'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [{
      name: '订单状态',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 120, name: '已完成' },
        { value: 80, name: '制作中' },
        { value: 30, name: '已支付' },
        { value: 15, name: '待支付' }
      ]
    }]
  }
  statusChart.setOption(statusOption)
  
  // 响应式
  window.addEventListener('resize', () => {
    revenueChart.resize()
    statusChart.resize()
  })
}

onMounted(() => {
  // 设置默认日期范围（最近7天）
  const today = new Date()
  const lastWeek = new Date(today.getTime() - 7 * 24 * 60 * 60 * 1000)
  dateRange.value = [
    lastWeek.toISOString().split('T')[0],
    today.toISOString().split('T')[0]
  ]
  
  loadStatistics()
})
</script>

<style scoped>
.statistics {
  padding: 20px;
}

.page-title {
  margin-bottom: 20px;
  color: #333;
}

.time-selector {
  margin-bottom: 20px;
}

.overview-cards {
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

.data-table {
  margin-top: 20px;
}
</style>
