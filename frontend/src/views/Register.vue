<template>
  <div class="register">
    <van-nav-bar
      title="注册"
      left-text="返回"
      left-arrow
      @click-left="$router.go(-1)"
    />

    <div class="register-form">
      <div class="logo">
        <van-icon name="shop-o" size="60" color="#ff6b35" />
        <h2>注册账号</h2>
      </div>

      <van-form @submit="handleRegister">
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
        <van-field
          v-model="form.password"
          type="password"
          name="password"
          label="密码"
          placeholder="请输入密码（6-20位）"
          :rules="[{ required: true, message: '请输入密码' }, { min: 6, message: '密码至少6位' }]"
        />
        <van-field
          v-model="form.nickname"
          name="nickname"
          label="昵称"
          placeholder="请输入昵称（可选）"
        />

        <div class="register-btn">
          <van-button
            round
            block
            type="primary"
            native-type="submit"
            :loading="loading"
          >
            注册
          </van-button>
        </div>
      </van-form>

      <div class="login-link">
        <span>已有账号？</span>
        <van-button type="primary" plain size="small" @click="goToLogin">
          立即登录
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

const form = reactive({
  phone: '',
  code: '',
  password: '',
  nickname: ''
})

const loading = ref(false)
const smsDisabled = ref(false)
const smsText = ref('发送验证码')

const sendSms = async () => {
  if (!form.phone || !/^1[3-9]\d{9}$/.test(form.phone)) {
    showToast('请输入正确的手机号')
    return
  }

  try {
    await userApi.sendSms({
      phone: form.phone,
      type: 1 // 注册验证码
    })
    showToast('验证码发送成功')
    startCountdown()
  } catch (error) {
    showToast(error.message || '发送失败')
  }
}

const startCountdown = () => {
  smsDisabled.value = true
  let countdown = 60
  smsText.value = `${countdown}s后重发`

  const timer = setInterval(() => {
    countdown--
    smsText.value = `${countdown}s后重发`

    if (countdown <= 0) {
      clearInterval(timer)
      smsDisabled.value = false
      smsText.value = '发送验证码'
    }
  }, 1000)
}

const handleRegister = async () => {
  loading.value = true

  try {
    const result = await userApi.register(form)
    userStore.login(result)
    showToast('注册成功')
    router.push('/')
  } catch (error) {
    showToast(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<style scoped>
.register {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.register-form {
  padding: 40px 20px;
  background: white;
  margin: 20px;
  border-radius: 10px;
}

.logo {
  text-align: center;
  margin-bottom: 30px;
}

.logo h2 {
  margin-top: 10px;
  color: #333;
}

.register-btn {
  margin-top: 30px;
}

.login-link {
  text-align: center;
  margin-top: 20px;
}

:deep(.van-button--primary) {
  background: #ff6b35;
  border-color: #ff6b35;
}
</style>