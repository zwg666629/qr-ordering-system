<template>
  <div class="profile">
    <van-nav-bar
      title="个人中心"
      left-text="返回"
      left-arrow
      @click-left="$router.go(-1)"
    />

    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="avatar-section" @click="showAvatarUpload = true">
        <van-image
          :src="userStore.avatar || '/src/assets/default-avatar.png'"
          round
          width="80"
          height="80"
          fit="cover"
        />
        <van-icon name="camera-o" class="camera-icon" />
      </div>
      <div class="user-info">
        <h3>{{ userStore.nickname }}</h3>
        <p>{{ userStore.phone }}</p>
      </div>
    </div>

    <!-- 菜单列表 -->
    <van-cell-group>
      <van-cell title="我的订单" is-link @click="goToOrders">
        <template #icon>
          <van-icon name="records" />
        </template>
      </van-cell>
      <van-cell title="个人信息" is-link @click="showEditProfile = true">
        <template #icon>
          <van-icon name="contact" />
        </template>
      </van-cell>
      <van-cell title="修改密码" is-link @click="showChangePassword = true">
        <template #icon>
          <van-icon name="lock" />
        </template>
      </van-cell>
      <van-cell title="退出登录" @click="handleLogout">
        <template #icon>
          <van-icon name="sign" />
        </template>
      </van-cell>
    </van-cell-group>

    <!-- 头像上传弹窗 -->
    <van-action-sheet v-model:show="showAvatarUpload" title="更换头像">
      <div class="upload-actions">
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          style="display: none"
          @change="handleAvatarUpload"
        />
        <van-button block @click="$refs.fileInput.click()">
          选择图片
        </van-button>
      </div>
    </van-action-sheet>

    <!-- 编辑个人信息弹窗 -->
    <van-popup v-model:show="showEditProfile" position="bottom" :style="{ height: '60%' }">
      <div class="edit-profile">
        <van-nav-bar title="编辑个人信息" @click-left="showEditProfile = false">
          <template #left>
            <van-icon name="cross" />
          </template>
          <template #right>
            <van-button type="primary" size="small" @click="saveProfile">保存</van-button>
          </template>
        </van-nav-bar>

        <van-form>
          <van-field v-model="profileForm.nickname" label="昵称" placeholder="请输入昵称" />
          <van-field label="性别">
            <template #input>
              <van-radio-group v-model="profileForm.gender" direction="horizontal">
                <van-radio name="0">保密</van-radio>
                <van-radio name="1">男</van-radio>
                <van-radio name="2">女</van-radio>
              </van-radio-group>
            </template>
          </van-field>
          <van-field
            v-model="birthdayDisplay"
            label="生日"
            placeholder="请选择生日"
            readonly
            is-link
            @click="handleBirthdayClick"
          />
        </van-form>
      </div>
    </van-popup>

    <!-- 日期选择器 -->
    <van-popup v-model:show="showDatePicker" position="bottom" round>
      <div class="date-picker-container">
        <div class="date-picker-header">
          <van-button size="small" @click="showDatePicker = false">取消</van-button>
          <span class="date-picker-title">选择生日</span>
          <van-button size="small" type="primary" @click="confirmDate">确定</van-button>
        </div>
        <van-picker
          v-model="currentDate"
          :columns="dateColumns"
        />
      </div>
    </van-popup>

    <!-- 修改密码弹窗 -->
    <van-popup v-model:show="showChangePassword" position="bottom" :style="{ height: '50%' }">
      <div class="change-password">
        <van-nav-bar title="修改密码" @click-left="showChangePassword = false">
          <template #left>
            <van-icon name="cross" />
          </template>
        </van-nav-bar>

        <van-form @submit="handleChangePassword">
          <van-field
            v-model="passwordForm.oldPassword"
            type="password"
            label="原密码"
            placeholder="请输入原密码"
            :rules="[{ required: true, message: '请输入原密码' }]"
          />
          <van-field
            v-model="passwordForm.newPassword"
            type="password"
            label="新密码"
            placeholder="请输入新密码"
            :rules="[{ required: true, message: '请输入新密码' }, { min: 6, message: '密码至少6位' }]"
          />
          <van-field
            v-model="passwordForm.confirmPassword"
            type="password"
            label="确认密码"
            placeholder="请再次输入新密码"
            :rules="[{ required: true, message: '请确认密码' }]"
          />

          <div class="password-btn">
            <van-button
              round
              block
              type="primary"
              native-type="submit"
              :loading="passwordLoading"
            >
              确认修改
            </van-button>
          </div>
        </van-form>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi, fileApi } from '@/api'
import { showToast, showConfirmDialog } from 'vant'

const router = useRouter()
const userStore = useUserStore()

// 弹窗状态
const showAvatarUpload = ref(false)
const showEditProfile = ref(false)
const showChangePassword = ref(false)
const showDatePicker = ref(false)

// 个人信息表单
const profileForm = reactive({
  nickname: '',
  gender: '0', // 默认为字符串"0"(保密)，与radio name属性匹配
  birthday: ''
})

// 密码修改表单
const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const passwordLoading = ref(false)

// 日期选择器
const currentDate = ref([2000, 1, 1]) // 年、月、日的索引

// 生成年份列表（1950-当前年份）
const currentYear = new Date().getFullYear()
const years = Array.from({ length: currentYear - 1949 }, (_, i) => ({
  text: `${1950 + i}年`,
  value: 1950 + i
}))

// 生成月份列表
const months = Array.from({ length: 12 }, (_, i) => ({
  text: `${i + 1}月`,
  value: i + 1
}))

// 生成日期列表
const days = Array.from({ length: 31 }, (_, i) => ({
  text: `${i + 1}日`,
  value: i + 1
}))

// 日期选择器的列配置
const dateColumns = [years, months, days]

// 生日显示格式
const birthdayDisplay = computed(() => {
  if (!profileForm.birthday) return ''
  // 将YYYY-MM-DD格式转换为更友好的显示格式
  const date = new Date(profileForm.birthday)
  return `${date.getFullYear()}年${String(date.getMonth() + 1).padStart(2, '0')}月${String(date.getDate()).padStart(2, '0')}日`
})

// 初始化用户信息
onMounted(async () => {
  try {
    const userInfo = await userApi.getUserInfo()
    profileForm.nickname = userInfo.nickname || ''
    // 确保性别值为字符串类型，与radio的name属性匹配
    profileForm.gender = String(userInfo.gender || 0)
    profileForm.birthday = userInfo.birthday || ''
    
    console.log('用户信息加载:', {
      nickname: profileForm.nickname,
      gender: profileForm.gender,
      birthday: profileForm.birthday
    })

    // 如果有生日，更新currentDate
    if (userInfo.birthday) {
      const date = new Date(userInfo.birthday)
      currentDate.value = [
        date.getFullYear(),
        date.getMonth() + 1,
        date.getDate()
      ]
    }

    // 更新store中的用户信息
    userStore.updateUserInfo(userInfo)
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
})

// 头像上传
const handleAvatarUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  try {
    showToast({ type: 'loading', message: '上传中...' })
    const avatarUrl = await fileApi.uploadAvatar(file)

    // 更新用户头像
    await userApi.updateUserInfo({ avatar: avatarUrl })
    userStore.updateUserInfo({ avatar: avatarUrl })

    showToast({ type: 'success', message: '头像更新成功' })
    showAvatarUpload.value = false
  } catch (error) {
    showToast({ type: 'fail', message: error.message || '上传失败' })
  }
}

// 保存个人信息
const saveProfile = async () => {
  try {
    // 准备保存的数据，将性别转换为数字类型
    const saveData = {
      ...profileForm,
      gender: parseInt(profileForm.gender) // 将字符串转换为数字
    }
    
    console.log('保存的数据:', saveData)
    
    await userApi.updateUserInfo(saveData)
    userStore.updateUserInfo(saveData)
    showToast({ type: 'success', message: '保存成功' })
    showEditProfile.value = false
  } catch (error) {
    console.error('保存个人信息失败:', error)
    showToast({ type: 'fail', message: error.message || '保存失败' })
  }
}

// 处理生日字段点击
const handleBirthdayClick = () => {
  console.log('生日字段被点击了')
  showDatePicker.value = true
  console.log('日期选择器状态:', showDatePicker.value)
}

// 确认日期选择
const confirmDate = () => {
  try {
    const [year, month, day] = currentDate.value
    
    // 格式化日期为YYYY-MM-DD
    const monthStr = String(month).padStart(2, '0')
    const dayStr = String(day).padStart(2, '0')
    
    profileForm.birthday = `${year}-${monthStr}-${dayStr}`
    showDatePicker.value = false
    
    console.log('选择的生日:', profileForm.birthday)
    showToast({ type: 'success', message: '生日已选择' })
  } catch (error) {
    console.error('日期格式化失败:', error)
    showToast({ type: 'fail', message: '日期选择失败' })
  }
}

// 修改密码
const handleChangePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    showToast({ type: 'fail', message: '两次输入的密码不一致' })
    return
  }

  passwordLoading.value = true

  try {
    await userApi.changePassword(passwordForm.oldPassword, passwordForm.newPassword)
    showToast({ type: 'success', message: '密码修改成功' })
    showChangePassword.value = false

    // 清空表单
    Object.keys(passwordForm).forEach(key => {
      passwordForm[key] = ''
    })
  } catch (error) {
    showToast({ type: 'fail', message: error.message || '修改失败' })
  } finally {
    passwordLoading.value = false
  }
}

// 跳转到订单页面
const goToOrders = () => {
  router.push('/order')
}

// 退出登录
const handleLogout = async () => {
  try {
    await showConfirmDialog({
      title: '提示',
      message: '确定要退出登录吗？'
    })

    userStore.logout()
    showToast({ type: 'success', message: '已退出登录' })
    router.push('/')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.profile {
  min-height: 100vh;
  background: #f5f5f5;
}

.user-card {
  background: white;
  padding: 30px 20px;
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.avatar-section {
  position: relative;
  margin-right: 20px;
  cursor: pointer;
}

.camera-icon {
  position: absolute;
  bottom: 0;
  right: 0;
  background: #ff6b35;
  color: white;
  border-radius: 50%;
  padding: 4px;
  font-size: 12px;
}

.user-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
  color: #333;
}

.user-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.upload-actions {
  padding: 20px;
}

.edit-profile, .change-password {
  height: 100%;
  background: white;
}

.password-btn {
  padding: 20px;
}

/* 修复图标对齐问题 */
:deep(.van-cell) {
  display: flex;
  align-items: center;
  min-height: 50px;
}

:deep(.van-cell__left-icon) {
  margin-right: 12px;
  color: #ff6b35;
  font-size: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  vertical-align: middle;
}

:deep(.van-cell__title) {
  display: flex;
  align-items: center;
  line-height: 1.5;
}

:deep(.van-cell__value) {
  display: flex;
  align-items: center;
}

:deep(.van-button--primary) {
  background: #ff6b35;
  border-color: #ff6b35;
}

:deep(.van-radio__icon--checked .van-icon) {
  background-color: #ff6b35;
  border-color: #ff6b35;
}

/* 强制图标垂直居中 */
:deep(.van-cell-group .van-cell) {
  padding: 12px 16px;
  display: flex;
  align-items: center;
}

:deep(.van-cell .van-icon) {
  vertical-align: middle;
  line-height: 1;
}

/* 修复具体的图标位置 */
:deep(.van-icon-records),
:deep(.van-icon-contact), 
:deep(.van-icon-lock),
:deep(.van-icon-sign) {
  transform: translateY(0);
  vertical-align: middle;
}

/* 日期选择器样式 */
.date-picker-container {
  background: #fff;
  border-radius: 16px 16px 0 0;
  overflow: hidden;
}

.date-picker-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 16px;
  background: #fff;
  border-bottom: 1px solid #ebedf0;
}

.date-picker-title {
  font-weight: 500;
  font-size: 16px;
  color: #323233;
}

:deep(.van-popup--bottom) {
  border-radius: 16px 16px 0 0;
}

/* 确保生日字段可点击 */
:deep(.van-field__control--disabled) {
  color: #323233;
  -webkit-text-fill-color: #323233;
}

:deep(.van-field__control[readonly]) {
  cursor: pointer;
}
</style>