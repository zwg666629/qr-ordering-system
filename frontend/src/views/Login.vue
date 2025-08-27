<template>
  <div class="login">
    <!-- 导航栏 -->
    <van-nav-bar
      title="登录"
      left-text="返回"
      left-arrow
      @click-left="$router.go(-1)"
    />

    <!-- 登录表单 -->
    <div class="login-form">
      <div class="logo">
        <van-icon name="shop-o" size="60" color="#ff6b35" />
        <h2>扫码点餐</h2>
      </div>

      <!-- 登录方式切换 -->
      <van-tabs v-model:active="loginType" @change="onLoginTypeChange">
        <van-tab title="密码登录" name="password">
          <van-form @submit="handleLogin">
            <van-field
              v-model="form.phone"
              name="phone"
              label="手机号"
              placeholder="请输入手机号"
              :rules="[{ required: true, message: '请输入手机号' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }]"
            />
            <van-field
              v-model="form.password"
              type="password"
              name="password"
              label="密码"
              placeholder="请输入密码"
              :rules="[{ required: true, message: '请输入密码' }]"
            />
            <div class="login-btn">
              <van-button
                round
                block
                type="primary"
                native-type="submit"
                :loading="loading"
              >
                登录
              </van-button>
            </div>
          </van-form>
        </van-tab>

        <van-tab title="验证码登录" name="sms">
          <van-form @submit="handleLogin">
            <van-field
              v-model="form.phone"
              name="phone"
              label="手机号"
              placeholder="请输入手机号"
              :rules="[{ required: true, message: '请输入手机号' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确' }]"
            />
            <van-field
              v-model="form.code"
              name="code"
              label="验证码"
              placeholder="请输入验证码"
              :rules="[{ required: true, message: '请输入验证码' }]"
            >
              <template #button>
                <van-button
                  size="small"
                  type="primary"
                  :disabled="smsDisabled"
                  @click="sendSms"
                >
                  {{ smsText }}
                </van-button>
              </template>
            </van-field>
            <div class="login-btn">
              <van-button
                round
                block
                type="primary"
                native-type="submit"
                :loading="loading"
              >
                登录
              </van-button>
            </div>
          </van-form>
        </van-tab>
      </van-tabs>

      <!-- 注册链接 -->
      <div class="register-link">
        <span>还没有账号？</span>
        <van-button type="primary" plain size="small" @click="goToRegister">
          立即注册
        </van-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api'
import { showToast } from 'vant'

const router = useRouter()
const userStore = useUserStore()

// 登录类型
const loginType = ref('password')

// 表单数据
const form = reactive({
  phone: '',
  password: '',
  code: ''
})

// 加载状态
const loading = ref(false)

// 短信相关
const smsDisabled = ref(false)
const smsText = ref('发送验证码')
const smsCountdown = ref(0)

// 登录类型切换
const onLoginTypeChange = () => {
  // 清空表单
  form.password = ''
  form.code = ''
}

// 发送短信验证码
const sendSms = async () => {
  if (!form.phone) {
    showToast('请输入手机号')
    return
  }

  if (!/^1[3-9]\d{9}$/.test(form.phone)) {
    showToast('手机号格式不正确')
    return
  }

  try {
    await userApi.sendSms({
      phone: form.phone,
      type: 2 // 登录验证码
    })

    showToast('验证码发送成功')
    startCountdown()
  } catch (error) {
    showToast(error.message || '发送失败')
  }
}

// 开始倒计时
const startCountdown = () => {
  smsDisabled.value = true
  smsCountdown.value = 60
  smsText.value = `${smsCountdown.value}s后重发`

  const timer = setInterval(() => {
    smsCountdown.value--
    smsText.value = `${smsCountdown.value}s后重发`

    if (smsCountdown.value <= 0) {
      clearInterval(timer)
      smsDisabled.value = false
      smsText.value = '发送验证码'
    }
  }, 1000)
}

// 处理登录
const handleLogin = async () => {
  loading.value = true

  try {
    const loginData = {
      phone: form.phone,
      loginType: loginType.value === 'password' ? '1' : '2'
    }

    if (loginType.value === 'password') {
      loginData.password = form.password
    } else {
      loginData.code = form.code
    }

    const result = await userApi.login(loginData)

    // 保存登录状态
    userStore.login(result)

    showToast('登录成功')

    // 跳转到首页或返回上一页
    const redirect = router.currentRoute.value.query.redirect
    if (redirect) {
      router.push(redirect)
    } else {
      router.push('/')
    }
  } catch (error) {
    showToast(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

// 跳转到注册页
const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-form {
  padding: 40px 20px;
}

.logo {
  text-align: center;
  margin-bottom: 40px;
  color: white;
}

.logo h2 {
  margin-top: 10px;
  font-size: 24px;
  font-weight: bold;
}

.login-btn {
  margin-top: 30px;
}

.register-link {
  text-align: center;
  margin-top: 20px;
  color: white;
}

.register-link span {
  margin-right: 10px;
}

:deep(.van-tabs__wrap) {
  background: white;
  border-radius: 10px 10px 0 0;
}

:deep(.van-tabs__content) {
  background: white;
  border-radius: 0 0 10px 10px;
  padding: 20px;
}

:deep(.van-field__label) {
  color: #333;
  font-weight: 500;
}

:deep(.van-button--primary) {
  background: #ff6b35;
  border-color: #ff6b35;
}

:deep(.van-button--primary.van-button--plain) {
  color: #ff6b35;
  border-color: #ff6b35;
}
</style>